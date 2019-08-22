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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DisassemblerFromFile extends BaseDisassembler {

    private final Path inputFile;

    DisassemblerFromFile(Path inputFile, ErrorListener errorListener, boolean verbose) {
        super(errorListener, verbose);

        this.inputFile = inputFile;
    }

    @Override
    protected DisassemblerInput getInput() {
        if (!Files.exists(inputFile)) {
            errorListener.emitInputFileDoesNotExist(inputFile);
            return null;
        }
        if (Files.isDirectory(inputFile)) {
            errorListener.emitInputFileIsDirectory(inputFile);
            return null;
        }
        if (!Files.isReadable(inputFile)) {
            errorListener.emitInputFileIsNotReadable(inputFile);
            return null;
        }

        try {

            var binaryClassFile = Files.readAllBytes(inputFile);
            return new DisassemblerInput(new ClassReader(binaryClassFile), inputFile.toString());

        } catch (IOException e) {
            if (verbose) {
                errorListener.emitUnexpectedErrorWhileReadingInputFile(inputFile, e);
            } else {
                errorListener.emitUnexpectedErrorWhileReadingInputFile(inputFile);
            }
            return null;
        }
    }

}
