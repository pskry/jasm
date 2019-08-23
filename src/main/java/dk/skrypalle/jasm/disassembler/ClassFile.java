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

class ClassFile {

    private String version;
    private String source;
    private final ClassSpec classSpec;
    private final List<DisassemblerMethodVisitor> methodVisitors;
    private final List<DisassemblerFieldVisitor> fieldVisitors;

    ClassFile() {
        classSpec = new ClassSpec();
        methodVisitors = new ArrayList<>();
        fieldVisitors = new ArrayList<>();
    }

    public void setVersion(int version) {
        this.version = parseVersion(version);
    }

    private String parseVersion(int version) {
        switch (version) {
            case Opcodes.V1_1:
                return "45.0";
            case Opcodes.V1_2:
                return "46.0";
            case Opcodes.V1_3:
                return "47.0";
            case Opcodes.V1_4:
                return "48.0";
            case Opcodes.V1_5:
                return "49.0";
            case Opcodes.V1_6:
                return "50.0";
            case Opcodes.V1_7:
                return "51.0";
            case Opcodes.V1_8:
                return "52.0";
            case Opcodes.V9:
                return "53.0";
            case Opcodes.V10:
                return "54.0";
            case Opcodes.V11:
                return "55.0";
            case Opcodes.V12:
                return "56.0";
            case Opcodes.V13:
                return "57.0";
            default:
                throw new IllegalStateException();
        }
    }

    public void setSource(String source) {
        this.source = source;
    }

    ClassSpec getClassSpec() {
        return classSpec;
    }

    void addMethodVisitor(DisassemblerMethodVisitor methodVisitor) {
        methodVisitors.add(methodVisitor);
    }

    void addFieldVisitor(DisassemblerFieldVisitor fieldVisitor) {
        fieldVisitors.add(fieldVisitor);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(".bytecode ").append(version).append('\n');
        if (source != null) {
            buf.append(".source \"").append(source).append("\"\n");
        }
        buf.append(classSpec.toString());

        for (DisassemblerFieldVisitor fieldVisitor : fieldVisitors) {
            buf.append('\n');
            var fieldSpec = fieldVisitor.getFieldSpec();
            buf.append(fieldSpec.toString());
        }

        for (DisassemblerMethodVisitor methodVisitor : methodVisitors) {
            buf.append('\n');
            var methodSpec = methodVisitor.getMethodSpec();
            buf.append(methodSpec.toString());
        }

        return buf.toString();
    }

}
