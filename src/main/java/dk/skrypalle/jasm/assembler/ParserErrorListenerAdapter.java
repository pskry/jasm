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
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

class ParserErrorListenerAdapter extends BaseErrorListener {

    private final ErrorListener errorListener;

    ParserErrorListenerAdapter(ErrorListener errorListener) {
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
        if (e instanceof InputMismatchException) {
            emitInputMismatch(e);
        } else if (e instanceof NoViableAltException) {
            emitInputMismatch(e);
        } else if (e == null) {
            var parser = (Parser) recognizer;
            var token = parser.getCurrentToken();
            var expected = parser.getExpectedTokens().toString(parser.getVocabulary());
            errorListener.emitInputMismatch(token, expected);
        } else {
            var message = String.format(
                    "SyntaxError :: recognizer=%s, offSym=%s, line=%d, col=%d, msg=%s, e=%s",
                    recognizer,
                    offendingSymbol,
                    line,
                    charPositionInLine,
                    msg,
                    e
            );
            throw new UnsupportedOperationException(message);
        }
    }

    private void emitInputMismatch(RecognitionException e) {
        var vocabulary = e.getRecognizer().getVocabulary();
        var expectedTokens = e.getExpectedTokens().toString(vocabulary);
        errorListener.emitInputMismatch(e.getOffendingToken(), expectedTokens);
    }

}
