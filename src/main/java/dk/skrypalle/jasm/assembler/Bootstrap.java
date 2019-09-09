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

class Bootstrap {

    private final String identifier;
    private final int tag;
    private final String owner;
    private final String name;
    private final String descriptor;
    private final boolean isInterface;
    private final Object[] args;

    Bootstrap(
            String identifier,
            int tag,
            String owner,
            String name,
            String descriptor,
            boolean isInterface,
            Object[] args) {
        this.identifier = identifier;
        this.tag = tag;
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
        this.isInterface = isInterface;
        this.args = args;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getTag() {
        return tag;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public Object[] getArgs() {
        return args;
    }

}
