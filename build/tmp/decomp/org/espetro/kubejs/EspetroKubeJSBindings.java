/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.kubejs;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.espetro.Espetro;
import org.espetro.api.EspetroAPI;
import org.espetro.bastion.BastionData;
import org.espetro.bastion.BastionManager;
import org.espetro.config.GameConfig;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.stats.PlayerMatchStatsManager;
import org.espetro.team.ClassCountManager;
import org.espetro.team.ClassEquipment;
import org.espetro.team.ClassSelectManager;
import org.espetro.team.CommanderSkillManager;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;
import org.espetro.team.OutpostManager;
import org.espetro.team.SpawnPointConfig;
import org.espetro.team.SquadManager;
import org.espetro.team.TeamPackManager;
import org.espetro.team.TroopCountManager;
import org.espetro.team.VoteManager;
import org.espetro.vehicle.VehicleConfig;
import org.espetro.vehicle.VehicleManager;

public final class EspetroKubeJSBindings {
    private EspetroKubeJSBindings() {
    }

    public static String modId() {
        return "espetro";
    }

    @Nullable
    public static String getActiveMapId() {
        return BattlefieldContext.get().map(map -> map.mapFolder).orElse(null);
    }

    @Nullable
    public static String getActiveMapName() {
        return BattlefieldContext.get().map(map -> map.displayName).orElse(null);
    }

    @Nullable
    public static String getActiveBattlefieldDimension() {
        return BattlefieldContext.getActiveDimensionKey().map(key -> key.m_135782_().toString()).orElse(null);
    }

    public static long getBattlefieldSessionId() {
        return BattlefieldContext.getSessionId();
    }

    public static boolean endRound(String winner) {
        return GameStateManager.getInstance().endRound(winner);
    }

    @Nullable
    public static PlayerMatchStatsManager.PlayerMatchStats getPlayerMatchStats(Object playerRef) {
        UUID id = EspetroKubeJSBindings.uuidOrNull(playerRef);
        return id == null ? null : (PlayerMatchStatsManager.PlayerMatchStats)PlayerMatchStatsManager.getInstance().get(id).orElse(null);
    }

    @Nullable
    public static String getSquadCategory(Object playerRef) {
        UUID id = EspetroKubeJSBindings.uuidOrNull(playerRef);
        return id == null ? null : SquadManager.getInstance().getPlayerCategoryId(id);
    }

    @Nullable
    public static MinecraftServer server() {
        return Espetro.getServer();
    }

    public static Class<GameConfig> gameConfig() {
        return GameConfig.class;
    }

    public static ClassCountManager classes() {
        return ClassCountManager.getInstance();
    }

    public static FactionDataLoader factions() {
        return FactionDataProvider.getOrCreateLoader();
    }

    public static GameStateManager game() {
        return GameStateManager.getInstance();
    }

    public static ClassSelectManager factionSelection() {
        return ClassSelectManager.getInstance();
    }

    public static SquadManager squads() {
        return SquadManager.getInstance();
    }

    public static VoteManager votes() {
        return VoteManager.getInstance();
    }

    public static TroopCountManager troops() {
        return TroopCountManager.getInstance();
    }

    public static BastionManager bastions() {
        return BastionManager.getInstance();
    }

    public static TeamPackManager teamPacks() {
        return TeamPackManager.getInstance();
    }

    public static OutpostManager outposts() {
        return OutpostManager.getInstance();
    }

    public static VehicleManager vehicles() {
        return VehicleManager.getInstance();
    }

    public static CommanderSkillManager commanderSkills() {
        return CommanderSkillManager.getInstance();
    }

    public static void reloadAllConfigs() {
        Espetro.reloadAllConfigs();
    }

    public static void broadcast(String message) {
        Espetro.broadcastToAll(message);
    }

    public static void broadcastToAll(String message) {
        Espetro.broadcastToAll(message);
    }

    public static void broadcastToTeam(String team, String message) {
        Espetro.broadcastToTeam(team, message);
    }

    public static void sendToPlayer(ServerPlayer player, String message) {
        Espetro.sendToPlayer(player, message);
    }

    @Nullable
    public static ServerPlayer getPlayer(Object playerRef) {
        if (playerRef instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer)playerRef;
            return player;
        }
        MinecraftServer server = Espetro.getServer();
        if (server == null || playerRef == null) {
            return null;
        }
        if (playerRef instanceof CharSequence) {
            CharSequence nameOrUuid = (CharSequence)playerRef;
            String value = nameOrUuid.toString();
            UUID parsedUuid = EspetroKubeJSBindings.parseUuid(value);
            if (parsedUuid != null) {
                return server.m_6846_().m_11259_(parsedUuid);
            }
            return server.m_6846_().m_11255_(value);
        }
        if (playerRef instanceof Player) {
            Player player = (Player)playerRef;
            return server.m_6846_().m_11259_(player.m_20148_());
        }
        UUID uuid = EspetroKubeJSBindings.uuidOrNull(playerRef);
        return uuid == null ? null : server.m_6846_().m_11259_(uuid);
    }

    @Nullable
    public static UUID uuid(Object playerRef) {
        return EspetroKubeJSBindings.uuidOrNull(playerRef);
    }

    @Nullable
    public static String getPlayerName(Object playerRef) {
        ServerPlayer player = EspetroKubeJSBindings.getPlayer(playerRef);
        return player == null ? null : player.m_7755_().getString();
    }

    @Nullable
    public static String getPlayerTeam(Object playerRef) {
        ServerPlayer player = EspetroKubeJSBindings.getPlayer(playerRef);
        if (player != null) {
            return Espetro.getPlayerTeam(player);
        }
        UUID uuid = EspetroKubeJSBindings.uuidOrNull(playerRef);
        if (uuid == null) {
            return null;
        }
        String team = EspetroKubeJSBindings.classes().getPlayerTeam(uuid);
        return team != null ? team : EspetroKubeJSBindings.classes().getEffectivePlayerTeam(uuid);
    }

    public static boolean isPlayerDeployed(Object playerRef) {
        ServerPlayer player = EspetroKubeJSBindings.getPlayer(playerRef);
        return player != null && EspetroAPI.isPlayerVisibleOnTacticalMap(player);
    }

    public static boolean isPlayerVisibleOnTacticalMap(Object playerRef) {
        return EspetroKubeJSBindings.isPlayerDeployed(playerRef);
    }

    @Nullable
    public static String getPlayerFaction(Object playerRef) {
        UUID uuid = EspetroKubeJSBindings.uuidOrNull(playerRef);
        return uuid == null ? null : EspetroKubeJSBindings.classes().getPlayerFaction(uuid);
    }

    @Nullable
    public static String getPlayerClass(Object playerRef) {
        UUID uuid = EspetroKubeJSBindings.uuidOrNull(playerRef);
        return uuid == null ? null : EspetroKubeJSBindings.classes().getPlayerClass(uuid);
    }

    @Nullable
    public static String getPlayerClassVariant(Object playerRef) {
        UUID uuid = EspetroKubeJSBindings.uuidOrNull(playerRef);
        return uuid == null ? null : EspetroKubeJSBindings.classes().getPlayerVariant(uuid);
    }

    public static boolean selectPlayerClass(ServerPlayer player, String classId) {
        return EspetroKubeJSBindings.classes().selectClass(player, classId);
    }

    public static boolean selectPlayerClass(ServerPlayer player, String classId, String variantId) {
        return EspetroKubeJSBindings.classes().selectClass(player, classId, variantId);
    }

    public static void equipPlayer(ServerPlayer player, String classId) {
        ClassEquipment.equipPlayer(player, classId);
    }

    public static void equipPlayer(ServerPlayer player, String classId, String variantId) {
        if (player == null) {
            return;
        }
        String factionId = EspetroKubeJSBindings.classes().getPlayerFaction(player.m_20148_());
        ClassEquipment.equipPlayer(player, factionId, classId, variantId);
    }

    public static void clearEquipment(ServerPlayer player) {
        ClassEquipment.clearEquipment(player);
    }

    public static int getPlayerSquadId(Object playerRef) {
        UUID uuid = EspetroKubeJSBindings.uuidOrNull(playerRef);
        return uuid == null ? -1 : EspetroKubeJSBindings.squads().getPlayerSquadId(uuid);
    }

    public static boolean isSquadLeader(Object playerRef) {
        UUID uuid = EspetroKubeJSBindings.uuidOrNull(playerRef);
        return uuid != null && EspetroKubeJSBindings.squads().isSquadLeader(uuid);
    }

    public static boolean isCommander(Object playerRef) {
        UUID uuid = EspetroKubeJSBindings.uuidOrNull(playerRef);
        return uuid != null && EspetroKubeJSBindings.votes().isCommander(uuid);
    }

    public static List<SquadManager.SquadSnapshot> getSquads(String team) {
        return EspetroKubeJSBindings.squads().getSquadSnapshots(team);
    }

    public static Map<String, Integer> getClassCounts(String team, String factionId) {
        return EspetroKubeJSBindings.classes().getCountsForFaction(team, factionId);
    }

    public static Map<String, Map<String, Integer>> getClassVariantCounts(String team, String factionId) {
        return EspetroKubeJSBindings.classes().getVariantCountsForFaction(team, factionId);
    }

    public static FactionDataLoader.FactionData getFaction(String factionId) {
        return EspetroKubeJSBindings.factions().getFaction(factionId);
    }

    public static FactionDataLoader.FactionData[] getFactions() {
        return EspetroKubeJSBindings.factions().getFactionArray();
    }

    public static FactionDataLoader.ClassKitData getClassKit(String classId) {
        return EspetroKubeJSBindings.factions().getClassKit(classId);
    }

    public static FactionDataLoader.ClassKitData[] getClassesForFaction(String factionId) {
        return EspetroKubeJSBindings.factions().getClassesForFaction(factionId);
    }

    public static String[] getClassIdsForFaction(String factionId) {
        return EspetroKubeJSBindings.factions().getClassIdsForFaction(factionId);
    }

    public static Map<String, FactionDataLoader.VehicleData> getFactionVehicles(String factionId) {
        return EspetroKubeJSBindings.factions().getFactionVehicles(factionId);
    }

    public static Map<String, VehicleConfig.VehicleTypeConfig> getRuntimeVehicleConfig(String factionId) {
        return VehicleConfig.getFactionVehicles(factionId);
    }

    public static GamePhase phase() {
        return EspetroKubeJSBindings.game().getCurrentPhase();
    }

    public static String phaseId() {
        return EspetroKubeJSBindings.phase().name();
    }

    public static String phaseDisplayName() {
        return EspetroKubeJSBindings.phase().getDisplayName();
    }

    public static void setPhase(GamePhase phase) {
        EspetroKubeJSBindings.game().setPhase(phase);
    }

    public static boolean setPhase(String phaseId) {
        GamePhase parsed = EspetroKubeJSBindings.parsePhase(phaseId);
        if (parsed == null) {
            return false;
        }
        EspetroKubeJSBindings.game().setPhase(parsed);
        return true;
    }

    public static int getDeployTimeRemainingSeconds() {
        return EspetroKubeJSBindings.game().getDeployTimeRemainingSeconds();
    }

    public static void forceStartCommanderVote() {
        EspetroKubeJSBindings.game().forceStartCommanderVote();
    }

    public static void forceStartGame() {
        EspetroKubeJSBindings.game().forceStartGame();
    }

    public static void resetGame() {
        EspetroKubeJSBindings.game().resetGame();
    }

    @Nullable
    public static String getTeamFromFaction(String factionId) {
        return GameStateManager.getTeamFromFactionStatic(factionId);
    }

    public static int getAttackTroops() {
        return EspetroKubeJSBindings.troops().getAttackTroops();
    }

    public static int getDefendTroops() {
        return EspetroKubeJSBindings.troops().getDefendTroops();
    }

    public static void setAttackTroops(int value) {
        EspetroKubeJSBindings.troops().setAttackTroops(value);
    }

    public static void setDefendTroops(int value) {
        EspetroKubeJSBindings.troops().setDefendTroops(value);
    }

    public static void modifyAttackTroops(int delta) {
        EspetroKubeJSBindings.troops().modifyAttackTroops(delta);
    }

    public static void modifyDefendTroops(int delta) {
        EspetroKubeJSBindings.troops().modifyDefendTroops(delta);
    }

    public static List<BastionData> getBastions(String team) {
        return EspetroKubeJSBindings.bastions().getTeamBastions(team);
    }

    public static List<BastionData> getAllBastions() {
        return EspetroKubeJSBindings.bastions().getAllBastions();
    }

    public static int getBastionCooldownRemaining(Object playerRef) {
        UUID uuid = EspetroKubeJSBindings.uuidOrNull(playerRef);
        return uuid == null ? 0 : EspetroKubeJSBindings.bastions().getBastionCooldownRemaining(uuid);
    }

    public static int getResupplyCooldownRemaining(Object playerRef) {
        UUID uuid = EspetroKubeJSBindings.uuidOrNull(playerRef);
        return uuid == null ? 0 : EspetroKubeJSBindings.bastions().getResupplyCooldownRemaining(uuid);
    }

    @Nullable
    public static String tryResupply(Object playerRef) {
        UUID uuid = EspetroKubeJSBindings.uuidOrNull(playerRef);
        return uuid == null ? "Invalid player" : EspetroKubeJSBindings.bastions().tryResupply(uuid);
    }

    @Nullable
    public static String tryStartOutpostRedeploy(ServerPlayer player) {
        return EspetroKubeJSBindings.outposts().tryStartRedeploy(player);
    }

    @Nullable
    public static String tryDeployOutpost(ServerPlayer player, int outpostIndex) {
        return EspetroKubeJSBindings.outposts().tryDeploy(player, outpostIndex);
    }

    @Nullable
    public static String deployVehicle(ServerPlayer commander, String vehicleType) {
        return EspetroKubeJSBindings.vehicles().deployVehicle(commander, vehicleType);
    }

    public static long getVehicleCooldownRemaining(String factionId, String vehicleType) {
        return EspetroKubeJSBindings.vehicles().getCooldownRemaining(factionId, vehicleType);
    }

    public static int getVehicleActiveCount(String factionId, String vehicleType) {
        return EspetroKubeJSBindings.vehicles().getActiveCount(factionId, vehicleType);
    }

    public static boolean activateCommanderSkill(ServerPlayer commander, String skillId) {
        return EspetroKubeJSBindings.commanderSkills().activateSkill(commander, skillId);
    }

    public static boolean executeCommanderSkill(ServerPlayer commander, String skillId) {
        return EspetroKubeJSBindings.activateCommanderSkill(commander, skillId);
    }

    public static boolean openCommanderTargetMap(ServerPlayer commander, String skillId) {
        return EspetroKubeJSBindings.commanderSkills().beginArtilleryTargetSelection(commander, skillId);
    }

    public static boolean openArtillerySupportMap(ServerPlayer commander) {
        return EspetroKubeJSBindings.commanderSkills().beginArtilleryTargetSelection(commander);
    }

    public static boolean submitArtillerySupportTarget(ServerPlayer commander, double x, double z) {
        return EspetroKubeJSBindings.commanderSkills().submitArtillerySupportTarget(commander, x, z);
    }

    public static boolean submitCommanderSkillTarget(ServerPlayer commander, double x, double z) {
        return EspetroKubeJSBindings.commanderSkills().submitArtillerySupportTarget(commander, x, z);
    }

    @Nullable
    public static CommanderSkillManager.ArtillerySupportRequest getLatestArtillerySupportRequest() {
        return EspetroKubeJSBindings.commanderSkills().getLatestArtillerySupportRequest();
    }

    @Nullable
    public static CommanderSkillManager.ArtillerySupportRequest getLatestCommanderSkillTargetRequest() {
        return EspetroKubeJSBindings.commanderSkills().getLatestCommanderSkillTargetRequest();
    }

    public static List<CommanderSkillManager.ArtillerySupportRequest> getArtillerySupportRequests() {
        return EspetroKubeJSBindings.commanderSkills().getArtillerySupportRequestsSnapshot();
    }

    public static List<CommanderSkillManager.ArtillerySupportRequest> getCommanderSkillTargetRequests() {
        return EspetroKubeJSBindings.commanderSkills().getCommanderSkillTargetRequestsSnapshot();
    }

    public static List<CommanderSkillManager.ArtillerySupportRequest> drainArtillerySupportRequests() {
        return EspetroKubeJSBindings.commanderSkills().drainArtillerySupportRequests();
    }

    public static List<CommanderSkillManager.ArtillerySupportRequest> drainCommanderSkillTargetRequests() {
        return EspetroKubeJSBindings.commanderSkills().drainCommanderSkillTargetRequests();
    }

    public static int getCommanderSkillCooldown(Object playerRef, String skillId) {
        UUID uuid = EspetroKubeJSBindings.uuidOrNull(playerRef);
        if (uuid == null || skillId == null || skillId.isBlank()) {
            return 0;
        }
        return EspetroKubeJSBindings.commanderSkills().getRemainingCooldownSeconds(uuid, skillId);
    }

    public static boolean isCommanderSkillOnCooldown(Object playerRef, String skillId) {
        UUID uuid = EspetroKubeJSBindings.uuidOrNull(playerRef);
        return uuid != null && skillId != null && !skillId.isBlank() && EspetroKubeJSBindings.commanderSkills().isOnCooldown(uuid, skillId);
    }

    public static Map<String, Integer> getCommanderSkillCooldowns(Object playerRef) {
        UUID uuid = EspetroKubeJSBindings.uuidOrNull(playerRef);
        return uuid == null ? Map.of() : EspetroKubeJSBindings.commanderSkills().getCooldownData(uuid);
    }

    public static CommanderSkillManager.SkillStatus getCommanderSkillStatus(Object playerRef, String skillId) {
        return EspetroKubeJSBindings.commanderSkills().getSkillStatus(EspetroKubeJSBindings.getPlayer(playerRef), skillId);
    }

    public static boolean canUseCommanderSkill(Object playerRef, String skillId) {
        return EspetroKubeJSBindings.commanderSkills().getSkillStatus(EspetroKubeJSBindings.getPlayer(playerRef), skillId).canUse();
    }

    public static SpawnPointConfig.SpawnPoint getSpawnPoint(String team) {
        return SpawnPointConfig.getSpawnPoint(team);
    }

    public static Map<String, SpawnPointConfig.SpawnPoint> getAllSpawnPoints() {
        return SpawnPointConfig.getAllSpawnPoints();
    }

    @Nullable
    private static UUID uuidOrNull(Object playerRef) {
        if (playerRef == null) {
            return null;
        }
        if (playerRef instanceof UUID) {
            UUID uuid = (UUID)playerRef;
            return uuid;
        }
        if (playerRef instanceof Player) {
            Player player = (Player)playerRef;
            return player.m_20148_();
        }
        if (playerRef instanceof CharSequence) {
            ServerPlayer player;
            CharSequence value = (CharSequence)playerRef;
            UUID uuid = EspetroKubeJSBindings.parseUuid(value.toString());
            if (uuid != null) {
                return uuid;
            }
            MinecraftServer server = Espetro.getServer();
            if (server != null && (player = server.m_6846_().m_11255_(value.toString())) != null) {
                return player.m_20148_();
            }
        }
        return null;
    }

    @Nullable
    private static UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        }
        catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @Nullable
    private static GamePhase parsePhase(String phaseId) {
        if (phaseId == null || phaseId.isBlank()) {
            return null;
        }
        try {
            return GamePhase.valueOf(phaseId.trim().toUpperCase());
        }
        catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}

