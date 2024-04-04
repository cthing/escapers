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
public class XmlEscaperTest {

    public static Stream<Arguments> charSequenceProvider() {
        return Stream.of(
                arguments("", ""),
                arguments("  ", "  "),
                arguments("a", "a"),
                arguments("Z", "Z"),
                arguments("~", "~"),
                arguments("'", "&apos;"),
                arguments("&", "&amp;"),
                arguments("<", "&lt;"),
                arguments(">", "&gt;"),
                arguments("\n", "\n"),
                arguments("\t", "\t"),
                arguments("\r", "\r"),
                arguments("a<b>c\"d'e&f", "a&lt;b&gt;c&quot;d&apos;e&amp;f"),
                arguments("a\tb\rc\nd", "a\tb\rc\nd"),
                arguments("a\u0000b", "ab"),
                arguments("\u0000", ""),
                arguments("\u001F", ""),
                arguments("\uFFFF", ""),
                arguments("\u007F", "&#x7F;"),
                arguments("\uD7FF", "&#xD7FF;"),
                arguments("\uE000", "&#xE000;"),
                arguments("\uFFFD", "&#xFFFD;"),
                arguments("\uD83D\uDE03", "&#x1F603;"),
                arguments("a\u0001\u0008\u000b\u000c\u000e\u001fb", "ab"),
                arguments("a\u007e\u007f\u0084\u0085\u0086\u009f\u00a0b", "a\u007e&#x7F;&#x84;&#x85;&#x86;&#x9F;&#xA0;b"),
                arguments("a\ud7ff\ud800 \udfff \ue000b", "a&#xD7FF;  &#xE000;b"),
                arguments("a\ufffd\ufffe\uffffb", "a&#xFFFD;b")
        );
    }

    @ParameterizedTest
    @MethodSource("charSequenceProvider")
    public void testEscapeCharSequence(final String actual, final String expected) throws IOException {
        assertThat(XmlEscaper.escape(actual)).isEqualTo(expected);

        final StringWriter writer = new StringWriter();
        XmlEscaper.escape(actual, writer);
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("charSequenceProvider")
    public void testEscapeCharArray(final String actual, final String expected) throws IOException {
        assertThat(XmlEscaper.escape(actual.toCharArray())).isEqualTo(expected);

        final StringWriter writer = new StringWriter();
        XmlEscaper.escape(actual.toCharArray(), writer);
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

        assertThatIllegalArgumentException().isThrownBy(() -> XmlEscaper.escape("hello", null));
        assertThatIllegalArgumentException().isThrownBy(() -> XmlEscaper.escape("hello".toCharArray(), null));
    }
}
