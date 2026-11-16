package org.espetro.dimension;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.dimension.MapEditDraft.Box;
import org.espetro.dimension.MapEditDraft.PointDraft;
import org.espetro.dimension.MapEditDraft.Side;
import org.espetro.dimension.MapEditDraft.Spot;
import org.espetro.dimension.MapEditDraft.VehPoint;

import java.util.List;

/**
 * 编辑维度里的可视化预览：用服务端粒子画框/画圈，不改方块、不新增网络包。
 *
 * <p>只做一次性粒子爆发（由指令触发），客户端无需安装或更新；重复执行指令即可刷新。
 * 颜色约定：据点 = 白色 END_ROD，进攻方 = 蓝色 SOUL_FIRE_FLAME，防守方 = 橙色 FLAME。</p>
 */
public final class MapEditPreview {

    /** 单次调用最多撒多少个粒子，避免刷屏卡顿。 */
    private static final int MAX_PARTICLES = 4000;
    /** 边长上每格一个粒子，超过这个间距就按比例跳过。 */
    private static final double MAX_STEP = 1.0D;

    private MapEditPreview() {
    }

    public static void capturePoints(ServerLevel level, List<PointDraft> points) {
        int budget = MAX_PARTICLES;
        for (PointDraft point : points) {
            budget = outline(level, point.box(), ParticleTypes.END_ROD, budget);
            if (budget <= 0) {
                return;
            }
        }
    }

    public static void capturePoint(ServerLevel level, PointDraft point) {
        outline(level, point.box(), ParticleTypes.END_ROD, MAX_PARTICLES);
        Box box = point.box();
        pillar(level, box.centerX() + 0.5D, box.minY(), box.centerZ() + 0.5D,
            Math.min(12, Math.max(3, box.sizeY())), ParticleTypes.END_ROD);
    }

    public static void spawn(ServerLevel level, Side side, Spot spot) {
        ring(level, spot, side == Side.ATTACK ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, 3.0D);
        pillar(level, spot.x(), spot.y(), spot.z(), 8,
            side == Side.ATTACK ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME);
    }

    public static void vehPoints(ServerLevel level, String type, List<VehPoint> points) {
        int budget = MAX_PARTICLES;
        for (VehPoint point : points) {
            budget = vehPoint(level, point, budget);
            if (budget <= 0) {
                return;
            }
        }
    }

    public static void vehPoint(ServerLevel level, VehPoint point) {
        vehPoint(level, point, MAX_PARTICLES);
    }

    private static int vehPoint(ServerLevel level, VehPoint point, int budget) {
        int used = 0;
        if (point.attack() != null) {
            Spot spot = point.attack();
            ring(level, spot, ParticleTypes.SOUL_FIRE_FLAME, 2.0D);
            pillar(level, spot.x(), spot.y(), spot.z(), 5, ParticleTypes.SOUL_FIRE_FLAME);
            used += 24;
        }
        if (point.defend() != null) {
            Spot spot = point.defend();
            ring(level, spot, ParticleTypes.FLAME, 2.0D);
            pillar(level, spot.x(), spot.y(), spot.z(), 5, ParticleTypes.FLAME);
            used += 24;
        }
        return budget - used;
    }

    // ------------------------------------------------------------------

    private static int outline(ServerLevel level, Box box, ParticleOptions particle, int budget) {
        int used = 0;
        double minX = box.minX();
        double minY = box.minY();
        double minZ = box.minZ();
        double maxX = box.maxX() + 1.0D;
        double maxY = box.maxY() + 1.0D;
        double maxZ = box.maxZ() + 1.0D;

        // 12 条边：4 条底边、4 条顶边、4 条竖边
        used += line(level, minX, minY, minZ, maxX, minY, minZ, particle, budget - used);
        used += line(level, maxX, minY, minZ, maxX, minY, maxZ, particle, budget - used);
        used += line(level, maxX, minY, maxZ, minX, minY, maxZ, particle, budget - used);
        used += line(level, minX, minY, maxZ, minX, minY, minZ, particle, budget - used);
        used += line(level, minX, maxY, minZ, maxX, maxY, minZ, particle, budget - used);
        used += line(level, maxX, maxY, minZ, maxX, maxY, maxZ, particle, budget - used);
        used += line(level, maxX, maxY, maxZ, minX, maxY, maxZ, particle, budget - used);
        used += line(level, minX, maxY, maxZ, minX, maxY, minZ, particle, budget - used);
        used += line(level, minX, minY, minZ, minX, maxY, minZ, particle, budget - used);
        used += line(level, maxX, minY, minZ, maxX, maxY, minZ, particle, budget - used);
        used += line(level, maxX, minY, maxZ, maxX, maxY, maxZ, particle, budget - used);
        used += line(level, minX, minY, maxZ, minX, maxY, maxZ, particle, budget - used);
        return budget - used;
    }

    private static int line(ServerLevel level, double x1, double y1, double z1,
                            double x2, double y2, double z2,
                            ParticleOptions particle, int budget) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 1.0E-6D) {
            return 0;
        }
        int steps = (int) Math.min(budget, Math.ceil(length / MAX_STEP));
        if (steps <= 0) {
            return 0;
        }
        for (int i = 0; i < steps; i++) {
            double t = steps == 1 ? 0.0D : (double) i / (steps - 1);
            spawn(level, particle, x1 + dx * t, y1 + dy * t, z1 + dz * t, 1, 0.0D);
        }
        return steps;
    }

    private static void ring(ServerLevel level, Spot spot, ParticleOptions particle, double radius) {
        int steps = Math.max(12, (int) Math.ceil(2.0D * Math.PI * radius));
        for (int i = 0; i < steps; i++) {
            double angle = 2.0D * Math.PI * i / steps;
            spawn(level, particle,
                spot.x() + Math.cos(angle) * radius,
                spot.y() + 0.2D,
                spot.z() + Math.sin(angle) * radius,
                1, 0.0D);
        }
    }

    private static void pillar(ServerLevel level, double x, double y, double z, int height, ParticleOptions particle) {
        for (int i = 0; i < height; i++) {
            spawn(level, particle, x, y + i, z, 1, 0.0D);
        }
    }

    private static void spawn(ServerLevel level, ParticleOptions particle,
                              double x, double y, double z, int count, double spread) {
        level.sendParticles(particle, x, y, z, count, spread, spread, spread, 0.0D);
    }

    /** 仅向指定玩家可见的预览（给指令执行者的即时反馈）。 */
    public static void toPlayer(ServerPlayer player, ParticleOptions particle,
                                double x, double y, double z, int count) {
        ServerLevel level = player.serverLevel();
        level.sendParticles(player, particle, true, x, y, z, count, 0.4D, 0.4D, 0.4D, 0.0D);
    }
}
