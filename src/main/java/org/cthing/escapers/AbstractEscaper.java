/*
 * Copyright 2024 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */

package org.cthing.escapers;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.EnumSet;


/**
 * Base class for all escapers.
 */
abstract class AbstractEscaper {

    /**
     * Creates a set from the specified enum values.
     *
     * @param clazz Enum class
     * @param values Enum values
     * @param <T> Enum type
     * @return Set consisting of the specified values
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    protected static <T extends Enum<T>> EnumSet<T> toEnumSet(final Class<T> clazz, final T... values) {
        final EnumSet<T> set = EnumSet.noneOf(clazz);
        if (values.length > 0) {
            Collections.addAll(set, values);
        }
        return set;
    }

    /**
     * Writes the specified code point as a hexadecimal numeric entity reference.
     *
     * @param cp Code point to write
     * @param writer Destination writer
     * @throws IOException if there is a problem writing the escaped code point.
     */
    protected static void escapeHexEntity(final int cp, final Writer writer) throws IOException {
        writer.write("&#x");
        HexUtils.writeHex(cp, writer);
        writer.write(';');
    }

    /**
     * Writes the specified code point as a decimal numeric entity reference.
     *
     * @param cp Code point to write
     * @param writer Destination writer
     * @throws IOException if there is a problem writing the escaped code point.
     */
    protected static void escapeDecimalEntity(final int cp, final Writer writer) throws IOException {
        writer.write("&#");
        writer.write(String.valueOf(cp));
        writer.write(';');
    }

    /**
     * Writes the specified code point as a Unicode escape.
     *
     * @param cp Code point to write
     * @param writer Destination writer
     * @throws IOException if there is a problem writing the escaped code point.
     */
    protected static void escapeUnicode(final int cp, final Writer writer) throws IOException {
        writer.write("\\u");
        HexUtils.writeHex4(cp, writer);
    }

    /**
     * Writes the specified code point in a percent escaped UTF-8 encoded format. The UTF-8 encoding is
     * performed in compliance with ISO-10646 Section 9.1. The percent escaping is performed in compliance
     * with RFC 3986 section 2.1. This method performs escapes regardless of all code points. Any special
     * handling of characters (e.g. unreserved, spaces) must take place external to this method.
     *
     * @param cp Code point to write
     * @param writer Destination writer
     * @throws IOException if there was a problem writing the escaped code point.
     */
    protected static void escapePercent(final int cp, final Writer writer) throws IOException {
        if (cp <= 0x7F) {
            writePercentByte(cp, writer);
        } else if (cp <= 0x7FF) {
            writePercentByte(0xC0 | (cp >>> 6), writer);
            writePercentByte(0x80 | (cp & 0x3F), writer);
        } else if (cp <= 0xFFFF) {
            writePercentByte(0xE0 | (cp >>> 12), writer);
            writePercentByte(0x80 | ((cp >>> 6) & 0x3F), writer);
            writePercentByte(0x80 | (cp & 0x3F), writer);
        } else {
            writePercentByte(0xF0 | (cp >>> 18), writer);
            writePercentByte(0x80 | ((cp >>> 12) & 0x3F), writer);
            writePercentByte(0x80 | ((cp >>> 6) & 0x3F), writer);
            writePercentByte(0x80 | (cp & 0x3F), writer);
        }
    }

    private static void writePercentByte(final int b, final Writer writer) throws IOException {
        writer.write('%');
        HexUtils.writeHex2(b, writer);
    }
}
