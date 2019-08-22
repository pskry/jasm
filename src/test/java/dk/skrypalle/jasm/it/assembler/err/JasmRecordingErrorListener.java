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
package dk.skrypalle.jasm.it.assembler.err;

import dk.skrypalle.jasm.assembler.err.ErrorListener;
import org.assertj.core.api.Assertions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class JasmRecordingErrorListener extends ErrorListener {

    static class JasmError {
        private final String sourceName;
        private final int line;
        private final int column;
        private final String message;

        JasmError(String sourceName, int line, int column, String message) {
            this.sourceName = sourceName;
            this.line = line;
            this.column = column;
            this.message = message;
        }

        String getSourceName() {
            return sourceName;
        }

        int getLine() {
            return line;
        }

        int getColumn() {
            return column;
        }

        String getMessage() {
            return message;
        }

        boolean isEqualPosition(int line, int column) {
            return this.line == line && this.column == column;
        }

        @Override
        public String toString() {
            return String.format("%s:%d:%d - %s", sourceName, line, column, message);
        }
    }

    private final List<JasmError> errors = new ArrayList<>();

    @Override
    protected void emitSourceError(String sourceName, int line, int column, String message) {
        errors.add(new JasmError(sourceName, line, column, message));
    }

    @Override
    protected void emitGeneralError(String message) {
        Assertions.fail(message);
    }

    @Override
    protected void emitGeneralError(String message, Throwable error) {
        Assertions.fail(message, error);
    }

    JasmError getError(int line, int column) {
        for (JasmError error : errors) {
            if (error.line == line && error.column == column) {
                return error;
            }
        }
        return null;
    }

    Collection<JasmError> getErrors() {
        return errors;
    }

    String getRecordedErrors() {
        return errors.stream()
                .map(JasmError::toString)
                .collect(Collectors.joining("\n"));
    }

}
