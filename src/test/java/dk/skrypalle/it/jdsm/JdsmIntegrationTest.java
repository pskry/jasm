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
package dk.skrypalle.it.jdsm;

import dk.skrypalle.it.util.TestUtil;
import dk.skrypalle.jdsm.Disassembler;
import dk.skrypalle.jdsm.Disassemblers;
import org.apache.commons.text.CaseUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

import static dk.skrypalle.it.util.TestAssertions.assertThat;

public class JdsmIntegrationTest {

    @DataProvider
    public static Object[][] provideTestFileNames() {
        return asObjectArray(
                "constructor",
                "integers",
                "local_vars",
                "math_max",
                "print_main_args"
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
                "dk.skrypalle.it.jdsm." + camelCase,
                new JdsmAssertingErrorListener(),
                true
        );
    }

    private String loadJasmFile(String testName) throws IOException {
        var path = TestUtil.getResourcePath("/dk/skrypalle/it/jdsm/" + testName + ".jasm");
        return Files.readString(path, StandardCharsets.UTF_8);
    }

}
