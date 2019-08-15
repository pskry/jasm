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

import org.apache.commons.io.HexDump;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ClassFile {

    private final String jvmClassName;
    private final byte[] binaryData;

    ClassFile(String jvmClassName, byte[] binaryData) {
        this.jvmClassName = jvmClassName;
        this.binaryData = binaryData.clone();
    }

    public String getJvmClassName() {
        return jvmClassName;
    }

    public byte[] getBinaryData() {
        return binaryData.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ClassFile other = (ClassFile) obj;
        return new EqualsBuilder()
                .append(this.jvmClassName, other.jvmClassName)
                .append(this.binaryData, other.binaryData)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(jvmClassName)
                .append(binaryData)
                .toHashCode();
    }

    @Override
    public String toString() {
        String binToString;
        try {
            var out = new ByteArrayOutputStream();
            HexDump.dump(binaryData, 0, out, 0);
            binToString = new String(out.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            binToString = Arrays.toString(binaryData);
        }

        return jvmClassName + '\n' + binToString;
    }

}
