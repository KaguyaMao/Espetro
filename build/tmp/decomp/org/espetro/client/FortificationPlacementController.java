/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.client.event.InputEvent$InteractionKeyMappingTriggered
 *  net.minecraftforge.client.event.RenderLevelStageEvent
 *  net.minecraftforge.client.event.RenderLevelStageEvent$Stage
 */
package org.espetro.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.espetro.bastion.OnBuildingBlock;
import org.espetro.network.FortificationPlacementPacket;
import org.espetro.network.FortificationPreviewPacket;
import org.espetro.network.NetworkManager;
import org.espetro.vehicle.VehicleManager;

public final class FortificationPlacementController {
    private static final double REACH = 6.0;
    private static final int WORK_INTERVAL_TICKS = 5;
    private static Preview preview;
    private static BlockPos anchor;
    private static Direction facing;
    private static List<AABB> boxes;
    private static boolean valid;
    private static long lastWorkTick;

    private FortificationPlacementController() {
    }

    public static void begin(FortificationPreviewPacket packet) {
        preview = new Preview(packet.token(), packet.displayName(), packet.occupiedOffsets());
        anchor = null;
        boxes = List.of();
        valid = false;
    }

    public static void clear() {
        preview = null;
        anchor = null;
        boxes = List.of();
        valid = false;
    }

    public static boolean isPreviewing() {
        return preview != null;
    }

    /*
     * Enabled aggressive block sorting
     */
    public static void tick(Minecraft minecraft) {
        if (minecraft == null || minecraft.f_91074_ == null || minecraft.f_91073_ == null) {
            FortificationPlacementController.clear();
            return;
        }
        if (preview != null) {
            FortificationPlacementController.updateOutline(minecraft);
            return;
        }
        if (minecraft.f_91080_ != null) return;
        if (minecraft.f_91074_.m_21205_().m_41720_() != Items.f_42384_) {
            return;
        }
        boolean build = minecraft.f_91066_.f_92096_.m_90857_();
        boolean remove = minecraft.f_91066_.f_92095_.m_90857_();
        if (!build && !remove) {
            return;
        }
        long now = minecraft.f_91073_.m_46467_();
        if (now - lastWorkTick < 5L) {
            return;
        }
        HitResult hit = minecraft.f_91077_;
        lastWorkTick = now;
        if (hit instanceof BlockHitResult) {
            BlockHitResult blockHit = (BlockHitResult)hit;
            if (hit.m_6662_() == HitResult.Type.BLOCK) {
                NetworkManager.sendFortificationWork(blockHit.m_82425_(), build && !remove);
                return;
            }
        }
        if (!(hit instanceof EntityHitResult)) return;
        EntityHitResult entityHit = (EntityHitResult)hit;
        if (!VehicleManager.isMappedSupplyStation(entityHit.m_82443_())) return;
        NetworkManager.sendFortificationEntityWork(entityHit.m_82443_().m_20148_(), build && !remove);
    }

    public static void onInteraction(InputEvent.InteractionKeyMappingTriggered event) {
        BlockHitResult hit;
        Minecraft mc;
        block14: {
            block13: {
                EntityHitResult entityHit;
                mc = Minecraft.m_91087_();
                if (mc.f_91074_ == null || mc.f_91073_ == null || mc.f_91080_ != null) {
                    return;
                }
                if (preview != null) {
                    if (event.isUseItem()) {
                        NetworkManager.sendFortificationPlacement(FortificationPlacementPacket.Action.CANCEL, FortificationPlacementController.preview.token, BlockPos.f_121853_, Direction.NORTH);
                        FortificationPlacementController.clear();
                        event.setCanceled(true);
                        event.setSwingHand(false);
                        return;
                    }
                    if (!event.isAttack()) {
                        return;
                    }
                    event.setCanceled(true);
                    event.setSwingHand(false);
                    if (!valid || anchor == null) {
                        mc.f_91074_.m_5661_(Component.m_237113_("\u00a7c\u7ea2\u8272\u8303\u56f4\u65e0\u6cd5\u653e\u7f6e\u5de5\u4e8b\u3002"), true);
                        return;
                    }
                    NetworkManager.sendFortificationPlacement(FortificationPlacementPacket.Action.CONFIRM, FortificationPlacementController.preview.token, anchor, facing);
                    FortificationPlacementController.clear();
                    return;
                }
                if (mc.f_91074_.m_21205_().m_41720_() != Items.f_42384_) {
                    return;
                }
                HitResult hitResult = mc.f_91077_;
                if (hitResult instanceof EntityHitResult && VehicleManager.isMappedSupplyStation((entityHit = (EntityHitResult)hitResult).m_82443_())) {
                    event.setCanceled(true);
                    event.setSwingHand(false);
                    if (event.isAttack() || event.isUseItem()) {
                        NetworkManager.sendFortificationEntityWork(entityHit.m_82443_().m_20148_(), event.isAttack());
                        lastWorkTick = mc.f_91073_.m_46467_();
                    }
                    return;
                }
                hitResult = mc.f_91077_;
                if (!(hitResult instanceof BlockHitResult)) break block13;
                hit = (BlockHitResult)hitResult;
                if (mc.f_91077_.m_6662_() == HitResult.Type.BLOCK) break block14;
            }
            return;
        }
        if (mc.f_91073_.m_8055_(hit.m_82425_()).m_60734_() instanceof OnBuildingBlock) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
        if (event.isAttack() || event.isUseItem()) {
            NetworkManager.sendFortificationWork(hit.m_82425_(), event.isAttack());
            lastWorkTick = mc.f_91073_.m_46467_();
        }
    }

    private static void updateOutline(Minecraft mc) {
        Vec3 end;
        Vec3 eye = mc.f_91074_.m_20299_(1.0f);
        BlockHitResult hit = mc.f_91073_.m_45547_(new ClipContext(eye, end = eye.m_82549_(mc.f_91074_.m_20154_().m_82490_(6.0)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, mc.f_91074_));
        if (hit.m_6662_() == HitResult.Type.MISS) {
            anchor = null;
            boxes = List.of();
            valid = false;
            return;
        }
        anchor = mc.f_91073_.m_8055_(hit.m_82425_()).m_60713_(Blocks.f_50125_) ? hit.m_82425_() : hit.m_82425_().m_121945_(hit.m_82434_());
        facing = mc.f_91074_.m_6350_();
        Direction right = facing.m_122427_();
        ArrayList<AABB> next = new ArrayList<AABB>(FortificationPlacementController.preview.offsets.size());
        boolean clear = true;
        for (FortificationPreviewPacket.Offset offset : FortificationPlacementController.preview.offsets) {
            int dx = right.m_122429_() * offset.x() + facing.m_122429_() * offset.z();
            int dz = right.m_122431_() * offset.x() + facing.m_122431_() * offset.z();
            BlockPos pos = anchor.m_7918_(dx, offset.y(), dz);
            next.add(new AABB(pos).m_82400_(0.002));
            BlockState state = mc.f_91073_.m_8055_(pos);
            if (!state.m_60795_() && !state.m_60713_(Blocks.f_50125_)) {
                clear = false;
            }
            if (mc.f_91073_.m_6249_(null, new AABB(pos), entity -> entity != mc.f_91074_ && entity instanceof LivingEntity && entity.m_6084_()).isEmpty()) continue;
            clear = false;
        }
        boxes = List.copyOf(next);
        valid = clear;
    }

    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS || preview == null || boxes.isEmpty()) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91066_.f_92062_) {
            return;
        }
        PoseStack pose = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.m_91269_().m_110104_();
        VertexConsumer lines = buffers.m_6299_(RenderType.m_110504_());
        Vec3 camera = event.getCamera().m_90583_();
        float red = valid ? 1.0f : 1.0f;
        float green = valid ? 0.85f : 0.15f;
        float blue = valid ? 0.1f : 0.12f;
        pose.m_85836_();
        pose.m_85837_(-camera.f_82479_, -camera.f_82480_, -camera.f_82481_);
        for (AABB box : boxes) {
            LevelRenderer.m_109646_(pose, lines, box, red, green, blue, 1.0f);
        }
        pose.m_85849_();
        buffers.m_109912_(RenderType.m_110504_());
    }

    static {
        facing = Direction.NORTH;
        boxes = List.of();
        lastWorkTick = -4611686018427387904L;
    }

    private record Preview(UUID token, String name, List<FortificationPreviewPacket.Offset> offsets) {
    }
}

