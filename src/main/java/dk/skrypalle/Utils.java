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

import org.apache.commons.io.HexDump;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class Utils {

    /**
     * Dump an array of bytes.
     *
     * <p>The output is formatted for human inspection, with a hexadecimal offset followed by the
     * hexadecimal values of the next 16 bytes of data and the printable ASCII characters (if any)
     * that those bytes represent printed per each line of output.
     *
     * @param data the byte array to be dumped
     * @return human readable hex-dump
     */
    public static String hexDump(byte[] data) {
        var out = new ByteArrayOutputStream();
        try {
            HexDump.dump(data, 0, out, 0);
        } catch (IOException e) { /* ignored - cannot happen in ByteArrayOutputStream */ }

        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * Accesses a given instance field via reflection.
     *
     * <p>If any problem occurred during the operation, the method returns {@code null}.
     *
     * @param owner     the owner of the field to access
     * @param fieldName the name of the field to read
     * @param <T>       the type of the field to read
     * @return the field value, null if any error
     */
    public static <T> T readField(Object owner, String fieldName) {
        try {
            var field = owner.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            var t = (T) field.get(owner);
            return t;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Accesses a given static field via reflection.
     *
     * <p>If any problem occurred during the operation, the method returns {@code null}.
     *
     * @param ownerClass the owner class of the field to access
     * @param fieldName  the name of the field to read
     * @param <T>        the type of the field to read
     * @return the field value, null if any error
     */
    public static <T> T readField(Class<?> ownerClass, String fieldName) {
        try {
            var field = ownerClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            var t = (T) field.get(null);
            return t;
        } catch (Throwable t) {
            return null;
        }
    }

}
