/*
 * Copyright 2026 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */

package org.cthing.escapers;

import java.io.IOException;
import java.io.Writer;
import java.util.BitSet;
import java.util.function.IntPredicate;

import org.cthing.annotations.NoCoverageGenerated;
import org.jspecify.annotations.Nullable;


/**
 * Escapes strings and character arrays for use in the
 * <a href="https://www.rfc-editor.org/info/rfc3986/#section-3.5">fragment portion</a> of a URL.
 * <ul>
 * <li>The alphanumeric characters &quot;{@code a}&quot; through &quot;{@code z}&quot;,
 *     &quot;{@code A}&quot; through &quot;{@code Z}&quot; and &quot;{@code 0}&quot;
 *     through &quot;{@code 9}&quot; remain the same.
 * <li>The special characters  &quot;{@code -}&quot;, &quot;{@code _}&quot;, &quot;{@code .}&quot;,
 *     &quot;{@code *}&quot;, &quot;{@code :}&quot;, &quot;{@code ~}&quot;, &quot;{@code /}&quot;,
 *     &quot;{@code ?}&quot;, &quot;{@code @}&quot;, &quot;{@code !}&quot;, &quot;{@code $}&quot;,
 *     &quot;{@code &}&quot;, &quot;{@code '}&quot;, &quot;{@code (}&quot;, &quot;{@code )}&quot;,
 *     &quot;{@code +}&quot;, &quot;{@code ,}&quot;, &quot;{@code ;}&quot;, &quot;{@code =}&quot;
 *     remain the same.
 * <li>All other characters are unsafe and are first converted into one or more bytes using the
 *     UTF-8 encoding scheme. Each byte is then represented by the 3-character string
 *     &quot;<i>{@code %xy}</i>&quot;, where <i>xy</i> is the two-digit uppercase hexadecimal
 *     representation of the byte.
 * </ul>
 *
 */
public final class UrlFragmentEscaper extends AbstractUrlEscaper {

    private static final IntPredicate NO_ESCAPE;

    static {
        final BitSet bitSet = new BitSet(128);
        bitSet.or(BASE_NO_ESCAPE);
        bitSet.set('-');
        bitSet.set('_');
        bitSet.set('.');
        bitSet.set('*');
        bitSet.set(':');
        bitSet.set('~');
        bitSet.set('/');
        bitSet.set('?');
        bitSet.set('@');
        bitSet.set('!');
        bitSet.set('$');
        bitSet.set('&');
        bitSet.set('\'');
        bitSet.set('(');
        bitSet.set(')');
        bitSet.set('+');
        bitSet.set(',');
        bitSet.set(';');
        bitSet.set('=');

        NO_ESCAPE = bitSet::get;
    }

    @NoCoverageGenerated
    private UrlFragmentEscaper() {
    }

    /**
     * Applies URL fragment escaping to the specified string.
     *
     * @param charSequence String to escape
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    @Nullable
    public static String escape(@Nullable final CharSequence charSequence) {
        return escape(charSequence, NO_ESCAPE, false);
    }

    /**
     * Applies URL fragment escaping to the specified string and writes the result to the specified writer.
     *
     * @param charSequence String to escape
     * @param writer Writer to which the escaped string is written. Will not be closed by this method.
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    public static void escape(@Nullable final CharSequence charSequence, final Writer writer)
            throws IOException {
        escape(charSequence, writer, NO_ESCAPE, false);
    }

    /**
     * Applies URL fragment escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    @Nullable
    public static String escape(final char @Nullable[] charArr) {
        return escape(charArr, NO_ESCAPE, false);
    }

    /**
     * Applies URL fragment escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @param offset Start index in array
     * @param length Number of characters in array
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    @Nullable
    public static String escape(final char @Nullable[] charArr, final int offset, final int length) {
        return escape(charArr, offset, length, NO_ESCAPE, false);
    }

    /**
     * Applies URL fragment escaping to the specified character array and writes the result to the specified writer.
     *
     * @param charArr Character array to escape
     * @param writer Writer to which the escaped string is written. Will not be closed by this method.
     * @throws IOException if there was a problem writing the escaped string
     * @throws IllegalArgumentException if the writer is {@code null}
     */
    public static void escape(final char @Nullable[] charArr, final Writer writer) throws IOException {
        escape(charArr, writer, NO_ESCAPE, false);
    }

    /**
     * Applies URL fragment escaping to the specified character array and writes the result to the specified writer.
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
        escape(charArr, offset, length, writer, NO_ESCAPE, false);
    }
}
