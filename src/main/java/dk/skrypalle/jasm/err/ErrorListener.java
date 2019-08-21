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
package dk.skrypalle.jasm.err;

import org.antlr.v4.runtime.Token;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ErrorListener {

    private final AtomicInteger numberOfErrors;

    protected ErrorListener() {
        numberOfErrors = new AtomicInteger();
    }

    //region general errors

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

    public void emitUnexpectedErrorWhileAssembling(String sourceName) {
        failGeneral("Unexpected error while assembling '%s'", sourceName);
    }

    public void emitUnexpectedErrorWhileAssembling(String sourceName, Throwable error) {
        failGeneral(error, "Unexpected error while assembling '%s'", sourceName);
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

    public void emitUnknownSymbol(String sourceName, int line, int column, String symbol) {
        fail(sourceName, line, column, "unknown symbol '%s'", symbol);
    }

    public void emitInputMismatch(Token position, String expectedSymbols) {
        fail(position, "input mismatch at '%s' - expected %s", getTokenErrorDisplay(position), expectedSymbols);
    }

    public void emitInvalidClassType(Token position, String className) {
        fail(position, "invalid class type '%s'", className);
    }

    public void emitInvalidPrimitiveType(Token position, int offset) {
        int type = position.getText().charAt(offset);
        int column = position.getCharPositionInLine() + 1 + offset;
        fail(position.getTokenSource().getSourceName(), position.getLine(), column, "unknown primitive type '%c'", type);
    }

    public void emitIllegalMinorBytecodeVersion(Token position, int minAllowed, int maxAllowed) {
        fail(position, "illegal minor bytecode version '%s'- expect interval [%d; %d]", position.getText(), minAllowed, maxAllowed);
    }

    public void emitIllegalMajorBytecodeVersion(Token position, int minAllowed, int maxAllowed) {
        fail(position, "illegal major bytecode version '%s'- expect interval [%d; %d]", position.getText(), minAllowed, maxAllowed);
    }

    private void fail(Token position, String format, Object... args) {
        fail(position.getTokenSource().getSourceName(), position.getLine(), position.getCharPositionInLine() + 1, format, args);
    }

    private void fail(String sourceName, int line, int column, String format, Object... args) {
        var message = String.format(format, args);
        try {
            emitSourceError(sourceName, line, column, message);
        } finally {
            numberOfErrors.incrementAndGet();
        }
    }

    private void failGeneral(String format, Object... args) {
        emitGeneralError(String.format(format, args));
    }

    private void failGeneral(Throwable error, String format, Object... args) {
        emitGeneralError(String.format(format, args), error);
    }

    private String getTokenErrorDisplay(Token t) {
        if (t == null) {
            return "<no token>";
        }
        String s = getSymbolText(t);
        if (s == null) {
            if (getSymbolType(t) == Token.EOF) {
                s = "<EOF>";
            } else {
                s = "<" + getSymbolType(t) + ">";
            }
        }
        return escapeWSAndQuote(s);
    }

    private String getSymbolText(Token symbol) {
        return symbol.getText();
    }

    private int getSymbolType(Token symbol) {
        return symbol.getType();
    }

    private String escapeWSAndQuote(String s) {
        s = s.replace("\n", "\\n");
        s = s.replace("\r", "\\r");
        s = s.replace("\t", "\\t");
        return "'" + s + "'";
    }

    public final int getNumberOfErrors() {
        return numberOfErrors.get();
    }

    protected abstract void emitSourceError(String sourceName, int line, int column, String message);
    protected abstract void emitGeneralError(String message);
    protected abstract void emitGeneralError(String message, Throwable error);

}
