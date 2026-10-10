/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.util.Mth
 *  org.espetro.client.aui.GuiElement
 */
package com.example.espoints.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.espetro.client.aui.GuiElement;

public class ScrollableList
extends GuiElement {
    private static final int SCROLLBAR_W = 4;
    private static final int MIN_HANDLE_H = 10;
    private static final int TRACK_COLOR = 0x35FFFFFF;
    private static final int TRACK_DISABLED_COLOR = 0x20FFFFFF;
    private static final int HANDLE_COLOR = -1325400065;
    private static final int HANDLE_DISABLED_COLOR = 0x45FFFFFF;
    private double scrollOffset = 0.0;
    private double scrollVelocity = 0.0;
    private int maxScroll = 0;
    private int scrollStep = 12;
    private boolean alwaysShowScrollbar = true;
    private boolean dirty = true;
    private long lastDraw = System.currentTimeMillis();

    public ScrollableList(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public ScrollableList setScrollStep(int scrollStep) {
        this.scrollStep = Math.max(1, scrollStep);
        return this;
    }

    public ScrollableList setAlwaysShowScrollbar(boolean alwaysShowScrollbar) {
        this.alwaysShowScrollbar = alwaysShowScrollbar;
        return this;
    }

    public void markDirty() {
        this.dirty = true;
    }

    private void recalculateBounds() {
        int totalHeight = 0;
        for (GuiElement child : this.getChildren()) {
            totalHeight = Math.max(totalHeight, child.getY() + child.getHeight());
        }
        this.maxScroll = Math.max(0, totalHeight - this.getHeight());
        this.scrollOffset = Mth.m_14008_((double)this.scrollOffset, (double)0.0, (double)this.maxScroll);
        this.dirty = false;
    }

    public void addChild(GuiElement child) {
        super.addChild(child);
        this.markDirty();
    }

    public void clearChildren() {
        super.clearChildren();
        this.markDirty();
    }

    public boolean onMouseScroll(double mouseX, double mouseY, double distance) {
        if (super.onMouseScroll(mouseX, mouseY, distance)) {
            return true;
        }
        if (this.dirty) {
            this.recalculateBounds();
        }
        if (this.maxScroll > 0 && this.hasFocus()) {
            if (Math.signum(this.scrollVelocity) != Math.signum(-distance)) {
                this.scrollVelocity = 0.0;
            }
            this.scrollVelocity -= distance * (double)this.scrollStep;
            this.scrollOffset = Mth.m_14008_((double)(this.scrollOffset - distance * (double)this.scrollStep), (double)0.0, (double)this.maxScroll);
            return true;
        }
        return false;
    }

    public void updateFocusState(int refX, int refY, int mouseX, int mouseY) {
        boolean gainFocus;
        boolean bl = gainFocus = mouseX >= this.getX() + refX && mouseX < this.getX() + refX + this.getWidth() && mouseY >= this.getY() + refY && mouseY < this.getY() + refY + this.getHeight();
        if (gainFocus != this.hasFocus) {
            this.hasFocus = gainFocus;
            if (this.hasFocus) {
                this.onFocus();
            } else {
                this.onBlur();
            }
        }
        int childRefX = this.hasFocus ? refX + this.getX() : -536870912;
        int childRefY = this.hasFocus ? refY + this.getY() - (int)this.scrollOffset : -536870912;
        for (GuiElement child : this.getChildren()) {
            if (!child.isVisible()) continue;
            child.updateFocusState(childRefX, childRefY, mouseX, mouseY);
        }
    }

    protected void drawChildren(GuiGraphics graphics, int refX, int refY, int screenWidth, int screenHeight, int mouseX, int mouseY, float opacity) {
        if (this.dirty) {
            this.recalculateBounds();
        }
        long now = System.currentTimeMillis();
        if (this.scrollVelocity != 0.0) {
            double dist = (this.scrollVelocity * 0.2 + Math.signum(this.scrollVelocity) * 1.0) * (double)(now - this.lastDraw) / 1000.0 * 50.0;
            if (Math.signum(this.scrollVelocity) != Math.signum(this.scrollVelocity - dist)) {
                dist = this.scrollVelocity;
                this.scrollVelocity = 0.0;
            } else {
                this.scrollVelocity -= dist;
            }
            this.scrollOffset = Mth.m_14008_((double)(this.scrollOffset + dist), (double)0.0, (double)this.maxScroll);
        }
        this.lastDraw = now;
        graphics.m_280588_(refX, refY, refX + this.getWidth(), refY + this.getHeight());
        super.drawChildren(graphics, refX, refY - (int)this.scrollOffset, screenWidth, screenHeight, mouseX, mouseY, opacity);
        graphics.m_280618_();
        if (this.alwaysShowScrollbar || this.maxScroll > 0) {
            int trackX = refX + this.getWidth() - 4;
            int trackY = refY;
            int trackH = this.getHeight();
            boolean scrollable = this.maxScroll > 0;
            graphics.m_280509_(trackX, trackY, trackX + 1, trackY + trackH, scrollable ? 0x35FFFFFF : 0x20FFFFFF);
            int handleH = scrollable ? Math.max(10, (int)((double)this.getHeight() / (double)(this.getHeight() + this.maxScroll) * (double)trackH)) : trackH;
            int travel = Math.max(0, trackH - handleH);
            int handleY = scrollable ? trackY + (int)(this.scrollOffset / (double)this.maxScroll * (double)travel) : trackY;
            graphics.m_280509_(trackX - 1, handleY, trackX + 4 - 1, handleY + handleH, scrollable ? -1325400065 : 0x45FFFFFF);
        }
    }
}

