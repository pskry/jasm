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

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

class TypeTokenMap {

    private final Map<String, String> classMap;
    private final Map<String, String> methodMap;

    TypeTokenMap() {
        classMap = new HashMap<>();
        methodMap = new HashMap<>();
    }

    void mapClassSignature(String signature) {
        mapSignature(signature, classMap);
    }

    void mapMethodSignature(String signature) {
        mapSignature(signature, methodMap);
    }

    void nextMethod() {
        methodMap.clear();
    }

    private void mapSignature(String signature, Map<String, String> map) {
        if (StringUtils.isBlank(signature)) {
            return;
        }
        if (signature.charAt(0) != '<') {
            return;
        }

        var genericTypeTokens = seekGenericEnd(signature);
        if (genericTypeTokens == null) {
            return;
        }

        var typeTokens = AssemblerUtils.removeGenericsAndExceptions(genericTypeTokens).split(";");
        for (String typeToken : typeTokens) {
            var pair = typeToken.split(":+");
            map.put(pair[0], pair[1] + ";");
        }
    }

    String getLowerTypeBound(String typeToken) {
        var result = methodMap.get(typeToken);
        if (result != null) {
            return result;
        }

        return classMap.getOrDefault(typeToken, "Ljava/lang/Object;");
    }

    private static String seekGenericEnd(String signature) {
        int level = 0;
        for (int i = 0; i < signature.length(); i++) {
            if (signature.charAt(i) == '<') {
                level++;
            }
            if (signature.charAt(i) == '>') {
                level--;
            }
            if (level == 0) {
                return signature.substring(1, i);
            }
        }

        return null;
    }

}
