/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package com.sighs.apricityui.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.Graph;
import com.sighs.apricityui.render.ImageDrawer;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.spi.AuiRenderService;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.spi.FboHandle;
import com.sighs.apricityui.spi.MeshBuilder;
import com.sighs.apricityui.spi.MeshFormat;
import com.sighs.apricityui.spi.MeshMode;
import com.sighs.apricityui.style.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class FilterRenderer {
    private static final Stack<FboHandle> fboStack = new Stack();
    private static FboHandle mainRenderTarget;
    private static final List<FboHandle> fboPool;
    private static int poolPointer;
    private static final List<FboHandle> backdropPool;
    private static int backdropPoolPointer;
    private static final float MAX_REASONABLE_BACKDROP_BLUR = 32.0f;
    private static boolean stencilCapabilityResolved;
    private static boolean stencilAvailable;

    public static boolean isStencilAvailable() {
        FilterRenderer.resolveStencilCapability();
        return stencilAvailable;
    }

    private static void resolveStencilCapability() {
        if (stencilCapabilityResolved) {
            return;
        }
        stencilCapabilityResolved = true;
        try {
            if (!AuiServices.render().supportsStencil()) {
                stencilAvailable = false;
                ApricityUI.LOGGER.warn("[ApricityUI] stencil masks are unavailable on this render backend; using scissor fallback");
                return;
            }
            String version = AuiServices.render().getGLVersionString();
            if (version != null && version.toLowerCase(Locale.ROOT).contains("opengl es")) {
                stencilAvailable = false;
                ApricityUI.LOGGER.warn("[ApricityUI] OpenGL ES detected ({}); disabling stencil-backed masks for compatibility", (Object)version);
            }
        }
        catch (RuntimeException ignored) {
            stencilAvailable = true;
        }
    }

    public static void beginFrame() {
        if (!fboStack.isEmpty()) {
            fboStack.clear();
        }
        mainRenderTarget = AuiServices.render().getMainRenderTarget();
        poolPointer = 0;
        backdropPoolPointer = 0;
    }

    public static void endFrame() {
        if (!fboStack.isEmpty()) {
            fboStack.clear();
            if (mainRenderTarget != null) {
                AuiServices.render().bindWrite(mainRenderTarget, false);
            }
        }
    }

    public static void pushFilter() {
        FboHandle temp;
        ImageDrawer.flushBatch();
        Graph.endBatch();
        if (fboStack.isEmpty()) {
            mainRenderTarget = AuiServices.render().getMainRenderTarget();
            poolPointer = 0;
        }
        double width = AuiServices.client().getWindowWidth();
        double height = AuiServices.client().getWindowHeight();
        if (poolPointer < fboPool.size()) {
            temp = fboPool.get(poolPointer);
            if (temp.width != (int)width || temp.height != (int)height) {
                AuiServices.render().destroyBuffers(temp);
                temp = AuiServices.render().createOffscreenTarget((int)width, (int)height, true);
                fboPool.set(poolPointer, temp);
            }
        } else {
            temp = AuiServices.render().createOffscreenTarget((int)width, (int)height, true);
            fboPool.add(temp);
        }
        ++poolPointer;
        AuiServices.render().clear(temp, 0.0f, 0.0f, 0.0f, 0.0f);
        fboStack.push(temp);
        AuiServices.render().bindWrite(temp, false);
    }

    public static FboHandle getCurrentTarget() {
        return fboStack.isEmpty() ? AuiServices.render().getMainRenderTarget() : fboStack.peek();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void popFilter(Filter.FilterState state) {
        if (fboStack.isEmpty()) {
            return;
        }
        ImageDrawer.flushBatch();
        Graph.endBatch();
        FboHandle currentFbo = fboStack.pop();
        FboHandle parentFbo = fboStack.isEmpty() ? mainRenderTarget : fboStack.peek();
        try {
            FboHandle filteredFbo = FilterRenderer.prepareFullFilterSource(currentFbo, state.blurRadius());
            FboHandle shadowFbo = state.hasDropShadow() ? FilterRenderer.prepareFullFilterSource(currentFbo, state.dropShadowBlur()) : currentFbo;
            AuiServices.render().bindWrite(parentFbo, true);
            FilterRenderer.drawWithShader(filteredFbo, shadowFbo, state);
        }
        finally {
            if (parentFbo != null) {
                AuiServices.render().bindWrite(parentFbo, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void withBlendRenderState(boolean enableBlend, Runnable body) {
        Matrix4f oldProjection = new Matrix4f((Matrix4fc)Base.getProjectionMatrix());
        AuiRenderService.RenderStateScope scope = AuiServices.render().pushFilterRenderState();
        if (scope == null) {
            scope = AuiRenderService.RenderStateScope.NOOP;
        }
        if (enableBlend) {
            AuiServices.render().enableBlend();
            AuiServices.render().setBlendFuncSeparate(770, 771, 1, 771);
        } else {
            AuiServices.render().disableBlend();
        }
        AuiServices.render().disableDepthTest();
        AuiServices.render().setDepthMask(false);
        AuiServices.render().disableCull();
        try {
            body.run();
        }
        finally {
            try {
                AuiServices.render().setDepthMask(true);
                if (Base.isDepthTestEnabled()) {
                    AuiServices.render().enableDepthTest();
                } else {
                    AuiServices.render().disableDepthTest();
                }
                Base.setProjectionMatrix(oldProjection);
            }
            finally {
                scope.close();
            }
        }
    }

    private static void drawWithShader(FboHandle fbo, FboHandle shadowFbo, Filter.FilterState state) {
        Object shader = AuiServices.render().getFilterShader();
        FilterRenderer.withBlendRenderState(true, () -> {
            if (shader == null) {
                Base.setPositionColorShader();
            } else {
                Base.setShader(shader);
                FilterRenderer.setupUniforms(shader, state, fbo, false, true, 1.0f / (float)Math.max(1, AuiServices.client().getScaledWidth()), 1.0f / (float)Math.max(1, AuiServices.client().getScaledHeight()));
            }
            AuiServices.render().bindColorTexture(fbo, 0);
            AuiServices.render().bindColorTexture(shadowFbo, 1);
            Base.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            float guiW = AuiServices.client().getScaledWidth();
            float guiH = AuiServices.client().getScaledHeight();
            Base.setProjectionMatrix(FilterRenderer.orthoProjection(guiW, guiH));
            MeshBuilder mesh = AuiServices.render().beginMesh(MeshMode.QUADS, MeshFormat.POSITION_TEX);
            Matrix4f identity = new Matrix4f();
            mesh.vertexUV(identity, 0.0f, guiH, 0.0f, 0.0f, 0.0f);
            mesh.vertexUV(identity, guiW, guiH, 0.0f, 1.0f, 0.0f);
            mesh.vertexUV(identity, guiW, 0.0f, 0.0f, 1.0f, 1.0f);
            mesh.vertexUV(identity, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
            mesh.submit();
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void renderBackdrop(Element target, PoseStack poseStack) {
        Base.commitDraws();
        FboHandle currentBound = fboStack.isEmpty() ? AuiServices.render().getMainRenderTarget() : fboStack.peek();
        Filter.FilterState state = Filter.getBackdropFilterOf(target);
        Rect rect = Rect.of(target);
        try {
            BackdropSource source = FilterRenderer.prepareBackdropSource(currentBound, rect, state.blurRadius());
            if (source == null) {
                return;
            }
            FboHandle shadowTarget = FilterRenderer.prepareBackdropShadow(source, state);
            AuiServices.render().bindWrite(currentBound, true);
            FilterRenderer.drawBackdropWithShader(source, shadowTarget, state, rect);
        }
        finally {
            if (currentBound != null) {
                AuiServices.render().bindWrite(currentBound, true);
            }
        }
    }

    private static void drawBackdropWithShader(BackdropSource source, FboHandle shadowTarget, Filter.FilterState state, Rect rect) {
        Object shader = AuiServices.render().getFilterShader();
        if (shader == null) {
            return;
        }
        FilterRenderer.withBlendRenderState(true, () -> {
            Position p = rect.getBodyRectPosition();
            Size s = rect.getBodyRectSize();
            float guiW = AuiServices.client().getScaledWidth();
            float guiH = AuiServices.client().getScaledHeight();
            Base.setProjectionMatrix(FilterRenderer.orthoProjection(guiW, guiH));
            Base.setShader(shader);
            FilterRenderer.setupUniforms(shader, state, source.target(), true, true, source.uvPerGuiX(), source.uvPerGuiY());
            FilterRenderer.setupBackdropClipUniforms(shader, rect, guiW, guiH);
            AuiServices.render().bindColorTexture(source.target(), 0);
            AuiServices.render().bindColorTexture(shadowTarget, 1);
            Base.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            Base.setProjectionMatrix(FilterRenderer.orthoProjection(guiW, guiH));
            MeshBuilder mesh = AuiServices.render().beginMesh(MeshMode.QUADS, MeshFormat.POSITION_TEX);
            Matrix4f identity = new Matrix4f();
            float x0 = (float)p.x;
            float y0 = (float)p.y;
            float x1 = x0 + (float)s.width();
            float y1 = y0 + (float)s.height();
            mesh.vertexUV(identity, x0, y1, 0.0f, source.u0(), source.vBottom());
            mesh.vertexUV(identity, x1, y1, 0.0f, source.u1(), source.vBottom());
            mesh.vertexUV(identity, x1, y0, 0.0f, source.u1(), source.vTop());
            mesh.vertexUV(identity, x0, y0, 0.0f, source.u0(), source.vTop());
            mesh.submit();
        });
    }

    private static BackdropSource prepareBackdropSource(FboHandle source, Rect rect, float cssBlurRadius) {
        if (source == null || source.width <= 0 || source.height <= 0) {
            return null;
        }
        float guiW = AuiServices.client().getScaledWidth();
        float guiH = AuiServices.client().getScaledHeight();
        if (guiW <= 0.0f || guiH <= 0.0f) {
            return null;
        }
        Position position = rect.getBodyRectPosition();
        Size size = rect.getBodyRectSize();
        float scaleX = (float)source.width / guiW;
        float scaleY = (float)source.height / guiH;
        float physicalRadius = Math.min(32.0f, Math.max(0.0f, cssBlurRadius)) * Math.max(scaleX, scaleY);
        float padding = physicalRadius + 2.0f;
        int srcX0 = FilterRenderer.clamp((int)Math.floor(position.x * (double)scaleX - (double)padding), 0, source.width);
        int srcX1 = FilterRenderer.clamp((int)Math.ceil((position.x + size.width()) * (double)scaleX + (double)padding), 0, source.width);
        int srcY0 = FilterRenderer.clamp((int)Math.floor((double)source.height - (position.y + size.height()) * (double)scaleY - (double)padding), 0, source.height);
        int srcY1 = FilterRenderer.clamp((int)Math.ceil((double)source.height - position.y * (double)scaleY + (double)padding), 0, source.height);
        if (srcX1 <= srcX0 || srcY1 <= srcY0) {
            return null;
        }
        int downsample = FilterRenderer.chooseDownsample(physicalRadius);
        int targetWidth = Math.max(1, (int)Math.ceil((double)(srcX1 - srcX0) / (double)downsample));
        int targetHeight = Math.max(1, (int)Math.ceil((double)(srcY1 - srcY0) / (double)downsample));
        FboHandle ping = FilterRenderer.acquireBackdropTarget(targetWidth, targetHeight);
        FilterRenderer.blitRegion(source, ping, srcX0, srcY0, srcX1, srcY1);
        float reducedRadius = physicalRadius / (float)downsample;
        if (reducedRadius >= 0.5f) {
            FboHandle pong = FilterRenderer.acquireBackdropTarget(targetWidth, targetHeight);
            FilterRenderer.drawBlurPass(ping, pong, reducedRadius, 1.0f / (float)targetWidth, 0.0f);
            FilterRenderer.drawBlurPass(pong, ping, reducedRadius, 0.0f, 1.0f / (float)targetHeight);
        }
        float sourceWidth = srcX1 - srcX0;
        float sourceHeight = srcY1 - srcY0;
        float u0 = (float)((position.x * (double)scaleX - (double)srcX0) / (double)sourceWidth);
        float u1 = (float)(((position.x + size.width()) * (double)scaleX - (double)srcX0) / (double)sourceWidth);
        float vTop = (float)(((double)source.height - position.y * (double)scaleY - (double)srcY0) / (double)sourceHeight);
        float vBottom = (float)(((double)source.height - (position.y + size.height()) * (double)scaleY - (double)srcY0) / (double)sourceHeight);
        float uvPerGuiX = scaleX / sourceWidth;
        float uvPerGuiY = scaleY / sourceHeight;
        return new BackdropSource(ping, u0, vBottom, u1, vTop, uvPerGuiX, uvPerGuiY);
    }

    private static FboHandle prepareBackdropShadow(BackdropSource source, Filter.FilterState state) {
        if (!state.hasDropShadow() || state.dropShadowBlur() < 0.5f) {
            return source.target();
        }
        float textureRadius = state.dropShadowBlur() * Math.max(source.uvPerGuiX() * (float)source.target().width, source.uvPerGuiY() * (float)source.target().height);
        return FilterRenderer.blurTexture(source.target(), textureRadius);
    }

    private static FboHandle prepareFullFilterSource(FboHandle source, float cssBlurRadius) {
        if (source == null || cssBlurRadius < 0.5f) {
            return source;
        }
        float guiW = Math.max(1.0f, (float)AuiServices.client().getScaledWidth());
        float guiH = Math.max(1.0f, (float)AuiServices.client().getScaledHeight());
        float physicalRadius = Math.max(0.0f, cssBlurRadius) * Math.max((float)source.width / guiW, (float)source.height / guiH);
        return FilterRenderer.blurTexture(source, physicalRadius);
    }

    private static FboHandle blurTexture(FboHandle source, float physicalRadius) {
        int downsample = FilterRenderer.chooseDownsample(physicalRadius);
        int width = Math.max(1, (int)Math.ceil((double)source.width / (double)downsample));
        int height = Math.max(1, (int)Math.ceil((double)source.height / (double)downsample));
        FboHandle ping = FilterRenderer.acquireBackdropTarget(width, height);
        FilterRenderer.blitRegion(source, ping, 0, 0, source.width, source.height);
        FboHandle pong = FilterRenderer.acquireBackdropTarget(width, height);
        float reducedRadius = Math.min(32.0f, physicalRadius / (float)downsample);
        FilterRenderer.drawBlurPass(ping, pong, reducedRadius, 1.0f / (float)width, 0.0f);
        FilterRenderer.drawBlurPass(pong, ping, reducedRadius, 0.0f, 1.0f / (float)height);
        return ping;
    }

    private static int chooseDownsample(float physicalRadius) {
        int result;
        int n = result = physicalRadius >= 6.0f ? 2 : 1;
        while (physicalRadius / (float)result > 18.0f && result < 64) {
            result *= 2;
        }
        return result;
    }

    private static FboHandle acquireBackdropTarget(int width, int height) {
        FboHandle target;
        if (backdropPoolPointer < backdropPool.size()) {
            target = backdropPool.get(backdropPoolPointer);
            if (target.width != width || target.height != height) {
                AuiServices.render().destroyBuffers(target);
                target = AuiServices.render().createOffscreenTarget(width, height, false);
                backdropPool.set(backdropPoolPointer, target);
            }
        } else {
            target = AuiServices.render().createOffscreenTarget(width, height, false);
            backdropPool.add(target);
        }
        ++backdropPoolPointer;
        return target;
    }

    private static void blitRegion(FboHandle source, FboHandle target, int srcX0, int srcY0, int srcX1, int srcY1) {
        AuiServices.render().blitFramebuffer(source, target, srcX0, srcY0, srcX1, srcY1);
    }

    private static void drawBlurPass(FboHandle source, FboHandle target, float radius, float directionX, float directionY) {
        Object shader = AuiServices.render().getFilterBlurShader();
        if (shader == null) {
            return;
        }
        AuiServices.render().clear(target, 0.0f, 0.0f, 0.0f, 0.0f);
        AuiServices.render().bindWrite(target, true);
        FilterRenderer.withBlendRenderState(false, () -> {
            Base.setProjectionMatrix(FilterRenderer.orthoProjection(target.width, target.height));
            Base.setShader(shader);
            AuiServices.render().setShaderUniform2f("Direction", directionX, directionY);
            AuiServices.render().setShaderUniformFloat("Radius", Math.min(32.0f, radius));
            AuiServices.render().bindColorTexture(source, 0);
            Base.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            MeshBuilder mesh = AuiServices.render().beginMesh(MeshMode.QUADS, MeshFormat.POSITION_TEX);
            Matrix4f identity = new Matrix4f();
            mesh.vertexUV(identity, 0.0f, target.height, 0.0f, 0.0f, 0.0f);
            mesh.vertexUV(identity, target.width, target.height, 0.0f, 1.0f, 0.0f);
            mesh.vertexUV(identity, target.width, 0.0f, 0.0f, 1.0f, 1.0f);
            mesh.vertexUV(identity, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
            mesh.submit();
        });
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static void setupUniforms(Object shader, Filter.FilterState state, FboHandle fbo, boolean forceAlpha, boolean preBlurred, float uvPerGuiX, float uvPerGuiY) {
        float blurRadius = preBlurred ? 0.0f : (forceAlpha ? Math.min(state.blurRadius(), 32.0f) : state.blurRadius());
        AuiServices.render().setShaderUniformFloat("BlurRadius", blurRadius);
        AuiServices.render().setShaderUniformFloat("Brightness", state.brightness());
        AuiServices.render().setShaderUniformFloat("Grayscale", state.grayscale());
        AuiServices.render().setShaderUniformFloat("Invert", state.invert());
        AuiServices.render().setShaderUniformFloat("HueRotate", state.hueRotate());
        AuiServices.render().setShaderUniformFloat("Opacity", state.opacity());
        AuiServices.render().setShaderUniform2f("ShadowOffset", state.dropShadowX(), state.dropShadowY());
        AuiServices.render().setShaderUniformFloat("ShadowBlur", state.dropShadowBlur());
        int c = state.dropShadowColor();
        float a = (float)(c >>> 24 & 0xFF) / 255.0f;
        float r = (float)(c >>> 16 & 0xFF) / 255.0f;
        float g = (float)(c >>> 8 & 0xFF) / 255.0f;
        float b = (float)(c & 0xFF) / 255.0f;
        AuiServices.render().setShaderUniform4f("ShadowColor", r, g, b, a);
        AuiServices.render().setShaderUniform2f("InSize", fbo.width, fbo.height);
        AuiServices.render().setShaderUniformFloat("ForceAlpha", forceAlpha ? 1.0f : 0.0f);
        AuiServices.render().setShaderUniformFloat("ClipEnabled", 0.0f);
        AuiServices.render().setShaderUniform2f("GuiSize", AuiServices.client().getScaledWidth(), AuiServices.client().getScaledHeight());
        AuiServices.render().setShaderUniform2f("UvPerGuiPixel", uvPerGuiX, uvPerGuiY);
    }

    private static void setupBackdropClipUniforms(Object shader, Rect rect, float guiW, float guiH) {
        Position p = rect.getBodyRectPosition();
        Size s = rect.getBodyRectSize();
        float[] radii = rect.getBodyRadius();
        AuiServices.render().setShaderUniformFloat("ClipEnabled", 1.0f);
        AuiServices.render().setShaderUniform4f("ClipRect", (float)p.x, (float)p.y, (float)s.width(), (float)s.height());
        if (radii != null && radii.length >= 4) {
            AuiServices.render().setShaderUniform4f("ClipRadii", radii[0], radii[1], radii[2], radii[3]);
        }
        AuiServices.render().setShaderUniform2f("GuiSize", guiW, guiH);
    }

    private static Matrix4f orthoProjection(float width, float height) {
        return new Matrix4f().setOrtho(0.0f, width, height, 0.0f, -1000.0f, 1000.0f);
    }

    static {
        fboPool = new ArrayList<FboHandle>();
        poolPointer = 0;
        backdropPool = new ArrayList<FboHandle>();
        backdropPoolPointer = 0;
        stencilAvailable = true;
    }

    private record BackdropSource(FboHandle target, float u0, float vBottom, float u1, float vTop, float uvPerGuiX, float uvPerGuiY) {
    }
}

