/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.AABB
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  net.minecraftforge.client.gui.overlay.IGuiOverlay
 */
package com.redabysslucia.dragonrise_reforge.client.overlay;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.redabysslucia.dragonrise_reforge.entities.AmmoSupplyStationEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@OnlyIn(value=Dist.CLIENT)
public class SupplyProgressOverlay
implements IGuiOverlay {
    public static final String ID = "dragonrise_reforge_supply_progress";
    private static final float OUTER_RADIUS = 12.0f;
    private static final float INNER_RADIUS = 10.0f;
    private static final int RING_SEGMENTS = 64;

    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = gui.getMinecraft();
        if (mc.f_91074_ == null || mc.f_91073_ == null) {
            return;
        }
        float progress = this.getNearbySupplyProgress(mc);
        if (progress <= 0.0f) {
            return;
        }
        float centerX = (float)screenWidth / 2.0f;
        float centerY = (float)screenHeight / 2.0f;
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::m_172811_);
        this.drawShadowRing(centerX, centerY);
        this.drawProgressArc(centerX, centerY, progress);
        RenderSystem.depthMask((boolean)true);
        RenderSystem.enableDepthTest();
    }

    private float getNearbySupplyProgress(Minecraft mc) {
        Entity vehicle = mc.f_91074_.m_20202_();
        if (!(vehicle instanceof GeoVehicleEntity)) {
            return 0.0f;
        }
        AABB searchBox = mc.f_91074_.m_20191_().m_82400_(64.0);
        for (Entity entity : mc.f_91073_.m_6249_((Entity)mc.f_91074_, searchBox, e -> e instanceof AmmoSupplyStationEntity)) {
            AmmoSupplyStationEntity station = (AmmoSupplyStationEntity)entity;
            if (!station.isCharging() || !(station.m_20280_(vehicle) <= (double)(station.getSupplyRange() * station.getSupplyRange()))) continue;
            return station.getSupplyProgress();
        }
        return 0.0f;
    }

    private void drawShadowRing(float cx, float cy) {
        float shadowOuter = 13.0f;
        float shadowInner = 9.0f;
        Tesselator tesselator = Tesselator.m_85913_();
        BufferBuilder buffer = tesselator.m_85915_();
        buffer.m_166779_(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.f_85815_);
        for (int i = 0; i <= 64; ++i) {
            double angle = (double)i / 64.0 * Math.PI * 2.0 - 1.5707963267948966;
            float x = (float)Math.cos(angle);
            float y = (float)Math.sin(angle);
            buffer.m_5483_((double)(cx + x * shadowOuter), (double)(cy + y * shadowOuter), 0.0).m_6122_(0, 0, 0, 60).m_5752_();
            buffer.m_5483_((double)(cx + x * shadowInner), (double)(cy + y * shadowInner), 0.0).m_6122_(0, 0, 0, 30).m_5752_();
        }
        tesselator.m_85914_();
    }

    private void drawProgressArc(float cx, float cy, float progress) {
        int remainingSegments;
        float progressClamped = Math.min(1.0f, Math.max(0.0f, progress));
        int filledSegments = (int)(64.0f * progressClamped);
        float r = 12.0f;
        float ir = 10.0f;
        float startAngle = -1.5707964f;
        Tesselator tesselator = Tesselator.m_85913_();
        BufferBuilder buffer = tesselator.m_85915_();
        if (filledSegments > 0) {
            buffer.m_166779_(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.f_85815_);
            for (int i = 0; i <= filledSegments; ++i) {
                double angle = (double)startAngle + (double)i / 64.0 * Math.PI * 2.0;
                float x = (float)Math.cos(angle);
                float y = (float)Math.sin(angle);
                buffer.m_5483_((double)(cx + x * r), (double)(cy + y * r), 0.0).m_6122_(255, 255, 255, 220).m_5752_();
                buffer.m_5483_((double)(cx + x * ir), (double)(cy + y * ir), 0.0).m_6122_(255, 255, 255, 200).m_5752_();
            }
            tesselator.m_85914_();
        }
        if ((remainingSegments = 64 - filledSegments) > 0) {
            buffer.m_166779_(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.f_85815_);
            for (int i = filledSegments; i <= 64; ++i) {
                double angle = (double)startAngle + (double)i / 64.0 * Math.PI * 2.0;
                float x = (float)Math.cos(angle);
                float y = (float)Math.sin(angle);
                buffer.m_5483_((double)(cx + x * r), (double)(cy + y * r), 0.0).m_6122_(255, 255, 255, 60).m_5752_();
                buffer.m_5483_((double)(cx + x * ir), (double)(cy + y * ir), 0.0).m_6122_(255, 255, 255, 40).m_5752_();
            }
            tesselator.m_85914_();
        }
    }
}

