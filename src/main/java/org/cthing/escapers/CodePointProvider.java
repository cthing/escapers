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
