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
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

class DisassemblerFromClassName extends BaseDisassembler {

    private final String className;

    DisassemblerFromClassName(String className, ErrorListener errorListener, boolean verbose) {
        super(errorListener, verbose);

        this.className = className;
    }

    @Override
    protected DisassemblerInput getInput() {
        try {
            return new DisassemblerInput(new ClassReader(className), className);
        } catch (IOException e) {
            if (verbose) {
                getPath().ifPresent(
                        path -> errorListener.emitUnexpectedErrorWhileReadingInputFile(path, e)
                );
            } else {
                getPath().ifPresent(errorListener::emitUnexpectedErrorWhileReadingInputFile);
            }
            return null;
        }
    }

    private Optional<Path> getPath() {
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            if (verbose) {
                errorListener.emitClassNotFound(className, e);
            } else {
                errorListener.emitClassNotFound(className);
            }
            return Optional.empty();
        }

        var jvmClassName = className.replace('.', '/') + ".class";
        var url = ClassLoader.getSystemResource(jvmClassName);

        try {

            return Optional.of(Paths.get(url.toURI()));

        } catch (URISyntaxException e) {
            if (verbose) {
                errorListener.emitClassNotFound(className, e);
            } else {
                errorListener.emitClassNotFound(className);
            }
            return Optional.empty();
        }
    }

}
