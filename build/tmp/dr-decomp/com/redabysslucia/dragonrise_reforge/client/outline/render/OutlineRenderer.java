/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.CameraType
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.material.FogType
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.client.event.RenderGuiEvent$Pre
 *  net.minecraftforge.client.event.RenderLevelStageEvent
 *  net.minecraftforge.client.event.RenderLevelStageEvent$Stage
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.ModList
 *  org.joml.Matrix4f
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL30
 */
package com.redabysslucia.dragonrise_reforge.client.outline.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redabysslucia.dragonrise_reforge.client.outline.render.EntityMaskRenderer;
import com.redabysslucia.dragonrise_reforge.client.outline.render.OutlineFramebuffer;
import com.redabysslucia.dragonrise_reforge.client.outline.render.OutlineShader;
import java.io.IOException;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class OutlineRenderer {
    private static final boolean OCULUS_LOADED = ModList.get().isLoaded("oculus");
    private static final boolean AR_LOADED = ModList.get().isLoaded("acceleratedrendering");
    private static OutlineFramebuffer maskFramebuffer;
    private static OutlineFramebuffer edgeFramebuffer;
    private static OutlineFramebuffer blurFramebuffer1;
    private static OutlineFramebuffer blurFramebuffer2;
    private static OutlineFramebuffer blackOutlineFramebuffer;
    private static OutlineShader maskShader;
    private static OutlineShader blurShader;
    private static OutlineShader applyShader;
    private static OutlineShader blackOutlineShader;
    private static int processedMaskTexture;
    private static int quadVAO;
    private static int quadVBO;
    private static BufferBuilder maskBuffer;
    private static MultiBufferSource.BufferSource maskBufferSource;
    private static int lastEntityCount;
    private static int framesSinceLastCheck;
    private static final int CHECK_INTERVAL = 5;
    private static boolean renderThisFrame;
    private static RenderMode renderMode;
    private static Predicate<Entity> outlinePredicate;
    private static Function<Entity, Integer> colorProvider;
    private static float outlineR;
    private static float outlineG;
    private static float outlineB;
    private static float outlineA;
    private static boolean useColoredOutline;
    private static boolean useBlackOutline;

    public static void register() {
        MinecraftForge.EVENT_BUS.register(OutlineRenderer.class);
        System.out.println("OutlineRenderer registered to event bus");
    }

    public static void setRenderMode(RenderMode mode) {
        renderMode = mode;
    }

    public static RenderMode getRenderMode() {
        return renderMode;
    }

    public static void setOutlineColorProvider(Function<Entity, Integer> provider) {
        colorProvider = provider;
    }

    public static void setOutlinePredicate(Predicate<Entity> predicate) {
        outlinePredicate = predicate;
    }

    public static void setOutlineColor(float r, float g, float b, float a) {
        outlineR = r;
        outlineG = g;
        outlineB = b;
        outlineA = a;
    }

    public static void setUseColoredOutline(boolean use) {
        useColoredOutline = use;
    }

    public static boolean isUsingColoredOutline() {
        return useColoredOutline;
    }

    public static void setUseBlackOutline(boolean use) {
        useBlackOutline = use;
    }

    public static boolean isUsingBlackOutline() {
        return useBlackOutline;
    }

    public static void init() {
        Minecraft mc = Minecraft.m_91087_();
        int width = mc.m_91268_().m_85441_();
        int height = mc.m_91268_().m_85442_();
        maskFramebuffer = new OutlineFramebuffer(width, height, true);
        edgeFramebuffer = new OutlineFramebuffer(width, height);
        blurFramebuffer1 = new OutlineFramebuffer(width, height);
        blurFramebuffer2 = new OutlineFramebuffer(width, height);
        blackOutlineFramebuffer = new OutlineFramebuffer(width, height);
        try {
            maskShader = new OutlineShader("mask");
            blurShader = new OutlineShader("blur");
            applyShader = new OutlineShader("apply");
            blackOutlineShader = new OutlineShader("black_outline");
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load outline shaders", e);
        }
        OutlineRenderer.createQuadVAO();
        maskBuffer = new BufferBuilder(262144);
        maskBufferSource = MultiBufferSource.m_109898_((BufferBuilder)maskBuffer);
    }

    private static void minimalStateReset() {
        GL20.glUseProgram((int)0);
        GlStateManager._glBindFramebuffer((int)36160, (int)0);
        for (int i = 0; i < 4; ++i) {
            RenderSystem.activeTexture((int)(33984 + i));
            GlStateManager._bindTexture((int)0);
        }
        RenderSystem.activeTexture((int)33984);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc((int)515);
        RenderSystem.depthMask((boolean)true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        GL30.glBindVertexArray((int)0);
    }

    private static void createQuadVAO() {
        quadVAO = GL30.glGenVertexArrays();
        quadVBO = GL30.glGenBuffers();
        float[] quadVertices = new float[]{-1.0f, 1.0f, 0.0f, 1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f};
        GL30.glBindVertexArray((int)quadVAO);
        GL30.glBindBuffer((int)34962, (int)quadVBO);
        GL30.glBufferData((int)34962, (float[])quadVertices, (int)35044);
        GL30.glEnableVertexAttribArray((int)0);
        GL30.glVertexAttribPointer((int)0, (int)2, (int)5126, (boolean)false, (int)16, (long)0L);
        GL30.glEnableVertexAttribArray((int)1);
        GL30.glVertexAttribPointer((int)1, (int)2, (int)5126, (boolean)false, (int)16, (long)8L);
        GL30.glBindVertexArray((int)0);
    }

    public static void resize(int width, int height) {
        if (maskFramebuffer != null) {
            maskFramebuffer.resize(width, height);
            edgeFramebuffer.resize(width, height);
            blurFramebuffer1.resize(width, height);
            blurFramebuffer2.resize(width, height);
            blackOutlineFramebuffer.resize(width, height);
        }
    }

    public static void cleanup() {
        if (maskFramebuffer != null) {
            maskFramebuffer.destroy();
            edgeFramebuffer.destroy();
            blurFramebuffer1.destroy();
            blurFramebuffer2.destroy();
            blackOutlineFramebuffer.destroy();
            maskFramebuffer = null;
        }
        if (maskShader != null) {
            maskShader.close();
            blurShader.close();
            applyShader.close();
            blackOutlineShader.close();
            maskShader = null;
        }
        if (quadVAO != -1) {
            GL30.glDeleteVertexArrays((int)quadVAO);
            GL30.glDeleteBuffers((int)quadVBO);
            quadVAO = -1;
            quadVBO = -1;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (renderMode == RenderMode.OFF) {
            return;
        }
        if (OCULUS_LOADED) {
            return;
        }
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            Minecraft mc = Minecraft.m_91087_();
            boolean arDisabled = false;
            try {
                MultiBufferSource.BufferSource bufferSource = mc.m_91269_().m_110104_();
                bufferSource.m_109911_();
                if (AR_LOADED) {
                    arDisabled = false;
                }
                if (OutlineRenderer.captureMobMasks(event.getPoseStack(), event.getProjectionMatrix())) {
                    renderThisFrame = true;
                } else if (renderThisFrame) {
                    renderThisFrame = false;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (!arDisabled || AR_LOADED) {
                    // empty if block
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderScreenEffects(RenderGuiEvent.Pre event) {
        if (renderThisFrame) {
            OutlineRenderer.drawOverlayToScreen();
            renderThisFrame = false;
        }
    }

    private static boolean captureMobMasks(PoseStack poseStack, Matrix4f projectionMatrix) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91073_ == null || maskFramebuffer == null) {
            System.out.println("captureMobMasks: level or framebuffer is null");
            return false;
        }
        int count = 0;
        if (++framesSinceLastCheck >= 5) {
            for (Entity entity : mc.f_91073_.m_104735_()) {
                if (!outlinePredicate.test(entity)) continue;
                ++count;
                EntityRenderer renderer = mc.m_91290_().m_114382_(entity);
                if (renderer == null) continue;
                System.out.println("captureMobMasks: found entity " + entity.m_7755_() + " with renderer " + renderer.getClass().getName());
            }
            lastEntityCount = count;
            framesSinceLastCheck = 0;
            System.out.println("captureMobMasks: found " + count + " entities to outline");
        } else {
            count = lastEntityCount;
        }
        if (count == 0) {
            System.out.println("captureMobMasks: no entities to outline");
            return false;
        }
        if (!useColoredOutline && !useBlackOutline) {
            System.out.println("captureMobMasks: outline disabled");
            return false;
        }
        try {
            System.out.println("captureMobMasks: rendering entity masks");
            OutlineRenderer.renderEntityMasks(poseStack, projectionMatrix);
            return true;
        }
        catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    private static void drawOverlayToScreen() {
        try {
            RenderSystem.clearStencil((int)0);
            GL11.glClear((int)1024);
            Minecraft mc = Minecraft.m_91087_();
            MultiBufferSource.BufferSource bufferSource = mc.m_91269_().m_110104_();
            bufferSource.m_109911_();
            mc.m_91385_().m_83947_(false);
            OutlineRenderer.extractEdges();
            OutlineRenderer.applyBlur(true);
            OutlineRenderer.applyBlur(false);
            if (useBlackOutline) {
                OutlineRenderer.createBlackOutline();
            }
            OutlineRenderer.compositeOutline();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            OutlineRenderer.minimalStateReset();
            Minecraft.m_91087_().m_91385_().m_83947_(false);
        }
    }

    private static void renderEntityMasks(PoseStack poseStack, Matrix4f projectionMatrix) {
        boolean canShareDepth;
        Minecraft mc = Minecraft.m_91087_();
        int targetWidth = mc.m_91385_().f_83915_;
        int targetHeight = mc.m_91385_().f_83916_;
        if (OutlineRenderer.maskFramebuffer.width != targetWidth || OutlineRenderer.maskFramebuffer.height != targetHeight) {
            OutlineRenderer.resize(targetWidth, targetHeight);
        }
        int mainFbo = mc.m_91385_().f_83920_;
        GL30.glBindFramebuffer((int)36008, (int)mainFbo);
        int depthType = GL30.glGetFramebufferAttachmentParameteri((int)36008, (int)36096, (int)36048);
        int depthId = GL30.glGetFramebufferAttachmentParameteri((int)36008, (int)36096, (int)36049);
        boolean bl = canShareDepth = depthId != 0;
        if (canShareDepth) {
            maskFramebuffer.attachExternalDepth(depthType, depthId);
        } else {
            maskFramebuffer.restoreInternalDepth();
        }
        maskFramebuffer.bind();
        RenderSystem.clearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        if (canShareDepth) {
            RenderSystem.clear((int)16384, (boolean)false);
        } else {
            RenderSystem.clear((int)16640, (boolean)false);
        }
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc((int)515);
        RenderSystem.depthMask((boolean)false);
        RenderSystem.colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        GL11.glDisable((int)2960);
        if (maskBufferSource == null) {
            maskBuffer = new BufferBuilder(262144);
            maskBufferSource = MultiBufferSource.m_109898_((BufferBuilder)maskBuffer);
        }
        boolean oldRenderShadows = (Boolean)mc.f_91066_.m_231818_().m_231551_();
        boolean oldRenderHitboxes = mc.m_91290_().m_114377_();
        mc.m_91290_().m_114468_(false);
        mc.m_91290_().m_114473_(false);
        float partialTick = mc.m_91296_();
        int renderDistanceChunks = (Integer)mc.f_91066_.m_231984_().m_231551_();
        double renderDistanceBlocks = (double)renderDistanceChunks * 16.0;
        FogType fogType = mc.f_91063_.m_109153_().m_167685_();
        if (fogType == FogType.WATER || fogType == FogType.LAVA) {
            renderDistanceBlocks = Math.min(renderDistanceBlocks, 32.0);
        }
        double renderDistanceSquared = renderDistanceBlocks * renderDistanceBlocks;
        for (Entity entity : mc.f_91073_.m_104735_()) {
            if (!outlinePredicate.test(entity) || entity == mc.f_91074_ && mc.f_91066_.m_92176_() == CameraType.FIRST_PERSON) continue;
            double lerpX = entity.f_19790_ + (entity.m_20185_() - entity.f_19790_) * (double)partialTick;
            double lerpY = entity.f_19791_ + (entity.m_20186_() - entity.f_19791_) * (double)partialTick;
            double lerpZ = entity.f_19792_ + (entity.m_20189_() - entity.f_19792_) * (double)partialTick;
            Vec3 cameraPos = mc.f_91063_.m_109153_().m_90583_();
            double dx = lerpX - cameraPos.f_82479_;
            double dy = lerpY - cameraPos.f_82480_;
            double dz = lerpZ - cameraPos.f_82481_;
            double distanceSquared = dx * dx + dy * dy + dz * dz;
            if (distanceSquared > renderDistanceSquared) continue;
            if (colorProvider != null) {
                int color = colorProvider.apply(entity);
                float r = (float)(color >> 16 & 0xFF) / 255.0f;
                float g = (float)(color >> 8 & 0xFF) / 255.0f;
                float b = (float)(color & 0xFF) / 255.0f;
                float a = (float)(color >> 24 & 0xFF) / 255.0f;
                if (a == 0.0f) {
                    a = 1.0f;
                }
                RenderSystem.setShaderColor((float)r, (float)g, (float)b, (float)a);
            } else {
                RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            }
            EntityMaskRenderer.renderEntityMask(entity, lerpX, lerpY, lerpZ, partialTick, poseStack, projectionMatrix, maskBufferSource);
            if (colorProvider == null) continue;
            maskBufferSource.m_109911_();
        }
        if (colorProvider == null) {
            maskBufferSource.m_109911_();
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        mc.m_91290_().m_114468_(oldRenderShadows);
        mc.m_91290_().m_114473_(oldRenderHitboxes);
        RenderSystem.depthMask((boolean)true);
        RenderSystem.depthFunc((int)515);
        maskFramebuffer.restoreInternalDepth();
        mc.m_91385_().m_83947_(false);
    }

    private static void extractEdges() {
        edgeFramebuffer.bind();
        RenderSystem.clearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        RenderSystem.clear((int)16384, (boolean)false);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        GL30.glBindVertexArray((int)quadVAO);
        maskShader.use();
        int sourceMaskTex = processedMaskTexture != 0 ? processedMaskTexture : maskFramebuffer.getColorTexture();
        maskShader.setTexture("DiffuseSampler", sourceMaskTex);
        maskShader.setUniform("InSize", OutlineRenderer.maskFramebuffer.width, OutlineRenderer.maskFramebuffer.height);
        maskShader.setUniform("OutSize", OutlineRenderer.edgeFramebuffer.width, OutlineRenderer.edgeFramebuffer.height);
        OutlineRenderer.drawFullscreenQuad();
        blurFramebuffer1.bind();
        RenderSystem.clearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        RenderSystem.clear((int)16384, (boolean)false);
        applyShader.use();
        applyShader.setTexture("DiffuseSampler", edgeFramebuffer.getColorTexture());
        applyShader.setUniform("OutlineColor", 1.0f, 1.0f, 1.0f, 1.0f);
        applyShader.setUniform("UseSourceColor", 1);
        OutlineRenderer.drawFullscreenQuad();
        Minecraft.m_91087_().m_91385_().m_83947_(false);
    }

    private static void applyBlur(boolean horizontal) {
        OutlineFramebuffer source = horizontal ? blurFramebuffer1 : blurFramebuffer2;
        OutlineFramebuffer target = horizontal ? blurFramebuffer2 : blurFramebuffer1;
        target.bind();
        RenderSystem.clearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        RenderSystem.clear((int)16384, (boolean)false);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        RenderSystem.disableBlend();
        GL30.glBindVertexArray((int)quadVAO);
        blurShader.use();
        blurShader.setTexture("DiffuseSampler", source.getColorTexture());
        blurShader.setUniform("InSize", source.width, source.height);
        blurShader.setUniform("OutSize", target.width, target.height);
        blurShader.setUniform("BlurDir", horizontal ? 1.0f : 0.0f, horizontal ? 0.0f : 1.0f);
        blurShader.setUniform("Radius", 1.0f);
        OutlineRenderer.drawFullscreenQuad();
        Minecraft.m_91087_().m_91385_().m_83947_(false);
    }

    private static void createBlackOutline() {
        blackOutlineFramebuffer.bind();
        RenderSystem.clearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        RenderSystem.clear((int)16384, (boolean)false);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        RenderSystem.disableBlend();
        GL30.glBindVertexArray((int)quadVAO);
        blackOutlineShader.use();
        blackOutlineShader.setTexture("DiffuseSampler", edgeFramebuffer.getColorTexture());
        blackOutlineShader.setTexture("EntityMask", maskFramebuffer.getColorTexture());
        blackOutlineShader.setUniform("InSize", OutlineRenderer.edgeFramebuffer.width, OutlineRenderer.edgeFramebuffer.height);
        blackOutlineShader.setUniform("OutSize", OutlineRenderer.blackOutlineFramebuffer.width, OutlineRenderer.blackOutlineFramebuffer.height);
        blackOutlineShader.setUniform("Radius", 1.5f);
        OutlineRenderer.drawFullscreenQuad();
        Minecraft.m_91087_().m_91385_().m_83947_(false);
    }

    private static void compositeOutline() {
        int sourceMaskTex;
        Minecraft mc = Minecraft.m_91087_();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GL30.glBindVertexArray((int)quadVAO);
        int n = sourceMaskTex = processedMaskTexture != 0 ? processedMaskTexture : maskFramebuffer.getColorTexture();
        if (renderMode == RenderMode.OUTLINE) {
            if (useBlackOutline) {
                applyShader.use();
                applyShader.setTexture("DiffuseSampler", blackOutlineFramebuffer.getColorTexture());
                applyShader.setUniform("OutlineColor", 0.0f, 0.0f, 0.0f, 1.0f);
                applyShader.setUniform("UseSourceColor", 0);
                OutlineRenderer.drawFullscreenQuad();
            }
            if (useColoredOutline) {
                applyShader.use();
                applyShader.setTexture("DiffuseSampler", blurFramebuffer1.getColorTexture());
                applyShader.setUniform("OutlineColor", outlineR, outlineG, outlineB, outlineA);
                if (colorProvider != null) {
                    applyShader.setUniform("UseSourceColor", 1);
                } else {
                    applyShader.setUniform("UseSourceColor", 0);
                }
                OutlineRenderer.drawFullscreenQuad();
            }
        } else if (renderMode == RenderMode.OVERLAY) {
            applyShader.use();
            applyShader.setTexture("DiffuseSampler", sourceMaskTex);
            applyShader.setUniform("OutlineColor", outlineR, outlineG, outlineB, outlineA);
            if (colorProvider != null) {
                applyShader.setUniform("UseSourceColor", 1);
            } else {
                applyShader.setUniform("UseSourceColor", 0);
            }
            OutlineRenderer.drawFullscreenQuad();
        }
        processedMaskTexture = 0;
        RenderSystem.disableBlend();
    }

    private static void drawFullscreenQuad() {
        GL30.glBindVertexArray((int)quadVAO);
        GL30.glDrawArrays((int)4, (int)0, (int)6);
    }

    static {
        processedMaskTexture = 0;
        quadVAO = -1;
        quadVBO = -1;
        maskBuffer = null;
        maskBufferSource = null;
        lastEntityCount = 0;
        framesSinceLastCheck = 0;
        renderThisFrame = false;
        renderMode = RenderMode.OFF;
        outlinePredicate = entity -> entity instanceof LivingEntity;
        colorProvider = null;
        outlineR = 1.0f;
        outlineG = 1.0f;
        outlineB = 1.0f;
        outlineA = 1.0f;
        useColoredOutline = true;
        useBlackOutline = true;
    }

    public static enum RenderMode {
        OFF,
        OUTLINE,
        OVERLAY;

    }
}

