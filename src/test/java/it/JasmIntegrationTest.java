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
package it;

import dk.skrypalle.jasm.ClassFile;
import dk.skrypalle.jasm.Assemblers;
import org.objectweb.asm.ClassReader;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class JasmIntegrationTest {

    @DataProvider
    public static Object[][] provideSourceFileAndExpectedResult() {
        return new Object[][]{
                {"/constructor.jasm", null, "\"Hello\""},
                {"/integers.jasm", null, "0\n1\n2\n3\n4\n5\n-1\n-128\n127\n-32768\n32767\n-2147483648\n2147483647\n"},
                {"/local_vars.jasm", null, "2310"},
                {"/math_max.jasm", null, "2"},
                {"/no_newline_at_end_of_file.jasm", null, "WorksWithoutNewlineAtTheEndOfTheFile"},
                {"/print_main_args.jasm", new String[]{"a", "b", "c", "d"}, "[a, b, c, d]"},
        };
    }

    @Test(dataProvider = "provideSourceFileAndExpectedResult")
    public void assemble(String resourceName, String[] mainArgs, String expected) throws Exception {
        // arrange
        var asm = Assemblers.fromFile(
                getResourcePath(resourceName),
                new AssertingErrorListener(),
                true
        );

        // act
        var classFile = asm.assemble();

        // assert
        assertThat(classFile)
                .as("assembled class-file not expected to be null")
                .isNotNull();

        var clazz = defineClass(classFile);
        var stdout = invokeMainAndCaptureStdOut(clazz, mainArgs);
        assertThat(stdout)
                .isEqualToNormalizingNewlines(expected);
    }

    private static Path getResourcePath(String resourceName) {
        try {
            var uri = JasmIntegrationTest.class.getResource(resourceName).toURI();
            return Paths.get(uri);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to locate resource file " + resourceName, e);
        }
    }

    private static Class<?> defineClass(ClassFile classFile) {
        var reader = new ClassReader(classFile.getBinaryData());
        assertThat(classFile.getJvmClassName())
                .as("Expecting className integrity.")
                .isEqualTo(reader.getClassName());
        return new DynamicClassLoader().defineClass(classFile);
    }

    private static String invokeMainAndCaptureStdOut(Class<?> clazz, String[] mainArgs) throws Exception {
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
