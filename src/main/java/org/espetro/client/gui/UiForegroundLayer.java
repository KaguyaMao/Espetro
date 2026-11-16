package org.espetro.client.gui;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.Objects;

/** Frame-time opacity for a whole foreground tree, including self-painted widgets. */
@OnlyIn(Dist.CLIENT)
public final class UiForegroundLayer {
    private static final org.slf4j.Logger LOGGER = LogUtils.getLogger();
    private static TextureTarget target;
    private static int renderingDepth;
    private static boolean disabled;

    private UiForegroundLayer() {
    }

    /**
     * Draw backgrounds before calling this method. Only the foreground callback fades.
     * Opaque calls draw directly, zero opacity skips the callback, and nested calls share
     * their parent's layer rather than copying an already partly painted foreground.
     * The callback must balance its own pose/scissor stacks and must not bind another FBO.
     */
    public static void draw(GuiGraphics graphics, int guiWidth, int guiHeight,
                            float opacity, Runnable foregroundDraw) {
        Objects.requireNonNull(foregroundDraw, "foregroundDraw");
        float alpha = normalizedOpacity(opacity);
        if (alpha <= 0.0F) return;
        if (alpha >= 1.0F || disabled || renderingDepth > 0 || graphics == null
            || guiWidth <= 0 || guiHeight <= 0 || !RenderSystem.isOnRenderThread()) {
            foregroundDraw.run();
            return;
        }

        State state;
        try {
            state = new State();
        } catch (RuntimeException | LinkageError exception) {
            disable(exception);
            foregroundDraw.run();
            return;
        }
        boolean fallback = false;
        int phase = 0; // Setup failures can fall back; exceptions from caller code propagate.
        renderingDepth++;
        try {
            graphics.flush();
            int pixelWidth = Math.max(Minecraft.getInstance().getMainRenderTarget().width,
                state.viewport[0] + state.viewport[2]);
            int pixelHeight = Math.max(Minecraft.getInstance().getMainRenderTarget().height,
                state.viewport[1] + state.viewport[3]);
            if (state.viewport[0] < 0 || state.viewport[1] < 0
                || state.viewport[2] <= 0 || state.viewport[3] <= 0) {
                throw new IllegalStateException("Invalid foreground viewport");
            }
            ensureTarget(pixelWidth, pixelHeight);
            copyBackground(state);
            // Constructor, copy and clear have temporary render state. Paint exactly
            // as a normal foreground tree would, with its original viewport and clip.
            state.restore();
            target.bindWrite(false);
            RenderSystem.viewport(state.viewport[0], state.viewport[1], state.viewport[2], state.viewport[3]);
            phase = 1;
            foregroundDraw.run();
            phase = 2;
            graphics.flush();
            if (GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING) != target.frameBufferId) {
                throw new IllegalStateException("Foreground drawing changed the active framebuffer");
            }
            makeAlphaOpaque();
            state.restore();
            // An embedded screen can have a smaller layout width while its pose
            // maps into the window's full GUI projection. Composite that projection.
            composite(graphics.guiWidth(), graphics.guiHeight(), alpha, state);
        } catch (RuntimeException | LinkageError exception) {
            if (phase == 1) throw exception;
            fallback = true;
            disable(exception);
        } finally {
            // If caller code failed with pending geometry, keep that geometry out of
            // the restored destination framebuffer before propagating its exception.
            try {
                if (phase == 1) graphics.flush();
            } finally {
                try {
                    state.restore();
                } finally {
                    renderingDepth--;
                }
            }
        }
        if (fallback) foregroundDraw.run();
    }

    static float normalizedOpacity(float value) {
        return Float.isFinite(value) ? Math.max(0.0F, Math.min(1.0F, value)) : 1.0F;
    }

    private static void ensureTarget(int width, int height) {
        if (target != null && target.width == width && target.height == height) return;
        if (target != null) {
            target.destroyBuffers();
            target = null;
        }
        target = new TextureTarget(width, height, true, Minecraft.ON_OSX);
        target.setFilterMode(GL11.GL_NEAREST);
    }

    private static void copyBackground(State state) {
        RenderSystem.disableScissor();
        RenderSystem.colorMask(true, true, true, true);
        GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, state.drawFramebuffer);
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, target.frameBufferId);
        int left = state.viewport[0], bottom = state.viewport[1];
        int right = left + state.viewport[2], top = bottom + state.viewport[3];
        // Same pixel rectangle on each side: no GUI scaling or color filtering.
        GL30.glBlitFramebuffer(left, bottom, right, top, left, bottom, right, top,
            GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
        int error = GL11.glGetError();
        if (error != GL11.GL_NO_ERROR) {
            throw new IllegalStateException("Foreground background copy failed: 0x" + Integer.toHexString(error));
        }
        RenderSystem.depthMask(true);
        RenderSystem.clearDepth(1.0D);
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
    }

    private static void makeAlphaOpaque() {
        // Vanilla/custom renderers do not all accumulate alpha in the same way.
        // RGB already contains the normal foreground-over-background result; only
        // reset its unused alpha channel, never its RGB or the destination alpha.
        RenderSystem.disableScissor();
        RenderSystem.colorMask(false, false, false, true);
        RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 1.0F);
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, Minecraft.ON_OSX);
    }

    private static void composite(int guiWidth, int guiHeight, float alpha, State state) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableBlend();
        RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA,
            GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.colorMask(state.colorMask[0], state.colorMask[1], state.colorMask[2], false);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, target.getColorTextureId());
        float u0 = state.viewport[0] / (float) target.width;
        float u1 = (state.viewport[0] + state.viewport[2]) / (float) target.width;
        float v0 = state.viewport[1] / (float) target.height;
        float v1 = (state.viewport[1] + state.viewport[3]) / (float) target.height;
        // The callback already used graphics.pose(), including nested preview scale
        // and slide translation. Applying it here again would move the whole scene.
        Matrix4f identity = new Matrix4f();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(identity, 0.0F, guiHeight, 0.0F).uv(u0, v0).endVertex();
        buffer.vertex(identity, guiWidth, guiHeight, 0.0F).uv(u1, v0).endVertex();
        buffer.vertex(identity, guiWidth, 0.0F, 0.0F).uv(u1, v1).endVertex();
        buffer.vertex(identity, 0.0F, 0.0F, 0.0F).uv(u0, v1).endVertex();
        // Texture alpha is one: RGB = alpha * (background + foreground result)
        // + (1-alpha) * identical background. Background visibility never changes.
        BufferUploader.drawWithShader(buffer.end());
    }

    private static void disable(Throwable exception) {
        if (!disabled) {
            disabled = true;
            LOGGER.warn("Foreground opacity compositing unavailable; drawing the normal foreground", exception);
        }
    }

    /** Snapshot before flush/target allocation, independent of FancyMenu's classes. */
    private static final class State {
        private static final int TEXTURE_UNITS = 12;
        final int drawFramebuffer = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        final int readFramebuffer = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        final int[] viewport = new int[4];
        final int[] scissor = new int[4];
        final boolean scissorEnabled = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
        final boolean depth = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        final boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        final int depthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        final double clearDepth = GL11.glGetDouble(GL11.GL_DEPTH_CLEAR_VALUE);
        final boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        final int srcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        final int dstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        final int srcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        final int dstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        final int rgbEquation = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_RGB);
        final int alphaEquation = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_ALPHA);
        final boolean cull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        final boolean colorLogic = GL11.glIsEnabled(GL11.GL_COLOR_LOGIC_OP);
        final boolean[] colorMask = new boolean[4];
        final float[] clearColor = new float[4];
        final float[] shaderColor = RenderSystem.getShaderColor().clone();
        final ShaderInstance shader = RenderSystem.getShader();
        final int activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        final int activeBinding = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        final int[] bindings = new int[TEXTURE_UNITS];
        final int[] shaderTextures = new int[TEXTURE_UNITS];
        final Matrix4f projection = new Matrix4f(RenderSystem.getProjectionMatrix());
        final Matrix4f modelView = new Matrix4f(RenderSystem.getModelViewMatrix());
        final Matrix3f normal = new Matrix3f(RenderSystem.getModelViewStack().last().normal());
        final VertexSorting sorting = RenderSystem.getVertexSorting();

        State() {
            GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
            GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, scissor);
            GL11.glGetFloatv(GL11.GL_COLOR_CLEAR_VALUE, clearColor);
            try (MemoryStack memory = MemoryStack.stackPush()) {
                ByteBuffer mask = memory.malloc(4);
                GL11.glGetBooleanv(GL11.GL_COLOR_WRITEMASK, mask);
                for (int index = 0; index < 4; index++) colorMask[index] = mask.get(index) != 0;
            }
            try {
                for (int index = 0; index < TEXTURE_UNITS; index++) {
                    shaderTextures[index] = RenderSystem.getShaderTexture(index);
                    RenderSystem.activeTexture(GL13.GL_TEXTURE0 + index);
                    bindings[index] = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                }
            } finally {
                RenderSystem.activeTexture(activeTexture);
            }
        }

        void restore() {
            GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, readFramebuffer);
            GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, drawFramebuffer);
            RenderSystem.viewport(viewport[0], viewport[1], viewport[2], viewport[3]);
            if (scissorEnabled) RenderSystem.enableScissor(scissor[0], scissor[1], scissor[2], scissor[3]);
            else RenderSystem.disableScissor();
            RenderSystem.depthFunc(depthFunc);
            RenderSystem.depthMask(depthMask);
            if (depth) RenderSystem.enableDepthTest();
            else RenderSystem.disableDepthTest();
            RenderSystem.clearDepth(clearDepth);
            GL20.glBlendEquationSeparate(rgbEquation, alphaEquation);
            RenderSystem.blendFuncSeparate(srcRgb, dstRgb, srcAlpha, dstAlpha);
            if (blend) RenderSystem.enableBlend();
            else RenderSystem.disableBlend();
            if (cull) RenderSystem.enableCull();
            else RenderSystem.disableCull();
            if (colorLogic) RenderSystem.enableColorLogicOp();
            else RenderSystem.disableColorLogicOp();
            RenderSystem.colorMask(colorMask[0], colorMask[1], colorMask[2], colorMask[3]);
            RenderSystem.clearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
            for (int index = 0; index < TEXTURE_UNITS; index++) {
                RenderSystem.setShaderTexture(index, shaderTextures[index]);
                RenderSystem.activeTexture(GL13.GL_TEXTURE0 + index);
                RenderSystem.bindTexture(bindings[index]);
            }
            RenderSystem.activeTexture(activeTexture);
            RenderSystem.bindTexture(activeBinding);
            RenderSystem.setShader(() -> shader);
            RenderSystem.setShaderColor(shaderColor[0], shaderColor[1], shaderColor[2], shaderColor[3]);
            RenderSystem.setProjectionMatrix(new Matrix4f(projection), sorting);
            RenderSystem.getModelViewStack().last().pose().set(modelView);
            RenderSystem.getModelViewStack().last().normal().set(normal);
            RenderSystem.applyModelViewMatrix();
        }
    }
}
