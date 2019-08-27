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

import dk.skrypalle.jasm.Promise;
import org.objectweb.asm.Label;

import java.util.ArrayList;
import java.util.List;

class LabelTracker {

    private final List<Label> definedLabels;
    private final List<Label> usedLabels;

    LabelTracker() {
        definedLabels = new ArrayList<>();
        usedLabels = new ArrayList<>();
    }

    Promise<String> useLabel(Label label) {
        if (!usedLabels.contains(label)) {
            usedLabels.add(label);
        }

        return () -> {
            int labelNumber = getLabelNumber(label);
            if (labelNumber == -1) {
                throw new IllegalStateException("use of undefined label");
            }
            return refLabel(labelNumber);
        };
    }

    Promise<String> defineLabel(Label label) {
        if (!definedLabels.contains(label)) {
            definedLabels.add(label);
        }

        return () -> {
            int labelNumber = getLabelNumber(label);
            return labelNumber >= 0
                    ? defLabel(labelNumber)
                    : null;
        };
    }

    private int getLabelNumber(Label label) {
        var i = 0;
        for (Label definedLabel : definedLabels) {
            if (usedLabels.contains(definedLabel)) {
                if (definedLabel == label) {
                    return i;
                }
                i++;
            }
        }
        return -1;
    }

    private static String refLabel(int number) {
        return String.format("label_%d", number);
    }

    private static String defLabel(int number) {
        return String.format("label_%d:", number);
    }

}
