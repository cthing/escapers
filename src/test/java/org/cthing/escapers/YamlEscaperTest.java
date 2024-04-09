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
import java.util.stream.Stream;

import org.cthing.escapers.YamlEscaper.Quoting;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.params.provider.Arguments.arguments;


@SuppressWarnings({ "UnnecessaryUnicodeEscape", "DataFlowIssue" })
public class YamlEscaperTest {

    public static Stream<Arguments> quotesProviderNonAscii() {
        return Stream.of(
                arguments("a", Quoting.None),
                arguments("abc", Quoting.None),
                arguments("ab c", Quoting.None),
                arguments("", Quoting.Single),
                arguments(" ", Quoting.Single),
                arguments(" abc", Quoting.Single),
                arguments("abc ", Quoting.Single),
                arguments("---", Quoting.Single),
                arguments("...", Quoting.Single),
                arguments("ab#c", Quoting.Single),
                arguments("ab,c", Quoting.Single),
                arguments("ab[c", Quoting.Single),
                arguments("ab]c", Quoting.Single),
                arguments("ab{c", Quoting.Single),
                arguments("ab}c", Quoting.Single),
                arguments("ab&c", Quoting.Single),
                arguments("ab*c", Quoting.Single),
                arguments("ab!c", Quoting.Single),
                arguments("ab|c", Quoting.Single),
                arguments("ab>c", Quoting.Single),
                arguments("ab\"c", Quoting.Double),
                arguments("ab\\c", Quoting.Double),
                arguments("ab/c", Quoting.Single),
                arguments("ab%c", Quoting.Single),
                arguments("ab@c", Quoting.Single),
                arguments("ab?c", Quoting.Single),
                arguments("ab:c", Quoting.Single),
                arguments("ab\n", Quoting.Double),
                arguments("\u0123", Quoting.Double),
                arguments("\u1F603", Quoting.Double)
        );
    }

    public static Stream<Arguments> quotesProviderAscii() {
        return Stream.of(
                arguments("a", Quoting.None),
                arguments("abc", Quoting.None),
                arguments("ab c", Quoting.None),
                arguments("", Quoting.Single),
                arguments(" ", Quoting.Single),
                arguments(" abc", Quoting.Single),
                arguments("abc ", Quoting.Single),
                arguments("---", Quoting.Single),
                arguments("...", Quoting.Single),
                arguments("ab#c", Quoting.Single),
                arguments("ab,c", Quoting.Single),
                arguments("ab[c", Quoting.Single),
                arguments("ab]c", Quoting.Single),
                arguments("ab{c", Quoting.Single),
                arguments("ab}c", Quoting.Single),
                arguments("ab&c", Quoting.Single),
                arguments("ab*c", Quoting.Single),
                arguments("ab!c", Quoting.Single),
                arguments("ab|c", Quoting.Single),
                arguments("ab>c", Quoting.Single),
                arguments("ab\"c", Quoting.Double),
                arguments("ab\\c", Quoting.Double),
                arguments("ab/c", Quoting.Single),
                arguments("ab%c", Quoting.Single),
                arguments("ab@c", Quoting.Single),
                arguments("ab?c", Quoting.Single),
                arguments("ab:c", Quoting.Single),
                arguments("ab\n", Quoting.Double),
                arguments("\u0123", Quoting.None),
                arguments("\u1F603", Quoting.None)
        );
    }

    @ParameterizedTest
    @MethodSource("quotesProviderNonAscii")
    public void testRequiresQuotesNonAscii(final String str, final Quoting quoting) {
        assertThat(YamlEscaper.requiresQuotes(str, true)).isEqualTo(quoting);
    }

    @ParameterizedTest
    @MethodSource("quotesProviderAscii")
    public void testRequiresQuotesAscii(final String str, final Quoting quoting) {
        assertThat(YamlEscaper.requiresQuotes(str, false)).isEqualTo(quoting);
    }

    public static Stream<Arguments> escapeProviderNonAscii() {
        return Stream.of(
                arguments("a", "a"),
                arguments("abc", "abc"),
                arguments("ab c", "ab c"),
                arguments(" abc", "' abc'"),
                arguments("abc ", "'abc '"),
                arguments("---", "'---'"),
                arguments("...", "'...'"),
                arguments("", "''"),
                arguments("  ", "'  '"),
                arguments("a\u0000b", "\"a\\0b\""),
                arguments("a\u0007b", "\"a\\ab\""),
                arguments("a\u0008b", "\"a\\bb\""),
                arguments("a\tb", "\"a\\tb\""),
                arguments("a\nb", "\"a\\nb\""),
                arguments("a\u000Bb", "\"a\\vb\""),
                arguments("a\rb", "\"a\\rb\""),
                arguments("a\fb", "\"a\\fb\""),
                arguments("a\"b", "\"a\\\"b\""),
                arguments("a\\b", "\"a\\\\b\""),
                arguments("a/b", "'a/b'"),
                arguments("a/b\n", "\"a\\/b\\n\""),
                arguments("a\u005Cb", "\"a\\b\""),
                arguments("a\u0085b", "\"a\\Nb\""),
                arguments("a\u00A0b", "\"a\\_b\""),
                arguments("a\u2028b", "\"a\\Lb\""),
                arguments("a\u2029b", "\"a\\Pb\""),
                arguments("a\u0001", "\"a\\x01\""),
                arguments("a\u00FF", "\"a\\xFF\""),
                arguments("a\u2030", "\"a\\u2030\""),
                arguments("a\uD83D\uDE03", "\"a\\U0001F603\"")
        );
    }

    public static Stream<Arguments> escapeProviderAscii() {
        return Stream.of(
                arguments("a", "a"),
                arguments("abc", "abc"),
                arguments("ab c", "ab c"),
                arguments(" abc", "' abc'"),
                arguments("abc ", "'abc '"),
                arguments("---", "'---'"),
                arguments("...", "'...'"),
                arguments("", "''"),
                arguments("  ", "'  '"),
                arguments("a\u0000b", "\"a\\0b\""),
                arguments("a\u0007b", "\"a\\ab\""),
                arguments("a\u0008b", "\"a\\bb\""),
                arguments("a\tb", "\"a\\tb\""),
                arguments("a\nb", "\"a\\nb\""),
                arguments("a\u000Bb", "\"a\\vb\""),
                arguments("a\rb", "\"a\\rb\""),
                arguments("a\fb", "\"a\\fb\""),
                arguments("a\"b", "\"a\\\"b\""),
                arguments("a\\b", "\"a\\\\b\""),
                arguments("a/b", "'a/b'"),
                arguments("a/b\n", "\"a\\/b\\n\""),
                arguments("a\u005Cb", "\"a\\b\""),
                arguments("a\u0085b", "\"a\\Nb\""),
                arguments("a\u00A0b", "\"a\\_b\""),
                arguments("a\u2028b", "\"a\\Lb\""),
                arguments("a\u2029b", "\"a\\Pb\""),
                arguments("a\u0001", "\"a\\x01\""),
                arguments("a\u00FF", "a\u00FF"),
                arguments("a\u2030", "a\u2030"),
                arguments("a\uD83D\uDE03", "a\uD83D\uDE03")
        );
    }

    @ParameterizedTest
    @MethodSource("escapeProviderNonAscii")
    public void testEscapeCharSequenceNonAscii(final String actual, final String expected) throws IOException {
        assertThat(YamlEscaper.escape(actual, YamlEscaper.Option.ESCAPE_NON_ASCII)).isEqualTo(expected);

        final StringWriter writer = new StringWriter();
        YamlEscaper.escape(actual, writer, YamlEscaper.Option.ESCAPE_NON_ASCII);
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProviderAscii")
    public void testEscapeCharSequenceAscii(final String actual, final String expected) throws IOException {
        assertThat(YamlEscaper.escape(actual)).isEqualTo(expected);

        final StringWriter writer = new StringWriter();
        YamlEscaper.escape(actual, writer);
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProviderNonAscii")
    public void testEscapeCharArrayNonAscii(final String actual, final String expected) throws IOException {
        assertThat(YamlEscaper.escape(actual.toCharArray(), YamlEscaper.Option.ESCAPE_NON_ASCII)).isEqualTo(expected);

        final StringWriter writer = new StringWriter();
        YamlEscaper.escape(actual.toCharArray(), writer, YamlEscaper.Option.ESCAPE_NON_ASCII);
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProviderAscii")
    public void testEscapeCharArrayAscii(final String actual, final String expected) throws IOException {
        assertThat(YamlEscaper.escape(actual.toCharArray())).isEqualTo(expected);

        final StringWriter writer = new StringWriter();
        YamlEscaper.escape(actual.toCharArray(), writer);
        assertThat(writer).hasToString(expected);
    }

    @Test
    public void testErrors() throws IOException {
        assertThat(YamlEscaper.escape((CharSequence)null)).isNull();
        assertThat(YamlEscaper.escape((char[])null)).isNull();

        final StringWriter writer = new StringWriter();
        YamlEscaper.escape((CharSequence)null, writer);
        assertThat(writer.toString()).isEmpty();
        YamlEscaper.escape((char[])null, writer);
        assertThat(writer.toString()).isEmpty();

        assertThatIllegalArgumentException().isThrownBy(() -> YamlEscaper.escape("hello", (Writer)null));
        assertThatIllegalArgumentException().isThrownBy(() -> YamlEscaper.escape("hello".toCharArray(), (Writer)null));
    }
}
