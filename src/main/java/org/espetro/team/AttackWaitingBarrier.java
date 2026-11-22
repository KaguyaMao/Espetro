package org.espetro.team;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.espetro.Espetro;
import org.espetro.bastion.BastionManager;
import org.espetro.config.GameConfig;
import org.espetro.network.NetworkManager;
import org.espetro.network.DeployScreenSync;
import org.espetro.network.FactionDisplay;
import org.espetro.network.MatchBroadcasts;
import org.espetro.network.PreMatchScreens;
import org.espetro.network.SquadSync;
import org.espetro.vehicle.VehicleManager;
import org.espetro.vehicle.VehicleEventHandler;
import org.espetro.stats.PlayerMatchStatsManager;
import org.espetro.mapconfig.ExternalConfigBootstrap;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.governance.CommanderGovernanceManager;
import org.espetro.dimension.BattlefieldWorldManager;
import org.espetro.logistics.SupplyManager;
import org.espetro.logistics.resupply.ResupplySessionManager;
import net.minecraftforge.common.MinecraftForge;
import org.espetro.api.event.GamePhaseChangedEvent;
import java.util.*;
import java.util.function.Consumer;

/**
 * 攻方等待屏障：防守部署阶段在攻方出生点四周临时放置的屏障外壳。
 * <p>
 * 放置与恢复都按 {@link #BLOCKS_PER_TICK} 分帧进行，避免单 tick 上万次 setBlock；
 * 只记录并恢复实际被替换的方块。由 {@link GameStateManager} 持有，从其中拆出。
 */
final class AttackWaitingBarrier {

    /** 硬上限：防止配置/默认值过大导致单 tick 上万 setBlock（S100 尖峰）。 */
    static final int SIDE = 80;

    static final int HEIGHT = 12;

    /** 每 tick 最多放置/恢复的屏障块数（分帧，避免开战假死）。 */
    private static final int BLOCKS_PER_TICK = 400;

    // 攻方等待防守部署时临时放置的屏障，记录原方块以便开战后恢复
    private final Map<BlockPos, BlockState> placedBlocks = new HashMap<>();

    private ResourceKey<Level> dimension;

    /** 分帧放置队列：pos → 写入前的方块（AIR 占位则存 AIR） */
    private final ArrayDeque<BlockPos> pendingPlace = new ArrayDeque<>();

    private final ArrayDeque<Map.Entry<BlockPos, BlockState>> pendingRemove = new ArrayDeque<>();

    private ServerLevel workLevel;

    void place() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;

        remove();

        ServerLevel level = BattlefieldContext.requireBattlefield(server);
        if (!BattlefieldContext.isActiveBattlefield(level)) {
            Espetro.LOGGER.error("拒绝在非活动战场维度创建部署屏障: {}",
                level.dimension().location());
            return;
        }
        dimension = level.dimension();
        workLevel = level;
        SpawnPointConfig.SpawnPoint spawn = SpawnPointConfig.getSpawnPoint("ATTACK");
        BlockPos center = new BlockPos((int) Math.floor(spawn.x), (int) Math.floor(spawn.y), (int) Math.floor(spawn.z));

        int half = SIDE / 2;
        int minX = center.getX() - half;
        int maxX = minX + SIDE - 1;
        int minZ = center.getZ() - half;
        int maxZ = minZ + SIDE - 1;
        int baseY = center.getY();
        // 仅外壳；入队分帧放置，避免单 tick 上万 setBlock
        pendingPlace.clear();
        for (int y = baseY; y < baseY + HEIGHT; y++) {
            for (int x = minX; x <= maxX; x++) {
                pendingPlace.add(new BlockPos(x, y, minZ));
                pendingPlace.add(new BlockPos(x, y, maxZ));
            }
            for (int z = minZ + 1; z < maxZ; z++) {
                pendingPlace.add(new BlockPos(minX, y, z));
                pendingPlace.add(new BlockPos(maxX, y, z));
            }
        }

        int queued = pendingPlace.size();
        processWork();
        Espetro.LOGGER.info("已排队攻方等待屏障: center={}, side={}, height={}, queued={}, placedNow={}",
            center, SIDE, HEIGHT,
            queued, placedBlocks.size());
    }

    private void setTemporaryBarrier(ServerLevel level, BlockPos pos) {
        BlockState previous = level.getBlockState(pos);
        if (!previous.isAir() && !previous.getCollisionShape(level, pos).isEmpty()) {
            return;
        }
        if (!placedBlocks.containsKey(pos)) {
            placedBlocks.put(pos.immutable(), previous);
        }
        level.setBlock(pos, Blocks.BARRIER.defaultBlockState(), 18);
    }

    /** 分帧放置/恢复屏障；部署 tick 与开战清理共用。 */
    void processWork() {
        ServerLevel level = workLevel;
        if (level == null && dimension != null) {
            MinecraftServer server = Espetro.getServer();
            if (server != null) {
                level = server.getLevel(dimension);
                workLevel = level;
            }
        }
        if (level == null) {
            pendingPlace.clear();
            pendingRemove.clear();
            return;
        }

        int budget = BLOCKS_PER_TICK;
        while (budget > 0 && !pendingPlace.isEmpty()) {
            setTemporaryBarrier(level, pendingPlace.poll());
            budget--;
        }
        while (budget > 0 && !pendingRemove.isEmpty()) {
            Map.Entry<BlockPos, BlockState> entry = pendingRemove.poll();
            BlockPos pos = entry.getKey();
            if (level.getBlockState(pos).is(Blocks.BARRIER)) {
                level.setBlock(pos, entry.getValue(), 3);
            }
            budget--;
        }
        if (pendingPlace.isEmpty() && pendingRemove.isEmpty()) {
            // 放置队列清空后保留 workLevel 直到 remove 完成
        }
    }

    int remove() {
        return remove(Espetro.getServer());
    }

    int remove(MinecraftServer server) {
        pendingPlace.clear();
        if (server == null) {
            placedBlocks.clear();
            dimension = null;
            pendingRemove.clear();
            workLevel = null;
            return 0;
        }

        ResourceKey<Level> recordedDimension = dimension;
        ServerLevel level = recordedDimension == null ? null : server.getLevel(recordedDimension);
        if (level == null || Level.OVERWORLD.equals(level.dimension())) {
            placedBlocks.clear();
            dimension = null;
            pendingRemove.clear();
            workLevel = null;
            return 0;
        }
        workLevel = level;
        pendingRemove.clear();
        for (Map.Entry<BlockPos, BlockState> entry : placedBlocks.entrySet()) {
            pendingRemove.add(Map.entry(entry.getKey(), entry.getValue()));
        }
        int queued = pendingRemove.size();
        placedBlocks.clear();
        // 即使管理员强制终止也只消耗一帧预算；战场副本随后会整体删除。
        processWork();
        if (pendingRemove.isEmpty()) {
            dimension = null;
            workLevel = null;
        }
        if (queued > 0) {
            Espetro.LOGGER.info("已清理攻方等待屏障: 排队恢复{}个, 本批处理剩余{}",
                queued, pendingRemove.size());
        }
        return queued;
    }

    private int removeOrphaned(ServerLevel level) {
        SpawnPointConfig.SpawnPoint spawn = SpawnPointConfig.getSpawnPoint("ATTACK");
        BlockPos center = new BlockPos(
            (int) Math.floor(spawn.x), (int) Math.floor(spawn.y), (int) Math.floor(spawn.z));
        int half = SIDE / 2;
        int minX = center.getX() - half;
        int maxX = minX + SIDE - 1;
        int minZ = center.getZ() - half;
        int maxZ = minZ + SIDE - 1;
        int baseY = center.getY();
        int removed = 0;

        for (int y = baseY; y < baseY + HEIGHT; y++) {
            for (int x = minX; x <= maxX; x++) {
                removed += removeBarrierAt(level, new BlockPos(x, y, minZ));
                removed += removeBarrierAt(level, new BlockPos(x, y, maxZ));
            }
            for (int z = minZ + 1; z < maxZ; z++) {
                removed += removeBarrierAt(level, new BlockPos(minX, y, z));
                removed += removeBarrierAt(level, new BlockPos(maxX, y, z));
            }
        }

        return removed;
    }

    private int removeBarrierAt(ServerLevel level, BlockPos pos) {
        if (!level.getBlockState(pos).is(Blocks.BARRIER)) {
            return 0;
        }
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 18);
        return 1;
    }

    /**
     * Stop placing the deployment shell and queue restoration of only the
     * blocks that were actually changed. Unlike the administrative cleanup
     * path this never drains thousands of setBlock calls in one tick.
     */
    void queueRemoval() {
        pendingPlace.clear();
        pendingRemove.clear();
        for (Map.Entry<BlockPos, BlockState> entry : placedBlocks.entrySet()) {
            pendingRemove.add(Map.entry(entry.getKey(), entry.getValue()));
        }
        int queued = pendingRemove.size();
        placedBlocks.clear();
        Espetro.LOGGER.info("部署结束：已分帧排队恢复攻方等待屏障 {} 个", queued);
    }

    /** 是否还有待放置或待恢复的方块。 */
    boolean hasPendingWork() {
        return !pendingPlace.isEmpty() || !pendingRemove.isEmpty();
    }

    /** 恢复全部完成后释放对战场维度的引用。 */
    void releaseLevel() {
        dimension = null;
        workLevel = null;
    }

    /** 若已无待处理的方块，释放对战场维度的引用。 */
    void releaseLevelIfIdle() {
        if (!hasPendingWork() && dimension != null && workLevel != null) {
            releaseLevel();
        }
    }
}
