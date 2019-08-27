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
package dk.skrypalle.jasm.it.disassembler;

import dk.skrypalle.jasm.disassembler.Disassembler;
import dk.skrypalle.jasm.disassembler.Disassemblers;
import dk.skrypalle.jasm.it.util.TestUtil;
import org.apache.commons.text.CaseUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

import static dk.skrypalle.jasm.it.util.TestAssertions.assertThat;

public class JdsmIntegrationTest {

    @DataProvider
    public static Object[][] provideTestFileNames() {
        return asObjectArray(
                "abstract_method",
                "constructor",
                "double_for_loop",
                "doubles",
                "floats",
                "for_loop",
                "if_int_eq",
                "if_int_ge",
                "if_int_gt",
                "if_int_le",
                "if_int_lt",
                "integers",
                "local_vars",
                "longs",
                "lookup_switch_int",
                "lookup_switch_string",
                "math_max",
                "mixed_descriptor",
                "overwrite_a0",
                "overwrite_i0",
                "print_main_args",
                "static_field",
                "while_loop"
        );
    }

    @Test(dataProvider = "provideTestFileNames")
    public void disassemble(String testName) throws Exception {
        // arrange
        var dsm = getDisassembler(testName);

        // act
        var disassembly = dsm.disassemble();

        // assert
        var jasmSource = loadJasmFile(testName);
        assertThat(disassembly)
                .isNotNull()
                .hasSourceEquivalentTo(jasmSource);
    }

    private static Object[][] asObjectArray(String... testNames) {
        return Stream.of(testNames)
                .map(testName -> new Object[]{testName})
                .toArray(Object[][]::new);
    }

    private Disassembler getDisassembler(String testName) {
        var camelCase = CaseUtils.toCamelCase(testName, true, '_');
        return Disassemblers.fromClassName(
                "dk.skrypalle.jasm.it.disassembler." + camelCase,
                new JdsmAssertingErrorListener(),
                true
        );
    }

    private String loadJasmFile(String testName) throws IOException {
        var resourceName = String.format("/dk/skrypalle/jasm/it/disassembler/%s.jasm", testName);
        var path = TestUtil.getResourcePath(resourceName);
        return Files.readString(path, StandardCharsets.UTF_8);
    }

}
