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
package dk.skrypalle.jasm.assembler;

import dk.skrypalle.jasm.assembler.err.ErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class AssemblerFromFile extends BaseAssembler {

    private final Path jasmSourceFile;

    AssemblerFromFile(Path jasmSourceFile, ErrorListener errorListener, boolean verbose) {
        super(errorListener, verbose);

        this.jasmSourceFile = Objects.requireNonNull(jasmSourceFile, "jasmSourceFile").normalize();
    }

    @Override
    protected CharStream getInput() {
        if (!Files.exists(jasmSourceFile)) {
            errorListener.emitInputFileDoesNotExist(jasmSourceFile);
            return null;
        }
        if (Files.isDirectory(jasmSourceFile)) {
            errorListener.emitInputFileIsDirectory(jasmSourceFile);
            return null;
        }
        if (!Files.isReadable(jasmSourceFile)) {
            errorListener.emitInputFileIsNotReadable(jasmSourceFile);
            return null;
        }

        try {
            var jasmSource = Files.readString(jasmSourceFile, StandardCharsets.UTF_8);

            return CharStreams.fromString(
                    AsmUtils.sanitizeInput(jasmSource),
                    jasmSourceFile.toString()
            );
        } catch (IOException e) {
            if (verbose) {
                errorListener.emitUnexpectedErrorWhileReadingInputFile(jasmSourceFile, e);
            } else {
                errorListener.emitUnexpectedErrorWhileReadingInputFile(jasmSourceFile);
            }
            return null;
        }
    }

}
