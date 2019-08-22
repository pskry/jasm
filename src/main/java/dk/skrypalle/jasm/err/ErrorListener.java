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

import dk.skrypalle.jasm.Assembler;
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

    /**
     * Emits error when the provided input file does not exist.
     *
     * @param inputFilePath the provided path to the input file
     */
    public void emitInputFileDoesNotExist(Path inputFilePath) {
        failGeneral("File '%s' does not exit.", inputFilePath);
    }

    /**
     * Emits error when the provided input file to the {@linkplain Assembler} points to a
     * directory.
     *
     * @param inputFilePath the provided path to the input file
     */
    public void emitInputFileIsDirectory(Path inputFilePath) {
        failGeneral("The file to assemble must not point to a directory ('%s').", inputFilePath);
    }

    /**
     * Emits error when the provided input file to the {@linkplain Assembler} is not readable.
     *
     * @param inputFilePath the provided path to the input file
     */
    public void emitInputFileIsNotReadable(Path inputFilePath) {
        failGeneral("File '%s' is not readable.", inputFilePath);
    }

    /**
     * Emits error when the provided working directory of the {@linkplain Assembler} does not point
     * to a directory.
     *
     * @param workingDirectory current working directory
     */
    public void emitWorkingDirectoryMustPointToDirectory(Path workingDirectory) {
        failGeneral("The working directory must point to a directory ('%s')", workingDirectory);
    }

    /**
     * Emits unexpected error while reading assembler input file.
     *
     * @param inputFilePath path to the file where the IO error occurred
     */
    public void emitUnexpectedErrorWhileReadingInputFile(Path inputFilePath) {
        failGeneral("Unexpected I/O error while reading input file '%s'", inputFilePath);
    }

    /**
     * Emits an unexpected error while reading assembler input file.
     *
     * <p>This method is called when the driving {@linkplain Assembler} is run in verbose mode.
     *
     * @param inputFilePath path to the file where the IO error occurred
     * @param error         the underlying IO error
     */
    public void emitUnexpectedErrorWhileReadingInputFile(Path inputFilePath, IOException error) {
        failGeneral(error, "Unexpected I/O error while reading input file '%s'", inputFilePath);
    }

    /**
     * Emits an unexpected error while assembling the current input.
     *
     * @param sourceName the name of the current input source
     */
    public void emitUnexpectedErrorWhileAssembling(String sourceName) {
        failGeneral("Unexpected error while assembling '%s'", sourceName);
    }

    /**
     * Emits an unexpected error while assembling the current input.
     *
     * <p>This method is called when the driving {@linkplain Assembler} is run in verbose mode.
     *
     * @param sourceName the name of the current input source
     * @param error      the underlying unexpected error
     */
    public void emitUnexpectedErrorWhileAssembling(String sourceName, Throwable error) {
        failGeneral(error, "Unexpected error while assembling '%s'", sourceName);
    }

    /**
     * Emits error when the required output directory (and it's parent(s)) could not be created.
     *
     * @param outputDirectory the output directory that failed to be created
     */
    public void emitOutputDirectoryCreationFailure(Path outputDirectory) {
        failGeneral("Failed creating output directory '%s'", outputDirectory);
    }

    /**
     * Emits error when the required output directory (and it's parent(s)) could not be created.
     *
     * <p>This method is called when the driving {@linkplain Assembler} is run in verbose mode.
     *
     * @param outputDirectory the output directory that failed to be created
     * @param error           the underlying IO error
     */
    public void emitOutputDirectoryCreationFailure(Path outputDirectory, IOException error) {
        failGeneral(error, "Failed creating output directory '%s'", outputDirectory);
    }

    /**
     * Emits error when the designated output class file could not be written.
     *
     * @param outputFile the output file that failed to be written to
     */
    public void emitUnexpectedErrorWhileWritingOutputFile(Path outputFile) {
        failGeneral("Failed writing assembled class file to '%s'", outputFile);
    }

    /**
     * Emits error when the designated output class file could not be written.
     *
     * <p>This method is called when the driving {@linkplain Assembler} is run in verbose mode.
     *
     * @param outputFile the output file that failed to be written to
     * @param error      the underlying IO error
     */
    public void emitUnexpectedErrorWhileWritingOutputFile(Path outputFile, IOException error) {
        failGeneral(error, "Failed writing assembled class file to '%s'", outputFile);
    }

    //endregion general errors

    /**
     * Emits error when encountering an unknown symbol during lexical analysis.
     *
     * @param sourceName the name of the current input source
     * @param line       the line in the current source where the error occurred
     * @param column     the column in the current source where the error occurred
     * @param symbol     the offending symbol that could not be recognized
     */
    public void emitUnknownSymbol(String sourceName, int line, int column, String symbol) {
        fail(sourceName, line, column, "unknown symbol '%s'", symbol);
    }

    /**
     * Emits error when encountering an unrecognizable sequence on tokens during parsing.
     *
     * @param offendingToken  the token that caused the error
     * @param expectedSymbols comma-separated list of expected token types
     */
    public void emitInputMismatch(Token offendingToken, String expectedSymbols) {
        fail(
                offendingToken,
                "input mismatch at '%s' - expected %s",
                getTokenErrorDisplay(offendingToken),
                expectedSymbols
        );
    }

    /**
     * Emits error when encountering malformed class descriptors during semantic analysis.
     *
     * @param position  the position where the error occurred
     * @param className the malformed class name
     */
    public void emitInvalidClassType(Token position, String className) {
        fail(position, "invalid class type '%s'", className);
    }

    /**
     * Emits error when encountering an invalid primitive type during semantic analysis.
     *
     * @param offendingToken the token that caused the error
     * @param offset         the offset into offendingToken that caused the error
     */
    public void emitInvalidPrimitiveType(Token offendingToken, int offset) {
        int type = offendingToken.getText().charAt(offset);
        int column = offendingToken.getCharPositionInLine() + 1 + offset;
        fail(
                offendingToken.getTokenSource().getSourceName(),
                offendingToken.getLine(),
                column,
                "unknown primitive type '%c'",
                type
        );
    }

    /**
     * Emits error when encountering an invalid minor bytecode version during semantic analysis.
     *
     * @param offendingToken the token that caused the error
     * @param minAllowed     the minimum (inclusive) allowed minor bytecode version
     * @param maxAllowed     the maximum (inclusive) allowed minor bytecode version
     */
    public void emitIllegalMinorBytecodeVersion(
            Token offendingToken,
            int minAllowed,
            int maxAllowed) {
        fail(
                offendingToken,
                "illegal minor bytecode version '%s'- expect interval [%d; %d]",
                getTokenErrorDisplay(offendingToken),
                minAllowed,
                maxAllowed
        );
    }

    /**
     * Emits error when encountering an invalid major bytecode version during semantic analysis.
     *
     * @param offendingToken the token that caused the error
     * @param minAllowed     the minimum (inclusive) allowed major bytecode version
     * @param maxAllowed     the maximum (inclusive) allowed major bytecode version
     */
    public void emitIllegalMajorBytecodeVersion(
            Token offendingToken,
            int minAllowed,
            int maxAllowed) {
        fail(
                offendingToken,
                "illegal major bytecode version '%s'- expect interval [%d; %d]",
                offendingToken.getText(),
                minAllowed,
                maxAllowed
        );
    }

    private void fail(Token position, String format, Object... args) {
        fail(position.getTokenSource().getSourceName(),
                position.getLine(),
                position.getCharPositionInLine() + 1,
                format,
                args);
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
        return escapeWhitespaceAndQuote(s);
    }

    private String getSymbolText(Token symbol) {
        return symbol.getText();
    }

    private int getSymbolType(Token symbol) {
        return symbol.getType();
    }

    private String escapeWhitespaceAndQuote(String s) {
        s = s.replace("\n", "\\n");
        s = s.replace("\r", "\\r");
        s = s.replace("\t", "\\t");
        return "'" + s + "'";
    }

    public final int getNumberOfErrors() {
        return numberOfErrors.get();
    }

    protected abstract void emitSourceError(
            String sourceName,
            int line,
            int column,
            String message
    );

    protected abstract void emitGeneralError(String message);

    protected abstract void emitGeneralError(String message, Throwable error);

}
