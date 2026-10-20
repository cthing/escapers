/*
 * Copyright 2026 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */

package org.cthing.escapers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.BitSet;
import java.util.function.IntPredicate;

import org.cthing.annotations.NoCoverageGenerated;
import org.jspecify.annotations.Nullable;


/**
 * Base class for URL escapers. The escaping behavior is dictated by the escaping predicate and
 * the plus sign substitution parameters.
 */
public abstract class AbstractUrlEscaper extends AbstractEscaper {

    protected static final BitSet BASE_NO_ESCAPE = new BitSet(128);

    static {
        BASE_NO_ESCAPE.set('a', 'z' + 1);
        BASE_NO_ESCAPE.set('A', 'Z' + 1);
        BASE_NO_ESCAPE.set('0', '9' + 1);
    }

    @NoCoverageGenerated
    protected AbstractUrlEscaper() {
    }

    /**
     * Applies to the specified string.
     *
     * @param charSequence String to escape
     * @param noEscape Predicate indicating whether a given code point should be escaped
     * @param spaceAsPlus {@code true} if a space character should be replaced with a plus character
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    @Nullable
    protected static String escape(@Nullable final CharSequence charSequence, final IntPredicate noEscape,
                                   final boolean spaceAsPlus) {
        return (charSequence == null)
               ? null
               : escape(index -> Character.codePointAt(charSequence, index), 0, charSequence.length(), noEscape,
                        spaceAsPlus);
    }

    /**
     * Applies escaping to the specified string and writes the result to the specified writer.
     *
     * @param charSequence String to escape
     * @param writer Writer to which the escaped string is written. Will not be closed by this method.
     * @param noEscape Predicate indicating whether a given code point should be escaped
     * @param spaceAsPlus {@code true} if a space character should be replaced with a plus character
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    protected static void escape(@Nullable final CharSequence charSequence, final Writer writer,
                                 final IntPredicate noEscape, final boolean spaceAsPlus)
            throws IOException {
        if (charSequence != null) {
            escape(index -> Character.codePointAt(charSequence, index), 0, charSequence.length(), writer,
                   noEscape, spaceAsPlus);
        }
    }

    /**
     * Applies escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param noEscape Predicate indicating whether a given code point should be escaped
     * @param spaceAsPlus {@code true} if a space character should be replaced with a plus character
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    @Nullable
    protected static String escape(final char @Nullable[] charArr, final IntPredicate noEscape,
                                   final boolean spaceAsPlus) {
        return (charArr == null)
               ? null
               : escape(index -> Character.codePointAt(charArr, index), 0, charArr.length, noEscape, spaceAsPlus);
    }

    /**
     * Applies escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param offset Start index in array
     * @param length Number of characters in array
     * @param noEscape Predicate indicating whether a given code point should be escaped
     * @param spaceAsPlus {@code true} if a space character should be replaced with a plus character
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    @Nullable
    protected static String escape(final char @Nullable[] charArr, final int offset, final int length,
                                   final IntPredicate noEscape, final boolean spaceAsPlus) {
        return (charArr == null)
               ? null
               : escape(index -> Character.codePointAt(charArr, index), offset, length, noEscape, spaceAsPlus);
    }

    /**
     * Applies escaping to the specified character array and writes the result to the specified writer.
     *
     * @param charArr Character array to escape
     * @param writer Writer to which the escaped string is written. Will not be closed by this method.
     * @param noEscape Predicate indicating whether a given code point should be escaped
     * @param spaceAsPlus {@code true} if a space character should be replaced with a plus character
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    protected static void escape(final char @Nullable[] charArr, final Writer writer,
                                 final IntPredicate noEscape, final boolean spaceAsPlus) throws IOException {
        if (charArr != null) {
            escape(index -> Character.codePointAt(charArr, index), 0, charArr.length, writer, noEscape,
                   spaceAsPlus);
        }
    }

    /**
     * Applies escaping to the specified character array and writes the result to the specified writer.
     *
     * @param charArr Character array to escape
     * @param offset Start index in array
     * @param length Number of characters in array
     * @param writer Writer to which the escaped string is written. Will not be closed by this method.
     * @param noEscape Predicate indicating whether a given code point should be escaped
     * @param spaceAsPlus {@code true} if a space character should be replaced with a plus character
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    protected static void escape(final char @Nullable [] charArr, final int offset, final int length,
                                 final Writer writer, final IntPredicate noEscape, final boolean spaceAsPlus)
            throws IOException {
        if (charArr != null) {
            escape(index -> Character.codePointAt(charArr, index), offset, length, writer, noEscape,
                   spaceAsPlus);
        }
    }

    private static String escape(final CodePointProvider codePointProvider, final int offset, final int length,
                                 final IntPredicate noEscape, final boolean spaceAsPlus) {
        try {
            final StringWriter writer = new StringWriter();
            escape(codePointProvider, offset, length, writer, noEscape, spaceAsPlus);
            return writer.toString();
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static void escape(final CodePointProvider codePointProvider, final int offset, final int length,
                               final Writer writer, final IntPredicate noEscape, final boolean spaceAsPlus)
            throws IOException {
        if (length < 0) {
            throw new IndexOutOfBoundsException("length must be greater than or equal to 0");
        }

        int index = offset;
        final int end = offset + length;
        while (index < end) {
            final int cp = codePointProvider.codePointAt(index);
            final int charCount = Character.charCount(cp);

            if (spaceAsPlus && cp == 0x20) {
                writer.write('+');
            } else if (cp > 0x20 && cp < 0x7F && noEscape.test(cp)) {
                writer.write(cp);
            } else {
                escapePercent(cp, writer);
            }

            index += charCount;
        }
    }
}
