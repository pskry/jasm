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

import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dk.skrypalle.jdsm.JdsmUtils.isSet;

class ClassSpec {

    private List<String> accessList;
    private String name;
    private String superName;
    private List<String> interfaceList;

    void setAccess(int access) {
        accessList = parseClassAccess(access);
    }

    private List<String> parseClassAccess(int access) {
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
        if (isSet(access, Opcodes.ACC_FINAL)) {
            accessSpec.add("final");
        }
        if (isSet(access, Opcodes.ACC_SUPER)) {
            accessSpec.add("super");
        }
        if (isSet(access, Opcodes.ACC_INTERFACE)) {
            accessSpec.add("interface");
        }
        if (isSet(access, Opcodes.ACC_ABSTRACT)) {
            accessSpec.add("abstract");
        }
        if (isSet(access, Opcodes.ACC_SYNTHETIC)) {
            accessSpec.add("synthetic");
        }
        if (isSet(access, Opcodes.ACC_ANNOTATION)) {
            accessSpec.add("annotation");
        }
        if (isSet(access, Opcodes.ACC_ENUM)) {
            accessSpec.add("enum");
        }

        return accessSpec.isEmpty()
                ? null
                : accessSpec;
    }

    void setName(String name) {
        this.name = name;
    }

    void setSuperName(String superName) {
        this.superName = superName;
    }

    void addInterfaces(String[] interfaceNames) {
        if (interfaceNames == null || interfaceNames.length == 0) {
            return;
        }

        if (interfaceList == null) {
            interfaceList = new ArrayList<>();
        }

        interfaceList.addAll(Arrays.asList(interfaceNames));
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(".class ");
        if (accessList != null) {
            for (String access : accessList) {
                buf.append(access).append(' ');
            }
        }
        buf.append(name).append('\n');

        buf.append(".super ").append(superName).append('\n');
        if (interfaceList != null) {
            for (String interfaceName : interfaceList) {
                buf.append(".implements ").append(interfaceName).append('\n');
            }
        }
        return buf.toString();
    }

}
