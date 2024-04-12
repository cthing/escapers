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
public class JavaEscaperTest {

    public static Stream<Arguments> escapeProvider() {
        return Stream.of(
                arguments("", ""),
                arguments("   ", "   "),
                arguments(" ", "\\s", JavaEscaper.Option.ESCAPE_SPACE),
                arguments("Hello World", "Hello World"),
                arguments("Hello World", "Hello\\sWorld", JavaEscaper.Option.ESCAPE_SPACE),
                arguments("Hello don't World", "Hello don't World"),
                arguments("  Hello World  ", "\\s\\sHello\\sWorld\\s\\s", JavaEscaper.Option.ESCAPE_SPACE),
                arguments("Hello World\n", "Hello World\\n"),
                arguments("Hello World\n", "Hello World\\n", JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("Hello World\r\n", "Hello World\\r\\n"),
                arguments("Hello World\r\n", "Hello World\\r\\n", JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("Hello\tWorld", "Hello\\tWorld"),
                arguments("Hello\tWorld", "Hello\\tWorld", JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("Hello World\f", "Hello World\\f"),
                arguments("Hello World\f", "Hello World\\f", JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("Hello World\b", "Hello World\\b"),
                arguments("Hello World\b", "Hello World\\b", JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("Hello \"World\"", "Hello \\\"World\\\""),
                arguments("Hello \"World\"", "Hello \\\"World\\\"", JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("https://www.cthing.com/foo", "https://www.cthing.com/foo"),
                arguments("https://www.cthing.com/foo", "https://www.cthing.com/foo",
                          JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("This \\ That", "This \\\\ That"),
                arguments("This \\ That", "This \\\\ That", JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("Hello \u1E80orld", "Hello \u1E80orld"),
                arguments("Hello \u1E80orld", "Hello \\u1E80orld", JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("Hello \uD834\uDD1E", "Hello \uD834\uDD1E"),
                arguments("Hello \uD834\uDD1E", "Hello \\uD834\\uDD1E", JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("He didn't say, \"stop!\"", "He didn't say, \\\"stop!\\\""),
                arguments("He didn't say, \"stop!\"", "He didn't say, \\\"stop!\\\"",
                          JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\"foo\" isn't \"bar\". specials: \b\r\n\f\t\\/",
                          "\\\"foo\\\" isn't \\\"bar\\\". specials: \\b\\r\\n\\f\\t\\\\/"),
                arguments("\"foo\" isn't \"bar\". specials: \b\r\n\f\t\\/",
                          "\\\"foo\\\" isn't \\\"bar\\\". specials: \\b\\r\\n\\f\\t\\\\/",
                          JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\\\b\t\r", "\\\\\\b\\t\\r"),
                arguments("\\\b\t\r", "\\\\\\b\\t\\r", JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\u1234", "\u1234"),
                arguments("\u1234", "\\u1234", JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\u0234", "\u0234"),
                arguments("\u0234", "\\u0234", JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\u00ef", "\u00EF"),
                arguments("\u00ef", "\\u00EF", JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\u0001", "\\u0001"),
                arguments("\u0001", "\\u0001", JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\uabcd", "\uABCD"),
                arguments("\uabcd", "\\uABCD", JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("He didn't say, \"stop!\"", "He didn't say, \\\"stop!\\\""),
                arguments("He didn't say, \"stop!\"", "He didn't say, \\\"stop!\\\"",
                          JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("This space is non-breaking:\u00a0", "This space is non-breaking:" + "\u00A0"),
                arguments("This space is non-breaking:\u00a0", "This space is non-breaking:" + "\\u00A0",
                          JavaEscaper.Option.ESCAPE_NON_ASCII),
                arguments("\uABCD\u1234\u012C", "\uABCD\u1234\u012C"),
                arguments("\uABCD\u1234\u012C", "\\uABCD\\u1234\\u012C", JavaEscaper.Option.ESCAPE_NON_ASCII)
        );
    }

    private static class OptionsAggregator extends AbstractVarargsAggregator<JavaEscaper.Option> {
        OptionsAggregator() {
            super(JavaEscaper.Option.class, 2);
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
                                       @Options final JavaEscaper.Option[] options) {
        assertThat(JavaEscaper.escape(input, options)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharSequenceWriter(final String input, final String expected,
                                             @Options final JavaEscaper.Option[] options) throws IOException {
        final StringWriter writer = new StringWriter();
        JavaEscaper.escape(input, writer, options);
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArray(final String input, final String expected,
                                    @Options final JavaEscaper.Option[] options) {
        assertThat(JavaEscaper.escape(input.toCharArray(), options)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArrayWriter(final String input, final String expected,
                                          @Options final JavaEscaper.Option[] options) throws IOException {
        final StringWriter writer = new StringWriter();
        JavaEscaper.escape(input.toCharArray(), writer, options);
        assertThat(writer).hasToString(expected);
    }

    @Test
    public void testErrors() throws IOException {
        assertThat(JavaEscaper.escape((CharSequence)null)).isNull();
        assertThat(JavaEscaper.escape((char[])null)).isNull();

        final StringWriter writer = new StringWriter();
        JavaEscaper.escape((CharSequence)null, writer);
        assertThat(writer.toString()).isEmpty();
        JavaEscaper.escape((char[])null, writer);
        assertThat(writer.toString()).isEmpty();

        assertThatIllegalArgumentException().isThrownBy(() -> JavaEscaper.escape("hello", (Writer)null));
        assertThatIllegalArgumentException().isThrownBy(() -> JavaEscaper.escape("hello".toCharArray(), (Writer)null));
    }
}
