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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dk.skrypalle.jasm.it.util.TestAssertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class JdkRoundTripIntegrationTest {

    @Test
    public void updateWorkingJdkClassesIfNoRegression() throws Exception {
        var allJdkClasses = Stream.of(TestDataProvider.provideJdkClassNames())
                .map(array -> array[0])
                .map(Object::toString)
                .sorted()
                .collect(Collectors.toList());

        var newWorkingJdkClasses = new ArrayList<String>();
        var errors = new HashMap<String, String>();
        for (String jdkClass : allJdkClasses) {
            try {

                runRoundTrip(jdkClass);
                newWorkingJdkClasses.add(jdkClass);

            } catch (Throwable t) {
                var message = t.getMessage();
                if (message.trim().startsWith("Expecting:")) {
                    message = "RoundTrip discrepancy. Output length: " + message.length();
                }
                errors.put(jdkClass, message);
            }
        }

        var sortedNewWorkingJdkClasses = newWorkingJdkClasses.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        var previouslyWorkingJdkClasses = Stream.of(TestDataProvider.provideWorkingJdkClassNames())
                .map(array -> array[0])
                .map(Object::toString)
                .sorted()
                .collect(Collectors.toList());

        var nowFailing = new ArrayList<String>();
        for (String previouslyWorkingJdkClass : previouslyWorkingJdkClasses) {
            if (!newWorkingJdkClasses.contains(previouslyWorkingJdkClass)) {
                // we have broken something.
                var errorMessage = String.format("%s :: %s",
                        previouslyWorkingJdkClass,
                        errors.get(previouslyWorkingJdkClass)
                );
                nowFailing.add(errorMessage);
            }
        }

        if (!nowFailing.isEmpty()) {
            var brokenClasses = nowFailing.stream()
                    .collect(Collectors.joining("\n    - ", "    - ", ""));
            fail(
                    "%d previously working jdk classes are now broken:\n%s",
                    nowFailing.size(),
                    brokenClasses
            );
        }

        Files.write(
                Paths.get("src/test/resources/dk/skrypalle/jasm/it/jdk/se12_working.txt"),
                sortedNewWorkingJdkClasses
        );
    }

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
            dataProvider = "provideWorkingJdkClassNames",
            dependsOnMethods = "updateWorkingJdkClassesIfNoRegression"
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
