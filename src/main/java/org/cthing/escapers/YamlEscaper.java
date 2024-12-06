/*
 * Copyright 2024 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */

package org.cthing.escapers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Set;

import org.cthing.annotations.AccessForTesting;
import org.cthing.annotations.NoCoverageGenerated;
import org.jspecify.annotations.Nullable;


/**
 * Escapes strings and character arrays according to the
 * <a href="https://yaml.org/spec/1.2.2/#57-escaped-characters">Escaped Characters</a> section of the
 * <a href="https://yaml.org/spec/1.2.2/">YAML Ain’t Markup Language</a> specification. The returned string
 * may be surrounded by single or double quotes depending on the characters found in the string.
 *
 * <p>
 * The returned string will be surrounded by double quotes under the following conditions:
 * </p>
 * <ul>
 *     <li>The string contains a character that requires escaping</li>
 *     <li>The string contains a double quote, single quote or backslash</li>
 * </ul>
 *
 * <p>
 * The returned string will be surrounded by single quotes under the following conditions
 * </p>
 * <ul>
 *     <li>The string does not require double quotes</li>
 *     <li>The string is empty</li>
 *     <li>The string contains leading or trailing spaces</li>
 *     <li>The string begins with the document start character sequence</li>
 *     <li>The string end with the document end character sequence</li>
 *     <li>The string contains any of the following characters: {@literal # , [ ] { } & * ! | > % @ ? : - /}</li>
 * </ul>
 *
 * <p>
 * If none of the above conditions apply, the string is returned without quotes.
 * </p>
 *
 * <p>
 * Characters are escaped according to the following table:
 * </p>
 * <table style="border: 1px solid; border-collapse: collapse;">
 *     <caption>Character Escaping</caption>
 *     <thead>
 *         <tr>
 *             <th style="border: 1px solid; border-collapse: collapse; padding: 5px;">Code Point</th>
 *             <th style="border: 1px solid; border-collapse: collapse; padding: 5px;">Escape</th>
 *             <th style="border: 1px solid; border-collapse: collapse; padding: 5px;">Description</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x00</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\0</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Null</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x01 - 0x06</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\xXX</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">ASCII control characters</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x07</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\a</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Bell</td>
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
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\v</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Vertical tab</td>
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
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\xXX</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">ASCII control characters</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x20 - 0x21</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Printable ASCII characters</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x22</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\"</td>
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
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x7F</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\xXX</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">ASCII control characters</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x80 - 0x84</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\xXX</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Escaped 8-bit hex code</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x85</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\N</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Next line</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x86 - 0x9F</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\xXX</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">ISO Latin-1</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0xA0</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\_</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Non-breaking space</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0xA1 - 0xFF</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\xXX</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">ISO Latin-1</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x100 - 0x2027</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\&#x75;XXXX</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unicode BMP</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x2028</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\L</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Line separator</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x2029</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\P</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Paragraph separator</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x202A - 0xFFFF</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\&#x75;XXXX</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unicode BMP</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x10000 - 0x10FFFF</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">\&#x55;XXXXXXXX</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unicode surrogate pairs</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
public final class YamlEscaper extends AbstractEscaper {

    /**
     * Escaping options.
     */
    public enum Option {
        /**
         * Escape characters above the ASCII range (i.e. ch &gt; 0x7F). By default, only ASCII control characters
         * and YAML specified characters are escaped. Specifying this option causes all ISO Latin-1, Unicode
         * BMP and surrogate pair characters to be escaped.
         */
        ESCAPE_NON_ASCII
    }

    @AccessForTesting
    enum Quoting {
        None,
        Single,
        Double
    }

    private static final String SPECIAL_CHARS = "#,[]{}&*!|>%@?:-/";
    private static final String DOCUMENT_START = "---";
    private static final String DOCUMENT_END = "...";

    @NoCoverageGenerated
    private YamlEscaper() {
    }

    /**
     * Applies YAML escaping to the specified string.
     *
     * @param charSequence String to escape
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    @Nullable
    public static String escape(@Nullable final CharSequence charSequence, final Option... options) {
        return (charSequence == null)
               ? null : escape(charSequence.toString(), toEnumSet(YamlEscaper.Option.class, options));
    }

    /**
     * Applies YAML escaping to the specified string.
     *
     * @param charSequence String to escape
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    @Nullable
    public static String escape(@Nullable final CharSequence charSequence, final Set<Option> options) {
        return (charSequence == null) ? null : escape(charSequence.toString(), options);
    }

    /**
     * Applies YAML escaping to the specified string and writes the result to the specified writer.
     *
     * @param charSequence String to escape
     * @param writer Writer to which the escaped string is written. Will not be closed by this method.
     * @param options Escaping options
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    public static void escape(@Nullable final CharSequence charSequence, final Writer writer, final Option... options)
            throws IOException {
        if (charSequence != null) {
            escape(charSequence.toString(), writer, toEnumSet(YamlEscaper.Option.class, options));
        }
    }

    /**
     * Applies YAML escaping to the specified string and writes the result to the specified writer.
     *
     * @param charSequence String to escape
     * @param writer Writer to which the escaped string is written. Will not be closed by this method.
     * @param options Escaping options
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    public static void escape(@Nullable final CharSequence charSequence, final Writer writer, final Set<Option> options)
            throws IOException {
        if (charSequence != null) {
            escape(charSequence.toString(), writer, options);
        }
    }

    /**
     * Applies YAML escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in. Note that invalid XML characters are
     *      not included in the output.
     */
    @Nullable
    public static String escape(final char @Nullable[] charArr, final Option... options) {
        return (charArr == null) ? null : escape(new String(charArr), toEnumSet(YamlEscaper.Option.class, options));
    }

    /**
     * Applies YAML escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param offset Start index in array
     * @param length Number of characters in array
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in. Note that invalid XML characters are
     *      not included in the output.
     */
    @Nullable
    public static String escape(final char @Nullable[] charArr, final int offset, final int length,
                                final Option... options) {
        return (charArr == null) ? null : escape(new String(charArr, offset, length),
                                                 toEnumSet(YamlEscaper.Option.class, options));
    }

    /**
     * Applies YAML escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in. Note that invalid XML characters are
     *      not included in the output.
     */
    @Nullable
    public static String escape(final char @Nullable[] charArr, final Set<Option> options) {
        return (charArr == null) ? null : escape(new String(charArr), options);
    }

    /**
     * Applies YAML escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param offset Start index in array
     * @param length Number of characters in array
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in. Note that invalid XML characters are
     *      not included in the output.
     */
    @Nullable
    public static String escape(final char @Nullable[] charArr, final int offset, final int length,
                                final Set<Option> options) {
        return (charArr == null) ? null : escape(new String(charArr, offset, length), options);
    }

    /**
     * Applies YAML escaping to the specified character array and writes the result to the specified writer.
     *
     * @param charArr Character array to escape
     * @param writer Writer to which the escaped string is written. Will not be closed by this method.
     * @param options Escaping options
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    public static void escape(final char @Nullable[] charArr, final Writer writer, final Option... options)
            throws IOException {
        if (charArr != null) {
            escape(new String(charArr), writer, toEnumSet(YamlEscaper.Option.class, options));
        }
    }

    /**
     * Applies YAML escaping to the specified character array and writes the result to the specified writer.
     *
     * @param charArr Character array to escape
     * @param offset Start index in array
     * @param length Number of characters in array
     * @param writer Writer to which the escaped string is written. Will not be closed by this method.
     * @param options Escaping options
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    public static void escape(final char @Nullable[] charArr, final int offset, final int length, final Writer writer,
                              final Option... options) throws IOException {
        if (charArr != null) {
            escape(new String(charArr, offset, length), writer, toEnumSet(YamlEscaper.Option.class, options));
        }
    }

    /**
     * Applies YAML escaping to the specified character array and writes the result to the specified writer.
     *
     * @param charArr Character array to escape
     * @param writer Writer to which the escaped string is written. Will not be closed by this method.
     * @param options Escaping options
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    public static void escape(final char @Nullable[] charArr, final Writer writer, final Set<Option> options)
            throws IOException {
        if (charArr != null) {
            escape(new String(charArr), writer, options);
        }
    }
    /**
     * Applies YAML escaping to the specified character array and writes the result to the specified writer.
     *
     * @param charArr Character array to escape
     * @param offset Start index in array
     * @param length Number of characters in array
     * @param writer Writer to which the escaped string is written. Will not be closed by this method.
     * @param options Escaping options
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    public static void escape(final char @Nullable[] charArr, final int offset, final int length, final Writer writer,
                              final Set<Option> options) throws IOException {
        if (charArr != null) {
            escape(new String(charArr, offset, length), writer, options);
        }
    }

    private static String escape(final String str, final Set<Option> options) {
        try {
            final StringWriter writer = new StringWriter();
            escape(str, writer, options);
            return writer.toString();
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static void escape(final String str, final Writer writer, final Set<Option> options) throws IOException {
        final boolean escapeNonAscii = options.contains(Option.ESCAPE_NON_ASCII);

        final Quoting quoting = requiresQuotes(str, escapeNonAscii);
        if (quoting == Quoting.None) {
            writer.write(str);
            return;
        }
        if (quoting == Quoting.Single) {
            writer.write('\'');
            writer.write(str);
            writer.write('\'');
            return;
        }

        writer.write('"');

        int idx = 0;
        while (idx < str.length()) {
            final int cp = str.codePointAt(idx);
            switch (cp) {
                case 0x00 -> writer.write("\\0");
                case 0x07 -> writer.write("\\a");
                case 0x08 -> writer.write("\\b");
                case 0x09 -> writer.write("\\t");
                case 0x0A -> writer.write("\\n");
                case 0x0B -> writer.write("\\v");
                case 0x0C -> writer.write("\\f");
                case 0x0D -> writer.write("\\r");
                case 0x22 -> writer.write("\\\"");
                case 0x2F -> writer.write("\\/");
                case 0x5C -> writer.write("\\\\");
                case 0x85 -> writer.write("\\N");
                case 0xA0 -> writer.write("\\_");
                case 0x2028 -> writer.write("\\L");
                case 0x2029 -> writer.write("\\P");
                default -> {
                    if (cp < 0x20 || cp == 0x7F) {
                        writer.write("\\x");
                        HexUtils.writeHex2(cp, writer);
                    } else if (cp <= 0x7E) {
                        writer.write(cp);
                    } else {
                        if (escapeNonAscii) {
                            if (cp <= 0xFF) {
                                writer.write("\\x");
                                HexUtils.writeHex2(cp, writer);
                            } else if (cp <= 0xFFFF) {
                                writer.write("\\u");
                                HexUtils.writeHex4(cp, writer);
                            } else {
                                writer.write("\\U");
                                HexUtils.writeHex8(cp, writer);
                            }
                        } else {
                            if (cp <= 0xFFFF) {
                                writer.write(cp);
                            } else {
                                writer.write(Character.toChars(cp));
                            }
                        }
                    }
                }
            }

            idx += Character.charCount(cp);
        }

        writer.write('"');
    }

    /**
     * Indicates whether the specified string requires quoting according to the YAML spec. Anything requiring
     * escapes must be double-quoted. If the string only contains YAML indicator characters, it can be single-quoted.
     *
     * @param str String to test
     * @param escapeNonAscii {@code true} if non-ASCII characters should be escaped
     * @return Whether the string requires single, double or no quotes.
     */
    @AccessForTesting
    static Quoting requiresQuotes(final String str, final boolean escapeNonAscii) {
        if (str.isEmpty()) {
            return Quoting.Single;
        }

        boolean needsSingle = str.charAt(0) == ' '
                || str.charAt(str.length() - 1) == ' '
                || str.startsWith(DOCUMENT_START)
                || str.endsWith(DOCUMENT_END);

        int idx = 0;
        while (idx < str.length()) {
            final int cp = str.codePointAt(idx);

            if (SPECIAL_CHARS.indexOf(cp) != -1) {
                needsSingle = true;
            } else if (cp < 0x20
                    || cp == 0x22
                    || cp == 0x2F
                    || cp == 0x5C
                    || cp == 0x7F
                    || cp == 0x85
                    || cp == 0xA0
                    || cp == 0x2028
                    || cp == 0x2029) {
                return Quoting.Double;
            } else if (cp > 0x7F && escapeNonAscii) {
                return Quoting.Double;
            }

            idx += Character.charCount(cp);
        }

        return needsSingle ? Quoting.Single : Quoting.None;
    }
}
