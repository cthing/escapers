/*
 * Copyright 2024 C Thing Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import static org.cthing.escapers.HtmlEscaper.Option;
import static org.junit.jupiter.params.provider.Arguments.arguments;


@SuppressWarnings({ "DataFlowIssue", "UnnecessaryUnicodeEscape" })
public class HtmlEscaperTest {

    public static Stream<Arguments> escapeProvider() {
        return Stream.of(
                arguments("", ""),
                arguments("   ", "   "),
                arguments("a", "a"),
                arguments("Z", "Z"),
                arguments("~", "~"),
                arguments("&", "&amp;"),
                arguments("<", "&lt;"),
                arguments(">", "&gt;"),
                arguments("\n", "\n"),
                arguments("\t", "\t"),
                arguments("\r", "\r"),
                arguments("a<b>c\"d'e&f", "a&lt;b&gt;c&quot;d&#x27;e&amp;f"),
                arguments("a<b>c\"d'e&f", "a&lt;b&gt;c&quot;d&#x27;e&amp;f", Option.USE_ISO_LATIN_1_ENTITIES),
                arguments("a\tb\rc\nd", "a\tb\rc\nd"),
                arguments("a\tb\rc\nd", "a\tb\rc\nd", Option.ESCAPE_NON_ASCII),
                arguments("a\u0000b", "ab"),
                arguments("a\u0000b", "ab", Option.ESCAPE_NON_ASCII),
                arguments("\u0000", ""),
                arguments("\u0000", "", Option.ESCAPE_NON_ASCII),
                arguments("\u001F", ""),
                arguments("\u001F", "", Option.ESCAPE_NON_ASCII),
                arguments("\u00A3", "\u00A3"),
                arguments("\u00A3", "&#xA3;", Option.ESCAPE_NON_ASCII),
                arguments("\u00A3", "&pound;", Option.USE_ISO_LATIN_1_ENTITIES),
                arguments("\u00A3", "&pound;", Option.ESCAPE_NON_ASCII, Option.USE_ISO_LATIN_1_ENTITIES),
                arguments("\u03A0", "&#x3A0;", Option.ESCAPE_NON_ASCII),
                arguments("\u03A0", "&Pi;", Option.USE_HTML4_EXTENDED_ENTITIES),
                arguments("\u03A0", "&Pi;", Option.ESCAPE_NON_ASCII, Option.USE_HTML4_EXTENDED_ENTITIES),
                arguments("\u03A0", "&Pi;", Option.ESCAPE_NON_ASCII, Option.USE_ISO_LATIN_1_ENTITIES,
                          Option.USE_HTML4_EXTENDED_ENTITIES),
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
                arguments("a\u0001\u0008\u000b\u000c\u000e\u001fb", "ab"),
                arguments("a\u0001\u0008\u000b\u000c\u000e\u001fb", "ab", Option.ESCAPE_NON_ASCII),
                arguments("a\u007e\u007f\u0084\u0085\u0086\u009f\u00a0b",
                          "a\u007e&#x7F;\u0084\u0085\u0086\u009f\u00a0b"),
                arguments("a\ud7ff\ud800 \udfff \ue000b", "a\ud7ff  \ue000b"),
                arguments("a\ufffd\ufffe\uffffb", "a\ufffdb"),
                arguments("a\ufffd\ufffe\uffffb", "a\ufffdb", Option.USE_DECIMAL),
                arguments("a\ufffd\ufffe\uffffb", "a&#xFFFD;b", Option.ESCAPE_NON_ASCII),
                arguments("a\ufffd\ufffe\uffffb", "a&#65533;b", Option.ESCAPE_NON_ASCII, Option.USE_DECIMAL)
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
        assertThat(HtmlEscaper.escape(input, options)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharSequenceVarArgs(final String input, final String expected,
                                              @Options final Set<Option> options) {
        assertThat(HtmlEscaper.escape(input, options.toArray(new Option[0]))).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharSequenceWriter(final String input, final String expected,
                                             @Options final Set<Option> options) throws IOException {
        final StringWriter writer = new StringWriter();
        HtmlEscaper.escape(input, writer, options);
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharSequenceWriterVarArgs(final String input, final String expected,
                                                    @Options final Set<Option> options) throws IOException {
        final StringWriter writer = new StringWriter();
        HtmlEscaper.escape(input, writer, options.toArray(new Option[0]));
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArray(final String input, final String expected, @Options final Set<Option> options) {
        assertThat(HtmlEscaper.escape(input.toCharArray(), options)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArrayVarArgs(final String input, final String expected,
                                           @Options final Set<Option> options) {
        assertThat(HtmlEscaper.escape(input.toCharArray(), options.toArray(new Option[0]))).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArrayWriter(final String input, final String expected,
                                          @Options final Set<Option> options) throws IOException {
        final StringWriter writer = new StringWriter();
        HtmlEscaper.escape(input.toCharArray(), writer, options);
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArrayWriterVarArgs(final String input, final String expected,
                                                 @Options final Set<Option> options) throws IOException {
        final StringWriter writer = new StringWriter();
        HtmlEscaper.escape(input.toCharArray(), writer, options.toArray(new Option[0]));
        assertThat(writer).hasToString(expected);
    }

    @Test
    public void testEscapeCharArrayOffsetLength() throws IOException {
        assertThat(HtmlEscaper.escape("Hello & World".toCharArray(), 6, 7)).isEqualTo("&amp; World");
        assertThat(HtmlEscaper.escape("Hello & World".toCharArray(), 6, 7, Option.ESCAPE_NON_ASCII))
                .isEqualTo("&amp; World");
        assertThat(HtmlEscaper.escape("Hello & World".toCharArray(), 6, 7, Set.of(Option.ESCAPE_NON_ASCII)))
                .isEqualTo("&amp; World");

        StringWriter writer = new StringWriter();
        HtmlEscaper.escape("Hello & World".toCharArray(), 6, 7, writer, Option.ESCAPE_NON_ASCII);
        assertThat(writer).hasToString("&amp; World");

        writer = new StringWriter();
        HtmlEscaper.escape("Hello & World".toCharArray(), 6, 7, writer, Set.of(Option.ESCAPE_NON_ASCII));
        assertThat(writer).hasToString("&amp; World");
    }

    @Test
    public void testErrors() throws IOException {
        assertThat(HtmlEscaper.escape((CharSequence)null)).isNull();
        assertThat(HtmlEscaper.escape((char[])null)).isNull();

        final StringWriter writer = new StringWriter();
        HtmlEscaper.escape((CharSequence)null, writer);
        assertThat(writer.toString()).isEmpty();
        HtmlEscaper.escape((char[])null, writer);
        assertThat(writer.toString()).isEmpty();

        assertThatIllegalArgumentException().isThrownBy(() -> HtmlEscaper.escape("hello", (Writer)null));
        assertThatIllegalArgumentException().isThrownBy(() -> HtmlEscaper.escape("hello".toCharArray(), (Writer)null));

        assertThatIndexOutOfBoundsException().isThrownBy(() -> HtmlEscaper.escape("hello".toCharArray(), -1, 3));
        assertThatIndexOutOfBoundsException().isThrownBy(() -> HtmlEscaper.escape("hello".toCharArray(), 0, 20));
        assertThatIndexOutOfBoundsException().isThrownBy(() -> HtmlEscaper.escape("hello".toCharArray(), 0, -1));
    }
}
