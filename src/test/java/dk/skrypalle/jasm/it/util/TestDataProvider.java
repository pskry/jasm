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

import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public final class TestDataProvider {

    private static final String JDK_VERSION = "se12";

    @DataProvider(parallel = true)
    public static Object[][] provideJdkClassNames() throws Exception {
        var path = TestUtil.getResourcePath("/dk/skrypalle/jasm/it/jdk/" + JDK_VERSION + ".txt");
        var lines = Files.readString(path).split("\\n");
        return Stream.of(lines)
                .map(className -> new Object[]{className})
                .toArray(Object[][]::new);
    }

    @DataProvider(parallel = true)
    public static Object[][] provideJasmSourceFiles() throws IOException {
        return ResourceLocator.locateResources(".*assembler[/|\\\\][^/|\\\\]+\\.jasm$").stream()
                .map(path -> new Object[]{Paths.get(path)})
                .toArray(Object[][]::new);
    }

    private TestDataProvider() { /* static utility */ }

}
