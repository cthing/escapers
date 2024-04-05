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

import javax.annotation.WillNotClose;

import org.cthing.annotations.NoCoverageGenerated;


/**
 * Escapes strings and character arrays using <a href="https://www.w3.org/TR/xml/">XML 1.0</a> entities. For example,
 * calling the {@link #escape(CharSequence)} with the string "this &amp; that" produces the string
 * "this {@literal &amp;} that". Characters are escaped according to the following table:
 *
 * <table style="border: 1px solid; border-collapse: collapse;">
 *     <caption>Character Escaping</caption>
 *     <tr>
 *         <th style="border: 1px solid; border-collapse: collapse; padding: 5px;">Character</th>
 *         <th style="border: 1px solid; border-collapse: collapse; padding: 5px;">Escape</th>
 *         <th style="border: 1px solid; border-collapse: collapse; padding: 5px;">Description</th>
 *     </tr>
 *     <tr>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">'</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">{@literal &apos;}</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Single quote</td>
 *     </tr>
 *     <tr>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">&quot;</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">{@literal &quot;}</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Double quote</td>
 *     </tr>
 *     <tr>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">&amp;</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">{@literal &amp;}</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Ampersand</td>
 *     </tr>
 *     <tr>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">&gt;</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">{@literal &gt;}</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Greater than symbol</td>
 *     </tr>
 *     <tr>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">&lt;</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">{@literal &lt;}</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Less than symbol</td>
 *     </tr>
 *     <tr>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\n, \t, \r</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Newline, carriage return and tab</td>
 *     </tr>
 *     <tr>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x20 - 0x07E</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Printable ASCII characters</td>
 *     </tr>
 *     <tr>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x7F - 0xD7FF</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Numeric character entity (e.g. {@literal &#x7F;})</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Printable Unicode characters</td>
 *     </tr>
 *     <tr>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0xE000 - 0xFFFD</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Numeric character entity (e.g. {@literal &#xE000;})</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Printable Unicode characters</td>
 *     </tr>
 *     <tr>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x10000 - 0x10FFFF</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Numeric character entity (e.g. {@literal &#x10000;})</td>
 *         <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Printable Unicode surrogate pair characters</td>
 *     </tr>
 * </table>
 * <p>
 * The characters in the above table correspond to the <a href="https://www.w3.org/TR/xml/#charsets">valid XML
 * characters</a>. Invalid characters are not included in the output (e.g. the null character 0x00 is dropped).
 * </p>
 */
public final class XmlEscaper {

    @FunctionalInterface
    private interface CodePointProvider {
        int codePointAt(int index);
    }

    @NoCoverageGenerated
    private XmlEscaper() {
    }

    /**
     * Applies XML escaping to the specified string.
     *
     * @param charSequence String to escape
     * @return Escaped string or {@code null} if {@code null} was passed in. Note that invalid XML characters are
     *      not included in the output.
     */
    public static String escape(final CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }

        try {
            final StringWriter writer = new StringWriter();
            escape(index -> Character.codePointAt(charSequence, index), charSequence.length(), writer);
            return writer.toString();
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * Applies XML escaping to the specified string and writes the result to the specified writer. Note that
     * invalid XML characters are not included in the output.
     *
     * @param charSequence String to escape
     * @param writer Writer to which the escaped string is written
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    @WillNotClose
    public static void escape(final CharSequence charSequence, final Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("writer must not be null");
        }
        if (charSequence == null) {
            return;
        }

        escape(index -> Character.codePointAt(charSequence, index), charSequence.length(), writer);
    }

    /**
     * Applies XML escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @return Escaped string or {@code null} if {@code null} was passed in. Note that invalid XML characters are
     *      not included in the output.
     */
    public static String escape(final char[] charArr) {
        if (charArr == null) {
            return null;
        }

        try {
            final StringWriter writer = new StringWriter();
            escape(index -> Character.codePointAt(charArr, index), charArr.length, writer);
            return writer.toString();
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * Applies XML escaping to the specified character array and writes the result to the specified writer. Note that
     * invalid XML characters are not included in the output.
     *
     * @param charArr Character array to escape
     * @param writer Writer to which the escaped string is written
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    @WillNotClose
    public static void escape(final char[] charArr, final Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("writer must not be null");
        }
        if (charArr == null) {
            return;
        }

        escape(index -> Character.codePointAt(charArr, index), charArr.length, writer);
    }

    private static void escape(final CodePointProvider codePointProvider, final int length,
                                       final Writer writer) throws IOException {
        int index = 0;
        while (index < length) {
            final int cp = codePointProvider.codePointAt(index);
            switch (cp) {
                case '\'' -> writer.write("&apos;");
                case '"' -> writer.write("&quot;");
                case '&' -> writer.write("&amp;");
                case '<' -> writer.write("&lt;");
                case '>' -> writer.write("&gt;");
                case '\n', '\t', '\r' -> writer.write(cp);
                default -> {
                    if (cp > 0x001F && cp < 0x007F) {
                        writer.write(cp);
                    } else if ((cp >= 0x007F && cp <= 0xD7FF)
                            || (cp >= 0xE000 && cp <= 0xFFFD)
                            || (cp >= 0x10000 && cp <= 0x10FFFF)) {
                        writer.write("&#x");
                        writer.write(Integer.toHexString(cp).toUpperCase());
                        writer.write(';');
                    }
                }
            }
            index += Character.charCount(cp);
        }
    }
}
