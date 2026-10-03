/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.resources.ResourceLocation
 *  org.joml.Matrix4f
 */
package net.minecraftforge.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

@Deprecated(forRemoval=true)
public class ScreenUtils {
    public static final int DEFAULT_BACKGROUND_COLOR = -267386864;
    public static final int DEFAULT_BORDER_COLOR_START = 0x505000FF;
    public static final int DEFAULT_BORDER_COLOR_END = 1344798847;
    public static final String UNDO_CHAR = "\u21b6";
    public static final String RESET_CHAR = "\u2604";
    public static final String VALID = "\u2714";
    public static final String INVALID = "\u2715";
    public static int[] TEXT_COLOR_CODES = new int[]{0, 170, 43520, 43690, 0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA, 0x555555, 0x5555FF, 0x55FF55, 0x55FFFF, 0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF, 0, 42, 10752, 10794, 0x2A0000, 0x2A002A, 0x2A2A00, 0x2A2A2A, 0x151515, 1381695, 1392405, 1392447, 4134165, 4134207, 4144917, 0x3F3F3F};

    public static int getColorFromFormattingCharacter(char c, boolean isLighter) {
        return TEXT_COLOR_CODES[isLighter ? "0123456789abcdef".indexOf(c) : "0123456789abcdef".indexOf(c) + 16];
    }

    public static void blitWithBorder(GuiGraphics guiGraphics, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int borderSize, float zLevel) {
        ScreenUtils.blitWithBorder(guiGraphics, x, y, u, v, width, height, textureWidth, textureHeight, borderSize, borderSize, borderSize, borderSize, zLevel);
    }

    public static void blitWithBorder(GuiGraphics guiGraphics, ResourceLocation res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int borderSize, float zLevel) {
        ScreenUtils.blitWithBorder(guiGraphics, res, x, y, u, v, width, height, textureWidth, textureHeight, borderSize, borderSize, borderSize, borderSize, zLevel);
    }

    public static void blitWithBorder(GuiGraphics guiGraphics, ResourceLocation res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
        RenderSystem.setShader(GameRenderer::m_172817_);
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)res);
        ScreenUtils.blitWithBorder(guiGraphics, x, y, u, v, width, height, textureWidth, textureHeight, topBorder, bottomBorder, leftBorder, rightBorder, zLevel);
    }

    public static void blitWithBorder(GuiGraphics guiGraphics, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int fillerWidth = textureWidth - leftBorder - rightBorder;
        int fillerHeight = textureHeight - topBorder - bottomBorder;
        int canvasWidth = width - leftBorder - rightBorder;
        int canvasHeight = height - topBorder - bottomBorder;
        int xPasses = canvasWidth / fillerWidth;
        int remainderWidth = canvasWidth % fillerWidth;
        int yPasses = canvasHeight / fillerHeight;
        int remainderHeight = canvasHeight % fillerHeight;
        ScreenUtils.drawTexturedModalRect(guiGraphics, x, y, u, v, leftBorder, topBorder, zLevel);
        ScreenUtils.drawTexturedModalRect(guiGraphics, x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder, zLevel);
        ScreenUtils.drawTexturedModalRect(guiGraphics, x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder, zLevel);
        ScreenUtils.drawTexturedModalRect(guiGraphics, x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder, zLevel);
        for (int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); ++i) {
            ScreenUtils.drawTexturedModalRect(guiGraphics, x + leftBorder + i * fillerWidth, y, u + leftBorder, v, i == xPasses ? remainderWidth : fillerWidth, topBorder, zLevel);
            ScreenUtils.drawTexturedModalRect(guiGraphics, x + leftBorder + i * fillerWidth, y + topBorder + canvasHeight, u + leftBorder, v + topBorder + fillerHeight, i == xPasses ? remainderWidth : fillerWidth, bottomBorder, zLevel);
            for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); ++j) {
                ScreenUtils.drawTexturedModalRect(guiGraphics, x + leftBorder + i * fillerWidth, y + topBorder + j * fillerHeight, u + leftBorder, v + topBorder, i == xPasses ? remainderWidth : fillerWidth, j == yPasses ? remainderHeight : fillerHeight, zLevel);
            }
        }
        for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); ++j) {
            ScreenUtils.drawTexturedModalRect(guiGraphics, x, y + topBorder + j * fillerHeight, u, v + topBorder, leftBorder, j == yPasses ? remainderHeight : fillerHeight, zLevel);
            ScreenUtils.drawTexturedModalRect(guiGraphics, x + leftBorder + canvasWidth, y + topBorder + j * fillerHeight, u + leftBorder + fillerWidth, v + topBorder, rightBorder, j == yPasses ? remainderHeight : fillerHeight, zLevel);
        }
    }

    @Deprecated(forRemoval=true)
    public static void drawTexturedModalRect(GuiGraphics guiGraphics, int x, int y, int u, int v, int width, int height, float zLevel) {
        float uScale = 0.00390625f;
        float vScale = 0.00390625f;
        Tesselator tessellator = Tesselator.m_85913_();
        BufferBuilder wr = tessellator.m_85915_();
        wr.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85817_);
        Matrix4f matrix = guiGraphics.m_280168_().m_85850_().m_252922_();
        wr.m_252986_(matrix, (float)x, (float)(y + height), zLevel).m_7421_((float)u * 0.00390625f, (float)(v + height) * 0.00390625f).m_5752_();
        wr.m_252986_(matrix, (float)(x + width), (float)(y + height), zLevel).m_7421_((float)(u + width) * 0.00390625f, (float)(v + height) * 0.00390625f).m_5752_();
        wr.m_252986_(matrix, (float)(x + width), (float)y, zLevel).m_7421_((float)(u + width) * 0.00390625f, (float)v * 0.00390625f).m_5752_();
        wr.m_252986_(matrix, (float)x, (float)y, zLevel).m_7421_((float)u * 0.00390625f, (float)v * 0.00390625f).m_5752_();
        tessellator.m_85914_();
    }

    @Deprecated(forRemoval=true)
    public static void drawGradientRect(Matrix4f mat, int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
        float startAlpha = (float)(startColor >> 24 & 0xFF) / 255.0f;
        float startRed = (float)(startColor >> 16 & 0xFF) / 255.0f;
        float startGreen = (float)(startColor >> 8 & 0xFF) / 255.0f;
        float startBlue = (float)(startColor & 0xFF) / 255.0f;
        float endAlpha = (float)(endColor >> 24 & 0xFF) / 255.0f;
        float endRed = (float)(endColor >> 16 & 0xFF) / 255.0f;
        float endGreen = (float)(endColor >> 8 & 0xFF) / 255.0f;
        float endBlue = (float)(endColor & 0xFF) / 255.0f;
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::m_172811_);
        Tesselator tessellator = Tesselator.m_85913_();
        BufferBuilder buffer = tessellator.m_85915_();
        buffer.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
        buffer.m_252986_(mat, (float)right, (float)top, (float)zLevel).m_85950_(startRed, startGreen, startBlue, startAlpha).m_5752_();
        buffer.m_252986_(mat, (float)left, (float)top, (float)zLevel).m_85950_(startRed, startGreen, startBlue, startAlpha).m_5752_();
        buffer.m_252986_(mat, (float)left, (float)bottom, (float)zLevel).m_85950_(endRed, endGreen, endBlue, endAlpha).m_5752_();
        buffer.m_252986_(mat, (float)right, (float)bottom, (float)zLevel).m_85950_(endRed, endGreen, endBlue, endAlpha).m_5752_();
        tessellator.m_85914_();
        RenderSystem.disableBlend();
    }

    public static void blitInscribed(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int boundsWidth, int boundsHeight, int rectWidth, int rectHeight) {
        ScreenUtils.blitInscribed(guiGraphics, texture, x, y, boundsWidth, boundsHeight, rectWidth, rectHeight, true, true);
    }

    public static void blitInscribed(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int boundsWidth, int boundsHeight, int rectWidth, int rectHeight, boolean centerX, boolean centerY) {
        if (rectWidth * boundsHeight > rectHeight * boundsWidth) {
            int h = boundsHeight;
            boundsHeight = (int)((double)boundsWidth * ((double)rectHeight / (double)rectWidth));
            if (centerY) {
                y += (h - boundsHeight) / 2;
            }
        } else {
            int w = boundsWidth;
            boundsWidth = (int)((double)boundsHeight * ((double)rectWidth / (double)rectHeight));
            if (centerX) {
                x += (w - boundsWidth) / 2;
            }
        }
        guiGraphics.m_280411_(texture, x, y, boundsWidth, boundsHeight, 0.0f, 0.0f, rectWidth, rectHeight, rectWidth, rectHeight);
    }
}

