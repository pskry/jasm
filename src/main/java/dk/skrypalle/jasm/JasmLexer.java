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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.regex.Pattern;

@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_SUPERCLASS")
class JasmLexer extends dk.skrypalle.jasm.generated.JasmLexer {

    private static final Pattern PRIMITIVE_CLASS_PATTERN = Pattern.compile("[ZBSIJV]+L.*$");

    private final Queue<Token> tokenStash = new ArrayDeque<>();

    JasmLexer(CharStream input) {
        super(input);
    }

    @Override
    public Token nextToken() {
        var next = next();
        if (PRIMITIVE_CLASS_PATTERN.matcher(next.getText()).matches()) {
            return splitAndStash(next);
        }
        return next;
    }

    private Token next() {
        var next = tokenStash.poll();
        return next == null
                ? super.nextToken()
                : next;
    }

    private Token splitAndStash(Token token) {
        var split = split(token, token.getStartIndex(), token.getStartIndex());
        var stash = split(token, token.getStartIndex() + 1, token.getStopIndex());
        tokenStash.add(stash);
        return split;
    }

    private Token split(Token token, int start, int stop) {
        var tokenSource = token.getTokenSource();
        var inStream = token.getInputStream();
        var source = new Pair<>(tokenSource, inStream);

        return new CommonToken(source, token.getType(), token.getChannel(), start, stop);
    }

}
