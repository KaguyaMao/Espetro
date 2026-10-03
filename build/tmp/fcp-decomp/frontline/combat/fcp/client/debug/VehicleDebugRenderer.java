/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Camera
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.client.event.RenderLevelStageEvent
 *  net.minecraftforge.client.event.RenderLevelStageEvent$Stage
 *  net.minecraftforge.client.settings.IKeyConflictContext
 *  net.minecraftforge.client.settings.KeyConflictContext
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.registries.ForgeRegistries
 *  org.joml.Matrix3f
 *  org.joml.Matrix4d
 *  org.joml.Matrix4f
 *  org.joml.Vector4d
 */
package frontline.combat.fcp.client.debug;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import frontline.combat.fcp.entity.vehicle.Trailers.AbstractTrailerEntity;
import frontline.combat.fcp.entity.vehicle.Trailers.TrailerDriverData;
import frontline.combat.fcp.init.TrailerDriverConfigs;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector4d;

@Mod.EventBusSubscriber(modid="fcp", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.FORGE)
public final class VehicleDebugRenderer {
    public static boolean enabled = false;
    private static final double RANGE = 96.0;
    public static final KeyMapping TOGGLE_KEY = new KeyMapping("key.fcp.vehicle_debug", (IKeyConflictContext)KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, 295, "key.categories.fcp");

    private VehicleDebugRenderer() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        while (TOGGLE_KEY.m_90859_()) {
            enabled = !enabled;
            Minecraft mc = Minecraft.m_91087_();
            if (mc.f_91074_ == null) continue;
            mc.f_91074_.m_5661_((Component)Component.m_237113_((String)("[FCP] Vehicle debug overlay " + (enabled ? "ON" : "OFF"))), true);
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (!enabled) {
            return;
        }
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91073_ == null) {
            return;
        }
        float pt = event.getPartialTick();
        Camera camera = mc.f_91063_.m_109153_();
        Vec3 cam = camera.m_90583_();
        PoseStack pose = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.m_91269_().m_110104_();
        VertexConsumer lines = buffers.m_6299_(RenderType.m_110504_());
        pose.m_85836_();
        pose.m_85837_(-cam.f_82479_, -cam.f_82480_, -cam.f_82481_);
        for (Entity e : mc.f_91073_.m_104735_()) {
            if (!(e instanceof VehicleEntity)) continue;
            VehicleEntity vehicle = (VehicleEntity)e;
            if (e.m_20182_().m_82557_(cam) > 9216.0) continue;
            VehicleDebugRenderer.drawTerrainPoints(mc, vehicle, pt, pose, lines);
            VehicleDebugRenderer.drawBarrel(vehicle, pt, pose, lines);
            VehicleDebugRenderer.drawTurret(vehicle, pt, pose, lines);
            VehicleDebugRenderer.drawShootPositions(vehicle, pt, pose, lines);
            VehicleDebugRenderer.drawHitch(vehicle, pt, pose, lines);
        }
        pose.m_85849_();
        buffers.m_109912_(RenderType.m_110504_());
    }

    private static void drawTerrainPoints(Minecraft mc, VehicleEntity v, float pt, PoseStack pose, VertexConsumer vc) {
        try {
            List points = v.computed().getTerrainCompat();
            if (points == null || points.isEmpty()) {
                return;
            }
            Matrix4d transform = v.getWheelsTransform(pt);
            for (Vec3 local : points) {
                Vector4d w = v.transformPosition(transform, local.f_82479_, local.f_82480_, local.f_82481_);
                Vec3 p = new Vec3(w.x, w.y, w.z);
                VehicleDebugRenderer.cross(pose, vc, p, 0.08, 0.0f, 1.0f, 1.0f, 1.0f);
                Vec3 down = p.m_82520_(0.0, -20.0, 0.0);
                BlockHitResult hit = mc.f_91073_.m_45547_(new ClipContext(p, down, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)v));
                if (hit.m_6662_() == HitResult.Type.MISS) continue;
                Vec3 g = hit.m_82450_();
                VehicleDebugRenderer.line(pose, vc, p.f_82479_, p.f_82480_, p.f_82481_, g.f_82479_, g.f_82480_, g.f_82481_, 0.0f, 0.55f, 0.55f, 1.0f);
                VehicleDebugRenderer.cross(pose, vc, g, 0.12, 0.0f, 1.0f, 0.0f, 1.0f);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void drawBarrel(VehicleEntity v, float pt, PoseStack pose, VertexConsumer vc) {
        try {
            Matrix4d bt = v.getBarrelTransform(pt);
            Vector4d bp = v.transformPosition(bt, 0.0, 0.0, 0.0);
            Vec3 pos = new Vec3(bp.x, bp.y, bp.z);
            VehicleDebugRenderer.cross(pose, vc, pos, 0.1, 1.0f, 0.0f, 0.0f, 1.0f);
            Vec3 dir = v.getBarrelVector(pt);
            if (dir.m_82556_() > 1.0E-6) {
                Vec3 tip = pos.m_82549_(dir.m_82541_().m_82490_(3.0));
                VehicleDebugRenderer.line(pose, vc, pos.f_82479_, pos.f_82480_, pos.f_82481_, tip.f_82479_, tip.f_82480_, tip.f_82481_, 1.0f, 0.6f, 0.0f, 1.0f);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void drawTurret(VehicleEntity v, float pt, PoseStack pose, VertexConsumer vc) {
        try {
            Matrix4d tt = v.getTurretTransform(pt);
            Vector4d tp = v.transformPosition(tt, 0.0, 0.0, 0.0);
            Vec3 pos = new Vec3(tp.x, tp.y, tp.z);
            VehicleDebugRenderer.cross(pose, vc, pos, 0.12, 1.0f, 1.0f, 0.0f, 1.0f);
            Vec3 dir = v.getTurretVector(pt);
            if (dir.m_82556_() > 1.0E-6) {
                Vec3 tip = pos.m_82549_(dir.m_82541_().m_82490_(2.0));
                VehicleDebugRenderer.line(pose, vc, pos.f_82479_, pos.f_82480_, pos.f_82481_, tip.f_82479_, tip.f_82480_, tip.f_82481_, 0.7f, 0.7f, 0.0f, 1.0f);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void drawShootPositions(VehicleEntity v, float pt, PoseStack pose, VertexConsumer vc) {
        try {
            for (String weaponName : v.getGunDataMap().keySet()) {
                Vec3 sp = v.getShootPos(weaponName, pt);
                VehicleDebugRenderer.cross(pose, vc, sp, 0.11, 0.7f, 0.2f, 1.0f, 1.0f);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void drawHitch(VehicleEntity v, float pt, PoseStack pose, VertexConsumer vc) {
        try {
            if (v instanceof AbstractTrailerEntity) {
                Entity driver;
                AbstractTrailerEntity trailer = (AbstractTrailerEntity)v;
                Vec3 tow = trailer.getTowOffset();
                Vec3 tongue = VehicleDebugRenderer.localToWorld((Entity)trailer, tow, pt);
                VehicleDebugRenderer.cross(pose, vc, tongue, 0.14, 0.2f, 0.5f, 1.0f, 1.0f);
                if (trailer.isAttached() && (driver = trailer.getDriver()) != null) {
                    Vec3 hitchLocal = trailer.getHitchOffset();
                    Vec3 hitch = VehicleDebugRenderer.localToWorld(driver, hitchLocal, pt);
                    VehicleDebugRenderer.cross(pose, vc, hitch, 0.14, 1.0f, 0.0f, 1.0f, 1.0f);
                    double gap = hitch.m_82554_(tongue);
                    float bad = (float)Math.min(1.0, gap / 1.0);
                    VehicleDebugRenderer.line(pose, vc, tongue.f_82479_, tongue.f_82480_, tongue.f_82481_, hitch.f_82479_, hitch.f_82480_, hitch.f_82481_, 1.0f, 1.0f - bad, 1.0f - bad, 1.0f);
                    float dYaw = Mth.m_14189_((float)pt, (float)driver.f_19859_, (float)driver.m_146908_());
                    double dr = Math.toRadians(dYaw);
                    Vec3 fwd = new Vec3(-Math.sin(dr), 0.0, Math.cos(dr)).m_82490_(1.5);
                    VehicleDebugRenderer.line(pose, vc, hitch.f_82479_, hitch.f_82480_, hitch.f_82481_, hitch.f_82479_ + fwd.f_82479_, hitch.f_82480_ + fwd.f_82480_, hitch.f_82481_ + fwd.f_82481_, 1.0f, 1.0f, 0.0f, 1.0f);
                }
                return;
            }
        }
        catch (Throwable trailer) {
            // empty catch block
        }
        try {
            ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey((Object)v.m_6095_());
            if (id == null) {
                return;
            }
            TrailerDriverData drv = TrailerDriverConfigs.get(id);
            if (drv == null) {
                return;
            }
            Vec3 hitch = VehicleDebugRenderer.localToWorld((Entity)v, new Vec3(drv.hitchX(), drv.hitchY(), drv.hitchZ()), pt);
            VehicleDebugRenderer.cross(pose, vc, hitch, 0.14, 1.0f, 0.0f, 1.0f, 1.0f);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static Vec3 localToWorld(Entity e, Vec3 local, float pt) {
        double ex = Mth.m_14139_((double)pt, (double)e.f_19854_, (double)e.m_20185_());
        double ey = Mth.m_14139_((double)pt, (double)e.f_19855_, (double)e.m_20186_());
        double ez = Mth.m_14139_((double)pt, (double)e.f_19856_, (double)e.m_20189_());
        double theta = Math.toRadians(Mth.m_14189_((float)pt, (float)e.f_19859_, (float)e.m_146908_()));
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        return new Vec3(ex + (local.f_82479_ * cos - local.f_82481_ * sin), ey + local.f_82480_, ez + (local.f_82479_ * sin + local.f_82481_ * cos));
    }

    private static void cross(PoseStack pose, VertexConsumer vc, Vec3 p, double s, float r, float g, float b, float a) {
        VehicleDebugRenderer.line(pose, vc, p.f_82479_ - s, p.f_82480_, p.f_82481_, p.f_82479_ + s, p.f_82480_, p.f_82481_, r, g, b, a);
        VehicleDebugRenderer.line(pose, vc, p.f_82479_, p.f_82480_ - s, p.f_82481_, p.f_82479_, p.f_82480_ + s, p.f_82481_, r, g, b, a);
        VehicleDebugRenderer.line(pose, vc, p.f_82479_, p.f_82480_, p.f_82481_ - s, p.f_82479_, p.f_82480_, p.f_82481_ + s, r, g, b, a);
    }

    private static void line(PoseStack pose, VertexConsumer vc, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a) {
        Matrix4f m = pose.m_85850_().m_252922_();
        Matrix3f n = pose.m_85850_().m_252943_();
        float nx = (float)(x2 - x1);
        float ny = (float)(y2 - y1);
        float nz = (float)(z2 - z1);
        float len = Mth.m_14116_((float)(nx * nx + ny * ny + nz * nz));
        if (len < 1.0E-6f) {
            return;
        }
        vc.m_252986_(m, (float)x1, (float)y1, (float)z1).m_85950_(r, g, b, a).m_252939_(n, nx /= len, ny /= len, nz /= len).m_5752_();
        vc.m_252986_(m, (float)x2, (float)y2, (float)z2).m_85950_(r, g, b, a).m_252939_(n, nx, ny, nz).m_5752_();
    }

    @Mod.EventBusSubscriber(modid="fcp", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static final class KeyReg {
        @SubscribeEvent
        public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
            event.register(TOGGLE_KEY);
        }
    }
}

