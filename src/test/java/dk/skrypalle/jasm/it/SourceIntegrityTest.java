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

import dk.skrypalle.jasm.it.util.TestDataProvider;
import org.apache.commons.text.CaseUtils;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class SourceIntegrityTest {

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "provideAllJasmSourceFiles")
    public void inspectSource(Path sourcePath) throws Exception {
        var jasmSource = Files.readString(sourcePath);

        assertSourceIntegrity(sourcePath, jasmSource);
        assertClassNameIntegrity(sourcePath, jasmSource);
    }

    private void assertSourceIntegrity(Path sourcePath, String jasmSource) {
        var sourceStart = jasmSource.indexOf(".source");
        if (sourceStart == -1) {
            // no source present, nothing to assert
            return;
        }

        var sourceEnd = jasmSource.indexOf('\n', sourceStart);
        var sourceSpec = jasmSource.substring(sourceStart, sourceEnd);
        var sourceName = sourceSpec.replace(".source", "").replace("\"", "").trim();

        assertThat(sourceName)
                .isEqualTo(sourcePath.getFileName().toString());
    }

    private void assertClassNameIntegrity(Path sourcePath, String jasmSource) {
        var classStart = jasmSource.indexOf(".class");
        if (classStart == -1) {
            // no class present, nothing to assert
            return;
        }

        var classEnd = jasmSource.indexOf('\n', classStart);
        var classSpec = jasmSource.substring(classStart, classEnd);
        var lastSpace = classSpec.lastIndexOf(' ');
        var className = classSpec.substring(lastSpace).trim();

        var classTokens = tokenizeClassName(className);
        var pathTokens = tokenizeSourcePath(sourcePath, classTokens.length);

        // assert
        assertThat(classTokens)
                .isEqualTo(pathTokens);
    }

    private static String[] tokenizeClassName(String className) {
        return className.split("/");
    }

    private static String[] tokenizeSourcePath(Path sourcePath, int numTokens) {
        var pathTokens = sourcePath.toAbsolutePath()
                .toString()
                .replace(".jasm", "")
                .split("[\\\\/]");

        // format last token (should be class-name in snake-case) to CamelCase.
        pathTokens[pathTokens.length - 1] = CaseUtils.toCamelCase(
                pathTokens[pathTokens.length - 1],
                true,
                '_'
        );

        var result = new String[numTokens];
        var j = pathTokens.length - 1;
        for (int i = numTokens - 1; i >= 0; i--) {
            if (j == 0) {
                result[i] = null;
            } else {
                result[i] = pathTokens[j--];
            }
        }

        return result;
    }

}
