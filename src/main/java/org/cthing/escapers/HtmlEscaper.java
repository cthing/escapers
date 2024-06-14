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
import java.util.function.Function;

import javax.annotation.WillNotClose;

import org.cthing.annotations.NoCoverageGenerated;


/**
 * Escapes string and character arrays using numeric and named HTML character entities. Markup-significant characters
 * (e.g. &lt;) are always escaped. Options are provided to escape additional characters using numeric or named
 * character entities. Characters are escaped according to the following table:
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
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x09</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Tab</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x0A</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Line feed</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x0D</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Carriage return</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x20 - 0x21</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Printable ASCII characters</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x22</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">{@literal &quot;}</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Double quote</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x23 - 0x25</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Printable ASCII characters</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x26</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">{@literal &amp;}</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Ampersand</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x27</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">{@literal &#x27;}</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Single quote</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x28 - 0x3B</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Printable ASCII characters</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x3C</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">{@literal &lt;}</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Less than</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x3D</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Printable ASCII characters</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x3E</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">{@literal &gt;}</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Greater than</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x3F - 0x7E</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unchanged</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Printable ASCII characters</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x7F</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">
 *                 Numeric character entity (e.g. {@literal &#x7F;})
 *             </td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">ASCII control character</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x80 - 0xFF</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">
 *                 Unchanged by default or numeric character entity (e.g. {@literal &#x84;})
 *                 if {@link Option#ESCAPE_NON_ASCII} specified, or named character entity (e.g. {@literal &reg;})
 *                 if {@link Option#USE_ISO_LATIN_1_ENTITIES} specified.
 *             </td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">ISO Latin-1</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x100 - 0xD7FF</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">
 *                 Unchanged by default or numeric character entity (e.g. {@literal &#x7F;})
 *                 if {@link Option#ESCAPE_NON_ASCII} specified, or named character entity (e.g. {@literal &delta;})
 *                 if {@link Option#USE_HTML4_EXTENDED_ENTITIES} specified.
 *             </td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unicode BMP</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0xE000 - 0xFFFD</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">
 *                 Unchanged by default or numeric character entity (e.g. {@literal &#xE000;})
 *                 if {@link Option#ESCAPE_NON_ASCII} specified
 *             </td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unicode BMP</td>
 *         </tr>
 *         <tr>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">0x10000 - 0x10FFFF</td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">
 *                 Unchanged by default or numeric character entity (e.g. {@literal &#x10000;})
 *                 if {@link Option#ESCAPE_NON_ASCII} specified
 *             </td>
 *             <td style="border: 1px solid; border-collapse: collapse; padding: 5px;">Unicode surrogate pairs</td>
 *         </tr>
 *     </tbody>
 * </table>
 * <p>
 * Characters not listed in the above table are not included in the output (e.g. the null character 0x00 is dropped).
 * </p>
 */
public final class HtmlEscaper extends AbstractEscaper {

    /**
     * Escaping options.
     */
    public enum Option {

        /**
         * Use decimal for numerical character entities (i.e. &amp;#DDDD;). By default, this library uses hexadecimal
         * (i.e. &amp;#xHHH;) for numerical character entities.
         */
        USE_DECIMAL,

        /**
         * Escape characters above the ASCII range (i.e. ch &gt; 0x7F). By default, only ASCII control characters
         * and markup-significant ASCII characters are escaped. Specifying this option causes all ISO Latin-1,
         * Unicode BMP and surrogate pair characters to be escaped.
         */
        ESCAPE_NON_ASCII,

        /**
         * Replaces characters in the ISO Latin-1 range (0x80 - 0xFF) with their corresponding named entity
         * references. For example, the pound character (0xA3) is replaced with the pound entity reference
         * (&amp;pound;). See
         * <a href="https://www.w3.org/TR/html40/sgml/entities.html#h-24.2">Section 24.2</a> of the HTML 4
         * specification for the complete list of ISO Latin-1 character entities.
         * <p>
         * Note that markup-significant HTML escapes are always applied (e.g. &amp;lt;, &amp;quot;) regardless of
         * the use of this option.
         * </p>
         */
        USE_ISO_LATIN_1_ENTITIES,

        /**
         * Replaces characters in the HTML 4 extended range (0x100 - 0xFFFF) with their corresponding named entity
         * references where they exist. For example, the pi character (0x3A0) is replaced with the pi entity
         * reference (&amp;pi;). See
         * <a href="https://www.w3.org/TR/html40/sgml/entities.html#h-24.3">Section 24.3</a> and
         * <a href="https://www.w3.org/TR/html40/sgml/entities.html#h-24.4">Section 24.4 </a> of the HTML 4
         * specification for the complete list of these character entities.
         * <p>
         * Note that markup-significant HTML escapes are always applied (e.g. &amp;lt;, &amp;quot;) regardless of
         * the use of this option.
         * </p>
         */
        USE_HTML4_EXTENDED_ENTITIES
    }

    @FunctionalInterface
    private interface CharEscaper {
        void escape(int cp, Writer writer) throws IOException;
    }

    @NoCoverageGenerated
    private HtmlEscaper() {
    }

    /**
     * Applies HTML escaping to the specified string.
     *
     * @param charSequence String to escape
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final CharSequence charSequence, final Option... options) {
        return (charSequence == null)
               ? null
               : escape(index -> Character.codePointAt(charSequence, index), 0, charSequence.length(),
                        toEnumSet(HtmlEscaper.Option.class, options));
    }

    /**
     * Applies HTML escaping to the specified string.
     *
     * @param charSequence String to escape
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final CharSequence charSequence, final Set<Option> options) {
        return (charSequence == null)
               ? null : escape(index -> Character.codePointAt(charSequence, index), 0, charSequence.length(), options);
    }

    /**
     * Applies HTML escaping to the specified string and writes the result to the specified writer.
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
                   toEnumSet(HtmlEscaper.Option.class, options));
        }
    }

    /**
     * Applies HTML escaping to the specified string and writes the result to the specified writer.
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
     * Applies HTML escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final char[] charArr, final Option... options) {
        return (charArr == null)
               ? null
               : escape(index -> Character.codePointAt(charArr, index), 0, charArr.length,
                        toEnumSet(HtmlEscaper.Option.class, options));
    }

    /**
     * Applies HTML escaping to the specified character array.
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
                        toEnumSet(HtmlEscaper.Option.class, options));
    }

    /**
     * Applies HTML escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final char[] charArr, final Set<Option> options) {
        return (charArr == null)
               ? null : escape(index -> Character.codePointAt(charArr, index), 0, charArr.length, options);
    }

    /**
     * Applies HTML escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param offset Start index in array
     * @param length Number of characters in array
     * @param options Escaping options
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final char[] charArr, final int offset, final int length, final Set<Option> options) {
        return (charArr == null)
               ? null : escape(index -> Character.codePointAt(charArr, index), offset, length, options);
    }

    /**
     * Applies HTML escaping to the specified character array and writes the result to the specified writer.
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
                   toEnumSet(HtmlEscaper.Option.class, options));
        }
    }

    /**
     * Applies HTML escaping to the specified character array and writes the result to the specified writer.
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
                   toEnumSet(HtmlEscaper.Option.class, options));
        }
    }

    /**
     * Applies HTML escaping to the specified character array and writes the result to the specified writer.
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
     * Applies HTML escaping to the specified character array and writes the result to the specified writer.
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

        final CharEscaper charEscaper = options.contains(Option.USE_DECIMAL)
                                        ? AbstractEscaper::escapeDecimalEntity : AbstractEscaper::escapeHexEntity;
        final boolean escapeNonAscii = options.contains(Option.ESCAPE_NON_ASCII);
        final boolean useLatin1 = options.contains(Option.USE_ISO_LATIN_1_ENTITIES);
        final boolean useExtended = options.contains(Option.USE_HTML4_EXTENDED_ENTITIES);

        final Function<Integer, String> findEntity;
        if (!useLatin1 && !useExtended) {
            findEntity = HtmlEntities.MARKUP_SIGNIFICANT::get;
        } else {
            findEntity = cp -> {
                String entity = HtmlEntities.MARKUP_SIGNIFICANT.get(cp);
                if (entity != null) {
                    return entity;
                }
                if (useLatin1) {
                    entity = HtmlEntities.ISO_LATIN_1.get(cp);
                    if (entity != null) {
                        return entity;
                    }
                }
                return useExtended ? HtmlEntities.HTML4_EXTENDED.get(cp) : null;
            };
        }

        int index = offset;
        final int end = offset + length;
        while (index < end) {
            final int cp = codePointProvider.codePointAt(index);
            final String entity = findEntity.apply(cp);

            if (entity != null) {
                writer.write(entity);
            } else {
                switch (cp) {
                    case '\'' -> charEscaper.escape(cp, writer);
                    case '\n', '\t', '\r' -> writer.write(cp);
                    default -> {
                        if (cp > 0x1F && cp < 0x7F) {
                            writer.write(cp);
                        } else if (cp == 0x7F) {
                            charEscaper.escape(cp, writer);
                        } else if (escapeNonAscii) {
                            if ((cp >= 0x80 && cp <= 0xD7FF)
                                    || (cp >= 0xE000 && cp <= 0xFFFD)
                                    || (cp >= 0x10000 && cp <= 0x10FFFF)) {
                                charEscaper.escape(cp, writer);
                            }
                        } else {
                            if ((cp >= 0x80 && cp <= 0xD7FF) || (cp >= 0xE000 && cp <= 0xFFFD)) {
                                writer.write(cp);
                            } else if (cp >= 0x10000 && cp <= 0x10FFFF) {
                                writer.write(Character.toChars(cp));
                            }
                        }
                    }
                }
            }

            index += Character.charCount(cp);
        }
    }
}
