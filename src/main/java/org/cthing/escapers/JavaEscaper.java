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
 * Escapes strings and character arrays according to the
 * <a href="https://docs.oracle.com/javase/specs/jls/se17/html/jls-3.html#jls-EscapeSequence">Escape Sequences</a>
 * section of the <a href="https://docs.oracle.com/javase/specs/jls/se17/html">Java Language Specification</a>.
 * Characters are escaped according to the following table:
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
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">&#x5C;uNNNN</td>
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
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Newline</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x0B</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">&#x5C;uNNNN</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">ASCII control characters</td>
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
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">&#x5C;uNNNN</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">ASCII control characters</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x20</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged or \s</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Space character</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x21</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Printable ASCII characters</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x22</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\&quot;</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Double quote</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x23 - 0x5B</td>
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
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x7F - 0xFFFF</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">&#x5C;uNNNN</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unicode BMP and surrogate pairs</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
public final class JavaEscaper {

    @NoCoverageGenerated
    private JavaEscaper() {
    }

    /**
     * Applies JSON escaping to the specified string.
     *
     * @param charSequence String to escape
     * @param escapeSpace With the introduction of
     *      <a href="https://docs.oracle.com/javase/specs/jls/se15/html/jls-3.html#jls-3.10.6">text nlocks</a> in
     *      Java 15, an escape sequence ('\s') was created to indicate a space character (0x20). If this parameter
     *      is {@code true}, space characters are written using the '\s' escape sequence. If this parameter is
     *      {@code false}, space characters are written unescaped. When targeting Java 15 or newer, this parameter
     *      can be set to {@code true} if the escaped string will be used in a text block. When targeting Java 14 or
     *      older, this parameter must be set to {@code false}.
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final CharSequence charSequence, final boolean escapeSpace) {
        if (charSequence == null) {
            return null;
        }

        try {
            final StringWriter writer = new StringWriter();
            escape(charSequence::charAt, charSequence.length(), escapeSpace, writer);
            return writer.toString();
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * Applies JSON escaping to the specified string and writes the result to the specified writer.
     *
     * @param charSequence String to escape
     * @param escapeSpace With the introduction of
     *      <a href="https://docs.oracle.com/javase/specs/jls/se15/html/jls-3.html#jls-3.10.6">text nlocks</a> in
     *      Java 15, an escape sequence ('\s') was created to indicate a space character (0x20). If this parameter
     *      is {@code true}, space characters are written using the '\s' escape sequence. If this parameter is
     *      {@code false}, space characters are written unescaped. When targeting Java 15 or newer, this parameter
     *      can be set to {@code true} if the escaped string will be used in a text block. When targeting Java 14 or
     *      older, this parameter must be set to {@code false}.
     * @param writer Writer to which the escaped string is written
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    @WillNotClose
    public static void escape(final CharSequence charSequence, final boolean escapeSpace, final Writer writer)
            throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("writer must not be null");
        }
        if (charSequence == null) {
            return;
        }

        escape(charSequence::charAt, charSequence.length(), escapeSpace, writer);
    }

    /**
     * Applies JSON escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param escapeSpace With the introduction of
     *      <a href="https://docs.oracle.com/javase/specs/jls/se15/html/jls-3.html#jls-3.10.6">text nlocks</a> in
     *      Java 15, an escape sequence ('\s') was created to indicate a space character (0x20). If this parameter
     *      is {@code true}, space characters are written using the '\s' escape sequence. If this parameter is
     *      {@code false}, space characters are written unescaped. When targeting Java 15 or newer, this parameter
     *      can be set to {@code true} if the escaped string will be used in a text block. When targeting Java 14 or
     *      older, this parameter must be set to {@code false}.
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final char[] charArr, final boolean escapeSpace) {
        if (charArr == null) {
            return null;
        }

        try {
            final StringWriter writer = new StringWriter();
            escape(index -> charArr[index], charArr.length, escapeSpace, writer);
            return writer.toString();
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * Applies JSON escaping to the specified character array and writes the result to the specified writer.
     *
     * @param charArr Character array to escape
     * @param escapeSpace With the introduction of
     *      <a href="https://docs.oracle.com/javase/specs/jls/se15/html/jls-3.html#jls-3.10.6">text nlocks</a> in
     *      Java 15, an escape sequence ('\s') was created to indicate a space character (0x20). If this parameter
     *      is {@code true}, space characters are written using the '\s' escape sequence. If this parameter is
     *      {@code false}, space characters are written unescaped. When targeting Java 15 or newer, this parameter
     *      can be set to {@code true} if the escaped string will be used in a text block. When targeting Java 14 or
     *      older, this parameter must be set to {@code false}.
     * @param writer Writer to which the escaped string is written
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    @WillNotClose
    public static void escape(final char[] charArr, final boolean escapeSpace, final Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("writer must not be null");
        }
        if (charArr == null) {
            return;
        }

        escape(index -> charArr[index], charArr.length, escapeSpace, writer);
    }

    private static void escape(final CharProvider charProvider, final int length, final boolean escapeSpace,
                               final Writer writer) throws IOException {
        for (int i = 0; i < length; i++) {
            final char ch = charProvider.charAt(i);
            switch (ch) {
                case '"':
                    writer.write("\\\"");
                    break;
                case '\\':
                    writer.write("\\\\");
                    break;
                case '\n':
                    writer.write("\\n");
                    break;
                case '\r':
                    writer.write("\\r");
                    break;
                case '\f':
                    writer.write("\\f");
                    break;
                case '\t':
                    writer.write("\\t");
                    break;
                case '\b':
                    writer.write("\\b");
                    break;
                case ' ':
                    if (escapeSpace) {
                        writer.write("\\s");
                    } else {
                        writer.write(' ');
                    }
                    break;
                default:
                    if (ch > '\u001F' && ch < '\u007F') {
                        writer.write(ch);
                    } else {
                        writer.write("\\u");
                        HexUtils.writeHex4(ch, writer);
                    }
                    break;
            }
        }
    }
}
