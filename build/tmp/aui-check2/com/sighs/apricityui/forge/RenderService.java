/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  com.mojang.blaze3d.pipeline.TextureTarget
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.shaders.Uniform
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferBuilder$RenderedBuffer
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.blaze3d.vertex.VertexSorting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.resources.ResourceLocation
 *  org.joml.Matrix4f
 *  org.lwjgl.opengl.GL11
 */
package com.sighs.apricityui.forge;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.sighs.apricityui.forge.ShaderRegistry;
import com.sighs.apricityui.spi.AuiRenderService;
import com.sighs.apricityui.spi.FboHandle;
import com.sighs.apricityui.spi.MeshBuilder;
import com.sighs.apricityui.spi.MeshFormat;
import com.sighs.apricityui.spi.MeshMode;
import com.sighs.apricityui.spi.RenderHandle;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public final class RenderService
implements AuiRenderService {
    public static final RenderService INSTANCE = new RenderService();

    private RenderService() {
    }

    @Override
    public void setProjectionMatrix(Matrix4f matrix) {
        RenderSystem.setProjectionMatrix((Matrix4f)matrix, (VertexSorting)RenderSystem.getVertexSorting());
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        return RenderSystem.getProjectionMatrix();
    }

    @Override
    public void enableDepthTest() {
        RenderSystem.enableDepthTest();
    }

    @Override
    public void disableDepthTest() {
        RenderSystem.disableDepthTest();
    }

    @Override
    public void enableBlend() {
        RenderSystem.enableBlend();
    }

    @Override
    public void setBlendFunc(int srcFactor, int dstFactor) {
        RenderSystem.blendFunc((int)srcFactor, (int)dstFactor);
    }

    @Override
    public MeshBuilder beginMesh(MeshMode mode, MeshFormat format) {
        VertexFormat.Mode m;
        VertexFormat.Mode mode2 = m = mode == MeshMode.QUADS ? VertexFormat.Mode.QUADS : VertexFormat.Mode.TRIANGLES;
        VertexFormat fmt = format == MeshFormat.POSITION ? DefaultVertexFormat.f_85814_ : (format == MeshFormat.POSITION_TEX ? DefaultVertexFormat.f_85817_ : DefaultVertexFormat.f_85815_);
        BufferBuilder buf = Tesselator.m_85913_().m_85915_();
        buf.m_166779_(m, fmt);
        return MeshBuilder.of(buf);
    }

    @Override
    public void emitVertex(Object mesh, Matrix4f mat, float x, float y, float z, int r, int g, int b, int a) {
        ((BufferBuilder)mesh).m_252986_(mat, x, y, z).m_6122_(r, g, b, a).m_5752_();
    }

    @Override
    public void submitMesh(Object mesh) {
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)((BufferBuilder)mesh).m_231175_());
    }

    @Override
    public Object beginTextureBatch(RenderHandle render) {
        RenderType renderType = (RenderType)render.as();
        return new TextureBatchHandle(Minecraft.m_91087_().m_91269_().m_110104_(), renderType);
    }

    @Override
    public void emitTextureQuad(Object batch, Matrix4f mat, float x, float y, float width, float height, float u0, float v0, float u1, float v1) {
        TextureBatchHandle handle = (TextureBatchHandle)batch;
        VertexConsumer consumer = handle.source().m_6299_(handle.renderType());
        consumer.m_252986_(mat, x, y + height, 0.0f).m_6122_(255, 255, 255, 255).m_7421_(u0, v1).m_85969_(0xF000F0).m_5752_();
        consumer.m_252986_(mat, x + width, y + height, 0.0f).m_6122_(255, 255, 255, 255).m_7421_(u1, v1).m_85969_(0xF000F0).m_5752_();
        consumer.m_252986_(mat, x + width, y, 0.0f).m_6122_(255, 255, 255, 255).m_7421_(u1, v0).m_85969_(0xF000F0).m_5752_();
        consumer.m_252986_(mat, x, y, 0.0f).m_6122_(255, 255, 255, 255).m_7421_(u0, v0).m_85969_(0xF000F0).m_5752_();
    }

    @Override
    public void flushTextureBatch(Object batch, RenderHandle render) {
        TextureBatchHandle handle = (TextureBatchHandle)batch;
        handle.source().m_109912_(handle.renderType());
    }

    @Override
    public void emitVertexUV(Object mesh, Matrix4f mat, float x, float y, float z, float u, float v) {
        ((BufferBuilder)mesh).m_252986_(mat, x, y, z).m_7421_(u, v).m_5752_();
    }

    @Override
    public FboHandle createOffscreenTarget(int width, int height, boolean useDepth) {
        TextureTarget target = new TextureTarget(width, height, useDepth, Minecraft.f_91002_);
        return FboHandle.of(target, width, height);
    }

    @Override
    public FboHandle getMainRenderTarget() {
        RenderTarget target = Minecraft.m_91087_().m_91385_();
        return FboHandle.of(target, target.f_83915_, target.f_83916_);
    }

    @Override
    public void enableStencil(FboHandle target) {
        RenderTarget rt = (RenderTarget)target.as();
        rt.enableStencil();
        if (rt == Minecraft.m_91087_().m_91385_()) {
            RenderService.enableFabulousChainStencil();
        }
    }

    private static void enableFabulousChainStencil() {
        LevelRenderer levelRenderer = Minecraft.m_91087_().f_91060_;
        if (levelRenderer == null) {
            return;
        }
        RenderService.enableStencilIfPresent(levelRenderer.m_109828_());
        RenderService.enableStencilIfPresent(levelRenderer.m_109829_());
        RenderService.enableStencilIfPresent(levelRenderer.m_109830_());
        RenderService.enableStencilIfPresent(levelRenderer.m_109831_());
        RenderService.enableStencilIfPresent(levelRenderer.m_109832_());
    }

    private static void enableStencilIfPresent(RenderTarget target) {
        if (target != null && !target.isStencilEnabled()) {
            target.enableStencil();
        }
    }

    public void reconcileFabulousChainStencil() {
        RenderTarget main = Minecraft.m_91087_().m_91385_();
        if (main != null && main.isStencilEnabled()) {
            RenderService.enableFabulousChainStencil();
        }
    }

    @Override
    public void destroyBuffers(FboHandle target) {
        ((RenderTarget)target.as()).m_83930_();
    }

    @Override
    public void clear(FboHandle target, float r, float g, float b, float a) {
        RenderTarget rt = (RenderTarget)target.as();
        rt.m_83931_(r, g, b, a);
        rt.m_83954_(Minecraft.f_91002_);
    }

    @Override
    public void bindWrite(FboHandle target, boolean setViewport) {
        ((RenderTarget)target.as()).m_83947_(setViewport);
    }

    @Override
    public void bindColorTexture(FboHandle target, int unit) {
        RenderSystem.setShaderTexture((int)unit, (int)((RenderTarget)target.as()).m_83975_());
    }

    @Override
    public AuiRenderService.RenderStateScope pushFilterRenderState() {
        return LegacyFilterState.capture();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void blitFramebuffer(FboHandle source, FboHandle target, int srcX0, int srcY0, int srcX1, int srcY1) {
        RenderTarget src = (RenderTarget)source.as();
        RenderTarget dst = (RenderTarget)target.as();
        int previousReadFbo = GL11.glGetInteger((int)36010);
        int previousDrawFbo = GL11.glGetInteger((int)36006);
        try {
            GlStateManager._glBindFramebuffer((int)36008, (int)src.f_83920_);
            GlStateManager._glBindFramebuffer((int)36009, (int)dst.f_83920_);
            GlStateManager._glBlitFrameBuffer((int)srcX0, (int)srcY0, (int)srcX1, (int)srcY1, (int)0, (int)0, (int)dst.f_83915_, (int)dst.f_83916_, (int)16384, (int)9729);
        }
        finally {
            GlStateManager._glBindFramebuffer((int)36008, (int)previousReadFbo);
            GlStateManager._glBindFramebuffer((int)36009, (int)previousDrawFbo);
        }
    }

    @Override
    public Object createDynamicTexture(String name, Object nativeImage, boolean linear) {
        DynamicTexture texture = new DynamicTexture((NativeImage)nativeImage);
        texture.m_117960_(linear, false);
        return texture;
    }

    @Override
    public void uploadTextureRegion(Object texture, Object nativeImage, int x, int y, int width, int height, boolean linear) {
        ((DynamicTexture)texture).m_117966_();
        ((NativeImage)nativeImage).m_85003_(0, x, y, x, y, width, height, linear, false);
    }

    @Override
    public void setImagePixel(Object nativeImage, int x, int y, int pixel) {
        ((NativeImage)nativeImage).m_84988_(x, y, pixel);
    }

    @Override
    public void closeTexture(Object texture) {
        ((DynamicTexture)texture).close();
    }

    @Override
    public void registerTexture(Object texture, Object location) {
        Minecraft.m_91087_().m_91097_().m_118495_((ResourceLocation)location, (AbstractTexture)texture);
    }

    @Override
    public void releaseTexture(Object location) {
        Minecraft.m_91087_().m_91097_().m_118513_((ResourceLocation)location);
    }

    @Override
    public void setShader(Object shader) {
        RenderSystem.setShader(() -> (ShaderInstance)shader);
    }

    @Override
    public void setPositionColorShader() {
        RenderSystem.setShader(GameRenderer::m_172811_);
    }

    @Override
    public void setShaderColor(float a, float r, float g, float b) {
        RenderSystem.setShaderColor((float)a, (float)r, (float)g, (float)b);
    }

    @Override
    public Object getFilterShader() {
        return ShaderRegistry.getFilterShader();
    }

    @Override
    public Object getFilterBlurShader() {
        return ShaderRegistry.getFilterBlurShader();
    }

    @Override
    public void setDepthFunc(int func) {
        RenderSystem.depthFunc((int)func);
    }

    @Override
    public void setDepthMask(boolean write) {
        GlStateManager._depthMask((boolean)write);
    }

    @Override
    public boolean isDepthTestEnabled() {
        return GL11.glIsEnabled((int)2929);
    }

    @Override
    public boolean isDepthMaskEnabled() {
        return GL11.glGetBoolean((int)2930);
    }

    @Override
    public void setBlendFuncSeparate(int srcRgb, int dstRgb, int srcAlpha, int dstAlpha) {
        RenderSystem.blendFuncSeparate((int)srcRgb, (int)dstRgb, (int)srcAlpha, (int)dstAlpha);
    }

    @Override
    public void disableBlend() {
        RenderSystem.disableBlend();
    }

    @Override
    public void enableCull() {
        RenderSystem.enableCull();
    }

    @Override
    public void disableCull() {
        RenderSystem.disableCull();
    }

    @Override
    public boolean isCullEnabled() {
        return GL11.glIsEnabled((int)2884);
    }

    @Override
    public void enablePolygonOffset() {
        RenderSystem.enablePolygonOffset();
    }

    @Override
    public void disablePolygonOffset() {
        RenderSystem.disablePolygonOffset();
    }

    @Override
    public void polygonOffset(float factor, float units) {
        RenderSystem.polygonOffset((float)factor, (float)units);
    }

    @Override
    public void enableScissorTest() {
        GlStateManager._enableScissorTest();
    }

    @Override
    public void scissorBox(int x, int y, int width, int height) {
        GlStateManager._scissorBox((int)x, (int)y, (int)width, (int)height);
    }

    @Override
    public void disableScissorTest() {
        GlStateManager._disableScissorTest();
    }

    @Override
    public void enableStencilTest() {
        GL11.glEnable((int)2960);
    }

    @Override
    public void disableStencilTest() {
        GL11.glDisable((int)2960);
    }

    @Override
    public void setStencilMask(int mask) {
        GL11.glStencilMask((int)mask);
    }

    @Override
    public void setStencilFunc(int func, int ref, int mask) {
        GL11.glStencilFunc((int)func, (int)ref, (int)mask);
    }

    @Override
    public void setStencilOp(int sfail, int dpfail, int dppass) {
        GL11.glStencilOp((int)sfail, (int)dpfail, (int)dppass);
    }

    @Override
    public void clearStencilBuffer() {
        GL11.glClear((int)1024);
    }

    @Override
    public void setColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        GL11.glColorMask((boolean)red, (boolean)green, (boolean)blue, (boolean)alpha);
    }

    @Override
    public boolean isOnRenderThread() {
        return RenderSystem.isOnRenderThread();
    }

    @Override
    public void recordRenderCall(Runnable task) {
        RenderSystem.recordRenderCall(task::run);
    }

    @Override
    public String getGLVersionString() {
        return GL11.glGetString((int)7938);
    }

    @Override
    public void flushSharedBuffers() {
        Minecraft.m_91087_().m_91269_().m_110104_().m_109911_();
    }

    private static void setShaderUniform(String name, Consumer<Uniform> setter) {
        ShaderInstance shader = RenderSystem.getShader();
        if (shader == null) {
            return;
        }
        Uniform uniform = shader.m_173348_(name);
        if (uniform != null) {
            setter.accept(uniform);
        }
    }

    @Override
    public void setShaderUniformFloat(String name, float value) {
        RenderService.setShaderUniform(name, uniform -> uniform.m_5985_(value));
    }

    @Override
    public void setShaderUniform2f(String name, float a, float b) {
        RenderService.setShaderUniform(name, uniform -> uniform.m_7971_(a, b));
    }

    @Override
    public void setShaderUniform3f(String name, float a, float b, float c) {
        RenderService.setShaderUniform(name, uniform -> uniform.m_5889_(a, b, c));
    }

    @Override
    public void setShaderUniform4f(String name, float a, float b, float c, float d) {
        RenderService.setShaderUniform(name, uniform -> uniform.m_5805_(a, b, c, d));
    }

    @Override
    public void setShaderUniformI(String name, int value) {
        RenderService.setShaderUniform(name, uniform -> uniform.m_142617_(value));
    }

    private record TextureBatchHandle(MultiBufferSource.BufferSource source, RenderType renderType) {
    }

    private static final class LegacyFilterState
    implements AuiRenderService.RenderStateScope {
        private static final int SHADER_SAMPLER_COUNT = 12;
        private final ShaderInstance shader;
        private final int program;
        private final int[] shaderTextures;
        private final int[] boundTextures;
        private final int activeTexture;
        private final float[] shaderColor;
        private final boolean blend;
        private final int srcRgb;
        private final int dstRgb;
        private final int srcAlpha;
        private final int dstAlpha;
        private final boolean depthTest;
        private final int depthFunc;
        private final boolean depthMask;
        private final boolean cull;
        private boolean closed;

        private LegacyFilterState(ShaderInstance shader, int program, int[] shaderTextures, int[] boundTextures, int activeTexture, float[] shaderColor, boolean blend, int srcRgb, int dstRgb, int srcAlpha, int dstAlpha, boolean depthTest, int depthFunc, boolean depthMask, boolean cull) {
            this.shader = shader;
            this.program = program;
            this.shaderTextures = shaderTextures;
            this.boundTextures = boundTextures;
            this.activeTexture = activeTexture;
            this.shaderColor = shaderColor;
            this.blend = blend;
            this.srcRgb = srcRgb;
            this.dstRgb = dstRgb;
            this.srcAlpha = srcAlpha;
            this.dstAlpha = dstAlpha;
            this.depthTest = depthTest;
            this.depthFunc = depthFunc;
            this.depthMask = depthMask;
            this.cull = cull;
        }

        private static LegacyFilterState capture() {
            int activeTexture = GlStateManager._getActiveTexture();
            int[] shaderTextures = new int[12];
            int[] boundTextures = new int[12];
            for (int unit = 0; unit < 12; ++unit) {
                shaderTextures[unit] = RenderSystem.getShaderTexture((int)unit);
                GlStateManager._activeTexture((int)(33984 + unit));
                boundTextures[unit] = GL11.glGetInteger((int)32873);
            }
            GlStateManager._activeTexture((int)activeTexture);
            return new LegacyFilterState(RenderSystem.getShader(), GL11.glGetInteger((int)35725), shaderTextures, boundTextures, activeTexture, (float[])RenderSystem.getShaderColor().clone(), GL11.glIsEnabled((int)3042), GL11.glGetInteger((int)32969), GL11.glGetInteger((int)32968), GL11.glGetInteger((int)32971), GL11.glGetInteger((int)32970), GL11.glIsEnabled((int)2929), GL11.glGetInteger((int)2932), GL11.glGetBoolean((int)2930), GL11.glIsEnabled((int)2884));
        }

        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            RenderSystem.setShader(() -> this.shader);
            if (this.shader != null) {
                this.shader.m_173363_();
                this.shader.m_173362_();
            }
            for (int unit = 0; unit < 12; ++unit) {
                RenderSystem.setShaderTexture((int)unit, (int)this.shaderTextures[unit]);
                GlStateManager._activeTexture((int)(33984 + unit));
                GlStateManager._bindTexture((int)this.boundTextures[unit]);
            }
            GlStateManager._activeTexture((int)this.activeTexture);
            GlStateManager._glUseProgram((int)this.program);
            RenderSystem.setShaderColor((float)this.shaderColor[0], (float)this.shaderColor[1], (float)this.shaderColor[2], (float)this.shaderColor[3]);
            RenderSystem.blendFuncSeparate((int)this.srcRgb, (int)this.dstRgb, (int)this.srcAlpha, (int)this.dstAlpha);
            if (this.blend) {
                RenderSystem.enableBlend();
            } else {
                RenderSystem.disableBlend();
            }
            RenderSystem.depthFunc((int)this.depthFunc);
            GlStateManager._depthMask((boolean)this.depthMask);
            if (this.depthTest) {
                RenderSystem.enableDepthTest();
            } else {
                RenderSystem.disableDepthTest();
            }
            if (this.cull) {
                RenderSystem.enableCull();
            } else {
                RenderSystem.disableCull();
            }
        }
    }
}

