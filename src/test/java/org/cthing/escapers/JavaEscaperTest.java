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
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.params.provider.Arguments.arguments;


@SuppressWarnings({ "UnnecessaryUnicodeEscape", "DataFlowIssue" })
public class JavaEscaperTest {

    public static Stream<Arguments> charSequenceProvider() {
        return Stream.of(
                arguments("", ""),
                arguments("   ", "   "),
                arguments("Hello World", "Hello World"),
                arguments("Hello don't World", "Hello don't World"),
                arguments("Hello World\n", "Hello World\\n"),
                arguments("Hello World\r\n", "Hello World\\r\\n"),
                arguments("Hello\tWorld", "Hello\\tWorld"),
                arguments("Hello World\f", "Hello World\\f"),
                arguments("Hello World\b", "Hello World\\b"),
                arguments("Hello \"World\"", "Hello \\\"World\\\""),
                arguments("https://www.cthing.com/foo", "https://www.cthing.com/foo"),
                arguments("This \\ That", "This \\\\ That"),
                arguments("Hello \u1E80orld", "Hello \\u1E80orld"),
                arguments("Hello \uD834\uDD1E", "Hello \\uD834\\uDD1E"),
                arguments("He didn't say, \"stop!\"", "He didn't say, \\\"stop!\\\""),
                arguments("\"foo\" isn't \"bar\". specials: \b\r\n\f\t\\/",
                          "\\\"foo\\\" isn't \\\"bar\\\". specials: \\b\\r\\n\\f\\t\\\\/"),
                arguments("\\\b\t\r", "\\\\\\b\\t\\r"),
                arguments("\u1234", "\\u1234"),
                arguments("\u0234", "\\u0234"),
                arguments("\u00ef", "\\u00EF"),
                arguments("\u0001", "\\u0001"),
                arguments("\uabcd", "\\uABCD"),
                arguments("He didn't say, \"stop!\"", "He didn't say, \\\"stop!\\\""),
                arguments("This space is non-breaking:\u00a0", "This space is non-breaking:" + "\\u00A0"),
                arguments("\uABCD\u1234\u012C", "\\uABCD\\u1234\\u012C")
        );
    }

    @ParameterizedTest
    @MethodSource("charSequenceProvider")
    public void testEscapeCharSequence(final String actual, final String expected) throws IOException {
        assertThat(JavaEscaper.escape(actual, false)).isEqualTo(expected);

        final StringWriter writer = new StringWriter();
        JavaEscaper.escape(actual, false, writer);
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("charSequenceProvider")
    public void testEscapeCharArray(final String actual, final String expected) throws IOException {
        assertThat(JavaEscaper.escape(actual.toCharArray(), false)).isEqualTo(expected);

        final StringWriter writer = new StringWriter();
        JavaEscaper.escape(actual.toCharArray(), false, writer);
        assertThat(writer).hasToString(expected);
    }

    @Test
    public void testEscapeSpace() {
        assertThat(JavaEscaper.escape(" ", true)).isEqualTo("\\s");
        assertThat(JavaEscaper.escape("Hello World", true)).isEqualTo("Hello\\sWorld");
        assertThat(JavaEscaper.escape("  Hello World  ", true)).isEqualTo("\\s\\sHello\\sWorld\\s\\s");
    }

    @Test
    public void testErrors() throws IOException {
        assertThat(JavaEscaper.escape((CharSequence)null, false)).isNull();
        assertThat(JavaEscaper.escape((char[])null, false)).isNull();

        final StringWriter writer = new StringWriter();
        JavaEscaper.escape((CharSequence)null, false, writer);
        assertThat(writer.toString()).isEmpty();
        JavaEscaper.escape((char[])null, false, writer);
        assertThat(writer.toString()).isEmpty();

        assertThatIllegalArgumentException().isThrownBy(() -> JavaEscaper.escape("hello", false, null));
        assertThatIllegalArgumentException().isThrownBy(() -> JavaEscaper.escape("hello".toCharArray(), false, null));
    }
}