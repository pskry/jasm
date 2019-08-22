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
package dk.skrypalle.jasm.it;

import dk.skrypalle.jasm.assembler.Assemblers;
import dk.skrypalle.jasm.assembler.Assembly;
import dk.skrypalle.jasm.disassembler.Disassemblers;
import dk.skrypalle.jasm.disassembler.Disassembly;
import dk.skrypalle.jasm.it.assembler.JasmAssertingErrorListener;
import dk.skrypalle.jasm.it.disassembler.JdsmAssertingErrorListener;
import dk.skrypalle.jasm.it.util.TestDataProvider;
import org.testng.annotations.Test;

import java.nio.file.Path;

import static dk.skrypalle.jasm.it.util.TestAssertions.assertThat;

public class RoundTripIntegrationTest {

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "provideJasmSourceFiles")
    public void roundTrip(Path resourcePath) {
        // arrange
        // act
        var assembly = assemble(resourcePath);
        var disassembly = disassemble(assembly);
        var roundTripAssembly = assemble(disassembly);

        // assert
        assertThat(roundTripAssembly)
                .isEqualTo(assembly);
    }

    private Assembly assemble(Path resourcePath) {
        // arrange
        var asm = Assemblers.fromFile(
                resourcePath,
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
        assertThat(disassembly)
                .as("disassembly not expected to be null")
                .isNotNull();

        return disassembly;
    }

}
