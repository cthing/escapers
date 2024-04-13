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
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.EnumSet;
import java.util.Set;

import javax.annotation.WillNotClose;

import org.cthing.annotations.NoCoverageGenerated;


/**
 * Escapes strings and character arrays according to the
 * <a href="https://262.ecma-international.org/">ECMA-262 ECMAScript&reg; 2023 Language Specification</a>. ECMAScript
 * is typically referred to as JavaScript. Characters are escaped according to the following table:
 *
 * <table style="border: 1px solid; border-collapse: collapse;">
 *     <caption>Character Escaping</caption>
 *     <thead>
 *         <tr>
 *             <th style="border: 1px solid; border-collapse: collapse; padding: 5px;">Character</th>
 *             <th style="border: 1px solid; border-collapse: collapse; padding: 5px;">Escape</th>
 *             <th style="border: 1px solid; border-collapse: collapse; padding: 5px;">Description</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x00 - 0x07</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">&#x5C;uXXXX</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">ASCII control characters</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x08</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\b</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Backspace</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x09</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\t</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Tab</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x0A</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\n</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Line feed</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x0B</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">&#x5C;uXXXX</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">ASCII control character</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x0C</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\f</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Form feed</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x0D</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\r</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Carriage return</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x0E - 0x1F</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">&#x5C;uXXXX</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">ASCII control characters</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x20 - 0x21</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Printable ASCII characters</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x22</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\&quot;</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Double quote</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x23 - 0x26</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Printable ASCII characters</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x27</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\'</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Single quote</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x28 - 0x2E</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Printable ASCII characters</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x2F</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\/</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Forward slash</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x30 - 0x5B</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Printable ASCII characters</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x5C</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\\</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Backslash</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x5D - 0x7E</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Printable ASCII characters</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x7F</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">&#x5C;uXXXX</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">ASCII control character</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x80 - 0x10FFFF</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged by default or &#x5C;uXXXX[&#x5C;uXXXX] if {@link Option#ESCAPE_NON_ASCII} specified</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">ISO Latin-1, Unicode BMP and surrogate pairs</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
public final class JavaScriptEscaper {

    /**
     * Escaping options.
     */
    public enum Option {
        /**
         * Escape characters above the ASCII range (i.e. ch &gt; 0x7F). By default, only ASCII control characters
         * and certain printable ASCII characters are escaped. Specifying this option causes all ISO Latin-1, Unicode
         * BMP and surrogate pair characters to be escaped (i.e. one or two &#x5C;uXXXX).
         */
        ESCAPE_NON_ASCII
    }

    @NoCoverageGenerated
    private JavaScriptEscaper() {
    }

    /**
     * Applies JavaScript escaping to the specified string.
     *
     * @param charSequence String to escape
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final CharSequence charSequence, final Option... options) {
        return (charSequence == null)
               ? null
               : escape(index -> Character.codePointAt(charSequence, index), charSequence.length(), toSet(options));
    }

    /**
     * Applies JavaScript escaping to the specified string.
     *
     * @param charSequence String to escape
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final CharSequence charSequence, final Set<Option> options) {
        return (charSequence == null)
               ? null : escape(index -> Character.codePointAt(charSequence, index), charSequence.length(), options);
    }

    /**
     * Applies JavaScript escaping to the specified string and writes the result to the specified writer.
     *
     * @param charSequence String to escape
     * @param writer Writer to which the escaped string is written
     * @param options Escaping options
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    @WillNotClose
    public static void escape(final CharSequence charSequence, final Writer writer, final Option... options)
            throws IOException {
        if (charSequence != null) {
            escape(index -> Character.codePointAt(charSequence, index), charSequence.length(), writer, toSet(options));
        }
    }

    /**
     * Applies JavaScript escaping to the specified string and writes the result to the specified writer.
     *
     * @param charSequence String to escape
     * @param writer Writer to which the escaped string is written
     * @param options Escaping options
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    @WillNotClose
    public static void escape(final CharSequence charSequence, final Writer writer, final Set<Option> options)
            throws IOException {
        if (charSequence != null) {
            escape(index -> Character.codePointAt(charSequence, index), charSequence.length(), writer, options);
        }
    }

    /**
     * Applies JavaScript escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final char[] charArr, final Option... options) {
        return (charArr == null)
               ? null : escape(index -> Character.codePointAt(charArr, index), charArr.length, toSet(options));
    }

    /**
     * Applies JavaScript escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final char[] charArr, final Set<Option> options) {
        return (charArr == null)
               ? null : escape(index -> Character.codePointAt(charArr, index), charArr.length, options);
    }

    /**
     * Applies JavaScript escaping to the specified character array and writes the result to the specified writer.
     *
     * @param charArr Character array to escape
     * @param writer Writer to which the escaped string is written
     * @param options Escaping options
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    @WillNotClose
    public static void escape(final char[] charArr, final Writer writer, final Option... options) throws IOException {
        if (charArr != null) {
            escape(index -> Character.codePointAt(charArr, index), charArr.length, writer, toSet(options));
        }
    }

    /**
     * Applies JavaScript escaping to the specified character array and writes the result to the specified writer.
     *
     * @param charArr Character array to escape
     * @param writer Writer to which the escaped string is written
     * @param options Escaping options
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    @WillNotClose
    public static void escape(final char[] charArr, final Writer writer, final Set<Option> options) throws IOException {
        if (charArr != null) {
            escape(index -> Character.codePointAt(charArr, index), charArr.length, writer, options);
        }
    }

    private static String escape(final CodePointProvider codePointProvider, final int length,
                                 final Set<Option> options) {
        try {
            final StringWriter writer = new StringWriter();
            escape(codePointProvider, length, writer, options);
            return writer.toString();
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static void escape(final CodePointProvider codePointProvider, final int length, final Writer writer,
                               final Set<Option> options) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("writer must not be null");
        }

        final boolean escapeNonAscii = options.contains(Option.ESCAPE_NON_ASCII);

        int index = 0;
        while (index < length) {
            final int cp = codePointProvider.codePointAt(index);
            final int charCount = Character.charCount(cp);
            switch (cp) {
                case '"' -> writer.write("\\\"");
                case '\'' -> writer.write("\\'");
                case '\\' -> writer.write("\\\\");
                case '/' -> writer.write("\\/");
                case '\n' -> writer.write("\\n");
                case '\r' -> writer.write("\\r");
                case '\f' -> writer.write("\\f");
                case '\t' -> writer.write("\\t");
                case '\b' -> writer.write("\\b");
                default -> {
                    if (charCount == 1) {
                        if (cp < 0x20 || cp == 0x7F || (escapeNonAscii && cp > 0x7F)) {
                            escape(cp, writer);
                        } else {
                            writer.write(cp);
                        }
                    } else {
                        if (escapeNonAscii) {
                            for (final char ch : Character.toChars(cp)) {
                                escape(ch, writer);
                            }
                        } else {
                            writer.write(Character.toChars(cp));
                        }
                    }
                }
            }
            index += charCount;
        }
    }

    private static void escape(final int ch, final Writer writer) throws IOException {
        writer.write("\\u");
        HexUtils.writeHex4(ch, writer);
    }

    private static Set<Option> toSet(final Option... options) {
        return options.length > 0 ? EnumSet.of(options[0], options) : EnumSet.noneOf(Option.class);
    }
}
