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

import org.objectweb.asm.Handle;

import java.util.HashMap;
import java.util.Map;

class BootstrapTracker {

    private static final Object[] EMPTY = new Object[0];

    private final Map<String, Handle> handleMap;
    private final Map<String, Object[]> argsMap;

    BootstrapTracker() {
        handleMap = new HashMap<>();
        argsMap = new HashMap<>();
    }

    void record(Bootstrap bootstrap) {
        var identifier = bootstrap.getIdentifier();
        handleMap.computeIfAbsent(
                identifier,
                id -> new Handle(
                        bootstrap.getTag(),
                        bootstrap.getOwner(),
                        bootstrap.getName(),
                        bootstrap.getDescriptor(),
                        bootstrap.isInterface()
                ));
        var args = bootstrap.getArgs();
        if (args != null && args.length != 0) {
            argsMap.putIfAbsent(identifier, args);
        }
    }

    Handle getHandleForId(String identifier) {
        return handleMap.get(identifier);
    }

    Object[] getArgsForId(String identifier) {
        return argsMap.getOrDefault(identifier, EMPTY);
    }

}
