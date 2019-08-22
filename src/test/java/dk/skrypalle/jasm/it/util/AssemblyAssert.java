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
package dk.skrypalle.jasm.it.util;

import dk.skrypalle.Utils;
import dk.skrypalle.jasm.assembler.Assembly;
import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.api.Assertions.assertThat;

public class AssemblyAssert extends AbstractAssert<AssemblyAssert, Assembly> {

    AssemblyAssert(Assembly assembly) {
        super(assembly, AssemblyAssert.class);
    }

    /**
     * Asserts that the {@linkplain Assembly} under test has {@linkplain Assembly#getBinaryData()
     * binary data} byte-by-byte equal to the provided byte array.
     *
     * @param binaryClassFile binary class file to test against
     * @return this assert for method chaining
     */
    public AssemblyAssert isBinaryEqualTo(byte[] binaryClassFile) {
        var actualDump = Utils.hexDump(actual.getBinaryData());
        var expectedDump = Utils.hexDump(binaryClassFile);
        assertThat(actualDump)
                .isEqualTo(expectedDump);

        return myself;
    }

}
