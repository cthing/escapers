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
public final class CsvEscaper {

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
        if (charSequence == null) {
            return null;
        }

        try {
            final StringWriter writer = new StringWriter();
            escape(index -> Character.codePointAt(charSequence, index), charSequence.length(), writer);
            return writer.toString();
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
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
        if (writer == null) {
            throw new IllegalArgumentException("writer must not be null");
        }
        if (charSequence == null) {
            return;
        }

        escape(index -> Character.codePointAt(charSequence, index), charSequence.length(), writer);
    }

    /**
     * Applies CSV escaping to the specified character array.
     *
     * @param charArr Character array to escape
     * @return Escaped string or {@code null} if {@code null} was passed in.
     */
    public static String escape(final char[] charArr) {
        if (charArr == null) {
            return null;
        }

        try {
            final StringWriter writer = new StringWriter();
            escape(index -> Character.codePointAt(charArr, index), charArr.length, writer);
            return writer.toString();
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
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
        if (writer == null) {
            throw new IllegalArgumentException("writer must not be null");
        }
        if (charArr == null) {
            return;
        }

        escape(index -> Character.codePointAt(charArr, index), charArr.length, writer);
    }

    private static void escape(final CodePointProvider codePointProvider, final int length, final Writer writer)
            throws IOException {
        final StringBuilder sb = new StringBuilder();

        boolean requiresQuotes = false;
        int index = 0;
        while (index < length) {
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
