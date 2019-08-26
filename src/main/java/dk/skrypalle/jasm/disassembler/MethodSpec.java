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
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

import static dk.skrypalle.jasm.disassembler.DisassemblerUtils.isSet;

class MethodSpec {

    private List<String> accessList;
    private String name;
    private String descriptor;
    private List<Promise<String>> instructions;

    void setAccess(int access) {
        accessList = parseMethodAccess(access);
    }

    private List<String> parseMethodAccess(int access) {
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
        if (isSet(access, Opcodes.ACC_SYNCHRONIZED)) {
            accessSpec.add("synchronized");
        }
        if (isSet(access, Opcodes.ACC_BRIDGE)) {
            accessSpec.add("bridge");
        }
        if (isSet(access, Opcodes.ACC_VARARGS)) {
            accessSpec.add("varargs");
        }
        if (isSet(access, Opcodes.ACC_NATIVE)) {
            accessSpec.add("native");
        }
        if (isSet(access, Opcodes.ACC_ABSTRACT)) {
            accessSpec.add("abstract");
        }
        if (isSet(access, Opcodes.ACC_STRICT)) {
            accessSpec.add("strict");
        }
        if (isSet(access, Opcodes.ACC_SYNTHETIC)) {
            accessSpec.add("synthetic");
        }

        return accessSpec.isEmpty()
                ? null
                : accessSpec;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    void addInstruction(Promise<String> instruction) {
        add(instruction);
    }

    void addInstruction(String instruction) {
        add(() -> "  " + instruction);
    }

    void addLabel(String label) {
        add(() -> label);
    }

    private void add(Promise<String> instruction) {
        if (instructions == null) {
            instructions = new ArrayList<>();
        }

        instructions.add(instruction);
    }

    @Override
    public String toString() {
        var buf = new StringBuilder();
        buf.append(".method ");
        if (accessList != null) {
            for (String access : accessList) {
                buf.append(access).append(' ');
            }
        }
        buf.append(name).append(descriptor).append('\n');
        if (instructions != null) {
            for (var instruction : instructions) {
                var resolved = instruction.resolve();
                buf.append(resolved).append("\n");
            }
        }

        buf.append(".end method\n");
        return buf.toString();
    }

}
