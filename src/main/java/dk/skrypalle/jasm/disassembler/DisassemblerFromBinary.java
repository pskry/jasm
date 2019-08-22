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
package dk.skrypalle.jasm.disassembler;

import dk.skrypalle.jasm.disassembler.err.ErrorListener;
import org.objectweb.asm.ClassReader;

class DisassemblerFromBinary extends BaseDisassembler {

    private final byte[] binaryClassFile;
    private final String sourceName;

    DisassemblerFromBinary(
            byte[] binaryClassFile,
            String sourceName,
            ErrorListener errorListener,
            boolean verbose) {
        super(errorListener, verbose);

        this.binaryClassFile = binaryClassFile;
        this.sourceName = sourceName;
    }

    @Override
    protected DisassemblerInput getInput() {
        return new DisassemblerInput(new ClassReader(binaryClassFile), sourceName);
    }

}
