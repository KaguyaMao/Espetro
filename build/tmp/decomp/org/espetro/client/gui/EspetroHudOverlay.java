/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.FobSupplyHud;
import org.espetro.client.gui.FortificationProgressHud;
import org.espetro.client.gui.OutpostSupplyHud;

public final class EspetroHudOverlay {
    private static GuiElement root;
    private static int width;
    private static int height;

    private EspetroHudOverlay() {
    }

    public static void render(GuiGraphics graphics, Minecraft minecraft, float partialTick) {
        if (minecraft == null || minecraft.f_91073_ == null || minecraft.f_91066_.f_92062_) {
            return;
        }
        int nextWidth = graphics.m_280182_();
        int nextHeight = graphics.m_280206_();
        if (root == null || width != nextWidth || height != nextHeight) {
            width = nextWidth;
            height = nextHeight;
            root = new GuiElement(0, 0, width, height);
            root.addChild(FobSupplyHud.createElement());
            root.addChild(OutpostSupplyHud.createElement());
            root.addChild(FortificationProgressHud.createElement());
        }
        root.draw(graphics, 0, 0, width, height, -1, -1, partialTick);
    }

    static {
        width = -1;
        height = -1;
    }
}

