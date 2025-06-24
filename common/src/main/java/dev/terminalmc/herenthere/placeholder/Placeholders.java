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

package dev.terminalmc.herenthere.placeholder;

import dev.terminalmc.herenthere.placeholder.util.PlayerDimensionUtil;
import dev.terminalmc.herenthere.placeholder.util.PlayerInfoUtil;
import dev.terminalmc.herenthere.placeholder.util.PlayerPositionUtil;
import dev.terminalmc.herenthere.placeholder.util.TargetedEntityUtil;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholders {

    private static int faults;

    public static final String POS_STRING =
            "%(l?)pos(d?)(\\W? ?)(?:\\^(-?\\d+(?:\\.\\d+)?)?\\^(-?\\d+(?:\\.\\d+)?)?\\^(-?\\d+(?:\\.\\d+)?)?)?%";
    public static final String POS_COMPONENT_STRING =
            "%(l?)([xyz])(d?)(?:([+\\-*/])(-?\\d+(?:\\.\\d+)?))?%";
    public static final String FACING_ANGLE_STRING =
            "%facing(\\W? ?)(?:([+\\-*/])(-?\\d+(?:\\.\\d+)?))?%";
    public static final String TARGET_ENTITY_ID_STRING =
            "%target-id%";
    public static final String PLAYER_NAME_STRING =
            "%name%";
    public static final String DIMENSION_STRING =
            "%dimension%";
    public static final String DIMENSION_PATH_STRING =
            "%dimension-path%";

    public static final Placeholder[] PLACEHOLDERS = {
            new Placeholder(
                    Pattern.compile(POS_STRING),
                    PlayerPositionUtil::getPosString
            ),
            new Placeholder(
                    Pattern.compile(POS_COMPONENT_STRING),
                    PlayerPositionUtil::getPosComponentString
            ),
            new Placeholder(
                    Pattern.compile(FACING_ANGLE_STRING),
                    PlayerPositionUtil::getFacingAngleString
            ),
            new Placeholder(
                    Pattern.compile(TARGET_ENTITY_ID_STRING),
                    TargetedEntityUtil::getTargetEntityId
            ),
            new Placeholder(
                    Pattern.compile(PLAYER_NAME_STRING),
                    PlayerInfoUtil::getPlayerName
            ),
            new Placeholder(
                    Pattern.compile(DIMENSION_STRING),
                    PlayerDimensionUtil::getDimension
            ),
            new Placeholder(
                    Pattern.compile(DIMENSION_PATH_STRING),
                    PlayerDimensionUtil::getDimensionPath
            ),
    };

    /**
     * Breaks if player is not in-game. Does not self-check for performance reasons, but expects
     * caller to validate.
     */
    public static PlaceholderResult replace(String message) {
        faults = 0;
        PlayerPositionUtil.reset();
        PlayerInfoUtil.reset();
        TargetedEntityUtil.reset();
        PlayerDimensionUtil.reset();

        if (!message.contains("%"))
            return new PlaceholderResult(message, faults);

        for (Placeholder p : PLACEHOLDERS)
            message = p.apply(message);

        return new PlaceholderResult(message, faults);
    }

    public static String fault() {
        faults++;
        return "?";
    }

    public record Placeholder(Pattern pattern, Function<String[], String> operator) {

        public String apply(String message) {
            Matcher matcher = pattern.matcher(message);
            while (true) {
                // Check for the next match
                if (!matcher.find())
                    return message;
                // Collect groups
                String[] groups = new String[matcher.groupCount()];
                for (int i = 0; i < groups.length; i++) {
                    groups[i] = matcher.group(i + 1);
                }
                // Replace the match
                message = message.substring(0, matcher.start()) + operator.apply(groups)
                        + message.substring(matcher.end());
            }
        }
    }

    public record PlaceholderResult(String string, int faultCount) {

    }
}
