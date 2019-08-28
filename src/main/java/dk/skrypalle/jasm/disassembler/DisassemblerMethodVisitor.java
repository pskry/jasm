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

import org.apache.commons.text.StringEscapeUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class DisassemblerMethodVisitor extends MethodVisitor {

    private final MethodSpec methodSpec;
    private final LabelTracker labelTracker;

    DisassemblerMethodVisitor() {
        super(Opcodes.ASM7);

        methodSpec = new MethodSpec();
        labelTracker = new LabelTracker();
    }

    MethodSpec getMethodSpec() {
        return methodSpec;
    }

    @Override
    public void visitInsn(int opcode) {
        methodSpec.addInstruction(parseInsn(opcode));
    }

    private String parseInsn(int opcode) {
        switch (opcode) {
            case Opcodes.NOP:
                return "nop";
            case Opcodes.ACONST_NULL:
                return "ldc null";
            case Opcodes.ICONST_M1:
                return "ldc -1";
            case Opcodes.ICONST_0:
                return "ldc 0";
            case Opcodes.ICONST_1:
                return "ldc 1";
            case Opcodes.ICONST_2:
                return "ldc 2";
            case Opcodes.ICONST_3:
                return "ldc 3";
            case Opcodes.ICONST_4:
                return "ldc 4";
            case Opcodes.ICONST_5:
                return "ldc 5";
            case Opcodes.LCONST_0:
                return "ldc 0l";
            case Opcodes.LCONST_1:
                return "ldc 1l";
            case Opcodes.FCONST_0:
                return "ldc 0.0f";
            case Opcodes.FCONST_1:
                return "ldc 1.0f";
            case Opcodes.FCONST_2:
                return "ldc 2.0f";
            case Opcodes.DCONST_0:
                return "ldc 0.0";
            case Opcodes.DCONST_1:
                return "ldc 1.0";
            case Opcodes.IALOAD:
                return "iaload";
            case Opcodes.LALOAD:
                return "laload";
            case Opcodes.FALOAD:
                return "faload";
            case Opcodes.DALOAD:
                return "daload";
            case Opcodes.AALOAD:
                return "aaload";
            case Opcodes.BALOAD:
                return "baload";
            case Opcodes.CALOAD:
                return "caload";
            case Opcodes.SALOAD:
                return "saload";
            case Opcodes.IASTORE:
                return "iastore";
            case Opcodes.LASTORE:
                return "lastore";
            case Opcodes.FASTORE:
                return "fastore";
            case Opcodes.DASTORE:
                return "dastore";
            case Opcodes.AASTORE:
                return "aastore";
            case Opcodes.BASTORE:
                return "bastore";
            case Opcodes.CASTORE:
                return "castore";
            case Opcodes.SASTORE:
                return "sastore";
            case Opcodes.POP:
                return "pop";
            case Opcodes.POP2:
                return "pop2";
            case Opcodes.DUP:
                return "dup";
            case Opcodes.DUP_X1:
                return "dup_x1";
            case Opcodes.DUP_X2:
                return "dup_x2";
            case Opcodes.DUP2:
                return "dup2";
            case Opcodes.DUP2_X1:
                return "dup2_x1";
            case Opcodes.DUP2_X2:
                return "dup2_x2";
            case Opcodes.SWAP:
                return "swap";
            case Opcodes.IADD:
                return "iadd";
            case Opcodes.LADD:
                return "ladd";
            case Opcodes.FADD:
                return "fadd";
            case Opcodes.DADD:
                return "dadd";
            case Opcodes.ISUB:
                return "isub";
            case Opcodes.LSUB:
                return "lsub";
            case Opcodes.FSUB:
                return "fsub";
            case Opcodes.DSUB:
                return "dsub";
            case Opcodes.IMUL:
                return "imul";
            case Opcodes.LMUL:
                return "lmul";
            case Opcodes.FMUL:
                return "fmul";
            case Opcodes.DMUL:
                return "dmul";
            case Opcodes.IDIV:
                return "idiv";
            case Opcodes.LDIV:
                return "ldiv";
            case Opcodes.FDIV:
                return "fdiv";
            case Opcodes.DDIV:
                return "ddiv";
            case Opcodes.IREM:
                return "irem";
            case Opcodes.LREM:
                return "lrem";
            case Opcodes.FREM:
                return "frem";
            case Opcodes.DREM:
                return "drem";
            case Opcodes.INEG:
                return "ineg";
            case Opcodes.LNEG:
                return "lneg";
            case Opcodes.FNEG:
                return "fneg";
            case Opcodes.DNEG:
                return "dneg";
            case Opcodes.ISHL:
                return "ishl";
            case Opcodes.LSHL:
                return "lshl";
            case Opcodes.ISHR:
                return "ishr";
            case Opcodes.LSHR:
                return "lshr";
            case Opcodes.IUSHR:
                return "iushr";
            case Opcodes.LUSHR:
                return "lushr";
            case Opcodes.IAND:
                return "iand";
            case Opcodes.LAND:
                return "land";
            case Opcodes.IOR:
                return "ior";
            case Opcodes.LOR:
                return "lor";
            case Opcodes.IXOR:
                return "ixor";
            case Opcodes.LXOR:
                return "lxor";
            case Opcodes.I2L:
                return "i2l";
            case Opcodes.I2F:
                return "i2f";
            case Opcodes.I2D:
                return "i2d";
            case Opcodes.L2I:
                return "l2i";
            case Opcodes.L2F:
                return "l2f";
            case Opcodes.L2D:
                return "l2d";
            case Opcodes.F2I:
                return "f2i";
            case Opcodes.F2L:
                return "f2l";
            case Opcodes.F2D:
                return "f2d";
            case Opcodes.D2I:
                return "d2i";
            case Opcodes.D2L:
                return "d2l";
            case Opcodes.D2F:
                return "d2f";
            case Opcodes.I2B:
                return "i2b";
            case Opcodes.I2C:
                return "i2c";
            case Opcodes.I2S:
                return "i2s";
            case Opcodes.LCMP:
                return "lcmp";
            case Opcodes.FCMPL:
                return "fcmpl";
            case Opcodes.FCMPG:
                return "fcmpg";
            case Opcodes.DCMPL:
                return "dcmpl";
            case Opcodes.DCMPG:
                return "dcmpg";
            case Opcodes.IRETURN:
                return "ireturn";
            case Opcodes.LRETURN:
                return "lreturn";
            case Opcodes.FRETURN:
                return "freturn";
            case Opcodes.DRETURN:
                return "dreturn";
            case Opcodes.ARETURN:
                return "areturn";
            case Opcodes.RETURN:
                return "return";
            case Opcodes.ARRAYLENGTH:
                return "arraylength";
            case Opcodes.ATHROW:
                return "athrow";
            case Opcodes.MONITORENTER:
                return "monitorenter";
            case Opcodes.MONITOREXIT:
                return "monitorexit";
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        methodSpec.addInstruction(parseVarInsn(opcode) + " " + var);
    }

    private String parseVarInsn(int opcode) {
        switch (opcode) {
            case Opcodes.ILOAD:
                return "iload";
            case Opcodes.LLOAD:
                return "lload";
            case Opcodes.FLOAD:
                return "fload";
            case Opcodes.DLOAD:
                return "dload";
            case Opcodes.ALOAD:
                return "aload";
            case Opcodes.ISTORE:
                return "istore";
            case Opcodes.LSTORE:
                return "lstore";
            case Opcodes.FSTORE:
                return "fstore";
            case Opcodes.DSTORE:
                return "dstore";
            case Opcodes.ASTORE:
                return "astore";
            case Opcodes.RET:
                return "ret";
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        methodSpec.addInstruction(parseIntInsn(opcode, operand));
    }

    private String parseIntInsn(int opcode, int operand) {
        switch (opcode) {
            case Opcodes.BIPUSH:
            case Opcodes.SIPUSH:
                return "ldc " + operand;
            case Opcodes.NEWARRAY:
                return "newarray " + parseArrayType(operand);
            default:
                throw new IllegalStateException();
        }
    }

    private String parseArrayType(int type) {
        switch (type) {
            case Opcodes.T_BOOLEAN:
                return "Z";
            case Opcodes.T_CHAR:
                return "C";
            case Opcodes.T_FLOAT:
                return "F";
            case Opcodes.T_DOUBLE:
                return "D";
            case Opcodes.T_BYTE:
                return "B";
            case Opcodes.T_SHORT:
                return "S";
            case Opcodes.T_INT:
                return "I";
            case Opcodes.T_LONG:
                return "J";
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void visitLdcInsn(Object value) {
        Class<?> valueClass = value.getClass();
        if (valueClass == String.class) {
            value = "\"" + StringEscapeUtils.escapeJava((String) value) + "\"";
        }
        if (valueClass == Float.class) {
            value += "f";
        }
        if (valueClass == Long.class) {
            value += "l";
        }
        methodSpec.addInstruction("ldc " + value);
    }

    @Override
    public void visitMethodInsn(
            int opcode,
            String owner,
            String name,
            String descriptor,
            boolean isInterface) {
        var instruction = String.format(
                "%s %s.%s:%s",
                parseMethodInsn(opcode),
                owner,
                name,
                descriptor
        );
        methodSpec.addInstruction(instruction);
    }

    private String parseMethodInsn(int opcode) {
        switch (opcode) {
            case Opcodes.INVOKEVIRTUAL:
                return "invokevirtual";
            case Opcodes.INVOKESPECIAL:
                return "invokespecial";
            case Opcodes.INVOKESTATIC:
                return "invokestatic";
            case Opcodes.INVOKEINTERFACE:
                return "invokeinterface";
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        var labelName = labelTracker.useLabel(label);
        var jump = parseJumpInsn(opcode);
        methodSpec.addInstruction(() -> "  " + jump + " " + labelName.resolve());
    }

    private String parseJumpInsn(int opcode) {
        switch (opcode) {
            case Opcodes.IFEQ:
                return "ifeq";
            case Opcodes.IFNE:
                return "ifne";
            case Opcodes.IFLT:
                return "iflt";
            case Opcodes.IFGE:
                return "ifge";
            case Opcodes.IFGT:
                return "ifgt";
            case Opcodes.IFLE:
                return "ifle";
            case Opcodes.IF_ICMPEQ:
                return "if_icmpeq";
            case Opcodes.IF_ICMPNE:
                return "if_icmpne";
            case Opcodes.IF_ICMPLT:
                return "if_icmplt";
            case Opcodes.IF_ICMPGE:
                return "if_icmpge";
            case Opcodes.IF_ICMPGT:
                return "if_icmpgt";
            case Opcodes.IF_ICMPLE:
                return "if_icmple";
            case Opcodes.IF_ACMPEQ:
                return "if_acmpeq";
            case Opcodes.IF_ACMPNE:
                return "if_acmpne";
            case Opcodes.GOTO:
                return "goto";
            case Opcodes.JSR:
                return "jsr";
            case Opcodes.IFNULL:
                return "ifnull";
            case Opcodes.IFNONNULL:
                return "ifnonnull";
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void visitLabel(Label label) {
        var labelName = labelTracker.defineLabel(label);
        methodSpec.addLabel(labelName);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        methodSpec.addInstruction(parseTypeInsn(opcode) + " " + type);
    }

    private String parseTypeInsn(int opcode) {
        switch (opcode) {
            case Opcodes.NEW:
                return "new";
            case Opcodes.ANEWARRAY:
                return "anewarray";
            case Opcodes.CHECKCAST:
                return "checkcast";
            case Opcodes.INSTANCEOF:
                return "instanceof";
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        var instruction = String.format(
                "%s %s.%s:%s",
                parseFieldInsn(opcode),
                owner,
                name,
                descriptor
        );
        methodSpec.addInstruction(instruction);
    }

    private String parseFieldInsn(int opcode) {
        switch (opcode) {
            case Opcodes.GETSTATIC:
                return "getstatic";
            case Opcodes.PUTSTATIC:
                return "putstatic";
            case Opcodes.GETFIELD:
                return "getfield";
            case Opcodes.PUTFIELD:
                return "putfield";
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        methodSpec.addInstruction(String.format("iinc %d %d", var, increment));
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        methodSpec.addInstruction("lookupswitch");
        var maxKeyLength = findMaxLookupKeyLength(keys);

        for (int i = 0; i < keys.length; i++) {
            int index = i;
            methodSpec.addInstruction(() -> String.format(
                    "    %s: %s",
                    indentLookupKey(maxKeyLength, keys[index]),
                    labelTracker.useLabel(labels[index]).resolve()
            ));
        }
        methodSpec.addInstruction(() -> String.format(
                "    %s: %s",
                indentDefaultLookupKey(maxKeyLength),
                labelTracker.useLabel(dflt).resolve()
        ));
        methodSpec.addInstruction("endswitch");
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        var maxKeyLength = Math.max("default".length(), Integer.toString(max).length());

        methodSpec.addInstruction("tableswitch");
        var j = 0;
        for (int i = min; i <= max; i++) {
            var key = i;
            var labelIndex = j;
            methodSpec.addInstruction(() -> String.format(
                    "    %s: %s",
                    indentLookupKey(maxKeyLength, key),
                    labelTracker.useLabel(labels[labelIndex]).resolve()
            ));
            j++;
        }
        methodSpec.addInstruction(() -> String.format(
                "    %s: %s",
                indentDefaultLookupKey(maxKeyLength),
                labelTracker.useLabel(dflt).resolve()
        ));
        methodSpec.addInstruction("endswitch");
    }

    private int findMaxLookupKeyLength(int[] keys) {
        var maxKeyLength = "default".length();
        for (int key : keys) {
            var length = Integer.toString(key).length();
            if (length > maxKeyLength) {
                maxKeyLength = length;
            }
        }
        return maxKeyLength;
    }

    private String indentLookupKey(int maxKeyLength, int key) {
        return String.format("%" + maxKeyLength + "d", key);
    }

    private String indentDefaultLookupKey(int maxKeyLength) {
        return String.format("%" + maxKeyLength + "s", "default");
    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        methodSpec.addInstruction(String.format("multianewarray %s %d", descriptor, numDimensions));
    }

}
