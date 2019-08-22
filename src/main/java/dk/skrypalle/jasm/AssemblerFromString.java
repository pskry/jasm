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
package dk.skrypalle.jasm;

import dk.skrypalle.jasm.err.ErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.util.Objects;

import static dk.skrypalle.jasm.AsmUtils.sanitizeInput;

class AssemblerFromString extends BaseAssembler {

    private final String jasmSourceCode;
    private final String sourceName;

    AssemblerFromString(
            String jasmSourceCode,
            String sourceName,
            ErrorListener errorListener,
            boolean verbose) {
        super(errorListener, verbose);

        this.jasmSourceCode = sanitizeInput(
                Objects.requireNonNull(jasmSourceCode, "jasmSourceCode")
        );
        this.sourceName = sourceName;
    }

    @Override
    protected CharStream getInput() {
        return CharStreams.fromString(jasmSourceCode, sourceName);
    }

}
