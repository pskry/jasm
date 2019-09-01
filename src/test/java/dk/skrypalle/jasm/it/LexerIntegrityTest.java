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
package dk.skrypalle.jasm.it;

import dk.skrypalle.jasm.generated.JasmLexer;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.fail;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class LexerIntegrityTest {

    @Test
    public void allRulesAndTokensAreDefined() {
        // arrange
        var invalidPattern = Pattern.compile("^T__[0-9]+$");

        // act
        var invalidRuleTypes = new ArrayList<Integer>();
        for (int i = 1; i <= getMaxTokenIndex(); i++) {
            var ruleName = getRuleName(i);
            if (invalidPattern.matcher(ruleName).matches()) {
                invalidRuleTypes.add(i);
            }
        }

        // assert
        if (!invalidRuleTypes.isEmpty()) {
            var message = formatViolations(invalidRuleTypes);
            fail("Found undefined lexer rules and/or tokens:\n%s", message);
        }
    }

    @Test
    public void instructionTokensAreInOneContinuousBlock() {
        assertContinuousTokenBlock(
                "instructions",
                fieldName -> fieldName.endsWith("_INSTR")
        );
    }

    @Test
    public void directiveTokensAreInOneContinuousBlock() {
        assertContinuousTokenBlock(
                "directives",
                fieldName -> fieldName.endsWith("_DIRECTIVE")
        );
    }

    private static int getMaxTokenIndex() {
        return JasmLexer.VOCABULARY.getMaxTokenType();
    }

    private static String getLiteralName(int index) {
        return StringUtils.defaultString(JasmLexer.VOCABULARY.getLiteralName(index))
                .replace("'", "");
    }

    private static String getRuleName(int index) {
        return JasmLexer.ruleNames[index - 1];
    }

    private static String formatViolations(Collection<Integer> indices) {
        return indices.stream()
                .map(LexerIntegrityTest::formatInvalidType)
                .collect(Collectors.joining("\n    - ", "    - ", ""));
    }

    private static String formatInvalidType(int index) {
        return String.format(
                "%s :: %s",
                getRuleName(index),
                getLiteralName(index)
        );
    }

    private static void assertContinuousTokenBlock(
            String tokenBlockName,
            Predicate<String> blockIdentifier) {
        // arrange
        // act
        var tokenDefinitions = Stream.of(JasmLexer.class.getDeclaredFields())
                .filter(f -> f.getType() == int.class)
                .collect(Collectors.toList());
        var block = getFullBlock(tokenDefinitions, blockIdentifier);
        var misplaced = detectMisplacedTokens(block, blockIdentifier);

        // assert
        if (!misplaced.isEmpty()) {
            var buf = new StringBuilder();
            for (int i = 0; i < block.size(); i++) {
                var field = block.get(i);
                if (misplaced.contains(i)) {
                    buf.append("misplaced --> ");

                } else {
                    buf.append("              ");
                }
                buf.append(field.getName()).append('\n');
            }
            fail(
                    "Token-block '%s' is interrupted by %d token(s).\n%s",
                    tokenBlockName,
                    misplaced.size(),
                    buf
            );
        }
    }

    private static List<Field> getFullBlock(
            List<Field> tokenDefinitions,
            Predicate<String> blockIdentifier) {
        int firstIndex = -1;
        int lastIndex = -1;
        for (int i = 0; i < tokenDefinitions.size(); i++) {
            var tokenDef = tokenDefinitions.get(i);
            if (!blockIdentifier.test(tokenDef.getName())) {
                continue;
            }

            if (firstIndex == -1) {
                firstIndex = i;
            }
            lastIndex = i;
        }

        var block = new ArrayList<Field>();
        for (int i = firstIndex; i <= lastIndex; i++) {
            block.add(tokenDefinitions.get(i));
        }

        return block;
    }

    private static List<Integer> detectMisplacedTokens(
            List<Field> block,
            Predicate<String> blockIdentifier) {
        var misplaced = new ArrayList<Integer>();
        for (int i = 0; i < block.size(); i++) {
            var field = block.get(i);
            if (!blockIdentifier.test(field.getName())) {
                misplaced.add(i);
            }
        }

        return misplaced;
    }

}
