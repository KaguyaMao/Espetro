/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraftforge.common.capabilities.ForgeCapabilities
 */
package org.espetro.vehicle;

import java.lang.invoke.CallSite;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.espetro.Espetro;
import org.espetro.api.EspetroAPI;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.mapconfig.VehSpawnSnapshot;
import org.espetro.network.NetworkManager;
import org.espetro.team.ClassCountManager;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;
import org.espetro.team.SpawnPointConfig;
import org.espetro.team.TroopCountManager;
import org.espetro.team.VoteManager;
import org.espetro.vehicle.InitialVehicleDeploymentLedger;
import org.espetro.vehicle.VehicleConfig;

public class VehicleManager {
    private static VehicleManager INSTANCE;
    private static final String VEHICLE_TAG = "espetro_vehicle";
    public static final String VEHICLE_TEAM_KEY = "espetro_vehicle_team";
    private static final String PAD_SUPPLY_TAG = "espetro_vehicle_pad_supply";
    public static final String SUPPLY_STATION_TAG = "espetro_vehicle_supply_station";
    public static final String SUPPLY_STATION_TEAM_KEY = "espetro_vehicle_supply_station_team";
    public static final String SUPPLY_STATION_ID_KEY = "espetro_vehicle_supply_station_id";
    public static final String SUPPLY_STATION_X_KEY = "espetro_vehicle_supply_station_x";
    public static final String SUPPLY_STATION_Y_KEY = "espetro_vehicle_supply_station_y";
    public static final String SUPPLY_STATION_Z_KEY = "espetro_vehicle_supply_station_z";
    public static final String SUPPLY_STATION_DISPLAY_NAME = "\u8f7d\u5177\u8865\u7ed9\u7ad9";
    private static final ResourceLocation SUPPLY_STATION_ID;
    private static final ResourceLocation SUPPLY_STATION_ITEM_ID;
    private static final double SUPPLY_SIDE_OFFSET = 6.0;
    private static final double MAIN_BASE_SUPPLY_SIDE_OFFSET = 3.0;
    private static final String MAIN_BASE_SUPPLY_TAG = "espetro_main_base_supply_station";
    private static final int INITIAL_CHUNKS_STARTED_PER_TICK = 2;
    private static final int INITIAL_CHUNKS_MAX_IN_FLIGHT = 4;
    private final Map<String, Map<String, List<UUID>>> activeVehicles = new HashMap<String, Map<String, List<UUID>>>();
    private final Set<UUID> activeVehicleIds = new HashSet<UUID>();
    private final Map<UUID, ActiveVehicleData> activeVehicleData = new HashMap<UUID, ActiveVehicleData>();
    private final Map<UUID, VehicleSupplyState> vehicleSupplies = new HashMap<UUID, VehicleSupplyState>();
    private final Map<UUID, SupplyStationSnapshot> mappedSupplyStations = new HashMap<UUID, SupplyStationSnapshot>();
    private final Map<String, Map<String, Long>> cooldowns = new HashMap<String, Map<String, Long>>();
    private final Map<RespawnKey, PriorityQueue<Long>> autoRespawnQueue = new HashMap<RespawnKey, PriorityQueue<Long>>();
    private final InitialVehicleDeploymentLedger initialDeploymentLedger = new InitialVehicleDeploymentLedger();
    private final PriorityQueue<PendingInitialVehicle> delayedInitialVehicles = new PriorityQueue<PendingInitialVehicle>(Comparator.comparingLong(PendingInitialVehicle::readyAtEpochMs));
    private final Map<ChunkPos, List<PendingInitialVehicle>> initialVehiclesByChunk = new LinkedHashMap<ChunkPos, List<PendingInitialVehicle>>();
    private final ArrayDeque<ChunkPos> pendingInitialChunks = new ArrayDeque();
    private final Set<ChunkPos> readyInitialChunks = new LinkedHashSet<ChunkPos>();
    private final Set<ChunkPos> ticketedInitialChunks = new LinkedHashSet<ChunkPos>();
    @Nullable
    private ServerLevel initialDeploymentLevel;
    private int initialChunksInFlight;
    private long initialDeploymentGeneration;
    private boolean initialDeploymentActive;
    private boolean initialDeploymentCompletionLogged;
    private int initialVehiclesPlanned;
    private int initialVehiclesSpawned;
    private int initialVehiclesFailed;
    private static final long INITIAL_DEPLOY_GLOBAL_DELAY_MS = 5000L;
    @Nullable
    private ServerLevel pendingMainBaseStationLevel;
    private long pendingMainBaseStationAtEpochMs;
    public static final double SUPPLY_INTERACT_RANGE = 5.0;

    private VehicleManager() {
        INSTANCE = this;
    }

    public static VehicleManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VehicleManager();
        }
        return INSTANCE;
    }

    public int getActiveCount(String factionId, String vehicleType) {
        List<UUID> vehicles = this.findList(factionId, vehicleType);
        return vehicles == null ? 0 : vehicles.size();
    }

    public int getActiveCount(String team, String factionId, String vehicleType) {
        int count = 0;
        for (ActiveVehicleData data : this.activeVehicleData.values()) {
            if (!factionId.equals(data.factionId()) || !vehicleType.equals(data.vehicleType()) || !team.equalsIgnoreCase(data.team())) continue;
            ++count;
        }
        return count;
    }

    private List<UUID> getList(String factionId, String vehicleType) {
        return this.activeVehicles.computeIfAbsent(factionId, k -> new HashMap()).computeIfAbsent(vehicleType, k -> new ArrayList());
    }

    @Nullable
    private List<UUID> findList(String factionId, String vehicleType) {
        Map<String, List<UUID>> typeMap = this.activeVehicles.get(factionId);
        return typeMap != null ? typeMap.get(vehicleType) : null;
    }

    public long getCooldownRemaining(String factionId, String vehicleType) {
        return this.getCooldownRemaining(GameStateManager.getTeamFromFactionStatic(factionId), factionId, vehicleType);
    }

    public long getCooldownRemaining(@Nullable String team, String factionId, String vehicleType) {
        Long until;
        Map<String, Long> factionCooldowns = this.cooldowns.get(VehicleManager.cooldownOwner(team, factionId));
        Long l = until = factionCooldowns != null ? factionCooldowns.get(vehicleType) : null;
        if (until == null) {
            return 0L;
        }
        return Math.max(0L, until - System.currentTimeMillis());
    }

    public void armInitialDeployCooldowns(@Nullable String attackFaction, String attackTeam, @Nullable String defendFaction, String defendTeam) {
        this.armFactionInitialCooldowns(attackFaction, attackTeam);
        this.armFactionInitialCooldowns(defendFaction, defendTeam);
    }

    private void armFactionInitialCooldowns(@Nullable String factionId, @Nullable String team) {
        if (factionId == null || factionId.isBlank() || team == null) {
            return;
        }
        Map<String, VehicleConfig.VehicleTypeConfig> types = VehicleConfig.getFactionVehicles(factionId);
        if (types == null || types.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        Map map = this.cooldowns.computeIfAbsent(VehicleManager.cooldownOwner(team, factionId), k -> new HashMap());
        for (Map.Entry<String, VehicleConfig.VehicleTypeConfig> e : types.entrySet()) {
            int delaySec = e.getValue().initialDeployDelaySeconds(team);
            if (delaySec > 0) {
                map.put(e.getKey(), now + (long)delaySec * 1000L);
                continue;
            }
            map.remove(e.getKey());
        }
        Espetro.LOGGER.info("\u7f16\u5236 {} \u4f5c\u4e3a {} \u7684\u9996\u6b21\u8f7d\u5177\u51b7\u5374\u5df2\u5199\u5165 ({} \u79cd)", new Object[]{factionId, team, types.size()});
    }

    private void startRespawnCooldown(String team, String factionId, String vehicleType) {
        long ms;
        VehicleConfig.VehicleTypeConfig cfg = VehicleConfig.getVehicleConfig(factionId, vehicleType);
        long l = ms = cfg != null ? cfg.respawnMillis() : 0L;
        if (ms <= 0L) {
            Map<String, Long> map = this.cooldowns.get(VehicleManager.cooldownOwner(team, factionId));
            if (map != null) {
                map.remove(vehicleType);
            }
            return;
        }
        this.cooldowns.computeIfAbsent(VehicleManager.cooldownOwner(team, factionId), k -> new HashMap()).put(vehicleType, System.currentTimeMillis() + ms);
    }

    public void onServerTick() {
        this.processAutoRespawns();
        this.processPendingMainBaseStations();
    }

    private void scheduleAutoRespawn(String team, String factionId, String vehicleType) {
        VehicleConfig.VehicleTypeConfig cfg = VehicleConfig.getVehicleConfig(factionId, vehicleType);
        long ms = cfg != null ? cfg.respawnMillis() : 0L;
        long readyAt = System.currentTimeMillis() + Math.max(0L, ms);
        this.autoRespawnQueue.computeIfAbsent(new RespawnKey(team, factionId, vehicleType), ignored -> new PriorityQueue()).add(readyAt);
        this.refreshAutoRespawnCooldown(team, factionId, vehicleType);
        this.broadcastVehicleInfoToTeam(team);
        Espetro.LOGGER.info("\u8f7d\u5177\u5df2\u6467\u6bc1\uff0c\u81ea\u52a8\u5237\u65b0\u5df2\u6392\u961f: {} / {} / {}, delay={}s", new Object[]{team, factionId, vehicleType, Math.max(0L, ms) / 1000L});
    }

    private void processAutoRespawns() {
        if (this.autoRespawnQueue.isEmpty()) {
            return;
        }
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        if (phase != GamePhase.DEPLOYING && phase != GamePhase.BATTLE) {
            return;
        }
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        ServerLevel level = BattlefieldContext.requireBattlefield(server);
        if (level == null || !BattlefieldContext.isActiveBattlefield(level)) {
            return;
        }
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<RespawnKey, PriorityQueue<Long>>> iterator = this.autoRespawnQueue.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<RespawnKey, PriorityQueue<Long>> entry = iterator.next();
            RespawnKey key = entry.getKey();
            PriorityQueue<Long> queue = entry.getValue();
            while (!queue.isEmpty() && queue.peek() <= now && this.tryAutoRespawn(level, key)) {
                queue.poll();
                this.refreshAutoRespawnCooldown(key.team(), key.factionId(), key.vehicleType());
            }
            if (!queue.isEmpty()) continue;
            iterator.remove();
        }
    }

    private boolean tryAutoRespawn(ServerLevel level, RespawnKey key) {
        Entity vehicle;
        String vehicleType;
        String team = key.team();
        String factionId = key.factionId();
        VehicleConfig.VehicleTypeConfig cfg = VehicleConfig.getVehicleConfig(factionId, vehicleType = key.vehicleType());
        if (cfg == null) {
            return false;
        }
        if (this.getActiveCount(team, factionId, vehicleType) >= cfg.max) {
            return false;
        }
        int slotIndex = this.findAvailableSlot(team, factionId, vehicleType, cfg);
        if (slotIndex < 0) {
            return false;
        }
        VehicleConfig.VehicleSlotConfig slot = cfg.slots.isEmpty() ? null : cfg.slots.get(slotIndex);
        VehicleConfig.DeploymentPointConfig deployment = slot != null ? slot.forTeam(team) : this.resolveDeploymentPoint(cfg, team);
        BlockPos spawnPos = this.resolveSpawnPosition(deployment);
        if (spawnPos == null) {
            return false;
        }
        if (!level.m_46805_(spawnPos)) {
            try {
                level.m_46745_(spawnPos);
            }
            catch (RuntimeException e) {
                Espetro.LOGGER.warn("\u8f7d\u5177\u81ea\u52a8\u5237\u65b0\u65e0\u6cd5\u52a0\u8f7d\u51fa\u751f\u533a\u5757: {} / {} / {} at {}", new Object[]{team, factionId, vehicleType, spawnPos, e});
                return false;
            }
        }
        if ((vehicle = this.createVehicleEntity(level, vehicleType, spawnPos, factionId, cfg, slot, deployment != null ? deployment.yaw : 0.0f)) == null) {
            return false;
        }
        if (!level.m_7967_(vehicle)) {
            vehicle.m_146870_();
            return false;
        }
        this.trackVehicle(vehicle, factionId, vehicleType, slotIndex, team, false);
        this.broadcastVehicleInfoToTeam(team);
        Espetro.LOGGER.info("\u8f7d\u5177\u81ea\u52a8\u5237\u65b0: {} / {} / {} at {}", new Object[]{team, factionId, vehicleType, spawnPos});
        return true;
    }

    private void refreshAutoRespawnCooldown(String team, String factionId, String vehicleType) {
        RespawnKey key = new RespawnKey(team, factionId, vehicleType);
        PriorityQueue<Long> queue = this.autoRespawnQueue.get(key);
        Map map = this.cooldowns.computeIfAbsent(VehicleManager.cooldownOwner(team, factionId), ignored -> new HashMap());
        if (queue == null || queue.isEmpty()) {
            map.remove(vehicleType);
        } else {
            map.put(vehicleType, queue.peek());
        }
    }

    private void broadcastVehicleInfoToTeam(String team) {
        MinecraftServer server = Espetro.getServer();
        if (server == null || team == null) {
            return;
        }
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            String factionId;
            if (!team.equals(Espetro.getPlayerTeam(player)) || (factionId = ClassCountManager.getInstance().getPlayerFaction(player.m_20148_())) == null) continue;
            NetworkManager.syncVehicleDeployScreen(player, factionId);
        }
    }

    private static String cooldownOwner(@Nullable String team, String factionId) {
        String normalized = team == null ? "UNKNOWN" : team.trim().toUpperCase(Locale.ROOT);
        return normalized + "|" + factionId;
    }

    @Nullable
    public String deployVehicle(ServerPlayer commander, String vehicleType) {
        if (commander == null) {
            return "\u00a7c\u65e0\u6548\u7684\u90e8\u7f72\u8005\uff01";
        }
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        if (phase != GamePhase.DEPLOYING && phase != GamePhase.BATTLE) {
            return "\u00a7c\u53ea\u80fd\u5728\u90e8\u7f72\u6216\u6218\u6597\u9636\u6bb5\u90e8\u7f72\u8f7d\u5177\uff01";
        }
        if (!BattlefieldContext.isActiveBattlefield(commander.m_284548_())) {
            return "\u00a7c\u4f60\u4e0d\u5728\u5f53\u524d\u6218\u573a\u7ef4\u5ea6\uff01";
        }
        if (!VoteManager.getInstance().isCommander(commander.m_20148_())) {
            return "\u00a7c\u53ea\u6709\u5f53\u524d\u6307\u6325\u5b98\u53ef\u4ee5\u90e8\u7f72\u8f7d\u5177\uff01";
        }
        String factionId = ClassCountManager.getInstance().getPlayerFaction(commander.m_20148_());
        if (factionId == null) {
            return "\u00a7c\u4f60\u6ca1\u6709\u9009\u62e9\u7f16\u5236\uff01";
        }
        String team = Espetro.getPlayerTeam(commander);
        if (team == null) {
            return "\u00a7c\u65e0\u6cd5\u786e\u5b9a\u4f60\u6240\u5728\u7684\u653b\u5b88\u9635\u8425\uff01";
        }
        VehicleConfig.VehicleTypeConfig cfg = VehicleConfig.getVehicleConfig(factionId, vehicleType);
        if (cfg == null) {
            return "\u00a7c\u5f53\u524d\u7f16\u5236\u4e0d\u652f\u6301\u90e8\u7f72\u6b64\u8f7d\u5177\u7c7b\u578b\uff01";
        }
        if (this.hasPendingInitialVehicle(team, factionId, vehicleType)) {
            return "\u00a7e\u8be5\u7c7b\u578b\u9996\u6279\u8f7d\u5177\u6b63\u5728\u81ea\u52a8\u90e8\u7f72\uff0c\u8bf7\u7a0d\u5019\uff01";
        }
        int current = this.getActiveCount(team, factionId, vehicleType);
        if (current >= cfg.max) {
            return "\u00a7c" + VehicleManager.getDisplayName(factionId, vehicleType) + " \u5df2\u8fbe\u5230\u90e8\u7f72\u4e0a\u9650\uff01(" + current + "/" + cfg.max + ")";
        }
        long cooldownRemaining = this.getCooldownRemaining(team, factionId, vehicleType);
        if (cooldownRemaining > 0L) {
            long seconds = cooldownRemaining / 1000L;
            return "\u00a7c" + VehicleManager.getDisplayName(factionId, vehicleType) + " \u5237\u65b0\u51b7\u5374\u4e2d\uff01\u5269\u4f59 " + seconds + " \u79d2\u3002";
        }
        int slotIndex = this.findAvailableSlot(team, factionId, vehicleType, cfg);
        if (slotIndex < 0) {
            return "\u00a7c" + VehicleManager.getDisplayName(factionId, vehicleType) + " \u7684\u6bcf\u4e2a\u8f7d\u5177\u69fd\u4f4d\u5747\u5df2\u8fbe\u5230\u4e0a\u9650\uff01";
        }
        ServerLevel level = this.resolveDeployLevel(commander);
        VehicleConfig.VehicleSlotConfig slot = cfg.slots.isEmpty() ? null : cfg.slots.get(slotIndex);
        VehicleConfig.DeploymentPointConfig deployment = slot != null ? slot.forTeam(team) : this.resolveDeploymentPoint(cfg, team);
        BlockPos spawnPos = this.resolveSpawnPosition(deployment);
        if (spawnPos == null) {
            return "\u00a7c\u8be5\u8f7d\u5177\u672a\u5728\u7f16\u5236 JSON \u4e2d\u914d\u7f6e\u5f53\u524d\u9635\u8425\u7684 deployment." + team + ".position \u5750\u6807\uff01";
        }
        if (!level.m_46805_(spawnPos)) {
            return "\u00a7c\u8f7d\u5177\u90e8\u7f72\u533a\u5757\u5c1a\u672a\u5b8c\u6210\u9884\u8f7d\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5\uff01";
        }
        Entity vehicleEntity = this.createVehicleEntity(level, vehicleType, spawnPos, factionId, cfg, slot, deployment.yaw);
        if (vehicleEntity == null) {
            return "\u00a7c\u521b\u5efa\u8f7d\u5177\u5b9e\u4f53\u5931\u8d25\uff01";
        }
        if (!level.m_7967_(vehicleEntity)) {
            vehicleEntity.m_146870_();
            return "\u00a7c\u8f7d\u5177\u5b9e\u4f53\u672a\u80fd\u52a0\u5165\u6218\u573a\uff01";
        }
        this.trackVehicle(vehicleEntity, factionId, vehicleType, slotIndex, team, false);
        this.startRespawnCooldown(team, factionId, vehicleType);
        commander.m_213846_(Component.m_237113_("\u00a7a\u5df2\u90e8\u7f72 " + VehicleManager.getDisplayName(factionId, vehicleType) + " \u00a7a\uff01(" + (current + 1) + "/" + cfg.max + ") \u00a77\u4f4d\u7f6e: " + spawnPos.m_123341_() + " " + spawnPos.m_123342_() + " " + spawnPos.m_123343_()));
        Espetro.LOGGER.info("\u6307\u6325\u5b98 {} \u90e8\u7f72\u8f7d\u5177: {} (\u961f\u4f0d: {}, \u7f16\u5236: {}, \u4f4d\u7f6e: {})", new Object[]{commander.m_7755_().getString(), vehicleType, team, factionId, spawnPos});
        NetworkManager.syncVehicleDeployScreen(commander, factionId);
        return null;
    }

    public int prepareInitialVehicles(String factionId, String team, ServerLevel level) {
        return this.prepareInitialVehicles(factionId, team, level, System.currentTimeMillis());
    }

    public int prepareInitialVehicles(String factionId, String team, ServerLevel level, long deploymentStartedAtEpochMs) {
        String normalizedTeam;
        if (level == null || factionId == null || factionId.isBlank()) {
            return 0;
        }
        String string = normalizedTeam = team == null ? "" : team.trim().toUpperCase(Locale.ROOT);
        if (!"ATTACK".equals(normalizedTeam) && !"DEFEND".equals(normalizedTeam)) {
            Espetro.LOGGER.warn("\u62d2\u7edd\u51c6\u5907\u672a\u77e5\u9635\u8425\u7684\u521d\u59cb\u8f7d\u5177: faction={}, team={}", (Object)factionId, (Object)team);
            return 0;
        }
        if (this.initialDeploymentLevel != null && this.initialDeploymentLevel != level) {
            Espetro.LOGGER.error("\u62d2\u7edd\u8de8\u7ef4\u5ea6\u6df7\u5408\u521d\u59cb\u8f7d\u5177\u961f\u5217: existing={}, requested={}", (Object)this.initialDeploymentLevel.m_46472_().m_135782_(), (Object)level.m_46472_().m_135782_());
            return 0;
        }
        this.initialDeploymentLevel = level;
        Map<String, VehicleConfig.VehicleTypeConfig> configs = VehicleConfig.getFactionVehicles(factionId);
        if (configs.isEmpty()) {
            return 0;
        }
        int scheduled = 0;
        for (Map.Entry<String, VehicleConfig.VehicleTypeConfig> entry : configs.entrySet()) {
            String vehicleType = entry.getKey();
            VehicleConfig.VehicleTypeConfig cfg = entry.getValue();
            long readyAtEpochMs = VehicleManager.computeInitialReadyAt(deploymentStartedAtEpochMs, cfg.initialDeployDelaySeconds(normalizedTeam));
            int slotCount = cfg.slots.isEmpty() ? 1 : cfg.slots.size();
            for (int slotIndex = 0; slotIndex < slotCount; ++slotIndex) {
                InitialVehicleDeploymentLedger.SlotKey key = new InitialVehicleDeploymentLedger.SlotKey(factionId, vehicleType, slotIndex, normalizedTeam);
                if (!this.initialDeploymentLedger.claim(key)) continue;
                ++scheduled;
                ++this.initialVehiclesPlanned;
                this.initialDeploymentCompletionLogged = false;
                VehicleConfig.VehicleSlotConfig slot = cfg.slots.isEmpty() ? null : cfg.slots.get(slotIndex);
                VehicleConfig.DeploymentPointConfig deployment = slot != null ? slot.forTeam(normalizedTeam) : this.resolveDeploymentPoint(cfg, normalizedTeam);
                BlockPos spawnPos = this.resolveSpawnPosition(deployment);
                if (spawnPos == null) {
                    Espetro.LOGGER.warn("\u521d\u59cb\u8f7d\u5177\u9884\u90e8\u7f72\u5931\u8d25: {} / {} \u69fd\u4f4d{}\u7f3a\u5c11 {} \u5750\u6807", new Object[]{factionId, vehicleType, slotIndex, normalizedTeam});
                    ++this.initialVehiclesFailed;
                    continue;
                }
                PendingInitialVehicle pending = new PendingInitialVehicle(key, cfg, slot, deployment, spawnPos.m_7949_(), readyAtEpochMs);
                this.delayedInitialVehicles.add(pending);
            }
        }
        this.processInitialVehicleDeployments();
        return scheduled;
    }

    public int activateInitialVehicleDeployment() {
        int before = this.initialVehiclesSpawned;
        this.initialDeploymentActive = true;
        for (ChunkPos chunk : new ArrayList<ChunkPos>(this.readyInitialChunks)) {
            this.spawnInitialVehiclesInChunk(chunk);
        }
        this.processInitialVehicleDeployments();
        this.logInitialDeploymentCompletionIfReady();
        return this.initialVehiclesSpawned - before;
    }

    public int deployInitialVehicles(String factionId, String team, ServerLevel level) {
        int scheduled = this.prepareInitialVehicles(factionId, team, level);
        this.activateInitialVehicleDeployment();
        return scheduled;
    }

    public void processInitialVehicleDeployments() {
        ServerLevel level = this.initialDeploymentLevel;
        if (level == null) {
            return;
        }
        if (this.initialDeploymentActive) {
            long now = System.currentTimeMillis();
            while (!this.delayedInitialVehicles.isEmpty() && this.delayedInitialVehicles.peek().readyAtEpochMs() <= now) {
                this.stageDueInitialVehicle(this.delayedInitialVehicles.poll());
            }
        }
        int started = 0;
        while (started < 2 && this.initialChunksInFlight < 4 && !this.pendingInitialChunks.isEmpty()) {
            ChunkPos chunk = this.pendingInitialChunks.poll();
            if (!this.initialVehiclesByChunk.containsKey(chunk)) continue;
            ++started;
            ++this.initialChunksInFlight;
            long generation = this.initialDeploymentGeneration;
            this.ticketedInitialChunks.add(chunk);
            level.m_7726_().m_8387_(TicketType.f_9447_, chunk, 1, chunk.m_45615_());
            level.m_7726_().m_8431_(chunk.f_45578_, chunk.f_45579_, ChunkStatus.f_62326_, true).whenComplete((loaded, error) -> level.m_7654_().execute(() -> {
                boolean available;
                if (generation != this.initialDeploymentGeneration) {
                    return;
                }
                --this.initialChunksInFlight;
                boolean bl = available = error == null && loaded != null && loaded.left().isPresent();
                if (!available) {
                    List<PendingInitialVehicle> failed = this.initialVehiclesByChunk.remove(chunk);
                    this.readyInitialChunks.remove(chunk);
                    int failedCount = failed == null ? 0 : failed.size();
                    this.initialVehiclesFailed += failedCount;
                    Espetro.LOGGER.error("\u521d\u59cb\u8f7d\u5177\u51fa\u751f\u533a\u5757\u52a0\u8f7d\u5931\u8d25: chunk={}, vehicles={}, reason={}", new Object[]{chunk, failedCount, error == null ? "\u533a\u5757 future \u672a\u8fd4\u56de FULL \u533a\u5757" : error.getMessage()});
                    this.releaseInitialChunkTicket(level, chunk);
                } else if (this.initialDeploymentActive) {
                    this.spawnInitialVehiclesInChunk(chunk);
                } else {
                    this.readyInitialChunks.add(chunk);
                }
                this.processInitialVehicleDeployments();
                this.logInitialDeploymentCompletionIfReady();
            }));
        }
        this.logInitialDeploymentCompletionIfReady();
    }

    public InitialDeploymentStatus getInitialDeploymentStatus() {
        int pending = this.delayedInitialVehicles.size() + this.initialVehiclesByChunk.values().stream().mapToInt(List::size).sum();
        return new InitialDeploymentStatus(this.initialVehiclesPlanned, this.initialVehiclesSpawned, this.initialVehiclesFailed, pending);
    }

    public boolean isInitialVehicleDeploymentSettled() {
        return this.initialDeploymentActive && this.delayedInitialVehicles.isEmpty() && this.pendingInitialChunks.isEmpty() && this.initialChunksInFlight == 0 && this.initialVehiclesByChunk.isEmpty();
    }

    private void spawnInitialVehiclesInChunk(ChunkPos chunk) {
        ServerLevel level = this.initialDeploymentLevel;
        if (level == null) {
            return;
        }
        List<PendingInitialVehicle> vehicles = this.initialVehiclesByChunk.remove(chunk);
        this.readyInitialChunks.remove(chunk);
        if (vehicles == null) {
            this.releaseInitialChunkTicket(level, chunk);
            return;
        }
        LinkedHashMap<CallSite, ActiveVehicleData> panelSyncs = new LinkedHashMap<CallSite, ActiveVehicleData>();
        for (PendingInitialVehicle pending : vehicles) {
            ActiveVehicleData tracked;
            InitialVehicleDeploymentLedger.SlotKey key = pending.key();
            Entity vehicleEntity = this.createVehicleEntity(level, key.vehicleType(), pending.spawnPosition(), key.factionId(), pending.config(), pending.slot(), pending.deployment().yaw);
            if (vehicleEntity == null) {
                ++this.initialVehiclesFailed;
                Espetro.LOGGER.warn("\u521d\u59cb\u8f7d\u5177\u9884\u90e8\u7f72\u5931\u8d25: \u65e0\u6cd5\u521b\u5efa {} / {} \u69fd\u4f4d{}\u7684\u5b9e\u4f53", new Object[]{key.factionId(), key.vehicleType(), key.slotIndex()});
                continue;
            }
            if (!level.m_7967_(vehicleEntity)) {
                vehicleEntity.m_146870_();
                ++this.initialVehiclesFailed;
                Espetro.LOGGER.warn("\u521d\u59cb\u8f7d\u5177\u9884\u90e8\u7f72\u5931\u8d25: {} / {} \u69fd\u4f4d{}\u672a\u80fd\u52a0\u5165\u6218\u573a", new Object[]{key.factionId(), key.vehicleType(), key.slotIndex()});
                continue;
            }
            this.trackVehicle(vehicleEntity, key.factionId(), key.vehicleType(), key.slotIndex(), key.team(), true);
            Map<String, Long> initialCooldown = this.cooldowns.get(VehicleManager.cooldownOwner(key.team(), key.factionId()));
            if (initialCooldown != null) {
                initialCooldown.remove(key.vehicleType());
            }
            if ((tracked = this.activeVehicleData.get(vehicleEntity.m_20148_())) != null) {
                panelSyncs.put((CallSite)((Object)(tracked.team() + "|" + tracked.factionId())), tracked);
            }
            ++this.initialVehiclesSpawned;
            this.broadcastVehicleInfoToTeam(key.team());
        }
        panelSyncs.values().forEach(VehicleManager::syncCommanderVehiclePanel);
        this.releaseInitialChunkTicket(level, chunk);
    }

    private boolean hasPendingInitialVehicle(String team, String factionId, String vehicleType) {
        String normalizedTeam = team == null ? "" : team.trim().toUpperCase(Locale.ROOT);
        for (PendingInitialVehicle pendingInitialVehicle : this.delayedInitialVehicles) {
            if (!VehicleManager.matchesInitialType(pendingInitialVehicle, normalizedTeam, factionId, vehicleType)) continue;
            return true;
        }
        for (List list : this.initialVehiclesByChunk.values()) {
            for (PendingInitialVehicle pending : list) {
                if (!VehicleManager.matchesInitialType(pending, normalizedTeam, factionId, vehicleType)) continue;
                return true;
            }
        }
        return false;
    }

    private static boolean matchesInitialType(PendingInitialVehicle pending, String team, String factionId, String vehicleType) {
        InitialVehicleDeploymentLedger.SlotKey key = pending.key();
        return key.team().equals(team) && key.factionId().equals(factionId) && key.vehicleType().equals(vehicleType);
    }

    private void stageDueInitialVehicle(PendingInitialVehicle pending) {
        ChunkPos chunk = new ChunkPos(pending.spawnPosition());
        List<PendingInitialVehicle> chunkVehicles = this.initialVehiclesByChunk.get(chunk);
        if (chunkVehicles == null) {
            chunkVehicles = new ArrayList<PendingInitialVehicle>();
            this.initialVehiclesByChunk.put(chunk, chunkVehicles);
            this.pendingInitialChunks.add(chunk);
        }
        chunkVehicles.add(pending);
    }

    static long computeInitialReadyAt(long deploymentStartedAtEpochMs, int delaySeconds) {
        long safeStart = Math.max(0L, deploymentStartedAtEpochMs);
        long delayMillis = Math.max(0L, (long)delaySeconds) * 1000L + 5000L;
        return delayMillis > Long.MAX_VALUE - safeStart ? Long.MAX_VALUE : safeStart + delayMillis;
    }

    private void releaseInitialChunkTicket(ServerLevel level, ChunkPos chunk) {
        if (this.ticketedInitialChunks.remove(chunk)) {
            level.m_7726_().m_8438_(TicketType.f_9447_, chunk, 1, chunk.m_45615_());
        }
    }

    private void logInitialDeploymentCompletionIfReady() {
        if (!this.initialDeploymentActive || this.initialDeploymentCompletionLogged || !this.isInitialVehicleDeploymentSettled()) {
            return;
        }
        this.initialDeploymentCompletionLogged = true;
        Espetro.LOGGER.info("\u7f16\u5236\u9996\u6279\u8f7d\u5177\u90e8\u7f72\u5b8c\u6210: planned={}, spawned={}, failed={}", new Object[]{this.initialVehiclesPlanned, this.initialVehiclesSpawned, this.initialVehiclesFailed});
    }

    public void onVehicleDeath(UUID entityId) {
        ActiveVehicleData data = this.removeTrackedVehicle(entityId);
        if (data != null) {
            this.applyVehicleTroopPenalty(data);
            this.scheduleAutoRespawn(data.team(), data.factionId(), data.vehicleType());
            this.broadcastVehicleInfoToTeam(data.team());
        }
    }

    public void onVehicleRemoved(UUID entityId) {
        ActiveVehicleData data = this.removeTrackedVehicle(entityId);
        if (data != null) {
            this.broadcastVehicleInfoToTeam(data.team());
        }
    }

    private static void syncCommanderVehiclePanel(ActiveVehicleData data) {
        ServerPlayer commander;
        MinecraftServer server = Espetro.getServer();
        if (server == null || data == null) {
            return;
        }
        UUID commanderId = "ATTACK".equals(data.team()) ? VoteManager.getInstance().getAttackCommander() : VoteManager.getInstance().getDefendCommander();
        ServerPlayer serverPlayer = commander = commanderId == null ? null : server.m_6846_().m_11259_(commanderId);
        if (commander != null) {
            NetworkManager.syncVehicleDeployScreen(commander, data.factionId());
        }
    }

    @Nullable
    private ActiveVehicleData removeTrackedVehicle(UUID entityId) {
        this.activeVehicleIds.remove(entityId);
        this.vehicleSupplies.remove(entityId);
        ActiveVehicleData data = this.activeVehicleData.remove(entityId);
        if (data != null) {
            List<UUID> vehicles = this.findList(data.factionId(), data.vehicleType());
            if (vehicles != null) {
                vehicles.remove(entityId);
            }
            Espetro.LOGGER.debug("\u8f7d\u5177 {} \u5df2\u79fb\u9664\u8ffd\u8e2a", (Object)entityId);
            return data;
        }
        for (Map.Entry<String, Map<String, List<UUID>>> factionEntry : this.activeVehicles.entrySet()) {
            for (Map.Entry<String, List<UUID>> typeEntry : factionEntry.getValue().entrySet()) {
                if (!typeEntry.getValue().remove(entityId)) continue;
                Espetro.LOGGER.debug("\u8f7d\u5177 {} \u5df2\u79fb\u9664\u8ffd\u8e2a", (Object)entityId);
                return null;
            }
        }
        return null;
    }

    private void trackVehicle(Entity vehicle, String factionId, String vehicleType, int slotIndex, String team, boolean initial) {
        VehicleConfig.VehicleTypeConfig vcfg;
        String normalizedTeam;
        UUID vehicleId = vehicle.m_20148_();
        String string = normalizedTeam = team == null ? "" : team.trim().toUpperCase(Locale.ROOT);
        if (!normalizedTeam.isBlank()) {
            vehicle.m_20049_("espetro_team_" + normalizedTeam);
            vehicle.getPersistentData().m_128359_(VEHICLE_TEAM_KEY, normalizedTeam);
        }
        if ((vcfg = VehicleConfig.getVehicleConfig(factionId, vehicleType)) != null && vcfg.supplyVeh) {
            vehicle.m_20049_("espetro_supply_veh");
        }
        if (vcfg != null && vcfg.fightVeh) {
            vehicle.m_20049_("espetro_fight_veh");
        }
        this.getList(factionId, vehicleType).add(vehicleId);
        this.activeVehicleIds.add(vehicleId);
        this.activeVehicleData.put(vehicleId, new ActiveVehicleData(factionId, vehicleType, slotIndex, normalizedTeam, initial, vehicle.m_9236_().m_46472_(), vehicle.m_20183_().m_7949_()));
        if (vcfg != null && vcfg.supplyCapacity > 0) {
            VehicleSupplyState supply = new VehicleSupplyState(vcfg.supplyCapacity, vcfg.canCarryConstruction());
            if (vcfg.supplyVeh) {
                supply.fillHalf();
            } else {
                supply.fillAmmo();
            }
            this.vehicleSupplies.put(vehicleId, supply);
        }
    }

    public void updateVehicleLocation(Entity entity) {
        if (entity == null) {
            return;
        }
        ActiveVehicleData data = this.activeVehicleData.get(entity.m_20148_());
        if (data != null) {
            this.activeVehicleData.put(entity.m_20148_(), data.withLocation(entity));
        }
    }

    private void applyVehicleTroopPenalty(ActiveVehicleData data) {
        int penalty;
        if (GameStateManager.getInstance().getCurrentPhase() != GamePhase.BATTLE) {
            return;
        }
        VehicleConfig.VehicleTypeConfig cfg = VehicleConfig.getVehicleConfig(data.factionId(), data.vehicleType());
        int n = penalty = cfg != null ? Math.max(0, cfg.troopValue) : 0;
        if (penalty <= 0) {
            return;
        }
        TroopCountManager troopManager = TroopCountManager.getInstance();
        String displayName = VehicleManager.getDisplayName(data.factionId(), data.vehicleType());
        if ("ATTACK".equals(data.team())) {
            troopManager.modifyAttackTroops(-penalty);
            Espetro.broadcastToTeam(data.team(), "\u00a7c\u2620 \u653b\u65b9\u8f7d\u5177 [" + displayName + "] \u88ab\u6467\u6bc1\uff01- " + penalty + " \u5175\u529b");
            Espetro.LOGGER.info("\u653b\u65b9\u8f7d\u5177 {} \u88ab\u6467\u6bc1\uff0c\u6263\u9664 {} \u5175\u529b\uff0c\u5269\u4f59: {}", new Object[]{displayName, penalty, troopManager.getAttackTroops()});
        } else if ("DEFEND".equals(data.team())) {
            troopManager.modifyDefendTroops(-penalty);
            Espetro.broadcastToTeam(data.team(), "\u00a79\u2620 \u5b88\u65b9\u8f7d\u5177 [" + displayName + "] \u88ab\u6467\u6bc1\uff01- " + penalty + " \u5175\u529b");
            Espetro.LOGGER.info("\u5b88\u65b9\u8f7d\u5177 {} \u88ab\u6467\u6bc1\uff0c\u6263\u9664 {} \u5175\u529b\uff0c\u5269\u4f59: {}", new Object[]{displayName, penalty, troopManager.getDefendTroops()});
        }
        troopManager.checkVictoryCondition();
    }

    public boolean isTrackedVehicle(UUID entityId) {
        return this.activeVehicleIds.contains(entityId);
    }

    @Nullable
    public String getTrackedVehicleTeam(UUID entityId) {
        ActiveVehicleData data = this.activeVehicleData.get(entityId);
        return data == null || data.team() == null || data.team().isBlank() ? null : data.team();
    }

    public int getInitialTroopValueForTeam(String team) {
        int total = 0;
        for (ActiveVehicleData data : this.activeVehicleData.values()) {
            VehicleConfig.VehicleTypeConfig cfg;
            if (!data.initial() || !team.equals(data.team()) || (cfg = VehicleConfig.getVehicleConfig(data.factionId(), data.vehicleType())) == null) continue;
            total += cfg.troopValue;
        }
        return total;
    }

    @Nullable
    public VehicleSupplyState getVehicleSupply(UUID entityId) {
        return this.vehicleSupplies.get(entityId);
    }

    @Nullable
    public VehicleSupplyState getOrCreateVehicleSupply(UUID entityId, String factionId, String vehicleType) {
        VehicleSupplyState existing = this.vehicleSupplies.get(entityId);
        if (existing != null) {
            return existing;
        }
        VehicleConfig.VehicleTypeConfig vcfg = VehicleConfig.getVehicleConfig(factionId, vehicleType);
        if (vcfg == null || vcfg.supplyCapacity <= 0) {
            return null;
        }
        VehicleSupplyState supply = new VehicleSupplyState(vcfg.supplyCapacity, vcfg.canCarryConstruction());
        if (vcfg.supplyVeh) {
            supply.fillHalf();
        } else {
            supply.fillAmmo();
        }
        this.vehicleSupplies.put(entityId, supply);
        return supply;
    }

    public int loadAmmoToVehicle(UUID entityId, int amount) {
        VehicleSupplyState supply = this.vehicleSupplies.get(entityId);
        return supply != null ? supply.addAmmo(amount) : 0;
    }

    public int unloadAmmoFromVehicle(UUID entityId, int amount) {
        VehicleSupplyState supply = this.vehicleSupplies.get(entityId);
        return supply != null ? supply.removeAmmo(amount) : 0;
    }

    public int loadConstructionToVehicle(UUID entityId, int amount) {
        VehicleSupplyState supply = this.vehicleSupplies.get(entityId);
        return supply != null ? supply.addConstruction(amount) : 0;
    }

    public int unloadConstructionFromVehicle(UUID entityId, int amount) {
        VehicleSupplyState supply = this.vehicleSupplies.get(entityId);
        return supply != null ? supply.removeConstruction(amount) : 0;
    }

    public boolean canVehicleAffordAmmo(UUID entityId, int amount) {
        VehicleSupplyState supply = this.vehicleSupplies.get(entityId);
        return supply != null && supply.canAffordAmmo(amount);
    }

    public boolean consumeVehicleAmmo(UUID entityId, int amount) {
        VehicleSupplyState supply = this.vehicleSupplies.get(entityId);
        if (supply == null || !supply.canAffordAmmo(amount)) {
            return false;
        }
        supply.removeAmmo(amount);
        return true;
    }

    public boolean isVehicleSupplyCapable(UUID entityId) {
        return this.vehicleSupplies.containsKey(entityId);
    }

    @Nullable
    public String getVehicleFactionId(UUID entityId) {
        ActiveVehicleData data = this.activeVehicleData.get(entityId);
        return data != null ? data.factionId() : null;
    }

    @Nullable
    public String getVehicleType(UUID entityId) {
        ActiveVehicleData data = this.activeVehicleData.get(entityId);
        return data != null ? data.vehicleType() : null;
    }

    @Nullable
    public String getVehicleTeam(UUID entityId) {
        ActiveVehicleData data = this.activeVehicleData.get(entityId);
        return data != null ? data.team() : null;
    }

    @Nullable
    public BlockPos getVehicleLastPosition(UUID entityId) {
        ActiveVehicleData data = this.activeVehicleData.get(entityId);
        return data != null ? data.lastKnownPosition() : null;
    }

    public boolean canPlayerInteractWithVehicle(ServerPlayer player, UUID vehicleId) {
        Vec3 look;
        Vec3 end;
        if (player == null || vehicleId == null) {
            return false;
        }
        ActiveVehicleData data = this.activeVehicleData.get(vehicleId);
        if (data == null) {
            return false;
        }
        String playerTeam = Espetro.getPlayerTeam(player);
        if (playerTeam == null || data.team() == null || !playerTeam.equalsIgnoreCase(data.team())) {
            return false;
        }
        Entity target = player.m_284548_().m_8791_(vehicleId);
        if (target == null || target.m_213877_()) {
            return false;
        }
        double range = 5.0;
        Vec3 eye = player.m_20299_(1.0f);
        EntityHitResult hit = ProjectileUtil.m_37287_(player, eye, end = eye.m_82549_((look = player.m_20154_()).m_82490_(range)), player.m_20191_().m_82369_(look.m_82490_(range)).m_82400_(1.0), candidate -> candidate.m_6087_() && (vehicleId.equals(candidate.m_20148_()) || vehicleId.equals(candidate.m_20201_().m_20148_())), range * range);
        return hit != null && (vehicleId.equals(hit.m_82443_().m_20148_()) || vehicleId.equals(hit.m_82443_().m_20201_().m_20148_()));
    }

    public boolean canPlayerChangeClassAtVehicle(ServerPlayer player, UUID vehicleId) {
        if (!this.canPlayerInteractWithVehicle(player, vehicleId)) {
            return false;
        }
        ActiveVehicleData data = this.activeVehicleData.get(vehicleId);
        if (data == null) {
            return false;
        }
        VehicleConfig.VehicleTypeConfig config = VehicleConfig.getVehicleConfig(data.factionId(), data.vehicleType());
        return config != null && config.canChangeClass();
    }

    @Nullable
    public Entity getLoadedVehicle(ServerPlayer player, UUID vehicleId) {
        if (player == null || vehicleId == null) {
            return null;
        }
        Entity entity = player.m_284548_().m_8791_(vehicleId);
        return entity != null && !entity.m_213877_() ? entity : null;
    }

    public void reset() {
        this.removeAllDeployedVehicles(Espetro.getServer());
    }

    public int removeAllDeployedVehicles(@Nullable MinecraftServer server) {
        int removedCount = 0;
        if (server != null) {
            HashSet<UUID> trackedEntities = new HashSet<UUID>(this.activeVehicleIds);
            trackedEntities.addAll(this.mappedSupplyStations.keySet());
            for (UUID id : trackedEntities) {
                Entity entity = this.findEntity(server, id);
                if (entity == null || entity.m_213877_()) continue;
                entity.m_146870_();
                ++removedCount;
            }
        }
        this.cancelInitialVehicleDeployment(true);
        this.clearRuntimeCollections();
        return removedCount;
    }

    public void clearRuntimeState() {
        this.cancelInitialVehicleDeployment(false);
        this.clearRuntimeCollections();
    }

    private void clearRuntimeCollections() {
        this.activeVehicles.clear();
        this.activeVehicleIds.clear();
        this.activeVehicleData.clear();
        this.vehicleSupplies.clear();
        this.cooldowns.clear();
        this.autoRespawnQueue.clear();
        if (!this.mappedSupplyStations.isEmpty()) {
            this.mappedSupplyStations.clear();
            EspetroAPI.markTacticalMapStateDirty();
        }
    }

    private void cancelInitialVehicleDeployment(boolean releaseTickets) {
        ServerLevel level = this.initialDeploymentLevel;
        ++this.initialDeploymentGeneration;
        if (releaseTickets && level != null) {
            for (ChunkPos chunk : new ArrayList<ChunkPos>(this.ticketedInitialChunks)) {
                try {
                    this.releaseInitialChunkTicket(level, chunk);
                }
                catch (Exception e) {
                    Espetro.LOGGER.debug("\u53d6\u6d88\u521d\u59cb\u8f7d\u5177\u533a\u5757 ticket \u5931\u8d25: {} ({})", (Object)chunk, (Object)e.getMessage());
                }
            }
        }
        this.initialDeploymentLedger.clear();
        this.delayedInitialVehicles.clear();
        this.initialVehiclesByChunk.clear();
        this.pendingInitialChunks.clear();
        this.readyInitialChunks.clear();
        this.ticketedInitialChunks.clear();
        this.initialDeploymentLevel = null;
        this.pendingMainBaseStationLevel = null;
        this.initialChunksInFlight = 0;
        this.initialDeploymentActive = false;
        this.initialDeploymentCompletionLogged = false;
        this.initialVehiclesPlanned = 0;
        this.initialVehiclesSpawned = 0;
        this.initialVehiclesFailed = 0;
    }

    public void registerMappedSupplyStation(@Nullable Entity entity) {
        if (!VehicleManager.isMappedSupplyStation(entity) || entity == null) {
            return;
        }
        CompoundTag data = entity.getPersistentData();
        String team = data.m_128461_(SUPPLY_STATION_TEAM_KEY);
        if (team == null || team.isBlank()) {
            return;
        }
        team = team.trim().toUpperCase(Locale.ROOT);
        BlockPos pos = entity.m_20183_();
        String name = entity.m_7770_() == null ? SUPPLY_STATION_DISPLAY_NAME : entity.m_7770_().getString();
        SupplyStationSnapshot snapshot = new SupplyStationSnapshot(entity.m_20148_(), name, team, entity.m_9236_().m_46472_().m_135782_().toString(), pos.m_123341_(), pos.m_123342_(), pos.m_123343_());
        if (!snapshot.equals(this.mappedSupplyStations.get(entity.m_20148_()))) {
            this.mappedSupplyStations.put(entity.m_20148_(), snapshot);
            EspetroAPI.markTacticalMapStateDirty();
        }
    }

    public void registerMappedSupplyStation(UUID id, String name, String team, String dimension, BlockPos pos) {
        if (id == null || team == null || team.isBlank() || dimension == null || pos == null) {
            return;
        }
        SupplyStationSnapshot snapshot = new SupplyStationSnapshot(id, name == null || name.isBlank() ? SUPPLY_STATION_DISPLAY_NAME : name, team.trim().toUpperCase(Locale.ROOT), dimension, pos.m_123341_(), pos.m_123342_(), pos.m_123343_());
        if (!snapshot.equals(this.mappedSupplyStations.get(id))) {
            this.mappedSupplyStations.put(id, snapshot);
            EspetroAPI.markTacticalMapStateDirty();
        }
    }

    public void unregisterMappedSupplyStation(UUID entityId) {
        if (entityId != null && this.mappedSupplyStations.remove(entityId) != null) {
            EspetroAPI.markTacticalMapStateDirty();
        }
    }

    public List<SupplyStationSnapshot> getMappedSupplyStationSnapshots() {
        return List.copyOf(this.mappedSupplyStations.values());
    }

    public void removeInvalidVehicles() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        for (UUID id : new ArrayList<UUID>(this.activeVehicleIds)) {
            Entity entity;
            ActiveVehicleData data = this.activeVehicleData.get(id);
            if (data == null) {
                this.removeTrackedVehicle(id);
                continue;
            }
            ServerLevel level = server.m_129880_(data.dimension());
            if (level == null || !level.m_46805_(data.lastKnownPosition()) || (entity = level.m_8791_(id)) == null) continue;
            if (entity.m_213877_()) {
                this.removeTrackedVehicle(id);
                continue;
            }
            this.updateVehicleLocation(entity);
        }
    }

    public List<String> getFactionVehicleStatus(String factionId) {
        ArrayList<String> result = new ArrayList<String>();
        Map<String, VehicleConfig.VehicleTypeConfig> configs = VehicleConfig.getFactionVehicles(factionId);
        for (Map.Entry<String, VehicleConfig.VehicleTypeConfig> entry : configs.entrySet()) {
            String type = entry.getKey();
            VehicleConfig.VehicleTypeConfig cfg = entry.getValue();
            int count = this.getActiveCount(factionId, type);
            long cooldown = this.getCooldownRemaining(factionId, type);
            String cooldownStr = cooldown > 0L ? " \u00a77(\u51b7\u5374 " + cooldown / 1000L + "s)" : " \u00a7a\u2713";
            result.add(VehicleManager.getDisplayName(factionId, type) + ": " + count + "/" + cfg.max + cooldownStr);
        }
        return result;
    }

    private ServerLevel resolveDeployLevel(ServerPlayer commander) {
        return BattlefieldContext.requireBattlefield(commander.f_8924_);
    }

    private int findAvailableSlot(String team, String factionId, String vehicleType, VehicleConfig.VehicleTypeConfig cfg) {
        int slotCount = cfg.slots.isEmpty() ? 1 : cfg.slots.size();
        for (int i = 0; i < slotCount; ++i) {
            int count = 0;
            for (ActiveVehicleData data : this.activeVehicleData.values()) {
                if (!factionId.equals(data.factionId()) || !vehicleType.equals(data.vehicleType()) || !team.equalsIgnoreCase(data.team()) || data.slotIndex() != i) continue;
                ++count;
            }
            if (count >= cfg.perMaxCount) continue;
            return i;
        }
        return -1;
    }

    @Nullable
    private VehicleConfig.DeploymentPointConfig resolveDeploymentPoint(VehicleConfig.VehicleTypeConfig cfg, String team) {
        return cfg.deployment.forTeam(team);
    }

    @Nullable
    private BlockPos resolveSpawnPosition(@Nullable VehicleConfig.DeploymentPointConfig deployment) {
        if (deployment == null) {
            return null;
        }
        int[] position = deployment.position;
        if (position == null || position.length < 3) {
            return null;
        }
        return new BlockPos(position[0], position[1], position[2]);
    }

    @Nullable
    private Entity createVehicleEntity(ServerLevel level, String vehicleType, BlockPos pos, String factionId, VehicleConfig.VehicleTypeConfig config, @Nullable VehicleConfig.VehicleSlotConfig slot, float yaw) {
        EntityType<?> entityTypeObj;
        EntityType<?> entityType = entityTypeObj = slot != null ? slot.getEntityType() : config.getEntityType();
        if (entityTypeObj == null) {
            Espetro.LOGGER.warn("\u8f7d\u5177 {} \u672a\u914d\u7f6e entity_type \u6216\u6ce8\u518c\u540d\u65e0\u6548", (Object)vehicleType);
            return null;
        }
        Object entity = entityTypeObj.m_20615_(level);
        if (entity == null) {
            return null;
        }
        double x = (double)pos.m_123341_() + 0.5;
        double y = pos.m_123342_();
        double z = (double)pos.m_123343_() + 0.5;
        String name = config.displayName != null ? config.displayName : vehicleType;
        ((Entity)entity).m_6593_(Component.m_237113_(name));
        ((Entity)entity).m_20340_(false);
        ((Entity)entity).m_6034_(x, y, z);
        ((Entity)entity).m_146922_(yaw);
        ((Entity)entity).m_5616_(yaw);
        ((Entity)entity).m_20049_(VEHICLE_TAG);
        ((Entity)entity).m_20049_("espetro_" + vehicleType);
        ((Entity)entity).m_20049_("espetro_vehicle_type_" + vehicleType);
        for (String tag : config.entityTags) {
            ((Entity)entity).m_20049_(tag);
        }
        String spawnNbt = slot != null && slot.nbt != null && !slot.nbt.isBlank() ? slot.nbt : config.nbt;
        this.applyVehicleSpawnNbt((Entity)entity, spawnNbt);
        return entity;
    }

    private void applyVehicleSpawnNbt(Entity entity, @Nullable String snbt) {
        int energyAmount = Integer.MAX_VALUE;
        CompoundTag extras = null;
        if (snbt != null && !snbt.isBlank()) {
            try {
                extras = TagParser.m_129359_(snbt);
            }
            catch (Exception e) {
                Espetro.LOGGER.warn("\u8f7d\u5177\u90e8\u7f72 NBT \u65e0\u6548\uff0c\u5c06\u4ec5\u5c1d\u8bd5\u6ee1\u7535: {} ({})", (Object)snbt, (Object)e.getMessage());
            }
        }
        if (extras != null && extras.m_128425_("Energy", 3)) {
            energyAmount = extras.m_128451_("Energy");
            extras.m_128473_("Energy");
        }
        this.fillVehicleEnergy(entity, energyAmount);
        if (extras != null && !extras.m_128456_()) {
            extras.m_128473_("UUID");
            if (!extras.m_128456_()) {
                Espetro.LOGGER.warn("\u5ffd\u7565\u8f7d\u5177\u90e8\u7f72\u4e2d\u7684\u975e Energy NBT \u952e\uff08\u907f\u514d SW \u6b8b\u7f3a load \u6e05\u8840\uff09 type={} keys={}", entity.m_6095_(), extras.m_128431_());
            }
        }
    }

    private void fillVehicleEnergy(Entity entity, int amount) {
        if (amount <= 0) {
            return;
        }
        try {
            entity.getCapability(ForgeCapabilities.ENERGY).ifPresent(storage -> {
                int received;
                int room;
                if (!storage.canReceive()) {
                    return;
                }
                int guard = 0;
                while (storage.getEnergyStored() < storage.getMaxEnergyStored() && guard++ < 64 && (room = storage.getMaxEnergyStored() - storage.getEnergyStored()) > 0 && (received = storage.receiveEnergy(Math.min(amount, room), false)) > 0) {
                }
            });
        }
        catch (Exception e) {
            Espetro.LOGGER.warn("\u8f7d\u5177\u6ee1\u7535\u5931\u8d25 (type={}): {}", entity.m_6095_(), (Object)e.getMessage());
        }
    }

    public int spawnPadSupplyStations(ServerLevel level, @Nullable VehSpawnSnapshot spawn) {
        if (level == null || spawn == null || !spawn.isValid()) {
            return 0;
        }
        this.clearPadSupplyStations(level);
        EntityType stationType = BuiltInRegistries.f_256780_.m_6612_(SUPPLY_STATION_ID).orElse(null);
        if (stationType == null) {
            Espetro.LOGGER.warn("\u672a\u6ce8\u518c\u5b9e\u4f53 {}\uff0c\u8df3\u8fc7\u8f7d\u5177\u5751\u8865\u7ed9\u7ad9\u9884\u653e", (Object)SUPPLY_STATION_ID);
            return 0;
        }
        int spawned = 0;
        for (Map.Entry<String, List<VehSpawnSnapshot.SpawnPoint>> entry : spawn.spawnPointsByType.entrySet()) {
            String type = entry.getKey();
            List<VehSpawnSnapshot.SpawnPoint> points = entry.getValue();
            if (points == null) continue;
            for (VehSpawnSnapshot.SpawnPoint point : points) {
                if (point == null) continue;
                if (this.spawnOnePadSupply(level, stationType, point.attack(), "ATTACK", type, point.id())) {
                    ++spawned;
                }
                if (!this.spawnOnePadSupply(level, stationType, point.defend(), "DEFEND", type, point.id())) continue;
                ++spawned;
            }
        }
        Espetro.LOGGER.info("\u8f7d\u5177\u5751\u8865\u7ed9\u7ad9\u9884\u653e\u5b8c\u6210: {} \u4e2a (\u7ef4\u5ea6 {})", (Object)spawned, (Object)level.m_46472_().m_135782_());
        return spawned;
    }

    public int clearPadSupplyStations(@Nullable ServerLevel level) {
        if (level == null) {
            return 0;
        }
        String dimension = level.m_46472_().m_135782_().toString();
        int removed = 0;
        boolean mappedStationsChanged = false;
        for (SupplyStationSnapshot snapshot : new ArrayList<SupplyStationSnapshot>(this.mappedSupplyStations.values())) {
            if (!dimension.equals(snapshot.dimension())) continue;
            Entity entity = level.m_8791_(snapshot.id());
            if (entity != null && !entity.m_213877_() && VehicleManager.isPadSupplyStation(entity)) {
                entity.m_146870_();
                ++removed;
            }
            mappedStationsChanged |= this.mappedSupplyStations.remove(snapshot.id()) != null;
        }
        if (mappedStationsChanged) {
            EspetroAPI.markTacticalMapStateDirty();
        }
        return removed;
    }

    public void scheduleMainBaseSupplyStations(ServerLevel level) {
        this.pendingMainBaseStationLevel = level;
        this.pendingMainBaseStationAtEpochMs = System.currentTimeMillis() + 5000L;
    }

    private void processPendingMainBaseStations() {
        ServerLevel level = this.pendingMainBaseStationLevel;
        if (level == null) {
            return;
        }
        if (System.currentTimeMillis() < this.pendingMainBaseStationAtEpochMs) {
            return;
        }
        this.pendingMainBaseStationLevel = null;
        try {
            this.spawnMainBaseSupplyStations(level);
        }
        catch (Exception e) {
            Espetro.LOGGER.error("\u5ef6\u8fdf\u751f\u6210\u4e3b\u91cd\u751f\u70b9\u5f39\u836f\u8865\u7ed9\u7ad9\u5931\u8d25", (Throwable)e);
        }
    }

    public int spawnMainBaseSupplyStations(ServerLevel level) {
        if (level == null) {
            return 0;
        }
        this.clearMainBaseSupplyStations(level);
        EntityType stationType = BuiltInRegistries.f_256780_.m_6612_(SUPPLY_STATION_ID).orElse(null);
        if (stationType == null) {
            Espetro.LOGGER.warn("\u672a\u6ce8\u518c\u5b9e\u4f53 {}\uff0c\u8df3\u8fc7\u4e3b\u91cd\u751f\u70b9\u8865\u7ed9\u7ad9\u751f\u6210", (Object)SUPPLY_STATION_ID);
            return 0;
        }
        int spawned = 0;
        for (String team : new String[]{"ATTACK", "DEFEND"}) {
            SpawnPointConfig.SpawnPoint spawn = SpawnPointConfig.getSpawnPoint(team);
            if (spawn == null || !this.spawnOneMainBaseSupply(level, stationType, spawn, team)) continue;
            ++spawned;
        }
        Espetro.LOGGER.info("\u4e3b\u91cd\u751f\u70b9\u5f39\u836f\u8865\u7ed9\u7ad9\u751f\u6210\u5b8c\u6210: {} \u4e2a (\u7ef4\u5ea6 {})", (Object)spawned, (Object)level.m_46472_().m_135782_());
        return spawned;
    }

    public int clearMainBaseSupplyStations(@Nullable ServerLevel level) {
        if (level == null) {
            return 0;
        }
        String dimension = level.m_46472_().m_135782_().toString();
        int removed = 0;
        boolean mappedStationsChanged = false;
        for (SupplyStationSnapshot snapshot : new ArrayList<SupplyStationSnapshot>(this.mappedSupplyStations.values())) {
            Entity entity;
            if (!dimension.equals(snapshot.dimension()) || (entity = level.m_8791_(snapshot.id())) == null || entity.m_213877_() || !entity.m_19880_().contains(MAIN_BASE_SUPPLY_TAG)) continue;
            entity.m_146870_();
            ++removed;
            mappedStationsChanged |= this.mappedSupplyStations.remove(snapshot.id()) != null;
        }
        if (mappedStationsChanged) {
            EspetroAPI.markTacticalMapStateDirty();
        }
        return removed;
    }

    private boolean spawnOneMainBaseSupply(ServerLevel level, EntityType<?> stationType, SpawnPointConfig.SpawnPoint spawn, String team) {
        BlockPos stationPos = VehicleManager.getMainBaseSupplyPosition(spawn);
        if (!level.m_46805_(stationPos)) {
            Espetro.LOGGER.warn("\u4e3b\u91cd\u751f\u70b9\u8865\u7ed9\u7ad9\u533a\u5757\u5c1a\u672a\u9884\u8f7d\uff0c\u8df3\u8fc7 {} ({})", (Object)stationPos, (Object)team);
            return false;
        }
        Object entity = stationType.m_20615_(level);
        if (entity == null) {
            Espetro.LOGGER.warn("\u65e0\u6cd5\u521b\u5efa\u4e3b\u91cd\u751f\u70b9\u8865\u7ed9\u7ad9\u5b9e\u4f53 at {} ({})", (Object)stationPos, (Object)team);
            return false;
        }
        double x = (double)stationPos.m_123341_() + 0.5;
        double y = stationPos.m_123342_();
        double z = (double)stationPos.m_123343_() + 0.5;
        ((Entity)entity).m_6034_(x, y, z);
        ((Entity)entity).m_146922_(spawn.yaw);
        ((Entity)entity).m_5616_(spawn.yaw);
        ((Entity)entity).m_6593_(Component.m_237113_(SUPPLY_STATION_DISPLAY_NAME));
        ((Entity)entity).m_20340_(false);
        ((Entity)entity).m_20049_(MAIN_BASE_SUPPLY_TAG);
        ((Entity)entity).m_20049_("espetro_main_base_supply_station_team_" + team);
        VehicleManager.applySupplyStationMapTags(entity, team, "main_base_" + team);
        this.fillVehicleEnergy((Entity)entity, Integer.MAX_VALUE);
        if (!level.m_7967_((Entity)entity)) {
            ((Entity)entity).m_146870_();
            Espetro.LOGGER.warn("\u4e3b\u91cd\u751f\u70b9\u8865\u7ed9\u7ad9\u672a\u80fd\u52a0\u5165\u4e16\u754c at {} ({})", (Object)stationPos, (Object)team);
            return false;
        }
        return true;
    }

    public static BlockPos getMainBaseSupplyPosition(SpawnPointConfig.SpawnPoint spawn) {
        float yawRad = spawn.yaw * ((float)Math.PI / 180);
        double rightX = -Mth.m_14089_(yawRad);
        double rightZ = -Mth.m_14031_(yawRad);
        int x = Mth.m_14107_(spawn.x + rightX * 3.0);
        int y = Mth.m_14107_(spawn.y);
        int z = Mth.m_14107_(spawn.z + rightZ * 3.0);
        return new BlockPos(x, y, z);
    }

    private boolean spawnOnePadSupply(ServerLevel level, EntityType<?> stationType, @Nullable VehSpawnSnapshot.Pose pose, String team, String vehicleType, String pitId) {
        if (pose == null) {
            return false;
        }
        BlockPos stationPos = VehicleManager.getSupplyStationPosition(pose);
        if (!level.m_46805_(stationPos)) {
            Espetro.LOGGER.warn("\u8f7d\u5177\u5751\u8865\u7ed9\u7ad9\u533a\u5757\u5c1a\u672a\u9884\u8f7d\uff0c\u8df3\u8fc7 {} ({}/{})", new Object[]{stationPos, vehicleType, pitId});
            return false;
        }
        Object entity = stationType.m_20615_(level);
        if (entity == null) {
            Espetro.LOGGER.warn("\u65e0\u6cd5\u521b\u5efa\u8865\u7ed9\u7ad9\u5b9e\u4f53 at {} ({}/{})", new Object[]{stationPos, vehicleType, pitId});
            return false;
        }
        double x = (double)stationPos.m_123341_() + 0.5;
        double y = stationPos.m_123342_();
        double z = (double)stationPos.m_123343_() + 0.5;
        ((Entity)entity).m_6034_(x, y, z);
        ((Entity)entity).m_146922_(pose.yaw());
        ((Entity)entity).m_5616_(pose.yaw());
        ((Entity)entity).m_6593_(Component.m_237113_(SUPPLY_STATION_DISPLAY_NAME));
        ((Entity)entity).m_20340_(false);
        ((Entity)entity).m_20049_(PAD_SUPPLY_TAG);
        ((Entity)entity).m_20049_("espetro_pad_type_" + vehicleType);
        ((Entity)entity).m_20049_("espetro_pad_id_" + pitId);
        VehicleManager.applySupplyStationMapTags(entity, team, "pad_" + vehicleType + "_" + pitId + "_" + team);
        this.fillVehicleEnergy((Entity)entity, Integer.MAX_VALUE);
        if (!level.m_7967_((Entity)entity)) {
            ((Entity)entity).m_146870_();
            Espetro.LOGGER.warn("\u8865\u7ed9\u7ad9\u672a\u80fd\u52a0\u5165\u4e16\u754c at {} ({}/{})", new Object[]{stationPos, vehicleType, pitId});
            return false;
        }
        return true;
    }

    public static boolean isAmmoSupplyStationEntity(@Nullable Entity entity) {
        if (entity == null) {
            return false;
        }
        ResourceLocation id = BuiltInRegistries.f_256780_.m_7981_(entity.m_6095_());
        return SUPPLY_STATION_ID.equals(id);
    }

    public static boolean isSupplyStationDeployerItem(@Nullable ItemStack stack) {
        if (stack == null || stack.m_41619_()) {
            return false;
        }
        ResourceLocation id = BuiltInRegistries.f_257033_.m_7981_(stack.m_41720_());
        return SUPPLY_STATION_ITEM_ID.equals(id);
    }

    public static boolean isMappedSupplyStation(@Nullable Entity entity) {
        if (entity == null) {
            return false;
        }
        if (entity.m_19880_().contains(SUPPLY_STATION_TAG) || entity.m_19880_().contains(PAD_SUPPLY_TAG)) {
            return true;
        }
        CompoundTag data = entity.getPersistentData();
        return data.m_128441_(SUPPLY_STATION_TEAM_KEY) || data.m_128441_(SUPPLY_STATION_ID_KEY);
    }

    public static void tagCommanderSupplyStation(@Nullable Entity entity, @Nullable String team) {
        if (entity == null || !VehicleManager.isAmmoSupplyStationEntity(entity)) {
            return;
        }
        if (VehicleManager.isMappedSupplyStation(entity) && entity.m_19880_().contains(SUPPLY_STATION_TAG)) {
            if (entity.m_7770_() == null) {
                entity.m_6593_(Component.m_237113_(SUPPLY_STATION_DISPLAY_NAME));
                entity.m_20340_(false);
            }
            return;
        }
        String stationId = "commander_" + entity.m_20148_();
        VehicleManager.applySupplyStationMapTags(entity, team, stationId);
        Espetro.LOGGER.info("\u6307\u6325\u5b98\u8f7d\u5177\u8865\u7ed9\u7ad9\u5df2\u6807\u8bb0: team={} id={} at {}", new Object[]{team, stationId, entity.m_20183_()});
    }

    public static void applySupplyStationMapTags(@Nullable Entity entity, @Nullable String team, @Nullable String stationId) {
        if (entity == null) {
            return;
        }
        entity.m_6593_(Component.m_237113_(SUPPLY_STATION_DISPLAY_NAME));
        entity.m_20340_(false);
        entity.m_20049_(SUPPLY_STATION_TAG);
        if (team != null && !team.isBlank()) {
            String normalized = team.trim().toUpperCase(Locale.ROOT);
            entity.m_20049_("espetro_vehicle_supply_station_team_" + normalized);
            entity.m_20049_("espetro_team_" + normalized);
            CompoundTag data = entity.getPersistentData();
            data.m_128359_(SUPPLY_STATION_TEAM_KEY, normalized);
            if (stationId != null && !stationId.isBlank()) {
                data.m_128359_(SUPPLY_STATION_ID_KEY, stationId);
                entity.m_20049_("espetro_vehicle_supply_station_id_" + stationId);
            }
            BlockPos pos = entity.m_20183_();
            data.m_128405_(SUPPLY_STATION_X_KEY, pos.m_123341_());
            data.m_128405_(SUPPLY_STATION_Y_KEY, pos.m_123342_());
            data.m_128405_(SUPPLY_STATION_Z_KEY, pos.m_123343_());
        } else if (stationId != null && !stationId.isBlank()) {
            entity.getPersistentData().m_128359_(SUPPLY_STATION_ID_KEY, stationId);
        }
    }

    public static BlockPos getSupplyStationPosition(VehSpawnSnapshot.Pose pose) {
        float yawRad = pose.yaw() * ((float)Math.PI / 180);
        double rightX = -Mth.m_14089_(yawRad);
        double rightZ = -Mth.m_14031_(yawRad);
        int x = Mth.m_14107_(pose.x() + rightX * 6.0);
        int y = Mth.m_14107_(pose.y());
        int z = Mth.m_14107_(pose.z() + rightZ * 6.0);
        return new BlockPos(x, y, z);
    }

    private static boolean isPadSupplyStation(Entity entity) {
        return entity != null && (entity.m_19880_().contains(PAD_SUPPLY_TAG) || entity.m_19880_().contains(SUPPLY_STATION_TAG));
    }

    @Nullable
    private Entity findEntity(MinecraftServer server, UUID id) {
        for (ServerLevel level : server.m_129785_()) {
            Entity entity = level.m_8791_(id);
            if (entity == null) continue;
            return entity;
        }
        return null;
    }

    private boolean isDeployedVehicleEntity(Entity entity) {
        return entity.m_19880_().contains(VEHICLE_TAG) || this.activeVehicleIds.contains(entity.m_20148_());
    }

    public static String getDisplayName(String factionId, String vehicleType) {
        VehicleConfig.VehicleTypeConfig cfg = VehicleConfig.getVehicleConfig(factionId, vehicleType);
        if (cfg != null && cfg.displayName != null) {
            return cfg.displayName;
        }
        return vehicleType;
    }

    public static void sendDeployChatMessages(ServerPlayer player, String factionId) {
        player.m_213846_(Component.m_237113_(""));
        player.m_213846_(Component.m_237119_().m_7220_(Component.m_237113_("\u2550\u2550\u2550\u2550 ").m_130948_(Style.f_131099_.m_131148_(TextColor.m_131266_(0xFFAA00)).m_131136_(true))).m_7220_(Component.m_237113_("\u8f7d\u5177\u90e8\u7f72\u9762\u677f").m_130948_(Style.f_131099_.m_178520_(0xFFAA00).m_131136_(true))).m_7220_(Component.m_237113_(" \u2550\u2550\u2550\u2550").m_130948_(Style.f_131099_.m_131148_(TextColor.m_131266_(0xFFAA00)).m_131136_(true))));
        player.m_213846_(Component.m_237113_("\u7f16\u5236: ").m_130948_(Style.f_131099_.m_178520_(0xAAAAAA)).m_7220_(Component.m_237113_(VehicleManager.getFactionDisplayName(factionId)).m_130948_(Style.f_131099_.m_178520_(65450))));
        player.m_213846_(Component.m_237113_("\u8f7d\u5177\u5c06\u6309 JSON \u914d\u7f6e\u7684\u90e8\u7f72\u4f4d\u7f6e\u751f\u6210\u3002").m_130948_(Style.f_131099_.m_178520_(0x888888)));
        player.m_213846_(Component.m_237113_(""));
        Map<String, VehicleConfig.VehicleTypeConfig> configs = VehicleConfig.getFactionVehicles(factionId);
        if (configs.isEmpty()) {
            player.m_213846_(Component.m_237113_("\u00a7c\u5f53\u524d\u7f16\u5236\u65e0\u8f7d\u5177\u914d\u7f6e\u3002"));
            return;
        }
        for (Map.Entry<String, VehicleConfig.VehicleTypeConfig> entry : configs.entrySet()) {
            String type = entry.getKey();
            VehicleConfig.VehicleTypeConfig cfg = entry.getValue();
            String displayName = VehicleManager.getDisplayName(factionId, type);
            int current = VehicleManager.getInstance().getActiveCount(factionId, type);
            long cooldown = VehicleManager.getInstance().getCooldownRemaining(factionId, type);
            Object status = cooldown > 0L ? "\u00a7c\u51b7\u5374 " + cooldown / 1000L + "s" : (current >= cfg.max ? "\u00a76\u5df2\u6ee1" : "\u00a7a\u5c31\u7eea");
            player.m_213846_(Component.m_237119_().m_7220_(Component.m_237113_("\u25b8 " + displayName + "  ").m_130948_(Style.f_131099_.m_178520_(0xFFFFFF))).m_7220_(Component.m_237113_((String)status + " (" + current + "/" + cfg.max + " | " + cfg.respawnMinutes + "\u5206\u949f\u5237\u65b0)").m_130948_(Style.f_131099_.m_178520_(0xAAAAAA))));
            player.m_213846_(Component.m_237119_().m_7220_(Component.m_237113_("  [").m_130948_(Style.f_131099_.m_178520_(0x555555))).m_7220_(Component.m_237113_("\u70b9\u51fb\u90e8\u7f72").m_130948_(Style.f_131099_.m_131148_(TextColor.m_131266_(0x55FF55)).m_131136_(true).m_131142_(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vehicle spawn " + VehicleManager.quoteCommandString(type))).m_131144_(new HoverEvent(HoverEvent.Action.f_130831_, Component.m_237113_("\u00a7a\u70b9\u51fb\u90e8\u7f72 " + displayName))))).m_7220_(Component.m_237113_("]").m_130948_(Style.f_131099_.m_178520_(0x555555))));
        }
        player.m_213846_(Component.m_237113_(""));
        player.m_213846_(Component.m_237113_("\u8f93\u5165 /vehicle list \u67e5\u770b\u5b9e\u65f6\u72b6\u6001").m_130948_(Style.f_131099_.m_178520_(0x888888)));
    }

    private static String getFactionDisplayName(String factionId) {
        return switch (factionId) {
            case "pla_medium_brigade" -> "PLA\u4e2d\u578b\u5408\u6210\u65c5";
            case "pla_heavy_brigade" -> "PLA\u91cd\u578b\u5408\u6210\u65c5";
            case "russia_army" -> "\u4fc4\u7f57\u65af\u9646\u4e0a\u90e8\u961f";
            case "russia_logistics" -> "\u4fc4\u7f57\u65af\u652f\u63f4\u90e8\u961f";
            case "us_cavalry" -> "\u7f8e\u56fd\u7b2c\u4e00\u9a91\u5175\u65c5";
            case "us_airborne" -> "\u7f8e\u56fd141\u7a7a\u964d\u90e8\u961f";
            case "middle_east_militia" -> "\u4e2d\u4e1c\u8054\u5408\u6b66\u88c5";
            case "ukraine_irregular" -> "\u4e4c\u8428\u514b\u975e\u6b63\u89c4\u6b66\u88c5";
            default -> factionId;
        };
    }

    private static String quoteCommandString(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    static {
        SUPPLY_STATION_ID = new ResourceLocation("dragonrise_reforge", "ammo_supply_station");
        SUPPLY_STATION_ITEM_ID = new ResourceLocation("dragonrise_reforge", "ammo_supply_station");
    }

    private record ActiveVehicleData(String factionId, String vehicleType, int slotIndex, String team, boolean initial, ResourceKey<Level> dimension, BlockPos lastKnownPosition) {
        ActiveVehicleData withLocation(Entity entity) {
            return new ActiveVehicleData(this.factionId, this.vehicleType, this.slotIndex, this.team, this.initial, entity.m_9236_().m_46472_(), entity.m_20183_().m_7949_());
        }
    }

    private record RespawnKey(String team, String factionId, String vehicleType) {
    }

    private record PendingInitialVehicle(InitialVehicleDeploymentLedger.SlotKey key, VehicleConfig.VehicleTypeConfig config, @Nullable VehicleConfig.VehicleSlotConfig slot, VehicleConfig.DeploymentPointConfig deployment, BlockPos spawnPosition, long readyAtEpochMs) {
    }

    public record InitialDeploymentStatus(int planned, int spawned, int failed, int pending) {
        public boolean settled() {
            return this.pending == 0;
        }
    }

    public static final class VehicleSupplyState {
        private int ammo;
        private int construction;
        private final int maxCapacity;
        private final boolean canCarryConstruction;

        public VehicleSupplyState(int maxCapacity, boolean canCarryConstruction) {
            this.maxCapacity = maxCapacity;
            this.canCarryConstruction = canCarryConstruction;
        }

        public int getAmmo() {
            return this.ammo;
        }

        public int getConstruction() {
            return this.construction;
        }

        public int getMaxCapacity() {
            return this.maxCapacity;
        }

        public boolean canCarryConstruction() {
            return this.canCarryConstruction;
        }

        public int getTotalUsed() {
            return this.ammo + this.construction;
        }

        public int getFreeSpace() {
            return Math.max(0, this.maxCapacity - this.ammo - this.construction);
        }

        public int addAmmo(int amount) {
            int space = this.getFreeSpace();
            int added = Math.min(amount, space);
            this.ammo += added;
            return added;
        }

        public int removeAmmo(int amount) {
            int removed = Math.min(amount, this.ammo);
            this.ammo -= removed;
            return removed;
        }

        public int addConstruction(int amount) {
            if (!this.canCarryConstruction) {
                return 0;
            }
            int space = this.getFreeSpace();
            int added = Math.min(amount, space);
            this.construction += added;
            return added;
        }

        public int removeConstruction(int amount) {
            int removed = Math.min(amount, this.construction);
            this.construction -= removed;
            return removed;
        }

        public boolean canAffordAmmo(int amount) {
            return this.ammo >= Math.max(0, amount);
        }

        public void fillAmmo() {
            this.ammo = this.maxCapacity;
            this.construction = 0;
        }

        public void fillHalf() {
            this.ammo = this.maxCapacity / 2;
            this.construction = this.canCarryConstruction ? this.maxCapacity / 2 : 0;
        }
    }

    public record SupplyStationSnapshot(UUID id, String name, String team, String dimension, int x, int y, int z) {
    }
}

