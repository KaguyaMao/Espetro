/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RenderLevelStageEvent
 *  net.minecraftforge.client.event.RenderLevelStageEvent$Stage
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package LOL_141.vehicle_addition.compat;

import LOL_141.vehicle_addition.compat.SamplePointCache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value={Dist.CLIENT}, modid="vehicle_addition")
public final class SamplePointDebugRenderer {
    private SamplePointDebugRenderer() {
    }

    private static boolean hitboxesEnabled() {
        return Minecraft.m_91087_().m_91290_().m_114377_();
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }
        if (!SamplePointDebugRenderer.hitboxesEnabled()) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91073_ == null || mc.f_91074_ == null) {
            return;
        }
        Camera camera = mc.f_91063_.m_109153_();
        Vec3 cam = camera.m_90583_();
        MultiBufferSource.BufferSource buffers = mc.m_91269_().m_110104_();
        VertexConsumer buffer = buffers.m_6299_(RenderType.m_110504_());
        PoseStack poseStack = event.getPoseStack();
        SamplePointCache.sweep();
        for (SamplePointCache.SampleSet set : SamplePointCache.ALL.values()) {
            Entity vehicle = set.vehicle;
            if (vehicle == null || vehicle.m_213877_() || vehicle.m_9236_() != mc.f_91073_ || vehicle.m_20238_(cam) > 4096.0) continue;
            poseStack.m_85836_();
            poseStack.m_85837_(vehicle.m_20185_() - cam.f_82479_, vehicle.m_20186_() - cam.f_82480_, vehicle.m_20189_() - cam.f_82481_);
            SamplePointDebugRenderer.drawSamplePoints(poseStack, buffer, set);
            poseStack.m_85849_();
        }
    }

    private static void drawSamplePoints(PoseStack poseStack, VertexConsumer buffer, SamplePointCache.SampleSet set) {
        List<Vec3> embedded;
        List<Vec3> points = set.points;
        List<Boolean> pits = set.isPit;
        if (points == null || points.size() != 5) {
            return;
        }
        double vx = set.vehicle.m_20185_();
        double vy = set.vehicle.m_20186_();
        double vz = set.vehicle.m_20189_();
        float box = 0.08f;
        for (int i = 0; i < points.size(); ++i) {
            boolean pit;
            Vec3 p = points.get(i);
            double lx = p.f_82479_ - vx;
            double ly = p.f_82480_ - vy;
            double lz = p.f_82481_ - vz;
            boolean bl = pit = i < pits.size() && pits.get(i) != false;
            if (pit) {
                LevelRenderer.m_109608_((PoseStack)poseStack, (VertexConsumer)buffer, (double)(lx - (double)box), (double)(ly - (double)box), (double)(lz - (double)box), (double)(lx + (double)box), (double)(ly + (double)box), (double)(lz + (double)box), (float)1.0f, (float)0.1f, (float)0.1f, (float)1.0f);
                continue;
            }
            LevelRenderer.m_109608_((PoseStack)poseStack, (VertexConsumer)buffer, (double)(lx - (double)box), (double)(ly - (double)box), (double)(lz - (double)box), (double)(lx + (double)box), (double)(ly + (double)box), (double)(lz + (double)box), (float)0.1f, (float)1.0f, (float)0.1f, (float)1.0f);
        }
        List<Integer> supportIdx = set.supportIdx;
        if (supportIdx != null && supportIdx.size() >= 2) {
            for (int a = 0; a < supportIdx.size(); ++a) {
                for (int b = a + 1; b < supportIdx.size(); ++b) {
                    Vec3 pa = points.get(supportIdx.get(a));
                    Vec3 pb = points.get(supportIdx.get(b));
                    double ax = pa.f_82479_ - vx;
                    double ay = pa.f_82480_ - vy;
                    double az = pa.f_82481_ - vz;
                    double bx = pb.f_82479_ - vx;
                    double by = pb.f_82480_ - vy;
                    double bz = pb.f_82481_ - vz;
                    LevelRenderer.m_109608_((PoseStack)poseStack, (VertexConsumer)buffer, (double)Math.min(ax, bx), (double)Math.min(ay, by), (double)Math.min(az, bz), (double)Math.max(ax, bx), (double)Math.max(ay, by), (double)Math.max(az, bz), (float)0.2f, (float)1.0f, (float)1.0f, (float)0.5f);
                }
            }
        }
        if ((embedded = set.embedded) != null) {
            for (Vec3 p : embedded) {
                double lx = p.f_82479_ - vx;
                double ly = p.f_82480_ - vy;
                double lz = p.f_82481_ - vz;
                LevelRenderer.m_109608_((PoseStack)poseStack, (VertexConsumer)buffer, (double)(lx - (double)box), (double)(ly - (double)box), (double)(lz - (double)box), (double)(lx + (double)box), (double)(ly + (double)box), (double)(lz + (double)box), (float)1.0f, (float)1.0f, (float)0.0f, (float)1.0f);
            }
        }
    }
}

