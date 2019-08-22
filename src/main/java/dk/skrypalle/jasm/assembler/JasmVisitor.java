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

import dk.skrypalle.TypeVisitor;
import dk.skrypalle.jasm.assembler.err.ErrorListener;
import dk.skrypalle.jasm.generated.JasmBaseVisitor;
import dk.skrypalle.jasm.generated.JasmLexer;
import org.apache.commons.text.StringEscapeUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static dk.skrypalle.jasm.generated.JasmParser.AccessSpecContext;
import static dk.skrypalle.jasm.generated.JasmParser.BytecodeVersionContext;
import static dk.skrypalle.jasm.generated.JasmParser.DescriptorContext;
import static dk.skrypalle.jasm.generated.JasmParser.HeaderContext;
import static dk.skrypalle.jasm.generated.JasmParser.ImplementsSpecContext;
import static dk.skrypalle.jasm.generated.JasmParser.JasmFileContext;
import static dk.skrypalle.jasm.generated.JasmParser.MethodSpecContext;
import static dk.skrypalle.jasm.generated.JasmParser.SourceContext;
import static dk.skrypalle.jasm.generated.JasmParser.StringContext;
import static dk.skrypalle.jasm.generated.JasmParser.SuperSpecContext;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

class JasmVisitor extends JasmBaseVisitor<Object> {

    private final ErrorListener errorListener;
    private final List<Consumer<ClassWriter>> deferredActions;

    private String className;

    JasmVisitor(ErrorListener errorListener) {
        this.errorListener = errorListener;
        deferredActions = new ArrayList<>();
    }

    Assembly getAssembly() {
        if (errorListener.getNumberOfErrors() > 0) {
            return null;
        }

        var classWriter = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
        for (Consumer<ClassWriter> action : deferredActions) {
            action.accept(classWriter);
        }
        classWriter.visitEnd();

        return new Assembly(
                className,
                classWriter.toByteArray()
        );
    }

    @Override
    public Object visitJasmFile(JasmFileContext ctx) {
        visit(ctx.header());
        for (MethodSpecContext methodSpec : ctx.methodSpec()) {
            visitMethodSpec(methodSpec);
        }
        return null;
    }

    @Override
    public Object visitHeader(HeaderContext ctx) {
        var version = visitBytecodeVersion(ctx.bytecodeVersion());
        visitSource(ctx.source());
        var superName = visitSuperSpec(ctx.superSpec());
        var classSpec = ctx.classSpec();
        int access = visitAccessSpecs(classSpec.accessSpec());
        var interfaces = ctx.implementsSpec().stream()
                .map(this::visitImplementsSpec)
                .toArray(String[]::new);

        className = classSpec.name.getText();
        defer(cw -> cw.visit(
                version,
                access,
                className,
                null,
                superName,
                interfaces
        ));

        return null;
    }

    @Override
    public Integer visitBytecodeVersion(BytecodeVersionContext ctx) {
        var minor = Integer.decode(ctx.minor.getText());
        var major = Integer.decode(ctx.major.getText());

        if (minor != 0) {
            errorListener.emitIllegalMinorBytecodeVersion(ctx.minor, 0, 0);
            return null;
        }
        switch (major) {
            case 45:
                return Opcodes.V1_1;
            case 46:
                return Opcodes.V1_2;
            case 47:
                return Opcodes.V1_3;
            case 48:
                return Opcodes.V1_4;
            case 49:
                return Opcodes.V1_5;
            case 50:
                return Opcodes.V1_6;
            case 51:
                return Opcodes.V1_7;
            case 52:
                return Opcodes.V1_8;
            case 53:
                return Opcodes.V9;
            case 54:
                return Opcodes.V10;
            case 55:
                return Opcodes.V11;
            case 56:
                return Opcodes.V12;
            case 57:
                return Opcodes.V13;
            default:
                errorListener.emitIllegalMajorBytecodeVersion(ctx.major, 45, 57);
                return null;
        }
    }

    @Override
    public Object visitSource(SourceContext ctx) {
        var sourceName = visitString(ctx.sourceFile);
        defer(cw -> cw.visitSource(sourceName, null));
        return null;
    }

    @Override
    public String visitSuperSpec(SuperSpecContext ctx) {
        return ctx.name.getText();
    }

    @Override
    public Integer visitAccessSpec(AccessSpecContext ctx) {
        switch (ctx.start.getType()) {
            case JasmLexer.PUBLIC:
                return Opcodes.ACC_PUBLIC;
            case JasmLexer.PRIVATE:
                return Opcodes.ACC_PRIVATE;
            case JasmLexer.PROTECTED:
                return Opcodes.ACC_PROTECTED;
            case JasmLexer.STATIC:
                return Opcodes.ACC_STATIC;
            case JasmLexer.FINAL:
                return Opcodes.ACC_FINAL;
            case JasmLexer.SUPER:
                return Opcodes.ACC_SUPER;
            case JasmLexer.SYNCHRONIZED:
                return Opcodes.ACC_SYNCHRONIZED;
            //            case JasmLexer.OPEN:
            //                return Opcodes.ACC_OPEN;
            //            case JasmLexer.TRANSITIVE:
            //                return Opcodes.ACC_TRANSITIVE;
            case JasmLexer.VOLATILE:
                return Opcodes.ACC_VOLATILE;
            case JasmLexer.BRIDGE:
                return Opcodes.ACC_BRIDGE;
            //            case JasmLexer.STATIC_PHASE:
            //                return Opcodes.ACC_STATIC_PHASE;
            case JasmLexer.VARARGS:
                return Opcodes.ACC_VARARGS;
            case JasmLexer.TRANSIENT:
                return Opcodes.ACC_TRANSIENT;
            case JasmLexer.NATIVE:
                return Opcodes.ACC_NATIVE;
            case JasmLexer.INTERFACE:
                return Opcodes.ACC_INTERFACE;
            case JasmLexer.ABSTRACT:
                return Opcodes.ACC_ABSTRACT;
            //            case JasmLexer.STRICT:
            //                return Opcodes.ACC_STRICT;
            //            case JasmLexer.SYNTHETIC:
            //                return Opcodes.ACC_SYNTHETIC;
            case JasmLexer.ANNOTATION:
                return Opcodes.ACC_ANNOTATION;
            case JasmLexer.ENUM:
                return Opcodes.ACC_ENUM;
            //            case JasmLexer.MANDATED:
            //                return Opcodes.ACC_MANDATED;
            //            case JasmLexer.MODULE:
            //                return Opcodes.ACC_MODULE;
            default:
                return null;
        }
    }

    @Override
    public String visitImplementsSpec(ImplementsSpecContext ctx) {
        return ctx.name.getText();
    }

    @Override
    public Object visitMethodSpec(MethodSpecContext ctx) {
        int access = visitAccessSpecs(ctx.accessSpec());
        var descriptor = visitDescriptor(ctx.descriptor());

        var instrVisitor = new InstrVisitor(errorListener);
        var instructionList = ctx.instructionList();
        if (instructionList != null) {
            instrVisitor.visitInstructionList(ctx.instructionList());
        }

        defer(cw -> {
            var method = cw.visitMethod(
                    access,
                    ctx.name.getText(),
                    descriptor,
                    null,
                    null
            );
            instrVisitor.consumeActions(method);
            method.visitMaxs(0, 0);
        });

        return null;
    }

    @Override
    public String visitDescriptor(DescriptorContext ctx) {
        return new TypeVisitor(errorListener).visitDescriptor(ctx);
    }

    @Override
    public String visitString(StringContext ctx) {
        var raw = ctx.getText();
        return StringEscapeUtils.unescapeJava(raw.substring(1, raw.length() - 1));
    }

    @Override
    protected Object aggregateResult(Object aggregate, Object nextResult) {
        if (aggregate == null) {
            return nextResult;
        }
        if (nextResult == null) {
            return aggregate;
        }

        throw new UnsupportedOperationException("Cannot aggregate result.");
    }

    @Override
    protected Object defaultResult() {
        throw new UnsupportedOperationException("Cannot provide default result.");
    }

    private int visitAccessSpecs(List<AccessSpecContext> specs) {
        var access = 0;
        for (AccessSpecContext spec : specs) {
            access |= visitAccessSpec(spec);
        }
        return access;
    }

    private void defer(Consumer<ClassWriter> action) {
        deferredActions.add(action);
    }

}
