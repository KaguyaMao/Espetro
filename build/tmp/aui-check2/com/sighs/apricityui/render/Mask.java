/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  org.joml.Matrix4f
 */
package com.sighs.apricityui.render;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.render.AABB;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.ClipPath;
import com.sighs.apricityui.render.FilterRenderer;
import com.sighs.apricityui.render.Graph;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.spi.FboHandle;
import com.sighs.apricityui.spi.MeshBuilder;
import com.sighs.apricityui.spi.MeshFormat;
import com.sighs.apricityui.spi.MeshMode;
import java.util.ArrayDeque;
import java.util.Stack;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;

public class Mask {
    private static int depth = 0;
    private static final Stack<AABB> clipStack = new Stack();
    private static final Stack<AABB> scissorStack = new Stack();
    private static final Stack<MaskMode> maskModeStack = new Stack();
    private static final Stack<MaskMode> clipPathModeStack = new Stack();
    private static final Stack<AABB> clipPathScissorStack = new Stack();
    private static final Stack<SurfaceClipState> surfaceClipStack = new Stack();
    private static final ThreadLocal<ArrayDeque<Double>> scissorScaleStack = ThreadLocal.withInitial(ArrayDeque::new);
    private static final ThreadLocal<Integer> forceStencilDepth = ThreadLocal.withInitial(() -> 0);
    private static AABB currentScissor = null;
    private static AABB currentClip = new AABB(0.0f, 0.0f, 100000.0f, 100000.0f);
    private static SurfaceScissorTransform surfaceScissorTransform = null;

    public static void resetDepth() {
        Size window = Size.getWindowSize();
        Mask.resetDepth(window.width(), window.height());
    }

    public static void resetDepth(double width, double height) {
        depth = 0;
        clipStack.clear();
        scissorStack.clear();
        maskModeStack.clear();
        clipPathModeStack.clear();
        clipPathScissorStack.clear();
        surfaceClipStack.clear();
        currentScissor = null;
        surfaceScissorTransform = null;
        Mask.disableScissor();
        currentClip = new AABB(0.0f, 0.0f, (float)Math.max(1.0, width), (float)Math.max(1.0, height));
    }

    public static AABB getCurrentClip() {
        return currentClip;
    }

    public static AABB getCurrentScissor() {
        return currentScissor;
    }

    public static void restoreScissor(AABB rect) {
        currentScissor = rect;
        Mask.applyScissor(currentScissor);
    }

    public static void pushScissorScale(double scale) {
        double safeScale = scale > 0.0 && Double.isFinite(scale) ? scale : -1.0;
        scissorScaleStack.get().push(safeScale);
    }

    public static void popScissorScale() {
        ArrayDeque<Double> stack = scissorScaleStack.get();
        if (!stack.isEmpty()) {
            stack.pop();
        }
        if (stack.isEmpty()) {
            scissorScaleStack.remove();
        }
    }

    public static void pushForceStencil() {
        forceStencilDepth.set(forceStencilDepth.get() + 1);
    }

    public static void popForceStencil() {
        int depth = forceStencilDepth.get();
        if (depth <= 1) {
            forceStencilDepth.remove();
        } else {
            forceStencilDepth.set(depth - 1);
        }
    }

    public static boolean isActive() {
        return depth > 0;
    }

    public static void pushSurfaceClip(double width, double height, double offsetX, double offsetY, double scaleX, double scaleY) {
        Base.commitDraws();
        surfaceClipStack.push(new SurfaceClipState(currentClip, currentScissor, surfaceScissorTransform));
        currentScissor = currentClip = new AABB(0.0f, 0.0f, (float)width, (float)height);
        surfaceScissorTransform = new SurfaceScissorTransform(offsetX, offsetY, scaleX, scaleY);
        Mask.applyScissor(currentScissor);
    }

    public static void popSurfaceClip() {
        Base.commitDraws();
        if (surfaceClipStack.isEmpty()) {
            return;
        }
        SurfaceClipState previous = surfaceClipStack.pop();
        currentClip = previous.clip();
        currentScissor = previous.scissor();
        surfaceScissorTransform = previous.transform();
        if (currentScissor == null) {
            Mask.disableScissor();
        } else {
            Mask.applyScissor(currentScissor);
        }
    }

    private static void beginStencilIfNeeded() {
        if (depth == 0) {
            FboHandle currentTarget = FilterRenderer.getCurrentTarget();
            AuiServices.render().enableStencil(currentTarget);
            AuiServices.render().bindWrite(currentTarget, false);
            AuiServices.render().enableStencilTest();
            AuiServices.render().setStencilMask(255);
            AuiServices.render().clearStencilBuffer();
        }
    }

    public static void pushMask(PoseStack pose, float x, float y, float width, float height, float[] radii) {
        Mask.pushMask(pose, x, y, width, height, radii, false);
    }

    public static void pushMask(PoseStack pose, float x, float y, float width, float height, float[] radii, boolean forceStencil) {
        boolean forced;
        boolean bl = forced = forceStencil || forceStencilDepth.get() > 0;
        MaskMode mode = !Mask.stencilUsable() ? (forced ? MaskMode.NONE : MaskMode.SCISSOR) : (!forced && Mask.isRectMask(radii) ? MaskMode.SCISSOR : MaskMode.STENCIL);
        maskModeStack.push(mode);
        if (mode == MaskMode.SCISSOR) {
            Base.commitDraws();
            scissorStack.push(currentScissor);
            AABB newMask = new AABB(x, y, width, height);
            clipStack.push(currentClip);
            currentClip = currentClip.intersection(newMask);
            currentScissor = currentScissor == null ? newMask : currentScissor.intersection(newMask);
            Mask.applyScissor(currentScissor);
            return;
        }
        Base.commitDraws();
        clipStack.push(currentClip);
        currentClip = currentClip.intersection(new AABB(x, y, width, height));
        if (mode == MaskMode.NONE) {
            return;
        }
        Mask.beginStencilIfNeeded();
        pose.m_85836_();
        StencilDepthState state = Mask.setupStencilStatePush();
        Mask.drawToStencil(pose.m_85850_().m_252922_(), x, y, width, height, radii);
        Mask.restoreRenderState(state);
        AuiServices.render().setStencilFunc(514, ++depth, 255);
        AuiServices.render().setStencilMask(0);
        pose.m_85849_();
    }

    public static void popMask(PoseStack pose, float x, float y, float width, float height, float[] radii) {
        MaskMode mode;
        MaskMode maskMode = mode = maskModeStack.isEmpty() ? MaskMode.STENCIL : maskModeStack.pop();
        if (mode == MaskMode.SCISSOR) {
            Base.commitDraws();
            if (!clipStack.isEmpty()) {
                currentClip = clipStack.pop();
            }
            AABB aABB = currentScissor = scissorStack.isEmpty() ? null : scissorStack.pop();
            if (currentScissor == null) {
                Mask.disableScissor();
            } else {
                Mask.applyScissor(currentScissor);
            }
            return;
        }
        Base.commitDraws();
        if (!clipStack.isEmpty()) {
            currentClip = clipStack.pop();
        }
        if (mode == MaskMode.NONE) {
            return;
        }
        if (depth <= 1) {
            depth = 0;
            Mask.finishStencilPop();
            return;
        }
        pose.m_85836_();
        StencilDepthState state = Mask.setupStencilStatePop();
        Mask.drawToStencil(pose.m_85850_().m_252922_(), x, y, width, height, radii);
        --depth;
        Mask.restoreRenderState(state);
        Mask.finishStencilPop();
        pose.m_85849_();
    }

    private static void drawToStencil(Matrix4f matrix, float x, float y, float width, float height, float[] radii) {
        MeshBuilder mesh = AuiServices.render().beginMesh(MeshMode.TRIANGLES, MeshFormat.POSITION);
        Base.setMesh(mesh);
        Base.setPositionColorShader();
        Graph.addUnifiedRoundedRectVertices(mesh, matrix, x, y, width, height, radii, -1);
        mesh.submit();
        Base.setMesh(null);
    }

    private static StencilDepthState setupStencilStatePush() {
        StencilDepthState state = Mask.captureStencilDepthState();
        AuiServices.render().setColorMask(false, false, false, false);
        AuiServices.render().disableDepthTest();
        AuiServices.render().setDepthMask(false);
        AuiServices.render().disableCull();
        AuiServices.render().setStencilFunc(514, depth, 255);
        AuiServices.render().setStencilOp(7680, 7680, 7682);
        AuiServices.render().setStencilMask(255);
        return state;
    }

    private static StencilDepthState setupStencilStatePop() {
        StencilDepthState state = Mask.captureStencilDepthState();
        AuiServices.render().setColorMask(false, false, false, false);
        AuiServices.render().disableDepthTest();
        AuiServices.render().setDepthMask(false);
        AuiServices.render().disableCull();
        AuiServices.render().setStencilFunc(514, depth, 255);
        AuiServices.render().setStencilOp(7680, 7680, 7683);
        AuiServices.render().setStencilMask(255);
        return state;
    }

    private static StencilDepthState captureStencilDepthState() {
        return new StencilDepthState(AuiServices.render().isDepthTestEnabled(), AuiServices.render().isDepthMaskEnabled(), AuiServices.render().isCullEnabled());
    }

    private static void restoreRenderState(StencilDepthState state) {
        AuiServices.render().setColorMask(true, true, true, true);
        AuiServices.render().setDepthMask(state.depthWriteEnabled());
        if (state.depthTestEnabled()) {
            AuiServices.render().enableDepthTest();
        } else {
            AuiServices.render().disableDepthTest();
        }
        if (state.cullEnabled()) {
            AuiServices.render().enableCull();
        } else {
            AuiServices.render().disableCull();
        }
    }

    public static void pushClipPath(PoseStack pose, float x, float y, float width, float height, String clipPathValue) {
        Mask.pushClipPath(pose, x, y, width, height, clipPathValue, false);
    }

    public static void pushClipPath(PoseStack pose, float x, float y, float width, float height, String clipPathValue, boolean forceStencil) {
        boolean forced;
        boolean bl = forced = forceStencil || forceStencilDepth.get() > 0;
        MaskMode mode = !Mask.stencilUsable() ? (forced ? MaskMode.NONE : MaskMode.SCISSOR) : MaskMode.STENCIL;
        clipPathModeStack.push(mode);
        clipStack.push(currentClip);
        AABB newMask = new AABB(x, y, width, height);
        currentClip = currentClip.intersection(newMask);
        if (mode == MaskMode.NONE) {
            return;
        }
        if (mode == MaskMode.SCISSOR) {
            Base.commitDraws();
            clipPathScissorStack.push(currentScissor);
            currentScissor = currentScissor == null ? newMask : currentScissor.intersection(newMask);
            Mask.applyScissor(currentScissor);
            return;
        }
        Base.commitDraws();
        Mask.beginStencilIfNeeded();
        pose.m_85836_();
        StencilDepthState state = Mask.setupStencilStatePush();
        Mask.drawClipToStencil(pose.m_85850_().m_252922_(), x, y, width, height, clipPathValue);
        Mask.restoreRenderState(state);
        AuiServices.render().setStencilFunc(514, ++depth, 255);
        AuiServices.render().setStencilMask(0);
        pose.m_85849_();
    }

    public static void popClipPath(PoseStack pose, float x, float y, float width, float height, String clipPathValue) {
        MaskMode mode;
        MaskMode maskMode = mode = clipPathModeStack.isEmpty() ? MaskMode.STENCIL : clipPathModeStack.pop();
        if (mode == MaskMode.SCISSOR) {
            Base.commitDraws();
            if (!clipStack.isEmpty()) {
                currentClip = clipStack.pop();
            }
            AABB aABB = currentScissor = clipPathScissorStack.isEmpty() ? null : clipPathScissorStack.pop();
            if (currentScissor == null) {
                Mask.disableScissor();
            } else {
                Mask.applyScissor(currentScissor);
            }
            return;
        }
        Base.commitDraws();
        if (!clipStack.isEmpty()) {
            currentClip = clipStack.pop();
        }
        if (mode == MaskMode.NONE) {
            return;
        }
        if (depth <= 1) {
            depth = 0;
            Mask.finishStencilPop();
            return;
        }
        pose.m_85836_();
        StencilDepthState state = Mask.setupStencilStatePop();
        Mask.drawClipToStencil(pose.m_85850_().m_252922_(), x, y, width, height, clipPathValue);
        --depth;
        Mask.restoreRenderState(state);
        Mask.finishStencilPop();
        pose.m_85849_();
    }

    private static void finishStencilPop() {
        if (depth > 0) {
            AuiServices.render().setStencilFunc(514, depth, 255);
            AuiServices.render().setStencilMask(0);
            return;
        }
        AuiServices.render().disableStencilTest();
        AuiServices.render().setStencilMask(255);
    }

    private static void drawClipToStencil(Matrix4f matrix, float x, float y, float width, float height, String clipPath) {
        MeshBuilder mesh = AuiServices.render().beginMesh(MeshMode.TRIANGLES, MeshFormat.POSITION);
        Base.setMesh(mesh);
        Base.setPositionColorShader();
        ClipPath.drawToStencil(matrix, x, y, width, height, clipPath);
        mesh.submit();
        Base.setMesh(null);
    }

    public static void enableScissor(double x, double y, double width, double height) {
        Window window = Minecraft.m_91087_().m_91268_();
        double scale = Mask.getScissorScale(window);
        double left = x * scale;
        double top = y * scale;
        double right = (x + width) * scale;
        double bottom = (y + height) * scale;
        DeviceScissor scissor = Mask.quantizeScissor(left, top, right, bottom, window.m_85442_());
        AuiServices.render().enableScissorTest();
        AuiServices.render().scissorBox(scissor.x(), scissor.y(), scissor.width(), scissor.height());
    }

    static DeviceScissor quantizeScissor(double left, double top, double right, double bottom, int framebufferHeight) {
        int x0 = Mask.quantizeDeviceEdge(left);
        int x1 = Mask.quantizeDeviceEdge(right);
        int y0 = Mask.quantizeDeviceEdge((double)framebufferHeight - bottom);
        int y1 = Mask.quantizeDeviceEdge((double)framebufferHeight - top);
        return new DeviceScissor(x0, y0, Math.max(0, x1 - x0), Math.max(0, y1 - y0));
    }

    private static int quantizeDeviceEdge(double value) {
        if (!Double.isFinite(value)) {
            return 0;
        }
        return (int)Math.floor(value + 0.5);
    }

    public static void disableScissor() {
        AuiServices.render().disableScissorTest();
    }

    private static void applyScissor(AABB rect) {
        if (rect == null || !rect.isValid()) {
            Mask.disableScissor();
            return;
        }
        if (surfaceScissorTransform != null) {
            surfaceScissorTransform.apply(rect);
            return;
        }
        Mask.enableScissor(rect.x(), rect.y(), rect.width(), rect.height());
    }

    private static double getScissorScale(Window window) {
        double scale;
        ArrayDeque<Double> stack = scissorScaleStack.get();
        if (!stack.isEmpty() && (scale = stack.peek().doubleValue()) > 0.0 && Double.isFinite(scale)) {
            return scale;
        }
        return Math.max(1.0, window.m_85449_());
    }

    private static boolean isRectMask(float[] radii) {
        if (radii == null || radii.length == 0) {
            return true;
        }
        for (float r : radii) {
            if (!(r > 0.001f)) continue;
            return false;
        }
        return true;
    }

    private static boolean stencilUsable() {
        return FilterRenderer.isStencilAvailable() && AuiServices.render().currentTargetHasStencil();
    }

    private record SurfaceScissorTransform(double offsetX, double offsetY, double scaleX, double scaleY) {
        private void apply(AABB rect) {
            Window window = Minecraft.m_91087_().m_91268_();
            double guiScale = Math.max(1.0, window.m_85449_());
            double left = (this.offsetX + (double)rect.x() * this.scaleX) * guiScale;
            double top = (this.offsetY + (double)rect.y() * this.scaleY) * guiScale;
            double right = (this.offsetX + (double)(rect.x() + rect.width()) * this.scaleX) * guiScale;
            double bottom = (this.offsetY + (double)(rect.y() + rect.height()) * this.scaleY) * guiScale;
            DeviceScissor scissor = Mask.quantizeScissor(left, top, right, bottom, window.m_85442_());
            AuiServices.render().enableScissorTest();
            AuiServices.render().scissorBox(scissor.x(), scissor.y(), scissor.width(), scissor.height());
        }
    }

    private record SurfaceClipState(AABB clip, AABB scissor, SurfaceScissorTransform transform) {
    }

    private static enum MaskMode {
        SCISSOR,
        STENCIL,
        NONE;

    }

    private record StencilDepthState(boolean depthTestEnabled, boolean depthWriteEnabled, boolean cullEnabled) {
    }

    record DeviceScissor(int x, int y, int width, int height) {
    }
}

