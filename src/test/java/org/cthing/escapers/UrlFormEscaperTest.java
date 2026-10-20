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

class UrlFormEscaperTest {

    private static Stream<Arguments> escapeProvider() {
        return Stream.of(
                // Space
                Arguments.of(" ", "+"),
                // Unreserved characters
                Arguments.of("abcXYZ0123-_.*", "abcXYZ0123-_.*"),
                // ASCII symbols and control characters needing escaping
                Arguments.of("!", "%21"),
                Arguments.of("@", "%40"),
                Arguments.of("#", "%23"),
                Arguments.of("$", "%24"),
                Arguments.of("%", "%25"),
                // 2-byte UTF-8
                Arguments.of("é", "%C3%A9"),
                // 3-byte UTF-8
                Arguments.of("€", "%E2%82%AC"),
                // 4-byte UTF-8 (Emoji '😀' surrogate pair)
                Arguments.of("\uD83D\uDE00", "%F0%9F%98%80"),
                // Mixed string
                Arguments.of("hello world!?", "hello+world%21%3F")
        );
    }

    @ParameterizedTest(name = "escape CharSequence [{0}] should result in [{1}]")
    @MethodSource("escapeProvider")
    void testEscapeCharSequence(final String input, final String expected) {
        assertThat(UrlFormEscaper.escape(input)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "escape CharSequence [{0}] to writer should result in [{1}]")
    @MethodSource("escapeProvider")
    void testEscapeCharSequenceWriter(final String input, final String expected) throws IOException {
        final StringWriter writer = new StringWriter();
        UrlFormEscaper.escape(input, writer);
        assertThat(writer.toString()).isEqualTo(expected);
    }

    @Test
    void testEscapeCharSequenceNull() {
        assertThat(UrlFormEscaper.escape((CharSequence)null)).isNull();
    }

    @Test
    void testEscapeCharSequenceNullWriter() throws IOException {
        final StringWriter writer = new StringWriter();
        UrlFormEscaper.escape((CharSequence)null, writer);
        assertThat(writer.toString()).isEmpty();
    }

    @ParameterizedTest(name = "escape char[] [{0}] should result in [{1}]")
    @MethodSource("escapeProvider")
    void testEscapeCharArray(final String input, final String expected) {
        assertThat(UrlFormEscaper.escape(input.toCharArray())).isEqualTo(expected);
    }

    @Test
    void testEscapeCharArrayNull() {
        assertThat(UrlFormEscaper.escape((char[])null)).isNull();
    }

    @Test
    void testEscapeCharArrayOffsetLength() {
        final char[] chars = "xxhello world!%yy".toCharArray();
        // Extract "hello world!" from index 2, length 12
        assertThat(UrlFormEscaper.escape(chars, 2, 12)).isEqualTo("hello+world%21");
    }

    @Test
    void testEscapeCharArrayOffsetLengthNull() {
        assertThat(UrlFormEscaper.escape(null, 0, 5)).isNull();
    }

    @Test
    void testEscapeCharArrayWriter() throws IOException {
        final char[] chars = "hello world!".toCharArray();
        final StringWriter writer = new StringWriter();
        UrlFormEscaper.escape(chars, writer);
        assertThat(writer.toString()).isEqualTo("hello+world%21");
    }

    @Test
    void testEscapeCharArrayNullWriter() throws IOException {
        final StringWriter writer = new StringWriter();
        UrlFormEscaper.escape((char[])null, writer);
        assertThat(writer.toString()).isEmpty();
    }

    @Test
    void testEscapeCharArrayOffsetLengthWriter() throws IOException {
        final char[] chars = "xxhello world!yy".toCharArray();
        final StringWriter writer = new StringWriter();
        UrlFormEscaper.escape(chars, 2, 12, writer);
        assertThat(writer.toString()).isEqualTo("hello+world%21");
    }

    @Test
    void testEscapeCharArrayOffsetLengthNullWriter() throws IOException {
        final StringWriter writer = new StringWriter();
        UrlFormEscaper.escape(null, 0, 5, writer);
        assertThat(writer.toString()).isEmpty();
    }

    @Test
    void testNegativeLengthThrowsException() {
        assertThatThrownBy(() -> UrlFormEscaper.escape("test".toCharArray(), 0, -1))
                .isInstanceOf(IndexOutOfBoundsException.class)
                .hasMessage("length must be greater than or equal to 0");
    }

    @Test
    void testAllValidCodePointsExhaustively() {
        for (int cp = 0; cp <= 0x10FFFF; cp++) {
            final String s = new String(Character.toChars(cp));
            final String escaped = UrlFormEscaper.escape(s);
            assertThat(escaped).isNotNull();
        }
    }
}
