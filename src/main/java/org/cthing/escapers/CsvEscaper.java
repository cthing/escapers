/*
 * Copyright 2024 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */

package org.cthing.escapers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;

import javax.annotation.WillNotClose;

import org.cthing.annotations.NoCoverageGenerated;


/**
 * Escapes string and character arrays according to
 * <a href="https://datatracker.ietf.org/doc/html/rfc4180">RFC-4180</a>. If a string contains a comma,
 * carriage return, line feed, or a double quote, the string must be surrounded by double quotes and
 * any double quotes within the string must be replaced with two consecutive double quotes
 * (i.e. a single {@literal "} becomes {@literal ""}).
 */
public final class CsvEscaper extends AbstractEscaper {

    @NoCoverageGenerated
    private CsvEscaper() {
    }

    /**
     * Applies CSV escaping to the specified string.
     *
     * @param charSequence String to escape
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final CharSequence charSequence) {
        return (charSequence == null)
               ? null : escape(index -> Character.codePointAt(charSequence, index), 0, charSequence.length());
    }

    /**
     * Applies CSV escaping to the specified string and writes the result to the specified writer.
     *
     * @param charSequence String to escape
     * @param writer Writer to which the escaped string is written
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    @WillNotClose
    public static void escape(final CharSequence charSequence, final Writer writer) throws IOException {
        if (charSequence != null) {
            escape(index -> Character.codePointAt(charSequence, index), 0, charSequence.length(), writer);
        }
    }

    /**
     * Applies CSV escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final char[] charArr) {
        return (charArr == null) ? null : escape(index -> Character.codePointAt(charArr, index), 0, charArr.length);
    }

    /**
     * Applies CSV escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param offset Start index in array
     * @param length Number of characters in array
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final char[] charArr, final int offset, final int length) {
        return (charArr == null) ? null : escape(index -> Character.codePointAt(charArr, index), offset, length);
    }

    /**
     * Applies CSV escaping to the specified character array and writes the result to the specified writer.
     *
     * @param charArr Character array to escape
     * @param writer Writer to which the escaped string is written
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    @WillNotClose
    public static void escape(final char[] charArr, final Writer writer) throws IOException {
        if (charArr != null) {
            escape(index -> Character.codePointAt(charArr, index), 0, charArr.length, writer);
        }
    }

    /**
     * Applies CSV escaping to the specified character array and writes the result to the specified writer.
     *
     * @param charArr Character array to escape
     * @param offset Start index in array
     * @param length Number of characters in array
     * @param writer Writer to which the escaped string is written
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    @WillNotClose
    public static void escape(final char[] charArr, final int offset, final int length, final Writer writer)
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
                               final Writer writer)
            throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("writer must not be null");
        }
        if (length < 0) {
            throw new IndexOutOfBoundsException("length must be greater than or equal to 0");
        }

        final StringBuilder sb = new StringBuilder();

        boolean requiresQuotes = false;
        int index = offset;
        final int end = offset + length;
        while (index < end) {
            final int cp = codePointProvider.codePointAt(index);
            final int charCount = Character.charCount(cp);
            switch (cp) {
                case ',', '\n', '\r' -> {
                    sb.appendCodePoint(cp);
                    requiresQuotes = true;
                }
                case '"' -> {
                    sb.append("\"\"");
                    requiresQuotes = true;
                }
                default -> sb.appendCodePoint(cp);
            }
            index += charCount;
        }

        if (requiresQuotes) {
            writer.write('"');
            writer.write(sb.toString());
            writer.write('"');
        } else {
            writer.write(sb.toString());
        }
    }
}
