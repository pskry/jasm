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

import dk.skrypalle.jasm.err.ErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Utils;

class LexerErrorListenerAdapter extends BaseErrorListener {

    private final ErrorListener errorListener;

    LexerErrorListenerAdapter(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        if (e instanceof LexerNoViableAltException) {
            emitSyntaxError(recognizer.getInputStream().getSourceName(), line, charPositionInLine + 1, (LexerNoViableAltException) e);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private void emitSyntaxError(String sourceName, int line, int column, LexerNoViableAltException e) {
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
