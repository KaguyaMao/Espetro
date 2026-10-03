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
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.util.Mth
 *  org.joml.Matrix4f
 */
package cc.sighs.auratip.client.render;

import cc.sighs.auratip.handler.AuraShaders;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class RingRenderer {
    private RingRenderer() {
    }

    public static void drawRing(GuiGraphics gg, int cx, int cy, float innerRadius, float outerRadius, float startDeg, float endDeg, int innerColor, int outerColor, float smoothPixels) {
        RingRenderer.drawRing(gg, cx, cy, innerRadius, outerRadius, startDeg, endDeg, innerColor, outerColor, smoothPixels, 1.0f);
    }

    public static void drawRing(GuiGraphics gg, int cx, int cy, float innerRadius, float outerRadius, float startDeg, float endDeg, int innerColor, int outerColor, float smoothPixels, float fill) {
        if (outerRadius < 0.1f) {
            return;
        }
        ShaderInstance shader = AuraShaders.getRadialRing();
        if (shader == null) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(() -> shader);
        float x1 = (float)cx - outerRadius;
        float y1 = (float)cy - outerRadius;
        float x2 = (float)cx + outerRadius;
        float y2 = (float)cy + outerRadius;
        float innerR = Mth.m_14036_((float)(innerRadius / (2.0f * outerRadius)), (float)0.0f, (float)0.5f);
        float outerR = 0.5f;
        shader.m_173356_("uInnerRadius").m_5985_(innerR);
        shader.m_173356_("uOuterRadius").m_5985_(outerR);
        shader.m_173356_("uStartAngle").m_5985_(startDeg);
        shader.m_173356_("uEndAngle").m_5985_(endDeg);
        shader.m_173356_("uSmooth").m_5985_(smoothPixels / (2.0f * outerRadius));
        shader.m_173356_("uFill").m_5985_(fill);
        RingRenderer.setColor(shader, "uInnerColor", innerColor);
        RingRenderer.setColor(shader, "uOuterColor", outerColor);
        Matrix4f mat = gg.m_280168_().m_85850_().m_252922_();
        BufferBuilder buf = Tesselator.m_85913_().m_85915_();
        buf.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85817_);
        buf.m_252986_(mat, x1, y1, 0.0f).m_7421_(0.0f, 0.0f).m_5752_();
        buf.m_252986_(mat, x1, y2, 0.0f).m_7421_(0.0f, 1.0f).m_5752_();
        buf.m_252986_(mat, x2, y2, 0.0f).m_7421_(1.0f, 1.0f).m_5752_();
        buf.m_252986_(mat, x2, y1, 0.0f).m_7421_(1.0f, 0.0f).m_5752_();
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)buf.m_231175_());
    }

    private static void setColor(ShaderInstance shader, String name, int argb) {
        float a = (float)(argb >>> 24 & 0xFF) / 255.0f;
        float r = (float)(argb >>> 16 & 0xFF) / 255.0f;
        float g = (float)(argb >>> 8 & 0xFF) / 255.0f;
        float b = (float)(argb & 0xFF) / 255.0f;
        shader.m_173356_(name).m_5805_(r, g, b, a);
    }
}

