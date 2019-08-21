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
package dk.skrypalle.jdsm.err;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ErrorListener {

    private final AtomicInteger numberOfErrors;

    protected ErrorListener() {
        numberOfErrors = new AtomicInteger();
    }

    //region general errors

    public void emitClassNotFound(String className) {
        failGeneral("Class '%s' not found on classpath.", className);
    }

    public void emitClassNotFound(String className, Exception error) {
        failGeneral(error, "Class '%s' not found on classpath.", className);
    }

    public void emitInputFileDoesNotExist(Path inputFilePath) {
        failGeneral("File '%s' does not exit.", inputFilePath);
    }

    public void emitInputFileIsDirectory(Path inputFilePath) {
        failGeneral("The file to assemble must not point to a directory ('%s').", inputFilePath);
    }

    public void emitInputFileIsNotReadable(Path inputFilePath) {
        failGeneral("File '%s' is not readable.", inputFilePath);
    }

    public void emitWorkingDirectoryMustPointToDirectory(Path workingDirectory) {
        failGeneral("The working directory must point to a directory ('%s')", workingDirectory);
    }

    public void emitUnexpectedErrorWhileReadingInputFile(Path inputFilePath) {
        failGeneral("Unexpected I/O error while reading input file '%s'", inputFilePath);
    }

    public void emitUnexpectedErrorWhileReadingInputFile(Path inputFilePath, IOException error) {
        failGeneral(error, "Unexpected I/O error while reading input file '%s'", inputFilePath);
    }

    public void emitUnexpectedErrorWhileDisassembling(String sourceName) {
        failGeneral("Unexpected error while disassembling '%s'", sourceName);
    }

    public void emitUnexpectedErrorWhileDisassembling(String sourceName, Throwable error) {
        failGeneral(error, "Unexpected error while disassembling '%s'", sourceName);
    }

    public void emitOutputDirectoryCreationFailure(Path outputDirectory) {
        failGeneral("Failed creating output directory '%s'", outputDirectory);
    }

    public void emitOutputDirectoryCreationFailure(Path outputDirectory, IOException error) {
        failGeneral(error, "Failed creating output directory '%s'", outputDirectory);
    }

    public void emitUnexpectedErrorWhileWritingOutputFile(Path outputFile) {
        failGeneral("Failed writing assembled class file to '%s'", outputFile);
    }

    public void emitUnexpectedErrorWhileWritingOutputFile(Path outputFile, IOException error) {
        failGeneral(error, "Failed writing assembled class file to '%s'", outputFile);
    }

    //endregion general errors

    private void failGeneral(String format, Object... args) {
        emitGeneralError(String.format(format, args));
    }

    private void failGeneral(Throwable error, String format, Object... args) {
        emitGeneralError(String.format(format, args), error);
    }

    public final int getNumberOfErrors() {
        return numberOfErrors.get();
    }

    protected abstract void emitGeneralError(String message);
    protected abstract void emitGeneralError(String message, Throwable error);

}
