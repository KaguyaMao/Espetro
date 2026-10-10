/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 */
package dev.latvian.mods.kubejs.client.painter;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.latvian.mods.kubejs.client.ClientEventJS;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class PaintEventJS
extends ClientEventJS {
    public final Minecraft mc;
    public final Font font;
    public final GuiGraphics graphics;
    public final PoseStack matrices;
    public final Tesselator tesselator;
    public final BufferBuilder buffer;
    public final float delta;
    public final Screen screen;

    public PaintEventJS(Minecraft m, GuiGraphics g, float d, @Nullable Screen s) {
        this.mc = m;
        this.font = this.mc.f_91062_;
        this.graphics = g;
        this.matrices = g.m_280168_();
        this.tesselator = Tesselator.m_85913_();
        this.buffer = this.tesselator.m_85915_();
        this.delta = d;
        this.screen = s;
    }

    public void push() {
        this.matrices.m_85836_();
    }

    public void pop() {
        this.matrices.m_85849_();
    }

    public void translate(double x, double y, double z) {
        this.matrices.m_85837_(x, y, z);
    }

    public void scale(float x, float y, float z) {
        this.matrices.m_85841_(x, y, z);
    }

    public void multiply(Quaternionf q) {
        this.matrices.m_252781_(q);
    }

    public void multiplyWithMatrix(Matrix4f m) {
        this.matrices.m_252931_(m);
    }

    public Matrix4f getMatrix() {
        return this.matrices.m_85850_().m_252922_();
    }

    public void bindTextureForSetup(ResourceLocation tex) {
        this.mc.m_91097_().m_174784_(tex);
    }

    public void setShaderColor(float r, float g, float b, float a) {
        RenderSystem.setShaderColor((float)r, (float)g, (float)b, (float)a);
    }

    public void resetShaderColor() {
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public void setShaderTexture(ResourceLocation tex) {
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)tex);
    }

    public void begin(VertexFormat.Mode type, VertexFormat format) {
        this.buffer.m_166779_(type, format);
    }

    public void beginQuads(VertexFormat format) {
        this.begin(VertexFormat.Mode.QUADS, format);
    }

    public void beginQuads(boolean texture) {
        this.beginQuads(texture ? DefaultVertexFormat.f_85818_ : DefaultVertexFormat.f_85815_);
    }

    public void vertex(Matrix4f m, float x, float y, float z, int col) {
        this.buffer.m_252986_(m, x, y, z).m_6122_(col >> 16 & 0xFF, col >> 8 & 0xFF, col & 0xFF, col >> 24 & 0xFF).m_5752_();
    }

    public void vertex(Matrix4f m, float x, float y, float z, int col, float u, float v) {
        this.buffer.m_252986_(m, x, y, z).m_6122_(col >> 16 & 0xFF, col >> 8 & 0xFF, col & 0xFF, col >> 24 & 0xFF).m_7421_(u, v).m_5752_();
    }

    public void end() {
        this.tesselator.m_85914_();
    }

    public void setShaderInstance(Supplier<ShaderInstance> shader) {
        RenderSystem.setShader(shader);
    }

    public void setPositionColorShader() {
        RenderSystem.setShader(GameRenderer::m_172811_);
    }

    public void setPositionColorTextureShader() {
        RenderSystem.setShader(GameRenderer::m_172814_);
    }

    public void blend(boolean enabled) {
        if (enabled) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        } else {
            RenderSystem.disableBlend();
        }
    }
}

