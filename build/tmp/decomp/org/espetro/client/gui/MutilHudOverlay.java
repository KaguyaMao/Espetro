/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  se.mickelus.mutil.gui.GuiElement
 */
package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.espetro.client.gui.FobSupplyHud;
import org.espetro.client.gui.FortificationProgressHud;
import org.espetro.client.gui.OutpostSupplyHud;
import org.espetro.client.gui.StaminaOverlay;
import se.mickelus.mutil.gui.GuiElement;

public final class MutilHudOverlay {
    private static GuiElement root;
    private static int width;
    private static int height;

    private MutilHudOverlay() {
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
            root.addChild((GuiElement)new StaminaElement());
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

    private static final class StaminaElement
    extends GuiElement {
        private StaminaElement() {
            super(0, 0, 1, 1);
        }

        public void draw(GuiGraphics graphics, int x, int y, int drawWidth, int drawHeight, int mouseX, int mouseY, float partialTick) {
            StaminaOverlay.drawElement(graphics, Minecraft.m_91087_());
            super.draw(graphics, x, y, drawWidth, drawHeight, mouseX, mouseY, partialTick);
        }
    }
}

