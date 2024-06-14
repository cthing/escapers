/*
 * Copyright 2024 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */

package org.cthing.escapers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIndexOutOfBoundsException;
import static org.cthing.escapers.JsonEscaper.Option;
import static org.junit.jupiter.params.provider.Arguments.arguments;


@SuppressWarnings("UnnecessaryUnicodeEscape")
public class JsonEscaperTest {

    public static Stream<Arguments> escapeProvider() {
        return Stream.of(
                arguments("", ""),
                arguments("   ", "   "),
                arguments("Hello World", "Hello World"),
                arguments("Hello World\n", "Hello World\\n"),
                arguments("Hello World\n", "Hello World\\n", Option.ESCAPE_NON_ASCII),
                arguments("Hello World\r\n", "Hello World\\r\\n"),
                arguments("Hello World\r\n", "Hello World\\r\\n", Option.ESCAPE_NON_ASCII),
                arguments("Hello\tWorld", "Hello\\tWorld"),
                arguments("Hello\tWorld", "Hello\\tWorld", Option.ESCAPE_NON_ASCII),
                arguments("Hello World\f", "Hello World\\f"),
                arguments("Hello World\f", "Hello World\\f", Option.ESCAPE_NON_ASCII),
                arguments("Hello World\b", "Hello World\\b"),
                arguments("Hello World\b", "Hello World\\b", Option.ESCAPE_NON_ASCII),
                arguments("Hello \"World\"", "Hello \\\"World\\\""),
                arguments("Hello \"World\"", "Hello \\\"World\\\"", Option.ESCAPE_NON_ASCII),
                arguments("https://www.cthing.com/foo", "https:\\/\\/www.cthing.com\\/foo"),
                arguments("https://www.cthing.com/foo", "https:\\/\\/www.cthing.com\\/foo", Option.ESCAPE_NON_ASCII),
                arguments("This \\ That", "This \\\\ That"),
                arguments("This \\ That", "This \\\\ That", Option.ESCAPE_NON_ASCII),
                arguments("Hello \u1E80orld", "Hello \u1E80orld"),
                arguments("Hello \u1E80orld", "Hello \\u1E80orld", Option.ESCAPE_NON_ASCII),
                arguments("Hello \uD834\uDD1E", "Hello \uD834\uDD1E"),
                arguments("Hello \uD834\uDD1E", "Hello \\uD834\\uDD1E", Option.ESCAPE_NON_ASCII),
                arguments("He didn't say, \"stop!\"", "He didn't say, \\\"stop!\\\""),
                arguments("He didn't say, \"stop!\"", "He didn't say, \\\"stop!\\\"", Option.ESCAPE_NON_ASCII),
                arguments("\"foo\" isn't \"bar\". specials: \b\r\n\f\t\\/",
                          "\\\"foo\\\" isn't \\\"bar\\\". specials: \\b\\r\\n\\f\\t\\\\\\/"),
                arguments("\"foo\" isn't \"bar\". specials: \b\r\n\f\t\\/",
                          "\\\"foo\\\" isn't \\\"bar\\\". specials: \\b\\r\\n\\f\\t\\\\\\/", Option.ESCAPE_NON_ASCII)
        );
    }

    private static class OptionsAggregator extends AbstractVarargsAggregator<Option> {
        OptionsAggregator() {
            super(Option.class, 2);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @AggregateWith(OptionsAggregator.class)
    private @interface Options {
    }


    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharSequence(final String input, final String expected, @Options final Set<Option> options) {
        assertThat(JsonEscaper.escape(input, options)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharSequenceVarArgs(final String input, final String expected,
                                              @Options final Set<Option> options) {
        assertThat(JsonEscaper.escape(input, options.toArray(new Option[0]))).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharSequenceWriter(final String input, final String expected,
                                             @Options final Set<Option> options) throws IOException {
        final StringWriter writer = new StringWriter();
        JsonEscaper.escape(input, writer, options);
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharSequenceWriterVarArgs(final String input, final String expected,
                                                    @Options final Set<Option> options) throws IOException {
        final StringWriter writer = new StringWriter();
        JsonEscaper.escape(input, writer, options.toArray(new Option[0]));
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArray(final String input, final String expected, @Options final Set<Option> options) {
        assertThat(JsonEscaper.escape(input.toCharArray(), options)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArrayVarArgs(final String input, final String expected,
                                           @Options final Set<Option> options) {
        assertThat(JsonEscaper.escape(input.toCharArray(), options.toArray(new Option[0]))).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArrayWriter(final String input, final String expected,
                                          @Options final Set<Option> options) throws IOException {
        final StringWriter writer = new StringWriter();
        JsonEscaper.escape(input.toCharArray(), writer, options);
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArrayWriterVarArgs(final String input, final String expected,
                                                 @Options final Set<Option> options) throws IOException {
        final StringWriter writer = new StringWriter();
        JsonEscaper.escape(input.toCharArray(), writer, options.toArray(new Option[0]));
        assertThat(writer).hasToString(expected);
    }

    @Test
    public void testEscapeCharArrayOffsetLength() throws IOException {
        assertThat(JsonEscaper.escape("Hello \u2030 World".toCharArray(), 6, 7)).isEqualTo("\u2030 World");
        assertThat(JsonEscaper.escape("Hello \u2030 World".toCharArray(), 6, 7, Option.ESCAPE_NON_ASCII))
                .isEqualTo("\\u2030 World");
        assertThat(JsonEscaper.escape("Hello \u2030 World".toCharArray(), 6, 7, Set.of(Option.ESCAPE_NON_ASCII)))
                .isEqualTo("\\u2030 World");

        StringWriter writer = new StringWriter();
        JsonEscaper.escape("Hello \u2030 World".toCharArray(), 6, 7, writer, Option.ESCAPE_NON_ASCII);
        assertThat(writer).hasToString("\\u2030 World");

        writer = new StringWriter();
        JsonEscaper.escape("Hello \u2030 World".toCharArray(), 6, 7, writer, Set.of(Option.ESCAPE_NON_ASCII));
        assertThat(writer).hasToString("\\u2030 World");
    }

    @Test
    public void testErrors() throws IOException {
        assertThat(JsonEscaper.escape((CharSequence)null)).isNull();
        assertThat(JsonEscaper.escape((char[])null)).isNull();

        final StringWriter writer = new StringWriter();
        JsonEscaper.escape((CharSequence)null, writer);
        assertThat(writer.toString()).isEmpty();
        JsonEscaper.escape((char[])null, writer);
        assertThat(writer.toString()).isEmpty();

        assertThatIllegalArgumentException().isThrownBy(() -> JsonEscaper.escape("hello", (Writer)null));
        assertThatIllegalArgumentException().isThrownBy(() -> JsonEscaper.escape("hello".toCharArray(), (Writer)null));

        assertThatIndexOutOfBoundsException().isThrownBy(() -> JsonEscaper.escape("hello".toCharArray(), -1, 3));
        assertThatIndexOutOfBoundsException().isThrownBy(() -> JsonEscaper.escape("hello".toCharArray(), 0, 20));
        assertThatIndexOutOfBoundsException().isThrownBy(() -> JsonEscaper.escape("hello".toCharArray(), 0, -1));
    }
}
