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
package dk.skrypalle.jasm.it.jdk;

import dk.skrypalle.jasm.assembler.Assemblers;
import dk.skrypalle.jasm.assembler.Assembly;
import dk.skrypalle.jasm.disassembler.Disassemblers;
import dk.skrypalle.jasm.disassembler.Disassembly;
import dk.skrypalle.jasm.it.assembler.JasmAssertingErrorListener;
import dk.skrypalle.jasm.it.disassembler.JdsmAssertingErrorListener;
import dk.skrypalle.jasm.it.util.TestAssertions;
import dk.skrypalle.jasm.it.util.TestDataProvider;
import dk.skrypalle.jasm.it.util.TestUtil;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.stream.Stream;

import static dk.skrypalle.jasm.it.util.TestAssertions.assertThat;

public class JdkRoundTripIntegrationTest {

    @DataProvider
    public static Object[][] provideSpecificJdkClassNames() {
        return Stream.of(
        )
                .map(className -> new Object[]{className})
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "provideSpecificJdkClassNames")
    public void roundTrip(String className) {
        runRoundTrip(className);
    }

    @Test(
            dataProviderClass = TestDataProvider.class,
            dataProvider = "provideWorkingJdkClassNames"
    )
    public void roundTripWorkingJdkClass(String className) {
        runRoundTrip(className);
    }

    @Test(
            dataProviderClass = TestDataProvider.class,
            dataProvider = "provideJdkClassNames",
            enabled = false
    )
    public void roundTripJdkClass(String className) {
        runRoundTrip(className);
    }

    private void runRoundTrip(String className) {
        var disassembly = disassemble(className);
        var assembly = assemble(disassembly);
        var roundTripDisassembly = disassemble(assembly);

        TestAssertions.assertThat(roundTripDisassembly)
                .isEqualTo(disassembly);
    }

    private Disassembly disassemble(String className) {
        // arrange
        var dsm = Disassemblers.fromClassName(
                className,
                new JdsmAssertingErrorListener(),
                true
        );

        // act
        var disassembly = dsm.disassemble();

        // assert
        var jvmClassName = TestUtil.toJvmClassName(className);
        var regexSafeJvmClassName = TestUtil.escapeJvmClassNameForRegex(jvmClassName);
        assertThat(disassembly)
                .isNotNull()
                .hasJvmClassName(jvmClassName)
                .containsSourcePattern("\\.class.*" + regexSafeJvmClassName + "\\n");

        return disassembly;
    }

    private Disassembly disassemble(Assembly assembly) {
        // arrange
        var dsm = Disassemblers.fromBinary(
                assembly.getBinaryData(),
                assembly.getJvmClassName(),
                new JdsmAssertingErrorListener(),
                true
        );

        // act
        var disassembly = dsm.disassemble();

        // assert
        var jvmClassName = assembly.getJvmClassName();
        var regexSafeJvmClassName = TestUtil.escapeJvmClassNameForRegex(jvmClassName);
        assertThat(disassembly)
                .isNotNull()
                .hasJvmClassName(jvmClassName)
                .containsSourcePattern("\\.class.*" + regexSafeJvmClassName + "\\n");

        return disassembly;
    }

    private Assembly assemble(Disassembly disassembly) {
        // arrange
        var asm = Assemblers.fromString(
                disassembly.getJasmSourceCode(),
                disassembly.getJvmClassName(),
                new JasmAssertingErrorListener(),
                true
        );

        // act
        var assembly = asm.assemble();

        // assert
        assertThat(assembly)
                .as("assembly not expected to be null")
                .isNotNull();

        return assembly;
    }

}
