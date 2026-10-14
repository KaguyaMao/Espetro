package org.espetro.team;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.espetro.Espetro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 徐进弹幕执行器（纯 Java）。
 *
 * <p>与 155 定点炮击同一个技能、同一个冷却。时序与 KubeJS 脚本一致：
 * 释放后等 30 秒 → 起点附近两发校射 → 之后每一轮的中心点均匀分配在整条路径上，
 * 一路炸过去。每轮炮弹在该中心点周围的覆盖半径内随机落点。</p>
 *
 * <p>注意：不使用 {@code server.tell(new TickTask(...))} 做长延迟调度 ——
 * 部分 Forge/KubeJS 组合会丢失较长延迟的回调，表现为"刚画完路径整条线上先炸一遍"。
 * 这里改为自维护波次列表 + 每 tick 判定绝对 tick 数。</p>
 */
@Mod.EventBusSubscriber(modid = Espetro.MOD_ID)
public final class ArtilleryBarrageExecutor {

    private static final String SHELL_ENTITY = "superbwarfare:cannon_shell";
    /** 与 KubeJS 脚本一致：散布半径 45 格。 */
    private static final double COVERAGE_RADIUS = 45.0D;
    /** 释放后到第一发的延迟：30 秒。 */
    private static final int START_DELAY_TICKS = 30 * 20;
    private static final int SIGHT_SECOND_OFFSET_TICKS = 50;
    /** 覆盖射击起点（相对延迟）：400 tick = 20 秒。 */
    private static final int COVER_START_OFFSET_TICKS = 400;
    private static final int COVER_WAVES = 8;
    private static final int SHELLS_PER_WAVE = 6;
    private static final int WAVE_INTERVAL_TICKS = 140;   // 每轮间隔 7 秒
    private static final int SPAWN_HEIGHT = 180;
    private static final double DOWNWARD_VELOCITY = 1.5D;
    /** 炮弹战斗部强化（与脚本同一套 NBT）。 */
    private static final String SHELL_NBT = ",Damage:300.0f,ExplosionDamage:600.0f,Radius:12.0f";

    /** 一波待发射的弹幕。centerT ∈ [0,1] 为该轮中心在路径上的位置。 */
    private record PendingWave(int atTick, ServerLevel level,
                               double x1, double z1, double x2, double z2,
                               int count, double centerT) {
    }

    private static final List<PendingWave> PENDING = new ArrayList<>();

    private ArtilleryBarrageExecutor() {
    }

    /** 开始一次徐进弹幕（立即返回，波次由服务端 tick 驱动）。 */
    public static void start(ServerLevel level, double x1, double z1, double x2, double z2, int ignoredSession) {
        if (level == null || level.getServer() == null) {
            return;
        }
        MinecraftServer server = level.getServer();
        int base = server.getTickCount() + START_DELAY_TICKS;   // 30 秒后才开始落弹
        synchronized (PENDING) {
            PENDING.clear();   // 同一时间只保留一次进行中的弹幕，避免叠加
            // 两发校射：落在起点附近
            PENDING.add(new PendingWave(base, level, x1, z1, x2, z2, 1, 0.0D));
            PENDING.add(new PendingWave(base + SIGHT_SECOND_OFFSET_TICKS,
                level, x1, z1, x2, z2, 1, 0.0D));
            // 八轮覆盖：每轮中心点均匀分配在整条路径上
            for (int wave = 0; wave < COVER_WAVES; wave++) {
                double centerT = (wave + 0.5D) / COVER_WAVES;
                PENDING.add(new PendingWave(
                    base + COVER_START_OFFSET_TICKS + wave * WAVE_INTERVAL_TICKS,
                    level, x1, z1, x2, z2, SHELLS_PER_WAVE, centerT));
            }
        }
        Espetro.LOGGER.info("徐进弹幕已排程: ({}, {}) → ({}, {}), 首发 {} 秒后, {} 轮覆盖",
            Math.round(x1), Math.round(z1), Math.round(x2), Math.round(z2),
            START_DELAY_TICKS / 20, COVER_WAVES);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || PENDING.isEmpty()) {
            return;
        }
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        int now = server.getTickCount();
        List<PendingWave> due = null;
        synchronized (PENDING) {
            Iterator<PendingWave> iterator = PENDING.iterator();
            while (iterator.hasNext()) {
                PendingWave wave = iterator.next();
                if (now >= wave.atTick()) {
                    if (due == null) {
                        due = new ArrayList<>();
                    }
                    due.add(wave);
                    iterator.remove();
                }
            }
        }
        if (due == null) {
            return;
        }
        for (PendingWave wave : due) {
            try {
                fireWave(wave);
            } catch (RuntimeException error) {
                Espetro.LOGGER.warn("徐进弹幕落弹失败", error);
            }
        }
    }

    private static void fireWave(PendingWave wave) {
        ServerLevel level = wave.level();
        if (level == null || level.getServer() == null) {
            return;
        }
        if (!org.espetro.mapconfig.BattlefieldContext.isActiveBattlefield(level)) {
            return;   // 战场已切换：停止剩余波次
        }
        double centerX = wave.x1() + (wave.x2() - wave.x1()) * wave.centerT();
        double centerZ = wave.z1() + (wave.z2() - wave.z1()) * wave.centerT();
        // 本轮炮弹按角度均匀铺开（随机起始角 + 半径 0.55~1.0 随机），与定点炮击一致
        double spreadStep = Math.PI * 2.0D / Math.max(1, wave.count());
        double spreadStart = ThreadLocalRandom.current().nextDouble() * Math.PI * 2.0D;
        for (int i = 0; i < wave.count(); i++) {
            double angle = spreadStart + spreadStep * i;
            double distance = COVERAGE_RADIUS
                * (0.15D + 0.35D * ThreadLocalRandom.current().nextDouble());
            double px = centerX + Math.cos(angle) * distance;
            double pz = centerZ + Math.sin(angle) * distance;
            int y = level.getHeight(
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (int) Math.floor(px), (int) Math.floor(pz)) + SPAWN_HEIGHT;
            summonShell(level, px, y, pz);
        }
    }

    private static void summonShell(ServerLevel level, double x, double y, double z) {
        MinecraftServer server = level.getServer();
        if (server == null) {
            return;
        }
        String command = "summon " + SHELL_ENTITY + " "
            + fmt(x) + " " + fmt(y) + " " + fmt(z)
            + " {Rotation:[0.0f,90.0f],Motion:[0.0d," + fmt(-DOWNWARD_VELOCITY) + "d,0.0d]" + SHELL_NBT + "}";
        CommandSourceStack source = server.createCommandSourceStack()
            .withSuppressedOutput()
            .withLevel(level);
        server.getCommands().performPrefixedCommand(source, command);
    }

    private static String fmt(double value) {
        return String.format(Locale.ROOT, "%.3f", value);
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
