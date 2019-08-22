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
package dk.skrypalle.jasm.assembler;

import dk.skrypalle.jasm.assembler.err.ErrorListener;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.antlr.v4.runtime.CommonTokenStream;

@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_SUPERCLASS")
class JasmParser extends dk.skrypalle.jasm.generated.JasmParser {

    JasmParser(JasmLexer input, ErrorListener errorListener) {
        super(new CommonTokenStream(input));

        removeErrorListeners();
        addErrorListener(new ParserErrorListenerAdapter(errorListener));
    }

}
