/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.RenderHelper
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.data.gun.GunProp
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.event.ClientEventHandler
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RenderGuiOverlayEvent$Post
 *  net.minecraftforge.event.TickEvent$RenderTickEvent
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.slf4j.Logger
 */
package com.redabysslucia.dragonrise_reforge.events;

import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.GunProp;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redabysslucia.dragonrise_reforge.Dragonrise_reforge;
import com.redabysslucia.dragonrise_reforge.entities.projectile.GuidedBombEntity;
import com.redabysslucia.dragonrise_reforge.entities.utils.SyncCameraVehicle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE, value={Dist.CLIENT})
public class ClientEvent {
    private static float lastSyncCameraYRot = 0.0f;
    private static float lastSyncCameraXRot = 0.0f;
    private static boolean isFirstSyncCameraFrame = true;
    private static final ResourceLocation BOMB_RING = new ResourceLocation("superbwarfare", "textures/overlay/crosshair/rex_circle.png");
    private static final ResourceLocation BOMB_LOCK_FRAME = new ResourceLocation("superbwarfare", "textures/overlay/frame/frame_lock.png");
    private static final List<GuidedBombEntity> bombsInWorld = new ArrayList<GuidedBombEntity>();
    private static int scanCooldown = 0;
    private static final int SCAN_INTERVAL = 20;
    private static final double SCAN_RANGE = 512.0;
    private static final Set<UUID> ownBombUuids = new HashSet<UUID>();
    private static final Logger LOGGER = Dragonrise_reforge.LOGGER;
    private static int debugFrame = 0;

    public static void onOwnBombMessage(UUID bombUuid) {
        ownBombUuids.add(bombUuid);
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity;
        if (event.getLevel().m_5776_() && (entity = event.getEntity()) instanceof GuidedBombEntity) {
            GuidedBombEntity bomb = (GuidedBombEntity)entity;
            LOGGER.info("[BombHud] client join: bomb {} at {}", (Object)bomb.m_20148_(), (Object)bomb.m_20182_());
            if (!bombsInWorld.contains((Object)bomb)) {
                bombsInWorld.add(bomb);
            }
        }
    }

    private static void refreshBombList(Player player) {
        if (--scanCooldown > 0) {
            return;
        }
        scanCooldown = 20;
        Vec3 c = player.m_20182_();
        List found = player.m_9236_().m_6443_(GuidedBombEntity.class, new AABB(c.f_82479_ - 512.0, c.f_82480_ - 512.0, c.f_82481_ - 512.0, c.f_82479_ + 512.0, c.f_82480_ + 512.0, c.f_82481_ + 512.0), Entity::m_6084_);
        if (!found.isEmpty()) {
            LOGGER.info("[BombHud] scan found {} bomb(s)", (Object)found.size());
            for (GuidedBombEntity b2 : found) {
                if (bombsInWorld.contains((Object)b2)) continue;
                bombsInWorld.add(b2);
            }
        }
        bombsInWorld.removeIf(b -> b.m_213877_() || b.m_9236_() != player.m_9236_());
    }

    private static boolean isOwnBomb(GuidedBombEntity bomb, Player player) {
        boolean removed = bomb.m_213877_();
        boolean sameLevel = bomb.m_9236_() == player.m_9236_();
        boolean own = ownBombUuids.contains(bomb.m_20148_());
        if (!own) {
            boolean bl = own = bomb.m_19749_() == player;
        }
        if (++debugFrame % 400 == 0) {
            LOGGER.info("[BombHud] isOwnBomb removed={} sameLevel={} own={} uuid={} knownUuids={}", new Object[]{removed, sameLevel, own, bomb.m_20148_(), ownBombUuids.size()});
        }
        return !removed && sameLevel && own;
    }

    private static Vec3 projectWorldToScreen(Vec3 worldPos, float partialTick) {
        float fov;
        Minecraft mc = Minecraft.m_91087_();
        Camera camera = mc.f_91063_.m_109153_();
        Vec3 camPos = camera.m_90583_();
        Vector3f look = camera.m_253058_();
        Vector3f up = camera.m_253028_();
        if (ClientEventHandler.zoomVehicle) {
            fov = (float)ClientEventHandler.currentFov;
            if (fov <= 0.0f) {
                fov = ((Integer)mc.f_91066_.m_231837_().m_231551_()).intValue();
            }
        } else {
            fov = ((Integer)mc.f_91066_.m_231837_().m_231551_()).intValue();
        }
        double aspect = (double)mc.m_91268_().m_85443_() / (double)mc.m_91268_().m_85444_();
        Matrix4f view = new Matrix4f().lookAt((Vector3fc)new Vector3f((float)camPos.f_82479_, (float)camPos.f_82480_, (float)camPos.f_82481_), (Vector3fc)new Vector3f((float)camPos.f_82479_ + look.x(), (float)camPos.f_82480_ + look.y(), (float)camPos.f_82481_ + look.z()), (Vector3fc)up);
        Matrix4f proj = new Matrix4f().perspective((float)Math.toRadians(fov), (float)aspect, 0.05f, 20000.0f);
        Matrix4f combined = new Matrix4f((Matrix4fc)proj).mul((Matrix4fc)view);
        Vector4f clip = combined.transform(new Vector4f((float)worldPos.f_82479_, (float)worldPos.f_82480_, (float)worldPos.f_82481_, 1.0f));
        float w = clip.w;
        float absW = Math.abs(w);
        if (absW < 1.0E-8f) {
            return null;
        }
        float ndcX = clip.x / absW;
        float ndcY = clip.y / absW;
        float depth = w <= 0.0f ? 2.0f : clip.z / w;
        return new Vec3((double)mc.m_91268_().m_85445_() * (0.5 + (double)ndcX * 0.5), (double)mc.m_91268_().m_85446_() * (0.5 - (double)ndcY * 0.5), (double)depth);
    }

    @SubscribeEvent
    public static void onRenderGuiOverlayPost(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer player = mc.f_91074_;
        if (player == null) {
            return;
        }
        Entity entity = player.m_20202_();
        if (!(entity instanceof VehicleEntity)) {
            return;
        }
        VehicleEntity vehicle = (VehicleEntity)entity;
        GunData data = vehicle.getGunData((Entity)player);
        if (data == null) {
            return;
        }
        if (!"@AirBomb".equals(data.get(GunProp.CROSSHAIR))) {
            return;
        }
        if (!ClientEventHandler.zoomVehicle) {
            return;
        }
        Vec3 bombO = ClientEventHandler.bombHitPosO;
        Vec3 bombN = ClientEventHandler.bombHitPos;
        if (bombO.equals((Object)Vec3.f_82478_) && bombN.equals((Object)Vec3.f_82478_)) {
            return;
        }
        float pt = event.getPartialTick();
        Vec3 pos = new Vec3(Mth.m_14139_((double)pt, (double)bombO.f_82479_, (double)bombN.f_82479_), Mth.m_14139_((double)pt, (double)bombO.f_82480_, (double)bombN.f_82480_), Mth.m_14139_((double)pt, (double)bombO.f_82481_, (double)bombN.f_82481_));
        Vec3 p = ClientEvent.projectWorldToScreen(pos, pt);
        if (p == null) {
            return;
        }
        if (p.f_82481_ >= 1.0) {
            return;
        }
        float x = (float)p.f_82479_;
        float y = (float)p.f_82480_;
        if (x < -40.0f || x > (float)(mc.m_91268_().m_85445_() + 40) || y < -40.0f || y > (float)(mc.m_91268_().m_85446_() + 40)) {
            return;
        }
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::m_172817_);
        RenderSystem.blendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderHelper.preciseBlit((GuiGraphics)event.getGuiGraphics(), (ResourceLocation)BOMB_RING, (float)(x - 12.0f), (float)(y - 12.0f), (float)0.0f, (float)0.0f, (float)24.0f, (float)24.0f, (float)24.0f, (float)24.0f);
    }

    @SubscribeEvent
    public static void onRenderBombLockFrame(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer player = mc.f_91074_;
        if (player == null || player.m_9236_() == null) {
            return;
        }
        if (!(player.m_20202_() instanceof VehicleEntity)) {
            return;
        }
        ClientEvent.refreshBombList((Player)player);
        if (bombsInWorld.isEmpty()) {
            return;
        }
        float pt = event.getPartialTick();
        double cx = (double)mc.m_91268_().m_85445_() / 2.0;
        double cy = (double)mc.m_91268_().m_85446_() / 2.0;
        double margin = 30.0;
        for (GuidedBombEntity bomb : bombsInWorld) {
            double dy;
            double dx;
            double len;
            Vec3 p;
            Vec3 target;
            if (!ClientEvent.isOwnBomb(bomb, (Player)player) || !bomb.isLocked() || (target = bomb.getTargetPos()) == null || (p = ClientEvent.projectWorldToScreen(target, pt)) == null || (len = Math.sqrt((dx = p.f_82479_ - cx) * dx + (dy = p.f_82480_ - cy) * dy)) < 1.0E-6) continue;
            double scale = Math.min((cx - 30.0) / Math.abs(dx), (cy - 30.0) / Math.abs(dy));
            if (scale > 1.0) {
                scale = 1.0;
            }
            float fx = (float)(cx + dx * scale);
            float fy = (float)(cy + dy * scale);
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask((boolean)false);
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::m_172817_);
            RenderSystem.blendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            RenderHelper.blit((PoseStack)event.getGuiGraphics().m_280168_(), (ResourceLocation)BOMB_LOCK_FRAME, (float)(fx - 12.0f), (float)(fy - 12.0f), (float)0.0f, (float)0.0f, (float)24.0f, (float)24.0f, (float)24.0f, (float)24.0f, (float)1.0f);
        }
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer player = mc.f_91074_;
        if (player == null) {
            return;
        }
        Entity entity = player.m_20202_();
        if (entity instanceof SyncCameraVehicle) {
            SyncCameraVehicle vehicle = (SyncCameraVehicle)entity;
            float partialTicks = event.renderTickTime;
            float prevYRot = vehicle.f_19859_;
            float currYRot = vehicle.m_146908_();
            float prevXRot = vehicle.f_19860_;
            float currXRot = vehicle.m_146909_();
            float interpolatedYRot = Mth.m_14179_((float)partialTicks, (float)prevYRot, (float)currYRot);
            float interpolatedXRot = Mth.m_14179_((float)partialTicks, (float)prevXRot, (float)currXRot);
            if (isFirstSyncCameraFrame) {
                lastSyncCameraYRot = interpolatedYRot;
                lastSyncCameraXRot = interpolatedXRot;
                isFirstSyncCameraFrame = false;
                return;
            }
            float yRotDelta = interpolatedYRot - lastSyncCameraYRot;
            float xRotDelta = interpolatedXRot - lastSyncCameraXRot;
            yRotDelta = Mth.m_14177_((float)yRotDelta);
            player.m_5616_(player.m_6080_() + yRotDelta);
            player.m_146922_(player.m_146908_() + yRotDelta);
            player.m_146926_(player.m_146909_() + xRotDelta);
            lastSyncCameraYRot = interpolatedYRot;
            lastSyncCameraXRot = interpolatedXRot;
        } else {
            isFirstSyncCameraFrame = true;
        }
    }
}

