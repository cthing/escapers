/*
 * Copyright 2024 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */

package org.cthing.escapers;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;


/**
 * Utilities for creating hexadecimal strings.
 */
final class HexUtils {

    private static final char[] HEX_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
    };

    private HexUtils() {
    }

    /**
     * Writes the specified value to the specified writer as a string of two uppercase hexadecimal digits. Values
     * less that 0x10 are padded with a leading zero.
     *
     * @param value Value between 0 and 255 inclusive to write as a two digit hexadecimal string. Values greater
     *      than 255 are truncated.
     * @param writer Writer to which the hexadecimal string is written
     * @throws IOException if there is a problem writing the string
     */
    static void writeHex2(final int value, final Writer writer) throws IOException {
        writer.write(HEX_DIGITS[value >> 4 & 0xF]);
        writer.write(HEX_DIGITS[value & 0xF]);
    }

    /**
     * Writes the specified value to the specified writer as a string of four uppercase hexadecimal digits. Values
     * less that 0x1000 are padded with a leading zeros.
     *
     * @param value Value between 0 and 65535 inclusive to write as a four digit hexadecimal string. Values greater
     *      than 65535 are truncated.
     * @param writer Writer to which the hexadecimal string is written
     * @throws IOException if there is a problem writing the string
     */
    static void writeHex4(final int value, final Writer writer) throws IOException {
        writer.write(HEX_DIGITS[value >> 12 & 0xF]);
        writer.write(HEX_DIGITS[value >> 8 & 0xF]);
        writeHex2(value, writer);
    }

    /**
     * Writes the specified value to the specified writer as a string of eight uppercase hexadecimal digits. Values
     * less that 0x10000000 are padded with a leading zeros.
     *
     * @param value Value between 0 and 2147483647 inclusive to write as an eight digit hexadecimal string.
     * @param writer Writer to which the hexadecimal string is written
     * @throws IOException if there is a problem writing the string
     */
    static void writeHex8(final int value, final Writer writer) throws IOException {
        writer.write(HEX_DIGITS[value >> 28 & 0xF]);
        writer.write(HEX_DIGITS[value >> 24 & 0xF]);
        writer.write(HEX_DIGITS[value >> 20 & 0xF]);
        writer.write(HEX_DIGITS[value >> 16 & 0xF]);
        writeHex4(value, writer);
    }

    /**
     * Writes the specified value to the specified writer as a variable length string of uppercase hexadecimal digits.
     * No zero padding is applied.
     *
     * @param value Value to write as a hexadecimal string
     * @param writer Writer to which the hexadecimal string is written
     * @throws IOException if there is a problem writing the string
     */
    static void writeHex(final int value, final Writer writer) throws IOException {
        writer.write(Integer.toHexString(value).toUpperCase(Locale.ENGLISH));
    }
}
