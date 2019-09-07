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

import dk.skrypalle.jasm.assembler.err.ErrorListener;
import dk.skrypalle.jasm.generated.JasmBaseVisitor;
import dk.skrypalle.jasm.generated.JasmParser.FqcnContext;
import dk.skrypalle.jasm.generated.JasmParser.FqtnContext;
import dk.skrypalle.jasm.generated.JasmParser.IdentifierContext;
import dk.skrypalle.jasm.generated.JasmParser.MethodNameContext;
import dk.skrypalle.jasm.generated.JasmParser.StringContext;
import org.apache.commons.text.StringEscapeUtils;

class IdentifierVisitor extends JasmBaseVisitor<Object> {

    private final ErrorListener errorListener;

    IdentifierVisitor(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    @Override
    public String visitMethodName(MethodNameContext ctx) {
        var identifier = ctx.identifier();
        return identifier == null
                ? ctx.getText()
                : visitIdentifier(identifier);
    }

    @Override
    public String visitIdentifier(IdentifierContext ctx) {
        var string = ctx.string();
        return string == null
                ? ctx.getText()
                : visitString(string);
    }

    @Override
    public String visitString(StringContext ctx) {
        var raw = ctx.getText();
        return StringEscapeUtils.unescapeJava(raw.substring(1, raw.length() - 1));
    }

    @Override
    public String visitFqtn(FqtnContext ctx) {
        var fqcn = ctx.fqcn();
        var arrayType = ctx.arrayType();
        if (fqcn != null) {
            return visitFqcn(fqcn);
        } else if (arrayType != null) {
            return new TypeVisitor(errorListener, this).visitArrayType(arrayType);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String visitFqcn(FqcnContext ctx) {
        return ctx.getText();
    }

}
