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
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.params.provider.Arguments.arguments;


@SuppressWarnings({ "UnnecessaryUnicodeEscape", "DataFlowIssue" })
public class XmlEscaperTest {

    public static Stream<Arguments> escapeProvider() {
        return Stream.of(
                arguments("", ""),
                arguments("  ", "  "),
                arguments("a", "a"),
                arguments("Z", "Z"),
                arguments("~", "~"),
                arguments("'", "&apos;"),
                arguments("'", "&apos;", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("&", "&amp;"),
                arguments("&", "&amp;", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("<", "&lt;"),
                arguments("<", "&lt;", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments(">", "&gt;"),
                arguments(">", "&gt;", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\n", "\n"),
                arguments("\n", "\n", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\t", "\t"),
                arguments("\t", "\t", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\r", "\r"),
                arguments("\r", "\r", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("a<b>c\"d'e&f", "a&lt;b&gt;c&quot;d&apos;e&amp;f"),
                arguments("a<b>c\"d'e&f", "a&lt;b&gt;c&quot;d&apos;e&amp;f", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("a\tb\rc\nd", "a\tb\rc\nd"),
                arguments("a\tb\rc\nd", "a\tb\rc\nd", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("a\u0000b", "ab"),
                arguments("a\u0000b", "ab", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\u0000", ""),
                arguments("\u0000", "", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\u001F", ""),
                arguments("\u001F", "", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\uFFFF", ""),
                arguments("\uFFFF", "", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\u007F", "&#x7F;"),
                arguments("\u007F", "&#x7F;", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\uD7FF", "\uD7FF"),
                arguments("\uD7FF", "&#xD7FF;", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\uE000", "\uE000"),
                arguments("\uE000", "&#xE000;", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\uFFFD", "\uFFFD"),
                arguments("\uFFFD", "&#xFFFD;", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\uD83D\uDE03", "\uD83D\uDE03"),
                arguments("\uD83D\uDE03", "&#x1F603;", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("a\u0001\u0008\u000b\u000c\u000e\u001fb", "ab"),
                arguments("a\u0001\u0008\u000b\u000c\u000e\u001fb", "ab", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("a\u007e\u007f\u0084\u0085\u0086\u009f\u00a0b", "a\u007e&#x7F;\u0084\u0085\u0086\u009f\u00a0b"),
                arguments("a\u007e\u007f\u0084\u0085\u0086\u009f\u00a0b",
                          "a\u007e&#x7F;&#x84;&#x85;&#x86;&#x9F;&#xA0;b", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("a\ud7ff\ud800 \udfff \ue000b", "a\ud7ff  \ue000b"),
                arguments("a\ud7ff\ud800 \udfff \ue000b", "a&#xD7FF;  &#xE000;b", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("a\ufffd\ufffe\uffffb", "a\ufffdb"),
                arguments("a\ufffd\ufffe\uffffb", "a&#xFFFD;b", XmlEscaper.Option.ESCAPE_NON_ASCII),
                arguments("Hello \u007F", "Hello &#127;", XmlEscaper.Option.USE_DECIMAL),
                arguments("Hello \u1234", "Hello \u1234", XmlEscaper.Option.USE_DECIMAL),
                arguments("Hello \u1234", "Hello &#4660;", XmlEscaper.Option.ESCAPE_NON_ASCII,
                          XmlEscaper.Option.USE_DECIMAL)
        );
    }

    private static class OptionsAggregator extends AbstractVarargsAggregator<XmlEscaper.Option> {
        OptionsAggregator() {
            super(XmlEscaper.Option.class, 2);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @AggregateWith(OptionsAggregator.class)
    private @interface Options {
    }


    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharSequence(final String input, final String expected,
                                       @Options final XmlEscaper.Option[] options) {
        assertThat(XmlEscaper.escape(input, options)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharSequenceWriter(final String input, final String expected,
                                             @Options final XmlEscaper.Option[] options) throws IOException {
        final StringWriter writer = new StringWriter();
        XmlEscaper.escape(input, writer, options);
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArray(final String input, final String expected,
                                    @Options final XmlEscaper.Option[] options) {
        assertThat(XmlEscaper.escape(input.toCharArray(), options)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArrayWriter(final String input, final String expected,
                                          @Options final XmlEscaper.Option[] options) throws IOException {
        final StringWriter writer = new StringWriter();
        XmlEscaper.escape(input.toCharArray(), writer, options);
        assertThat(writer).hasToString(expected);
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

        assertThatIllegalArgumentException().isThrownBy(() -> XmlEscaper.escape("hello", (Writer)null));
        assertThatIllegalArgumentException().isThrownBy(() -> XmlEscaper.escape("hello".toCharArray(), (Writer)null));
    }
}
