/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package org.espetro.team;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import org.espetro.Espetro;
import org.espetro.api.EspetroAPI;
import org.espetro.bastion.BastionManager;
import org.espetro.config.GameConfig;
import org.espetro.network.NetworkManager;
import org.espetro.network.UnifiedDeployScreenPacket;
import org.espetro.team.ClassCountManager;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;
import org.espetro.team.SquadManager;

public class TeamPackManager {
    private static final String TEAM_PACK_ITEM_TAG = "EspetroTeamPack";
    private static final Gson GSON = new Gson();
    private static TeamPackManager INSTANCE;
    private final Map<UUID, TeamPackData> teamPacks = new HashMap<UUID, TeamPackData>();
    private final Map<SquadKey, UUID> squadTeamPacks = new HashMap<SquadKey, UUID>();
    private final Map<BlockPos, UUID> teamPackPositions = new HashMap<BlockPos, UUID>();
    private final Map<SquadKey, Long> squadCooldowns = new HashMap<SquadKey, Long>();
    private final Map<UUID, Long> inheritedLeaderCooldowns = new HashMap<UUID, Long>();
    private final Set<UUID> pendingItemSyncs = new HashSet<UUID>();
    private final Map<UUID, PendingRallyRespawn> pendingRespawns = new HashMap<UUID, PendingRallyRespawn>();
    private final Map<UUID, Long> playerDeathTimes = new HashMap<UUID, Long>();
    private final Map<UUID, Long> personalRallyReadyAt = new HashMap<UUID, Long>();
    private int cooldownSeconds = 120;
    private int durability = 1;
    private float breakSpeedMultiplier = 8.0f;
    private int teammateCount = 1;
    private double teammateRadius = 8.0;
    private double enemyPlacementRadius = 50.0;
    private double enemyBurnRadius = 30.0;
    private int waveSeconds = 60;
    private int minimumRespawnSeconds = 20;
    private long tickCounter;

    private TeamPackManager() {
        INSTANCE = this;
        this.loadConfig();
    }

    public static TeamPackManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TeamPackManager();
        }
        return INSTANCE;
    }

    public static void init() {
        boolean removedVisibleRallies = INSTANCE != null && TeamPackManager.INSTANCE.teamPacks.values().stream().anyMatch(teamPack -> teamPack.active);
        INSTANCE = new TeamPackManager();
        if (removedVisibleRallies) {
            EspetroAPI.markTacticalMapStateDirty();
        }
    }

    private void loadConfig() {
    }

    public void reloadConfig() {
    }

    public void applyExternalJson(String rawJson) {
        this.cooldownSeconds = 120;
        this.durability = 1;
        this.breakSpeedMultiplier = 8.0f;
        this.teammateCount = 1;
        this.teammateRadius = 8.0;
        this.enemyPlacementRadius = 50.0;
        this.enemyBurnRadius = 30.0;
        this.waveSeconds = 60;
        this.minimumRespawnSeconds = 20;
        JsonObject json = (JsonObject)GSON.fromJson(rawJson, JsonObject.class);
        if (json == null || !json.has("team_pack")) {
            return;
        }
        JsonObject teamPack = json.getAsJsonObject("team_pack");
        if (teamPack.has("cooldown_seconds")) {
            this.cooldownSeconds = Math.max(0, teamPack.get("cooldown_seconds").getAsInt());
        }
        if (teamPack.has("durability")) {
            this.durability = Math.max(1, teamPack.get("durability").getAsInt());
        }
        if (teamPack.has("break_speed_multiplier")) {
            this.breakSpeedMultiplier = Math.max(1.0f, teamPack.get("break_speed_multiplier").getAsFloat());
        }
        if (teamPack.has("teammate_count")) {
            this.teammateCount = Math.max(0, teamPack.get("teammate_count").getAsInt());
        }
        if (teamPack.has("teammate_radius")) {
            this.teammateRadius = Math.max(0.0, teamPack.get("teammate_radius").getAsDouble());
        }
        if (teamPack.has("enemy_placement_radius")) {
            this.enemyPlacementRadius = Math.max(0.0, teamPack.get("enemy_placement_radius").getAsDouble());
        }
        if (teamPack.has("enemy_burn_radius")) {
            this.enemyBurnRadius = Math.max(0.0, teamPack.get("enemy_burn_radius").getAsDouble());
        }
        if (teamPack.has("wave_seconds")) {
            this.waveSeconds = Math.max(1, teamPack.get("wave_seconds").getAsInt());
        }
        if (teamPack.has("minimum_respawn_seconds")) {
            this.minimumRespawnSeconds = Math.max(0, teamPack.get("minimum_respawn_seconds").getAsInt());
        }
    }

    public int getCooldownSeconds() {
        return this.cooldownSeconds;
    }

    public int getDurability() {
        return this.durability;
    }

    public float getBreakSpeedMultiplier() {
        return this.breakSpeedMultiplier;
    }

    public void onServerTick() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            this.pendingItemSyncs.clear();
            this.pendingRespawns.clear();
            return;
        }
        if (!this.pendingItemSyncs.isEmpty()) {
            ArrayList<UUID> pendingPlayers = new ArrayList<UUID>(this.pendingItemSyncs);
            this.pendingItemSyncs.clear();
            for (UUID playerId : pendingPlayers) {
                ServerPlayer player = server.m_6846_().m_11259_(playerId);
                if (player == null) continue;
                this.syncTeamPackItem(player);
            }
        }
        this.processPendingRespawns(server);
        if (this.tickCounter++ % 100L == 0L) {
            this.burnEnemyProxiedRallies();
        }
    }

    public void onPlayerDeath(UUID playerId) {
        this.playerDeathTimes.put(playerId, System.currentTimeMillis());
        this.pendingRespawns.remove(playerId);
    }

    private long computeAlignedReadyAt(UUID playerId, TeamPackData teamPack, long now) {
        long deathAt = this.playerDeathTimes.getOrDefault(playerId, now);
        long earliest = deathAt + (long)this.minimumRespawnSeconds * 1000L;
        long ready = now + (long)this.waveSeconds * 1000L;
        return Math.max(ready, earliest);
    }

    public void cancelPendingRespawn(UUID playerId) {
        if (playerId == null) {
            return;
        }
        this.pendingRespawns.remove(playerId);
    }

    public long getPersonalRallyReadyAt(UUID playerId, TeamPackData teamPack) {
        Long existing = this.personalRallyReadyAt.get(playerId);
        if (existing != null) {
            return existing;
        }
        long now = System.currentTimeMillis();
        long ready = this.computeAlignedReadyAt(playerId, teamPack, now);
        this.personalRallyReadyAt.put(playerId, ready);
        return ready;
    }

    public int getWaveSeconds() {
        return this.waveSeconds;
    }

    public List<UnifiedDeployScreenPacket.BastionItem> getDeployItemsForPlayer(ServerPlayer player) {
        long remainingSeconds;
        ArrayList<UnifiedDeployScreenPacket.BastionItem> result = new ArrayList<UnifiedDeployScreenPacket.BastionItem>();
        String team = Espetro.getPlayerTeam(player);
        if (team == null) {
            return result;
        }
        int squadId = SquadManager.getInstance().getPlayerSquadId(player.m_20148_());
        if (squadId == -1) {
            return result;
        }
        UUID teamPackId = this.squadTeamPacks.get(this.squadKey(team, squadId));
        if (teamPackId == null) {
            return result;
        }
        TeamPackData teamPack = this.getTeamPack(teamPackId);
        if (teamPack == null || !Objects.equals(teamPack.team, team)) {
            return result;
        }
        long now = System.currentTimeMillis();
        long personalReadyAt = this.getPersonalRallyReadyAt(player.m_20148_(), teamPack);
        PendingRallyRespawn pending = this.pendingRespawns.get(player.m_20148_());
        if (pending != null && Objects.equals(pending.teamPackId(), teamPack.teamPackId)) {
            personalReadyAt = pending.spawnAt();
        }
        String status = (remainingSeconds = Math.max(0L, (personalReadyAt - now + 999L) / 1000L)) <= 0L ? "\u5c31\u7eea" : "\u51b7\u5374 " + remainingSeconds + "/" + this.waveSeconds + "s";
        BlockPos spawnPos = teamPack.getSpawnPos();
        result.add(new UnifiedDeployScreenPacket.BastionItem(teamPack.teamPackId, "Rally " + squadId, spawnPos.m_123341_() + ", " + spawnPos.m_123342_() + ", " + spawnPos.m_123343_(), "rally", status, personalReadyAt, this.waveSeconds));
        return result;
    }

    public boolean respawnAtTeamPack(ServerPlayer player, UUID teamPackId) {
        String team = Espetro.getPlayerTeam(player);
        if (team == null) {
            return false;
        }
        String classId = ClassCountManager.getInstance().getPlayerClass(player.m_20148_());
        if (classId == null || classId.isEmpty()) {
            player.m_213846_(Component.m_237113_("\u00a7c\u8bf7\u5148\u9009\u62e9\u804c\u4e1a\u540e\u518d\u9009\u62e9\u90e8\u7f72\u70b9\uff01"));
            return false;
        }
        TeamPackData teamPack = this.getTeamPack(teamPackId);
        if (teamPack == null || !team.equals(teamPack.team)) {
            player.m_213846_(Component.m_237113_("\u00a7c\u65e0\u6548\u7684\u961f\u4f0d\u96c6\u7ed3\u70b9\uff01"));
            return false;
        }
        if (GameStateManager.getInstance().getCurrentPhase() != GamePhase.BATTLE && GameStateManager.getInstance().getCurrentPhase() != GamePhase.DEPLOYING) {
            player.m_213846_(Component.m_237113_("\u00a7c\u53ea\u80fd\u5728\u6218\u6597\u6216\u90e8\u7f72\u9636\u6bb5\u590d\u6d3b\uff01"));
            return false;
        }
        if (!BastionManager.getInstance().isWaitingForBastion(player.m_20148_())) {
            player.m_213846_(Component.m_237113_("\u00a7c\u4f60\u5df2\u7ecf\u5b8c\u6210\u4e86\u590d\u6d3b\u9009\u62e9\uff01"));
            return false;
        }
        int squadId = SquadManager.getInstance().getPlayerSquadId(player.m_20148_());
        if (squadId == -1 || squadId != teamPack.squadId) {
            player.m_213846_(Component.m_237113_("\u00a7c\u8be5\u961f\u4f0d\u96c6\u7ed3\u70b9\u4e0d\u5c5e\u4e8e\u4f60\u5f53\u524d\u7684\u5c0f\u961f\uff01"));
            return false;
        }
        if (this.isTeamPackMissing(teamPack)) {
            this.destroyTeamPack(teamPack, null, false, false);
            player.m_213846_(Component.m_237113_("\u00a7c\u8be5\u961f\u4f0d\u96c6\u7ed3\u70b9\u5df2\u5931\u6548\uff01"));
            return false;
        }
        long now = System.currentTimeMillis();
        long readyAt = this.getPersonalRallyReadyAt(player.m_20148_(), teamPack);
        if (readyAt <= now) {
            this.pendingRespawns.remove(player.m_20148_());
            this.spawnAtRally(player, teamPack);
            this.scheduleNextPersonalCooldown(player.m_20148_(), teamPack);
            return true;
        }
        this.pendingRespawns.put(player.m_20148_(), new PendingRallyRespawn(teamPackId, readyAt));
        long remaining = Math.max(1L, (readyAt - now + 999L) / 1000L);
        player.m_213846_(Component.m_237113_("\u00a7d\u5df2\u9009\u62e9 Rally\uff0c\u51b7\u5374\u4e2d \u00a7f" + remaining + "/" + this.waveSeconds + " \u79d2\u00a7d\u3002\u5c31\u7eea\u540e\u5c06\u81ea\u52a8\u90e8\u7f72\u3002"));
        return true;
    }

    private void scheduleNextPersonalCooldown(UUID playerId, TeamPackData teamPack) {
        long now = System.currentTimeMillis();
        this.personalRallyReadyAt.put(playerId, now + (long)this.waveSeconds * 1000L);
        this.playerDeathTimes.remove(playerId);
    }

    private void processPendingRespawns(MinecraftServer server) {
        long now = System.currentTimeMillis();
        boolean waveDisplayChanged = false;
        for (TeamPackData teamPack : this.teamPacks.values()) {
            while (teamPack.active && teamPack.nextWaveAt <= now) {
                teamPack.nextWaveAt += (long)this.waveSeconds * 1000L;
                waveDisplayChanged = true;
            }
        }
        if (waveDisplayChanged) {
            EspetroAPI.markTacticalMapStateDirty();
        }
        if (this.pendingRespawns.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<UUID, PendingRallyRespawn>> iterator = this.pendingRespawns.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, PendingRallyRespawn> entry = iterator.next();
            ServerPlayer player = server.m_6846_().m_11259_(entry.getKey());
            TeamPackData teamPack = this.getTeamPack(entry.getValue().teamPackId());
            if (player == null) {
                iterator.remove();
                continue;
            }
            if (!BastionManager.getInstance().isWaitingForBastion(player.m_20148_())) {
                iterator.remove();
                continue;
            }
            if (teamPack == null || this.isTeamPackMissing(teamPack)) {
                iterator.remove();
                player.m_213846_(Component.m_237113_("\u00a7c\u6240\u9009 Rally \u5df2\u5931\u6548\uff0c\u8bf7\u91cd\u65b0\u9009\u62e9\u90e8\u7f72\u70b9\u3002"));
                NetworkManager.sendUnifiedDeployScreen(player, -1);
                continue;
            }
            if (now < entry.getValue().spawnAt()) continue;
            this.spawnAtRally(player, teamPack);
            this.scheduleNextPersonalCooldown(player.m_20148_(), teamPack);
            iterator.remove();
        }
    }

    public boolean isTeamPackItem(ItemStack stack) {
        return stack.m_41720_() == Items.f_42065_ && stack.m_41782_() && stack.m_41783_() != null && stack.m_41783_().m_128471_(TEAM_PACK_ITEM_TAG);
    }

    public void syncTeamPackItem(ServerPlayer player) {
        this.applyInheritedLeaderCooldown(player.m_20148_(), Espetro.getPlayerTeam(player), SquadManager.getInstance().getPlayerSquadId(player.m_20148_()));
        if (!SquadManager.getInstance().isSquadLeader(player.m_20148_())) {
            this.removeTeamPackItems(player);
        }
    }

    public void giveTeamPackItemIfNeeded(ServerPlayer player) {
        this.syncTeamPackItem(player);
    }

    @Nullable
    public String giveRallyItem(ServerPlayer player) {
        if (GameStateManager.getInstance().getCurrentPhase() != GamePhase.BATTLE) {
            return "\u00a7c\u53ea\u6709\u6218\u6597\u9636\u6bb5\u624d\u80fd\u90e8\u7f72 Rally\u3002";
        }
        if (!SquadManager.getInstance().isSquadLeader(player.m_20148_())) {
            return "\u00a7c\u53ea\u6709\u5c0f\u961f\u957f\u53ef\u4ee5\u90e8\u7f72 Rally\u3002";
        }
        for (ItemStack stack : player.m_150109_().f_35974_) {
            if (!this.isTeamPackItem(stack)) continue;
            return "\u00a7e\u4f60\u5df2\u7ecf\u643a\u5e26\u4e86\u4e00\u4e2a Rally \u90e8\u7f72\u5305\uff0c\u5148\u653e\u7f6e\u5b83\u3002";
        }
        if (this.isTeamPackItem(player.m_150109_().f_35976_.get(0))) {
            return "\u00a7e\u4f60\u5df2\u7ecf\u643a\u5e26\u4e86\u4e00\u4e2a Rally \u90e8\u7f72\u5305\uff0c\u5148\u653e\u7f6e\u5b83\u3002";
        }
        ItemStack stack = new ItemStack(Items.f_42065_);
        stack.m_41784_().m_128379_(TEAM_PACK_ITEM_TAG, true);
        stack.m_41714_(Component.m_237113_("\u00a7bRally \u90e8\u7f72\u5305"));
        if (!player.m_150109_().m_36054_(stack)) {
            player.m_36176_(stack, false);
        }
        return null;
    }

    public void removeTeamPackItems(ServerPlayer player) {
        boolean removed = false;
        for (int i = 0; i < player.m_150109_().f_35974_.size(); ++i) {
            ItemStack stack = player.m_150109_().f_35974_.get(i);
            if (!this.isTeamPackItem(stack)) continue;
            player.m_150109_().f_35974_.set(i, ItemStack.f_41583_);
            removed = true;
        }
        if (this.isTeamPackItem(player.m_150109_().f_35976_.get(0))) {
            player.m_150109_().f_35976_.set(0, ItemStack.f_41583_);
            removed = true;
        }
        if (removed) {
            player.m_150109_().m_6596_();
            player.f_36095_.m_38946_();
            player.f_36096_.m_38946_();
        }
    }

    public void reconcileTeam(@Nullable String team) {
        if (team == null) {
            return;
        }
        this.cleanupInvalidTeamPacks(team);
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            if (!team.equals(Espetro.getPlayerTeam(player))) continue;
            this.syncTeamPackItem(player);
        }
    }

    @Nullable
    public String canPlaceTeamPack(ServerPlayer player, ServerLevel level, BlockPos pos) {
        if (GameStateManager.getInstance().getCurrentPhase() != GamePhase.BATTLE) {
            return "\u00a7c\u53ea\u80fd\u5728\u6218\u6597\u9636\u6bb5\u90e8\u7f72\u961f\u5305\uff01";
        }
        String team = Espetro.getPlayerTeam(player);
        if (team == null) {
            return "\u00a7c\u65e0\u6cd5\u786e\u5b9a\u4f60\u7684\u961f\u4f0d\uff01";
        }
        SquadManager squadManager = SquadManager.getInstance();
        int squadId = squadManager.getPlayerSquadId(player.m_20148_());
        if (squadId == -1) {
            return "\u00a7c\u4f60\u5f53\u524d\u4e0d\u5728\u5c0f\u961f\u4e2d\uff0c\u65e0\u6cd5\u90e8\u7f72\u961f\u5305\uff01";
        }
        if (!squadManager.isSquadLeader(player.m_20148_())) {
            return "\u00a7c\u53ea\u6709\u5c0f\u961f\u961f\u957f\u624d\u80fd\u90e8\u7f72\u961f\u5305\uff01";
        }
        SquadKey squadKey = this.squadKey(team, squadId);
        if (squadKey == null) {
            return "\u00a7c\u65e0\u6cd5\u786e\u5b9a\u4f60\u7684\u5c0f\u961f\uff01";
        }
        int cooldownRemaining = this.getSquadCooldownRemaining(team, squadId);
        if (cooldownRemaining > 0) {
            return "\u00a7c\u961f\u5305\u51b7\u5374\u4e2d\uff01\u8bf7\u7b49\u5f85 " + cooldownRemaining + " \u79d2\u540e\u518d\u8bd5\u3002";
        }
        int nearbySquadMembers = this.countNearbySquadMembers(player, team, squadId, pos, this.teammateRadius);
        if (nearbySquadMembers < this.teammateCount) {
            return "\u00a7c\u90e8\u7f72 Rally \u9700\u8981\u653e\u7f6e\u70b9 " + TeamPackManager.formatRadius(this.teammateRadius) + " \u683c\u5185\u81f3\u5c11 " + this.teammateCount + " \u540d\u540c\u5c0f\u961f\u961f\u5458\uff01\u5f53\u524d\u4ec5 " + nearbySquadMembers + " \u540d\u3002";
        }
        if (this.hasEnemyNear(level, pos, team, this.enemyPlacementRadius)) {
            return "\u00a7c\u9644\u8fd1 " + (int)this.enemyPlacementRadius + " \u683c\u5185\u6709\u654c\u4eba\uff0c\u65e0\u6cd5\u90e8\u7f72 Rally\uff01";
        }
        return null;
    }

    @Nullable
    public String placeTeamPack(ServerPlayer player, ServerLevel level, BlockPos pos) {
        TeamPackData existing;
        int squadId;
        String error = this.canPlaceTeamPack(player, level, pos);
        if (error != null) {
            return error;
        }
        String team = Espetro.getPlayerTeam(player);
        SquadKey squadKey = this.squadKey(team, squadId = SquadManager.getInstance().getPlayerSquadId(player.m_20148_()));
        if (squadKey == null) {
            return "\u00a7c\u65e0\u6cd5\u786e\u5b9a\u4f60\u7684\u5c0f\u961f\uff01";
        }
        UUID existingId = this.squadTeamPacks.get(squadKey);
        if (existingId != null && (existing = this.teamPacks.get(existingId)) != null && existing.active) {
            this.destroyTeamPack(existing, null, true, false);
            this.broadcastToSquad(team, squadId, "\u00a7e[\u961f\u5305] \u65e7\u961f\u5305\u5df2\u88ab\u66ff\u6362\u3002");
        }
        TeamPackData teamPack = new TeamPackData(UUID.randomUUID(), team, squadId, pos, level, this.durability);
        this.teamPacks.put(teamPack.teamPackId, teamPack);
        this.squadTeamPacks.put(squadKey, teamPack.teamPackId);
        this.teamPackPositions.put(pos.m_7949_(), teamPack.teamPackId);
        EspetroAPI.markTacticalMapStateDirty();
        this.setSquadCooldown(squadKey);
        this.pendingItemSyncs.add(player.m_20148_());
        player.m_213846_(Component.m_237113_("\u00a7aRally \u5df2\u90e8\u7f72\uff01\u5c0f\u961f\u5458\u4e2a\u4eba\u590d\u6d3b\u51b7\u5374 " + this.waveSeconds + " \u79d2\u3002"));
        if (this.cooldownSeconds > 0) {
            player.m_213846_(Component.m_237113_("\u00a77\u961f\u5305\u653e\u7f6e\u51b7\u5374: " + this.cooldownSeconds + "\u79d2"));
        }
        this.broadcastToSquad(team, squadId, "\u00a7d[\u961f\u5305] \u00a7f" + player.m_7755_().getString() + " \u00a7d\u90e8\u7f72\u4e86\u961f\u4f0d\u96c6\u7ed3\u70b9\u3002");
        return null;
    }

    public int getSquadCooldownRemaining(int squadId) {
        int remaining = 0;
        for (Map.Entry<SquadKey, Long> entry : this.squadCooldowns.entrySet()) {
            if (entry.getKey().squadId != squadId) continue;
            remaining = Math.max(remaining, this.getCooldownRemaining(entry.getValue()));
        }
        return remaining;
    }

    public int getSquadCooldownRemaining(@Nullable String team, int squadId) {
        SquadKey squadKey = this.squadKey(team, squadId);
        if (squadKey == null) {
            return 0;
        }
        return this.getCooldownRemaining(this.squadCooldowns.get(squadKey));
    }

    public void handleSquadLeaderTransition(ServerPlayer player, @Nullable String previousTeam, int previousSquadId, boolean wasLeader, @Nullable String currentTeam, int currentSquadId, boolean isLeader) {
        boolean changedSquad;
        if (!wasLeader && !isLeader) {
            return;
        }
        SquadKey previousKey = this.squadKey(previousTeam, previousSquadId);
        SquadKey currentKey = this.squadKey(currentTeam, currentSquadId);
        boolean bl = changedSquad = previousKey == null || !previousKey.equals(currentKey);
        if (wasLeader && changedSquad) {
            this.rememberLeaderCooldown(player.m_20148_(), previousKey);
        }
        if (isLeader) {
            this.applyInheritedLeaderCooldown(player.m_20148_(), currentTeam, currentSquadId);
            this.syncTeamPackItem(player);
        } else if (wasLeader) {
            this.removeTeamPackItems(player);
        }
    }

    private void setSquadCooldown(SquadKey squadKey) {
        if (this.cooldownSeconds <= 0) {
            this.squadCooldowns.remove(squadKey);
            return;
        }
        this.squadCooldowns.put(squadKey, System.currentTimeMillis());
    }

    @Nullable
    public TeamPackData getTeamPack(UUID teamPackId) {
        TeamPackData teamPack = this.teamPacks.get(teamPackId);
        return teamPack != null && teamPack.active ? teamPack : null;
    }

    @Nullable
    public TeamPackData findByPos(BlockPos pos) {
        UUID teamPackId = this.teamPackPositions.get(pos);
        return teamPackId == null ? null : this.getTeamPack(teamPackId);
    }

    public List<RallySnapshot> getRallySnapshots() {
        return this.teamPacks.values().stream().filter(teamPack -> teamPack.active).map(teamPack -> new RallySnapshot(teamPack.teamPackId, teamPack.team, teamPack.squadId, teamPack.level.m_46472_().m_135782_().toString(), teamPack.pos.m_123341_(), teamPack.pos.m_123342_(), teamPack.pos.m_123343_(), teamPack.nextWaveAt)).toList();
    }

    private void spawnAtRally(ServerPlayer player, TeamPackData teamPack) {
        BlockPos spawnPos = teamPack.getSpawnPos();
        player.m_8999_(teamPack.level, (double)spawnPos.m_123341_() + 0.5, (double)spawnPos.m_123342_() + 0.1, (double)spawnPos.m_123343_() + 0.5, 0.0f, 0.0f);
        player.f_8924_.execute(() -> BastionManager.getInstance().clearWaiting(player.m_20148_()));
        player.m_143403_(GameType.SURVIVAL);
        player.m_21219_();
        int invincibilityTicks = GameConfig.getRespawnInvincibilityTicks();
        player.m_7292_(new MobEffectInstance(MobEffects.f_19606_, invincibilityTicks, 127, false, false, false));
        GameStateManager.getInstance().applyBattlefieldMiningRestriction(player);
        player.m_213846_(Component.m_237113_("\u00a7a\u5df2\u968f Rally \u90e8\u7f72\u590d\u6d3b\uff01"));
        GameStateManager.getInstance().onMidGameDeployComplete(player);
    }

    private void burnEnemyProxiedRallies() {
        if (this.teamPacks.isEmpty()) {
            return;
        }
        for (TeamPackData teamPack : new ArrayList<TeamPackData>(this.teamPacks.values())) {
            if (!teamPack.active || !this.hasEnemyNear(teamPack.level, teamPack.pos, teamPack.team, this.enemyBurnRadius)) continue;
            this.destroyTeamPack(teamPack, null, true, true);
            this.broadcastToSquad(teamPack.team, teamPack.squadId, "\u00a7c[Rally] \u654c\u4eba\u8fdb\u5165 " + (int)this.enemyBurnRadius + " \u683c\u8303\u56f4\uff0cRally \u5df2\u70e7\u6bc1\uff01");
        }
    }

    private int countNearbySquadMembers(ServerPlayer leader, String team, int squadId, BlockPos center, double radius) {
        double radiusSquared = radius * radius;
        int count = 0;
        for (ServerPlayer player : leader.m_284548_().m_6907_()) {
            if (player == leader || !player.m_6084_() || player.m_5833_() || !team.equals(Espetro.getPlayerTeam(player)) || SquadManager.getInstance().getPlayerSquadId(player.m_20148_()) != squadId || !(player.m_20183_().m_123331_(center) <= radiusSquared)) continue;
            ++count;
        }
        return count;
    }

    private static String formatRadius(double radius) {
        return radius == Math.rint(radius) ? Integer.toString((int)radius) : Double.toString(radius);
    }

    private boolean hasEnemyNear(ServerLevel level, BlockPos pos, String team, double radius) {
        double radiusSquared = radius * radius;
        for (ServerPlayer player : level.m_6907_()) {
            String playerTeam;
            if (!player.m_6084_() || player.m_5833_() || (playerTeam = Espetro.getPlayerTeam(player)) == null || team.equals(playerTeam) || !(player.m_20183_().m_123331_(pos) <= radiusSquared)) continue;
            return true;
        }
        return false;
    }

    public void cleanupInvalidTeamPacks() {
        this.cleanupInvalidTeamPacks(null);
    }

    public void cleanupInvalidTeamPacks(@Nullable String team) {
        boolean removedVisibleRally = false;
        Iterator<TeamPackData> iterator = this.teamPacks.values().iterator();
        while (iterator.hasNext()) {
            TeamPackData teamPack = iterator.next();
            if (!teamPack.active) {
                iterator.remove();
                continue;
            }
            if (team != null && !team.equals(teamPack.team) || SquadManager.getInstance().hasSquad(teamPack.team, teamPack.squadId) && !this.isTeamPackMissing(teamPack)) continue;
            teamPack.active = false;
            this.squadTeamPacks.remove(this.squadKey(teamPack.team, teamPack.squadId));
            this.teamPackPositions.remove(teamPack.pos);
            iterator.remove();
            removedVisibleRally = true;
        }
        if (removedVisibleRally) {
            EspetroAPI.markTacticalMapStateDirty();
        }
    }

    public void destroyTeamPack(TeamPackData teamPack, @Nullable ServerPlayer actor, boolean removeBlock, boolean enemyAction) {
        if (teamPack == null || !teamPack.active) {
            return;
        }
        this.unregisterTeamPack(teamPack);
        if (removeBlock && teamPack.level != null && teamPack.level.m_46805_(teamPack.pos) && teamPack.level.m_8055_(teamPack.pos).m_60713_(Blocks.f_50273_)) {
            teamPack.level.m_7731_(teamPack.pos, Blocks.f_50016_.m_49966_(), 3);
        }
        if (enemyAction) {
            this.broadcastToSquad(teamPack.team, teamPack.squadId, "\u00a7c[\u961f\u5305] \u961f\u4f0d\u96c6\u7ed3\u70b9\u5df2\u88ab\u654c\u65b9\u6467\u6bc1\uff01");
            if (actor != null) {
                actor.m_213846_(Component.m_237113_("\u00a7a\u4f60\u5df2\u6467\u6bc1\u654c\u65b9\u961f\u4f0d\u96c6\u7ed3\u70b9\uff01"));
            }
        } else if (actor != null) {
            this.broadcastToSquad(teamPack.team, teamPack.squadId, "\u00a7e[\u961f\u5305] \u961f\u4f0d\u96c6\u7ed3\u70b9\u5df2\u5931\u6548\u3002");
        }
    }

    public void damageTeamPack(TeamPackData teamPack, @Nullable ServerPlayer actor, int damage, boolean enemyAction) {
        if (teamPack == null || !teamPack.active) {
            return;
        }
        teamPack.health -= Math.max(1, damage);
        if (teamPack.health <= 0) {
            this.destroyTeamPack(teamPack, actor, true, enemyAction);
            return;
        }
        if (actor != null) {
            actor.m_213846_(Component.m_237113_("\u00a7e\u961f\u5305\u8010\u4e45: " + teamPack.health + "/" + teamPack.maxHealth));
        }
    }

    public void destroyTeamPackByExplosion(TeamPackData teamPack) {
        if (teamPack == null || !teamPack.active) {
            return;
        }
        this.unregisterTeamPack(teamPack);
        this.broadcastToSquad(teamPack.team, teamPack.squadId, "\u00a7c[\u961f\u5305] \u961f\u4f0d\u96c6\u7ed3\u70b9\u5df2\u88ab\u7206\u70b8\u6467\u6bc1\uff01");
    }

    public void reset() {
        this.reset(true);
    }

    public void clearRuntimeState() {
        this.reset(false);
    }

    private void reset(boolean removeBlocks) {
        for (TeamPackData teamPack : new ArrayList<TeamPackData>(this.teamPacks.values())) {
            this.destroyTeamPack(teamPack, null, removeBlocks, false);
        }
        this.teamPacks.clear();
        this.squadTeamPacks.clear();
        this.teamPackPositions.clear();
        this.squadCooldowns.clear();
        this.inheritedLeaderCooldowns.clear();
        this.pendingItemSyncs.clear();
        this.pendingRespawns.clear();
        this.playerDeathTimes.clear();
        this.personalRallyReadyAt.clear();
        this.tickCounter = 0L;
    }

    private boolean isTeamPackMissing(TeamPackData teamPack) {
        return teamPack.level == null || teamPack.level.m_46805_(teamPack.pos) && !teamPack.level.m_8055_(teamPack.pos).m_60713_(Blocks.f_50273_);
    }

    private void unregisterTeamPack(TeamPackData teamPack) {
        MinecraftServer server;
        boolean removedVisibleRally;
        TeamPackData registered = this.teamPacks.remove(teamPack.teamPackId);
        boolean bl = removedVisibleRally = registered != null && registered.active;
        if (registered != null) {
            registered.active = false;
        }
        teamPack.active = false;
        this.squadTeamPacks.remove(this.squadKey(teamPack.team, teamPack.squadId));
        this.teamPackPositions.remove(teamPack.pos);
        if (removedVisibleRally) {
            EspetroAPI.markTacticalMapStateDirty();
        }
        if ((server = Espetro.getServer()) != null) {
            for (ServerPlayer player : server.m_6846_().m_11314_()) {
                if (!teamPack.team.equals(Espetro.getPlayerTeam(player)) || SquadManager.getInstance().getPlayerSquadId(player.m_20148_()) != teamPack.squadId || !SquadManager.getInstance().isSquadLeader(player.m_20148_())) continue;
                this.pendingItemSyncs.add(player.m_20148_());
            }
        }
    }

    @Nullable
    private SquadKey squadKey(@Nullable String team, int squadId) {
        if (team == null || squadId == -1) {
            return null;
        }
        return new SquadKey(team, squadId);
    }

    private void rememberLeaderCooldown(UUID playerId, @Nullable SquadKey squadKey) {
        Long startedAt = this.getActiveCooldownStartedAt(squadKey);
        if (startedAt == null) {
            this.inheritedLeaderCooldowns.remove(playerId);
            return;
        }
        this.inheritedLeaderCooldowns.put(playerId, startedAt);
    }

    private void applyInheritedLeaderCooldown(UUID playerId, @Nullable String team, int squadId) {
        Long inheritedStartedAt = this.inheritedLeaderCooldowns.get(playerId);
        if (inheritedStartedAt == null) {
            return;
        }
        if (this.getCooldownRemaining(inheritedStartedAt) <= 0) {
            this.inheritedLeaderCooldowns.remove(playerId);
            return;
        }
        SquadKey squadKey = this.squadKey(team, squadId);
        if (squadKey == null) {
            return;
        }
        Long currentStartedAt = this.getActiveCooldownStartedAt(squadKey);
        if (currentStartedAt == null || inheritedStartedAt > currentStartedAt) {
            this.squadCooldowns.put(squadKey, inheritedStartedAt);
        }
        this.inheritedLeaderCooldowns.remove(playerId);
    }

    @Nullable
    private Long getActiveCooldownStartedAt(@Nullable SquadKey squadKey) {
        if (squadKey == null || this.cooldownSeconds <= 0) {
            return null;
        }
        Long startedAt = this.squadCooldowns.get(squadKey);
        if (startedAt == null) {
            return null;
        }
        if (this.getCooldownRemaining(startedAt) <= 0) {
            this.squadCooldowns.remove(squadKey);
            return null;
        }
        return startedAt;
    }

    private int getCooldownRemaining(@Nullable Long startedAt) {
        if (startedAt == null || this.cooldownSeconds <= 0) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - startedAt;
        int remaining = (int)(((long)this.cooldownSeconds * 1000L - elapsed) / 1000L);
        return Math.max(0, remaining);
    }

    private void broadcastToSquad(String team, int squadId, String message) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        for (ServerPlayer onlinePlayer : server.m_6846_().m_11314_()) {
            if (!team.equals(Espetro.getPlayerTeam(onlinePlayer)) || SquadManager.getInstance().getPlayerSquadId(onlinePlayer.m_20148_()) != squadId) continue;
            onlinePlayer.m_213846_(Component.m_237113_(message));
        }
    }

    public static class TeamPackData {
        public final UUID teamPackId;
        public final String team;
        public final int squadId;
        public final BlockPos pos;
        public final ServerLevel level;
        public final int maxHealth;
        public int health;
        public boolean active = true;
        public long nextWaveAt;

        public TeamPackData(UUID teamPackId, String team, int squadId, BlockPos pos, ServerLevel level, int maxHealth) {
            this.teamPackId = teamPackId;
            this.team = team;
            this.squadId = squadId;
            this.pos = pos.m_7949_();
            this.level = level;
            this.health = this.maxHealth = Math.max(1, maxHealth);
            this.nextWaveAt = System.currentTimeMillis() + (long)TeamPackManager.getInstance().waveSeconds * 1000L;
        }

        public BlockPos getSpawnPos() {
            return this.pos.m_7494_();
        }
    }

    private static final class SquadKey {
        private final String team;
        private final int squadId;

        private SquadKey(String team, int squadId) {
            this.team = team;
            this.squadId = squadId;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof SquadKey)) {
                return false;
            }
            SquadKey other = (SquadKey)obj;
            return this.squadId == other.squadId && Objects.equals(this.team, other.team);
        }

        public int hashCode() {
            return Objects.hash(this.team, this.squadId);
        }
    }

    private record PendingRallyRespawn(UUID teamPackId, long spawnAt) {
    }

    public record RallySnapshot(UUID id, String team, int squadId, String dimension, int x, int y, int z, long nextWaveAtMillis) {
        public long nextWaveSeconds() {
            return Math.max(0L, (this.nextWaveAtMillis - System.currentTimeMillis() + 999L) / 1000L);
        }
    }
}

