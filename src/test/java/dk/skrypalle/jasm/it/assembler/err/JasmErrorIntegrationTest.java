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
package dk.skrypalle.jasm.it.assembler.err;

import dk.skrypalle.jasm.assembler.Assembler;
import dk.skrypalle.jasm.assembler.Assemblers;
import dk.skrypalle.jasm.assembler.err.ErrorListener;
import dk.skrypalle.jasm.it.util.TestUtil;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static dk.skrypalle.jasm.it.assembler.err.Arranger.assembling;
import static dk.skrypalle.jasm.it.util.TestAssertions.assertThat;

public class JasmErrorIntegrationTest {

    @DataProvider
    public static Object[][] provideSourceFileAndExpectedErrors() {
        //checkstyle.off: LineLength - exceeded due to readability of arranger DSL
        return new Object[][]{
                assembling("empty_file")
                        .shouldEmitErrorAt(19, 1).withMessagePattern("input mismatch.*<EOF>")
                        .andNothingElse(),
                assembling("invalid_bytecode_version_major")
                        .shouldEmitErrorAt(19, 11).withMessagePattern("illegal major bytecode version.*23")
                        .andNothingElse(),
                assembling("invalid_bytecode_version_minor")
                        .shouldEmitErrorAt(19, 11).withMessagePattern("illegal minor bytecode version.*12")
                        .andNothingElse(),
                assembling("invalid_class_type")
                        .shouldEmitErrorAt(24, 40).withMessagePattern("invalid class type.*java/lang/String")
                        .andAt(28, 41).withMessagePattern("invalid class type.*java/lang/String")
                        .andNothingElse(),
                assembling("invalid_primitive_type")
                        .shouldEmitErrorAt(24, 44).withMessagePattern("unknown primitive type.*Y")
                        .andAt(28, 41).withMessagePattern("unknown primitive type.*Y")
                        .andNothingElse(),
                assembling("missing_bytecode")
                        .shouldEmitErrorAt(19, 1).withMessagePattern("input mismatch.*source")
                        .andAt(20, 1).withMessagePattern("input mismatch.*class")
                        .andAt(21, 1).withMessagePattern("input mismatch.*super")
                        .andAt(22, 1).withMessagePattern("input mismatch.*<EOF>")
                        .andNothingElse(),
                assembling("missing_class")
                        .shouldEmitErrorAt(21, 1).withMessagePattern("input mismatch.*<EOF>.*expected.*class")
                        .andNothingElse(),
                assembling("unknown_symbol")
                        .shouldEmitErrorAt(19, 1).withMessagePattern("unknown symbol.*\\{")
                        .amongOthers()
        };
        //checkstyle.on: LineLength
    }

    @Test(dataProvider = "provideSourceFileAndExpectedErrors")
    public void expectErrors(String testName, boolean exclusive, ExpectedError... expectedErrors) {
        // arrange
        var errorListener = new JasmRecordingErrorListener();
        var asm = getAssembler(testName, errorListener);

        // act
        var assembly = asm.assemble();

        // assert
        assertThat(assembly)
                .isNull();

        if (exclusive) {
            assertThat(errorListener)
                    .hasExactlyErrorsInAnyOrder(expectedErrors);
        } else {
            assertThat(errorListener)
                    .containsErrors(expectedErrors);
        }
    }

    private static Assembler getAssembler(String testName, ErrorListener errorListener) {
        var resourceName = String.format("/dk/skrypalle/jasm/it/assembler/err/%s.jasm", testName);
        return Assemblers.fromFile(
                TestUtil.getResourcePath(resourceName),
                errorListener,
                true
        );
    }

}
