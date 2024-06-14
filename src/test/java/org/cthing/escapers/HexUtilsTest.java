/*
 * Copyright 2024 C Thing Software
 * SPDX-License-Identifier: Apache-2.0
 */

package org.cthing.escapers;

import java.io.IOException;
import java.io.StringWriter;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;


public class HexUtilsTest {

    private StringWriter writer;

    @BeforeEach
    public void setup() {
        this.writer = new StringWriter();
    }

    public static Stream<Arguments> hex2Provider() {
        return Stream.of(
                arguments(0x0,   "00"),
                arguments(0x1,   "01"),
                arguments(0xF,   "0F"),
                arguments(0x10,  "10"),
                arguments(0x15,  "15"),
                arguments(0x1A,  "1A"),
                arguments(0x1F,  "1F"),
                arguments(0xFF,  "FF"),
                arguments(0x1FF, "FF")
        );
    }

    @ParameterizedTest
    @MethodSource("hex2Provider")
    public void testWriteHex2(final int value, final String hexStr) throws IOException {
        HexUtils.writeHex2(value, this.writer);
        assertThat(this.writer).hasToString(hexStr);
    }

    public static Stream<Arguments> hex4Provider() {
        return Stream.of(
                arguments(0x0,     "0000"),
                arguments(0x1,     "0001"),
                arguments(0xF,     "000F"),
                arguments(0x10,    "0010"),
                arguments(0x15,    "0015"),
                arguments(0x1A,    "001A"),
                arguments(0x1F,    "001F"),
                arguments(0xFF,    "00FF"),
                arguments(0x1FF,   "01FF"),
                arguments(0xFEDC,  "FEDC"),
                arguments(0x1FEDC, "FEDC")
        );
    }

    @ParameterizedTest
    @MethodSource("hex4Provider")
    public void testWriteHex4(final int value, final String hexStr) throws IOException {
        HexUtils.writeHex4(value, this.writer);
        assertThat(this.writer).hasToString(hexStr);
    }

    public static Stream<Arguments> hex8Provider() {
        return Stream.of(
                arguments(0x0,        "00000000"),
                arguments(0x1,        "00000001"),
                arguments(0xF,        "0000000F"),
                arguments(0x10,       "00000010"),
                arguments(0x15,       "00000015"),
                arguments(0x1A,       "0000001A"),
                arguments(0x1F,       "0000001F"),
                arguments(0xFF,       "000000FF"),
                arguments(0x1FF,      "000001FF"),
                arguments(0xFEDC,     "0000FEDC"),
                arguments(0x1FEDC,    "0001FEDC"),
                arguments(0xFAB1FEDC, "FAB1FEDC")
        );
    }

    @ParameterizedTest
    @MethodSource("hex8Provider")
    public void testWriteHex8(final int value, final String hexStr) throws IOException {
        HexUtils.writeHex8(value, this.writer);
        assertThat(this.writer).hasToString(hexStr);
    }

    public static Stream<Arguments> hexProvider() {
        return Stream.of(
                arguments(0x0,               "0"),
                arguments(0x1,               "1"),
                arguments(0xF,               "F"),
                arguments(0x10,             "10"),
                arguments(0x15,             "15"),
                arguments(0x1A,             "1A"),
                arguments(0x1F,             "1F"),
                arguments(0xFF,             "FF"),
                arguments(0x1FF,           "1FF"),
                arguments(0xFEDC,         "FEDC"),
                arguments(0x1FEDC,       "1FEDC"),
                arguments(0xFAB1FEDC, "FAB1FEDC")
        );
    }

    @ParameterizedTest
    @MethodSource("hexProvider")
    public void testWriteHex(final int value, final String hexStr) throws IOException {
        HexUtils.writeHex(value, this.writer);
        assertThat(this.writer).hasToString(hexStr);
    }
}
