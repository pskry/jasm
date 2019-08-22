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
import dk.skrypalle.jasm.generated.JasmLexer;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.Utils;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.regex.Pattern;

class AssemblerLexer extends JasmLexer {

    private static final Pattern PRIMITIVE_CLASS_PATTERN = Pattern.compile("[ZBSIJV]+L.*$");

    private final Queue<Token> tokenStash = new ArrayDeque<>();

    AssemblerLexer(CharStream input, ErrorListener errorListener) {
        super(input);

        removeErrorListeners();
        addErrorListener(new ErrorListenerAdapter(errorListener));
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

    private static class ErrorListenerAdapter extends BaseErrorListener {

        private final ErrorListener errorListener;

        private ErrorListenerAdapter(ErrorListener errorListener) {
            this.errorListener = errorListener;
        }

        @Override
        public void syntaxError(
                Recognizer<?, ?> recognizer,
                Object offendingSymbol,
                int line,
                int charPositionInLine,
                String msg,
                RecognitionException e) {
            if (e instanceof LexerNoViableAltException) {
                emitSyntaxError(recognizer.getInputStream().getSourceName(),
                        line,
                        charPositionInLine + 1,
                        (LexerNoViableAltException) e
                );
            } else {
                throw new UnsupportedOperationException();
            }
        }

        private void emitSyntaxError(
                String sourceName,
                int line,
                int column,
                LexerNoViableAltException e) {
            String symbol = "";
            var startIndex = e.getStartIndex();
            var inputStream = e.getInputStream();
            if (startIndex >= 0 && startIndex < inputStream.size()) {
                symbol = inputStream.getText(Interval.of(startIndex, startIndex));
                symbol = Utils.escapeWhitespace(symbol, false);
            }
            errorListener.emitUnknownSymbol(sourceName, line, column, symbol);
        }

    }

}
