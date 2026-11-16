package org.espetro.naval;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import org.espetro.Espetro;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.mapconfig.VehSpawnSnapshot;
import org.espetro.team.SpawnPointConfig;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 两栖登陆部署编排：开图时把舰船粘贴进战场维度、解析甲板出生点与载具点、
 * 生成屏障，并在 {@code BattlefieldContext.activate} 之后覆盖出生点/载具点。
 *
 * <p>时序（见 {@code docs/AMPHIBIOUS_ASSAULT_PLAN.md} §3）：</p>
 * <pre>
 * BattlefieldWorldManager.rebuildSelectedMap
 *   → createServerLevel 挂载维度
 *   → AmphibiousDeployment.prepare(...)      粘贴 + 扫描 + 屏障（本类）
 *   → BattlefieldContext.activate(map)       会写回地图原始 spawn
 *   → AmphibiousDeployment.applyRuntime(map) 覆盖成甲板点 + 合并载具点
 * </pre>
 */
public final class AmphibiousDeployment {

    private static volatile State STATE;

    /** 舰船屏障的方块总数上限：防止配置失误把开图变成十万次 setBlock。 */
    public static final int MAX_BARRIER_BLOCKS = 200_000;

    private AmphibiousDeployment() {
    }

    /** 本轮两栖部署结果（只存在内存，随维度销毁）。 */
    public static final class State {
        public final String mapFolder;
        public final List<BlockPos> barrierBlocks;
        public final boolean hasSpawn;
        public final double spawnX;
        public final double spawnY;
        public final double spawnZ;
        public final float spawnYaw;
        public final Map<String, List<AmphibiousConfig.PointEntry>> vehiclePoints;

        private State(String mapFolder, List<BlockPos> barrierBlocks, boolean hasSpawn,
                      double spawnX, double spawnY, double spawnZ, float spawnYaw,
                      Map<String, List<AmphibiousConfig.PointEntry>> vehiclePoints) {
            this.mapFolder = mapFolder;
            this.barrierBlocks = List.copyOf(barrierBlocks);
            this.hasSpawn = hasSpawn;
            this.spawnX = spawnX;
            this.spawnY = spawnY;
            this.spawnZ = spawnZ;
            this.spawnYaw = spawnYaw;
            this.vehiclePoints = vehiclePoints;
        }
    }

    @Nullable
    public static State state() {
        return STATE;
    }

    public static boolean hasActiveLayout() {
        State state = STATE;
        return state != null && !state.barrierBlocks.isEmpty();
    }

    public static List<BlockPos> barrierBlocks() {
        State state = STATE;
        return state == null ? List.of() : state.barrierBlocks;
    }

    public static void reset() {
        STATE = null;
    }

    /**
     * 粘贴舰船并解析点位。必须在维度挂载之后、{@code BattlefieldContext.activate} 之前调用。
     *
     * @return 是否成功布置（false 表示按普通侵攻运行）
     */
    public static boolean prepare(MinecraftServer server, ServerLevel level, ActiveMapConfig map) {
        reset();
        AmphibiousConfig config = map.amphibious;
        if (config == null || !config.enabled) {
            return false;
        }
        if (!config.errors.isEmpty()) {
            Espetro.LOGGER.warn("两栖配置存在问题，按普通侵攻运行: {}", String.join("; ", config.errors));
            return false;
        }

        List<BlockPos> barrier = new ArrayList<>();
        Map<String, List<AmphibiousConfig.PointEntry>> vehPoints = new LinkedHashMap<>();
        AmphibiousConfig.Point deckSpawn = config.overrideSpawn != null ? config.overrideSpawn.attack : null;
        int placedShips = 0;

        for (AmphibiousConfig.Ship ship : config.ships) {
            if ("world".equals(ship.source)) {
                Espetro.LOGGER.info("舰船 {} 已烘在地图模板里，跳过粘贴（只接受 override_spawn 点位）",
                    ship.id);
                continue;
            }
            NavalStructureService.Loaded loaded;
            try {
                loaded = NavalStructureService.load(server, ship);
            } catch (Exception e) {
                Espetro.LOGGER.error("舰船 {} 加载失败: {}", ship.id, e.getMessage());
                continue;
            }
            Rotation rotation = NavalStructureService.rotationOf(ship.rotation);
            BlockPos anchor = new BlockPos(
                (int) Math.floor(ship.x),
                (int) Math.floor(ship.y) + ship.draftAdjust,
                (int) Math.floor(ship.z));
            int blocks = NavalStructureService.place(level, loaded, anchor, rotation, true);
            placedShips++;
            BlockPos[] bounds = NavalStructureService.rotatedBounds(anchor, loaded.size, rotation);
            Espetro.LOGGER.info("已粘贴舰船 {}（{}）: anchor={}, size={}, blocks={}, bounds={}~{}",
                ship.id, ship.structure, anchor, loaded.size, blocks, bounds[0], bounds[1]);

            for (NavalStructureService.Marker marker : loaded.markers) {
                BlockPos world = NavalStructureService.worldPos(anchor, marker.local(), rotation);
                String kind = marker.kind().toLowerCase(Locale.ROOT);
                if (kind.equals("attack_spawn")) {
                    if (deckSpawn == null && "lhd".equals(ship.role)) {
                        deckSpawn = new AmphibiousConfig.Point(world.getX() + 0.5D,
                            world.getY() + 1.0D, world.getZ() + 0.5D, marker.yaw());
                    }
                } else if (kind.startsWith("veh:")) {
                    parseVehMarker(kind, world, marker.yaw(), vehPoints);
                }
            }

            if (!"none".equals(ship.barrierMode)) {
                // height<=0 → 自动：四壁到船顶上一格、顶盖再上一格，整船封闭。
                int height = ship.barrierHeight > 0
                    ? ship.barrierHeight
                    : Math.max(2, (bounds[1].getY() - bounds[0].getY()) + 2);
                List<BlockPos> shell = BarrierShell.box(bounds[0], bounds[1], height, ship.barrierCap);
                if (barrier.size() + shell.size() > MAX_BARRIER_BLOCKS) {
                    Espetro.LOGGER.error("舰船 {} 屏障方块过多: {}（上限 {}），跳过该舰屏障",
                        ship.id, shell.size(), MAX_BARRIER_BLOCKS);
                } else {
                    Espetro.LOGGER.info("舰船 {} 屏障: mode={}, height={}, cap={}, blocks={}",
                        ship.id, ship.barrierMode, height, ship.barrierCap, shell.size());
                    barrier.addAll(shell);
                }
            }
        }

        if (deckSpawn == null) {
            Espetro.LOGGER.warn("两栖地图 {} 没有找到甲板出生点标记，也没有 override_spawn.attack；按普通侵攻运行",
                map.mapFolder);
            reset();
            return false;
        }

        if (config.overrideSpawn != null) {
            for (Map.Entry<String, List<AmphibiousConfig.PointEntry>> entry
                : config.overrideSpawn.vehicles.entrySet()) {
                vehPoints.putIfAbsent(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue());
            }
        }

        STATE = new State(map.mapFolder, barrier, true,
            deckSpawn.x(), deckSpawn.y(), deckSpawn.z(), deckSpawn.yaw(), vehPoints);
        Espetro.LOGGER.info("两栖地图已就绪: {}（舰船 {} 艘，屏障 {} 格，载具类型 {} 种，甲板出生点 {},{},{}）",
            map.displayName, placedShips, barrier.size(), vehPoints.size(),
            deckSpawn.x(), deckSpawn.y(), deckSpawn.z());
        return true;
    }

    /**
     * 在 {@code BattlefieldContext.activate(map)} **之前**调用：把舰上载具点合并进本轮快照
     * （只改内存）。必须早于 activate，因为 {@code GameConfigBridge.apply} 里的
     * {@code VehicleConfig.applyActiveMap} 会按 VehSpawn 重新计算每个编制的载具槽位。
     */
    public static void applyData(ActiveMapConfig map) {
        State state = STATE;
        if (state == null || map == null) {
            return;
        }
        if (!state.vehiclePoints.isEmpty() && map.vehSpawn != null) {
            map.vehSpawn = VehSpawnMerge.merge(map.vehSpawn, state.vehiclePoints);
            Espetro.LOGGER.info("两栖载具点已合并: {}", state.vehiclePoints.keySet());
        }
    }

    /**
     * 纯函数版本，供 {@code ActiveMapConfig.forRound} 使用。
     * <p>开图时 {@code BattlefieldContext.activate} 会调用 {@code forRound(seed)} 构造**新的**快照，
     * 新快照是从地图原始 {@code VehSpawn.json} 读出来的，会丢掉 {@link #applyData} 写进旧快照的合并结果，
     * 导致守方载具又回到舰上。因此在 {@code forRound} 里也必须做一次合并。</p>
     */
    public static VehSpawnSnapshot mergedVehSpawn(VehSpawnSnapshot base) {
        State state = STATE;
        if (state == null || base == null || state.vehiclePoints.isEmpty()) {
            return base;
        }
        return VehSpawnMerge.merge(base, state.vehiclePoints);
    }

    /**
     * 在 {@code BattlefieldContext.activate(map)} **之后**调用：覆盖攻方出生点。
     * 必须晚于 activate，因为 activate 会把地图原始 spawn_points 写回运行时配置。
     */
    public static void applySpawnPoint() {
        State state = STATE;
        if (state == null || !state.hasSpawn) {
            return;
        }
        SpawnPointConfig.SpawnPoint existingMain = SpawnPointConfig.getSpawnPoint("ATTACK", 0);
        String mainName = existingMain != null && existingMain.name != null && !existingMain.name.isBlank()
            ? existingMain.name : "075飞行甲板";
        SpawnPointConfig.overrideMainSpawn("ATTACK", 0,
            new SpawnPointConfig.SpawnPoint(state.spawnX, state.spawnY, state.spawnZ,
                state.spawnYaw, mainName));
        Espetro.LOGGER.info("两栖攻方出生点已覆盖: {}, {}, {} (yaw {})",
            String.format(Locale.ROOT, "%.1f", state.spawnX),
            String.format(Locale.ROOT, "%.1f", state.spawnY),
            String.format(Locale.ROOT, "%.1f", state.spawnZ), state.spawnYaw);
    }

    /**
     * 把舰船占用的区块加入开图预载集合，避免粘贴时同步生成区块造成卡顿。
     * 只需配置即可计算（不依赖已粘贴的方块）。
     */
    public static void collectShipChunks(@Nullable ActiveMapConfig map,
                                         java.util.Set<net.minecraft.world.level.ChunkPos> out) {
        AmphibiousConfig config = map == null ? null : map.amphibious;
        if (config == null || !config.enabled || out == null) {
            return;
        }
        MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        for (AmphibiousConfig.Ship ship : config.ships) {
            if ("world".equals(ship.source)) {
                continue;
            }
            NavalStructureService.Loaded loaded;
            try {
                loaded = NavalStructureService.load(server, ship);
            } catch (Exception e) {
                continue;
            }
            Rotation rotation = NavalStructureService.rotationOf(ship.rotation);
            BlockPos anchor = new BlockPos(
                (int) Math.floor(ship.x),
                (int) Math.floor(ship.y) + ship.draftAdjust,
                (int) Math.floor(ship.z));
            BlockPos[] bounds = NavalStructureService.rotatedBounds(anchor, loaded.size, rotation);
            net.minecraft.world.level.ChunkPos min = new net.minecraft.world.level.ChunkPos(bounds[0]);
            net.minecraft.world.level.ChunkPos max = new net.minecraft.world.level.ChunkPos(bounds[1]);
            for (int cx = min.x - 1; cx <= max.x + 1; cx++) {
                for (int cz = min.z - 1; cz <= max.z + 1; cz++) {
                    out.add(new net.minecraft.world.level.ChunkPos(cx, cz));
                }
            }
        }
    }

    private static void parseVehMarker(String kind, BlockPos world, float yaw,
                                       Map<String, List<AmphibiousConfig.PointEntry>> out) {
        // veh:<type>:<index>[:defend]
        String[] parts = kind.split(":");
        if (parts.length < 3) {
            return;
        }
        String type = parts[1].toLowerCase(Locale.ROOT);
        boolean defend = parts.length >= 4 && "defend".equals(parts[3]);
        int index;
        try {
            index = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            index = 0;
        }
        AmphibiousConfig.Point point = new AmphibiousConfig.Point(world.getX() + 0.5D,
            world.getY(), world.getZ() + 0.5D, yaw);
        List<AmphibiousConfig.PointEntry> list = out.computeIfAbsent(type, k -> new ArrayList<>());
        while (list.size() <= index) {
            list.add(null);
        }
        AmphibiousConfig.PointEntry existing = list.get(index);
        AmphibiousConfig.Point attack = existing == null ? null : existing.attack;
        AmphibiousConfig.Point defendPoint = existing == null ? null : existing.defend;
        if (defend) {
            defendPoint = point;
        } else {
            attack = point;
        }
        list.set(index, newPointEntry(type + "_" + (index + 1), attack, defendPoint));
    }

    private static AmphibiousConfig.PointEntry newPointEntry(String id,
                                                             @Nullable AmphibiousConfig.Point attack,
                                                             @Nullable AmphibiousConfig.Point defend) {
        return AmphibiousConfig.PointEntry.of(id, attack, defend);
    }
}
