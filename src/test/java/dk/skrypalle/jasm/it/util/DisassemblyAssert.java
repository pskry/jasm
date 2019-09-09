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
package dk.skrypalle.jasm.it.util;

import dk.skrypalle.jasm.disassembler.Disassembly;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.AbstractAssert;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class DisassemblyAssert extends AbstractAssert<DisassemblyAssert, Disassembly> {

    DisassemblyAssert(Disassembly disassembly) {
        super(disassembly, DisassemblyAssert.class);
    }

    /**
     * Asserts that the {@linkplain Disassembly} under test has {@linkplain
     * Disassembly#getJasmSourceCode() jASM source code} equivalent (ignoring comments and newlines)
     * to the provided jASM source code.
     *
     * @param jasmSource jASM source code to test against
     * @return this assert for method chaining
     */
    public DisassemblyAssert hasSourceEquivalentTo(String jasmSource) {
        var srcWithoutComments = removeComments(jasmSource);

        assertThat(actual.getJasmSourceCode())
                .isEqualToIgnoringNewLines(srcWithoutComments);

        return myself;
    }

    /**
     * Asserts that the {@linkplain Disassembly} under test has {@linkplain
     * Disassembly#getJasmSourceCode() jASM source code} matching the provided {@linkplain Pattern
     * regEx pattern}.
     *
     * @param pattern {@linkplain Pattern regEx pattern} to match against
     * @return this assert for method chaining
     */
    public DisassemblyAssert containsSourcePattern(String pattern) {
        assertThat(actual.getJasmSourceCode())
                .containsPattern(pattern);

        return myself;
    }

    /**
     * Asserts that the {@linkplain Disassembly} under test has {@linkplain
     * Disassembly#getJvmClassName() JVM class name} equal to the provided JVM class name.
     *
     * @param jvmClassName JVM class name to test against
     * @return this assert for method chaining
     */
    public DisassemblyAssert hasJvmClassName(String jvmClassName) {
        assertThat(actual.getJvmClassName())
                .isEqualTo(jvmClassName);

        return myself;
    }

    @Override
    public DisassemblyAssert isEqualTo(Object expected) {
        if (expected.getClass() == Disassembly.class) {
            var expectedDisassembly = (Disassembly) expected;
            hasJvmClassName(expectedDisassembly.getJvmClassName());
            hasSourceEquivalentTo(expectedDisassembly.getJasmSourceCode());
            return myself;
        }

        return super.isEqualTo(expected);
    }

    private static String removeComments(String jasmSource) {
        var buf = new StringBuilder();
        var normalized = jasmSource.replace("\r\n", "\n");

        for (var line : normalized.split("\\n")) {
            String toAppend;
            var commentStart = line.indexOf('#');
            if (commentStart == -1) {
                // no comment in line
                toAppend = line;
            } else {
                // comment in line
                // need to check whether it lies inside a string literal
                var literal = findStringLiteral(line);
                if (literal == null) {
                    toAppend = line.substring(0, commentStart);
                } else {
                    if (literal[0] < commentStart && literal[1] > commentStart) {
                        // # inside string literal, keep as is
                        toAppend = line;
                    } else {
                        toAppend = line.substring(0, commentStart);
                    }
                }
            }

            buf.append(StringUtils.stripEnd(toAppend, null)).append('\n');
        }
        return buf.toString();
    }

    private static int[] findStringLiteral(String line) {
        var start = line.indexOf("\"");
        if (start == -1) {
            return null;
        }

        for (int i = start + 1; i < line.length(); i++) {
            if (line.charAt(i) == '"') {
                if (line.charAt(i - 1) != '\\') {
                    return new int[]{start, i};
                }
                if (line.charAt(i - 1) == '\\' && line.charAt(i - 2) == '\\') {
                    return new int[]{start, i};
                }
            }
        }

        return null;
    }

}
