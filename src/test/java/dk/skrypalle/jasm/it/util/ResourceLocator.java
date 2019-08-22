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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

final class ResourceLocator {

    static Collection<String> locateResources(String regex) throws IOException {
        var pattern = Pattern.compile(regex);
        var result = new ArrayList<String>();
        var classPath = System.getProperty("java.class.path", ".");
        var classPathElements = classPath.split(File.pathSeparator);
        for (String element : classPathElements) {
            result.addAll(locateResources(element, pattern));
        }
        return result;
    }

    private static Collection<String> locateResources(String element, Pattern pattern)
            throws IOException {
        var result = new ArrayList<String>();
        var file = new File(element);
        if (file.isDirectory()) {
            result.addAll(getResourcesFromDirectory(file, pattern));
        } else {
            result.addAll(getResourcesFromJarFile(file, pattern));
        }
        return result;
    }

    private static Collection<String> getResourcesFromDirectory(File directory, Pattern pattern)
            throws IOException {
        var result = new ArrayList<String>();
        var files = directory.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        for (var file : files) {
            if (file.isDirectory()) {
                result.addAll(getResourcesFromDirectory(file, pattern));
            } else {
                var fileName = file.getCanonicalPath();
                var matches = pattern.matcher(fileName).matches();
                if (matches) {
                    result.add(fileName);
                }
            }
        }
        return result;
    }

    private static Collection<String> getResourcesFromJarFile(File file, Pattern pattern)
            throws IOException {
        var result = new ArrayList<String>();
        try (var zipFile = new ZipFile(file)) {
            var entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                var zipEntry = entries.nextElement();
                var fileName = zipEntry.getName();
                var matches = pattern.matcher(fileName).matches();
                if (matches) {
                    result.add(fileName);
                }
            }
        }
        return result;
    }

    private ResourceLocator() { /* static utility */ }

}
