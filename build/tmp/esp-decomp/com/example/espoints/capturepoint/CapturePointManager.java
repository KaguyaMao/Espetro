/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerChangedDimensionEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerRespawnEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.LogicalSide
 *  net.minecraftforge.fml.ModList
 *  org.espetro.api.EspetroAPI
 *  org.espetro.api.EspetroAPI$FobSnapshot
 *  org.espetro.api.TacticalMapStateSnapshot
 *  org.espetro.api.TacticalMapStateSnapshot$RallySnapshot
 */
package com.example.espoints.capturepoint;

import com.example.espoints.ESPointsMod;
import com.example.espoints.api.HCRAPI;
import com.example.espoints.capturepoint.CapturePoint;
import com.example.espoints.capturepoint.CapturePointSpatialIndex;
import com.example.espoints.capturepoint.CaptureState;
import com.example.espoints.capturepoint.DisplayState;
import com.example.espoints.config.ModConfig;
import com.example.espoints.integration.OptionalPointsIntegration;
import com.example.espoints.network.PlayLowReinforcementAudioMessage;
import com.example.espoints.network.RequestRateLimiter;
import com.example.espoints.network.RequestTacticalMapTileMessage;
import com.example.espoints.network.SyncBastionsMessage;
import com.example.espoints.network.SyncCapturePointsMessage;
import com.example.espoints.network.SyncConfigMessage;
import com.example.espoints.network.SyncMapPlayerDisplayMessage;
import com.example.espoints.network.SyncOperationModeMessage;
import com.example.espoints.network.SyncPlayerIdentityMessage;
import com.example.espoints.network.SyncPlayerPositionsMessage;
import com.example.espoints.network.SyncTacticalMapBackgroundMessage;
import com.example.espoints.network.SyncTacticalMapConfigMessage;
import com.example.espoints.tactical.TacticalMarkerManager;
import com.example.espoints.tile.TacticalMapTileService;
import com.example.espoints.util.EspetroTeamBridge;
import com.example.espoints.util.ModLogger;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModList;
import org.espetro.api.EspetroAPI;
import org.espetro.api.TacticalMapStateSnapshot;

public class CapturePointManager {
    private static CapturePointManager INSTANCE;
    private final Map<String, CapturePoint> capturePoints;
    private final CapturePointSpatialIndex capturePointSpatialIndex = new CapturePointSpatialIndex();
    private final Map<UUID, String> playerNameMap;
    private final Map<UUID, String> playerTeamNameMap;
    private final Map<UUID, Integer> tacticalPlayerIds = new HashMap<UUID, Integer>();
    private final Map<String, Integer> tacticalIdentityHashes = new HashMap<String, Integer>();
    private int nextTacticalPlayerId = 1;
    private long tacticalPositionSession;
    private final Map<UUID, BastionSyncState> lastBastionSyncByPlayer = new HashMap<UUID, BastionSyncState>();
    private final Map<String, BastionSyncState> tacticalStateByTeam = new HashMap<String, BastionSyncState>();
    private long tacticalStateRevision = Long.MIN_VALUE;
    private final Map<UUID, Long> tacticalMapSubscriptions = new HashMap<UUID, Long>();
    private static final long TACTICAL_MAP_SUBSCRIPTION_TTL_TICKS = 120L;
    private final Map<UUID, Map<String, Long>> playerEnterTimeByPoint = new ConcurrentHashMap<UUID, Map<String, Long>>();
    private final Map<String, CapturedInfo> capturedInfoMap = new ConcurrentHashMap<String, CapturedInfo>();
    private final Map<String, Long> lastLostCaptureTime = new ConcurrentHashMap<String, Long>();
    private int captureCheckTimer = 0;
    private int captureSyncFallbackTimer = 0;
    private static final int CAPTURE_SYNC_FALLBACK_INTERVAL = 80;
    private int currentBatch = 1;
    private int totalBatches = 0;
    private String endBehavior = "terminate";
    private boolean operationModeRunning = false;
    private final Map<String, String> teamRoles = new ConcurrentHashMap<String, String>();
    private final Map<String, Integer> teamReinforcements = new ConcurrentHashMap<String, Integer>();
    private final Map<String, Integer> teamInitialReinforcements = new ConcurrentHashMap<String, Integer>();
    private final Map<CapturePoint, Long> progressRecoveryTimers = new ConcurrentHashMap<CapturePoint, Long>();
    private final Map<String, CapturePoint.SerializableCapturePoint> operationPointSnapshots = new ConcurrentHashMap<String, CapturePoint.SerializableCapturePoint>();
    private int playerPositionSyncTimer = 0;
    private static final int PLAYER_POSITION_SYNC_INTERVAL = 10;
    private int bastionSyncTimer = 0;
    private static final int BASTION_SYNC_INTERVAL = 40;
    private int attackBatchCompletionReinforcement = 200;
    private boolean raasFrontline;
    private int captureReinforcement = 50;
    private int ticketBleedPerSecond = 1;
    private int attackFrontStage = 1;
    private int defendFrontStage = 1;
    private boolean raasFogLifted;
    private int raasBleedTickCounter;
    private final List<String> pendingRaasCapturingTeams = new ArrayList<String>();
    private static final String ESPETRO_MOD_ID = "espetro";
    private static final String ESPETRO_TROOP_COUNT_MANAGER_CLASS = "org.espetro.team.TroopCountManager";
    private static final String ESPETRO_BASTION_MANAGER_CLASS = "org.espetro.bastion.BastionManager";
    private static final String ESPETRO_API_CLASS = "org.espetro.api.EspetroAPI";
    private static final String ESPETRO_GAME_STATE_MANAGER_CLASS = "org.espetro.team.GameStateManager";
    private static final String ESPETRO_SPAWN_POINT_CONFIG_CLASS = "org.espetro.team.SpawnPointConfig";
    private static final String ESPETRO_VEHICLE_SUPPLY_STATION_TAG = "espetro_vehicle_supply_station";
    private static final String ESPETRO_VEHICLE_SUPPLY_STATION_TEAM_KEY = "espetro_vehicle_supply_station_team";
    private static final String ESPETRO_VEHICLE_SUPPLY_STATION_ID_KEY = "espetro_vehicle_supply_station_id";
    private static final String ESPETRO_VEHICLE_SUPPLY_STATION_X_KEY = "espetro_vehicle_supply_station_x";
    private static final String ESPETRO_VEHICLE_SUPPLY_STATION_Y_KEY = "espetro_vehicle_supply_station_y";
    private static final String ESPETRO_VEHICLE_SUPPLY_STATION_Z_KEY = "espetro_vehicle_supply_station_z";
    private boolean espetroDeployingCapturePointsActivated = false;
    private boolean tacticalMarkersClearedForWaiting = false;
    private boolean battlefieldLifecycleActive;
    private final Map<String, PlannedCapturePoint> plannedPointsMap;

    public boolean isValidPointName(String name) {
        return name != null && name.length() == 1 && name.charAt(0) >= 'A' && name.charAt(0) <= 'Z';
    }

    public boolean isValidCoordinates(BlockPos pos1, BlockPos pos2) {
        return pos1 != null && pos2 != null && !pos1.equals((Object)pos2);
    }

    private CapturePointManager() {
        this.capturePoints = new ConcurrentHashMap<String, CapturePoint>();
        this.playerNameMap = new ConcurrentHashMap<UUID, String>();
        this.playerTeamNameMap = new ConcurrentHashMap<UUID, String>();
        this.plannedPointsMap = new ConcurrentHashMap<String, PlannedCapturePoint>();
        this.bindEspetroTeams();
    }

    public boolean addPlannedCapturePoint(String name, BlockPos pos1, BlockPos pos2, int batch) {
        try {
            if (this.plannedPointsMap.containsKey(name)) {
                ModLogger.warn("\u8ba1\u5212\u636e\u70b9\u3010" + name + "\u3011\u5df2\u5b58\u5728\uff0c\u6dfb\u52a0\u5931\u8d25");
                return false;
            }
            PlannedCapturePoint plannedPoint = new PlannedCapturePoint(name, pos1, pos2, batch);
            this.plannedPointsMap.put(name, plannedPoint);
            ModLogger.info("\u8ba1\u5212\u636e\u70b9\u3010" + name + "\u3011\uff08\u6279\u6b21 " + batch + "\uff09\u6dfb\u52a0\u6210\u529f");
            return true;
        }
        catch (Exception e) {
            ModLogger.error("\u6dfb\u52a0\u8ba1\u5212\u636e\u70b9\u65f6\u53d1\u751f\u5f02\u5e38: " + e.getMessage());
            return false;
        }
    }

    public boolean removePlannedCapturePoint(String name) {
        try {
            PlannedCapturePoint plannedPoint = this.plannedPointsMap.remove(name);
            if (plannedPoint == null) {
                ModLogger.warn("\u672a\u627e\u5230\u8ba1\u5212\u636e\u70b9\u3010" + name + "\u3011\uff0c\u79fb\u9664\u5931\u8d25");
                return false;
            }
            this.operationPointSnapshots.remove(name);
            ModLogger.info("\u8ba1\u5212\u636e\u70b9\u3010" + name + "\u3011\u79fb\u9664\u6210\u529f");
            return true;
        }
        catch (Exception e) {
            ModLogger.error("\u79fb\u9664\u8ba1\u5212\u636e\u70b9\u65f6\u53d1\u751f\u5f02\u5e38: " + e.getMessage());
            return false;
        }
    }

    public void clearPlannedCapturePoints() {
        this.plannedPointsMap.clear();
        this.operationPointSnapshots.clear();
        this.pendingRaasCapturingTeams.clear();
        this.raasFrontline = false;
        this.captureReinforcement = 50;
        ModLogger.info("\u6240\u6709\u8ba1\u5212\u636e\u70b9\u5df2\u6e05\u7a7a");
    }

    public static CapturePointManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CapturePointManager();
        }
        return INSTANCE;
    }

    public void resetTransientSyncCaches() {
        this.tacticalPlayerIds.clear();
        this.tacticalIdentityHashes.clear();
        this.nextTacticalPlayerId = 1;
        ++this.tacticalPositionSession;
        this.lastBastionSyncByPlayer.clear();
        this.tacticalStateByTeam.clear();
        this.tacticalStateRevision = Long.MIN_VALUE;
        this.tacticalMapSubscriptions.clear();
    }

    public void setTacticalMapSubscription(ServerPlayer player, boolean active) {
        if (player == null) {
            return;
        }
        UUID playerId = player.m_20148_();
        if (!active) {
            this.tacticalMapSubscriptions.remove(playerId);
            return;
        }
        MinecraftServer server = player.m_20194_();
        if (server == null) {
            return;
        }
        long currentTick = server.m_129921_();
        Long previousExpiry = this.tacticalMapSubscriptions.get(playerId);
        boolean newlySubscribed = previousExpiry == null || previousExpiry < currentTick;
        this.tacticalMapSubscriptions.put(playerId, currentTick + 120L);
        if (newlySubscribed) {
            this.syncPlayerPositionsToPlayer(player);
            this.syncEspetroBastionsToPlayer(player);
        }
    }

    private boolean isTacticalMapSubscribed(ServerPlayer player, long currentTick) {
        Long expiresAt = this.tacticalMapSubscriptions.get(player.m_20148_());
        if (expiresAt == null) {
            return false;
        }
        if (expiresAt < currentTick) {
            this.tacticalMapSubscriptions.remove(player.m_20148_());
            return false;
        }
        return true;
    }

    public void onBattlefieldActivated() {
        if (this.battlefieldLifecycleActive) {
            return;
        }
        this.battlefieldLifecycleActive = true;
        this.espetroDeployingCapturePointsActivated = false;
        this.tacticalMarkersClearedForWaiting = false;
        this.resetTransientSyncCaches();
        if (this.operationModeRunning) {
            this.stopOperationMode();
        } else {
            this.clearAllCapturePoints();
        }
        this.clearPlannedCapturePoints();
    }

    public void onBattlefieldCleared() {
        if (!this.battlefieldLifecycleActive) {
            return;
        }
        this.battlefieldLifecycleActive = false;
        this.espetroDeployingCapturePointsActivated = false;
        this.tacticalMarkersClearedForWaiting = true;
        if (this.operationModeRunning) {
            this.stopOperationMode();
        } else {
            this.clearAllCapturePoints();
            this.syncOperationModeToClients();
        }
        this.clearPlannedCapturePoints();
        this.resetTransientSyncCaches();
        TacticalMarkerManager.reset();
    }

    public void onEspetroDeployingStarted() {
        if (this.plannedPointsMap.isEmpty() || this.espetroDeployingCapturePointsActivated) {
            return;
        }
        this.espetroDeployingCapturePointsActivated = true;
        int detectedTotalBatches = Math.max(this.totalBatches, this.calculateTotalBatches());
        if (detectedTotalBatches <= 0) {
            return;
        }
        this.startOperationMode(detectedTotalBatches, this.endBehavior == null || this.endBehavior.isEmpty() ? "terminate" : this.endBehavior);
        ModLogger.info("Espetro \u90e8\u7f72\u9636\u6bb5\u5f00\u59cb\uff0c\u5df2\u663e\u793a\u5f53\u524d\u5730\u56fe\u7684\u9996\u6279\u636e\u70b9");
    }

    public CapturePoint createCapturePoint(String name, BlockPos pos1, BlockPos pos2) {
        return this.createCapturePoint(name, pos1, pos2, 1);
    }

    public CapturePoint createCapturePoint(String name, BlockPos pos1, BlockPos pos2, int batch) {
        ModLogger.warn("\u666e\u901a\u636e\u70b9\u6a21\u5f0f\u5df2\u79fb\u9664\uff0c\u8bf7\u4f7f\u7528 addPlannedCapturePoint \u521b\u5efa\u884c\u52a8\u6a21\u5f0f\u8ba1\u5212\u636e\u70b9");
        return null;
    }

    public boolean removeCapturePoint(String name) {
        try {
            CapturePoint removed = this.capturePoints.remove(name);
            if (removed != null) {
                this.rebuildCapturePointSpatialIndex();
                ModLogger.info("\u636e\u70b9 " + name + " \u5df2\u5220\u9664");
                this.syncToAllClients();
                return true;
            }
            ModLogger.warn("\u5220\u9664\u636e\u70b9\u5931\u8d25\uff1a\u672a\u627e\u5230\u540d\u79f0\u4e3a " + name + " \u7684\u636e\u70b9");
            return false;
        }
        catch (Exception e) {
            ModLogger.error("\u5220\u9664\u636e\u70b9\u65f6\u53d1\u751f\u5f02\u5e38: " + e.getMessage());
            return false;
        }
    }

    public void clearAllCapturePoints() {
        try {
            this.capturePoints.clear();
            this.rebuildCapturePointSpatialIndex();
            ModLogger.info("\u6240\u6709\u636e\u70b9\u5df2\u6e05\u7a7a");
            this.syncToAllClients();
        }
        catch (Exception e) {
            ModLogger.error("\u6e05\u7a7a\u636e\u70b9\u65f6\u53d1\u751f\u5f02\u5e38: " + e.getMessage());
        }
    }

    public Collection<CapturePoint> getAllCapturePoints() {
        return new ArrayList<CapturePoint>(this.capturePoints.values());
    }

    public CapturePoint getCapturePoint(String name) {
        return this.capturePoints.get(name);
    }

    public CapturePoint checkPlayerInCapturePoint(Player player) {
        String string;
        BlockPos playerPos = player.m_20183_();
        if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            string = EspetroTeamBridge.getServerPlayerTeam(serverPlayer);
        } else {
            string = null;
        }
        String team = string;
        for (CapturePoint point : this.capturePointSpatialIndex.candidates(playerPos)) {
            if (this.operationModeRunning && (!this.raasFrontline ? point.getBatch() != this.currentBatch : !this.canTeamSeeOrInteract(team, point)) || !point.isPositionInside(playerPos)) continue;
            return point;
        }
        return null;
    }

    public boolean setTeamRole(String team, String role) {
        return this.setTeamRole(team, role, 50);
    }

    public boolean setTeamRole(String team, String role, int reinforcements) {
        String normalizedRole = role.toLowerCase();
        if (!normalizedRole.equals("attacker") && !normalizedRole.equals("defender")) {
            ModLogger.warn("\u65e0\u6548\u7684\u89d2\u8272\u7c7b\u578b\uff1a" + role + "\uff0c\u53ea\u80fd\u662f attacker \u6216 defender");
            return false;
        }
        if (reinforcements <= 0) {
            ModLogger.warn("\u65e0\u6548\u7684\u5175\u529b\u503c\uff1a" + reinforcements + "\uff0c\u5175\u529b\u5fc5\u987b\u5927\u4e8e0");
            return false;
        }
        String canonicalTeam = "attacker".equals(normalizedRole) ? "ATTACK" : "DEFEND";
        this.teamRoles.put(canonicalTeam, normalizedRole);
        this.teamReinforcements.put(canonicalTeam, reinforcements);
        this.teamInitialReinforcements.put(canonicalTeam, reinforcements);
        ModLogger.info("Espetro \u9635\u8425 " + canonicalTeam + " \u5df2\u7ed1\u5b9a\u4e3a " + normalizedRole + " \u89d2\u8272\uff0c\u5175\u529b\uff1a" + reinforcements);
        this.syncOperationModeToClients();
        return true;
    }

    public void bindEspetroTeams() {
        this.teamRoles.put("ATTACK", "attacker");
        this.teamRoles.put("DEFEND", "defender");
        this.teamReinforcements.putIfAbsent("ATTACK", 50);
        this.teamReinforcements.putIfAbsent("DEFEND", 50);
        this.teamInitialReinforcements.putIfAbsent("ATTACK", 50);
        this.teamInitialReinforcements.putIfAbsent("DEFEND", 50);
    }

    public boolean hasBothRolesSet() {
        this.bindEspetroTeams();
        return true;
    }

    public String getAttackerTeam() {
        return "ATTACK";
    }

    public String getDefenderTeam() {
        return "DEFEND";
    }

    public int getTeamReinforcements(String team) {
        this.bindEspetroTeams();
        String canonicalTeam = EspetroTeamBridge.canonicalizeTeamName(team);
        return canonicalTeam != null ? this.teamReinforcements.getOrDefault(canonicalTeam, 0) : 0;
    }

    public int getTeamInitialReinforcements(String team) {
        this.bindEspetroTeams();
        String canonicalTeam = EspetroTeamBridge.canonicalizeTeamName(team);
        return canonicalTeam != null ? this.teamInitialReinforcements.getOrDefault(canonicalTeam, 0) : 0;
    }

    public int deductTeamReinforcements(String team, int amount) {
        String canonicalTeam = EspetroTeamBridge.canonicalizeTeamName(team);
        if (canonicalTeam == null || !this.teamReinforcements.containsKey(canonicalTeam)) {
            return -1;
        }
        int currentReinforcements = this.teamReinforcements.get(canonicalTeam);
        int newReinforcements = Math.max(0, currentReinforcements - amount);
        this.teamReinforcements.put(canonicalTeam, newReinforcements);
        ModLogger.info("\u961f\u4f0d " + canonicalTeam + " \u5175\u529b\u51cf\u5c11 " + amount + "\uff0c\u5269\u4f59\u5175\u529b\uff1a" + newReinforcements);
        this.checkWinLossCondition();
        this.checkLowReinforcementThreshold();
        this.syncOperationModeToClients();
        return newReinforcements;
    }

    public void clearTeamReinforcements(String team) {
        String canonicalTeam = EspetroTeamBridge.canonicalizeTeamName(team);
        if (canonicalTeam == null) {
            return;
        }
        this.teamReinforcements.put(canonicalTeam, 0);
        ModLogger.info("\u961f\u4f0d " + canonicalTeam + " \u5175\u529b\u5df2\u6e05\u7a7a");
        this.checkWinLossCondition();
        this.checkLowReinforcementThreshold();
        this.syncOperationModeToClients();
    }

    private void checkWinLossCondition() {
        String attackerTeam = this.getAttackerTeam();
        String defenderTeam = this.getDefenderTeam();
        if (attackerTeam != null && defenderTeam != null) {
            int attackerReinforcements = this.getTeamReinforcements(attackerTeam);
            int defenderReinforcements = this.getTeamReinforcements(defenderTeam);
            if (attackerReinforcements <= 0) {
                this.endOperationModeWithResult(defenderTeam, attackerTeam);
            } else if (defenderReinforcements <= 0) {
                this.endOperationModeWithResult(attackerTeam, defenderTeam);
            }
        }
    }

    private void checkLowReinforcementThreshold() {
        double threshold = (Double)ModConfig.lowReinforcementThreshold.get();
        if (threshold <= 0.0) {
            return;
        }
        String attackerTeam = this.getAttackerTeam();
        String defenderTeam = this.getDefenderTeam();
        if (attackerTeam != null && defenderTeam != null) {
            boolean isLowReinforcement;
            int attackerReinforcements = this.getTeamReinforcements(attackerTeam);
            int defenderReinforcements = this.getTeamReinforcements(defenderTeam);
            int attackerInitial = this.getTeamInitialReinforcements(attackerTeam);
            int defenderInitial = this.getTeamInitialReinforcements(defenderTeam);
            double attackerPercentage = 0.0;
            double defenderPercentage = 0.0;
            if (attackerInitial > 0) {
                attackerPercentage = (double)attackerReinforcements / (double)attackerInitial * 100.0;
            }
            if (defenderInitial > 0) {
                defenderPercentage = (double)defenderReinforcements / (double)defenderInitial * 100.0;
            }
            boolean bl = isLowReinforcement = attackerPercentage <= threshold || defenderPercentage <= threshold;
            if (isLowReinforcement) {
                PlayLowReinforcementAudioMessage.broadcastToAll(true);
                ModLogger.info("\u4e00\u65b9\u5175\u529b\u4f4e\u4e8e\u9608\u503c " + threshold + "%\uff0c\u5f00\u59cb\u64ad\u653e\u80cc\u6c34\u4e00\u6218\u97f3\u9891");
            }
        }
    }

    private void endOperationModeWithResult(String winnerTeam, String loserTeam) {
        MinecraftServer server = ESPointsMod.getServer();
        if (server == null) {
            return;
        }
        String canonicalWinner = EspetroTeamBridge.canonicalizeTeamName(winnerTeam);
        String canonicalLoser = EspetroTeamBridge.canonicalizeTeamName(loserTeam);
        ModLogger.info("\u884c\u52a8\u7ed3\u675f\uff1a" + canonicalWinner + " \u80dc\u5229\uff0c" + canonicalLoser + " \u5931\u8d25");
        try {
            EspetroAPI.notifyObjectiveVictory((String)canonicalWinner);
        }
        catch (Throwable t) {
            ModLogger.warn("\u901a\u77e5 Espetro \u636e\u70b9\u80dc\u5229\u5931\u8d25: " + t.getMessage());
        }
        PlayLowReinforcementAudioMessage.broadcastToAll(false);
        long endTime = System.currentTimeMillis() + 4000L;
        ServerLevel battlefield = EspetroAPI.getActiveBattlefieldLevel((MinecraftServer)server).orElse(null);
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            String playerTeamName;
            if (battlefield == null || player.m_284548_() != battlefield || (playerTeamName = EspetroTeamBridge.getServerPlayerTeam(player)) == null) continue;
            if (playerTeamName.equals(canonicalWinner)) {
                HCRAPI.showWinMessage(player.m_20148_());
                player.m_213846_((Component)Component.m_237113_((String)"\u4f60\u7684\u961f\u4f0d\u80dc\u5229\u4e86\uff01"));
                continue;
            }
            if (!playerTeamName.equals(canonicalLoser)) continue;
            HCRAPI.showLoseMessage(player.m_20148_());
            player.m_213846_((Component)Component.m_237113_((String)"\u4f60\u7684\u961f\u4f0d\u5931\u8d25\u4e86\uff01"));
        }
        if (this.endBehavior.equals("terminate")) {
            this.stopOperationMode();
            ModLogger.info("\u884c\u52a8\u5df2\u7ec8\u6b62\uff0c\u5175\u529b\u8017\u5c3d");
        } else if (this.endBehavior.equals("loop")) {
            this.startOperationMode(this.totalBatches, this.endBehavior);
            ModLogger.info("\u884c\u52a8\u5df2\u91cd\u542f\uff0c\u5f00\u59cb\u65b0\u7684\u5faa\u73af");
        }
    }

    public void startOperationMode(int totalBatches, String endBehavior) {
        this.bindEspetroTeams();
        this.currentBatch = 1;
        this.totalBatches = totalBatches;
        this.endBehavior = endBehavior.toLowerCase();
        this.operationModeRunning = true;
        this.operationPointSnapshots.clear();
        this.pendingRaasCapturingTeams.clear();
        this.raasFogLifted = false;
        this.raasBleedTickCounter = 0;
        this.attackFrontStage = 1;
        this.defendFrontStage = Math.max(1, this.totalBatches);
        for (String team : this.teamInitialReinforcements.keySet()) {
            int initialReinforcements = this.teamInitialReinforcements.get(team);
            this.teamReinforcements.put(team, initialReinforcements);
            ModLogger.info("\u961f\u4f0d " + team + " \u5175\u529b\u5df2\u91cd\u7f6e\u4e3a\u521d\u59cb\u503c\uff1a" + initialReinforcements);
        }
        MinecraftServer server = ESPointsMod.getServer();
        if (server != null) {
            for (ServerPlayer player : server.m_6846_().m_11314_()) {
                if (EspetroTeamBridge.isEspetroTeamPlayer((Player)player)) continue;
                ModLogger.info("\u73a9\u5bb6 " + player.m_7755_().getString() + " \u672a\u52a0\u5165 Espetro \u9635\u8425\uff0c\u88ab\u89c6\u4e3a\u89c2\u6218\u8005");
            }
        }
        this.clearAllCapturePoints();
        this.createCurrentBatchPoints();
        ModLogger.info("\u884c\u52a8\u6a21\u5f0f\u5df2\u542f\u52a8\uff0c\u5f53\u524d\u6279\u6b21\uff1a" + this.currentBatch + ", \u603b\u6279\u6570\uff1a" + totalBatches + ", \u7ed3\u675f\u884c\u4e3a\uff1a" + this.endBehavior + (String)(this.raasFrontline ? "\uff0cRAAS \u524d\u7ebf A=" + this.attackFrontStage + " B=" + this.defendFrontStage : ""));
        this.syncOperationModeToClients();
    }

    private void createCurrentBatchPoints() {
        this.clearAllCapturePoints();
        String defenderTeam = this.getDefenderTeam();
        int createdCount = 0;
        for (PlannedCapturePoint plannedPoint : this.plannedPointsMap.values()) {
            CapturePoint point;
            if (!this.raasFrontline && plannedPoint.getBatch() != this.currentBatch || (point = this.createCapturePointObjectForBatch(plannedPoint)) == null) continue;
            this.capturePoints.put(point.getName(), point);
            ModLogger.info("\u5df2\u521b\u5efa\u6279\u6b21 " + plannedPoint.getBatch() + " \u7684\u636e\u70b9\uff1a" + point.getName());
            if (!this.raasFrontline && defenderTeam != null && !defenderTeam.isEmpty()) {
                point.setCaptorName(defenderTeam);
                point.setProgress(100);
                point.setState(CaptureState.CAPTURED);
                point.setDisplayState(DisplayState.CAPTURED);
                ModLogger.info("\u636e\u70b9 " + point.getName() + " \u5df2\u9ed8\u8ba4\u5f52\u9632\u5b88\u65b9 " + defenderTeam + " \u5360\u9886");
            }
            ++createdCount;
        }
        this.rebuildCapturePointSpatialIndex();
        this.syncToAllClients();
        ModLogger.info("\u5df2\u521b\u5efa\u6279\u6b21 " + this.currentBatch + " \u7684\u6240\u6709\u636e\u70b9\uff0c\u5171 " + createdCount + " \u4e2a");
    }

    private CapturePoint createCapturePointObjectForBatch(PlannedCapturePoint plannedPoint) {
        try {
            int maxPoints;
            int n = maxPoints = this.raasFrontline ? 26 : 7;
            if (this.capturePoints.size() >= maxPoints) {
                ModLogger.warn("\u521b\u5efa\u636e\u70b9\u5931\u8d25\uff1a\u5df2\u8fbe\u6700\u5927\u636e\u70b9\u6570\u91cf\uff08" + maxPoints + "\u4e2a\uff09");
                return null;
            }
            if (this.capturePoints.containsKey(plannedPoint.getName())) {
                ModLogger.warn("\u521b\u5efa\u636e\u70b9\u5931\u8d25\uff1a\u636e\u70b9\u540d\u79f0 " + plannedPoint.getName() + " \u5df2\u5b58\u5728");
                return null;
            }
            if (plannedPoint.getPos1().equals((Object)plannedPoint.getPos2())) {
                ModLogger.warn("\u521b\u5efa\u636e\u70b9\u5931\u8d25\uff1a\u4e24\u70b9\u9700\u6784\u6210\u6709\u6548\u957f\u65b9\u4f53\u533a\u57df");
                return null;
            }
            CapturePoint point = new CapturePoint(plannedPoint.getName(), plannedPoint.getPos1(), plannedPoint.getPos2(), plannedPoint.getBatch());
            ModLogger.info("\u636e\u70b9 " + plannedPoint.getName() + " (\u6279\u6b21 " + plannedPoint.getBatch() + ") \u521b\u5efa\u6210\u529f\uff0c\u533a\u57df\uff1a(" + plannedPoint.getPos1().m_123341_() + ", " + plannedPoint.getPos1().m_123342_() + ", " + plannedPoint.getPos1().m_123343_() + ") - (" + plannedPoint.getPos2().m_123341_() + ", " + plannedPoint.getPos2().m_123342_() + ", " + plannedPoint.getPos2().m_123343_() + ")");
            return point;
        }
        catch (Exception e) {
            ModLogger.error("\u521b\u5efa\u636e\u70b9\u65f6\u53d1\u751f\u5f02\u5e38: " + e.getMessage());
            return null;
        }
    }

    public void stopOperationMode() {
        this.operationModeRunning = false;
        this.currentBatch = 1;
        this.progressRecoveryTimers.clear();
        this.pendingRaasCapturingTeams.clear();
        this.raasFogLifted = false;
        this.raasBleedTickCounter = 0;
        this.attackFrontStage = 1;
        this.defendFrontStage = 1;
        this.clearAllCapturePoints();
        ModLogger.info("\u884c\u52a8\u6a21\u5f0f\u5df2\u7ed3\u675f");
        this.syncOperationModeToClients();
    }

    public boolean nextBatch() {
        if (this.raasFrontline) {
            return false;
        }
        int nextBatch = this.currentBatch + 1;
        boolean hasNextBatch = false;
        for (PlannedCapturePoint plannedPoint : this.plannedPointsMap.values()) {
            if (plannedPoint.getBatch() != nextBatch) continue;
            hasNextBatch = true;
            break;
        }
        if (hasNextBatch) {
            this.snapshotCurrentOperationPoints();
            this.currentBatch = nextBatch;
            this.createCurrentBatchPoints();
            ModLogger.info("\u884c\u52a8\u5df2\u8fdb\u5165\u4e0b\u4e00\u6279\u6b21\uff1a" + this.currentBatch);
            this.syncOperationModeToClients();
            return true;
        }
        return false;
    }

    public int getCurrentBatch() {
        return this.currentBatch;
    }

    public void setTotalBatches(int totalBatches) {
        this.totalBatches = totalBatches;
    }

    public int getTotalBatches() {
        return this.totalBatches;
    }

    public void setEndBehavior(String endBehavior) {
        this.endBehavior = endBehavior;
    }

    public String getEndBehavior() {
        return this.endBehavior;
    }

    public void setAttackBatchCompletionReinforcement(int amount) {
        this.attackBatchCompletionReinforcement = Math.max(0, amount);
    }

    public int getAttackBatchCompletionReinforcement() {
        return this.attackBatchCompletionReinforcement;
    }

    public void setRaasFrontline(boolean raasFrontline) {
        this.raasFrontline = raasFrontline;
    }

    public boolean isRaasFrontline() {
        return this.raasFrontline;
    }

    public void setCaptureReinforcement(int amount) {
        this.captureReinforcement = Math.max(0, amount);
    }

    public int getCaptureReinforcement() {
        return this.captureReinforcement;
    }

    public void setTicketBleedPerSecond(int amount) {
        this.ticketBleedPerSecond = Math.max(0, amount);
    }

    public int getTicketBleedPerSecond() {
        return this.ticketBleedPerSecond;
    }

    public boolean isRaasFogLifted() {
        return this.raasFogLifted;
    }

    public boolean canTeamSeeOrInteract(String team, CapturePoint point) {
        if (!this.raasFrontline || !this.operationModeRunning || this.raasFogLifted || point == null) {
            return true;
        }
        String canonical = EspetroTeamBridge.canonicalizeTeamName(team);
        if (canonical == null) {
            return false;
        }
        if (EspetroTeamBridge.isSameTeam(point.getCaptorName(), canonical) && point.getState() == CaptureState.CAPTURED) {
            return true;
        }
        int front = "ATTACK".equals(canonical) ? this.attackFrontStage : this.defendFrontStage;
        return point.getBatch() == front;
    }

    public int calculateTotalBatches() {
        int maxBatch = 0;
        for (PlannedCapturePoint plannedPoint : this.plannedPointsMap.values()) {
            if (plannedPoint.getBatch() <= maxBatch) continue;
            maxBatch = plannedPoint.getBatch();
        }
        return maxBatch;
    }

    private void checkBatchProgression(MinecraftServer server) {
        if (server == null) {
            return;
        }
        if (this.raasFrontline) {
            this.checkRaasFrontlineProgression(server);
            return;
        }
        ArrayList<CapturePoint> currentPoints = new ArrayList<CapturePoint>();
        for (CapturePoint point : this.capturePoints.values()) {
            if (point.getBatch() != this.currentBatch) continue;
            currentPoints.add(point);
        }
        if (currentPoints.isEmpty()) {
            return;
        }
        String attackerTeam = this.getAttackerTeam();
        if (attackerTeam == null || attackerTeam.isEmpty()) {
            return;
        }
        boolean allCapturedByAttacker = true;
        for (CapturePoint capturePoint : currentPoints) {
            if (capturePoint.getState() == CaptureState.CAPTURED && EspetroTeamBridge.isSameTeam(capturePoint.getCaptorName(), attackerTeam)) continue;
            allCapturedByAttacker = false;
            break;
        }
        if (allCapturedByAttacker) {
            int batchReward = Math.max(0, this.attackBatchCompletionReinforcement);
            if (batchReward > 0 && this.grantEspetroAttackReinforcement(batchReward)) {
                ModLogger.info("\u6279\u6b21 " + this.currentBatch + " \u5b8c\u6210\uff0c\u5df2\u901a\u8fc7 Espetro \u5175\u529b\u63a5\u53e3\u4e3a\u8fdb\u653b\u65b9\u589e\u52a0 " + batchReward + " \u5175\u529b");
            } else if (batchReward <= 0) {
                ModLogger.info("\u6279\u6b21 " + this.currentBatch + " \u5b8c\u6210\uff0cattackBatchCompletionReinforcement=0\uff0c\u8df3\u8fc7\u5175\u529b\u589e\u63f4");
            } else {
                ModLogger.info("\u6279\u6b21 " + this.currentBatch + " \u5b8c\u6210\uff0c\u672a\u68c0\u6d4b\u5230 Espetro\uff0c\u8df3\u8fc7\u8fdb\u653b\u65b9\u5175\u529b\u589e\u63f4");
            }
            for (ServerPlayer player : server.m_6846_().m_11314_()) {
                String playerTeam = EspetroTeamBridge.getServerPlayerTeam(player);
                if (!attackerTeam.equals(playerTeam)) continue;
                Object reinforcementText = !ModList.get().isLoaded(ESPETRO_MOD_ID) ? "\u672a\u5b89\u88c5 Espetro\uff0c\u8df3\u8fc7\u5175\u529b\u589e\u63f4\u3002" : (batchReward <= 0 ? "\u672c\u6279\u6b21\u65e0\u5175\u529b\u589e\u63f4\uff08\u914d\u7f6e\u4e3a 0\uff09\u3002" : "\u8fdb\u653b\u65b9\u83b7\u5f97 " + batchReward + " \u5175\u529b\u589e\u63f4\uff01");
                String atkLabel = EspetroTeamBridge.displayName(attackerTeam);
                if (((String)reinforcementText).startsWith("\u8fdb\u653b\u65b9\u83b7\u5f97")) {
                    reinforcementText = atkLabel + "\u83b7\u5f97 " + batchReward + " \u5175\u529b\u589e\u63f4\uff01";
                }
                player.m_213846_((Component)Component.m_237113_((String)("\u00a76[\u636e\u70b9] \u00a7e\u7b2c " + this.currentBatch + " \u6279\u6b21\u636e\u70b9\u5df2\u5168\u90e8\u5360\u9886\uff01" + (String)reinforcementText)));
            }
            if (this.currentBatch == this.totalBatches) {
                ModLogger.info("\u6700\u540e\u6279\u6b21 " + this.currentBatch + " \u6240\u6709\u636e\u70b9\u5df2\u88ab\u8fdb\u653b\u65b9\u5360\u9886\uff0c\u6839\u636e\u7ed3\u675f\u884c\u4e3a '" + this.endBehavior + "' \u5904\u7406");
                String string = this.getDefenderTeam();
                if (attackerTeam != null && string != null) {
                    this.endOperationModeWithResult(attackerTeam, string);
                }
            } else {
                ModLogger.info("\u5f53\u524d\u6279\u6b21 " + this.currentBatch + " \u6240\u6709\u636e\u70b9\u5df2\u88ab\u8fdb\u653b\u65b9\u5360\u9886\uff0c\u51c6\u5907\u8fdb\u5165\u4e0b\u4e00\u6279\u6b21");
                this.nextBatch();
            }
        }
    }

    private void checkRaasFrontlineProgression(MinecraftServer server) {
        int amount = Math.max(0, this.captureReinforcement);
        for (String team : this.pendingRaasCapturingTeams) {
            boolean granted;
            boolean bl = granted = amount > 0 && this.grantEspetroTeamReinforcement(team, amount);
            if (granted) {
                ModLogger.info("RAAS \u636e\u70b9\u88ab " + team + " \u5360\u9886\uff0c\u5df2\u589e\u63f4 " + amount + " \u5175\u529b");
            } else if (amount <= 0) {
                ModLogger.info("RAAS \u636e\u70b9\u88ab " + team + " \u5360\u9886\uff0ccaptureReinforcement=0\uff0c\u8df3\u8fc7\u5175\u529b\u589e\u63f4");
            } else {
                ModLogger.info("RAAS \u636e\u70b9\u88ab " + team + " \u5360\u9886\uff0c\u672a\u68c0\u6d4b\u5230 Espetro\uff0c\u8df3\u8fc7\u5175\u529b\u589e\u63f4");
            }
            String canonicalTeam = EspetroTeamBridge.canonicalizeTeamName(team);
            for (ServerPlayer player : server.m_6846_().m_11314_()) {
                if (canonicalTeam == null || !canonicalTeam.equals(EspetroTeamBridge.getServerPlayerTeam(player))) continue;
                Object reinforcementText = granted ? "\u83b7\u5f97 " + amount + " \u5175\u529b\u589e\u63f4\uff01" : (amount <= 0 ? "\u672c\u636e\u70b9\u65e0\u5175\u529b\u589e\u63f4\uff08\u914d\u7f6e\u4e3a 0\uff09\u3002" : "\u672a\u5b89\u88c5 Espetro\uff0c\u8df3\u8fc7\u5175\u529b\u589e\u63f4\u3002");
                player.m_213846_((Component)Component.m_237113_((String)("\u00a76[\u636e\u70b9] \u00a7e\u5360\u9886\u6210\u529f\uff01" + (String)reinforcementText)));
            }
        }
        this.pendingRaasCapturingTeams.clear();
        this.updateRaasFrontlines();
        if (!this.raasFogLifted && this.attackFrontStage == this.defendFrontStage) {
            this.raasFogLifted = true;
            ModLogger.info("RAAS \u524d\u7ebf\u91cd\u5408\u4e8e\u9636\u6bb5 " + this.attackFrontStage + "\uff0c\u96fe\u6563\uff1a\u5168\u56fe\u636e\u70b9\u53cc\u65b9\u53ef\u89c1");
            for (ServerPlayer player : server.m_6846_().m_11314_()) {
                player.m_213846_((Component)Component.m_237113_((String)"\u00a76[\u636e\u70b9] \u00a7e\u53cc\u65b9\u524d\u7ebf\u76f8\u9047\uff01\u5168\u56fe\u636e\u70b9\u5df2\u5bf9\u53cc\u65b9\u53ef\u89c1\u3002"));
            }
            this.syncToAllClients();
        }
    }

    private void updateRaasFrontlines() {
        int maxStage = Math.max(1, this.totalBatches);
        while (this.attackFrontStage <= maxStage && this.stageFullyOwnedBy(this.attackFrontStage, "ATTACK") && this.attackFrontStage < maxStage) {
            ++this.attackFrontStage;
            ModLogger.info("RAAS \u9635\u8425A \u524d\u7ebf\u63a8\u8fdb\u81f3\u9636\u6bb5 " + this.attackFrontStage);
        }
        int attackRetreat = this.firstMissingOwnedStage("ATTACK", 1, this.attackFrontStage);
        if (attackRetreat >= 1 && attackRetreat < this.attackFrontStage) {
            ModLogger.info("RAAS \u9635\u8425A \u524d\u7ebf\u56de\u9000\u81f3\u9636\u6bb5 " + attackRetreat);
            this.attackFrontStage = attackRetreat;
        }
        while (this.defendFrontStage >= 1 && this.stageFullyOwnedBy(this.defendFrontStage, "DEFEND") && this.defendFrontStage > 1) {
            --this.defendFrontStage;
            ModLogger.info("RAAS \u9635\u8425B \u524d\u7ebf\u63a8\u8fdb\u81f3\u9636\u6bb5 " + this.defendFrontStage);
        }
        int defendRetreat = this.firstMissingOwnedStageFromEnd("DEFEND", this.defendFrontStage, maxStage);
        if (defendRetreat <= maxStage && defendRetreat > this.defendFrontStage) {
            ModLogger.info("RAAS \u9635\u8425B \u524d\u7ebf\u56de\u9000\u81f3\u9636\u6bb5 " + defendRetreat);
            this.defendFrontStage = defendRetreat;
        }
    }

    private boolean stageFullyOwnedBy(int stage, String team) {
        boolean any = false;
        for (CapturePoint point : this.capturePoints.values()) {
            if (point.getBatch() != stage) continue;
            any = true;
            if (point.getState() == CaptureState.CAPTURED && EspetroTeamBridge.isSameTeam(point.getCaptorName(), team)) continue;
            return false;
        }
        return any;
    }

    private int firstMissingOwnedStage(String team, int low, int highInclusive) {
        for (int stage = low; stage <= highInclusive; ++stage) {
            if (this.stageFullyOwnedBy(stage, team)) continue;
            return stage;
        }
        return -1;
    }

    private int firstMissingOwnedStageFromEnd(String team, int lowInclusive, int high) {
        for (int stage = high; stage >= lowInclusive; --stage) {
            if (this.stageFullyOwnedBy(stage, team)) continue;
            return stage;
        }
        return -1;
    }

    public void tickRaasTicketBleed() {
        if (!this.raasFrontline || !this.operationModeRunning || this.ticketBleedPerSecond <= 0) {
            return;
        }
        String owner = this.raasSoleOwnerOrNull();
        if (owner == null) {
            return;
        }
        if (++this.raasBleedTickCounter < 20) {
            return;
        }
        this.raasBleedTickCounter = 0;
        String loser = "ATTACK".equals(owner) ? "DEFEND" : "ATTACK";
        this.grantEspetroTeamReinforcement(loser, -this.ticketBleedPerSecond);
    }

    private String raasSoleOwnerOrNull() {
        if (this.capturePoints.isEmpty()) {
            return null;
        }
        String owner = null;
        for (CapturePoint point : this.capturePoints.values()) {
            if (point.getState() != CaptureState.CAPTURED) {
                return null;
            }
            String captor = EspetroTeamBridge.canonicalizeTeamName(point.getCaptorName());
            if (captor == null || captor.isEmpty()) {
                return null;
            }
            if (owner == null) {
                owner = captor;
                continue;
            }
            if (owner.equals(captor)) continue;
            return null;
        }
        return owner;
    }

    private static boolean isSuccessfulCaptureTransition(CaptureState oldState, CaptureState newState) {
        if (newState != CaptureState.CAPTURED || oldState == CaptureState.CAPTURED) {
            return false;
        }
        return oldState != CaptureState.CONTESTED && oldState != CaptureState.CAPTURING_DOWN;
    }

    private boolean grantEspetroAttackReinforcement(int amount) {
        return this.grantEspetroTeamReinforcement("ATTACK", amount);
    }

    private boolean grantEspetroTeamReinforcement(String team, int amount) {
        if (amount == 0) {
            return false;
        }
        if (!ModList.get().isLoaded(ESPETRO_MOD_ID)) {
            return false;
        }
        String canonicalTeam = EspetroTeamBridge.canonicalizeTeamName(team);
        if (canonicalTeam == null) {
            return false;
        }
        try {
            EspetroAPI.modifyTeamTroops((String)canonicalTeam, (int)amount, (String)(this.raasFrontline ? "RAAS capture" : "AAS batch"));
            return true;
        }
        catch (Throwable primary) {
            try {
                Class<?> managerClass = Class.forName(ESPETRO_TROOP_COUNT_MANAGER_CLASS);
                Object manager = managerClass.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
                if ("ATTACK".equals(canonicalTeam)) {
                    managerClass.getMethod("modifyAttackTroops", Integer.TYPE).invoke(manager, amount);
                    return true;
                }
                if ("DEFEND".equals(canonicalTeam)) {
                    managerClass.getMethod("modifyDefendTroops", Integer.TYPE).invoke(manager, amount);
                    return true;
                }
                return false;
            }
            catch (Exception e) {
                ModLogger.warn("\u8c03\u7528 Espetro \u5175\u529b\u63a5\u53e3\u5931\u8d25\uff0c\u5df2\u8df3\u8fc7 " + canonicalTeam + " \u5175\u529b\u589e\u63f4: " + e.getMessage());
                return false;
            }
        }
    }

    public List<CapturePoint> getPlannedPointsByBatch(int batch) {
        ArrayList<CapturePoint> result = new ArrayList<CapturePoint>();
        for (PlannedCapturePoint plannedPoint : this.plannedPointsMap.values()) {
            if (plannedPoint.getBatch() != batch) continue;
            CapturePoint point = new CapturePoint(plannedPoint.getName(), plannedPoint.getPos1(), plannedPoint.getPos2(), plannedPoint.getBatch());
            result.add(point);
        }
        return result;
    }

    public Set<CapturePoint> getCurrentBatchPoints() {
        HashSet<CapturePoint> currentPoints = new HashSet<CapturePoint>();
        for (CapturePoint point : this.capturePoints.values()) {
            currentPoints.add(point);
        }
        return currentPoints;
    }

    public boolean isOperationModeRunning() {
        return this.operationModeRunning;
    }

    public Collection<PlannedCapturePoint> getAllPlannedCapturePoints() {
        return this.plannedPointsMap.values();
    }

    public Map<String, PlannedCapturePoint> getPlannedPointsMap() {
        return this.plannedPointsMap;
    }

    public Map<String, String> getTeamRoles() {
        this.bindEspetroTeams();
        return this.teamRoles;
    }

    public Map<String, Integer> getTeamReinforcementsMap() {
        this.bindEspetroTeams();
        return this.teamReinforcements;
    }

    public void clearTeamRoles() {
        this.teamRoles.clear();
        this.teamReinforcements.clear();
        this.teamInitialReinforcements.clear();
        this.bindEspetroTeams();
        ModLogger.info("\u961f\u4f0d\u89d2\u8272\u5df2\u91cd\u7f6e\u4e3a Espetro \u653b\u9632\u9635\u8425");
        this.syncOperationModeToClients();
    }

    public void syncOperationModeFromServer(boolean operationModeRunning, int currentBatch, int totalBatches, String endBehavior, Map<String, String> teamRoles, Map<String, Integer> teamReinforcements, Map<String, Integer> teamInitialReinforcements) {
        this.operationModeRunning = operationModeRunning;
        this.currentBatch = currentBatch;
        this.totalBatches = totalBatches;
        this.endBehavior = endBehavior;
        this.teamRoles.clear();
        this.teamRoles.putAll(this.normalizeTeamRoleMap(teamRoles));
        this.teamReinforcements.clear();
        this.teamReinforcements.putAll(this.normalizeTeamIntegerMap(teamReinforcements));
        this.teamInitialReinforcements.clear();
        this.teamInitialReinforcements.putAll(this.normalizeTeamIntegerMap(teamInitialReinforcements));
        this.bindEspetroTeams();
        ModLogger.info("\u5ba2\u6237\u7aef\u884c\u52a8\u6a21\u5f0f\u72b6\u6001\u5df2\u540c\u6b65");
    }

    private void syncOperationModeToClients() {
        if (!ESPointsMod.isServerRunning()) {
            return;
        }
        this.bindEspetroTeams();
        SyncOperationModeMessage.broadcastToAll(this.operationModeRunning, this.currentBatch, this.totalBatches, this.endBehavior, this.teamRoles, this.teamReinforcements, this.teamInitialReinforcements);
    }

    private Map<String, String> normalizeTeamRoleMap(Map<String, String> roles) {
        HashMap<String, String> normalizedRoles = new HashMap<String, String>();
        if (roles != null) {
            for (Map.Entry<String, String> entry : roles.entrySet()) {
                String role;
                String string = role = entry.getValue() == null ? "" : entry.getValue().toLowerCase(Locale.ROOT);
                if (!"attacker".equals(role) && !"defender".equals(role)) continue;
                String canonicalTeam = EspetroTeamBridge.canonicalizeTeamName(entry.getKey());
                if (canonicalTeam == null) {
                    canonicalTeam = "attacker".equals(role) ? "ATTACK" : "DEFEND";
                }
                normalizedRoles.put(canonicalTeam, role);
            }
        }
        normalizedRoles.put("ATTACK", "attacker");
        normalizedRoles.put("DEFEND", "defender");
        return normalizedRoles;
    }

    private Map<String, Integer> normalizeTeamIntegerMap(Map<String, Integer> values) {
        HashMap<String, Integer> normalizedValues = new HashMap<String, Integer>();
        if (values != null) {
            for (Map.Entry<String, Integer> entry : values.entrySet()) {
                String canonicalTeam = EspetroTeamBridge.canonicalizeTeamName(entry.getKey());
                Integer value = entry.getValue();
                if (canonicalTeam == null || value == null) continue;
                normalizedValues.put(canonicalTeam, value);
            }
        }
        normalizedValues.putIfAbsent("ATTACK", 50);
        normalizedValues.putIfAbsent("DEFEND", 50);
        return normalizedValues;
    }

    public void restorePlannedPointsFromMap(Map<String, PlannedCapturePoint> plannedPointsMap) {
        this.plannedPointsMap.clear();
        this.plannedPointsMap.putAll(plannedPointsMap);
        this.operationPointSnapshots.keySet().removeIf(pointName -> !this.plannedPointsMap.containsKey(pointName));
        ModLogger.info("\u5df2\u6062\u590d " + plannedPointsMap.size() + " \u4e2a\u8ba1\u5212\u636e\u70b9");
    }

    public void restoreTeamRolesFromMap(Map<String, String> teamRoles) {
        this.teamRoles.clear();
        this.teamRoles.putAll(this.normalizeTeamRoleMap(teamRoles));
        this.bindEspetroTeams();
        ModLogger.info("\u5df2\u6062\u590d\u5e76\u7ed1\u5b9a Espetro \u653b\u9632\u9635\u8425\u961f\u4f0d\u89d2\u8272");
    }

    public List<String> getPlannedPointsInfo() {
        ArrayList<String> infoList = new ArrayList<String>();
        TreeMap<Integer, List> batchMap = new TreeMap<Integer, List>();
        for (PlannedCapturePoint plannedCapturePoint : this.plannedPointsMap.values()) {
            batchMap.computeIfAbsent(plannedCapturePoint.getBatch(), k -> new ArrayList()).add(plannedCapturePoint);
        }
        for (Map.Entry entry : batchMap.entrySet()) {
            int batch = (Integer)entry.getKey();
            List points = (List)entry.getValue();
            infoList.add("\u6279\u6b21 " + batch + " (\u5171 " + points.size() + " \u4e2a\u636e\u70b9):");
            for (PlannedCapturePoint point : points) {
                BlockPos pos1 = point.getPos1();
                BlockPos pos2 = point.getPos2();
                infoList.add("  - " + point.getName() + "\uff1a(" + pos1.m_123341_() + "," + pos1.m_123342_() + "," + pos1.m_123343_() + ") - (" + pos2.m_123341_() + "," + pos2.m_123342_() + "," + pos2.m_123343_() + ")");
            }
        }
        return infoList;
    }

    public void updateAllCapturePoints(Level level) {
        this.updateAllCapturePoints(level, 40);
    }

    public void updateAllCapturePoints(Level level, int elapsedTicks) {
        try {
            if (!(level instanceof ServerLevel)) {
                return;
            }
            ServerLevel serverLevel = (ServerLevel)level;
            MinecraftServer server = serverLevel.m_7654_();
            if (server == null || this.capturePoints.isEmpty()) {
                return;
            }
            HashMap playersByPoint = new HashMap();
            for (String name : this.capturePoints.keySet()) {
                playersByPoint.put(name, new ArrayList());
            }
            long now = System.currentTimeMillis();
            long pointRewardIntervalMs = (long)((Integer)ModConfig.pointRewardInterval.get()).intValue() * 1000L;
            int rewardAmount = (Integer)ModConfig.pointRewardAmount.get();
            HashSet<UUID> playersInAnyPoint = new HashSet<UUID>();
            for (ServerPlayer player : server.m_6846_().m_11314_()) {
                if (player.m_284548_() != serverLevel || !this.isPlayerInTeam(player)) continue;
                BlockPos pos = player.m_20183_();
                UUID playerUUID = player.m_20148_();
                Map enterByPoint = this.playerEnterTimeByPoint.computeIfAbsent(playerUUID, ignored -> new ConcurrentHashMap());
                HashSet<String> stillInside = new HashSet<String>();
                String playerTeam = EspetroTeamBridge.getServerPlayerTeam(player);
                for (CapturePoint point : this.capturePointSpatialIndex.candidates(pos)) {
                    if (!point.isPositionInside(pos) || this.operationModeRunning && this.raasFrontline && !this.canTeamSeeOrInteract(playerTeam, point) || this.operationModeRunning && !this.raasFrontline && point.getBatch() != this.currentBatch) continue;
                    ((List)playersByPoint.get(point.getName())).add(player);
                    playersInAnyPoint.add(playerUUID);
                    stillInside.add(point.getName());
                    long enterTime = enterByPoint.getOrDefault(point.getName(), now);
                    if (!enterByPoint.containsKey(point.getName())) {
                        enterByPoint.put(point.getName(), now);
                        enterTime = now;
                    }
                    if (now - enterTime < pointRewardIntervalMs) continue;
                    this.addPointsToPlayer(player, rewardAmount, "\u636e\u70b9\u5956\u52b1");
                    enterByPoint.put(point.getName(), now);
                }
                enterByPoint.keySet().removeIf(pointName -> !stillInside.contains(pointName));
                if (!enterByPoint.isEmpty()) continue;
                this.playerEnterTimeByPoint.remove(playerUUID);
            }
            this.playerEnterTimeByPoint.keySet().removeIf(uuid -> !playersInAnyPoint.contains(uuid));
            boolean shouldSync = false;
            for (CapturePoint point : this.capturePoints.values()) {
                String captorName;
                List playersInPoint = playersByPoint.getOrDefault(point.getName(), List.of());
                CaptureState oldState = point.getState();
                int oldProgress = point.getProgress();
                String oldCaptorName = point.getCaptorName();
                point.updateStatus(playersInPoint, elapsedTicks);
                this.progressRecoveryTimers.remove(point);
                if (oldState != point.getState() || oldProgress != point.getProgress() || !oldCaptorName.equals(point.getCaptorName())) {
                    shouldSync = true;
                    if (oldState != CaptureState.CAPTURED && point.getState() == CaptureState.CAPTURED) {
                        captorName = point.getCaptorName();
                        if (captorName != null && !captorName.isEmpty()) {
                            boolean isScoreSpam;
                            if (this.raasFrontline && CapturePointManager.isSuccessfulCaptureTransition(oldState, point.getState())) {
                                this.pendingRaasCapturingTeams.add(captorName);
                            }
                            ModLogger.info("\u5360\u9886\u8005 " + captorName + " \u5360\u9886\u636e\u70b9 " + point.getName());
                            long scoreSpamCheckTime = System.currentTimeMillis();
                            Long lastLostTime = this.lastLostCaptureTime.get(point.getName());
                            boolean bl = isScoreSpam = lastLostTime != null && scoreSpamCheckTime - lastLostTime < 3000L;
                            if (isScoreSpam) {
                                ModLogger.info("\u68c0\u6d4b\u5230\u5237\u5206\u64cd\u4f5c\uff0c\u4e0d\u7ed9\u4e88\u5360\u9886\u5956\u52b1\uff1a" + point.getName());
                            } else {
                                this.giveCaptureReward(server, captorName, point.getName());
                            }
                            this.capturedInfoMap.put(point.getName(), new CapturedInfo(captorName, scoreSpamCheckTime));
                            this.lastLostCaptureTime.remove(point.getName());
                        }
                    } else if (oldState == CaptureState.CAPTURED && point.getState() != CaptureState.CAPTURED) {
                        this.lastLostCaptureTime.put(point.getName(), System.currentTimeMillis());
                        this.capturedInfoMap.remove(point.getName());
                    } else if (point.getState() == CaptureState.CAPTURED && !oldCaptorName.equals(point.getCaptorName()) && (captorName = point.getCaptorName()) != null && !captorName.isEmpty()) {
                        this.capturedInfoMap.put(point.getName(), new CapturedInfo(captorName, System.currentTimeMillis()));
                        this.lastLostCaptureTime.remove(point.getName());
                    }
                }
                if (point.getState() == CaptureState.CAPTURED) {
                    captorName = point.getCaptorName();
                    if (captorName == null || captorName.isEmpty()) continue;
                    long rewardCheckTime = System.currentTimeMillis();
                    long delay = (long)((Integer)ModConfig.capturedRewardDelay.get()).intValue() * 1000L;
                    long interval = (long)((Integer)ModConfig.capturedRewardInterval.get()).intValue() * 1000L;
                    CapturedInfo capturedInfo = this.capturedInfoMap.computeIfAbsent(point.getName(), k -> new CapturedInfo(captorName, rewardCheckTime));
                    if (rewardCheckTime - capturedInfo.getCaptureTime() < delay || rewardCheckTime - capturedInfo.getLastRewardTime() < interval) continue;
                    this.giveCapturedReward(server, captorName, point.getName());
                    capturedInfo.setLastRewardTime(rewardCheckTime);
                    continue;
                }
                this.capturedInfoMap.remove(point.getName());
            }
            if (this.isOperationModeRunning()) {
                this.checkBatchProgression(server);
            }
            if (shouldSync || ++this.captureSyncFallbackTimer >= 80) {
                this.syncToAllClients();
                this.captureSyncFallbackTimer = 0;
            }
        }
        catch (Exception e) {
            ModLogger.error("\u66f4\u65b0\u636e\u70b9\u72b6\u6001\u65f6\u53d1\u751f\u5f02\u5e38: " + e.getMessage());
        }
    }

    public void syncToAllClients() {
        if (!ESPointsMod.isServerRunning()) {
            ModLogger.warn("\u5c1d\u8bd5\u5728\u5ba2\u6237\u7aef\u8c03\u7528\u670d\u52a1\u5668\u540c\u6b65\u65b9\u6cd5\uff0c\u5ffd\u7565");
            return;
        }
        try {
            MinecraftServer server = ESPointsMod.getServer();
            if (server == null) {
                return;
            }
            List players = server.m_6846_().m_11314_();
            if (players.isEmpty()) {
                return;
            }
            List<CapturePoint.SerializableCapturePoint> commonPoints = this.operationModeRunning ? null : this.getAllSerializablePoints();
            HashMap<String, List<CapturePoint.SerializableCapturePoint>> pointsByTeam = new HashMap<String, List<CapturePoint.SerializableCapturePoint>>();
            for (ServerPlayer player : players) {
                String teamName;
                List<CapturePoint.SerializableCapturePoint> points = commonPoints;
                if (points == null && (points = (List<CapturePoint.SerializableCapturePoint>)pointsByTeam.get(teamName = Optional.ofNullable(EspetroTeamBridge.getServerPlayerTeam(player)).orElse(""))) == null) {
                    points = this.getSerializablePointsForMap(player);
                    pointsByTeam.put(teamName, points);
                }
                SyncCapturePointsMessage.sendToPlayer(player, points);
            }
        }
        catch (Exception e) {
            ModLogger.error("\u540c\u6b65\u636e\u70b9\u6570\u636e\u5230\u6240\u6709\u5ba2\u6237\u7aef\u65f6\u53d1\u751f\u5f02\u5e38: " + e.getMessage());
        }
    }

    public int getCapturePointCount() {
        return this.capturePoints.size();
    }

    public List<CapturePoint.SerializableCapturePoint> getAllSerializablePoints() {
        ArrayList<CapturePoint.SerializableCapturePoint> serializedPoints = new ArrayList<CapturePoint.SerializableCapturePoint>();
        for (CapturePoint point : this.capturePoints.values()) {
            serializedPoints.add(point.toSerializable());
        }
        return serializedPoints;
    }

    public List<CapturePoint.SerializableCapturePoint> getOverviewSerializablePoints() {
        if (this.plannedPointsMap.isEmpty()) {
            List<CapturePoint.SerializableCapturePoint> points = this.getAllSerializablePoints();
            points.sort(Comparator.comparingInt(point -> point.batch).thenComparing(point -> point.name));
            return points;
        }
        String defenderTeam = this.getDefenderTeam();
        ArrayList<CapturePoint.SerializableCapturePoint> points = new ArrayList<CapturePoint.SerializableCapturePoint>();
        for (PlannedCapturePoint plannedPoint : this.plannedPointsMap.values()) {
            CapturePoint activePoint = this.capturePoints.get(plannedPoint.getName());
            if (activePoint != null) {
                points.add(activePoint.toSerializable());
                continue;
            }
            CapturePoint.SerializableCapturePoint snapshot = this.operationPointSnapshots.get(plannedPoint.getName());
            points.add(snapshot != null ? snapshot : this.serializableFromPlannedPoint(plannedPoint, defenderTeam));
        }
        points.sort(Comparator.comparingInt(point -> point.batch).thenComparing(point -> point.name));
        return points;
    }

    private List<CapturePoint.SerializableCapturePoint> getSerializablePointsForMap(ServerPlayer player) {
        if (!this.operationModeRunning) {
            return this.getAllSerializablePoints();
        }
        String playerTeamName = EspetroTeamBridge.getServerPlayerTeam(player);
        if (this.raasFrontline) {
            return this.getRaasVisiblePointsForTeam(playerTeamName);
        }
        String attackerTeam = this.getAttackerTeam();
        String defenderTeam = this.getDefenderTeam();
        if (defenderTeam.equals(playerTeamName)) {
            return this.getAllPlannedPointsForMap(defenderTeam);
        }
        if (attackerTeam.equals(playerTeamName)) {
            return this.getAttackerVisiblePointsForMap(attackerTeam, defenderTeam);
        }
        return this.getCurrentBatchSerializablePoints();
    }

    private List<CapturePoint.SerializableCapturePoint> getRaasVisiblePointsForTeam(String team) {
        ArrayList<CapturePoint.SerializableCapturePoint> points = new ArrayList<CapturePoint.SerializableCapturePoint>();
        for (CapturePoint point2 : this.capturePoints.values()) {
            if (!this.canTeamSeeOrInteract(team, point2)) continue;
            points.add(point2.toSerializable());
        }
        points.sort(Comparator.comparingInt(point -> point.batch).thenComparing(point -> point.name));
        return points;
    }

    private List<CapturePoint.SerializableCapturePoint> getAllPlannedPointsForMap(String defenderTeam) {
        ArrayList<CapturePoint.SerializableCapturePoint> points = new ArrayList<CapturePoint.SerializableCapturePoint>();
        for (PlannedCapturePoint plannedPoint : this.plannedPointsMap.values()) {
            CapturePoint activePoint = this.capturePoints.get(plannedPoint.getName());
            if (activePoint != null) {
                points.add(activePoint.toSerializable());
                continue;
            }
            CapturePoint.SerializableCapturePoint snapshot = this.operationPointSnapshots.get(plannedPoint.getName());
            points.add(snapshot != null ? snapshot : this.serializableFromPlannedPoint(plannedPoint, defenderTeam));
        }
        points.sort(Comparator.comparingInt(point -> point.batch).thenComparing(point -> point.name));
        return points;
    }

    private List<CapturePoint.SerializableCapturePoint> getAttackerVisiblePointsForMap(String attackerTeam, String defenderTeam) {
        ArrayList<CapturePoint.SerializableCapturePoint> points = new ArrayList<CapturePoint.SerializableCapturePoint>();
        for (PlannedCapturePoint plannedPoint : this.plannedPointsMap.values()) {
            if (plannedPoint.getBatch() < this.currentBatch) {
                CapturePoint.SerializableCapturePoint snapshot = this.operationPointSnapshots.get(plannedPoint.getName());
                points.add(snapshot != null ? snapshot : this.serializableFromPlannedPoint(plannedPoint, attackerTeam, CaptureState.CAPTURED, DisplayState.CAPTURED, 100));
                continue;
            }
            if (plannedPoint.getBatch() != this.currentBatch) continue;
            CapturePoint activePoint = this.capturePoints.get(plannedPoint.getName());
            if (activePoint != null) {
                points.add(activePoint.toSerializable());
                continue;
            }
            CapturePoint.SerializableCapturePoint snapshot = this.operationPointSnapshots.get(plannedPoint.getName());
            points.add(snapshot != null ? snapshot : this.serializableFromPlannedPoint(plannedPoint, defenderTeam));
        }
        points.sort(Comparator.comparingInt(point -> point.batch).thenComparing(point -> point.name));
        return points;
    }

    private void snapshotCurrentOperationPoints() {
        for (CapturePoint point : this.capturePoints.values()) {
            this.operationPointSnapshots.put(point.getName(), point.toSerializable());
        }
    }

    private List<CapturePoint.SerializableCapturePoint> getCurrentBatchSerializablePoints() {
        ArrayList<CapturePoint.SerializableCapturePoint> points = new ArrayList<CapturePoint.SerializableCapturePoint>();
        for (CapturePoint point2 : this.capturePoints.values()) {
            if (point2.getBatch() != this.currentBatch) continue;
            points.add(point2.toSerializable());
        }
        points.sort(Comparator.comparingInt(point -> point.batch).thenComparing(point -> point.name));
        return points;
    }

    private CapturePoint.SerializableCapturePoint serializableFromPlannedPoint(PlannedCapturePoint plannedPoint, String defaultCaptor) {
        return this.serializableFromPlannedPoint(plannedPoint, defaultCaptor != null ? defaultCaptor : "", CaptureState.CAPTURED, DisplayState.CAPTURED, 100);
    }

    private CapturePoint.SerializableCapturePoint serializableFromPlannedPoint(PlannedCapturePoint plannedPoint, String captorName, CaptureState state, DisplayState displayState, int progress) {
        return new CapturePoint.SerializableCapturePoint(plannedPoint.getName(), plannedPoint.getPos1(), plannedPoint.getPos2(), plannedPoint.getBatch(), state, displayState, captorName, progress);
    }

    private void rebuildCapturePointSpatialIndex() {
        this.capturePointSpatialIndex.rebuild(this.capturePoints.values());
    }

    private void syncPlayerPositions() {
        MinecraftServer server = ESPointsMod.getServer();
        if (server == null) {
            return;
        }
        ServerLevel battlefield = EspetroAPI.getActiveBattlefieldLevel((MinecraftServer)server).orElse(null);
        if (battlefield == null) {
            return;
        }
        long currentTick = server.m_129921_();
        HashMap<String, List> recipientsByTeam = new HashMap<String, List>();
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            String team;
            if (player.m_284548_() != battlefield || !this.isTacticalMapSubscribed(player, currentTick) || (team = EspetroTeamBridge.canonicalizeTeamName(EspetroTeamBridge.getServerPlayerTeam(player))) == null) continue;
            recipientsByTeam.computeIfAbsent(team, ignored -> new ArrayList()).add(player);
        }
        if (recipientsByTeam.isEmpty()) {
            return;
        }
        HashMap positionsByTeam = new HashMap();
        HashMap identitiesByTeam = new HashMap();
        for (ServerPlayer serverPlayer : server.m_6846_().m_11314_()) {
            if (serverPlayer.m_284548_() != battlefield || !EspetroTeamBridge.isPlayerVisibleOnTacticalMap((Player)serverPlayer)) continue;
            UUID playerUUID = serverPlayer.m_20148_();
            String playerName = serverPlayer.m_7755_().getString();
            String teamName = EspetroTeamBridge.canonicalizeTeamName(EspetroTeamBridge.getServerPlayerTeam(serverPlayer));
            if (teamName == null || !recipientsByTeam.containsKey(teamName)) continue;
            this.syncMapDataIfPlayerTeamChanged(serverPlayer, teamName);
            int squadId = EspetroTeamBridge.getPlayerSquadId(serverPlayer);
            boolean squadLeader = EspetroTeamBridge.isSquadLeaderPublic(serverPlayer);
            boolean commander = EspetroTeamBridge.isCommander(serverPlayer);
            int shortId = this.tacticalPlayerId(playerUUID);
            MapPositionSample sample = CapturePointManager.samplePlayerMapPosition(serverPlayer);
            SyncPlayerPositionsMessage.PlayerPosition pos = SyncPlayerPositionsMessage.PlayerPosition.positionOnly(sample.x, sample.y, sample.z, sample.yaw);
            positionsByTeam.computeIfAbsent(teamName, ignored -> new HashMap()).put(shortId, pos);
            identitiesByTeam.computeIfAbsent(teamName, ignored -> new ArrayList()).add(new SyncPlayerIdentityMessage.Identity(shortId, playerUUID, playerName, teamName, squadId, squadLeader, commander));
        }
        for (Map.Entry entry : recipientsByTeam.entrySet()) {
            List<SyncPlayerIdentityMessage.Identity> identities = identitiesByTeam.getOrDefault(entry.getKey(), List.of()).stream().sorted(Comparator.comparingInt(SyncPlayerIdentityMessage.Identity::shortId)).toList();
            int identityHash = identities.hashCode();
            if (!Objects.equals(this.tacticalIdentityHashes.put((String)entry.getKey(), identityHash), identityHash)) {
                SyncPlayerIdentityMessage.sendToPlayers((Collection)entry.getValue(), this.tacticalPositionSession, identities);
            }
            SyncPlayerPositionsMessage.sendToPlayers((Collection)entry.getValue(), this.tacticalPositionSession, positionsByTeam.getOrDefault(entry.getKey(), Map.of()));
        }
    }

    private void syncBastionsToBattlefieldPlayers() {
        MinecraftServer server = ESPointsMod.getServer();
        if (server == null) {
            return;
        }
        ServerLevel battlefield = EspetroAPI.getActiveBattlefieldLevel((MinecraftServer)server).orElse(null);
        if (battlefield == null) {
            return;
        }
        long currentTick = server.m_129921_();
        HashMap<String, List> recipientsByTeam = new HashMap<String, List>();
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            String team;
            if (player.m_284548_() != battlefield || !this.isTacticalMapSubscribed(player, currentTick) || (team = this.getVisibleEspetroBastionTeam(player)) == null || team.isEmpty()) continue;
            recipientsByTeam.computeIfAbsent(team, ignored -> new ArrayList()).add(player);
        }
        for (List recipients : recipientsByTeam.values()) {
            BastionSyncState state = this.createBastionSyncState((ServerPlayer)recipients.get(0));
            for (ServerPlayer player : recipients) {
                this.sendBastionStateToPlayer(player, state);
            }
        }
    }

    private void syncPlayerPositionsToPlayer(ServerPlayer player) {
        MinecraftServer server = ESPointsMod.getServer();
        if (server == null) {
            return;
        }
        ServerLevel battlefield = EspetroAPI.getActiveBattlefieldLevel((MinecraftServer)server).orElse(null);
        if (battlefield == null || player.m_284548_() != battlefield || !this.isTacticalMapSubscribed(player, server.m_129921_())) {
            return;
        }
        String viewerTeam = EspetroTeamBridge.canonicalizeTeamName(EspetroTeamBridge.getServerPlayerTeam(player));
        HashMap<Integer, SyncPlayerPositionsMessage.PlayerPosition> positions = new HashMap<Integer, SyncPlayerPositionsMessage.PlayerPosition>();
        ArrayList<SyncPlayerIdentityMessage.Identity> identities = new ArrayList<SyncPlayerIdentityMessage.Identity>();
        for (ServerPlayer onlinePlayer : server.m_6846_().m_11314_()) {
            String onlineTeam = EspetroTeamBridge.canonicalizeTeamName(EspetroTeamBridge.getServerPlayerTeam(onlinePlayer));
            if (viewerTeam == null || !viewerTeam.equals(onlineTeam) || onlinePlayer.m_284548_() != battlefield || !EspetroTeamBridge.isPlayerVisibleOnTacticalMap((Player)onlinePlayer)) continue;
            UUID playerUUID = onlinePlayer.m_20148_();
            String playerName = onlinePlayer.m_7755_().getString();
            String teamName = onlineTeam;
            MapPositionSample sample = CapturePointManager.samplePlayerMapPosition(onlinePlayer);
            int squadId = EspetroTeamBridge.getPlayerSquadId(onlinePlayer);
            boolean squadLeader = EspetroTeamBridge.isSquadLeaderPublic(onlinePlayer);
            boolean commander = EspetroTeamBridge.isCommander(onlinePlayer);
            int shortId = this.tacticalPlayerId(playerUUID);
            positions.put(shortId, SyncPlayerPositionsMessage.PlayerPosition.positionOnly(sample.x, sample.y, sample.z, sample.yaw));
            identities.add(new SyncPlayerIdentityMessage.Identity(shortId, playerUUID, playerName, teamName, squadId, squadLeader, commander));
        }
        identities.sort(Comparator.comparingInt(SyncPlayerIdentityMessage.Identity::shortId));
        SyncPlayerIdentityMessage.sendToPlayer(player, this.tacticalPositionSession, identities);
        SyncPlayerPositionsMessage.sendToPlayers(List.of(player), this.tacticalPositionSession, positions);
        ModLogger.debug("\u5df2\u5411\u73a9\u5bb6 " + player.m_7755_().getString() + " \u53d1\u9001\u73a9\u5bb6\u4f4d\u7f6e\u5feb\u7167\uff0c\u5171 " + positions.size() + " \u4eba");
    }

    private int tacticalPlayerId(UUID playerId) {
        Integer existing = this.tacticalPlayerIds.get(playerId);
        if (existing != null) {
            return existing;
        }
        if (this.nextTacticalPlayerId > 65535) {
            this.tacticalPlayerIds.clear();
            this.tacticalIdentityHashes.clear();
            this.nextTacticalPlayerId = 1;
            ++this.tacticalPositionSession;
        }
        int assigned = this.nextTacticalPlayerId++;
        this.tacticalPlayerIds.put(playerId, assigned);
        return assigned;
    }

    private static MapPositionSample samplePlayerMapPosition(ServerPlayer player) {
        Entity body = player.m_20201_();
        if (body == null) {
            body = player;
        }
        return new MapPositionSample(body.m_20185_(), body.m_20186_(), body.m_20189_(), player.m_146908_());
    }

    private void syncEspetroBastionsToPlayer(ServerPlayer player) {
        this.sendBastionStateToPlayer(player, this.createBastionSyncState(player));
    }

    private BastionSyncState createBastionSyncState(ServerPlayer player) {
        TacticalMapStateSnapshot snapshot = EspetroAPI.getTacticalMapStateSnapshot();
        if (snapshot.revision() != this.tacticalStateRevision) {
            this.tacticalStateRevision = snapshot.revision();
            this.tacticalStateByTeam.clear();
        }
        String team = this.getVisibleEspetroBastionTeam(player);
        String dimension = player.m_284548_().m_46472_().m_135782_().toString();
        if (team == null || team.isBlank()) {
            return new BastionSyncState(List.of(), List.of(), List.of());
        }
        String cacheKey = team + "\n" + dimension;
        BastionSyncState shared = this.tacticalStateByTeam.computeIfAbsent(cacheKey, ignored -> this.createSharedTacticalState(snapshot, team, dimension));
        ArrayList<SyncBastionsMessage.BaseInfo> bases = new ArrayList<SyncBastionsMessage.BaseInfo>(shared.bases());
        snapshot.playerDeployPoints().stream().filter(point -> player.m_20148_().equals(point.playerId()) && dimension.equals(point.dimension())).findFirst().ifPresent(point -> bases.add(new SyncBastionsMessage.BaseInfo("\u539f\u90e8\u7f72\u70b9", team, new BlockPos(point.x(), point.y(), point.z()), 0.0f)));
        return new BastionSyncState(shared.bastions(), List.copyOf(bases), shared.vehicleSupplyStations());
    }

    private BastionSyncState createSharedTacticalState(TacticalMapStateSnapshot snapshot, String team, String dimension) {
        ArrayList<SyncBastionsMessage.BastionInfo> bastions = new ArrayList<SyncBastionsMessage.BastionInfo>();
        for (EspetroAPI.FobSnapshot structure : snapshot.structures()) {
            String kind;
            if (!team.equals(structure.team()) || !dimension.equals(structure.dimension()) || "HAB".equals(kind = structure.type().toUpperCase(Locale.ROOT)) && !structure.radioCovered()) continue;
            bastions.add(new SyncBastionsMessage.BastionInfo(structure.name(), structure.team(), new BlockPos(structure.x(), structure.y(), structure.z()), kind, structure.construction(), structure.ammunition(), structure.habOperational(), structure.buildRadius(), structure.exclusionRadius(), 0L));
        }
        for (TacticalMapStateSnapshot.RallySnapshot rally : snapshot.rallies()) {
            if (!team.equals(rally.team()) || !dimension.equals(rally.dimension())) continue;
            bastions.add(new SyncBastionsMessage.BastionInfo("Rally " + rally.squadId(), rally.team(), new BlockPos(rally.x(), rally.y(), rally.z()), "RALLY", 0, 0, true, 0.0, 0.0, rally.nextWaveAtMillis()));
        }
        List<SyncBastionsMessage.BaseInfo> bases = snapshot.teamBases().stream().filter(base -> team.equals(base.team()) && dimension.equals(base.dimension())).map(base -> new SyncBastionsMessage.BaseInfo(base.name(), base.team(), new BlockPos(base.x(), base.y(), base.z()), base.yaw())).toList();
        List<SyncBastionsMessage.VehicleSupplyStationInfo> stations = snapshot.vehicleSupplyStations().stream().filter(station -> team.equals(station.team()) && dimension.equals(station.dimension())).map(station -> new SyncBastionsMessage.VehicleSupplyStationInfo(station.name(), station.team(), new BlockPos(station.x(), station.y(), station.z()))).toList();
        return new BastionSyncState(List.copyOf(bastions), List.copyOf(bases), List.copyOf(stations));
    }

    private void sendBastionStateToPlayer(ServerPlayer player, BastionSyncState state) {
        BastionSyncState previous = this.lastBastionSyncByPlayer.get(player.m_20148_());
        if (state.equals(previous)) {
            return;
        }
        this.lastBastionSyncByPlayer.put(player.m_20148_(), state);
        SyncBastionsMessage.sendToPlayer(player, state.bastions(), state.bases(), state.vehicleSupplyStations());
    }

    private List<SyncBastionsMessage.BastionInfo> getVisibleEspetroBastions(ServerPlayer player) {
        ArrayList<SyncBastionsMessage.BastionInfo> visibleBastions = new ArrayList<SyncBastionsMessage.BastionInfo>();
        if (!ModList.get().isLoaded(ESPETRO_MOD_ID)) {
            return visibleBastions;
        }
        try {
            String visibleTeam = this.getVisibleEspetroBastionTeam(player);
            if (visibleTeam == null || visibleTeam.isEmpty()) {
                return visibleBastions;
            }
            try {
                this.appendEspetroApiFobs(player, visibleTeam, visibleBastions);
                this.appendEspetroApiRallies(player, visibleTeam, visibleBastions);
                return visibleBastions;
            }
            catch (ClassNotFoundException | NoSuchMethodException reflectiveOperationException) {
                Class<?> managerClass = Class.forName(ESPETRO_BASTION_MANAGER_CLASS);
                Object manager = managerClass.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
                Method getAllBastionsMethod = managerClass.getMethod("getAllBastions", new Class[0]);
                Iterable bastions = (Iterable)getAllBastionsMethod.invoke(manager, new Object[0]);
                for (Object bastion : bastions) {
                    boolean isHab;
                    String bastionTeam;
                    BlockPos pos;
                    if (bastion == null || !this.isEspetroBastionActive(bastion) || (pos = this.getEspetroBastionPosition(managerClass, manager, bastion)) == null || !visibleTeam.equals(bastionTeam = (String)bastion.getClass().getMethod("getTeam", new Class[0]).invoke(bastion, new Object[0])) || (isHab = this.invokeOptionalBooleanGetter(bastion, "isHab", false)) && !this.invokeOptionalBooleanGetter(bastion, "isHabCoveredCache", true)) continue;
                    String name = (String)bastion.getClass().getMethod("getName", new Class[0]).invoke(bastion, new Object[0]);
                    visibleBastions.add(new SyncBastionsMessage.BastionInfo(name, bastionTeam, pos, isHab ? "HAB" : "RADIO", this.invokeOptionalIntGetter(bastion, "getConstructionSupplies"), this.invokeOptionalIntGetter(bastion, "getAmmunitionSupplies"), this.invokeOptionalBooleanGetter(bastion, "isHabBuilt", true), 150.0, 400.0, 0L));
                }
            }
        }
        catch (Exception e) {
            ModLogger.warn("\u540c\u6b65 Espetro \u5175\u7ad9\u4fe1\u606f\u5931\u8d25\uff0c\u5df2\u8df3\u8fc7: " + e.getMessage());
        }
        return visibleBastions;
    }

    private void appendEspetroApiFobs(ServerPlayer player, String visibleTeam, List<SyncBastionsMessage.BastionInfo> output) throws ReflectiveOperationException {
        Class<?> apiClass = Class.forName(ESPETRO_API_CLASS);
        Object result = apiClass.getMethod("getFobs", new Class[0]).invoke(null, new Object[0]);
        if (!(result instanceof Iterable)) {
            return;
        }
        Iterable snapshots = (Iterable)result;
        String dimension = player.m_284548_().m_46472_().m_135782_().toString();
        for (Object snapshot : snapshots) {
            if (snapshot == null || !visibleTeam.equals(this.invokeString(snapshot, "team")) || !dimension.equals(this.invokeString(snapshot, "dimension"))) continue;
            String kind = this.invokeOptionalString(snapshot, "kind");
            if (kind == null || kind.isBlank()) {
                kind = this.invokeOptionalString(snapshot, "type");
            }
            if (kind == null || kind.isBlank()) {
                String string = kind = this.invokeOptionalBoolean(snapshot, "habBuilt") ? "HAB" : "RADIO";
            }
            if ("FOB".equalsIgnoreCase(kind)) {
                kind = "RADIO";
            }
            if ("HAB".equalsIgnoreCase(kind) && !this.invokeOptionalBoolean(snapshot, "radioCovered", true)) continue;
            output.add(new SyncBastionsMessage.BastionInfo(this.invokeString(snapshot, "name"), this.invokeString(snapshot, "team"), new BlockPos(this.invokeInt(snapshot, "x"), this.invokeInt(snapshot, "y"), this.invokeInt(snapshot, "z")), kind.toUpperCase(Locale.ROOT), this.invokeInt(snapshot, "construction"), this.invokeInt(snapshot, "ammunition"), this.invokeBoolean(snapshot, "habOperational"), this.invokeDouble(snapshot, "buildRadius"), this.invokeDouble(snapshot, "exclusionRadius"), 0L));
        }
    }

    private void appendEspetroApiRallies(ServerPlayer player, String visibleTeam, List<SyncBastionsMessage.BastionInfo> output) throws ReflectiveOperationException {
        Class<?> apiClass = Class.forName(ESPETRO_API_CLASS);
        Object result = apiClass.getMethod("getRallies", new Class[0]).invoke(null, new Object[0]);
        if (!(result instanceof Iterable)) {
            return;
        }
        Iterable snapshots = (Iterable)result;
        String dimension = player.m_284548_().m_46472_().m_135782_().toString();
        for (Object snapshot : snapshots) {
            if (snapshot == null || !visibleTeam.equals(this.invokeString(snapshot, "team")) || !dimension.equals(this.invokeString(snapshot, "dimension"))) continue;
            int squadId = this.invokeInt(snapshot, "squadId");
            output.add(new SyncBastionsMessage.BastionInfo("Rally " + squadId, this.invokeString(snapshot, "team"), new BlockPos(this.invokeInt(snapshot, "x"), this.invokeInt(snapshot, "y"), this.invokeInt(snapshot, "z")), "RALLY", 0, 0, true, 0.0, 0.0, System.currentTimeMillis() + ((Number)snapshot.getClass().getMethod("nextWaveSeconds", new Class[0]).invoke(snapshot, new Object[0])).longValue() * 1000L));
        }
    }

    private String invokeString(Object target, String name) throws ReflectiveOperationException {
        return String.valueOf(target.getClass().getMethod(name, new Class[0]).invoke(target, new Object[0]));
    }

    @Nullable
    private String invokeOptionalString(Object target, String name) {
        try {
            Object value = target.getClass().getMethod(name, new Class[0]).invoke(target, new Object[0]);
            return value == null ? null : String.valueOf(value);
        }
        catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private boolean invokeOptionalBoolean(Object target, String name) {
        return this.invokeOptionalBoolean(target, name, false);
    }

    private boolean invokeOptionalBoolean(Object target, String name, boolean fallback) {
        try {
            return Boolean.TRUE.equals(target.getClass().getMethod(name, new Class[0]).invoke(target, new Object[0]));
        }
        catch (ReflectiveOperationException ignored) {
            return fallback;
        }
    }

    private int invokeInt(Object target, String name) throws ReflectiveOperationException {
        return ((Number)target.getClass().getMethod(name, new Class[0]).invoke(target, new Object[0])).intValue();
    }

    private double invokeDouble(Object target, String name) throws ReflectiveOperationException {
        return ((Number)target.getClass().getMethod(name, new Class[0]).invoke(target, new Object[0])).doubleValue();
    }

    private boolean invokeBoolean(Object target, String name) throws ReflectiveOperationException {
        return Boolean.TRUE.equals(target.getClass().getMethod(name, new Class[0]).invoke(target, new Object[0]));
    }

    private int invokeOptionalIntGetter(Object target, String name) {
        try {
            return ((Number)target.getClass().getMethod(name, new Class[0]).invoke(target, new Object[0])).intValue();
        }
        catch (ClassCastException | ReflectiveOperationException ignored) {
            return 0;
        }
    }

    private boolean invokeOptionalBooleanGetter(Object target, String name, boolean fallback) {
        try {
            return Boolean.TRUE.equals(target.getClass().getMethod(name, new Class[0]).invoke(target, new Object[0]));
        }
        catch (ReflectiveOperationException ignored) {
            return fallback;
        }
    }

    private List<SyncBastionsMessage.BaseInfo> getVisibleEspetroBases(ServerPlayer player) {
        ArrayList<SyncBastionsMessage.BaseInfo> bases = new ArrayList<SyncBastionsMessage.BaseInfo>();
        if (!ModList.get().isLoaded(ESPETRO_MOD_ID)) {
            return bases;
        }
        try {
            String visibleTeam = this.getVisibleEspetroBastionTeam(player);
            if (visibleTeam == null || visibleTeam.isEmpty()) {
                return bases;
            }
            Class<?> spawnPointConfigClass = Class.forName(ESPETRO_SPAWN_POINT_CONFIG_CLASS);
            Method getAllSpawnPointsMethod = spawnPointConfigClass.getMethod("getAllSpawnPoints", new Class[0]);
            Object result = getAllSpawnPointsMethod.invoke(null, new Object[0]);
            if (!(result instanceof Map)) {
                return bases;
            }
            Map spawnPoints = (Map)result;
            for (Map.Entry entry : spawnPoints.entrySet()) {
                Object spawnPoint;
                String team = EspetroTeamBridge.canonicalizeTeamName(String.valueOf(entry.getKey()));
                if (team == null || !visibleTeam.equals(team) || (spawnPoint = entry.getValue()) == null) continue;
                double x = this.readDoubleField(spawnPoint, "x");
                double y = this.readDoubleField(spawnPoint, "y");
                double z = this.readDoubleField(spawnPoint, "z");
                float yaw = this.readFloatField(spawnPoint, "yaw");
                BlockPos pos = BlockPos.m_274561_((double)x, (double)y, (double)z);
                bases.add(new SyncBastionsMessage.BaseInfo(EspetroTeamBridge.displayName(team) + "\u4e3b\u57fa\u5730", team, pos, yaw));
            }
        }
        catch (Exception e) {
            ModLogger.warn("\u540c\u6b65 Espetro \u4e3b\u57fa\u5730\u4fe1\u606f\u5931\u8d25\uff0c\u5df2\u8df3\u8fc7: " + e.getMessage());
        }
        return bases;
    }

    private List<SyncBastionsMessage.VehicleSupplyStationInfo> getVisibleEspetroVehicleSupplyStations(ServerPlayer player) {
        ArrayList<SyncBastionsMessage.VehicleSupplyStationInfo> stations = new ArrayList<SyncBastionsMessage.VehicleSupplyStationInfo>();
        if (!ModList.get().isLoaded(ESPETRO_MOD_ID)) {
            return stations;
        }
        try {
            String visibleTeam = this.getVisibleEspetroBastionTeam(player);
            if (visibleTeam == null || visibleTeam.isEmpty()) {
                return stations;
            }
            for (SyncBastionsMessage.VehicleSupplyStationInfo station : this.getAllEspetroVehicleSupplyStations(player.m_20194_())) {
                if (!visibleTeam.equals(station.getTeam())) continue;
                stations.add(station);
            }
        }
        catch (Exception e) {
            ModLogger.warn("\u540c\u6b65 Espetro \u8f7d\u5177\u8865\u7ed9\u7ad9\u4fe1\u606f\u5931\u8d25\uff0c\u5df2\u8df3\u8fc7: " + e.getMessage());
        }
        return stations;
    }

    private List<SyncBastionsMessage.VehicleSupplyStationInfo> getAllEspetroVehicleSupplyStations(MinecraftServer server) {
        if (server == null) {
            return List.of();
        }
        String activeDimension = EspetroAPI.getActiveBattlefieldDimension().map(key -> key.m_135782_().toString()).orElse("");
        return EspetroAPI.getTacticalMapStateSnapshot().vehicleSupplyStations().stream().filter(station -> activeDimension.equals(station.dimension())).map(station -> new SyncBastionsMessage.VehicleSupplyStationInfo(station.name(), station.team(), new BlockPos(station.x(), station.y(), station.z()))).sorted(Comparator.comparing(SyncBastionsMessage.VehicleSupplyStationInfo::getName).thenComparingInt(info -> info.getPos().m_123341_()).thenComparingInt(info -> info.getPos().m_123343_())).toList();
    }

    private boolean isEspetroVehicleSupplyStationEntity(Entity entity) {
        if (entity.m_19880_().contains(ESPETRO_VEHICLE_SUPPLY_STATION_TAG)) {
            return true;
        }
        CompoundTag data = entity.getPersistentData();
        if (data.m_128441_(ESPETRO_VEHICLE_SUPPLY_STATION_TEAM_KEY) || data.m_128441_(ESPETRO_VEHICLE_SUPPLY_STATION_ID_KEY) || data.m_128441_(ESPETRO_VEHICLE_SUPPLY_STATION_X_KEY) || data.m_128441_(ESPETRO_VEHICLE_SUPPLY_STATION_Y_KEY) || data.m_128441_(ESPETRO_VEHICLE_SUPPLY_STATION_Z_KEY)) {
            return true;
        }
        Component customName = entity.m_7770_();
        return customName != null && customName.getString().contains("\u8f7d\u5177\u8865\u7ed9\u7ad9");
    }

    private String getEspetroVehicleSupplyStationTeam(Entity entity) {
        CompoundTag data = entity.getPersistentData();
        String team = EspetroTeamBridge.canonicalizeTeamName(data.m_128461_(ESPETRO_VEHICLE_SUPPLY_STATION_TEAM_KEY));
        if (team != null) {
            return team;
        }
        for (String tag : entity.m_19880_()) {
            String fromSupplyPrefix = this.readTeamSuffix(tag, "espetro_vehicle_supply_station_team_");
            if (fromSupplyPrefix != null) {
                return fromSupplyPrefix;
            }
            String fromTeamPrefix = this.readTeamSuffix(tag, "espetro_team_");
            if (fromTeamPrefix == null) continue;
            return fromTeamPrefix;
        }
        return this.inferEspetroVehicleSupplyStationTeam(this.getEspetroVehicleSupplyStationPosition(entity));
    }

    private String inferEspetroVehicleSupplyStationTeam(BlockPos stationPos) {
        String nearestBaseTeam = this.findNearestEspetroBaseTeam(stationPos);
        if (nearestBaseTeam != null) {
            return nearestBaseTeam;
        }
        return this.findNearestEspetroBastionTeam(stationPos);
    }

    private String findNearestEspetroBaseTeam(BlockPos stationPos) {
        try {
            Class<?> spawnPointConfigClass = Class.forName(ESPETRO_SPAWN_POINT_CONFIG_CLASS);
            Method getAllSpawnPointsMethod = spawnPointConfigClass.getMethod("getAllSpawnPoints", new Class[0]);
            Object result = getAllSpawnPointsMethod.invoke(null, new Object[0]);
            if (!(result instanceof Map)) {
                return null;
            }
            Map spawnPoints = (Map)result;
            String nearestTeam = null;
            double nearestDistanceSquared = Double.MAX_VALUE;
            for (Map.Entry entry : spawnPoints.entrySet()) {
                BlockPos basePos;
                double distanceSquared;
                String team = EspetroTeamBridge.canonicalizeTeamName(String.valueOf(entry.getKey()));
                Object spawnPoint = entry.getValue();
                if (team == null || spawnPoint == null || !((distanceSquared = this.horizontalDistanceSquared(stationPos, basePos = BlockPos.m_274561_((double)this.readDoubleField(spawnPoint, "x"), (double)this.readDoubleField(spawnPoint, "y"), (double)this.readDoubleField(spawnPoint, "z")))) < nearestDistanceSquared)) continue;
                nearestDistanceSquared = distanceSquared;
                nearestTeam = team;
            }
            return nearestTeam;
        }
        catch (Exception ignored) {
            return null;
        }
    }

    private String findNearestEspetroBastionTeam(BlockPos stationPos) {
        try {
            Class<?> managerClass = Class.forName(ESPETRO_BASTION_MANAGER_CLASS);
            Object manager = managerClass.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            Method getTeamBastionsMethod = managerClass.getMethod("getTeamBastions", String.class);
            String nearestTeam = null;
            double nearestDistanceSquared = Double.MAX_VALUE;
            for (String candidateTeam : List.of("ATTACK", "DEFEND")) {
                Object result = getTeamBastionsMethod.invoke(manager, candidateTeam);
                if (!(result instanceof Iterable)) continue;
                Iterable bastions = (Iterable)result;
                for (Object bastion : bastions) {
                    double distanceSquared;
                    BlockPos bastionPos;
                    if (bastion == null || !this.isEspetroBastionActive(bastion) || (bastionPos = this.getEspetroBastionPosition(managerClass, manager, bastion)) == null) continue;
                    String team = EspetroTeamBridge.canonicalizeTeamName(String.valueOf(bastion.getClass().getMethod("getTeam", new Class[0]).invoke(bastion, new Object[0])));
                    if (team == null) {
                        team = candidateTeam;
                    }
                    if (!((distanceSquared = this.horizontalDistanceSquared(stationPos, bastionPos)) < nearestDistanceSquared)) continue;
                    nearestDistanceSquared = distanceSquared;
                    nearestTeam = team;
                }
            }
            return nearestTeam;
        }
        catch (Exception ignored) {
            return null;
        }
    }

    private double horizontalDistanceSquared(BlockPos first, BlockPos second) {
        double dx = first.m_123341_() - second.m_123341_();
        double dz = first.m_123343_() - second.m_123343_();
        return dx * dx + dz * dz;
    }

    private String readTeamSuffix(String tag, String prefix) {
        if (tag == null || !tag.startsWith(prefix)) {
            return null;
        }
        return EspetroTeamBridge.canonicalizeTeamName(tag.substring(prefix.length()));
    }

    private String getEspetroVehicleSupplyStationId(Entity entity) {
        CompoundTag data = entity.getPersistentData();
        if (data.m_128403_(ESPETRO_VEHICLE_SUPPLY_STATION_ID_KEY)) {
            return data.m_128342_(ESPETRO_VEHICLE_SUPPLY_STATION_ID_KEY).toString();
        }
        String stringId = data.m_128461_(ESPETRO_VEHICLE_SUPPLY_STATION_ID_KEY);
        if (!stringId.isBlank()) {
            return stringId;
        }
        String tagPrefix = "espetro_vehicle_supply_station_id_";
        for (String tag : entity.m_19880_()) {
            if (tag == null || !tag.startsWith(tagPrefix)) continue;
            return tag.substring(tagPrefix.length());
        }
        return null;
    }

    private BlockPos getEspetroVehicleSupplyStationPosition(Entity entity) {
        CompoundTag data = entity.getPersistentData();
        if (data.m_128441_(ESPETRO_VEHICLE_SUPPLY_STATION_X_KEY) && data.m_128441_(ESPETRO_VEHICLE_SUPPLY_STATION_Y_KEY) && data.m_128441_(ESPETRO_VEHICLE_SUPPLY_STATION_Z_KEY)) {
            return new BlockPos(data.m_128451_(ESPETRO_VEHICLE_SUPPLY_STATION_X_KEY), data.m_128451_(ESPETRO_VEHICLE_SUPPLY_STATION_Y_KEY), data.m_128451_(ESPETRO_VEHICLE_SUPPLY_STATION_Z_KEY));
        }
        return entity.m_20183_();
    }

    private String getEspetroVehicleSupplyStationName(Entity entity) {
        Component customName = entity.m_7770_();
        if (customName != null && !customName.getString().isBlank()) {
            return customName.getString();
        }
        return "\u8f7d\u5177\u8865\u7ed9\u7ad9";
    }

    private double readDoubleField(Object target, String fieldName) throws ReflectiveOperationException {
        double d;
        Object value = target.getClass().getField(fieldName).get(target);
        if (value instanceof Number) {
            Number number = (Number)value;
            d = number.doubleValue();
        } else {
            d = 0.0;
        }
        return d;
    }

    private float readFloatField(Object target, String fieldName) throws ReflectiveOperationException {
        float f;
        Object value = target.getClass().getField(fieldName).get(target);
        if (value instanceof Number) {
            Number number = (Number)value;
            f = number.floatValue();
        } else {
            f = 0.0f;
        }
        return f;
    }

    private String getVisibleEspetroBastionTeam(ServerPlayer player) {
        return EspetroTeamBridge.getServerPlayerTeam(player);
    }

    private String getEspetroBastionTeamFromHcrTeamName(String teamName) {
        return EspetroTeamBridge.canonicalizeTeamName(teamName);
    }

    private boolean isEspetroBastionActive(Object bastion) throws ReflectiveOperationException {
        try {
            Method method = bastion.getClass().getMethod("isActive", new Class[0]);
            Object result = method.invoke(bastion, new Object[0]);
            return !(result instanceof Boolean) || (Boolean)result != false;
        }
        catch (NoSuchMethodException e) {
            return true;
        }
    }

    private BlockPos getEspetroBastionPosition(Class<?> managerClass, Object manager, Object bastion) throws ReflectiveOperationException {
        Object result;
        Method recordedPositionMethod = this.findEspetroBastionPositionMethod(managerClass, bastion.getClass());
        if (recordedPositionMethod != null && (result = recordedPositionMethod.invoke(manager, bastion)) instanceof BlockPos) {
            return (BlockPos)result;
        }
        BlockPos armorStandPosition = this.invokeOptionalBlockPosGetter(bastion, "getArmorStandPosition");
        if (armorStandPosition != null) {
            return armorStandPosition;
        }
        BlockPos basePosition = this.invokeOptionalBlockPosGetter(bastion, "getPosition");
        return basePosition != null ? basePosition.m_7494_() : null;
    }

    private Method findEspetroBastionPositionMethod(Class<?> managerClass, Class<?> bastionClass) {
        for (Method method : managerClass.getMethods()) {
            Class<?> parameterType;
            if (!"getRecordedArmorStandPosition".equals(method.getName()) || method.getParameterCount() != 1 || !(parameterType = method.getParameterTypes()[0]).isAssignableFrom(bastionClass)) continue;
            return method;
        }
        return null;
    }

    private BlockPos invokeOptionalBlockPosGetter(Object target, String methodName) throws ReflectiveOperationException {
        Method method;
        try {
            method = target.getClass().getMethod(methodName, new Class[0]);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
        Object result = method.invoke(target, new Object[0]);
        return result instanceof BlockPos ? (BlockPos)result : null;
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer)event.getEntity();
            this.playerNameMap.put(player.m_20148_(), player.m_7755_().getString());
            String playerTeam = Optional.ofNullable(EspetroTeamBridge.getServerPlayerTeam(player)).orElse("");
            this.playerTeamNameMap.put(player.m_20148_(), playerTeam);
            SyncConfigMessage.sendToPlayer(player);
            SyncMapPlayerDisplayMessage.sendToPlayer(player);
            SyncTacticalMapConfigMessage.sendToPlayer(player);
            SyncTacticalMapBackgroundMessage.sendToPlayer(player);
            this.syncToClient(player);
            TacticalMarkerManager.sendTo(player);
            SyncOperationModeMessage.sendToPlayer(player, this.operationModeRunning, this.currentBatch, this.totalBatches, this.endBehavior, this.teamRoles, this.teamReinforcements, this.teamInitialReinforcements);
            if (!this.isPlayerInTeam(player)) {
                ModLogger.info("\u73a9\u5bb6 " + player.m_7755_().getString() + " \u672a\u52a0\u5165 Espetro \u9635\u8425\uff0c\u88ab\u89c6\u4e3a\u89c2\u6218\u8005");
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        RequestTacticalMapTileMessage.clearPlayer(player.m_20148_());
        RequestRateLimiter.clearPlayer(player.m_20148_());
        this.playerNameMap.remove(player.m_20148_());
        this.playerTeamNameMap.remove(player.m_20148_());
        this.playerEnterTimeByPoint.remove(player.m_20148_());
        this.lastBastionSyncByPlayer.remove(player.m_20148_());
        this.tacticalMapSubscriptions.remove(player.m_20148_());
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        UUID playerId = player.m_20148_();
        RequestTacticalMapTileMessage.clearPlayer(playerId);
        RequestRateLimiter.clearPlayer(playerId);
        this.tacticalMapSubscriptions.remove(playerId);
        this.lastBastionSyncByPlayer.remove(playerId);
        this.playerEnterTimeByPoint.remove(playerId);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        this.playerNameMap.put(player.m_20148_(), player.m_7755_().getString());
        this.playerTeamNameMap.put(player.m_20148_(), Optional.ofNullable(EspetroTeamBridge.getPlayerTeam(player)).orElse(""));
    }

    private void syncMapDataIfPlayerTeamChanged(ServerPlayer player, String currentTeamName) {
        UUID playerUUID = player.m_20148_();
        String previousTeamName = this.playerTeamNameMap.put(playerUUID, currentTeamName);
        if (previousTeamName != null && !previousTeamName.equals(currentTeamName)) {
            ModLogger.info("\u73a9\u5bb6 " + player.m_7755_().getString() + " \u961f\u4f0d\u4ece " + previousTeamName + " \u53d8\u66f4\u4e3a " + currentTeamName + "\uff0c\u91cd\u65b0\u540c\u6b65\u6218\u672f\u5730\u56fe\u636e\u70b9\u89c6\u91ce");
            this.syncToClient(player);
            this.syncPlayerPositionsToPlayer(player);
            this.syncEspetroBastionsToPlayer(player);
            TacticalMarkerManager.sendTo(player);
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.side != LogicalSide.SERVER || event.phase != TickEvent.Phase.END) {
            return;
        }
        MinecraftServer server = ESPointsMod.getServer();
        if (server == null || server.m_6846_().m_11314_().isEmpty()) {
            return;
        }
        TacticalMarkerManager.tick();
        TacticalMapTileService.get().tick(server);
        Optional battlefield = EspetroAPI.getActiveBattlefieldLevel((MinecraftServer)server);
        if (battlefield.isEmpty()) {
            return;
        }
        int configuredCheckInterval = Math.max(1, (Integer)ModConfig.checkInterval.get());
        if (++this.captureCheckTimer >= configuredCheckInterval) {
            int elapsedTicks = this.captureCheckTimer;
            this.captureCheckTimer = 0;
            this.updateAllCapturePoints((Level)battlefield.get(), elapsedTicks);
        }
        this.tickRaasTicketBleed();
        if (++this.playerPositionSyncTimer >= 10) {
            this.playerPositionSyncTimer = 0;
            this.syncPlayerPositions();
        }
        if (++this.bastionSyncTimer >= 40) {
            this.bastionSyncTimer = 0;
            this.syncBastionsToBattlefieldPlayers();
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof ServerPlayer) {
            ServerPlayer victim = (ServerPlayer)livingEntity;
            Entity entity = event.getSource().m_7639_();
            if (entity instanceof ServerPlayer) {
                ServerPlayer killer = (ServerPlayer)entity;
                if (!victim.m_20148_().equals(killer.m_20148_())) {
                    if (this.isFriendlyFire(killer, victim)) {
                        this.handleFriendlyFire(killer, victim);
                    } else {
                        int rewardAmount = (Integer)ModConfig.killRewardAmount.get();
                        this.addPointsToPlayer(killer, rewardAmount, "\u51fb\u6740\u73a9\u5bb6\uff1a" + victim.m_7755_().getString());
                        ModLogger.info("\u73a9\u5bb6 " + killer.m_7755_().getString() + " \u51fb\u6740\u4e86 " + victim.m_7755_().getString() + "\uff0c\u83b7\u5f97\u4e86 " + rewardAmount + " \u70b9\u6570");
                    }
                }
            }
        }
    }

    private boolean isFriendlyFire(ServerPlayer killer, ServerPlayer victim) {
        return EspetroTeamBridge.isSameTeam(EspetroTeamBridge.getServerPlayerTeam(killer), EspetroTeamBridge.getServerPlayerTeam(victim));
    }

    private void handleFriendlyFire(ServerPlayer killer, ServerPlayer victim) {
        if (!((Boolean)ModConfig.enableFriendlyFirePenalty.get()).booleanValue()) {
            return;
        }
        int penaltyAmount = (Integer)ModConfig.friendlyFirePenalty.get();
        this.removePointsFromPlayer(killer, penaltyAmount, "\u53cb\u519b\u51fb\u6740\uff1a" + victim.m_7755_().getString());
        ModLogger.info("\u73a9\u5bb6 " + killer.m_7755_().getString() + " \u51fb\u6740\u4e86\u53cb\u519b " + victim.m_7755_().getString() + "\uff0c\u6263\u9664\u4e86 " + penaltyAmount + " \u70b9\u6570");
    }

    private void removePointsFromPlayer(ServerPlayer player, int points, String reason) {
        if (player == null || points <= 0) {
            ModLogger.warn("\u65e0\u6548\u7684\u53c2\u6570\uff1aplayer=" + String.valueOf(player) + ", points=" + points);
            return;
        }
        if (player.m_9236_().m_5776_()) {
            ModLogger.warn("\u5c1d\u8bd5\u5728\u5ba2\u6237\u7aef\u8c03\u7528PlayerPointsAPI\uff0c\u8fd9\u4e0d\u4f1a\u751f\u6548");
            return;
        }
        if (OptionalPointsIntegration.remove((Player)player, points)) {
            player.m_213846_((Component)Component.m_237113_((String)("\u4f60\u56e0\u4e3a" + reason + "\u88ab\u6263\u4e86" + points + "\u70b9\uff01")));
            ModLogger.info("\u4e3a\u73a9\u5bb6 " + player.m_7755_().getString() + " \u6263\u9664\u4e86 " + points + " \u70b9\u6570\uff0c\u539f\u56e0\uff1a" + reason);
        }
    }

    public void syncToClient(ServerPlayer player) {
        if (!ESPointsMod.isServerRunning()) {
            ModLogger.warn("\u5c1d\u8bd5\u5728\u5ba2\u6237\u7aef\u8c03\u7528\u670d\u52a1\u5668\u540c\u6b65\u65b9\u6cd5\uff0c\u5ffd\u7565");
            return;
        }
        try {
            SyncCapturePointsMessage.sendToPlayer(player, this.getSerializablePointsForMap(player));
        }
        catch (Exception e) {
            ModLogger.error("\u540c\u6b65\u636e\u70b9\u6570\u636e\u5230\u5ba2\u6237\u7aef\u65f6\u53d1\u751f\u5f02\u5e38: " + e.getMessage());
        }
    }

    public String getPlayerName(UUID playerUUID) {
        return this.playerNameMap.getOrDefault(playerUUID, "Unknown");
    }

    private void giveCaptureReward(MinecraftServer server, String captorName, String pointName) {
        if (server == null || captorName == null || captorName.isEmpty()) {
            return;
        }
        int rewardAmount = (Integer)ModConfig.captureRewardAmount.get();
        String canonicalCaptor = EspetroTeamBridge.canonicalizeTeamName(captorName);
        if (canonicalCaptor == null) {
            return;
        }
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            if (!this.isPlayerInTeam(player) || !canonicalCaptor.equals(EspetroTeamBridge.getServerPlayerTeam(player))) continue;
            this.addPointsToPlayer(player, rewardAmount, "\u5360\u9886\u636e\u70b9\uff1a" + pointName);
        }
    }

    private boolean isPlayerInTeam(ServerPlayer player) {
        return EspetroTeamBridge.isEspetroTeamPlayer((Player)player);
    }

    private void giveCapturedReward(MinecraftServer server, String captorName, String pointName) {
        if (server == null || captorName == null || captorName.isEmpty()) {
            return;
        }
        int rewardAmount = (Integer)ModConfig.capturedRewardAmount.get();
        String canonicalCaptor = EspetroTeamBridge.canonicalizeTeamName(captorName);
        if (canonicalCaptor == null) {
            return;
        }
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            if (!this.isPlayerInTeam(player) || !canonicalCaptor.equals(EspetroTeamBridge.getServerPlayerTeam(player))) continue;
            this.addPointsToPlayer(player, rewardAmount, "\u6301\u7eed\u5360\u9886\u636e\u70b9\uff1a" + pointName);
        }
    }

    private void addPointsToPlayer(ServerPlayer player, int points) {
        this.addPointsToPlayer(player, points, "API\u8c03\u7528");
    }

    private void addPointsToPlayer(ServerPlayer player, int points, String reason) {
        if (player == null || points <= 0) {
            ModLogger.warn("\u65e0\u6548\u7684\u53c2\u6570\uff1aplayer=" + String.valueOf(player) + ", points=" + points);
            return;
        }
        if (player.m_9236_().m_5776_()) {
            ModLogger.warn("\u5c1d\u8bd5\u5728\u5ba2\u6237\u7aef\u8c03\u7528PlayerPointsAPI\uff0c\u8fd9\u4e0d\u4f1a\u751f\u6548");
            return;
        }
        if (OptionalPointsIntegration.add((Player)player, points, reason)) {
            ModLogger.info("\u4e3a\u73a9\u5bb6 " + player.m_7755_().getString() + " \u6dfb\u52a0\u4e86 " + points + " \u70b9\u6570\uff0c\u539f\u56e0\uff1a" + reason);
        }
    }

    private static class PlannedCapturePoint {
        private final String name;
        private final BlockPos pos1;
        private final BlockPos pos2;
        private final int batch;

        public PlannedCapturePoint(String name, BlockPos pos1, BlockPos pos2, int batch) {
            this.name = name;
            this.pos1 = pos1;
            this.pos2 = pos2;
            this.batch = batch;
        }

        public String getName() {
            return this.name;
        }

        public BlockPos getPos1() {
            return this.pos1;
        }

        public BlockPos getPos2() {
            return this.pos2;
        }

        public int getBatch() {
            return this.batch;
        }
    }

    private static class CapturedInfo {
        private final String captorName;
        private final long captureTime;
        private long lastRewardTime;

        public CapturedInfo(String captorName, long captureTime) {
            this.captorName = captorName;
            this.captureTime = captureTime;
            this.lastRewardTime = captureTime;
        }

        public String getCaptorName() {
            return this.captorName;
        }

        public long getCaptureTime() {
            return this.captureTime;
        }

        public long getLastRewardTime() {
            return this.lastRewardTime;
        }

        public void setLastRewardTime(long lastRewardTime) {
            this.lastRewardTime = lastRewardTime;
        }
    }

    private static final class MapPositionSample {
        private final double x;
        private final double y;
        private final double z;
        private final float yaw;

        private MapPositionSample(double x, double y, double z, float yaw) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
        }
    }

    private record BastionSyncState(List<SyncBastionsMessage.BastionInfo> bastions, List<SyncBastionsMessage.BaseInfo> bases, List<SyncBastionsMessage.VehicleSupplyStationInfo> vehicleSupplyStations) {
    }
}

