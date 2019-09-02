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
import dk.skrypalle.jasm.generated.JasmParser.ExceptionSpecContext;
import org.objectweb.asm.MethodVisitor;

class ExceptionSpecVisitor extends JasmBaseVisitor<Object> {

    private final MethodVisitor method;
    private final LabelTracker labelTracker;

    ExceptionSpecVisitor(MethodVisitor method, LabelTracker labelTracker) {
        this.method = method;
        this.labelTracker = labelTracker;
    }

    @Override
    public Object visitExceptionSpec(ExceptionSpecContext ctx) {
        var start = labelTracker.getLabel(ctx.start.getText());
        var end = labelTracker.getLabel(ctx.end.getText());
        var handler = labelTracker.getLabel(ctx.handler.getText());
        var type = ctx.typ.getText();

        method.visitTryCatchBlock(start, end, handler, type);

        return null;
    }

}
