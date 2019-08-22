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
package dk.skrypalle.it.jasm;

import dk.skrypalle.it.util.TestUtil;
import dk.skrypalle.jasm.Assembler;
import dk.skrypalle.jasm.Assemblers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static dk.skrypalle.it.jasm.Arranger.assembling;
import static dk.skrypalle.it.util.TestAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class JasmIntegrationTest {

    @DataProvider
    public static Object[][] provideSourceFileAndExpectedResult() {
        //checkstyle.off: LineLength - exceeded due to readability of arranger DSL
        return new Object[][]{
                assembling("abstract_method").shouldPrint("abstract"),
                assembling("constructor").shouldPrint("\"Hello\""),
                assembling("integers").shouldPrint("0\n1\n2\n3\n4\n5\n-1\n-128\n127\n-32768\n32767\n-2147483648\n2147483647\n"),
                assembling("local_vars").shouldPrint("2310"),
                assembling("math_max").shouldPrint("2"),
                assembling("mixed_descriptor").shouldPrint("12OneTwo"),
                assembling("no_newline_at_end_of_file").shouldPrint("WorksWithoutNewlineAtTheEndOfTheFile"),
                assembling("print_main_args").withArgs("a", "b", "c").shouldPrint("[a, b, c]"),
        };
        //checkstyle.on: LineLength
    }

    @Test(dataProvider = "provideSourceFileAndExpectedResult")
    public void assemble(String testName, String[] mainArgs, String expected) throws Exception {
        // arrange
        var asm = getAssembler(testName);

        // act
        var assembly = asm.assemble();

        // assert
        assertThat(assembly)
                .as("assembly not expected to be null")
                .isNotNull();

        var clazz = TestUtil.defineClass(assembly);
        var stdout = invokeMainAndCaptureStdOut(clazz, mainArgs);
        assertThat(stdout)
                .isEqualToNormalizingNewlines(expected);
    }

    private static Assembler getAssembler(String testName) {
        return Assemblers.fromFile(
                TestUtil.getResourcePath("/dk/skrypalle/it/jasm/" + testName + ".jasm"),
                new JasmAssertingErrorListener(),
                true
        );
    }

    private static String invokeMainAndCaptureStdOut(Class<?> clazz, String[] mainArgs)
            throws Exception {
        var oldStdout = System.out;
        try {
            var stdout = new ByteArrayOutputStream();
            System.setOut(new PrintStream(stdout));

            clazz.getDeclaredMethod("main", String[].class)
                    .invoke(null, new Object[]{mainArgs});

            return new String(stdout.toByteArray(), StandardCharsets.UTF_8);
        } finally {
            System.setOut(oldStdout);
        }
    }

}
