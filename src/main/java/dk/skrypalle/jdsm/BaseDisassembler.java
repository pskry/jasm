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
package dk.skrypalle.jdsm;

import dk.skrypalle.jdsm.err.ErrorListener;

abstract class BaseDisassembler implements Disassembler {

    protected final ErrorListener errorListener;
    protected final boolean verbose;

    protected BaseDisassembler(ErrorListener errorListener, boolean verbose) {
        this.errorListener = errorListener;
        this.verbose = verbose;
    }

    @Override
    public Disassembly disassemble() {
        var input = getInput();
        if (input == null) {
            return null;
        }

        try {

            var visitor = new JdsmClassVisitor();
            var reader = input.getInputReader();
            reader.accept(visitor, 0);

            if (errorListener.getNumberOfErrors() > 0) {
                return null;
            }

            var jasmSourceCode = visitor.dumpJasmSourceCode();
            return new Disassembly(reader.getClassName(), jasmSourceCode);

        } catch (Throwable t) {
            if (verbose) {
                errorListener.emitUnexpectedErrorWhileDisassembling(input.getSourceName(), t);
            } else {
                errorListener.emitUnexpectedErrorWhileDisassembling(input.getSourceName());
            }

            return null;
        }
    }

    protected abstract DisassemblerInput getInput();

}
