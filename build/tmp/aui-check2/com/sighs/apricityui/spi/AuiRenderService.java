/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package com.sighs.apricityui.spi;

import com.sighs.apricityui.spi.FboHandle;
import com.sighs.apricityui.spi.MeshBuilder;
import com.sighs.apricityui.spi.MeshFormat;
import com.sighs.apricityui.spi.MeshMode;
import com.sighs.apricityui.spi.RenderHandle;
import org.joml.Matrix4f;

public interface AuiRenderService {
    public void setProjectionMatrix(Matrix4f var1);

    public Matrix4f getProjectionMatrix();

    public void enableDepthTest();

    public void disableDepthTest();

    public void enableBlend();

    public void setBlendFunc(int var1, int var2);

    public MeshBuilder beginMesh(MeshMode var1, MeshFormat var2);

    public void emitVertex(Object var1, Matrix4f var2, float var3, float var4, float var5, int var6, int var7, int var8, int var9);

    public void submitMesh(Object var1);

    public Object beginTextureBatch(RenderHandle var1);

    public void emitTextureQuad(Object var1, Matrix4f var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10);

    public void flushTextureBatch(Object var1, RenderHandle var2);

    public void emitVertexUV(Object var1, Matrix4f var2, float var3, float var4, float var5, float var6, float var7);

    public FboHandle createOffscreenTarget(int var1, int var2, boolean var3);

    public FboHandle getMainRenderTarget();

    public void enableStencil(FboHandle var1);

    public void destroyBuffers(FboHandle var1);

    public void clear(FboHandle var1, float var2, float var3, float var4, float var5);

    public void bindWrite(FboHandle var1, boolean var2);

    public void bindColorTexture(FboHandle var1, int var2);

    default public RenderStateScope pushFilterRenderState() {
        return RenderStateScope.NOOP;
    }

    public void blitFramebuffer(FboHandle var1, FboHandle var2, int var3, int var4, int var5, int var6);

    public Object createDynamicTexture(String var1, Object var2, boolean var3);

    public void uploadTextureRegion(Object var1, Object var2, int var3, int var4, int var5, int var6, boolean var7);

    public void setImagePixel(Object var1, int var2, int var3, int var4);

    public void closeTexture(Object var1);

    public void registerTexture(Object var1, Object var2);

    public void releaseTexture(Object var1);

    public void setShader(Object var1);

    public void setPositionColorShader();

    public void setShaderColor(float var1, float var2, float var3, float var4);

    public Object getFilterShader();

    public Object getFilterBlurShader();

    public void setDepthFunc(int var1);

    public void setDepthMask(boolean var1);

    public boolean isDepthTestEnabled();

    public boolean isDepthMaskEnabled();

    public void setBlendFuncSeparate(int var1, int var2, int var3, int var4);

    public void disableBlend();

    public void enableCull();

    public void disableCull();

    public boolean isCullEnabled();

    public void enablePolygonOffset();

    public void disablePolygonOffset();

    public void polygonOffset(float var1, float var2);

    public void enableScissorTest();

    public void scissorBox(int var1, int var2, int var3, int var4);

    public void disableScissorTest();

    public void enableStencilTest();

    public void disableStencilTest();

    public void setStencilMask(int var1);

    public void setStencilFunc(int var1, int var2, int var3);

    public void setStencilOp(int var1, int var2, int var3);

    public void clearStencilBuffer();

    public void setColorMask(boolean var1, boolean var2, boolean var3, boolean var4);

    public boolean isOnRenderThread();

    public void recordRenderCall(Runnable var1);

    public String getGLVersionString();

    default public boolean supportsStencil() {
        return true;
    }

    default public boolean currentTargetHasStencil() {
        return true;
    }

    public void flushSharedBuffers();

    public void setShaderUniformFloat(String var1, float var2);

    public void setShaderUniform2f(String var1, float var2, float var3);

    public void setShaderUniform3f(String var1, float var2, float var3, float var4);

    public void setShaderUniform4f(String var1, float var2, float var3, float var4, float var5);

    public void setShaderUniformI(String var1, int var2);

    @FunctionalInterface
    public static interface RenderStateScope
    extends AutoCloseable {
        public static final RenderStateScope NOOP = () -> {};

        @Override
        public void close();
    }
}

