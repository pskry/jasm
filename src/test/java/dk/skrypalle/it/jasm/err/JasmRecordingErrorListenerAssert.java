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
package dk.skrypalle.it.jasm.err;

import dk.skrypalle.it.jasm.err.JasmRecordingErrorListener.JasmError;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.AbstractAssert;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class JasmRecordingErrorListenerAssert
        extends AbstractAssert<JasmRecordingErrorListenerAssert, JasmRecordingErrorListener> {

    public JasmRecordingErrorListenerAssert(JasmRecordingErrorListener jasmRecordingErrorListener) {
        super(jasmRecordingErrorListener, JasmRecordingErrorListenerAssert.class);
    }

    JasmRecordingErrorListenerAssert hasExactlyErrorsInAnyOrder(ExpectedError... expectedErrors) {
        var foundErrors = new ArrayList<JasmError>();
        for (ExpectedError expectedError : expectedErrors) {
            var foundError = assertAndGetError(expectedError);
            foundErrors.add(foundError);
        }

        assertThat(actual.getErrors())
                .containsExactlyInAnyOrderElementsOf(foundErrors);

        return myself;
    }

    JasmRecordingErrorListenerAssert containsErrors(ExpectedError... expectedErrors) {
        for (ExpectedError expectedError : expectedErrors) {
            assertAndGetError(expectedError);
        }

        return myself;
    }

    private JasmError assertAndGetError(ExpectedError expectedError) {
        var line = expectedError.getLine();
        var column = expectedError.getColumn();
        var err = actual.getError(line, column);
        if (err == null) {
            var recordedErrors = actual.getRecordedErrors();
            if (StringUtils.isBlank(recordedErrors)) {
                failWithMessage(
                        "Expected error at %d:%d - no errors have been recorded.",
                        line, column
                );
            } else {
                failWithMessage(
                        "Expected error at %d:%d - recorded errors:\n%s",
                        line, column, recordedErrors
                );
            }
            return null;
        }

        var messagePattern = expectedError.getMessagePattern();
        if (messagePattern == null) {
            return err;
        }

        assertThat(err.getMessage())
                .as("Info:\nFound errors:\n" + actual.getRecordedErrors())
                .containsPattern(messagePattern);

        return err;
    }

}
