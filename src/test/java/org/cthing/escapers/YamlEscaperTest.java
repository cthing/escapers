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

import org.cthing.escapers.YamlEscaper.Quoting;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIndexOutOfBoundsException;
import static org.cthing.escapers.YamlEscaper.Option;
import static org.junit.jupiter.params.provider.Arguments.arguments;


@SuppressWarnings({ "UnnecessaryUnicodeEscape", "DataFlowIssue" })
public class YamlEscaperTest {

    public static Stream<Arguments> quotesProvider() {
        return Stream.of(
                arguments("a", Quoting.None, true),
                arguments("a", Quoting.None, false),
                arguments("abc", Quoting.None, true),
                arguments("abc", Quoting.None, false),
                arguments("ab c", Quoting.None, true),
                arguments("ab c", Quoting.None, false),
                arguments("", Quoting.Single, true),
                arguments("", Quoting.Single, false),
                arguments(" ", Quoting.Single, true),
                arguments(" ", Quoting.Single, false),
                arguments(" abc", Quoting.Single, true),
                arguments(" abc", Quoting.Single, false),
                arguments("abc ", Quoting.Single, true),
                arguments("abc ", Quoting.Single, false),
                arguments("---", Quoting.Single, true),
                arguments("---", Quoting.Single, false),
                arguments("...", Quoting.Single, true),
                arguments("...", Quoting.Single, false),
                arguments("ab#c", Quoting.Single, true),
                arguments("ab#c", Quoting.Single, false),
                arguments("ab,c", Quoting.Single, true),
                arguments("ab,c", Quoting.Single, false),
                arguments("ab[c", Quoting.Single, true),
                arguments("ab[c", Quoting.Single, false),
                arguments("ab]c", Quoting.Single, true),
                arguments("ab]c", Quoting.Single, false),
                arguments("ab{c", Quoting.Single, true),
                arguments("ab{c", Quoting.Single, false),
                arguments("ab}c", Quoting.Single, true),
                arguments("ab}c", Quoting.Single, false),
                arguments("ab&c", Quoting.Single, true),
                arguments("ab&c", Quoting.Single, false),
                arguments("ab*c", Quoting.Single, true),
                arguments("ab*c", Quoting.Single, false),
                arguments("ab!c", Quoting.Single, true),
                arguments("ab!c", Quoting.Single, false),
                arguments("ab|c", Quoting.Single, true),
                arguments("ab|c", Quoting.Single, false),
                arguments("ab>c", Quoting.Single, true),
                arguments("ab>c", Quoting.Single, false),
                arguments("ab\"c", Quoting.Double, true),
                arguments("ab\"c", Quoting.Double, false),
                arguments("ab\\c", Quoting.Double, true),
                arguments("ab\\c", Quoting.Double, false),
                arguments("ab/c", Quoting.Single, true),
                arguments("ab/c", Quoting.Single, false),
                arguments("ab%c", Quoting.Single, true),
                arguments("ab%c", Quoting.Single, false),
                arguments("ab@c", Quoting.Single, true),
                arguments("ab@c", Quoting.Single, false),
                arguments("ab?c", Quoting.Single, true),
                arguments("ab?c", Quoting.Single, false),
                arguments("ab:c", Quoting.Single, true),
                arguments("ab:c", Quoting.Single, false),
                arguments("ab\n", Quoting.Double, true),
                arguments("ab\n", Quoting.Double, false),
                arguments("\u0123", Quoting.Double, true),
                arguments("\u0123", Quoting.None, false),
                arguments("\u1F603", Quoting.Double, true),
                arguments("\u1F603", Quoting.None, false)
        );
    }

    @ParameterizedTest
    @MethodSource("quotesProvider")
    public void testRequiresQuotes(final String str, final Quoting quoting, final boolean escapeNonAscii) {
        assertThat(YamlEscaper.requiresQuotes(str, escapeNonAscii)).isEqualTo(quoting);
    }

    public static Stream<Arguments> escapeProvider() {
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
                arguments("a\u0000b", "\"a\\0b\"", Option.ESCAPE_NON_ASCII),
                arguments("a\u0007b", "\"a\\ab\""),
                arguments("a\u0007b", "\"a\\ab\"", Option.ESCAPE_NON_ASCII),
                arguments("a\u0008b", "\"a\\bb\""),
                arguments("a\u0008b", "\"a\\bb\"", Option.ESCAPE_NON_ASCII),
                arguments("a\tb", "\"a\\tb\""),
                arguments("a\tb", "\"a\\tb\"", Option.ESCAPE_NON_ASCII),
                arguments("a\nb", "\"a\\nb\""),
                arguments("a\nb", "\"a\\nb\"", Option.ESCAPE_NON_ASCII),
                arguments("a\u000Bb", "\"a\\vb\""),
                arguments("a\u000Bb", "\"a\\vb\"", Option.ESCAPE_NON_ASCII),
                arguments("a\rb", "\"a\\rb\""),
                arguments("a\rb", "\"a\\rb\"", Option.ESCAPE_NON_ASCII),
                arguments("a\fb", "\"a\\fb\""),
                arguments("a\fb", "\"a\\fb\"", Option.ESCAPE_NON_ASCII),
                arguments("a\"b", "\"a\\\"b\""),
                arguments("a\"b", "\"a\\\"b\"", Option.ESCAPE_NON_ASCII),
                arguments("a\\b", "\"a\\\\b\""),
                arguments("a\\b", "\"a\\\\b\"", Option.ESCAPE_NON_ASCII),
                arguments("a/b", "'a/b'"),
                arguments("a/b", "'a/b'", Option.ESCAPE_NON_ASCII),
                arguments("a/b\n", "\"a\\/b\\n\""),
                arguments("a/b\n", "\"a\\/b\\n\"", Option.ESCAPE_NON_ASCII),
                arguments("a\u005Cb", "\"a\\b\""),
                arguments("a\u005Cb", "\"a\\b\"", Option.ESCAPE_NON_ASCII),
                arguments("a\u0085b", "\"a\\Nb\""),
                arguments("a\u0085b", "\"a\\Nb\"", Option.ESCAPE_NON_ASCII),
                arguments("a\u00A0b", "\"a\\_b\""),
                arguments("a\u00A0b", "\"a\\_b\"", Option.ESCAPE_NON_ASCII),
                arguments("a\u2028b", "\"a\\Lb\""),
                arguments("a\u2028b", "\"a\\Lb\"", Option.ESCAPE_NON_ASCII),
                arguments("a\u2029b", "\"a\\Pb\""),
                arguments("a\u2029b", "\"a\\Pb\"", Option.ESCAPE_NON_ASCII),
                arguments("a\u0001", "\"a\\x01\""),
                arguments("a\u0001", "\"a\\x01\"", Option.ESCAPE_NON_ASCII),
                arguments("a\u00FF", "a\u00FF"),
                arguments("a\u00FF", "\"a\\xFF\"", Option.ESCAPE_NON_ASCII),
                arguments("a\uFF00", "a\uFF00"),
                arguments("a\u2030", "a\u2030"),
                arguments("a\u2030", "\"a\\u2030\"", Option.ESCAPE_NON_ASCII),
                arguments("a\uD83D\uDE03", "a\uD83D\uDE03"),
                arguments("a\uD83D\uDE03", "\"a\\U0001F603\"", Option.ESCAPE_NON_ASCII),
                arguments("\n\u2030", "\"\\n\u2030\""),
                arguments("\n\uD83D\uDE03", "\"\\n\uD83D\uDE03\"")
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
        assertThat(YamlEscaper.escape(input, options)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharSequenceVarArgs(final String input, final String expected,
                                              @Options final Set<Option> options) {
        assertThat(YamlEscaper.escape(input, options.toArray(new Option[0]))).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharSequenceWriter(final String input, final String expected,
                                             @Options final Set<Option> options) throws IOException {
        final StringWriter writer = new StringWriter();
        YamlEscaper.escape(input, writer, options);
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharSequenceWriterVarArgs(final String input, final String expected,
                                                    @Options final Set<Option> options) throws IOException {
        final StringWriter writer = new StringWriter();
        YamlEscaper.escape(input, writer, options.toArray(new Option[0]));
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArray(final String input, final String expected, @Options final Set<Option> options) {
        assertThat(YamlEscaper.escape(input.toCharArray(), options)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArrayVarArgs(final String input, final String expected,
                                           @Options final Set<Option> options) {
        assertThat(YamlEscaper.escape(input.toCharArray(), options.toArray(new Option[0]))).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArrayWriter(final String input, final String expected,
                                          @Options final Set<Option> options) throws IOException {
        final StringWriter writer = new StringWriter();
        YamlEscaper.escape(input.toCharArray(), writer, options);
        assertThat(writer).hasToString(expected);
    }

    @ParameterizedTest
    @MethodSource("escapeProvider")
    public void testEscapeCharArrayWriterVarArgs(final String input, final String expected,
                                                 @Options final Set<Option> options) throws IOException {
        final StringWriter writer = new StringWriter();
        YamlEscaper.escape(input.toCharArray(), writer, options.toArray(new Option[0]));
        assertThat(writer).hasToString(expected);
    }

    @Test
    public void testEscapeCharArrayOffsetLength() throws IOException {
        assertThat(YamlEscaper.escape("Hello \n World".toCharArray(), 6, 7)).isEqualTo("\"\\n World\"");
        assertThat(YamlEscaper.escape("Hello \n World".toCharArray(), 6, 7, Option.ESCAPE_NON_ASCII))
                .isEqualTo("\"\\n World\"");
        assertThat(YamlEscaper.escape("Hello \n World".toCharArray(), 6, 7, Set.of(Option.ESCAPE_NON_ASCII)))
                .isEqualTo("\"\\n World\"");

        StringWriter writer = new StringWriter();
        YamlEscaper.escape("Hello \n World".toCharArray(), 6, 7, writer, Option.ESCAPE_NON_ASCII);
        assertThat(writer).hasToString("\"\\n World\"");

        writer = new StringWriter();
        YamlEscaper.escape("Hello \n World".toCharArray(), 6, 7, writer, Set.of(Option.ESCAPE_NON_ASCII));
        assertThat(writer).hasToString("\"\\n World\"");
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

        assertThatIndexOutOfBoundsException().isThrownBy(() -> YamlEscaper.escape("hello".toCharArray(), -1, 3));
        assertThatIndexOutOfBoundsException().isThrownBy(() -> YamlEscaper.escape("hello".toCharArray(), 0, 20));
        assertThatIndexOutOfBoundsException().isThrownBy(() -> YamlEscaper.escape("hello".toCharArray(), 0, -1));
    }
}
