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

package dev.terminalmc.herenthere.config;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.terminalmc.herenthere.util.Localization.localized;

public class Alias {

    public static final String DATA_FORMAT = "%s ||| %s";
    public static final String DATA_PATTERN_STRING = "^(.*) \\|\\|\\| (.*)$";
    public static final Pattern DATA_PATTERN = Pattern.compile(DATA_PATTERN_STRING);

    public String alias;
    public String replacement;

    public Alias(String alias, String replacement) {
        this.alias = alias;
        this.replacement = replacement;
    }

    public String toDataString() {
        return String.format(
                DATA_FORMAT,
                alias,
                replacement
        );
    }

    public static Alias fromDataString(String dataString) throws ParseException {
        dataString = dataString.strip();

        Matcher matcher = DATA_PATTERN.matcher(dataString);
        if (!matcher.matches()) {
            throw new ParseException(
                    localized("error", "alias.pattern", DATA_PATTERN_STRING).getString(),
                    0
            );
        }

        String alias = matcher.group(1);
        String replacement = matcher.group(2);

        return new Alias(alias, replacement);
    }
}
