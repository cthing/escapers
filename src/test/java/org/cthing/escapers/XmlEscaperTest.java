/*
 * Copyright 2024 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */

package org.cthing.escapers;

import java.io.IOException;
import java.io.StringWriter;
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
import static org.assertj.core.api.Assertions.assertThatIndexOutOfBoundsException;
import static org.cthing.escapers.XmlEscaper.Option;
import static org.junit.jupiter.params.provider.Arguments.arguments;


@SuppressWarnings("UnnecessaryUnicodeEscape")
public class XmlEscaperTest {

    public static Stream<Arguments> escapeProvider() {
        return Stream.of(
                arguments("", ""),
                arguments("  ", "  "),
                arguments("a", "a"),
                arguments("Z", "Z"),
                arguments("~", "~"),
                arguments("'", "&apos;"),
                arguments("'", "&apos;", Option.ESCAPE_NON_ASCII),
                arguments("&", "&amp;"),
                arguments("&", "&amp;", Option.ESCAPE_NON_ASCII),
                arguments("<", "&lt;"),
                arguments("<", "&lt;", Option.ESCAPE_NON_ASCII),
                arguments(">", "&gt;"),
                arguments(">", "&gt;", Option.ESCAPE_NON_ASCII),
                arguments("\n", "\n"),
                arguments("\n", "\n", Option.ESCAPE_NON_ASCII),
                arguments("\t", "\t"),
                arguments("\t", "\t", Option.ESCAPE_NON_ASCII),
                arguments("\r", "\r"),
                arguments("\r", "\r", Option.ESCAPE_NON_ASCII),
                arguments("a<b>c\"d'e&f", "a&lt;b&gt;c&quot;d&apos;e&amp;f"),
                arguments("a<b>c\"d'e&f", "a&lt;b&gt;c&quot;d&apos;e&amp;f", Option.ESCAPE_NON_ASCII),
                arguments("a\tb\rc\nd", "a\tb\rc\nd"),
                arguments("a\tb\rc\nd", "a\tb\rc\nd", Option.ESCAPE_NON_ASCII),
                arguments("a\u0000b", "ab"),
                arguments("a\u0000b", "ab", Option.ESCAPE_NON_ASCII),
                arguments("\u0000", ""),
                arguments("\u0000", "", Option.ESCAPE_NON_ASCII),
                arguments("\u001F", ""),
                arguments("\u001F", "", Option.ESCAPE_NON_ASCII),
                arguments("\uFFFF", ""),
                arguments("\uFFFF", "", Option.ESCAPE_NON_ASCII),
                arguments("\u007F", "&#x7F;"),
                arguments("\u007F", "&#x7F;", Option.ESCAPE_NON_ASCII),
                arguments("\uD7FF", "\uD7FF"),
                arguments("\uD7FF", "&#xD7FF;", Option.ESCAPE_NON_ASCII),
                arguments("\uE000", "\uE000"),
                arguments("\uE000", "&#xE000;", Option.ESCAPE_NON_ASCII),
                arguments("\uFFFD", "\uFFFD"),
                arguments("\uFFFD", "&#xFFFD;", Option.ESCAPE_NON_ASCII),
                arguments("\uD83D\uDE03", "\uD83D\uDE03"),
                arguments("\uD83D\uDE03", "&#x1F603;", Option.ESCAPE_NON_ASCII),
                arguments("a\u0001\u0008\u000b\u000c\u000e\u001fb", "ab"),
                arguments("a\u0001\u0008\u000b\u000c\u000e\u001fb", "ab", Option.ESCAPE_NON_ASCII),
                arguments("a\u007e\u007f\u0084\u0085\u0086\u009f\u00a0b", "a\u007e&#x7F;\u0084\u0085\u0086\u009f\u00a0b"),
                arguments("a\u007e\u007f\u0084\u0085\u0086\u009f\u00a0b",
                          "a\u007e&#x7F;&#x84;&#x85;&#x86;&#x9F;&#xA0;b", Option.ESCAPE_NON_ASCII),
                arguments("a\ud7ff\ud800 \udfff \ue000b", "a\ud7ff  \ue000b"),
                arguments("a\ud7ff\ud800 \udfff \ue000b", "a&#xD7FF;  &#xE000;b", Option.ESCAPE_NON_ASCII),
                arguments("a\ufffd\ufffe\uffffb", "a\ufffdb"),
                arguments("a\ufffd\ufffe\uffffb", "a&#xFFFD;b", Option.ESCAPE_NON_ASCII),
                arguments("Hello \u007F", "Hello &#127;", Option.USE_DECIMAL),
                arguments("Hello \u1234", "Hello \u1234", Option.USE_DECIMAL),
                arguments("Hello \u1234", "Hello &#4660;", Option.ESCAPE_NON_ASCII, Option.USE_DECIMAL)
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
        assertThat(XmlEscaper.escape(input, options)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharSequenceVarArgs(final String input, final String expected,
                                              @Options final Set<Option> options) {
        assertThat(XmlEscaper.escape(input, options.toArray(new Option[0]))).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharSequenceWriter(final String input, final String expected,
                                             @Options final Set<Option> options) throws IOException {
        final StringWriter writer = new StringWriter();
        XmlEscaper.escape(input, writer, options);
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharSequenceWriterVarArgs(final String input, final String expected,
                                                    @Options final Set<Option> options) throws IOException {
        final StringWriter writer = new StringWriter();
        XmlEscaper.escape(input, writer, options.toArray(new Option[0]));
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArray(final String input, final String expected, @Options final Set<Option> options) {
        assertThat(XmlEscaper.escape(input.toCharArray(), options)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArrayVarArgs(final String input, final String expected,
                                           @Options final Set<Option> options) {
        assertThat(XmlEscaper.escape(input.toCharArray(), options.toArray(new Option[0]))).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArrayWriter(final String input, final String expected,
                                          @Options final Set<Option> options) throws IOException {
        final StringWriter writer = new StringWriter();
        XmlEscaper.escape(input.toCharArray(), writer, options);
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArrayWriterVarArgs(final String input, final String expected,
                                                 @Options final Set<Option> options) throws IOException {
        final StringWriter writer = new StringWriter();
        XmlEscaper.escape(input.toCharArray(), writer, options.toArray(new Option[0]));
        assertThat(writer).hasToString(expected);
    }

    @Test
    public void testEscapeCharArrayOffsetLength() throws IOException {
        assertThat(XmlEscaper.escape("Hello & World".toCharArray(), 6, 7)).isEqualTo("&amp; World");
        assertThat(XmlEscaper.escape("Hello & World".toCharArray(), 6, 7, Option.ESCAPE_NON_ASCII))
                .isEqualTo("&amp; World");
        assertThat(XmlEscaper.escape("Hello & World".toCharArray(), 6, 7, Set.of(Option.ESCAPE_NON_ASCII)))
                .isEqualTo("&amp; World");

        StringWriter writer = new StringWriter();
        XmlEscaper.escape("Hello & World".toCharArray(), 6, 7, writer, Option.ESCAPE_NON_ASCII);
        assertThat(writer).hasToString("&amp; World");

        writer = new StringWriter();
        XmlEscaper.escape("Hello & World".toCharArray(), 6, 7, writer, Set.of(Option.ESCAPE_NON_ASCII));
        assertThat(writer).hasToString("&amp; World");
    }

    @Test
    public void testErrors() throws IOException {
        assertThat(XmlEscaper.escape((CharSequence)null)).isNull();
        assertThat(XmlEscaper.escape((char[])null)).isNull();

        final StringWriter writer = new StringWriter();
        XmlEscaper.escape((CharSequence)null, writer);
        assertThat(writer.toString()).isEmpty();
        XmlEscaper.escape((char[])null, writer);
        assertThat(writer.toString()).isEmpty();

        assertThatIndexOutOfBoundsException().isThrownBy(() -> XmlEscaper.escape("hello".toCharArray(), -1, 3));
        assertThatIndexOutOfBoundsException().isThrownBy(() -> XmlEscaper.escape("hello".toCharArray(), 0, 20));
        assertThatIndexOutOfBoundsException().isThrownBy(() -> XmlEscaper.escape("hello".toCharArray(), 0, -1));
    }
}
