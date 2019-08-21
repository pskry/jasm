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
package dk.skrypalle.it;

import dk.skrypalle.it.jasm.JasmAssertingErrorListener;
import dk.skrypalle.it.jdsm.JdsmAssertingErrorListener;
import dk.skrypalle.it.util.TestDataProvider;
import dk.skrypalle.it.util.TestUtil;
import dk.skrypalle.jasm.Assembler;
import dk.skrypalle.jasm.Assemblers;
import dk.skrypalle.jdsm.Disassembler;
import dk.skrypalle.jdsm.Disassemblers;
import dk.skrypalle.jdsm.Disassembly;
import org.testng.annotations.Test;

import static dk.skrypalle.it.util.TestAssertions.assertThat;

public class JdkRoundTripIntegrationTest {

    @Test(
            dataProviderClass = TestDataProvider.class,
            dataProvider = "provideJdkClassNames",
            enabled = false
    )
    public void roundTrip(String className) throws Exception {
        // disassembly::arrange
        var dsm = getDisassembler(className);

        // disassembly::act
        var disassembly = dsm.disassemble();

        // disassembly::assert
        var jvmClassName = TestUtil.toJvmClassName(className);
        var regexSafeJvmClassName = TestUtil.escapeJvmClassNameForRegex(jvmClassName);
        assertThat(disassembly)
                .isNotNull()
                .hasJvmClassName(jvmClassName)
                .containsSourcePattern("\\.class.*" + regexSafeJvmClassName + "\\n");

        // assembly::arrange
        var asm = getAssembler(disassembly);

        // assembly::act
        var assembly = asm.assemble();

        // assembly::assert
        assertThat(assembly)
                .as("assembly not expected to be null")
                .isNotNull();

        // assert
        var actualBinaryClassFile = TestUtil.readClass(className);
        assertThat(assembly)
                .isBinaryEqualTo(actualBinaryClassFile);
    }

    private Disassembler getDisassembler(String className) {
        return Disassemblers.fromClassName(
                className,
                new JdsmAssertingErrorListener(),
                true
        );
    }

    private Assembler getAssembler(Disassembly disassembly) {
        return Assemblers.fromString(
                disassembly.getJasmSourceCode(),
                disassembly.getJvmClassName(),
                new JasmAssertingErrorListener(),
                true
        );
    }

}
