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
package dk.skrypalle.jasm;

import dk.skrypalle.TypeVisitor;
import dk.skrypalle.jasm.err.ErrorListener;
import dk.skrypalle.jasm.generated.JasmBaseVisitor;
import org.antlr.v4.runtime.Token;
import org.apache.commons.text.StringEscapeUtils;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static dk.skrypalle.jasm.generated.JasmParser.*;

class InstrVisitor extends JasmBaseVisitor<Object> {

    private final ErrorListener errorListener;
    private final List<Consumer<MethodVisitor>> deferredActions;

    InstrVisitor(ErrorListener errorListener) {
        this.errorListener = errorListener;
        deferredActions = new ArrayList<>();
    }

    @Override
    public Void visitInstructionList(InstructionListContext ctx) {
        ctx.instruction().forEach(this::visit);
        return null;
    }

    //region instructions

    @Override
    public Void visitLdcIntInstr(LdcIntInstrContext ctx) {
        var value = Integer.decode(ctx.val.getText());
        if (value >= 0 && value <= 5) {
            defer(m -> m.visitInsn(Opcodes.ICONST_0 + value));
        } else if (value == -1) {
            defer(m -> m.visitInsn(Opcodes.ICONST_M1));
        } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            defer(m -> m.visitIntInsn(Opcodes.BIPUSH, value));
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            defer(m -> m.visitIntInsn(Opcodes.SIPUSH, value));
        } else {
            defer(m -> m.visitLdcInsn(value));
        }
        return null;
    }

    @Override
    public Object visitLdcStringInstr(LdcStringInstrContext ctx) {
        var value = visitString(ctx.val);
        defer(m -> m.visitLdcInsn(value));
        return null;
    }

    @Override
    public Object visitIstoreInstr(IstoreInstrContext ctx) {
        varInstr(Opcodes.ISTORE, ctx.val);
        return null;
    }

    @Override
    public Void visitAloadInstr(AloadInstrContext ctx) {
        varInstr(Opcodes.ALOAD, ctx.val);
        return null;
    }

    @Override
    public Object visitIloadInstr(IloadInstrContext ctx) {
        varInstr(Opcodes.ILOAD, ctx.val);
        return null;
    }

    private void varInstr(int opcode, Token token) {
        var value = Integer.decode(token.getText());
        defer(m -> m.visitVarInsn(opcode, value));
    }

    @Override
    public Object visitNewInstr(NewInstrContext ctx) {
        var type = visitFqcn(ctx.typ);
        defer(m -> m.visitTypeInsn(Opcodes.NEW, type));
        return null;
    }

    @Override
    public Object visitDupInstr(DupInstrContext ctx) {
        defer(m -> m.visitInsn(Opcodes.DUP));
        return null;
    }

    @Override
    public Object visitIaddInstr(IaddInstrContext ctx) {
        defer(m -> m.visitInsn(Opcodes.IADD));
        return null;
    }

    @Override
    public Object visitPopInstr(PopInstrContext ctx) {
        defer(m -> m.visitInsn(Opcodes.POP));
        return null;
    }

    @Override
    public Void visitReturnInstr(ReturnInstrContext ctx) {
        defer(m -> m.visitInsn(Opcodes.RETURN));
        return null;
    }

    @Override
    public Void visitGetStaticInstr(GetStaticInstrContext ctx) {
        var owner = ctx.owner.getText();
        var name = ctx.name.getText();
        var descriptor = visitDescriptor(ctx.desc);
        defer(m -> m.visitFieldInsn(
                Opcodes.GETSTATIC,
                owner,
                name,
                descriptor
        ));
        return null;
    }

    @Override
    public Void visitInvokeStaticInstr(InvokeStaticInstrContext ctx) {
        var owner = ctx.owner.getText();
        var name = ctx.name.getText();
        var descriptor = visitDescriptor(ctx.desc);
        defer(m -> m.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                owner,
                name,
                descriptor,
                false
        ));
        return null;
    }

    @Override
    public Void visitInvokeVirtualInstr(InvokeVirtualInstrContext ctx) {
        var owner = ctx.owner.getText();
        var name = ctx.name.getText();
        var descriptor = visitDescriptor(ctx.desc);
        defer(m -> m.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                owner,
                name,
                descriptor,
                false
        ));
        return null;
    }

    @Override
    public Object visitInvokeSpecialInstr(InvokeSpecialInstrContext ctx) {
        var owner = ctx.owner.getText();
        var name = ctx.name.getText();
        var descriptor = visitDescriptor(ctx.desc);
        defer(m -> m.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                owner,
                name,
                descriptor,
                false
        ));
        return null;
    }

    //endregion instructions

    @Override
    public String visitDescriptor(DescriptorContext ctx) {
        return new TypeVisitor(errorListener).visitDescriptor(ctx);
    }

    @Override
    public String visitFqcn(FqcnContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitString(StringContext ctx) {
        var raw = ctx.getText();
        var reduced = raw.substring(1, raw.length() - 1);
        return StringEscapeUtils.unescapeJava(reduced);
    }

    private void defer(Consumer<MethodVisitor> action) {
        deferredActions.add(action);
    }

    void consumeActions(MethodVisitor method) {
        for (Consumer<MethodVisitor> action : deferredActions) {
            action.accept(method);
        }
    }

}
