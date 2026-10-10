package org.espetro.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.espetro.network.FortificationWandPacket;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 工事选定棒的客户端可视化：选区线框 + 锚点 + 原点 + HUD 统计。
 *
 * <p>数据完全来自服务端 ({@link FortificationWandPacket})，本地不做任何判定。</p>
 */
public final class FortificationWandOverlay {

    private static State state;

    private FortificationWandOverlay() {
    }

    private record State(BlockPos a, BlockPos b, BlockPos anchor, int sizeX, int sizeY, int sizeZ,
                         int blockCount, int entityCount, boolean ready, String problem) {
    }

    public static void update(FortificationWandPacket packet) {
        if (packet == null || !packet.active() || packet.a() == null || packet.b() == null) {
            state = null;
            return;
        }
        state = new State(packet.a(), packet.b(), packet.anchor(), packet.sizeX(), packet.sizeY(),
            packet.sizeZ(), packet.blockCount(), packet.entityCount(), packet.ready(),
            packet.problem() == null ? "" : packet.problem());
    }

    public static void clear() {
        state = null;
    }

    public static void render(RenderLevelStageEvent event) {
        State current = state;
        if (current == null) return;
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;

        PoseStack pose = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        VertexConsumer lines = buffers.getBuffer(RenderType.lines());
        Vec3 camera = event.getCamera().getPosition();

        pose.pushPose();
        pose.translate(-camera.x, -camera.y, -camera.z);

        // 选区外框（黄色 / 预检不过时红色）
        AABB region = box(current.a(), current.b());
        if (current.ready()) {
            LevelRenderer.renderLineBox(pose, lines, region, 1.0F, 0.85F, 0.1F, 1.0F);
        } else {
            LevelRenderer.renderLineBox(pose, lines, region, 1.0F, 0.2F, 0.15F, 1.0F);
        }
        // 模板原点（min 角）绿色
        BlockPos min = minOf(current.a(), current.b());
        LevelRenderer.renderLineBox(pose, lines, cell(min), 0.2F, 1.0F, 0.3F, 1.0F);
        // 锚点青色
        if (current.anchor() != null) {
            LevelRenderer.renderLineBox(pose, lines, cell(current.anchor()).inflate(0.02D),
                0.2F, 0.9F, 1.0F, 1.0F);
        }
        pose.popPose();
        buffers.endBatch(RenderType.lines());
    }

    public static void renderHud(RenderGuiEvent.Post event) {
        State current = state;
        if (current == null) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) return;
        GuiGraphics graphics = event.getGuiGraphics();

        List<String> rows = new ArrayList<>();
        rows.add("§e工事选区 §f" + current.sizeX() + "×" + current.sizeY() + "×" + current.sizeZ()
            + " §7方块 §f" + current.blockCount() + " §7实体 §f" + current.entityCount());
        if (current.anchor() == null) {
            rows.add("§e还需设锚点（潜行+右键）");
        } else {
            BlockPos pivot = current.anchor().subtract(minOf(current.a(), current.b()));
            rows.add("§7锚点 §f" + current.anchor().getX() + ", " + current.anchor().getY() + ", "
                + current.anchor().getZ() + " §7pivot §f" + pivot.getX() + ", " + pivot.getY()
                + ", " + pivot.getZ() + " §7基准朝向 §f北");
        }
        if (!current.problem().isEmpty()) {
            rows.add(current.ready() ? "§e! " + current.problem() : "§c✗ " + current.problem());
        } else if (current.ready()) {
            rows.add("§a✓ 预检通过：/espetro fort save <名字>");
        }
        int y = 6;
        for (String row : rows) {
            graphics.drawString(mc.font, Component.literal(row), 6, y, 0xFFFFFF, true);
            y += 10;
        }
    }

    private static AABB box(BlockPos a, BlockPos b) {
        BlockPos min = minOf(a, b);
        BlockPos max = maxOf(a, b);
        return new AABB(min.getX(), min.getY(), min.getZ(),
            max.getX() + 1.0D, max.getY() + 1.0D, max.getZ() + 1.0D);
    }

    private static AABB cell(@Nullable BlockPos pos) {
        if (pos == null) return new AABB(0, 0, 0, 0, 0, 0);
        return new AABB(pos).inflate(0.002D);
    }

    private static BlockPos minOf(BlockPos a, BlockPos b) {
        return new BlockPos(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()),
            Math.min(a.getZ(), b.getZ()));
    }

    private static BlockPos maxOf(BlockPos a, BlockPos b) {
        return new BlockPos(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()),
            Math.max(a.getZ(), b.getZ()));
    }
}
