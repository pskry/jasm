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

import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

import static dk.skrypalle.jasm.Utils.quoteKeywords;
import static dk.skrypalle.jasm.disassembler.DisassemblerUtils.isSet;

class FieldSpec {

    private List<String> accessList;
    private String name;
    private String descriptor;

    void setAccess(int access) {
        accessList = parseFiledAccess(access);
    }

    private List<String> parseFiledAccess(int access) {
        var accessSpec = new ArrayList<String>();
        if (isSet(access, Opcodes.ACC_PUBLIC)) {
            accessSpec.add("public");
        }
        if (isSet(access, Opcodes.ACC_PRIVATE)) {
            accessSpec.add("private");
        }
        if (isSet(access, Opcodes.ACC_PROTECTED)) {
            accessSpec.add("protected");
        }
        if (isSet(access, Opcodes.ACC_STATIC)) {
            accessSpec.add("static");
        }
        if (isSet(access, Opcodes.ACC_FINAL)) {
            accessSpec.add("final");
        }
        if (isSet(access, Opcodes.ACC_VOLATILE)) {
            accessSpec.add("volatile");
        }
        if (isSet(access, Opcodes.ACC_TRANSIENT)) {
            accessSpec.add("transient");
        }
        if (isSet(access, Opcodes.ACC_SYNTHETIC)) {
            accessSpec.add("synthetic");
        }

        return accessSpec.isEmpty()
                ? null
                : accessSpec;
    }

    void setName(String name) {
        this.name = quoteKeywords(name);
    }

    void setDescriptor(String descriptor) {
        this.descriptor = quoteKeywords(descriptor);
    }

    @Override
    public String toString() {
        var buf = new StringBuilder();
        buf.append(".field ");
        if (accessList != null) {
            for (String access : accessList) {
                buf.append(access).append(' ');
            }
        }
        buf.append(name).append(' ').append(descriptor).append('\n');
        return buf.toString();
    }

}
