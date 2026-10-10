/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.Objects;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.EspetroAuiWidgets;

final class SquadNameField
extends GuiElement {
    private static final int MAX_LENGTH = 18;
    private static final float TEXT_SCALE = 0.72f;
    private final String placeholder;
    private final Runnable submit;
    private String value = "";
    private boolean active = false;

    SquadNameField(int x, int y, int width, int height, String placeholder, Runnable submit) {
        super(x, y, width, height);
        this.placeholder = placeholder == null ? "" : placeholder;
        this.submit = submit;
    }

    String getValue() {
        return this.value.trim();
    }

    void clear() {
        this.value = "";
    }

    @Override
    public boolean onMouseClick(int mouseX, int mouseY, int button) {
        if (button != 0 || !this.isVisible()) {
            return false;
        }
        this.active = this.hasFocus();
        return this.active;
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (!this.active) {
            return false;
        }
        if (keyCode == 257 || keyCode == 335) {
            if (this.submit != null) {
                this.submit.run();
            }
            return true;
        }
        if (keyCode == 259 && !this.value.isEmpty()) {
            int cut = this.value.offsetByCodePoints(this.value.length(), -1);
            this.value = this.value.substring(0, cut);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCharType(char codePoint, int modifiers) {
        if (!this.active || this.value.length() >= 18 || !SharedConstants.m_136188_(codePoint)) {
            return false;
        }
        this.value = this.value + codePoint;
        return true;
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        if (!this.isVisible()) {
            return;
        }
        int bx = x + this.getX();
        int by = y + this.getY();
        graphics.m_280509_(bx, by, bx + this.getWidth(), by + this.getHeight(), this.active ? -263174092 : -532660160);
        graphics.m_280637_(bx, by, this.getWidth(), this.getHeight(), this.active ? -1525668 : -10788256);
        String drawn = this.value.isEmpty() ? this.placeholder : this.value;
        int color = this.value.isEmpty() ? -5327681 : -1;
        int logicalTextWidth = Math.max(8, (int)((float)(this.getWidth() - 6) / 0.72f));
        String trimmed = Minecraft.m_91087_().f_91062_.m_92834_(drawn, logicalTextWidth);
        Objects.requireNonNull(Minecraft.m_91087_().f_91062_);
        int textHeight = Math.max(1, Math.round(9.0f * 0.72f));
        EspetroAuiWidgets.drawScaledString(graphics, trimmed, bx + 3, by + Math.max(1, (this.getHeight() - textHeight) / 2), color, 0.72f);
        if (this.active && !this.value.isEmpty() && System.currentTimeMillis() / 500L % 2L == 0L) {
            int textW = Math.round((float)Minecraft.m_91087_().f_91062_.m_92895_(trimmed) * 0.72f);
            int cursorX = Math.min(bx + this.getWidth() - 3, bx + 3 + textW + 1);
            graphics.m_280509_(cursorX, by + 2, cursorX + 1, by + this.getHeight() - 2, -1);
        }
    }
}

