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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dk.skrypalle.jasm.it.assembler.Arranger.assembling;
import static dk.skrypalle.jasm.it.util.TestAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class JasmIntegrationTest {

    @DataProvider
    public static Object[][] provideSourceFileAndExpectedResult() {
        //checkstyle.off: LineLength - exceeded due to readability of arranger DSL
        return new Object[][]{
                assembling("abstract_method").shouldPrint("abstract"),
                assembling("array_clone_int").shouldPrint("[23, 10, 19, 82]\n[23, 10, 19, 82]\n[100, 10, 19, 82]\n"),
                assembling("array_loop_int").shouldPrint("4\n3\n2\n1\n0\n"),
                assembling("array_loop_string").shouldPrint("e\nd\nc\nb\na\n"),
                assembling("constructor").shouldPrint("\"Hello\""),
                assembling("double_array_loop_int").shouldPrint("01\n23\n"),
                assembling("double_for_loop").shouldPrint("0\n1\n2\n3\n4\n5\n6\n7\n8\n"),
                assembling("doubles").shouldPrint("0.0\n1.0\n23.1\n2310.0\n0.231\n"),
                assembling("floats").shouldPrint("0.0\n1.0\n2.0\n23.1\n2310.0\n0.231\n"),
                assembling("for_loop").shouldPrint("0\n1\n2\n3\n4\n"),
                assembling("generic_array").shouldPrint("[Hi, there]"),
                assembling("generic_shadowing").shouldPrint("class java.lang.Integer\nHi :)\n"),
                assembling("generic_static_method").shouldPrint("[Hi]"),
                assembling("if_int_eq").shouldPrint("0==0\n1==1\n"),
                assembling("if_int_ge").shouldPrint("0>=0\n0>=-1\n1>=0\n1>=1\n1>=-1\n"),
                assembling("if_int_gt").shouldPrint("0>-1\n1>0\n1>-1\n"),
                assembling("if_int_le").shouldPrint("0<=0\n0<=1\n1<=1\n"),
                assembling("if_int_lt").shouldPrint("0<1\n"),
                assembling("infinity_nan").shouldPrint("float: NaN\nfloat: Infinity\nfloat: -Infinity\ndouble: NaN\ndouble: Infinity\ndouble: -Infinity\n"),
                assembling("integers").shouldPrint("0\n1\n2\n3\n4\n5\n-1\n-128\n127\n-32768\n32767\n-2147483648\n2147483647\n"),
                assembling("lambda_capture_args").withArgs("Hello", "jASM!").shouldPrint("[Hello, jASM!]"),
                assembling("lambda_no_capture").shouldPrint("lambda"),
                assembling("line_directives").shouldPrint("2310"),
                assembling("local_vars").shouldPrint("2310"),
                assembling("longs").shouldPrint("0\n1\n-9223372036854775808\n9223372036854775807\n"),
                assembling("lookup_switch_int").shouldPrint("0\n100\n200\n"),
                assembling("lookup_switch_string").shouldPrint("a\nb\n"),
                assembling("math_max").shouldPrint("2"),
                assembling("mixed_descriptor").shouldPrint("12OneTwo"),
                assembling("nested_try_catch_parse_int").shouldPrint("1\nCannot parse '0xff' to int\n2310\n255\n"),
                assembling("no_newline_at_end_of_file").shouldPrint("WorksWithoutNewlineAtTheEndOfTheFile"),
                assembling("null").shouldPrint("null"),
                assembling("odd_names").shouldPrint("23\n10\ntrue\n23101982\nHi\njavax.net.ssl.SSLException: Reason\n"),
                assembling("overwrite_a0").withArgs("arg0", "arg1").shouldPrint("[arg0, arg1]\nclass [Ljava.lang.String;\noverwrite_a0\nclass java.lang.String\n"),
                assembling("overwrite_i0").withArgs("arg0", "arg1").shouldPrint("[arg0, arg1]\nclass [Ljava.lang.String;\n2310\n2310\nclass java.lang.Integer\n"),
                assembling("print_main_args").withArgs("a", "b", "c").shouldPrint("[a, b, c]"),
                assembling("static_field").shouldPrint("2310"),
                assembling("synchronized_block").shouldPrint("Hi"),
                assembling("table_switch").shouldPrint("zero\none\ntwo\n"),
                assembling("try_catch_parse_int").shouldPrint("1\nCannot parse 'bye' to int\n2310\nCannot parse 'hello' to int\n"),
                assembling("var_directives_same_var").shouldPrint("2310\n1982\n"),
                assembling("var_directives_two_vars").shouldPrint("2310\n1982\n"),
                assembling("while_loop").shouldPrint("0\n1\n2\n3\n4\n"),
        };
        //checkstyle.on: LineLength
    }

    @Test
    public void provideSourceFileAndExpectedResult_areDistinct_and_orderedAlphabetically() {
        // arrange
        // act
        var original = Stream.of(provideSourceFileAndExpectedResult())
                .map(array -> (String) array[0])
                .collect(Collectors.toList());
        var ordered = original.stream()
                .sorted()
                .collect(Collectors.toList());
        var distinct = ordered.stream()
                .distinct()
                .collect(Collectors.toList());

        // assert
        assertThat(original)
                .containsExactlyElementsOf(ordered);
        assertThat(original)
                .containsExactlyElementsOf(distinct);
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
