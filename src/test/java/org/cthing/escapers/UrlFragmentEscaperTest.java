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

class UrlFragmentEscaperTest {

    private static Stream<Arguments> escapeProvider() {
        return Stream.of(
                // Space (URL fragment encodes space as %20 rather than +)
                Arguments.of(" ", "%20"),
                // Fragment safe characters (includes / and ?)
                Arguments.of("abcXYZ0123-_.*:~/?@!$&'()+;,=", "abcXYZ0123-_.*:~/?@!$&'()+;,="),
                // Characters needing escaping (e.g., #)
                Arguments.of("a#b", "a%23b"),
                // 2-byte UTF-8
                Arguments.of("é", "%C3%A9"),
                // 3-byte UTF-8
                Arguments.of("€", "%E2%82%AC"),
                // 4-byte UTF-8 (Emoji '😀' surrogate pair)
                Arguments.of("\uD83D\uDE00", "%F0%9F%98%80"),
                // Mixed fragment string (/ and ? are unescaped, # is escaped)
                Arguments.of("/usr/bin/foo bar?#section", "/usr/bin/foo%20bar?%23section")
        );
    }

    @ParameterizedTest(name = "escape CharSequence [{0}] should result in [{1}]")
    @MethodSource("escapeProvider")
    void testEscapeCharSequence(final String input, final String expected) {
        assertThat(UrlFragmentEscaper.escape(input)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "escape CharSequence [{0}] to writer should result in [{1}]")
    @MethodSource("escapeProvider")
    void testEscapeCharSequenceWriter(final String input, final String expected) throws IOException {
        final StringWriter writer = new StringWriter();
        UrlFragmentEscaper.escape(input, writer);
        assertThat(writer.toString()).isEqualTo(expected);
    }

    @Test
    void testEscapeCharSequenceNull() {
        assertThat(UrlFragmentEscaper.escape((CharSequence)null)).isNull();
    }

    @Test
    void testEscapeCharSequenceNullWriter() throws IOException {
        final StringWriter writer = new StringWriter();
        UrlFragmentEscaper.escape((CharSequence)null, writer);
        assertThat(writer.toString()).isEmpty();
    }

    @ParameterizedTest(name = "escape char[] [{0}] should result in [{1}]")
    @MethodSource("escapeProvider")
    void testEscapeCharArray(final String input, final String expected) {
        assertThat(UrlFragmentEscaper.escape(input.toCharArray())).isEqualTo(expected);
    }

    @Test
    void testEscapeCharArrayNull() {
        assertThat(UrlFragmentEscaper.escape((char[])null)).isNull();
    }

    @Test
    void testEscapeCharArrayOffsetLength() {
        final char[] chars = "xx/foo/bar?#yy".toCharArray();
        // Extract "/foo/bar?#" from index 2, length 10 (# must be escaped as %23)
        assertThat(UrlFragmentEscaper.escape(chars, 2, 10)).isEqualTo("/foo/bar?%23");
    }

    @Test
    void testEscapeCharArrayOffsetLengthNull() {
        assertThat(UrlFragmentEscaper.escape(null, 0, 5)).isNull();
    }

    @Test
    void testEscapeCharArrayWriter() throws IOException {
        final char[] chars = "/foo/bar#baz".toCharArray();
        final StringWriter writer = new StringWriter();
        UrlFragmentEscaper.escape(chars, writer);
        assertThat(writer.toString()).isEqualTo("/foo/bar%23baz");
    }

    @Test
    void testEscapeCharArrayNullWriter() throws IOException {
        final StringWriter writer = new StringWriter();
        UrlFragmentEscaper.escape((char[])null, writer);
        assertThat(writer.toString()).isEmpty();
    }

    @Test
    void testEscapeCharArrayOffsetLengthWriter() throws IOException {
        final char[] chars = "xx/foo/bar#yy".toCharArray();
        final StringWriter writer = new StringWriter();
        // Extract "/foo/bar#" from index 2, length 9
        UrlFragmentEscaper.escape(chars, 2, 9, writer);
        assertThat(writer.toString()).isEqualTo("/foo/bar%23");
    }

    @Test
    void testEscapeCharArrayOffsetLengthNullWriter() throws IOException {
        final StringWriter writer = new StringWriter();
        UrlFragmentEscaper.escape(null, 0, 5, writer);
        assertThat(writer.toString()).isEmpty();
    }

    @Test
    void testNegativeLengthThrowsException() {
        assertThatThrownBy(() -> UrlFragmentEscaper.escape("test".toCharArray(), 0, -1))
                .isInstanceOf(IndexOutOfBoundsException.class)
                .hasMessage("length must be greater than or equal to 0");
    }

    @Test
    void testAllValidCodePointsExhaustively() {
        for (int cp = 0; cp <= 0x10FFFF; cp++) {
            final String s = new String(Character.toChars(cp));
            final String escaped = UrlFragmentEscaper.escape(s);
            assertThat(escaped).isNotNull();
        }
    }
}
