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
package dk.skrypalle.jdsm;

import dk.skrypalle.jdsm.err.ErrorListener;

import java.nio.file.Path;

public final class Disassemblers {

    public static Disassembler fromClassName(String className, ErrorListener errorListener, boolean verbose) {
        return new DisassemblerFromClassName(className, errorListener, verbose);
    }

    public static Disassembler fromBinary(byte[] binaryClassFile, String sourceName, ErrorListener errorListener, boolean verbose) {
        return new DisassemblerFromBinary(binaryClassFile, sourceName, errorListener, verbose);
    }

    public static Disassembler fromFile(Path inputClassFile, ErrorListener errorListener, boolean verbose) {
        return new DisassemblerFromFile(inputClassFile, errorListener, verbose);
    }

    private Disassemblers() { /* static utility */ }

}
