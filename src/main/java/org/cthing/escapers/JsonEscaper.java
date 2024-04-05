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
 * <a href="https://datatracker.ietf.org/doc/html/rfc8259#page-8">JSON specification</a>. Characters are escaped
 * according to the following table:
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
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x23 - 0x2E</td>
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
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x7F - 0xFFFF</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">&#x5C;uNNNN</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unicode BMP and surrogate pairs</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
public final class JsonEscaper {

    @NoCoverageGenerated
    private JsonEscaper() {
    }

    /**
     * Applies JSON escaping to the specified string.
     *
     * @param charSequence String to escape
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }

        try {
            final StringWriter writer = new StringWriter();
            escape(charSequence::charAt, charSequence.length(), writer);
            return writer.toString();
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * Applies JSON escaping to the specified string and writes the result to the specified writer.
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

        escape(charSequence::charAt, charSequence.length(), writer);
    }

    /**
     * Applies JSON escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final char[] charArr) {
        if (charArr == null) {
            return null;
        }

        try {
            final StringWriter writer = new StringWriter();
            escape(index -> charArr[index], charArr.length, writer);
            return writer.toString();
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * Applies JSON escaping to the specified character array and writes the result to the specified writer.
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

        escape(index -> charArr[index], charArr.length, writer);
    }

    private static void escape(final CharProvider charProvider, final int length, final Writer writer)
            throws IOException {
        for (int i = 0; i < length; i++) {
            final char ch = charProvider.charAt(i);
            switch (ch) {
                case '"':
                    writer.write("\\\"");
                    break;
                case '\\':
                    writer.write("\\\\");
                    break;
                case '/':
                    writer.write("\\/");
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