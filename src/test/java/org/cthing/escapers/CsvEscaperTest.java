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


@SuppressWarnings("DataFlowIssue")
public class CsvEscaperTest {

    public static Stream<Arguments> escapeProvider() {
        return Stream.of(
                arguments("", ""),
                arguments("  ", "  "),
                arguments("foo bar", "foo bar"),
                arguments("foo.bar", "foo.bar"),
                arguments("foo,bar", "\"foo,bar\""),
                arguments("foo\nbar", "\"foo\nbar\""),
                arguments("foo\rbar", "\"foo\rbar\""),
                arguments("foo\"bar", "\"foo\"\"bar\""),
                arguments("foo\uD84C\uDFB4bar", "foo\uD84C\uDFB4bar")
        );
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscape(final String input, final String expected) throws IOException {
        assertThat(CsvEscaper.escape(input)).isEqualTo(expected);
        assertThat(CsvEscaper.escape(input.toCharArray())).isEqualTo(expected);

        StringWriter writer = new StringWriter();
        CsvEscaper.escape(input, writer);
        assertThat(writer).hasToString(expected);

        writer = new StringWriter();
        CsvEscaper.escape(input.toCharArray(), writer);
        assertThat(writer).hasToString(expected);
    }

    @Test
    public void testErrors() throws IOException {
        assertThat(CsvEscaper.escape((CharSequence)null)).isNull();
        assertThat(CsvEscaper.escape((char[])null)).isNull();

        final StringWriter writer = new StringWriter();
        CsvEscaper.escape((CharSequence)null, writer);
        assertThat(writer.toString()).isEmpty();
        CsvEscaper.escape((char[])null, writer);
        assertThat(writer.toString()).isEmpty();

        assertThatIllegalArgumentException().isThrownBy(() -> CsvEscaper.escape("hello", null));
        assertThatIllegalArgumentException().isThrownBy(() -> CsvEscaper.escape("hello".toCharArray(), null));
    }
}
