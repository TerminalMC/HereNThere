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
    private static @Nullable BlockPos playerBlockPos;
    private static @Nullable BlockPos lookBlockPos;
    private static @Nullable Vec3 lookAngle;
    private static @Nullable Vec2 rotation;

    public static void reset() {
        playerPos = null;
        playerBlockPos = null;
        lookBlockPos = null;
        lookAngle = null;
        rotation = null;
    }

    private static @NotNull Vec3 getPlayerPos() {
        if (playerPos == null) {
            playerPos = Minecraft.getInstance().player.position();
        }
        return playerPos;
    }

    private static @NotNull BlockPos getPlayerBlockPos() {
        if (playerBlockPos == null) {
            playerBlockPos = Minecraft.getInstance().player.blockPosition();
        }
        return playerBlockPos;
    }

    private static @Nullable BlockPos getLookBlockPos() {
        if (lookBlockPos == null) {
            Minecraft mc = Minecraft.getInstance();
            // Distance is arbitrary but will do for now
            HitResult result = mc.player.pick(
                    Math.max(384, (mc.levelRenderer.getLastViewDistance() + 1D) * 16),
                    0.0F,
                    false
            );
            if (result.getType().equals(HitResult.Type.BLOCK)) {
                lookBlockPos = ((BlockHitResult) result).getBlockPos();
            }
        }
        return lookBlockPos;
    }

    private static @NotNull Vec3 getLookAngle() {
        if (lookAngle == null) {
            lookAngle = Minecraft.getInstance().player.getLookAngle();
        }
        return lookAngle;
    }

    private static @NotNull Vec2 getRotation() {
        if (rotation == null) {
            rotation = Minecraft.getInstance().player.getRotationVector();
        }
        return rotation;
    }

    public static String getPosString(String[] groups) {
        if (groups.length != 6)
            return fault();

        boolean look = groups[0].equals("l");
        boolean decimal = groups[1].equals("d");
        String delimiter = groups[2];
        double leftCaret = groups[3] == null ? 0D : Double.parseDouble(groups[3]);
        double upCaret = groups[4] == null ? 0D : Double.parseDouble(groups[4]);
        double forwardsCaret = groups[5] == null ? 0D : Double.parseDouble(groups[5]);

        Vec3 pos;
        if (look) {
            BlockPos lookPos = getLookBlockPos();
            pos = lookPos == null ? null : lookPos.getBottomCenter();
            if (pos == null)
                return fault();
        } else if (decimal) {
            pos = getPlayerPos();
        } else {
            pos = getPlayerBlockPos().getBottomCenter();
        }

        pos = applyCaret(pos, leftCaret, upCaret, forwardsCaret);

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

    private static Vec3 applyCaret(Vec3 pos, double left, double up, double forwards) {
        Vec2 rot = getRotation();
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
        if (groups.length != 5)
            return fault();

        boolean look = groups[0].equals("l");
        String component = groups[1];
        boolean decimal = groups[2].equals("d");
        @Nullable String operator = groups[3];
        double operand = groups[4] == null ? 0D : Double.parseDouble(groups[4]);

        Vec3 pos;
        if (look) {
            BlockPos lookPos = getLookBlockPos();
            pos = lookPos == null ? null : lookPos.getBottomCenter();
            if (pos == null)
                return fault();
        } else if (decimal) {
            pos = getPlayerPos();
        } else {
            pos = getPlayerBlockPos().getBottomCenter();
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
        if (groups.length != 3)
            return fault();

        String delimiter = groups[0];
        @Nullable String operator = groups[1];
        double operand = groups[2] == null ? 0D : Double.parseDouble(groups[2]);

        Vec3 vec = getLookAngle();

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
