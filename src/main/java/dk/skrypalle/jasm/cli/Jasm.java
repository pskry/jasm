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

import dk.skrypalle.jasm.assembler.Assemblers;
import dk.skrypalle.jasm.assembler.err.ConsoleErrorListener;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Parameters;

@Command(
        name = "jasm",
        mixinStandardHelpOptions = true,
        version = "jasm 0.1",
        description = "Assembles .jasm source files to JVM class files."
)
public class Jasm implements Callable<Integer> {

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

        var asm = Assemblers.fromFile(
                PWD.relativize(file.toAbsolutePath()).normalize(),
                errorListener,
                verbose
        );
        var assembly = asm.assemble();

        if (assembly == null) {
            // an error must have occurred and it has been displayed via the error-listener
            return 1;
        }

        var outDir = workingDirectory == null
                ? PWD
                : workingDirectory.toAbsolutePath();

        var outFile = outDir.resolve(assembly.getJvmClassName() + ".class");
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
            Files.write(outFile, assembly.getBinaryData());
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

    public static void main(String... args) {
        int exitCode = new CommandLine(new Jasm()).execute(args);
        System.exit(exitCode);
    }

}
