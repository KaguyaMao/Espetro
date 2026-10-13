package org.espetro.team;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.espetro.Espetro;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 徐进弹幕执行器（纯 Java，不依赖服务端 KubeJS 脚本版本）。
 *
 * <p>与 155 定点炮击同一个技能、同一个冷却：指挥官在地图上按住右键拖出直线时，
 * 本执行器把 2 发校射 + 6 轮覆盖共 26 发按轮次均匀铺满整条直线，
 * 每发在覆盖半径内随机落点，形成从起点向终点推进的徐进弹幕。</p>
 */
public final class ArtilleryBarrageExecutor {

    private static final String SHELL_ENTITY = "superbwarfare:mortar_shell";
    private static final double COVERAGE_RADIUS = 90.0D;
    private static final int COVER_WAVES = 6;
    private static final int SHELLS_PER_WAVE = 4;
    private static final int SPAWN_HEIGHT = 180;
    private static final double DOWNWARD_VELOCITY = 1.5D;

    private ArtilleryBarrageExecutor() {
    }

    /** 开始一次徐进弹幕（立即返回，炮弹按时间表陆续落下）。 */
    public static void start(ServerLevel level, double x1, double z1, double x2, double z2, int sessionId) {
        if (level == null) {
            return;
        }
        MinecraftServer server = level.getServer();
        if (server == null) {
            return;
        }
        int base = server.getTickCount();
        // 两发校射打在直线起点
        schedule(server, level, base, x1, z1, x2, z2, sessionId, 0, 1, 0);
        schedule(server, level, base + 20 * 20, x1, z1, x2, z2, sessionId, 0, 1, 0);
        // 六轮覆盖沿直线推进
        for (int wave = 0; wave < COVER_WAVES; wave++) {
            schedule(server, level, base + 2 * 20 * 20 + wave * 4 * 20,
                x1, z1, x2, z2, sessionId, wave, SHELLS_PER_WAVE, wave);
        }
        Espetro.LOGGER.info("徐进弹幕已排程: ({}, {}) → ({}, {}) session={}",
            Math.round(x1), Math.round(z1), Math.round(x2), Math.round(z2), sessionId);
    }

    private static void schedule(MinecraftServer server, ServerLevel level, int atTick,
                                 double x1, double z1, double x2, double z2,
                                 int sessionId, int ignoredWave, int count, int waveIndex) {
        server.tell(new TickTask(atTick, () -> fireWave(level, x1, z1, x2, z2, sessionId,
            count, waveIndex)));
    }

    private static void fireWave(ServerLevel level, double x1, double z1, double x2, double z2,
                                 int sessionId, int count, int waveIndex) {
        if (level.getServer() == null) {
            return;
        }
        // 战场变化则中止（与脚本同样的保护）
        if (!org.espetro.mapconfig.BattlefieldContext.isActiveBattlefield(level)) {
            return;
        }
        for (int i = 0; i < count; i++) {
            double t = (waveIndex + (i + 0.5D) / Math.max(1, count)) / (double) COVER_WAVES;
            double baseX = x1 + (x2 - x1) * t;
            double baseZ = z1 + (z2 - z1) * t;
            Vec3 point = randomPoint(baseX, baseZ, COVERAGE_RADIUS);
            int y = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (int) Math.floor(point.x), (int) Math.floor(point.z)) + SPAWN_HEIGHT;
            summonShell(level, point.x, y, point.z);
        }
    }

    private static void summonShell(ServerLevel level, double x, double y, double z) {
        MinecraftServer server = level.getServer();
        if (server == null) {
            return;
        }
        String command = "summon " + SHELL_ENTITY + " "
            + fmt(x) + " " + fmt(y) + " " + fmt(z)
            + " {Motion:[0.0d," + fmt(-DOWNWARD_VELOCITY) + "d,0.0d]}";
        CommandSourceStack source = server.createCommandSourceStack()
            .withSuppressedOutput()
            .withLevel(level);
        server.getCommands().performPrefixedCommand(source, command);
    }

    private static String fmt(double value) {
        return String.format(java.util.Locale.ROOT, "%.3f", value);
    }

    private static Vec3 randomPoint(double centerX, double centerZ, double radius) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double radiusSquared = radius * radius;
        double offsetX = 0.0D;
        double offsetZ = 0.0D;
        for (int attempt = 0; attempt < 16; attempt++) {
            offsetX = (random.nextDouble() * 2 - 1) * radius;
            offsetZ = (random.nextDouble() * 2 - 1) * radius;
            if (offsetX * offsetX + offsetZ * offsetZ <= radiusSquared) {
                return new Vec3(centerX + offsetX, 0.0D, centerZ + offsetZ);
            }
        }
        return new Vec3(centerX + offsetX * 0.5D, 0.0D, centerZ + offsetZ * 0.5D);
    }
}
