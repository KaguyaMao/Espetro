/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Renderable
 *  net.minecraft.client.gui.components.events.AbstractContainerEventHandler
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarratableEntry
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.resources.ResourceLocation
 */
package net.minecraftforge.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public abstract class ScrollPanel
extends AbstractContainerEventHandler
implements Renderable,
NarratableEntry {
    private final Minecraft client;
    protected final int width;
    protected final int height;
    protected final int top;
    protected final int bottom;
    protected final int right;
    protected final int left;
    private boolean scrolling;
    protected float scrollDistance;
    protected boolean captureMouse = true;
    protected final int border;
    private final int barWidth;
    private final int barLeft;
    private final int bgColorFrom;
    private final int bgColorTo;
    private final int barBgColor;
    private final int barColor;
    private final int barBorderColor;

    public ScrollPanel(Minecraft client, int width, int height, int top, int left) {
        this(client, width, height, top, left, 4);
    }

    public ScrollPanel(Minecraft client, int width, int height, int top, int left, int border) {
        this(client, width, height, top, left, border, 6);
    }

    public ScrollPanel(Minecraft client, int width, int height, int top, int left, int border, int barWidth) {
        this(client, width, height, top, left, border, barWidth, -1072689136, -804253680);
    }

    public ScrollPanel(Minecraft client, int width, int height, int top, int left, int border, int barWidth, int bgColor) {
        this(client, width, height, top, left, border, barWidth, bgColor, bgColor);
    }

    public ScrollPanel(Minecraft client, int width, int height, int top, int left, int border, int barWidth, int bgColorFrom, int bgColorTo) {
        this(client, width, height, top, left, border, barWidth, bgColorFrom, bgColorTo, -16777216, -8355712, -4144960);
    }

    public ScrollPanel(Minecraft client, int width, int height, int top, int left, int border, int barWidth, int bgColorFrom, int bgColorTo, int barBgColor, int barColor, int barBorderColor) {
        this.client = client;
        this.width = width;
        this.height = height;
        this.top = top;
        this.left = left;
        this.bottom = height + this.top;
        this.right = width + this.left;
        this.barLeft = this.left + this.width - barWidth;
        this.border = border;
        this.barWidth = barWidth;
        this.bgColorFrom = bgColorFrom;
        this.bgColorTo = bgColorTo;
        this.barBgColor = barBgColor;
        this.barColor = barColor;
        this.barBorderColor = barBorderColor;
    }

    protected abstract int getContentHeight();

    protected void drawBackground(GuiGraphics guiGraphics, Tesselator tess, float partialTick) {
        BufferBuilder worldr = tess.m_85915_();
        if (this.client.f_91073_ != null) {
            this.drawGradientRect(guiGraphics, this.left, this.top, this.right, this.bottom, this.bgColorFrom, this.bgColorTo);
        } else {
            RenderSystem.setShader(GameRenderer::m_172820_);
            RenderSystem.setShaderTexture((int)0, (ResourceLocation)Screen.f_279548_);
            float texScale = 32.0f;
            worldr.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85819_);
            worldr.m_5483_((double)this.left, (double)this.bottom, 0.0).m_7421_((float)this.left / 32.0f, (float)(this.bottom + (int)this.scrollDistance) / 32.0f).m_6122_(32, 32, 32, 255).m_5752_();
            worldr.m_5483_((double)this.right, (double)this.bottom, 0.0).m_7421_((float)this.right / 32.0f, (float)(this.bottom + (int)this.scrollDistance) / 32.0f).m_6122_(32, 32, 32, 255).m_5752_();
            worldr.m_5483_((double)this.right, (double)this.top, 0.0).m_7421_((float)this.right / 32.0f, (float)(this.top + (int)this.scrollDistance) / 32.0f).m_6122_(32, 32, 32, 255).m_5752_();
            worldr.m_5483_((double)this.left, (double)this.top, 0.0).m_7421_((float)this.left / 32.0f, (float)(this.top + (int)this.scrollDistance) / 32.0f).m_6122_(32, 32, 32, 255).m_5752_();
            tess.m_85914_();
        }
    }

    protected abstract void drawPanel(GuiGraphics var1, int var2, int var3, Tesselator var4, int var5, int var6);

    protected boolean clickPanel(double mouseX, double mouseY, int button) {
        return false;
    }

    private int getMaxScroll() {
        return this.getContentHeight() - (this.height - this.border);
    }

    private void applyScrollLimits() {
        int max = this.getMaxScroll();
        if (max < 0) {
            max /= 2;
        }
        if (this.scrollDistance < 0.0f) {
            this.scrollDistance = 0.0f;
        }
        if (this.scrollDistance > (float)max) {
            this.scrollDistance = max;
        }
    }

    public boolean m_6050_(double mouseX, double mouseY, double scroll) {
        if (scroll != 0.0) {
            this.scrollDistance = (float)((double)this.scrollDistance + -scroll * (double)this.getScrollAmount());
            this.applyScrollLimits();
            return true;
        }
        return false;
    }

    protected int getScrollAmount() {
        return 20;
    }

    public boolean m_5953_(double mouseX, double mouseY) {
        return mouseX >= (double)this.left && mouseX <= (double)(this.left + this.width) && mouseY >= (double)this.top && mouseY <= (double)this.bottom;
    }

    public boolean m_6375_(double mouseX, double mouseY, int button) {
        if (super.m_6375_(mouseX, mouseY, button)) {
            return true;
        }
        boolean bl = this.scrolling = button == 0 && mouseX >= (double)this.barLeft && mouseX < (double)(this.barLeft + this.barWidth);
        if (this.scrolling) {
            return true;
        }
        int mouseListY = (int)mouseY - this.top - this.getContentHeight() + (int)this.scrollDistance - this.border;
        if (mouseX >= (double)this.left && mouseX <= (double)this.right && mouseListY < 0) {
            return this.clickPanel(mouseX - (double)this.left, mouseY - (double)this.top + (double)((int)this.scrollDistance) - (double)this.border, button);
        }
        return false;
    }

    public boolean m_6348_(double mouseX, double mouseY, int button) {
        if (super.m_6348_(mouseX, mouseY, button)) {
            return true;
        }
        boolean ret = this.scrolling;
        this.scrolling = false;
        return ret;
    }

    private int getBarHeight() {
        int barHeight = this.height * this.height / this.getContentHeight();
        if (barHeight < 32) {
            barHeight = 32;
        }
        if (barHeight > this.height - this.border * 2) {
            barHeight = this.height - this.border * 2;
        }
        return barHeight;
    }

    public boolean m_7979_(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrolling) {
            int maxScroll = this.height - this.getBarHeight();
            double moved = deltaY / (double)maxScroll;
            this.scrollDistance = (float)((double)this.scrollDistance + (double)this.getMaxScroll() * moved);
            this.applyScrollLimits();
            return true;
        }
        return false;
    }

    public void m_88315_(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Tesselator tess = Tesselator.m_85913_();
        BufferBuilder worldr = tess.m_85915_();
        double scale = this.client.m_91268_().m_85449_();
        RenderSystem.enableScissor((int)((int)((double)this.left * scale)), (int)((int)((double)this.client.m_91268_().m_85442_() - (double)this.bottom * scale)), (int)((int)((double)this.width * scale)), (int)((int)((double)this.height * scale)));
        this.drawBackground(guiGraphics, tess, partialTick);
        int baseY = this.top + this.border - (int)this.scrollDistance;
        this.drawPanel(guiGraphics, this.right, baseY, tess, mouseX, mouseY);
        RenderSystem.disableDepthTest();
        int extraHeight = this.getContentHeight() + this.border - this.height;
        if (extraHeight > 0) {
            int barHeight = this.getBarHeight();
            int barTop = (int)this.scrollDistance * (this.height - barHeight) / extraHeight + this.top;
            if (barTop < this.top) {
                barTop = this.top;
            }
            int barBgAlpha = this.barBgColor >> 24 & 0xFF;
            int barBgRed = this.barBgColor >> 16 & 0xFF;
            int barBgGreen = this.barBgColor >> 8 & 0xFF;
            int barBgBlue = this.barBgColor & 0xFF;
            RenderSystem.setShader(GameRenderer::m_172811_);
            worldr.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
            worldr.m_5483_((double)this.barLeft, (double)this.bottom, 0.0).m_6122_(barBgRed, barBgGreen, barBgBlue, barBgAlpha).m_5752_();
            worldr.m_5483_((double)(this.barLeft + this.barWidth), (double)this.bottom, 0.0).m_6122_(barBgRed, barBgGreen, barBgBlue, barBgAlpha).m_5752_();
            worldr.m_5483_((double)(this.barLeft + this.barWidth), (double)this.top, 0.0).m_6122_(barBgRed, barBgGreen, barBgBlue, barBgAlpha).m_5752_();
            worldr.m_5483_((double)this.barLeft, (double)this.top, 0.0).m_6122_(barBgRed, barBgGreen, barBgBlue, barBgAlpha).m_5752_();
            tess.m_85914_();
            int barAlpha = this.barColor >> 24 & 0xFF;
            int barRed = this.barColor >> 16 & 0xFF;
            int barGreen = this.barColor >> 8 & 0xFF;
            int barBlue = this.barColor & 0xFF;
            worldr.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
            worldr.m_5483_((double)this.barLeft, (double)(barTop + barHeight), 0.0).m_6122_(barRed, barGreen, barBlue, barAlpha).m_5752_();
            worldr.m_5483_((double)(this.barLeft + this.barWidth), (double)(barTop + barHeight), 0.0).m_6122_(barRed, barGreen, barBlue, barAlpha).m_5752_();
            worldr.m_5483_((double)(this.barLeft + this.barWidth), (double)barTop, 0.0).m_6122_(barRed, barGreen, barBlue, barAlpha).m_5752_();
            worldr.m_5483_((double)this.barLeft, (double)barTop, 0.0).m_6122_(barRed, barGreen, barBlue, barAlpha).m_5752_();
            tess.m_85914_();
            int barBorderAlpha = this.barBorderColor >> 24 & 0xFF;
            int barBorderRed = this.barBorderColor >> 16 & 0xFF;
            int barBorderGreen = this.barBorderColor >> 8 & 0xFF;
            int barBorderBlue = this.barBorderColor & 0xFF;
            worldr.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
            worldr.m_5483_((double)this.barLeft, (double)(barTop + barHeight - 1), 0.0).m_6122_(barBorderRed, barBorderGreen, barBorderBlue, barBorderAlpha).m_5752_();
            worldr.m_5483_((double)(this.barLeft + this.barWidth - 1), (double)(barTop + barHeight - 1), 0.0).m_6122_(barBorderRed, barBorderGreen, barBorderBlue, barBorderAlpha).m_5752_();
            worldr.m_5483_((double)(this.barLeft + this.barWidth - 1), (double)barTop, 0.0).m_6122_(barBorderRed, barBorderGreen, barBorderBlue, barBorderAlpha).m_5752_();
            worldr.m_5483_((double)this.barLeft, (double)barTop, 0.0).m_6122_(barBorderRed, barBorderGreen, barBorderBlue, barBorderAlpha).m_5752_();
            tess.m_85914_();
        }
        RenderSystem.disableBlend();
        RenderSystem.disableScissor();
    }

    protected void drawGradientRect(GuiGraphics guiGraphics, int left, int top, int right, int bottom, int color1, int color2) {
        guiGraphics.m_280024_(left, top, right, bottom, color1, color2);
    }

    public List<? extends GuiEventListener> m_6702_() {
        return Collections.emptyList();
    }
}

