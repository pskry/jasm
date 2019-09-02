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

import org.objectweb.asm.Label;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

class LabelTracker {

    private final Deque<Record> records;

    private enum RecordType {
        DEF, REF
    }

    private static class Record {

        private final Label label;
        private final RecordType type;
        private boolean referenced;
        private int slot = -1;
        private Record ref;

        private Record(Label label, RecordType type) {
            this.label = label;
            this.type = type;
        }

        private boolean isDef() {
            return type == RecordType.DEF;
        }

        private boolean isRef() {
            return type == RecordType.REF;
        }
    }

    LabelTracker() {
        records = new ArrayDeque<>();
    }

    void recordLabelDef(Label label) {
        records.add(new Record(label, RecordType.DEF));
    }

    void recordLabelRef(Label label) {
        records.add(new Record(label, RecordType.REF));
    }

    void link() {
        var recordedRefs = records.stream()
                .filter(Record::isRef)
                .collect(Collectors.toList());

        for (var ref : recordedRefs) {
            for (Record record : records) {
                if (record.isDef() && record.label == ref.label) {
                    record.referenced = true;
                    ref.ref = record;
                }
            }
        }

        // assign slots to referenced records
        int slot = 0;
        for (Record record : records) {
            if (record.isDef() && record.referenced) {
                record.slot = slot;
                slot++;
            }
        }
    }

    String refLabel() {
        var record = records.poll();
        if (record == null || !record.isRef()) {
            throw new IllegalStateException();
        }
        return String.format("label_%d", record.ref.slot);
    }

    String defLabel() {
        var record = records.poll();
        if (record == null || !record.isDef()) {
            throw new IllegalStateException();
        }
        if (!record.referenced) {
            return null;
        }
        return String.format("label_%d:", record.slot);
    }

}
