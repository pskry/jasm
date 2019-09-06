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

import java.util.ArrayList;
import java.util.List;

final class AssemblerUtils {

    static String sanitizeInput(String jasmSource) {
        var input = jasmSource.replaceAll("\\r\\n?", "\n");
        return input.endsWith("\n")
                ? input
                : input + '\n'; // ensure blank line at the end of the input
    }

    static String toSignature(String genericSpec, String descriptor) {
        if (genericSpec == null) {
            return null;
        }

        return genericSpec + descriptor;
    }

    static boolean isGenericDescriptor(String descriptor) {
        var angledStart = descriptor.indexOf('<');
        return angledStart != -1 && descriptor.indexOf('>') > angledStart;
    }

    static boolean hasTypeToken(String descriptor) {
        var types = splitPrimitives(descriptor.replace("(", "").replace(")", "").split(";"));
        for (String type : types) {
            var arrayDim = arrayDim(type);
            if (type.charAt(arrayDim) == 'T') {
                return true;
            }
        }
        return false;
    }

    static String toRawMethodDescriptor(String descriptor, TypeTokenMap typeTokenMap) {
        var argsAndReturnValueReturn = removeGenericsAndExceptions(descriptor)
                .replace("(", "")
                .split("\\)");
        var returnValue = argsAndReturnValueReturn[1].replace(";", "");
        var args = splitPrimitives(argsAndReturnValueReturn[0].split(";"));
        var argBuf = new StringBuilder();
        for (String arg : args) {
            argBuf.append(replace(arg, typeTokenMap));
        }

        return String.format(
                "(%s)%s",
                argBuf,
                replace(returnValue, typeTokenMap)
        );
    }

    static String toRawVarDescriptor(String descriptor, TypeTokenMap typeTokenMap) {
        var args = removeGenericsAndExceptions(descriptor).split(";");
        var argsBuf = new StringBuilder();
        for (String arg : args) {
            argsBuf.append(replace(arg, typeTokenMap));
        }

        return argsBuf.toString();
    }

    static String[] toRawExceptions(String descriptor, TypeTokenMap typeTokenMap) {
        var exceptionsStart = descriptor.indexOf('^');
        if (exceptionsStart == -1) {
            return null;
        }

        var exceptionsString = descriptor.substring(exceptionsStart + 1);
        var exceptions = new ArrayList<String>();
        splitPrimitive(exceptionsString, exceptions);

        if (exceptions.isEmpty()) {
            return null;
        }

        return exceptions.stream()
                .map(e -> e.replace(";", ""))
                .map(e -> replace(e, typeTokenMap))
                .map(AssemblerUtils::toRegularClassName)
                .toArray(String[]::new);
    }

    static String removeGenericsAndExceptions(String descriptor) {
        String descriptorWithoutExceptions;
        var exceptionStart = descriptor.indexOf('^');
        if (exceptionStart == -1) {
            descriptorWithoutExceptions = descriptor;
        } else {
            descriptorWithoutExceptions = descriptor.substring(0, exceptionStart);
        }

        int level = 0;
        var buf = new StringBuilder();
        for (int i = 0; i < descriptorWithoutExceptions.length(); i++) {
            char c = descriptorWithoutExceptions.charAt(i);
            if (c == '<') {
                level++;
                continue;
            }
            if (c == '>') {
                level--;
                continue;
            }

            if (level == 0) {
                buf.append(c);
            }
        }

        return buf.toString();
    }

    private static int arrayDim(String type) {
        int dim = 0;
        for (int i = 0; i < type.length(); i++) {
            if (type.charAt(i) == '[') {
                dim++;
            } else {
                break;
            }
        }
        return dim;
    }

    private static String replace(String arg, TypeTokenMap typeTokenMap) {
        var arrayDim = arrayDim(arg);
        if (isTypeToken(arg, arrayDim)) {
            var replace = typeTokenMap.getLowerTypeBound(arg.substring(arrayDim + 1));
            return "[".repeat(Math.max(0, arrayDim)) + replace;
        } else if (!isPrimitiveType(arg) && !isPrimitiveArray(arg)) {
            return arg + ";";
        } else {
            return arg;
        }
    }

    private static String toRegularClassName(String className) {
        if (className.startsWith("L") && className.endsWith(";")) {
            return className.substring(1, className.length() - 1);
        }
        return className;
    }

    private static List<String> splitPrimitives(String[] args) {
        var result = new ArrayList<String>();
        for (String arg : args) {
            splitPrimitive(arg, result);
        }
        return result;
    }

    private static void splitPrimitive(String arg, List<String> out) {
        if (StringUtils.isBlank(arg)) {
            return;
        }

        if (isPrimitiveType(arg)) {
            out.add(arg);
        } else if (isPrimitiveType(arg.charAt(0))) {
            int i = 0;
            while (i < arg.length() && isPrimitiveType(arg.charAt(i))) {
                out.add(arg.charAt(i) + "");
                i++;
            }

            if (arg.length() != i) {
                // this argument was not comprised of pure primitives.
                // either we have a trailing object (split before by ';')
                // or we have to deal with arrays.
                splitPrimitive(arg.substring(i), out);
            }
        } else if (isArrayType(arg)) {
            splitArray(arg, out);
        } else {
            out.add(arg);
        }
    }

    private static void splitArray(String type, List<String> out) {
        if (startsWithPrimitiveArray(type)) {
            splitPrimitiveArray(type, out);
        } else {
            out.add(type);
        }
    }

    private static void splitPrimitiveArray(String type, List<String> out) {
        int start = 0;
        int i = 0;
        while (i < type.length()) {
            if (type.charAt(i) == '[') {
                i++;
                continue;
            }
            int nextStart;
            if (isPrimitiveType(type.charAt(i))) {
                nextStart = i + 1;
            } else {
                nextStart = type.indexOf('[', i);
            }
            if (nextStart == -1) {
                nextStart = type.length();
            }
            out.add(type.substring(start, nextStart));
            i = nextStart;
            start = nextStart;
        }
    }

    private static boolean startsWithPrimitiveArray(String type) {
        return type.matches("^\\[+[BSIJFDZCV].*");
    }

    private static boolean isPrimitiveArray(String type) {
        return type.matches("^\\[+[BSIJFDZCV]");
    }

    private static boolean isTypeToken(String type, int arrayDim) {
        return type.charAt(arrayDim) == 'T';
    }

    private static boolean isPrimitiveType(String type) {
        if (type.length() != 1) {
            return false;
        }
        return isPrimitiveType(type.charAt(0));
    }

    private static boolean isPrimitiveType(char type) {
        switch (type) {
            case 'B':
            case 'S':
            case 'I':
            case 'J':
            case 'F':
            case 'D':
            case 'Z':
            case 'C':
            case 'V':
                return true;
            default:
                return false;
        }
    }

    private static boolean isArrayType(String type) {
        return type.startsWith("[");
    }

    private AssemblerUtils() { /* static utility */ }

}
