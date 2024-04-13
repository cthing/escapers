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
     * Write the specified code point as a Unicode escape.
     *
     * @param cp Code point to write
     * @param writer Destination writer
     * @throws IOException if there is a problem writing the escaped code point.
     */
    protected static void escapeUnicode(final int cp, final Writer writer) throws IOException {
        writer.write("\\u");
        HexUtils.writeHex4(cp, writer);
    }
}
