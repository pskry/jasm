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

import dk.skrypalle.Utils;
import org.objectweb.asm.Label;

public final class LabelUtils {

    private static final int FLAG_DEBUG_ONLY;
    private static final int FLAG_JUMP_TARGET;
    private static final int FLAG_RESOLVED;
    private static final int FLAG_REACHABLE;
    private static final boolean FLAGS_USABLE;

    static {
        var debugOnly = Utils.<Integer>readField(Label.class, "FLAG_DEBUG_ONLY");
        var jumpTarget = Utils.<Integer>readField(Label.class, "FLAG_JUMP_TARGET");
        var resolved = Utils.<Integer>readField(Label.class, "FLAG_RESOLVED");
        var reachable = Utils.<Integer>readField(Label.class, "FLAG_REACHABLE");

        if (debugOnly != null && jumpTarget != null && resolved != null && reachable != null) {
            FLAG_DEBUG_ONLY = debugOnly;
            FLAG_JUMP_TARGET = jumpTarget;
            FLAG_RESOLVED = resolved;
            FLAG_REACHABLE = reachable;
            FLAGS_USABLE = true;
        } else {
            FLAG_DEBUG_ONLY = 0;
            FLAG_JUMP_TARGET = 0;
            FLAG_RESOLVED = 0;
            FLAG_REACHABLE = 0;
            FLAGS_USABLE = false;
        }
    }

    /**
     * Determines if {@linkplain LabelUtils} is initialized properly.
     *
     * <p>If this method returns false, all other methods are not usable.
     *
     * @return {@code true} if this utility class has been initialized.
     */
    public static boolean isLabelUtilAvailable() {
        return FLAGS_USABLE;
    }

    /**
     * Determines if the {@linkplain Label} is flagged as debug label.
     *
     * <p>This method may only be used if {@linkplain #isLabelUtilAvailable()} returns {@code
     * true}.
     *
     * @param label the label to inspect
     * @return {@code true} if the label is a debug label, {@code false} otherwise
     */
    public static boolean isDebug(Label label) {
        return check(label, FLAG_DEBUG_ONLY);
    }

    /**
     * Determines if the {@linkplain Label} is flagged as jump target.
     *
     * @param label the label to inspect
     * @return {@code true} if the label is a jump target, {@code false} otherwise
     */
    public static boolean isJumpTarget(Label label) {
        return check(label, FLAG_JUMP_TARGET);
    }

    /**
     * Determines if the {@linkplain Label} is flagged as resolved.
     *
     * @param label the label to inspect
     * @return {@code true} if the label is resolved, {@code false} otherwise
     */
    public static boolean isResolved(Label label) {
        return check(label, FLAG_RESOLVED);
    }

    /**
     * Determines if the {@linkplain Label} is flagged as reachable.
     *
     * @param label the label to inspect
     * @return {@code true} if the label is reachable, {@code false} otherwise
     */
    public static boolean isReachable(Label label) {
        return check(label, FLAG_REACHABLE);
    }

    /**
     * Retrieve raw flag information from the provided {@linkplain Label}.
     *
     * @param label the label to inspect
     * @return raw label flags
     */
    public static int getFlags(Label label) {
        var flags = Utils.<Short>readField(label, "flags");
        if (flags == null) {
            return 0;
        }
        return flags;
    }

    private static boolean check(Label label, int flag) {
        var flags = getFlags(label);
        return isSet(flags, flag);
    }

    private static boolean isSet(int bitField, int checkBits) {
        return (bitField & checkBits) != 0;
    }

    private LabelUtils() { /* static utility */ }

}
