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
package dk.skrypalle.it.jasm;

class Arranger {

    static Stage1 assembling(String testName) {
        return new Stage1(testName);
    }

    static class Stage1 {
        private final String testName;

        private Stage1(String testName) {
            this.testName = testName;
        }

        Object[] shouldPrint(String expectedStdOut) {
            return withArgs().shouldPrint(expectedStdOut);
        }

        Stage2 withArgs(String... args) {
            return new Stage2(this, args);
        }
    }

    static class Stage2 {
        private final Stage1 stage1;
        private final String[] args;

        private Stage2(Stage1 stage1, String[] args) {
            this.stage1 = stage1;
            this.args = args;
        }

        Object[] shouldPrint(String expectedStdOut) {
            return new Object[]{stage1.testName, args, expectedStdOut};
        }
    }

}
