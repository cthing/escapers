/*
 * Copyright 2024 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */

package org.cthing.escapers;

/**
 * Functional interface for abstracting the access of code points so that character sequences and character array
 * code points can be accessed identically.
 */
@FunctionalInterface
interface CodePointProvider {

    /**
     * Obtains the Unicode code point at the specified index into a character sequence or character array.
     *
     * @param index Zero-based index into a character sequence or character array
     * @return Unicode code point at the specified index.
     */
    int codePointAt(int index);
}
