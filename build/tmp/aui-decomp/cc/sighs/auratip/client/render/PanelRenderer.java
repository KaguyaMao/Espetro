/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferBuilder$RenderedBuffer
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.GameRenderer
 *  org.joml.Matrix4f
 */
package cc.sighs.auratip.client.render;

import cc.sighs.auratip.util.ColorUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

public class PanelRenderer {
    public static void drawRoundedPanel(GuiGraphics graphics, int x, int y, int w, int h, int topColor, int bottomColor, float radiusPixels, float smoothPixels, float alphaFactor) {
        int i;
        if (alphaFactor <= 0.0f || w <= 0 || h <= 0) {
            return;
        }
        int mid = ColorUtil.lerpColor(topColor, bottomColor, 0.5f);
        int baseColor = ColorUtil.multiplyAlpha(mid, alphaFactor);
        float r = Math.max(0.0f, radiusPixels);
        if (r <= 0.5f) {
            graphics.m_280509_(x, y, x + w, y + h, baseColor);
            return;
        }
        float maxR = (float)Math.min(w, h) / 2.0f;
        if (r > maxR) {
            r = maxR;
        }
        int part = Math.max(3, (int)(r / 3.0f + 3.0f));
        Matrix4f pose = graphics.m_280168_().m_85850_().m_252922_();
        BufferBuilder builder = Tesselator.m_85913_().m_85915_();
        builder.m_166779_(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.f_85815_);
        double piece = 1.5707963267948966 / (double)(part + 1);
        float vx = (float)x - r;
        float vy = y;
        builder.m_252986_(pose, vx, vy, 0.0f).m_193479_(baseColor).m_5752_();
        vy = y + h;
        builder.m_252986_(pose, vx, vy, 0.0f).m_193479_(baseColor).m_5752_();
        for (i = 1; i <= part; ++i) {
            float xOff = (float)(Math.cos(piece * (double)i) * (double)r);
            float yOff = (float)(Math.sin(piece * (double)i) * (double)r);
            vx = (float)x - xOff;
            vy = (float)y - yOff;
            builder.m_252986_(pose, vx, vy, 0.0f).m_193479_(baseColor).m_5752_();
            vy = (float)(y + h) + yOff;
            builder.m_252986_(pose, vx, vy, 0.0f).m_193479_(baseColor).m_5752_();
        }
        vx = x;
        vy = (float)y - r;
        builder.m_252986_(pose, vx, vy, 0.0f).m_193479_(baseColor).m_5752_();
        vy = (float)y + r + (float)h;
        builder.m_252986_(pose, vx, vy, 0.0f).m_193479_(baseColor).m_5752_();
        vx = x + w;
        vy = (float)y - r;
        builder.m_252986_(pose, vx, vy, 0.0f).m_193479_(baseColor).m_5752_();
        vy = (float)y + r + (float)h;
        builder.m_252986_(pose, vx, vy, 0.0f).m_193479_(baseColor).m_5752_();
        for (i = 1; i <= part; ++i) {
            float yOff = (float)(Math.cos(piece * (double)i) * (double)r);
            float xOff = (float)(Math.sin(piece * (double)i) * (double)r);
            vx = (float)(x + w) + xOff;
            vy = (float)y - yOff;
            builder.m_252986_(pose, vx, vy, 0.0f).m_193479_(baseColor).m_5752_();
            vy = (float)(y + h) + yOff;
            builder.m_252986_(pose, vx, vy, 0.0f).m_193479_(baseColor).m_5752_();
        }
        vx = (float)(x + w) + r;
        vy = y;
        builder.m_252986_(pose, vx, vy, 0.0f).m_193479_(baseColor).m_5752_();
        vy = y + h;
        builder.m_252986_(pose, vx, vy, 0.0f).m_193479_(baseColor).m_5752_();
        RenderSystem.setShader(GameRenderer::m_172811_);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)builder.m_231175_());
        RenderSystem.disableBlend();
    }
}

