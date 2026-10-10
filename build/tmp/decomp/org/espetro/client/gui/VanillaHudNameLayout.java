/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

final class VanillaHudNameLayout {
    private VanillaHudNameLayout() {
    }

    static int nameX(int slotX, int textWidth) {
        return slotX - 6 - textWidth;
    }

    static int nameY(int slotY) {
        return slotY + 7;
    }

    static int nameFade(int timer, float hotbarAlpha) {
        int fade = (int)((float)timer * 256.0f / 10.0f);
        if (fade > 255) {
            fade = 255;
        }
        if (fade <= 0) {
            return 0;
        }
        fade = (int)((float)fade * hotbarAlpha);
        return Math.max(0, fade);
    }

    static int nameBackgroundColor(int fade) {
        return (int)(192.0f * (float)fade / 255.0f) << 24;
    }
}

