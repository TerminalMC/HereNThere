/*
 * Copyright 2026 TerminalMC
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

import com.mojang.blaze3d.platform.InputConstants;
import dev.terminalmc.herenthere.HereNThere;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.KeyEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {

    @Shadow
    protected EditBox input;

    @Inject(
            method = "keyPressed",
            at = @At("HEAD")
    )
    private void onKeyPressed(
            KeyEvent event,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (event.key() == InputConstants.KEY_TAB) {
            String val = input.getValue();
            String before = input.getValue();
            String after = "";
            int cursor = input.getCursorPosition();
            if (cursor >= 0 && cursor < val.length()) {
                before = val.substring(0, cursor);
                after = val.substring(cursor);
            }
            @Nullable String newVal = HereNThere.onTabKey(before, after);
            if (newVal != null) {
                input.setValue(newVal);
            }
        }
    }
}
