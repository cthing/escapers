/*
 * Copyright 2026 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */

package org.cthing.escapers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;

import org.cthing.annotations.NoCoverageGenerated;
import org.jspecify.annotations.Nullable;


/**
 * Escapes strings and character arrays for use as SQL string literals. The characters that are escaped are those
 * that pose a security risk or could cause a syntax error if used unescaped in a SQL literal string.
 * <br/><br/>
 * <table>
 *     <caption>SQL Character Escaping</caption>
 *     <tr><th>Character</th><th>Escaped Character</th><th>Description</th></tr>
 *     <tr><td>{@code \0}</td><td>{@code \\0}</td><td>Null</td></tr>
 *     <tr><td>{@code \n}</td><td>{@code \\n}</td><td>Newline</td></tr>
 *     <tr><td>{@code \r}</td><td>{@code \\r}</td><td>Carriage return</td></tr>
 *     <tr><td>{@code \\}</td><td>{@code \\\\}</td><td>Backslash</td></tr>
 *     <tr><td>{@code '}</td><td>{@code \\'}</td><td>Single quote</td></tr>
 *     <tr><td>{@code "}</td><td>{@code \\"}</td><td>Double quote</td></tr>
 *     <tr><td>{@code \032}</td><td>{@code \\Z}</td><td>Ctrl-Z</td></tr>
 * </table>
 *
 * <p>
 * <b>Note</b>: Normally the escaping of SQL strings should be done using a {@link java.sql.PreparedStatement}.
 * Use {@link SqlEscaper} when it is not possible to use JDBC (e.g. setting the root password on a new MySQL
 * installation).
 * </p>
 */
public final class SqlEscaper extends AbstractEscaper {

    @NoCoverageGenerated
    private SqlEscaper() {
    }

    /**
     * Applies SQL escaping to the specified string.
     *
     * @param charSequence String to escape
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    @Nullable
    public static String escape(@Nullable final CharSequence charSequence) {
        return (charSequence == null)
               ? null : escape(index -> Character.codePointAt(charSequence, index), 0, charSequence.length());
    }

    /**
     * Applies SQL escaping to the specified string and writes the result to the specified writer.
     *
     * @param charSequence String to escape
     * @param writer Writer to which the escaped string is written. Will not be closed by this method.
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    public static void escape(@Nullable final CharSequence charSequence, final Writer writer) throws IOException {
        if (charSequence != null) {
            escape(index -> Character.codePointAt(charSequence, index), 0, charSequence.length(), writer);
        }
    }

    /**
     * Applies SQL escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    @Nullable
    public static String escape(final char @Nullable [] charArr) {
        return (charArr == null) ? null : escape(index -> Character.codePointAt(charArr, index), 0, charArr.length);
    }

    /**
     * Applies SQL escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param offset Start index in array
     * @param length Number of characters in array
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    @Nullable
    public static String escape(final char @Nullable [] charArr, final int offset, final int length) {
        return (charArr == null) ? null : escape(index -> Character.codePointAt(charArr, index), offset, length);
    }

    /**
     * Applies SQL escaping to the specified character array and writes the result to the specified writer.
     *
     * @param charArr Character array to escape
     * @param writer Writer to which the escaped string is written. Will not be closed by this method.
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    public static void escape(final char @Nullable [] charArr, final Writer writer) throws IOException {
        if (charArr != null) {
            escape(index -> Character.codePointAt(charArr, index), 0, charArr.length, writer);
        }
    }

    /**
     * Applies SQL escaping to the specified character array and writes the result to the specified writer.
     *
     * @param charArr Character array to escape
     * @param offset Start index in array
     * @param length Number of characters in array
     * @param writer Writer to which the escaped string is written. Will not be closed by this method.
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    public static void escape(final char @Nullable [] charArr, final int offset, final int length, final Writer writer)
            throws IOException {
        if (charArr != null) {
            escape(index -> Character.codePointAt(charArr, index), offset, length, writer);
        }
    }

    private static String escape(final CodePointProvider codePointProvider, final int offset, final int length) {
        try {
            final StringWriter writer = new StringWriter();
            escape(codePointProvider, offset, length, writer);
            return writer.toString();
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static void escape(final CodePointProvider codePointProvider, final int offset, final int length,
                               final Writer writer) throws IOException {
        if (length < 0) {
            throw new IndexOutOfBoundsException("length must be greater than or equal to 0");
        }

        int index = offset;
        final int end = offset + length;
        while (index < end) {
            final int cp = codePointProvider.codePointAt(index);
            final int charCount = Character.charCount(cp);
            switch (cp) {
                case '\0' -> writer.write("\\0");
                case '\n' -> writer.write("\\n");
                case '\r' -> writer.write("\\r");
                case '\\' -> writer.write("\\\\");
                case '\'' -> writer.write("\\'");
                case '"' -> writer.write("\\\"");
                case '\032' -> writer.write("\\Z");
                default -> {
                    if (charCount == 1) {
                        writer.write(cp);
                    } else {
                        writer.write(Character.toChars(cp));
                    }
                }
            }
            index += charCount;
        }
    }
}
