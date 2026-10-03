/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.api;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.espetro.Espetro;
import org.espetro.api.ActiveBattlefieldSnapshot;
import org.espetro.api.TacticalMapStateSnapshot;
import org.espetro.audio.FactionAudioCoordinator;
import org.espetro.bastion.BastionData;
import org.espetro.bastion.BastionManager;
import org.espetro.logistics.LogisticsConfig;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.network.NetworkManager;
import org.espetro.ping.VehicleSeatPingCache;
import org.espetro.stats.PlayerMatchStatsManager;
import org.espetro.team.ClassCountManager;
import org.espetro.team.CommanderSkillManager;
import org.espetro.team.Fireteam;
import org.espetro.team.GameStateManager;
import org.espetro.team.SpawnPointConfig;
import org.espetro.team.SquadManager;
import org.espetro.team.TeamDisplayNames;
import org.espetro.team.TeamPackManager;
import org.espetro.team.TroopCountManager;
import org.espetro.team.VoteManager;
import org.espetro.vehicle.VehicleManager;

public class EspetroAPI {
    private static ActiveMapConfig cachedActiveMap;
    private static ActiveBattlefieldSnapshot cachedBattlefieldSnapshot;
    private static long tacticalRevision;
    private static long tacticalDirtyRevision;
    private static long tacticalBuiltRevision;
    private static long tacticalSnapshotSession;
    private static TacticalMapStateSnapshot lastTacticalSnapshot;

    public static synchronized void markTacticalMapStateDirty() {
        tacticalDirtyRevision = tacticalDirtyRevision == Long.MAX_VALUE ? 1L : tacticalDirtyRevision + 1L;
    }

    public static Optional<String> getActiveMapId() {
        return BattlefieldContext.get().map(map -> map.mapFolder);
    }

    public static Optional<String> getActiveMapName() {
        return BattlefieldContext.get().map(map -> map.displayName);
    }

    public static Optional<ResourceKey<Level>> getActiveBattlefieldDimension() {
        return BattlefieldContext.getActiveDimensionKey();
    }

    public static boolean isActiveBattlefield(ServerLevel level) {
        return BattlefieldContext.isActiveBattlefield(level);
    }

    public static Optional<ServerLevel> getActiveBattlefieldLevel(MinecraftServer server) {
        if (server == null) {
            return Optional.empty();
        }
        return BattlefieldContext.getActiveDimensionKey().map(server::m_129880_);
    }

    public static synchronized Optional<ActiveBattlefieldSnapshot> getActiveBattlefieldSnapshot() {
        ActiveMapConfig active = BattlefieldContext.getOrNull();
        if (active == null) {
            cachedActiveMap = null;
            cachedBattlefieldSnapshot = null;
            return Optional.empty();
        }
        if (active != cachedActiveMap || cachedBattlefieldSnapshot == null) {
            cachedActiveMap = active;
            cachedBattlefieldSnapshot = EspetroAPI.toPublicSnapshot(active);
        }
        return Optional.of(cachedBattlefieldSnapshot);
    }

    public static boolean endRound(String winner) {
        return GameStateManager.getInstance().endRound(winner);
    }

    public static boolean onCapturePointCaptured(String capturingTeam) {
        return FactionAudioCoordinator.broadcastCapture(capturingTeam);
    }

    public static boolean onCapturePointNeutralized(String originalOwnerTeam, String attackingTeam) {
        return FactionAudioCoordinator.broadcastNeutralized(originalOwnerTeam, attackingTeam);
    }

    public static Optional<PlayerMatchStatsManager.PlayerMatchStats> getPlayerMatchStats(UUID playerId) {
        return PlayerMatchStatsManager.getInstance().get(playerId);
    }

    public static Optional<String> getSquadCategory(UUID playerId) {
        return Optional.ofNullable(SquadManager.getInstance().getPlayerCategoryId(playerId));
    }

    public static String getPlayerTeam(ServerPlayer player) {
        return Espetro.getPlayerTeam(player);
    }

    public static boolean isPlayerVisibleOnTacticalMap(ServerPlayer player) {
        if (player == null || !player.m_6084_() || player.m_5833_() || !BattlefieldContext.isActiveBattlefield(player.m_284548_()) || BastionManager.getInstance().isWaitingForBastion(player.m_20148_())) {
            return false;
        }
        String team = ClassCountManager.getInstance().getPlayerTeam(player.m_20148_());
        if (team == null) {
            team = Espetro.getPlayerTeam(player);
        }
        return "ATTACK".equals(team) || "DEFEND".equals(team);
    }

    public static String getPlayerFaction(UUID playerId) {
        return ClassCountManager.getInstance().getPlayerFaction(playerId);
    }

    public static String getPlayerClass(UUID playerId) {
        return ClassCountManager.getInstance().getPlayerClass(playerId);
    }

    public static String getPlayerClassVariant(UUID playerId) {
        return ClassCountManager.getInstance().getPlayerVariant(playerId);
    }

    public static int getClassSwitchCooldownRemaining(UUID playerId) {
        return ClassCountManager.getInstance().getClassSwitchCooldownRemaining(playerId);
    }

    public static Map<String, Integer> getClassCounts(String team, String factionId) {
        return ClassCountManager.getInstance().getCountsForFaction(team, factionId);
    }

    public static Map<String, Map<String, Integer>> getClassVariantCounts(String team, String factionId) {
        return ClassCountManager.getInstance().getVariantCountsForFaction(team, factionId);
    }

    public static boolean selectPlayerClass(ServerPlayer player, String classId, String variantId) {
        return player != null && ClassCountManager.getInstance().selectClass(player, classId, variantId);
    }

    public static int getPlayerSquadId(UUID playerId) {
        return SquadManager.getInstance().getPlayerSquadId(playerId);
    }

    public static boolean isSquadLeader(UUID playerId) {
        String team = EspetroAPI.getPlayerTeamById(playerId);
        if (team == null) {
            return false;
        }
        int squadId = EspetroAPI.getPlayerSquadId(playerId);
        if (squadId == -1) {
            return false;
        }
        for (SquadManager.SquadSnapshot squad : SquadManager.getInstance().getSquadSnapshots(team)) {
            if (squad.id != squadId) continue;
            return squad.members.stream().anyMatch(m -> m.uuid.equals(playerId) && m.leader);
        }
        return false;
    }

    public static boolean isFireteamLeader(UUID playerId) {
        return playerId != null && SquadManager.getInstance().isFireteamLeader(playerId);
    }

    public static Fireteam getPlayerFireteam(UUID playerId) {
        return playerId == null ? null : SquadManager.getInstance().getPlayerFireteam(playerId);
    }

    public static boolean canPlacePing(UUID playerId) {
        if (playerId == null) {
            return false;
        }
        if (EspetroAPI.isCommander(playerId) || EspetroAPI.isSquadLeader(playerId) || EspetroAPI.isFireteamLeader(playerId)) {
            return true;
        }
        return VehicleSeatPingCache.canPingFromVehicle(playerId);
    }

    public static boolean canPlacePing(ServerPlayer player) {
        if (player == null || EspetroAPI.getPlayerTeam(player) == null) {
            return false;
        }
        UUID playerId = player.m_20148_();
        return EspetroAPI.isCommander(playerId) || EspetroAPI.isSquadLeader(playerId) || EspetroAPI.isFireteamLeader(playerId) || VehicleSeatPingCache.canPingFromVehicle(player);
    }

    public static boolean isCommander(UUID playerId) {
        return VoteManager.getInstance().isCommander(playerId);
    }

    public static boolean submitArtillerySupportTarget(ServerPlayer commander, double x, double z) {
        return CommanderSkillManager.getInstance().submitArtillerySupportTarget(commander, x, z);
    }

    public static boolean submitCommanderSkillTarget(ServerPlayer commander, double x, double z) {
        return CommanderSkillManager.getInstance().submitArtillerySupportTarget(commander, x, z);
    }

    public static CommanderSkillManager.ArtillerySupportRequest getLatestArtillerySupportRequest() {
        return CommanderSkillManager.getInstance().getLatestArtillerySupportRequest();
    }

    public static CommanderSkillManager.ArtillerySupportRequest getLatestCommanderSkillTargetRequest() {
        return CommanderSkillManager.getInstance().getLatestCommanderSkillTargetRequest();
    }

    public static List<CommanderSkillManager.ArtillerySupportRequest> getArtillerySupportRequests() {
        return CommanderSkillManager.getInstance().getArtillerySupportRequestsSnapshot();
    }

    public static List<CommanderSkillManager.ArtillerySupportRequest> getCommanderSkillTargetRequests() {
        return CommanderSkillManager.getInstance().getCommanderSkillTargetRequestsSnapshot();
    }

    public static List<CommanderSkillManager.ArtillerySupportRequest> drainArtillerySupportRequests() {
        return CommanderSkillManager.getInstance().drainArtillerySupportRequests();
    }

    public static List<CommanderSkillManager.ArtillerySupportRequest> drainCommanderSkillTargetRequests() {
        return CommanderSkillManager.getInstance().drainCommanderSkillTargetRequests();
    }

    public static int getCommanderSkillCooldown(ServerPlayer commander, String skillId) {
        return commander == null ? 0 : CommanderSkillManager.getInstance().getRemainingCooldownSeconds(commander.m_20148_(), skillId);
    }

    public static Map<String, Integer> getCommanderSkillCooldowns(ServerPlayer commander) {
        return commander == null ? Map.of() : CommanderSkillManager.getInstance().getCooldownData(commander.m_20148_());
    }

    public static boolean isCommanderSkillOnCooldown(ServerPlayer commander, String skillId) {
        return commander != null && CommanderSkillManager.getInstance().isOnCooldown(commander.m_20148_(), skillId);
    }

    public static CommanderSkillManager.SkillStatus getCommanderSkillStatus(ServerPlayer commander, String skillId) {
        return CommanderSkillManager.getInstance().getSkillStatus(commander, skillId);
    }

    public static boolean canUseCommanderSkill(ServerPlayer commander, String skillId) {
        return EspetroAPI.getCommanderSkillStatus(commander, skillId).canUse();
    }

    public static List<FobSnapshot> getFobs() {
        return EspetroAPI.getTacticalMapStateSnapshot().structures();
    }

    private static List<FobSnapshot> collectFobs() {
        return BastionManager.getInstance().getAllBastions().stream().filter(BastionData::isActive).map(EspetroAPI::toFobSnapshot).sorted(Comparator.comparing(snapshot -> snapshot.id().toString())).toList();
    }

    private static FobSnapshot toFobSnapshot(BastionData data) {
        boolean radio = data.isRadio();
        double buildRadius = radio ? LogisticsConfig.get().radioBuildRadius : 0.0;
        double exclusionRadius = radio ? LogisticsConfig.get().radioExclusionRadius : 0.0;
        int construction = radio ? data.getConstructionSupplies() : 0;
        int ammunition = radio ? data.getAmmunitionSupplies() : 0;
        boolean habOperational = BastionManager.getInstance().isHabOperational(data);
        boolean radioCovered = !data.isHab() || BastionManager.getInstance().isCoveredByFriendlyRadio(data);
        return new FobSnapshot(data.getBastionId(), data.getTeam(), data.getName(), data.getLevel().m_46472_().m_135782_().toString(), data.getPosition().m_123341_(), data.getPosition().m_123342_(), data.getPosition().m_123343_(), construction, ammunition, data.isHabBuilt() || data.isHab(), data.isAmmoCrateBuilt(), habOperational, buildRadius, exclusionRadius, radioCovered, data.getKind().networkType());
    }

    public static List<TeamPackManager.RallySnapshot> getRallies() {
        return EspetroAPI.getTacticalMapStateSnapshot().rallies().stream().map(rally -> new TeamPackManager.RallySnapshot(rally.id(), rally.team(), rally.squadId(), rally.dimension(), rally.x(), rally.y(), rally.z(), rally.nextWaveAtMillis())).toList();
    }

    public static List<VehicleManager.SupplyStationSnapshot> getVehicleSupplyStations() {
        return EspetroAPI.getTacticalMapStateSnapshot().vehicleSupplyStations().stream().map(station -> new VehicleManager.SupplyStationSnapshot(station.id(), station.name(), station.team(), station.dimension(), station.x(), station.y(), station.z())).toList();
    }

    public static synchronized TacticalMapStateSnapshot getTacticalMapStateSnapshot() {
        long session = BattlefieldContext.getSessionId();
        if (lastTacticalSnapshot != null && tacticalSnapshotSession == session && tacticalBuiltRevision == tacticalDirtyRevision) {
            return lastTacticalSnapshot;
        }
        String dimension = EspetroAPI.getActiveBattlefieldDimension().map(key -> key.m_135782_().toString()).orElse("");
        List<FobSnapshot> structures = EspetroAPI.collectFobs();
        List<TacticalMapStateSnapshot.RallySnapshot> rallies = TeamPackManager.getInstance().getRallySnapshots().stream().map(rally -> new TacticalMapStateSnapshot.RallySnapshot(rally.id(), rally.team(), rally.squadId(), rally.dimension(), rally.x(), rally.y(), rally.z(), rally.nextWaveAtMillis())).sorted(Comparator.comparing(snapshot -> snapshot.id().toString())).toList();
        List<TacticalMapStateSnapshot.TeamBaseSnapshot> teamBases = SpawnPointConfig.getAllSpawnPoints().entrySet().stream().map(entry -> new TacticalMapStateSnapshot.TeamBaseSnapshot((String)entry.getKey(), (String)entry.getKey() + " Main Base", dimension, (int)Math.floor(((SpawnPointConfig.SpawnPoint)entry.getValue()).x), (int)Math.floor(((SpawnPointConfig.SpawnPoint)entry.getValue()).y), (int)Math.floor(((SpawnPointConfig.SpawnPoint)entry.getValue()).z), ((SpawnPointConfig.SpawnPoint)entry.getValue()).yaw)).sorted(Comparator.comparing(TacticalMapStateSnapshot.TeamBaseSnapshot::team)).toList();
        List<TacticalMapStateSnapshot.PlayerDeployPointSnapshot> deployPoints = BastionManager.getInstance().getPlayerDeployPointSnapshots().stream().map(point -> new TacticalMapStateSnapshot.PlayerDeployPointSnapshot(point.playerId(), point.dimension(), point.x(), point.y(), point.z())).toList();
        List<TacticalMapStateSnapshot.VehicleSupplyStationSnapshot> stations = VehicleManager.getInstance().getMappedSupplyStationSnapshots().stream().map(station -> new TacticalMapStateSnapshot.VehicleSupplyStationSnapshot(station.id(), station.name(), station.team(), station.dimension(), station.x(), station.y(), station.z())).sorted(Comparator.comparing(snapshot -> snapshot.id().toString())).toList();
        tacticalSnapshotSession = session;
        lastTacticalSnapshot = new TacticalMapStateSnapshot(++tacticalRevision, session, structures, rallies, teamBases, deployPoints, stations);
        tacticalBuiltRevision = tacticalDirtyRevision;
        return lastTacticalSnapshot;
    }

    private static ActiveBattlefieldSnapshot toPublicSnapshot(ActiveMapConfig map) {
        String esConfigPath = map.esConfigDir != null ? map.esConfigDir.toAbsolutePath().normalize().toString() : "";
        return new ActiveBattlefieldSnapshot(map.mapFolder, map.displayName, map.dimensionKey, esConfigPath, map.esPoints != null ? map.esPoints.tacticalMapJson : "", map.esPoints != null ? map.esPoints.capturePointsJson : "", map.esPoints != null ? map.esPoints.backgroundImage : "", map.esPoints != null ? map.esPoints.backgroundBytes() : new byte[]{}, map.esPoints != null ? map.esPoints.backgroundSha256 : "", map.esPoints != null ? map.esPoints.backgroundWidth : 0, map.esPoints != null ? map.esPoints.backgroundHeight : 0, map.esPoints != null ? map.esPoints.objectiveMode : "", map.esPoints != null ? map.esPoints.objectiveLane : "", map.esPoints != null ? map.esPoints.objectiveSeed : 0L);
    }

    public static String teamDisplayName(String team) {
        return TeamDisplayNames.displayName(team);
    }

    public static boolean isSymmetricObjectiveMode() {
        return TeamDisplayNames.isSymmetricMode();
    }

    public static void modifyTeamTroops(String team, int delta, String reason) {
        if (team == null) {
            return;
        }
        TroopCountManager troops = TroopCountManager.getInstance();
        if ("ATTACK".equalsIgnoreCase(team.trim())) {
            troops.modifyAttackTroops(delta);
        } else if ("DEFEND".equalsIgnoreCase(team.trim())) {
            troops.modifyDefendTroops(delta);
        }
        if (reason != null && !reason.isBlank()) {
            Espetro.LOGGER.info("[Troops] {} {} {:+d} ({})", new Object[]{team, reason, delta});
        }
    }

    public static void notifyObjectiveVictory(String winnerTeam) {
        String canonical;
        String string = canonical = winnerTeam == null ? "" : winnerTeam.trim().toUpperCase();
        if (!"ATTACK".equals(canonical) && !"DEFEND".equals(canonical)) {
            Espetro.LOGGER.warn("ignore objective victory for unknown team: {}", (Object)winnerTeam);
            return;
        }
        BattlefieldContext.setLastRoundWinner(canonical);
        String winLabel = TeamDisplayNames.coloredDisplayName(canonical);
        Espetro.broadcastToAll("\u00a76\u00a7l[\u636e\u70b9] \u00a7r" + winLabel + "\u00a7a \u5df2\u8fbe\u6210\u5360\u9886\u76ee\u6807\uff01");
        Espetro.LOGGER.info("ESPoints \u636e\u70b9\u80dc\u5229: {}", (Object)canonical);
    }

    public static void setResolvedObjectiveMode(String mode, String laneId) {
        BattlefieldContext.setResolvedObjective(mode == null ? "" : mode, laneId == null ? "" : laneId);
        try {
            GameStateManager gsm = GameStateManager.getInstance();
            if (gsm != null) {
                NetworkManager.broadcastGamePhase(gsm.getCurrentPhase());
            }
        }
        catch (Throwable t) {
            Espetro.LOGGER.debug("objectiveMode \u5ba2\u6237\u7aef\u540c\u6b65\u8df3\u8fc7: {}", (Object)t.toString());
        }
    }

    private static String getPlayerTeamById(UUID playerId) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return null;
        }
        ServerPlayer player = server.m_6846_().m_11259_(playerId);
        return player != null ? Espetro.getPlayerTeam(player) : null;
    }

    static {
        tacticalDirtyRevision = 1L;
        tacticalBuiltRevision = Long.MIN_VALUE;
        tacticalSnapshotSession = Long.MIN_VALUE;
    }

    public record FobSnapshot(UUID id, String team, String name, String dimension, int x, int y, int z, int construction, int ammunition, boolean habBuilt, boolean ammoCrateBuilt, boolean habOperational, double buildRadius, double exclusionRadius, boolean radioCovered, String kind) {
        public FobSnapshot(UUID id, String team, String name, String dimension, int x, int y, int z, int construction, int ammunition, boolean habBuilt, boolean ammoCrateBuilt, boolean habOperational, double buildRadius, double exclusionRadius, String kind) {
            this(id, team, name, dimension, x, y, z, construction, ammunition, habBuilt, ammoCrateBuilt, habOperational, buildRadius, exclusionRadius, true, kind);
        }

        public FobSnapshot(UUID id, String team, String name, String dimension, int x, int y, int z, int construction, int ammunition, boolean habBuilt, boolean ammoCrateBuilt, boolean habOperational, double buildRadius, double exclusionRadius) {
            this(id, team, name, dimension, x, y, z, construction, ammunition, habBuilt, ammoCrateBuilt, habOperational, buildRadius, exclusionRadius, true, habBuilt ? "HAB" : "RADIO");
        }

        public String type() {
            return this.kind == null || this.kind.isBlank() ? "RADIO" : this.kind;
        }
    }
}

