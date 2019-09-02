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
package dk.skrypalle.jasm.disassembler;

import dk.skrypalle.jasm.Utils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

class DisassemblerLabelMethodVisitor extends MethodVisitor {

    private final LabelTracker labelTracker;

    DisassemblerLabelMethodVisitor(LabelTracker labelTracker) {
        super(Utils.ASM_VERSION);

        this.labelTracker = labelTracker;
    }

    @Override
    public void visitLabel(Label label) {
        labelTracker.recordLabelDef(label);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        labelTracker.recordLabelRef(label);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        for (Label label : labels) {
            labelTracker.recordLabelRef(label);
        }
        labelTracker.recordLabelRef(dflt);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        for (Label label : labels) {
            labelTracker.recordLabelRef(label);
        }
        labelTracker.recordLabelRef(dflt);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        labelTracker.recordLabelRef(start);
        labelTracker.recordLabelRef(end);
        labelTracker.recordLabelRef(handler);
    }

    @Override
    public void visitLocalVariable(
            String name,
            String descriptor,
            String signature,
            Label start,
            Label end,
            int index) {
        labelTracker.recordLabelRef(start);
        labelTracker.recordLabelRef(end);
    }

}
