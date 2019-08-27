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
package dk.skrypalle.jasm.it.assembler;

import dk.skrypalle.jasm.assembler.Assembler;
import dk.skrypalle.jasm.assembler.Assemblers;
import dk.skrypalle.jasm.it.util.TestUtil;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static dk.skrypalle.jasm.it.assembler.Arranger.assembling;
import static dk.skrypalle.jasm.it.util.TestAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class JasmIntegrationTest {

    @DataProvider
    public static Object[][] provideSourceFileAndExpectedResult() {
        //checkstyle.off: LineLength - exceeded due to readability of arranger DSL
        return new Object[][]{
                assembling("abstract_method").shouldPrint("abstract"),
                assembling("constructor").shouldPrint("\"Hello\""),
                assembling("double_for_loop").shouldPrint("0\n1\n2\n3\n4\n5\n6\n7\n8\n"),
                assembling("doubles").shouldPrint("0.0\n1.0\n23.1\n2310.0\n0.231\n"),
                assembling("floats").shouldPrint("0.0\n1.0\n2.0\n23.1\n2310.0\n0.231\n"),
                assembling("for_loop").shouldPrint("0\n1\n2\n3\n4\n"),
                assembling("if_int_eq").shouldPrint("0==0\n1==1\n"),
                assembling("if_int_ge").shouldPrint("0>=0\n0>=-1\n1>=0\n1>=1\n1>=-1\n"),
                assembling("if_int_gt").shouldPrint("0>-1\n1>0\n1>-1\n"),
                assembling("if_int_le").shouldPrint("0<=0\n0<=1\n1<=1\n"),
                assembling("if_int_lt").shouldPrint("0<1\n"),
                assembling("integers").shouldPrint("0\n1\n2\n3\n4\n5\n-1\n-128\n127\n-32768\n32767\n-2147483648\n2147483647\n"),
                assembling("local_vars").shouldPrint("2310"),
                assembling("longs").shouldPrint("0\n1\n-9223372036854775808\n9223372036854775807\n"),
                assembling("lookup_switch_int").shouldPrint("0\n100\n200\n"),
                assembling("lookup_switch_string").shouldPrint("a\nb\n"),
                assembling("math_max").shouldPrint("2"),
                assembling("mixed_descriptor").shouldPrint("12OneTwo"),
                assembling("no_newline_at_end_of_file").shouldPrint("WorksWithoutNewlineAtTheEndOfTheFile"),
                assembling("overwrite_a0").withArgs("arg0", "arg1").shouldPrint("[arg0, arg1]\nclass [Ljava.lang.String;\noverwrite_a0\nclass java.lang.String\n"),
                assembling("overwrite_i0").withArgs("arg0", "arg1").shouldPrint("[arg0, arg1]\nclass [Ljava.lang.String;\n2310\n2310\nclass java.lang.Integer\n"),
                assembling("print_main_args").withArgs("a", "b", "c").shouldPrint("[a, b, c]"),
                assembling("static_field").shouldPrint("2310"),
                assembling("while_loop").shouldPrint("0\n1\n2\n3\n4\n"),
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
        var resourceName = String.format("/dk/skrypalle/jasm/it/assembler/%s.jasm", testName);
        return Assemblers.fromFile(
                TestUtil.getResourcePath(resourceName),
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
