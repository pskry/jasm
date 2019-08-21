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

class ExpectedError {

    private final int line;
    private final int column;
    private String messagePattern;

    ExpectedError(int line, int column, String messagePattern) {
        this.line = line;
        this.column = column;
        this.messagePattern = messagePattern;
    }

    int getLine() {
        return line;
    }

    int getColumn() {
        return column;
    }

    String getMessagePattern() {
        return messagePattern;
    }

    void setMessagePattern(String messagePattern) {
        this.messagePattern = messagePattern;
    }

    boolean isEqualPosition(int line, int column) {
        return this.line == line && this.column == column;
    }

    @Override
    public String toString() {
        return String.format("%d:%d: %s", line, column, messagePattern);
    }

}
