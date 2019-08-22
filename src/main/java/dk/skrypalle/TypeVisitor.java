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
package dk.skrypalle;

import dk.skrypalle.jasm.assembler.err.ErrorListener;
import dk.skrypalle.jasm.generated.JasmBaseVisitor;

import java.util.Arrays;

import static dk.skrypalle.jasm.generated.JasmParser.ArgListContext;
import static dk.skrypalle.jasm.generated.JasmParser.ArrayTypeContext;
import static dk.skrypalle.jasm.generated.JasmParser.ClassTypeContext;
import static dk.skrypalle.jasm.generated.JasmParser.DescriptorContext;
import static dk.skrypalle.jasm.generated.JasmParser.FqcnContext;
import static dk.skrypalle.jasm.generated.JasmParser.MethodDescriptorContext;
import static dk.skrypalle.jasm.generated.JasmParser.PrimitiveTypeContext;
import static dk.skrypalle.jasm.generated.JasmParser.TypeDescriptorContext;

public class TypeVisitor extends JasmBaseVisitor<Object> {

    private final ErrorListener errorListener;

    public TypeVisitor(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    @Override
    public String visitDescriptor(DescriptorContext ctx) {
        var methodDescriptor = ctx.methodDescriptor();
        if (methodDescriptor != null) {
            return visitMethodDescriptor(methodDescriptor);
        }
        var typeDescriptor = ctx.typeDescriptor();
        if (typeDescriptor != null) {
            return visitTypeDescriptor(typeDescriptor);
        }

        throw new IllegalStateException();
    }

    @Override
    public String visitMethodDescriptor(MethodDescriptorContext ctx) {
        return "(" + visitArgList(ctx.args) + ")" + visit(ctx.returnType);
    }

    @Override
    public String visitTypeDescriptor(TypeDescriptorContext ctx) {
        return (String) visit(ctx.type());
    }

    @Override
    public String visitArgList(ArgListContext ctx) {
        if (ctx == null) {
            return "";
        }

        StringBuilder buf = new StringBuilder();
        ctx.type().stream()
                .map(this::visit)
                .forEach(buf::append);
        return buf.toString();
    }

    @Override
    public String visitPrimitiveType(PrimitiveTypeContext ctx) {
        var types = ctx.getText().toCharArray();
        var validTypes = Arrays.asList('Z', 'B', 'S', 'I', 'J', 'V');
        // TODO this should be handled by the lexer, not by the semantic analyzer.
        for (int i = 0; i < types.length; i++) {
            var type = types[i];
            if (!validTypes.contains(type)) {
                errorListener.emitInvalidPrimitiveType(ctx.start, i);
            }
        }
        return ctx.getText();
    }

    @Override
    public String visitArrayType(ArrayTypeContext ctx) {
        return "[" + visit(ctx.type());
    }

    @Override
    public Object visitClassType(ClassTypeContext ctx) {
        var fqcnCtx = ctx.fqcn();
        var fqcn = visitFqcn(fqcnCtx);
        if (!fqcn.matches("^L[^/]+(/[^/]+)*$")) {
            var start = fqcnCtx.start;
            errorListener.emitInvalidClassType(start, fqcn);
        }
        return fqcn + ";";
    }

    @Override
    public String visitFqcn(FqcnContext ctx) {
        return ctx.getText();
    }

}
