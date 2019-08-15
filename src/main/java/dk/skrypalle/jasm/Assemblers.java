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
package dk.skrypalle.jasm;

import dk.skrypalle.jasm.err.ErrorListener;

import java.io.File;
import java.nio.file.Path;

public final class Assemblers {

    public static Assembler fromString(String jasmSourceCode, String sourceName, ErrorListener errorListener, boolean verbose) {
        return new AssemblerFromString(jasmSourceCode, sourceName, errorListener, verbose);
    }

    public static Assembler fromString(String jasmSourceCode, ErrorListener errorListener, boolean verbose) {
        return new AssemblerFromString(jasmSourceCode, "<nil>", errorListener, verbose);
    }

    public static Assembler fromFile(Path jasmSourceFile, ErrorListener errorListener, boolean verbose) {
        return new AssemblerFromFile(jasmSourceFile, errorListener, verbose);
    }

    public static Assembler fromFile(File jasmSourceFile, ErrorListener errorListener, boolean verbose) {
        return new AssemblerFromFile(jasmSourceFile.toPath(), errorListener, verbose);
    }

}
