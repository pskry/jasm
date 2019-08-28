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
package dk.skrypalle.jasm.it.util;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class TestDataProvider {

    private static final int[] SUPPORTED_JDK_VERSIONS = {12};
    private static final Pattern JDK_VERSION_PATTERN = Pattern.compile("(se|se_)?(?<int>[0-9]+)");

    @DataProvider(parallel = true)
    public static Object[][] provideJdkClassNames() throws Exception {
        var path = TestUtil.getResourcePath(String.format(
                "/dk/skrypalle/jasm/it/jdk/%s.txt",
                parseJdkVersion()
        ));
        var lines = Files.readString(path).split("\\n");
        return Stream.of(lines)
                .map(StringUtils::trimToNull)
                .filter(Objects::nonNull)
                .map(className -> new Object[]{className})
                .toArray(Object[][]::new);
    }

    @DataProvider(parallel = true)
    public static Object[][] provideWorkingJdkClassNames() throws Exception {
        var path = TestUtil.getResourcePath(String.format(
                "/dk/skrypalle/jasm/it/jdk/%s_working.txt",
                parseJdkVersion()
        ));
        var lines = Files.readString(path).split("\\n");
        return Stream.of(lines)
                .map(StringUtils::trimToNull)
                .filter(Objects::nonNull)
                .map(className -> new Object[]{className})
                .toArray(Object[][]::new);
    }

    @DataProvider(parallel = true)
    public static Object[][] provideAllJasmSourceFiles() throws IOException {
        return ResourceLocator.locateResources(".*\\.jasm$").stream()
                .map(path -> new Object[]{Paths.get(path)})
                .toArray(Object[][]::new);
    }

    @DataProvider(parallel = true)
    public static Object[][] provideJasmSourceFiles() throws IOException {
        return ResourceLocator.locateResources(".*assembler[/|\\\\][^/|\\\\]+\\.jasm$").stream()
                .map(path -> new Object[]{Paths.get(path)})
                .toArray(Object[][]::new);
    }

    private static String parseJdkVersion() {
        var defaultValue = String.format("se%d", SUPPORTED_JDK_VERSIONS[0]);
        var propertyValue = System.getProperty("testJdkVersion", defaultValue);

        var matcher = JDK_VERSION_PATTERN.matcher(propertyValue);
        if (!matcher.matches()) {
            throw unsupportedJdkVersion(propertyValue);
        }

        try {

            var intVal = Integer.decode(matcher.group("int"));
            for (int supportedJdkVersion : SUPPORTED_JDK_VERSIONS) {
                if (supportedJdkVersion == intVal) {
                    return String.format("se%d", intVal);
                }
            }

            throw unsupportedJdkVersion(propertyValue);
        } catch (Throwable t) {
            throw unsupportedJdkVersion(propertyValue);
        }
    }

    private static IllegalArgumentException unsupportedJdkVersion(String propertyValue) {
        return new IllegalArgumentException(String.format(
                "jdk version %s is not supported",
                propertyValue
        ));
    }

    private TestDataProvider() { /* static utility */ }

}
