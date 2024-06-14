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
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x20</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged or \s if {@link Option#ESCAPE_SPACE} specified</td>
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
public final class JavaEscaper extends AbstractEscaper {

    /**
     * Escaping options.
     */
    public enum Option {
        /**
         * Escape space characters. With the introduction of
         * <a href="https://docs.oracle.com/javase/specs/jls/se15/html/jls-3.html#jls-3.10.6"> text blocks</a> in
         * Java 15, an escape sequence ('\s') was created to indicate a space character (0x20). If this option is
         * specified, space characters are written using the '\s' escape sequence. Without this option, space
         * characters are written unescaped. When targeting Java 15 or newer, this option can be specified if the
         * escaped string will be used in a text block. When targeting Java 14 or older, this option must not be
         * specified.
         */
        ESCAPE_SPACE,

        /**
         * Escape characters above the ASCII range (i.e. ch &gt; 0x7F). By default, only ASCII control characters and
         * certain ASCII printable characters are escaped. Specifying this option causes all ISO Latin-1, Unicode BMP
         * and surrogate pair characters to be escaped (i.e. one or two &#x5C;uXXXX).
         */
        ESCAPE_NON_ASCII
    }

    @NoCoverageGenerated
    private JavaEscaper() {
    }

    /**
     * Applies JSON escaping to the specified string.
     *
     * @param charSequence String to escape
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final CharSequence charSequence, final Option... options) {
        return (charSequence == null)
               ? null
               : escape(index -> Character.codePointAt(charSequence, index), 0, charSequence.length(),
                        toEnumSet(JavaEscaper.Option.class, options));
    }

    /**
     * Applies JSON escaping to the specified string.
     *
     * @param charSequence String to escape
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final CharSequence charSequence, final Set<Option> options) {
        return (charSequence == null)
               ? null
               : escape(index -> Character.codePointAt(charSequence, index), 0, charSequence.length(), options);
    }

    /**
     * Applies JSON escaping to the specified string and writes the result to the specified writer.
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
            escape(index -> Character.codePointAt(charSequence, index), 0, charSequence.length(), writer,
                   toEnumSet(JavaEscaper.Option.class, options));
        }
    }

    /**
     * Applies JSON escaping to the specified string and writes the result to the specified writer.
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
            escape(index -> Character.codePointAt(charSequence, index), 0, charSequence.length(), writer, options);
        }
    }

    /**
     * Applies JSON escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final char[] charArr, final Option... options) {
        return (charArr == null)
               ? null
               : escape(index -> Character.codePointAt(charArr, index), 0, charArr.length,
                        toEnumSet(JavaEscaper.Option.class, options));
    }

    /**
     * Applies JSON escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param offset Start index in array
     * @param length Number of characters in array
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final char[] charArr, final int offset, final int length, final Option... options) {
        return (charArr == null)
               ? null
               : escape(index -> Character.codePointAt(charArr, index), offset, length,
                        toEnumSet(JavaEscaper.Option.class, options));
    }

    /**
     * Applies JSON escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final char[] charArr, final Set<Option> options) {
        return (charArr == null)
               ? null
               : escape(index -> Character.codePointAt(charArr, index), 0, charArr.length, options);
    }

    /**
     * Applies JSON escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param offset Start index in array
     * @param length Number of characters in array
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final char[] charArr, final int offset, final int length, final Set<Option> options) {
        return (charArr == null)
               ? null
               : escape(index -> Character.codePointAt(charArr, index), offset, length, options);
    }

    /**
     * Applies JSON escaping to the specified character array and writes the result to the specified writer.
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
            escape(index -> Character.codePointAt(charArr, index), 0, charArr.length, writer,
                   toEnumSet(JavaEscaper.Option.class, options));
        }
    }

    /**
     * Applies JSON escaping to the specified character array and writes the result to the specified writer.
     *
     * @param charArr Character array to escape
     * @param offset Start index in array
     * @param length Number of characters in array
     * @param writer Writer to which the escaped string is written
     * @param options Escaping options
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    @WillNotClose
    public static void escape(final char[] charArr, final int offset, final int length, final Writer writer,
                              final Option... options) throws IOException {
        if (charArr != null) {
            escape(index -> Character.codePointAt(charArr, index), offset, length, writer,
                   toEnumSet(JavaEscaper.Option.class, options));
        }
    }

    /**
     * Applies JSON escaping to the specified character array and writes the result to the specified writer.
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
            escape(index -> Character.codePointAt(charArr, index), 0, charArr.length, writer, options);
        }
    }

    /**
     * Applies JSON escaping to the specified character array and writes the result to the specified writer.
     *
     * @param charArr Character array to escape
     * @param offset Start index in array
     * @param length Number of characters in array
     * @param writer Writer to which the escaped string is written
     * @param options Escaping options
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    @WillNotClose
    public static void escape(final char[] charArr, final int offset, final int length, final Writer writer,
                              final Set<Option> options) throws IOException {
        if (charArr != null) {
            escape(index -> Character.codePointAt(charArr, index), offset, length, writer, options);
        }
    }

    private static String escape(final CodePointProvider codePointProvider, final int offset, final int length,
                                 final Set<Option> options) {
        try {
            final StringWriter writer = new StringWriter();
            escape(codePointProvider, offset, length, writer, options);
            return writer.toString();
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static void escape(final CodePointProvider codePointProvider, final int offset, final int length,
                               final Writer writer, final Set<Option> options) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("writer must not be null");
        }
        if (length < 0) {
            throw new IndexOutOfBoundsException("length must be greater than or equal to 0");
        }

        final boolean escapeSpace = options.contains(Option.ESCAPE_SPACE);
        final boolean escapeNonAscii = options.contains(Option.ESCAPE_NON_ASCII);

        int index = offset;
        final int end = offset + length;
        while (index < end) {
            final int cp = codePointProvider.codePointAt(index);
            final int charCount = Character.charCount(cp);
            switch (cp) {
                case '"' -> writer.write("\\\"");
                case '\\' -> writer.write("\\\\");
                case '\n' -> writer.write("\\n");
                case '\r' -> writer.write("\\r");
                case '\f' -> writer.write("\\f");
                case '\t' -> writer.write("\\t");
                case '\b' -> writer.write("\\b");
                case ' ' -> {
                    if (escapeSpace) {
                        writer.write("\\s");
                    } else {
                        writer.write(' ');
                    }
                }
                default -> {
                    if (charCount == 1) {
                        if (cp < 0x20 || cp == 0x7F || (escapeNonAscii && cp > 0x7F)) {
                            escapeUnicode(cp, writer);
                        } else {
                            writer.write(cp);
                        }
                    } else {
                        if (escapeNonAscii) {
                            for (final char ch : Character.toChars(cp)) {
                                escapeUnicode(ch, writer);
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
}
