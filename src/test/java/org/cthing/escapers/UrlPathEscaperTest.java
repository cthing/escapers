/*
 * Copyright 2026 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */

package org.cthing.escapers;

import java.io.IOException;
import java.io.StringWriter;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UrlPathEscaperTest {

    private static Stream<Arguments> escapeProvider() {
        return Stream.of(
                // Space (URL path encodes space as %20 rather than +)
                Arguments.of(" ", "%20"),
                // Path safe characters
                Arguments.of("abcXYZ0123-_.*:~@!$&'()+;,=", "abcXYZ0123-_.*:~@!$&'()+;,="),
                // Characters needing escaping (e.g., /, ?, #)
                Arguments.of("a/b?c#d", "a%2Fb%3Fc%23d"),
                // 2-byte UTF-8
                Arguments.of("é", "%C3%A9"),
                // 3-byte UTF-8
                Arguments.of("€", "%E2%82%AC"),
                // 4-byte UTF-8 (Emoji '😀' surrogate pair)
                Arguments.of("\uD83D\uDE00", "%F0%9F%98%80"),
                // Mixed path string
                Arguments.of("/usr/bin/foo bar?", "%2Fusr%2Fbin%2Ffoo%20bar%3F")
        );
    }

    @ParameterizedTest(name = "escape CharSequence [{0}] should result in [{1}]")
    @MethodSource("escapeProvider")
    void testEscapeCharSequence(final String input, final String expected) {
        assertThat(UrlPathEscaper.escape(input)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "escape CharSequence [{0}] to writer should result in [{1}]")
    @MethodSource("escapeProvider")
    void testEscapeCharSequenceWriter(final String input, final String expected) throws IOException {
        final StringWriter writer = new StringWriter();
        UrlPathEscaper.escape(input, writer);
        assertThat(writer.toString()).isEqualTo(expected);
    }

    @Test
    void testEscapeCharSequenceNull() {
        assertThat(UrlPathEscaper.escape((CharSequence)null)).isNull();
    }

    @Test
    void testEscapeCharSequenceNullWriter() throws IOException {
        final StringWriter writer = new StringWriter();
        UrlPathEscaper.escape((CharSequence)null, writer);
        assertThat(writer.toString()).isEmpty();
    }

    @ParameterizedTest(name = "escape char[] [{0}] should result in [{1}]")
    @MethodSource("escapeProvider")
    void testEscapeCharArray(final String input, final String expected) {
        assertThat(UrlPathEscaper.escape(input.toCharArray())).isEqualTo(expected);
    }

    @Test
    void testEscapeCharArrayNull() {
        assertThat(UrlPathEscaper.escape((char[])null)).isNull();
    }

    @Test
    void testEscapeCharArrayOffsetLength() {
        final char[] chars = "xx/foo/bar?yy".toCharArray();
        // Extract "/foo/bar?" from index 2, length 9
        assertThat(UrlPathEscaper.escape(chars, 2, 9)).isEqualTo("%2Ffoo%2Fbar%3F");
    }

    @Test
    void testEscapeCharArrayOffsetLengthNull() {
        assertThat(UrlPathEscaper.escape(null, 0, 5)).isNull();
    }

    @Test
    void testEscapeCharArrayWriter() throws IOException {
        final char[] chars = "/foo/bar".toCharArray();
        final StringWriter writer = new StringWriter();
        UrlPathEscaper.escape(chars, writer);
        assertThat(writer.toString()).isEqualTo("%2Ffoo%2Fbar");
    }

    @Test
    void testEscapeCharArrayNullWriter() throws IOException {
        final StringWriter writer = new StringWriter();
        UrlPathEscaper.escape((char[])null, writer);
        assertThat(writer.toString()).isEmpty();
    }

    @Test
    void testEscapeCharArrayOffsetLengthWriter() throws IOException {
        final char[] chars = "xx/foo/barpyy".toCharArray();
        final StringWriter writer = new StringWriter();
        UrlPathEscaper.escape(chars, 2, 8, writer);
        assertThat(writer.toString()).isEqualTo("%2Ffoo%2Fbar");
    }

    @Test
    void testEscapeCharArrayOffsetLengthNullWriter() throws IOException {
        final StringWriter writer = new StringWriter();
        UrlPathEscaper.escape(null, 0, 5, writer);
        assertThat(writer.toString()).isEmpty();
    }

    @Test
    void testNegativeLengthThrowsException() {
        assertThatThrownBy(() -> UrlPathEscaper.escape("test".toCharArray(), 0, -1))
                .isInstanceOf(IndexOutOfBoundsException.class)
                .hasMessage("length must be greater than or equal to 0");
    }

    @Test
    void testAllValidCodePointsExhaustively() {
        for (int cp = 0; cp <= 0x10FFFF; cp++) {
            final String s = new String(Character.toChars(cp));
            final String escaped = UrlPathEscaper.escape(s);
            assertThat(escaped).isNotNull();
        }
    }
}
