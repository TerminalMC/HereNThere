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

package dev.terminalmc.herenthere.mixin;

import dev.terminalmc.herenthere.HereNThere;
import io.github.darkkronicle.advancedchatcore.chat.AdvancedTextField;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
        targets = "io.github.darkkronicle.advancedchatcore.chat.AdvancedChatScreen",
        remap = false
)
public class AdvancedChatScreenMixin {

    @Shadow
    protected AdvancedTextField chatField;

    @Inject(
            method = "method_25404",
            at = @At("HEAD")
    )
    private void onKeyPressed(
            int keyCode,
            int scanCode,
            int modifiers,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (keyCode == GLFW.GLFW_KEY_TAB) {
            String val = chatField.getValue();
            String before = chatField.getValue();
            String after = "";
            int cursor = chatField.getCursorPosition();
            if (cursor >= 0 && cursor < val.length()) {
                before = val.substring(0, cursor);
                after = val.substring(cursor);
            }
            @Nullable String newVal = HereNThere.onTabKey(before, after);
            if (newVal != null) {
                chatField.setValue(newVal);
            }
        }
    }
}
