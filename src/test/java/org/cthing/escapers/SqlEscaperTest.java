/*
 * Copyright 2026 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */

package org.cthing.escapers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.params.provider.Arguments.arguments;


public class SqlEscaperTest {

    static Stream<Arguments> escapeProvider() {
        return Stream.of(
                arguments("", ""),
                arguments("normal text", "normal text"),
                arguments("\0", "\\0"),
                arguments("\n", "\\n"),
                arguments("\r", "\\r"),
                arguments("\\", "\\\\"),
                arguments("'", "\\'"),
                arguments("\"", "\\\""),
                arguments("\032", "\\Z"),
                arguments("multi\nline\rtest", "multi\\nline\\rtest"),
                arguments("drop table 'users';", "drop table \\'users\\';"),
                arguments("path\\to\\file", "path\\\\to\\\\file"),
                arguments("emoji 😎 test", "emoji 😎 test"),
                arguments("emoji\n😎\r", "emoji\\n😎\\r"),
                // Surrogate pair split across boundaries (High surrogate only, should just pass through safely)
                arguments(String.valueOf('\uD83D'), String.valueOf('\uD83D'))
        );
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    void testEscapeCharSequence(final String input, final String expected) {
        assertThat(SqlEscaper.escape(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    void testEscapeCharSequenceWithWriter(final String input, final String expected) throws IOException {
        final StringWriter writer = new StringWriter();
        SqlEscaper.escape(input, writer);
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    void testEscapeCharArray(final String input, final String expected) {
        final char[] inputChars = input.toCharArray();
        assertThat(SqlEscaper.escape(inputChars)).isEqualTo(expected);
        assertThat(SqlEscaper.escape(inputChars, 0, inputChars.length)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    void testEscapeCharArrayWithWriter(final String input, final String expected) throws IOException {
        final char[] inputChars = input.toCharArray();

        final StringWriter writer1 = new StringWriter();
        SqlEscaper.escape(inputChars, writer1);
        assertThat(writer1).hasToString(expected);

        final StringWriter writer2 = new StringWriter();
        SqlEscaper.escape(inputChars, 0, inputChars.length, writer2);
        assertThat(writer2).hasToString(expected);
    }

    @Test
    void testEscapeNulls() throws IOException {
        assertThat(SqlEscaper.escape((CharSequence)null)).isNull();
        assertThat(SqlEscaper.escape((char[])null)).isNull();
        assertThat(SqlEscaper.escape(null, 0, 5)).isNull();

        // When Writer overloads receive null input arrays/sequences, they should silently do nothing.
        final StringWriter writer = new StringWriter();
        SqlEscaper.escape((CharSequence)null, writer);
        SqlEscaper.escape((char[])null, writer);
        SqlEscaper.escape(null, 0, 5, writer);
        assertThat(writer).hasToString("");
    }

    @Test
    void testEscapeCharArraySubrange() {
        final char[] input = "bad'; DROP --".toCharArray();

        // Escape just the "'; " portion
        assertThat(SqlEscaper.escape(input, 3, 3)).isEqualTo("\\'; ");
    }

    @Test
    void testEscapeCharArraySubrangeWithWriter() throws IOException {
        final char[] input = "bad'; DROP --".toCharArray();
        final StringWriter writer = new StringWriter();

        // Escape just the "'; " portion
        SqlEscaper.escape(input, 3, 3, writer);
        assertThat(writer).hasToString("\\'; ");
    }

    @Test
    void testBoundsValidation() {
        final char[] input = "test".toCharArray();

        // Negative length should trigger the explicit exception in the private implementation
        assertThatExceptionOfType(IndexOutOfBoundsException.class)
                .isThrownBy(() -> SqlEscaper.escape(input, 0, -1))
                .withMessageContaining("length must be greater than or equal to 0");

        // Writer overload bounds validation
        assertThatExceptionOfType(IndexOutOfBoundsException.class)
                .isThrownBy(() -> SqlEscaper.escape(input, 0, -1, new StringWriter()))
                .withMessageContaining("length must be greater than or equal to 0");
    }

    @Test
    void testWriterExceptionsBubbling() throws IOException {
        // Create a faulty writer that always throws an IOException
        try (Writer faultyWriter = new Writer() {
            @Override
            public void write(final char[] cbuf, final int off, final int len) {
            }

            @Override
            public void write(final String str) throws IOException {
                throw new IOException("Simulated write failure");
            }

            @Override
            public void write(final int c) throws IOException {
                throw new IOException("Simulated write failure");
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        }) {
            // Ensure the IOException cleanly escapes the API surface
            assertThatExceptionOfType(IOException.class)
                    .isThrownBy(() -> SqlEscaper.escape("trigger\nerror", faultyWriter))
                    .withMessage("Simulated write failure");
        }
    }
}
