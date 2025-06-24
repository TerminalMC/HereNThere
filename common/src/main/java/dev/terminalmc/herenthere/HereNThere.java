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

package dev.terminalmc.herenthere;

import dev.terminalmc.herenthere.config.Alias;
import dev.terminalmc.herenthere.config.Config;
import dev.terminalmc.herenthere.placeholder.Placeholders;
import dev.terminalmc.herenthere.placeholder.Placeholders.PlaceholderResult;
import dev.terminalmc.herenthere.util.ModLogger;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import static dev.terminalmc.herenthere.config.Config.options;

public class HereNThere {

    public static final String MOD_ID = "herenthere";
    public static final String MOD_NAME = "HereNThere";
    public static final ModLogger LOG = new ModLogger(MOD_NAME);
    public static final Component PREFIX = Component.empty()
            .append(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY))
            .append(Component.literal(MOD_NAME).withStyle(ChatFormatting.GOLD))
            .append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY))
            .withStyle(ChatFormatting.GRAY);
    public static final List<KeyMapping> KEYBINDS = List.of();
    public static final List<Alias> ALIASES = new ArrayList<>();

    public static void init() {
        Config.getAndSave();
    }

    public static void afterClientTick(Minecraft mc) {

    }

    public static void onConfigSaved(Config config) {
        ALIASES.clear();
        ALIASES.addAll(options().aliases);
        ALIASES.sort(Comparator.comparingInt(a -> -a.alias.length()));
    }

    public static String onTabKey(String before, String after) {
        String defaultVal = before + after;
        if (!options().modEnabled)
            return defaultVal;
        if (Minecraft.getInstance().player == null)
            return defaultVal;
        if (Minecraft.getInstance().level == null)
            return defaultVal;

        for (Alias alias : ALIASES) {
            if (aliasMatches(alias.alias, before)) {
                PlaceholderResult result = Placeholders.replace(alias.replacement);
                return before.substring(0, before.length() - alias.alias.length())
                        + result.string()
                        + after;
            }
        }
        return defaultVal;
    }

    private static boolean aliasMatches(String alias, String before) {
        if (options().requireDelimiter) {
            return Pattern.compile("(?<!\\w)" + Pattern.quote(alias) + "$")
                    .matcher(before)
                    .matches();
        } else {
            return before.endsWith(alias);
        }
    }
}
