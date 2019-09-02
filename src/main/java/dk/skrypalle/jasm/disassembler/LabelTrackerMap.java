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
package dk.skrypalle.jasm.disassembler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class LabelTrackerMap {

    private final Map<String, LabelTracker> labelTrackersByMethodId;

    LabelTrackerMap() {
        labelTrackersByMethodId = new HashMap<>();
    }

    LabelTracker getLabelTrackerForMethod(
            int access,
            String name,
            String descriptor,
            String signature,
            String[] exceptions) {
        var key = createMapKey(access, name, descriptor, signature, exceptions);
        return labelTrackersByMethodId.computeIfAbsent(key, k -> new LabelTracker());
    }

    void link() {
        labelTrackersByMethodId.values().forEach(LabelTracker::link);
    }

    private static String createMapKey(
            int access,
            String name,
            String descriptor,
            String signature,
            String[] exceptions) {
        return String.format(
                "%d::%s::%s::%s::%s",
                access,
                name,
                descriptor,
                signature,
                Arrays.toString(exceptions)
        );
    }

}
