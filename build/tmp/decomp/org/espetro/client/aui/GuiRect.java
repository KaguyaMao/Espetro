/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.aui;

import net.minecraft.client.gui.GuiGraphics;
import org.espetro.client.aui.GuiElement;

public class GuiRect
extends GuiElement {
    private int color;

    public GuiRect(int x, int y, int width, int height, int color) {
        super(x, y, width, height);
        this.color = color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        if (!this.isVisible()) {
            return;
        }
        if ((this.color & 0xFF000000) != 0) {
            int left = x + this.getX();
            int top = y + this.getY();
            graphics.m_280509_(left, top, left + this.getWidth(), top + this.getHeight(), this.color);
        }
        super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
    }
}

