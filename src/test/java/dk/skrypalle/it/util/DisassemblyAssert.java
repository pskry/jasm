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
package dk.skrypalle.it.util;

import dk.skrypalle.jdsm.Disassembly;
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

    private String removeComments(String jasmSource) {
        var buf = new StringBuilder();
        var normalized = jasmSource.replace("\r\n", "\n");

        for (String line : normalized.split("\\n")) {
            var chars = line.toCharArray();
            for (char c : chars) {
                if (c == '#') {
                    break;
                }
                buf.append(c);
            }
            buf.append('\n');
        }
        return buf.toString();
    }

}
