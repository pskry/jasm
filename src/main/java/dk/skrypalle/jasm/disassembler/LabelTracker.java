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

import dk.skrypalle.jasm.LabelUtils;
import dk.skrypalle.jasm.Promise;
import org.objectweb.asm.Label;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class LabelTracker {

    private final Map<Integer, String> labelDefs;
    private final Map<Integer, String> labelNames;

    LabelTracker() {
        labelDefs = new HashMap<>();
        labelNames = new HashMap<>();
    }

    Promise<String> useLabel(Label label) {
        var id = System.identityHashCode(label);
        return () -> labelNames.get(id);
    }

    String defineLabel(Label label) {
        var id = System.identityHashCode(label);
        var labelName = labelDefs.get(id);
        if (labelName == null) {
            var num = labelDefs.size();
            labelName = String.format(
                    "label_%d:%s",
                    num,
                    flagsToString(label)
            );
            labelDefs.put(id, labelName);
            labelNames.put(id, String.format("label_%d", num));
        }

        return labelName;
    }

    private String flagsToString(Label label) {
        if (!LabelUtils.isLabelUtilAvailable()) {
            return "";
        }

        var flagNames = new ArrayList<String>();
        if (LabelUtils.isDebug(label)) {
            flagNames.add("debug");
        }
        if (LabelUtils.isJumpTarget(label)) {
            flagNames.add("jump_target");
        }
        if (LabelUtils.isResolved(label)) {
            flagNames.add("resolved");
        }
        if (LabelUtils.isReachable(label)) {
            flagNames.add("reachable");
        }

        if (flagNames.isEmpty()) {
            return "";
        }

        var flags = String.join(", ", flagNames);
        return String.format(" # flags: %s (0x%04x)", flags, LabelUtils.getFlags(label));
    }

}
