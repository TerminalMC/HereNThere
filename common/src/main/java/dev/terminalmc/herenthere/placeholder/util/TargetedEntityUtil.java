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

package dev.terminalmc.herenthere.placeholder.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

import static dev.terminalmc.herenthere.placeholder.Placeholders.fault;

public class TargetedEntityUtil {

    private static @Nullable UUID playerLookEntityId;
    private static @Nullable UUID cameraLookEntityId;

    public static void reset() {
        playerLookEntityId = null;
        cameraLookEntityId = null;
    }

    private static @Nullable UUID getPlayerLookEntityId() {
        if (playerLookEntityId == null) {
            Minecraft mc = Minecraft.getInstance();
            // Distance is arbitrary but will do for now
            Optional<Entity> target = DebugRenderer.getTargetedEntity(
                    mc.player,
                    (int) Math.max(384, (mc.levelRenderer.getLastViewDistance() + 1D) * 16)
            );
            target.ifPresent(entity -> playerLookEntityId = entity.getUUID());
        }
        return playerLookEntityId;
    }

    private static @Nullable UUID getCameraLookEntityId() {
        if (cameraLookEntityId == null) {
            Minecraft mc = Minecraft.getInstance();
            // Distance is arbitrary but will do for now
            Optional<Entity> target = DebugRenderer.getTargetedEntity(
                    mc.getCameraEntity(),
                    (int) Math.max(384, (mc.levelRenderer.getLastViewDistance() + 1D) * 16)
            );
            target.ifPresent(entity -> cameraLookEntityId = entity.getUUID());
        }
        return cameraLookEntityId;
    }

    public static String getTargetEntityId(String[] groups) {
        if (groups.length != 1)
            return fault();

        boolean camera = groups[0].equals("c");

        UUID uuid = camera ? getCameraLookEntityId() : getPlayerLookEntityId();

        if (uuid == null)
            return fault();

        return uuid.toString();
    }
}
