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
package dk.skrypalle.jasm.cli;

import dk.skrypalle.jasm.disassembler.Disassemblers;
import dk.skrypalle.jasm.disassembler.err.ConsoleErrorListener;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Option;

@Command(
        name = "jdsm",
        mixinStandardHelpOptions = true,
        version = "jdsm 0.1",
        description = "Disassembles JVM .class files to jASM source files.")
public class Jdsm implements Callable<Integer> {

    private static final Path PWD = Paths.get(".").toAbsolutePath();

    @Parameters(index = "0", description = "The file to assemble.")
    private Path file;

    @Option(names = {"-d", "--directory"}, description = "Output base directory. Default is pwd.")
    private Path workingDirectory;

    @Option(names = {"-v", "--verbose"}, description = "Enable verbose error and logging output.")
    private boolean verbose;

    @Override
    public Integer call() {
        var errorListener = new ConsoleErrorListener();

        if (workingDirectory != null) {
            if (Files.exists(workingDirectory) && !Files.isDirectory(workingDirectory)) {
                errorListener.emitWorkingDirectoryMustPointToDirectory(workingDirectory);
                return 1;
            }
        }

        var dsm = Disassemblers.fromFile(PWD.relativize(file), errorListener, verbose);
        var jasmSourceCode = dsm.disassemble();

        if (jasmSourceCode == null) {
            // and error must have occurred and it has been displayed via the error-listener
            return 1;
        }

        var outDir = workingDirectory == null
                ? PWD
                : workingDirectory.toAbsolutePath();

        var outFile = outDir.resolve(jasmSourceCode.getJvmClassName() + ".jasm");
        var dirToCreate = outFile.getParent();
        assert dirToCreate != null;

        try {
            Files.createDirectories(dirToCreate);
        } catch (IOException e) {
            if (verbose) {
                errorListener.emitOutputDirectoryCreationFailure(dirToCreate, e);
            } else {
                errorListener.emitOutputDirectoryCreationFailure(dirToCreate);
            }
            return 1;
        }

        try {
            Files.writeString(outFile, jasmSourceCode.getJasmSourceCode(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            if (verbose) {
                errorListener.emitUnexpectedErrorWhileWritingOutputFile(outFile, e);
            } else {
                errorListener.emitUnexpectedErrorWhileWritingOutputFile(outFile);
            }
            return 1;
        }

        return 0;
    }

    public static void main(String[] args) {
        var exitCode = new CommandLine(new Jdsm()).execute(args);
        System.exit(exitCode);
    }

}
