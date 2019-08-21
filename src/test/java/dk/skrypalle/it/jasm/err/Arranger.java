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

import java.util.ArrayList;
import java.util.List;

class Arranger {

    static Stage1 assembling(String testName) {
        return new Stage1(testName);
    }

    static class Stage1 {
        private final List<ExpectedError> expectedErrors = new ArrayList<>();
        private final String testName;

        private Stage1(String testName) {
            this.testName = testName;
        }

        Stage2 shouldEmitErrorAt(int line, int column) {
            addExpectedError(line, column);
            return new Stage2(this, line, column);
        }

        private void addExpectedError(int line, int column) {
            addExpectedError(line, column, null);
        }

        private void addExpectedError(int line, int column, String messagePattern) {
            for (var error : expectedErrors) {
                if (error.isEqualPosition(line, column)) {
                    // modify existing
                    error.setMessagePattern(messagePattern);
                    return;
                }
            }
            // not found. create new
            expectedErrors.add(new ExpectedError(line, column, messagePattern));
        }
    }

    static class Stage2 {
        private final Stage1 stage1;
        private final int line;
        private final int column;

        private Stage2(Stage1 stage1, int line, int column) {
            this.stage1 = stage1;
            this.line = line;
            this.column = column;
        }

        Stage2 andAt(int line, int column) {
            stage1.addExpectedError(line, column);
            return new Stage2(stage1, line, column);
        }

        Stage3 withMessagePattern(String messagePattern) {
            stage1.addExpectedError(line, column, messagePattern);
            return new Stage3(this, messagePattern);
        }

        Object[] andNothingElse() {
            return build(stage1, true);
        }

        Object[] amongOthers() {
            return build(stage1, false);
        }
    }

    static class Stage3 {
        private final Stage2 stage2;
        private final String messagePattern;

        Stage3(Stage2 stage2, String messagePattern) {
            this.stage2 = stage2;
            this.messagePattern = messagePattern;
        }

        Stage2 andAt(int line, int column) {
            stage2.stage1.addExpectedError(stage2.line, stage2.column, messagePattern);
            return new Stage2(stage2.stage1, line, column);
        }

        Object[] andNothingElse() {
            return build(stage2.stage1, true);
        }

        Object[] amongOthers() {
            return build(stage2.stage1, false);
        }
    }

    private static Object[] build(Stage1 stage1, boolean exclusive) {
        var expectedErrors = stage1.expectedErrors;
        var result = new Object[3];
        result[0] = stage1.testName;
        result[1] = exclusive;
        result[2] = expectedErrors.toArray(ExpectedError[]::new);
        return result;
    }


}
