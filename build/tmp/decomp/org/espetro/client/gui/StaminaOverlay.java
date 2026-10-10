/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class StaminaOverlay {
    private static final int BAR_WIDTH = 40;
    private static final int BAR_HEIGHT = 2;
    private static final int BAR_BOTTOM_OFFSET = 62;
    private static boolean enabled;
    private static int stamina;
    private static int maxStamina;

    private StaminaOverlay() {
    }

    public static void update(boolean newEnabled, int newStamina, int newMaxStamina, int newJumpStaminaCost) {
        enabled = newEnabled;
        maxStamina = Math.max(0, newMaxStamina);
        stamina = Math.max(0, Math.min(newStamina, maxStamina));
    }

    public static boolean isExhausted() {
        return enabled && stamina <= 0;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    static void drawElement(GuiGraphics graphics, Minecraft mc) {
        if (!enabled || maxStamina <= 0 || stamina >= maxStamina || mc.f_91066_.f_92062_ || mc.f_91074_ == null) {
            return;
        }
        int filledWidth = Math.round(40.0f * ((float)stamina / (float)maxStamina));
        if (stamina > 0) {
            filledWidth = Math.max(1, filledWidth);
        }
        int x = (graphics.m_280182_() - 40) / 2;
        int y = Math.max(0, graphics.m_280206_() - 62);
        graphics.m_280509_(x, y, x + filledWidth, y + 2, -419430401);
    }
}

