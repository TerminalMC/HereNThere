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
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dev.terminalmc.herenthere.placeholder.Placeholders.fault;

public class PlayerPositionUtil {

    private static @Nullable Vec3 playerPos;
    private static @Nullable Vec3 cameraPos;
    private static @Nullable BlockPos playerBlockPos;
    private static @Nullable BlockPos cameraBlockPos;
    private static @Nullable BlockPos playerLookBlockPos;
    private static @Nullable BlockPos cameraLookBlockPos;
    private static @Nullable Vec3 playerLookAngle;
    private static @Nullable Vec3 cameraLookAngle;
    private static @Nullable Vec2 playerRotation;
    private static @Nullable Vec2 cameraRotation;

    public static void reset() {
        playerPos = null;
        cameraPos = null;
        playerBlockPos = null;
        cameraBlockPos = null;
        playerLookBlockPos = null;
        cameraLookBlockPos = null;
        playerLookAngle = null;
        cameraLookAngle = null;
        playerRotation = null;
        cameraRotation = null;
    }

    private static @NotNull Vec3 getPlayerPos() {
        if (playerPos == null) {
            playerPos = Minecraft.getInstance().player.position();
        }
        return playerPos;
    }

    private static @NotNull Vec3 getCameraPos() {
        if (cameraPos == null) {
            cameraPos = Minecraft.getInstance().getCameraEntity().position();
        }
        return cameraPos;
    }

    private static @NotNull BlockPos getPlayerBlockPos() {
        if (playerBlockPos == null) {
            playerBlockPos = Minecraft.getInstance().player.blockPosition();
        }
        return playerBlockPos;
    }

    private static @NotNull BlockPos getCameraBlockPos() {
        if (cameraBlockPos == null) {
            cameraBlockPos = Minecraft.getInstance().getCameraEntity().blockPosition();
        }
        return cameraBlockPos;
    }

    private static @Nullable BlockPos getPlayerLookBlockPos() {
        if (playerLookBlockPos == null) {
            Minecraft mc = Minecraft.getInstance();
            // Distance is arbitrary but will do for now
            HitResult result = mc.player.pick(
                    Math.max(384, (mc.levelRenderer.getLastViewDistance() + 1D) * 16),
                    0.0F,
                    false
            );
            if (result.getType().equals(HitResult.Type.BLOCK)) {
                playerLookBlockPos = ((BlockHitResult) result).getBlockPos();
            }
        }
        return playerLookBlockPos;
    }

    private static @Nullable BlockPos getCameraLookBlockPos() {
        if (cameraLookBlockPos == null) {
            Minecraft mc = Minecraft.getInstance();
            // Distance is arbitrary but will do for now
            HitResult result = mc.getCameraEntity().pick(
                    Math.max(384, (mc.levelRenderer.getLastViewDistance() + 1D) * 16),
                    0.0F,
                    false
            );
            if (result.getType().equals(HitResult.Type.BLOCK)) {
                cameraLookBlockPos = ((BlockHitResult) result).getBlockPos();
            }
        }
        return cameraLookBlockPos;
    }

    private static @NotNull Vec3 getPlayerLookAngle() {
        if (playerLookAngle == null) {
            playerLookAngle = Minecraft.getInstance().player.getLookAngle();
        }
        return playerLookAngle;
    }

    private static @NotNull Vec3 getCameraLookAngle() {
        if (cameraLookAngle == null) {
            cameraLookAngle = Minecraft.getInstance().getCameraEntity().getLookAngle();
        }
        return cameraLookAngle;
    }

    private static @NotNull Vec2 getPlayerRotation() {
        if (playerRotation == null) {
            playerRotation = Minecraft.getInstance().player.getRotationVector();
        }
        return playerRotation;
    }

    private static @NotNull Vec2 getCameraRotation() {
        if (cameraRotation == null) {
            cameraRotation = Minecraft.getInstance().getCameraEntity().getRotationVector();
        }
        return cameraRotation;
    }

    public static String getPosString(String[] groups) {
        if (groups.length != 7)
            return fault();

        boolean camera = groups[0].equals("c");
        boolean look = groups[1].equals("l");
        boolean decimal = groups[2].equals("d");
        String delimiter = groups[3];
        double leftCaret = groups[4] == null ? 0D : Double.parseDouble(groups[4]);
        double upCaret = groups[5] == null ? 0D : Double.parseDouble(groups[5]);
        double forwardsCaret = groups[6] == null ? 0D : Double.parseDouble(groups[6]);

        Vec3 pos;
        if (look) {
            BlockPos lookPos = camera ? getCameraLookBlockPos() : getPlayerLookBlockPos();
            pos = lookPos == null ? null : lookPos.getBottomCenter();
            if (pos == null)
                return fault();
        } else if (decimal) {
            pos = camera ? getCameraPos() : getPlayerPos();
        } else {
            pos = (camera ? getCameraBlockPos() : getPlayerBlockPos()).getBottomCenter();
        }

        pos = applyCaret(camera, pos, leftCaret, upCaret, forwardsCaret);

        if (decimal) {
            return String.format("%f%s%f%s%f", pos.x, delimiter, pos.y, delimiter, pos.z);
        } else {
            return String.format(
                    "%d%s%d%s%d",
                    Mth.floor(pos.x),
                    delimiter,
                    Mth.floor(pos.y),
                    delimiter,
                    Mth.floor(pos.z)
            );
        }
    }

    private static Vec3 applyCaret(
            boolean camera,
            Vec3 pos,
            double left,
            double up,
            double forwards
    ) {
        Vec2 rot = camera ? getCameraRotation() : getPlayerRotation();
        float f = Mth.cos((rot.y + 90.0D) * (Math.PI / 180.0D));
        float g = Mth.sin((rot.y + 90.0D) * (Math.PI / 180.0D));
        float h = Mth.cos(-rot.x * (Math.PI / 180.0D));
        float i = Mth.sin(-rot.x * (Math.PI / 180.0D));
        float j = Mth.cos((-rot.x + 90.0D) * (Math.PI / 180.0D));
        float k = Mth.sin((-rot.x + 90.0D) * (Math.PI / 180.0D));
        Vec3 vec32 = new Vec3(f * h, i, g * h);
        Vec3 vec33 = new Vec3(f * j, k, g * j);
        Vec3 vec34 = vec32.cross(vec33).scale(-1.0D);
        double d = vec32.x * forwards + vec33.x * up + vec34.x * left;
        double e = vec32.y * forwards + vec33.y * up + vec34.y * left;
        double l = vec32.z * forwards + vec33.z * up + vec34.z * left;
        return new Vec3(pos.x + d, pos.y + e, pos.z + l);
    }

    public static String getPosComponentString(String[] groups) {
        if (groups.length != 6)
            return fault();

        boolean camera = groups[0].equals("c");
        boolean look = groups[1].equals("l");
        String component = groups[2];
        boolean decimal = groups[3].equals("d");
        @Nullable String operator = groups[4];
        double operand = groups[5] == null ? 0D : Double.parseDouble(groups[5]);

        Vec3 pos;
        if (look) {
            BlockPos lookPos = camera ? getCameraLookBlockPos() : getPlayerLookBlockPos();
            pos = lookPos == null ? null : lookPos.getBottomCenter();
            if (pos == null)
                return fault();
        } else if (decimal) {
            pos = camera ? getCameraPos() : getPlayerPos();
        } else {
            pos = (camera ? getCameraBlockPos() : getPlayerBlockPos()).getBottomCenter();
        }

        double value = switch (component) {
            case "x" -> pos.x;
            case "y" -> pos.y;
            case "z" -> pos.z;
            default -> throw new IllegalArgumentException();
        };

        if (operator != null) {
            value = switch (operator) {
                case "+" -> value + operand;
                case "-" -> value - operand;
                case "*" -> value * operand;
                case "/" -> value / operand;
                default -> throw new IllegalArgumentException();
            };
        }

        if (decimal) {
            return String.format("%f", value);
        } else {
            return String.format("%d", Mth.floor(value));
        }
    }

    public static String getFacingAngleString(String[] groups) {
        if (groups.length != 4)
            return fault();

        boolean camera = groups[0].equals("c");
        String delimiter = groups[1];
        @Nullable String operator = groups[2];
        double operand = groups[3] == null ? 0D : Double.parseDouble(groups[3]);

        Vec3 vec = camera ? getCameraLookAngle() : getPlayerLookAngle();

        if (operator != null) {
            vec = switch (operator) {
                case "+" -> vec.add(vec.scale(operand));
                case "-" -> vec.subtract(vec.scale(operand));
                case "*" -> vec.scale(operand);
                case "/" -> vec.scale(1D / operand);
                default -> throw new IllegalArgumentException();
            };
        }

        return String.format("%f%s%f%s%f", vec.x, delimiter, vec.y, delimiter, vec.z);
    }
}
