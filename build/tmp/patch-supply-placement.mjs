// patch-supply-placement.mjs — 实施修 A（临时补票加载）+ 修 B（放置重试），保留 CRLF
import fs from 'node:fs';

const EOL = '\r\n';
const V = 'src/main/java/org/espetro/vehicle/VehicleManager.java';
const D = 'src/main/java/org/espetro/logistics/DeploySupplyStationPlacer.java';

function patch(file, edits) {
  let text = fs.readFileSync(file, 'utf8');
  const before = text;
  for (const [oldText, newText, label] of edits) {
    const old = oldText.split('\n').join(EOL);
    const neu = newText.split('\n').join(EOL);
    const count = text.split(old).length - 1;
    if (count !== 1) {
      console.error(`  ❌ [${label}] 匹配 ${count} 次（应为 1），未修改`);
      process.exitCode = 1;
      return false;
    }
    text = text.replace(old, neu);
    console.log(`  ✓ [${label}]`);
  }
  fs.writeFileSync(file, text, 'utf8');
  console.log(`  ${file}: ${before.length} → ${text.length} 字符`);
  return true;
}

// ---------- D: DeploySupplyStationPlacer ----------
console.log('== DeploySupplyStationPlacer ==');
patch(D, [
  ['import org.espetro.team.SpawnPointConfig;',
   'import org.espetro.team.SpawnPointConfig;\nimport org.espetro.util.ChunkTickets;',
   'import ChunkTickets'],
  ['import net.minecraft.world.level.block.state.BlockState;',
   'import net.minecraft.world.level.block.state.BlockState;\nimport net.minecraft.world.level.ChunkPos;',
   'import ChunkPos'],
  [`    private static boolean placeOne(ServerLevel level, SpawnPointConfig.SpawnPoint spawn, String team) {
        BlockPos pos = resolveOffset(spawn);
        if (!level.hasChunkAt(pos)) {
            Espetro.LOGGER.warn("部署点弹药箱区块尚未预载，跳过 {} ({})", pos, team);
            return false;
        }

        if (!level.setBlock(pos, Blocks.SHULKER_BOX.defaultBlockState(), 3)) {
            Espetro.LOGGER.warn("无法放置部署点弹药箱 at {} ({})", pos, team);
            return false;
        }
        // 在潜影盒 BlockEntity 上打标记，供交互逻辑识别为「主出生点无限弹药箱」
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            be.getPersistentData().putBoolean(MAIN_BASE_AMMO_KEY, true);
            be.setChanged();
        } else {
            Espetro.LOGGER.warn("部署点弹药箱缺少 BlockEntity at {} ({})", pos, team);
        }

        UUID labelId = spawnLabel(level, pos, team);
        if (labelId == null) {
            Espetro.LOGGER.warn("部署点弹药箱标题生成失败 at {} ({})", pos, team);
            labelId = UUID.randomUUID();
        }
        PLACED.computeIfAbsent(
            level.dimension().location().toString(), ignored -> new java.util.ArrayList<>())
            .add(new PlacedStation(pos.immutable(), labelId));
        return true;
    }`,
   `    private static boolean placeOne(ServerLevel level, SpawnPointConfig.SpawnPoint spawn, String team) {
        BlockPos pos = resolveOffset(spawn);
        // 修 A：地图激活时预载主基地用的 PORTAL 票已在预载结束时释放，而放置被推迟到
        // 「玩家进入战场」之后，此刻目标区块可能已被常规卸载；这里临时补票同步加载，
        // 放置结束后释放（失败路径也会释放，见 ChunkTickets）。
        ChunkPos ticket = ChunkTickets.acquire(level, pos);
        if (!level.hasChunkAt(pos)) {
            ChunkTickets.release(level, ticket);
            Espetro.LOGGER.warn("部署点弹药箱区块尚未预载，跳过 {} ({})", pos, team);
            return false;
        }

        try {
            if (!level.setBlock(pos, Blocks.SHULKER_BOX.defaultBlockState(), 3)) {
                Espetro.LOGGER.warn("无法放置部署点弹药箱 at {} ({})", pos, team);
                return false;
            }
            // 在潜影盒 BlockEntity 上打标记，供交互逻辑识别为「主出生点无限弹药箱」
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null) {
                be.getPersistentData().putBoolean(MAIN_BASE_AMMO_KEY, true);
                be.setChanged();
            } else {
                Espetro.LOGGER.warn("部署点弹药箱缺少 BlockEntity at {} ({})", pos, team);
            }

            UUID labelId = spawnLabel(level, pos, team);
            if (labelId == null) {
                Espetro.LOGGER.warn("部署点弹药箱标题生成失败 at {} ({})", pos, team);
                labelId = UUID.randomUUID();
            }
            PLACED.computeIfAbsent(
                level.dimension().location().toString(), ignored -> new java.util.ArrayList<>())
                .add(new PlacedStation(pos.immutable(), labelId));
        } finally {
            ChunkTickets.release(level, ticket);
        }
        return true;
    }`,
   'placeOne: 补票 + try/finally'],
]);

// ---------- V: VehicleManager ----------
console.log('== VehicleManager ==');
patch(V, [
  ['import org.espetro.team.TroopCountManager;',
   'import org.espetro.team.TroopCountManager;\nimport org.espetro.util.ChunkTickets;',
   'import ChunkTickets'],
  ['    /** 等待已选队玩家进入战场的超时兜底（tick）：超时后强制启动刷新队列。 */\n    private static final int INITIAL_SPAWN_ARM_TIMEOUT_TICKS = 20 * 30;',
   `    /** 等待已选队玩家进入战场的超时兜底（tick）：超时后强制启动刷新队列。 */
    private static final int INITIAL_SPAWN_ARM_TIMEOUT_TICKS = 20 * 30;
    /** 修 B：部署点弹药箱/主重生点补给站放置失败后的重试间隔（tick）：2s。 */
    private static final int DEFERRED_SUPPLY_RETRY_INTERVAL_TICKS = 40;
    /** 修 B：放置重试上限（约 5 分钟）。 */
    private static final int DEFERRED_SUPPLY_MAX_RETRIES = 150;`,
   '常量: 重试间隔/上限'],
  ['    /** 战场激活时置位：等玩家进入战场后再放置部署点弹药箱与主重生点补给站。 */\n    private boolean deferredSupplyPlacementPending;',
   `    /** 战场激活时置位：等玩家进入战场后再放置部署点弹药箱与主重生点补给站。 */
    private boolean deferredSupplyPlacementPending;
    /** 修 B：首次「等玩家进入战场」的门禁是否已通过（通过后重试不再等待）。 */
    private boolean deferredSupplyPlacementArmed;
    /** 修 B：弹药箱与补给站是否已全部放置完成。 */
    private boolean deferredSupplyPlacementDone;
    /** 修 B：已重试次数。 */
    private int deferredSupplyRetryCount;
    /** 修 B：下一次重试的服务器 tick。 */
    private long deferredSupplyNextAttemptTick;`,
   '字段: 重试状态'],
  [`    public void scheduleDeferredSupplyPlacement() {
        deferredSupplyPlacementPending = true;
    }`,
   `    public void scheduleDeferredSupplyPlacement() {
        deferredSupplyPlacementPending = true;
        deferredSupplyPlacementArmed = false;
        deferredSupplyPlacementDone = false;
        deferredSupplyRetryCount = 0;
        deferredSupplyNextAttemptTick = 0L;
    }`,
   'schedule: 重新武装'],
  [`    /**
     * 每 tick 由 {@link #processInitialVehicleDeployments()} 调用：
     * 满足「所有已选队玩家进入战场（或超时兜底）」后执行一次延迟补给站放置。
     */
    private void processDeferredSupplyPlacement(ServerLevel level) {
        if (!deferredSupplyPlacementPending || !initialDeploymentActive) {
            return;
        }
        long tick = level.getGameTime();
        if (initialSpawnArming && !allAssignedPlayersInBattlefield(level)) {
            long waited = tick - initialSpawnArmStartedTick;
            if (waited < INITIAL_SPAWN_ARM_TIMEOUT_TICKS) {
                return; // 还有已选队玩家未进入战场，继续等待
            }
            Espetro.LOGGER.warn(
                "等待玩家进入战场超时({}s)，强制放置部署点弹药箱/补给站",
                waited / 20L);
        }
        deferredSupplyPlacementPending = false;
        try {
            int deployStations = org.espetro.logistics.DeploySupplyStationPlacer
                .placeAtSpawnPoints(level);
            Espetro.LOGGER.info("原部署点无限弹药箱: {} 个", deployStations);
        } catch (Exception e) {
            Espetro.LOGGER.error("预放原部署点无限弹药箱失败", e);
        }
        try {
            int mainBaseStations = spawnMainBaseSupplyStations(level);
            Espetro.LOGGER.info("主重生点弹药补给站: {} 个", mainBaseStations);
        } catch (Exception e) {
            Espetro.LOGGER.error("生成主重生点弹药补给站失败", e);
        }
    }`,
   `    /**
     * 每 tick 由 {@link #processInitialVehicleDeployments()} 调用。
     *
     * <p>首次尝试仍然等「所有已选队玩家进入战场（或超时兜底）」；此后若未能全部放满
     * （例如目标区块当时未加载），按 {@link #DEFERRED_SUPPLY_RETRY_INTERVAL_TICKS} 重试，
     * 直到弹药箱与补给站都放满或达到 {@link #DEFERRED_SUPPLY_MAX_RETRIES} 上限（修 B）。</p>
     */
    private void processDeferredSupplyPlacement(ServerLevel level) {
        if (!deferredSupplyPlacementPending || !initialDeploymentActive
            || deferredSupplyPlacementDone) {
            return;
        }
        long tick = level.getGameTime();
        if (!deferredSupplyPlacementArmed) {
            if (initialSpawnArming && !allAssignedPlayersInBattlefield(level)) {
                long waited = tick - initialSpawnArmStartedTick;
                if (waited < INITIAL_SPAWN_ARM_TIMEOUT_TICKS) {
                    return; // 还有已选队玩家未进入战场，继续等待
                }
                Espetro.LOGGER.warn(
                    "等待玩家进入战场超时({}s)，强制放置部署点弹药箱/补给站",
                    waited / 20L);
            }
            deferredSupplyPlacementArmed = true;
            deferredSupplyNextAttemptTick = tick;
        }
        if (tick < deferredSupplyNextAttemptTick) {
            return;
        }

        int expected = 0;
        for (String team : new String[]{"ATTACK", "DEFEND"}) {
            if (SpawnPointConfig.getSpawnPoint(team) != null) {
                expected++;
            }
        }

        int deployStations = 0;
        try {
            deployStations = org.espetro.logistics.DeploySupplyStationPlacer
                .placeAtSpawnPoints(level);
            Espetro.LOGGER.info("原部署点无限弹药箱: {} 个", deployStations);
        } catch (Exception e) {
            Espetro.LOGGER.error("预放原部署点无限弹药箱失败", e);
        }
        int mainBaseStations = 0;
        try {
            mainBaseStations = spawnMainBaseSupplyStations(level);
            Espetro.LOGGER.info("主重生点弹药补给站: {} 个", mainBaseStations);
        } catch (Exception e) {
            Espetro.LOGGER.error("生成主重生点弹药补给站失败", e);
        }

        if (deployStations >= expected && mainBaseStations >= expected) {
            deferredSupplyPlacementDone = true;
            deferredSupplyPlacementPending = false;
            Espetro.LOGGER.info("部署点弹药箱/主重生点补给站放置完成: 弹药箱 {} 个, 补给站 {} 个",
                deployStations, mainBaseStations);
            return;
        }
        deferredSupplyRetryCount++;
        if (deferredSupplyRetryCount >= DEFERRED_SUPPLY_MAX_RETRIES) {
            Espetro.LOGGER.warn(
                "部署点弹药箱/主重生点补给站重试 {} 次仍未放满（弹药箱 {}/{}，补给站 {}/{}），放弃",
                deferredSupplyRetryCount, deployStations, expected, mainBaseStations, expected);
            deferredSupplyPlacementPending = false;
            return;
        }
        if (deferredSupplyRetryCount <= 3 || deferredSupplyRetryCount % 30 == 0) {
            Espetro.LOGGER.warn(
                "部署点弹药箱/主重生点补给站未放满（弹药箱 {}/{}，补给站 {}/{}），{} tick 后第 {} 次重试",
                deployStations, expected, mainBaseStations, expected,
                DEFERRED_SUPPLY_RETRY_INTERVAL_TICKS, deferredSupplyRetryCount + 1);
        }
        deferredSupplyNextAttemptTick = tick + DEFERRED_SUPPLY_RETRY_INTERVAL_TICKS;
    }`,
   'processDeferredSupplyPlacement: 重试逻辑'],
  [`        deferredSupplyPlacementPending = false;
        initialDeploymentLevel = null;`,
   `        deferredSupplyPlacementPending = false;
        deferredSupplyPlacementArmed = false;
        deferredSupplyPlacementDone = false;
        deferredSupplyRetryCount = 0;
        deferredSupplyNextAttemptTick = 0L;
        initialDeploymentLevel = null;`,
   'reset: 清重试状态'],
  [`    private boolean spawnOneMainBaseSupply(ServerLevel level, EntityType<?> stationType,
                                           SpawnPointConfig.SpawnPoint spawn, String team) {
        BlockPos stationPos = getMainBaseSupplyPosition(spawn);
        if (!level.hasChunkAt(stationPos)) {
            Espetro.LOGGER.warn("主重生点补给站区块尚未预载，跳过 {} ({})",
                stationPos, team);
            return false;
        }

        Entity entity = stationType.create(level);
        if (entity == null) {
            Espetro.LOGGER.warn("无法创建主重生点补给站实体 at {} ({})", stationPos, team);
            return false;
        }

        double x = stationPos.getX() + 0.5;
        double y = stationPos.getY();
        double z = stationPos.getZ() + 0.5;
        entity.setPos(x, y, z);
        entity.setYRot(spawn.yaw);
        entity.setYHeadRot(spawn.yaw);
        entity.setCustomName(Component.literal(SUPPLY_STATION_DISPLAY_NAME));
        entity.setCustomNameVisible(false);
        entity.addTag(MAIN_BASE_SUPPLY_TAG);
        entity.addTag(MAIN_BASE_SUPPLY_TAG + "_team_" + team);
        applySupplyStationMapTags(entity, team, "main_base_" + team);

        // Full FE if the station exposes energy — never Entity#load partial NBT.
        fillVehicleEnergy(entity, Integer.MAX_VALUE);

        if (!level.addFreshEntity(entity)) {
            entity.discard();
            Espetro.LOGGER.warn("主重生点补给站未能加入世界 at {} ({})", stationPos, team);
            return false;
        }
        // 方案 B：稍后重发 spawn 包，兜底客户端区块未就绪导致的丢失
        scheduleSpawnResend(entity);
        return true;
    }`,
   `    private boolean spawnOneMainBaseSupply(ServerLevel level, EntityType<?> stationType,
                                           SpawnPointConfig.SpawnPoint spawn, String team) {
        BlockPos stationPos = getMainBaseSupplyPosition(spawn);
        // 修 A：同 placeOne —— 放置时目标区块可能已被常规卸载，先临时补票加载，
        // 放置结束后释放（失败路径也会释放）。
        ChunkPos ticket = ChunkTickets.acquire(level, stationPos);
        if (!level.hasChunkAt(stationPos)) {
            ChunkTickets.release(level, ticket);
            Espetro.LOGGER.warn("主重生点补给站区块尚未预载，跳过 {} ({})",
                stationPos, team);
            return false;
        }

        try {
            Entity entity = stationType.create(level);
            if (entity == null) {
                Espetro.LOGGER.warn("无法创建主重生点补给站实体 at {} ({})", stationPos, team);
                return false;
            }

            double x = stationPos.getX() + 0.5;
            double y = stationPos.getY();
            double z = stationPos.getZ() + 0.5;
            entity.setPos(x, y, z);
            entity.setYRot(spawn.yaw);
            entity.setYHeadRot(spawn.yaw);
            entity.setCustomName(Component.literal(SUPPLY_STATION_DISPLAY_NAME));
            entity.setCustomNameVisible(false);
            entity.addTag(MAIN_BASE_SUPPLY_TAG);
            entity.addTag(MAIN_BASE_SUPPLY_TAG + "_team_" + team);
            applySupplyStationMapTags(entity, team, "main_base_" + team);

            // Full FE if the station exposes energy — never Entity#load partial NBT.
            fillVehicleEnergy(entity, Integer.MAX_VALUE);

            if (!level.addFreshEntity(entity)) {
                entity.discard();
                Espetro.LOGGER.warn("主重生点补给站未能加入世界 at {} ({})", stationPos, team);
                return false;
            }
            // 方案 B：稍后重发 spawn 包，兜底客户端区块未就绪导致的丢失
            scheduleSpawnResend(entity);
        } finally {
            ChunkTickets.release(level, ticket);
        }
        return true;
    }`,
   'spawnOneMainBaseSupply: 补票 + try/finally'],
]);
console.log(process.exitCode ? '\n有改动失败，请检查' : '\n全部改动成功');
