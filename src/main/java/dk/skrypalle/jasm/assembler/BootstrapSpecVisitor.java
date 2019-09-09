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

import dk.skrypalle.jasm.generated.JasmBaseVisitor;
import dk.skrypalle.jasm.generated.JasmParser.BootstrapArgsContext;
import dk.skrypalle.jasm.generated.JasmParser.BootstrapSpecContext;
import dk.skrypalle.jasm.generated.JasmParser.BootstrapTagContext;
import dk.skrypalle.jasm.generated.JasmParser.BootstrapTargetContext;
import dk.skrypalle.jasm.generated.JasmParser.DescriptorBootstrapArgContext;
import dk.skrypalle.jasm.generated.JasmParser.IntBootstrapArgContext;
import dk.skrypalle.jasm.generated.JasmParser.LabelContext;
import dk.skrypalle.jasm.generated.JasmParser.MethodHandleBootstrapArgContext;
import dk.skrypalle.jasm.generated.JasmParser.StringBootstrapArgContext;
import org.apache.commons.collections4.CollectionUtils;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

class BootstrapSpecVisitor extends JasmBaseVisitor<Object> {

    private final IdentifierVisitor identifierVisitor;
    private final TypeVisitor typeVisitor;
    private final BootstrapTracker bootstrapTracker;

    BootstrapSpecVisitor(
            IdentifierVisitor identifierVisitor,
            TypeVisitor typeVisitor,
            BootstrapTracker bootstrapTracker) {
        this.identifierVisitor = identifierVisitor;
        this.typeVisitor = typeVisitor;
        this.bootstrapTracker = bootstrapTracker;
    }

    @Override
    public Object visitBootstrapSpec(BootstrapSpecContext ctx) {
        var id = visitLabel(ctx.id);
        var target = visitBootstrapTarget(ctx.bootstrapTarget());
        var args = visitBootstrapArgs(ctx.bootstrapArgs());

        bootstrapTracker.record(new Bootstrap(
                id,
                target.tag,
                target.owner,
                target.name,
                target.descriptor,
                false,
                args
        ));
        return null;
    }

    @Override
    public Integer visitBootstrapTag(BootstrapTagContext ctx) {
        switch (ctx.getText()) {
            case "h_getfield":
                return Opcodes.H_GETFIELD;
            case "h_getstatic":
                return Opcodes.H_GETSTATIC;
            case "h_putfield":
                return Opcodes.H_PUTFIELD;
            case "h_putstatic":
                return Opcodes.H_PUTSTATIC;
            case "h_invokevirtual":
                return Opcodes.H_INVOKEVIRTUAL;
            case "h_invokestatic":
                return Opcodes.H_INVOKESTATIC;
            case "h_invokespecial":
                return Opcodes.H_INVOKESPECIAL;
            case "h_newinvokespecial":
                return Opcodes.H_NEWINVOKESPECIAL;
            case "h_invokeinterface":
                return Opcodes.H_INVOKEINTERFACE;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public BootstrapTarget visitBootstrapTarget(BootstrapTargetContext ctx) {
        var tag = visitBootstrapTag(ctx.tag);
        var owner = identifierVisitor.visitFqtn(ctx.owner);
        var name = identifierVisitor.visitMethodName(ctx.name);
        var descriptor = typeVisitor.visitDescriptor(ctx.desc);
        return new BootstrapTarget(tag, owner, name, descriptor);
    }

    @Override
    public Object[] visitBootstrapArgs(BootstrapArgsContext ctx) {
        if (ctx == null) {
            return null;
        }

        var argList = ctx.bootstrapArg();
        if (CollectionUtils.isEmpty(argList)) {
            return null;
        }

        return argList.stream()
                .map(this::visit)
                .toArray();
    }

    @Override
    public Object visitIntBootstrapArg(IntBootstrapArgContext ctx) {
        return Integer.parseInt(ctx.INTEGER().getText());
    }

    @Override
    public Object visitStringBootstrapArg(StringBootstrapArgContext ctx) {
        return identifierVisitor.visitString(ctx.string());
    }

    @Override
    public Object visitDescriptorBootstrapArg(DescriptorBootstrapArgContext ctx) {
        var descriptor = typeVisitor.visitDescriptor(ctx.descriptor());
        return Type.getType(descriptor);
    }

    @Override
    public Object visitMethodHandleBootstrapArg(MethodHandleBootstrapArgContext ctx) {
        var tag = visitBootstrapTag(ctx.tag);
        var owner = identifierVisitor.visitFqtn(ctx.owner);
        var name = identifierVisitor.visitMethodName(ctx.name);
        var descriptor = typeVisitor.visitDescriptor(ctx.desc);
        return new Handle(tag, owner, name, descriptor, false);
    }

    @Override
    public String visitLabel(LabelContext ctx) {
        return identifierVisitor.visitIdentifier(ctx.name);
    }

    private static class BootstrapTarget {
        private final int tag;
        private final String owner;
        private final String name;
        private final String descriptor;

        private BootstrapTarget(int tag, String owner, String name, String descriptor) {
            this.tag = tag;
            this.owner = owner;
            this.name = name;
            this.descriptor = descriptor;
        }
    }

}
