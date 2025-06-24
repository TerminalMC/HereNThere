/*
 * Copyright 2025 TerminalMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.terminalmc.herenthere.placeholder.util;

import net.minecraft.client.Minecraft;

public class PlayerDimensionUtil {

    public static void reset() {

    }

    public static String getDimension(String[] groups) {
        return Minecraft.getInstance().level.dimension().location().toString();
    }

    public static String getDimensionPath(String[] groups) {
        return Minecraft.getInstance().level.dimension().location().getPath();
    }
}
