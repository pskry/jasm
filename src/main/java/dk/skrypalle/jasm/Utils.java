/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright © 2018 Peter Skrypalle
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
package dk.skrypalle.jasm;

import org.apache.commons.io.HexDump;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Opcodes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Utils {

    public static final int ASM_VERSION = Opcodes.ASM7;

    /**
     * Dump an array of bytes.
     *
     * <p>The output is formatted for human inspection, with a hexadecimal offset followed by the
     * hexadecimal values of the next 16 bytes of data and the printable ASCII characters (if any)
     * that those bytes represent printed per each line of output.
     *
     * @param data the byte array to be dumped
     * @return human readable hex-dump
     */
    public static String hexDump(byte[] data) {
        var out = new ByteArrayOutputStream();
        try {
            HexDump.dump(data, 0, out, 0);
        } catch (IOException e) { /* ignored - cannot happen in ByteArrayOutputStream */ }

        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * Escapes special whitespace characters '\t', '\n' and '\r'.
     *
     * @param string the string to escape.
     * @return the escaped string.
     */
    public static String escapeSpecialWhitespace(String string) {
        var buf = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c == '\t') {
                buf.append("\\t");
            } else if (c == '\n') {
                buf.append("\\n");
            } else if (c == '\r') {
                buf.append("\\r");
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    /**
     * Quotes all keywords contained in the input string.
     *
     * @param input string to search and quote
     * @return quoted string
     */
    public static String quoteKeywords(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }

        if (!StringUtils.contains(input, '/')) {
            return quoteIfKeyword(input);
        }

        return Stream.of(StringUtils.split(input, "/"))
                .map(Utils::quoteIfKeyword)
                .collect(Collectors.joining("/"));
    }

    private static String quoteIfKeyword(String input) {
        switch (input) {
            case "any":
            case "strict":
            case "annotation":
            case "NaN":
                return "\"" + input + "\"";
            default:
                return input;
        }
    }

}
