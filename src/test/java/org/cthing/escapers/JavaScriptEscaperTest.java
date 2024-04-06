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
public class JavaScriptEscaperTest {

    public static Stream<Arguments> charSequenceProvider() {
        return Stream.of(
                arguments("", ""),
                arguments("   ", "   "),
                arguments("Hello World", "Hello World"),
                arguments("Hello World\n", "Hello World\\n"),
                arguments("Hello World\r\n", "Hello World\\r\\n"),
                arguments("Hello\tWorld", "Hello\\tWorld"),
                arguments("Hello World\f", "Hello World\\f"),
                arguments("Hello World\b", "Hello World\\b"),
                arguments("Hello \"World\"", "Hello \\\"World\\\""),
                arguments("Hello 'World'", "Hello \\'World\\'"),
                arguments("https://www.cthing.com/foo", "https:\\/\\/www.cthing.com\\/foo"),
                arguments("This \\ That", "This \\\\ That"),
                arguments("Hello \u1E80orld", "Hello \\u1E80orld"),
                arguments("Hello \uD834\uDD1E", "Hello \\uD834\\uDD1E"),
                arguments("He didn't say, \"stop!\"", "He didn\\'t say, \\\"stop!\\\""),
                arguments("\"foo\" isn't \"bar\". specials: \b\r\n\f\t\\/",
                          "\\\"foo\\\" isn\\'t \\\"bar\\\". specials: \\b\\r\\n\\f\\t\\\\\\/")
        );
    }

    @ParameterizedTest
    @MethodSource("charSequenceProvider")
    public void testEscapeCharSequence(final String actual, final String expected) throws IOException {
        assertThat(JavaScriptEscaper.escape(actual)).isEqualTo(expected);

        final StringWriter writer = new StringWriter();
        JavaScriptEscaper.escape(actual, writer);
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("charSequenceProvider")
    public void testEscapeCharArray(final String actual, final String expected) throws IOException {
        assertThat(JavaScriptEscaper.escape(actual.toCharArray())).isEqualTo(expected);

        final StringWriter writer = new StringWriter();
        JavaScriptEscaper.escape(actual.toCharArray(), writer);
        assertThat(writer).hasToString(expected);
    }

    @Test
    public void testErrors() throws IOException {
        assertThat(JavaScriptEscaper.escape((CharSequence)null)).isNull();
        assertThat(JavaScriptEscaper.escape((char[])null)).isNull();

        final StringWriter writer = new StringWriter();
        JavaScriptEscaper.escape((CharSequence)null, writer);
        assertThat(writer.toString()).isEmpty();
        JavaScriptEscaper.escape((char[])null, writer);
        assertThat(writer.toString()).isEmpty();

        assertThatIllegalArgumentException().isThrownBy(() -> JavaScriptEscaper.escape("hello", null));
        assertThatIllegalArgumentException().isThrownBy(() -> JavaScriptEscaper.escape("hello".toCharArray(), null));
    }
}
