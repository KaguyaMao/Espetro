/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.event.RenderLevelStageEvent
 *  net.minecraftforge.client.event.RenderLevelStageEvent$Stage
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 */
package com.example.espoints.client;

import com.example.espoints.config.TacticalMapJsonConfig;
import com.example.espoints.tactical.ClientTacticalMarkerState;
import com.example.espoints.tactical.TacticalMarker;
import com.example.espoints.tactical.TacticalMarkerIcons;
import com.example.espoints.tactical.TacticalMarkerType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@OnlyIn(value=Dist.CLIENT)
public final class TacticalMarkerWorldRenderer {
    private static final float BASE_HALF_SIZE = 0.55f;
    private static final float MIN_SCALE = 0.4f;
    private static final float MAX_SCALE = 1.4f;
    private static final int MAX_DRAW = 48;

    private TacticalMarkerWorldRenderer() {
    }

    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null || mc.f_91073_ == null || mc.f_91066_.f_92062_) {
            return;
        }
        List<TacticalMarker> markers = ClientTacticalMarkerState.getMarkers();
        if (markers.isEmpty()) {
            return;
        }
        TacticalMapJsonConfig config = TacticalMapJsonConfig.getInstance();
        double maxDist = config.getTacticalMarkerMaxRenderDistance();
        double maxDistSq = maxDist * maxDist;
        long duration = config.getTacticalMarkerDurationMillis();
        long fadeMs = config.getTacticalMarkerFadeMillis();
        long now = System.currentTimeMillis();
        Vec3 cam = event.getCamera().m_90583_();
        PoseStack poseStack = event.getPoseStack();
        EnumMap<TacticalMarkerType, List> batches = new EnumMap<TacticalMarkerType, List>(TacticalMarkerType.class);
        int drawn = 0;
        for (TacticalMarker marker : markers) {
            double dz;
            double dy;
            double dx;
            double distSq;
            if (drawn >= 48) break;
            float opacity = TacticalMarkerWorldRenderer.opacityOf(marker, duration, fadeMs, now);
            if (opacity <= 0.02f || (distSq = (dx = marker.x() - cam.f_82479_) * dx + (dy = marker.y() - cam.f_82480_) * dy + (dz = marker.z() - cam.f_82481_) * dz) > maxDistSq || distSq < 1.0E-6) continue;
            double dist = Math.sqrt(distSq);
            float half = 0.55f * TacticalMarkerWorldRenderer.scaleForDistance(dist, maxDist);
            int argb = TacticalMarkerWorldRenderer.colorFor(marker.type(), opacity);
            batches.computeIfAbsent(marker.type(), t -> new ArrayList(8)).add(new DrawItem(dx, dy, dz, half, argb));
            ++drawn;
        }
        if (batches.isEmpty()) {
            return;
        }
        Quaternionf cameraRot = event.getCamera().m_253121_();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask((boolean)false);
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::m_172820_);
        for (Map.Entry entry : batches.entrySet()) {
            ResourceLocation tex = TacticalMarkerIcons.textureFor(entry.getKey());
            RenderSystem.setShaderTexture((int)0, (ResourceLocation)tex);
            for (DrawItem item : (List)entry.getValue()) {
                poseStack.m_85836_();
                poseStack.m_85837_(item.dx, item.dy, item.dz);
                poseStack.m_252781_(cameraRot);
                poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0f));
                TacticalMarkerWorldRenderer.drawQuad(poseStack, item.half, item.argb);
                poseStack.m_85849_();
            }
        }
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask((boolean)true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private static float opacityOf(TacticalMarker marker, long duration, long fadeMs, long now) {
        if (marker.type().isPersistentUntilRemoved()) {
            return 1.0f;
        }
        long age = Math.max(0L, now - marker.createdAtMillis());
        long remaining = duration - age;
        if (remaining <= 0L) {
            return 0.0f;
        }
        if (remaining >= fadeMs) {
            return 1.0f;
        }
        return Mth.m_14036_((float)((float)remaining / (float)Math.max(1L, fadeMs)), (float)0.0f, (float)1.0f);
    }

    private static float scaleForDistance(double dist, double maxDist) {
        double t = Mth.m_14008_((double)(dist / Math.max(1.0, maxDist)), (double)0.0, (double)1.0);
        t *= t;
        return (float)((double)1.4f + -1.0 * t);
    }

    private static int colorFor(TacticalMarkerType type, float opacity) {
        int base = switch (type) {
            case TacticalMarkerType.ATTACK_HERE -> TacticalMarkerType.ATTACK_HERE.getColor();
            case TacticalMarkerType.DEFEND_HERE -> TacticalMarkerType.DEFEND_HERE.getColor();
            case TacticalMarkerType.ENEMY_INFANTRY, TacticalMarkerType.ENEMY_TANK, TacticalMarkerType.ENEMY_IFV, TacticalMarkerType.ENEMY_LIGHT_VEHICLE, TacticalMarkerType.ENEMY_HELICOPTER -> -1;
            default -> -2076078;
        };
        int a = Mth.m_14045_((int)Math.round(255.0f * opacity), (int)0, (int)255);
        return base & 0xFFFFFF | a << 24;
    }

    private static void drawQuad(PoseStack pose, float half, int argb) {
        Matrix4f mat = pose.m_85850_().m_252922_();
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        Tesselator tess = Tesselator.m_85913_();
        BufferBuilder buf = tess.m_85915_();
        buf.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85819_);
        buf.m_252986_(mat, -half, -half, 0.0f).m_7421_(0.0f, 1.0f).m_6122_(r, g, b, a).m_5752_();
        buf.m_252986_(mat, -half, half, 0.0f).m_7421_(0.0f, 0.0f).m_6122_(r, g, b, a).m_5752_();
        buf.m_252986_(mat, half, half, 0.0f).m_7421_(1.0f, 0.0f).m_6122_(r, g, b, a).m_5752_();
        buf.m_252986_(mat, half, -half, 0.0f).m_7421_(1.0f, 1.0f).m_6122_(r, g, b, a).m_5752_();
        tess.m_85914_();
    }

    private record DrawItem(double dx, double dy, double dz, float half, int argb) {
    }
}

