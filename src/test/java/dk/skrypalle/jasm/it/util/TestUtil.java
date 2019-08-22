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

import dk.skrypalle.jasm.assembler.Assembly;
import org.objectweb.asm.ClassReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public final class TestUtil {

    private static final int INPUT_STREAM_DATA_CHUNK_SIZE = 4096;

    public static String toJvmClassName(String className) {
        return className.replace('.', '/');
    }

    public static String escapeJvmClassNameForRegex(String jvmClassName) {
        return jvmClassName.replace("$", "\\$");
    }

    public static byte[] readClass(String className) throws IOException {
        var classFileName = toJvmClassName(className) + ".class";
        var in = ClassLoader.getSystemClassLoader().getResourceAsStream(classFileName);
        if (in == null) {
            var message = String.format(
                    "Could not load class '%s' - File %s not found",
                    className,
                    classFileName
            );
            throw new IOException(message);
        }
        try (in; var out = new ByteArrayOutputStream()) {
            var data = new byte[INPUT_STREAM_DATA_CHUNK_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(data, 0, data.length)) != -1) {
                out.write(data, 0, bytesRead);
            }
            out.flush();
            return out.toByteArray();
        }
    }

    public static Path getResourcePath(String resourceName) {
        try {
            var uri = TestUtil.class.getResource(resourceName).toURI();
            return Paths.get(uri);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to locate resource file " + resourceName, e);
        }
    }

    public static Class<?> defineClass(Assembly assembly) {
        var reader = new ClassReader(assembly.getBinaryData());
        assertThat(assembly.getJvmClassName())
                .as("Expecting className integrity.")
                .isEqualTo(reader.getClassName());
        return new DynamicClassLoader().defineClass(assembly);
    }

    private TestUtil() { /* static utility */ }

}
