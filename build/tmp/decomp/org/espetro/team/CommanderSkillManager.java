/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.ModList
 */
package org.espetro.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.fml.ModList;
import org.espetro.Espetro;
import org.espetro.kubejs.commander.EspetroCommanderSkills;
import org.espetro.kubejs.commander.KubeCommanderSkillDefinition;
import org.espetro.kubejs.commander.KubeCommanderSkillEvent;
import org.espetro.network.NetworkManager;
import org.espetro.team.CommanderSkillType;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;
import org.espetro.team.SquadManager;
import org.espetro.team.VoteManager;

public class CommanderSkillManager {
    private static CommanderSkillManager INSTANCE;
    private static final String ESPOINTS_ARTILLERY_PACKET_CLASS_NAME = "com.example.espoints.network.OpenArtillerySupportMapMessage";
    private static final int MAX_ARTILLERY_REQUEST_HISTORY = 128;
    private final Map<UUID, Map<String, Long>> cooldownEndTicks = new HashMap<UUID, Map<String, Long>>();
    private final List<ArtillerySupportRequest> artillerySupportRequests = new ArrayList<ArtillerySupportRequest>();
    private final Map<UUID, String> pendingTargetSkillIds = new HashMap<UUID, String>();

    private CommanderSkillManager() {
        INSTANCE = this;
    }

    public static CommanderSkillManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CommanderSkillManager();
        }
        return INSTANCE;
    }

    public static void init() {
        INSTANCE = new CommanderSkillManager();
    }

    public boolean activateSkill(ServerPlayer commander, CommanderSkillType skillType) {
        if (commander == null || skillType == null) {
            return false;
        }
        return this.activateSkill(commander, skillType.getId());
    }

    public boolean activateSkill(ServerPlayer commander, String skillId) {
        boolean success;
        if (commander == null || skillId == null || skillId.isBlank()) {
            return false;
        }
        KubeCommanderSkillDefinition definition = EspetroCommanderSkills.getDefinition(skillId);
        if (definition == null) {
            Espetro.sendToPlayer(commander, "\u00a7c\u672a\u914d\u7f6e\u6280\u80fd: " + skillId + "\uff0c\u8bf7\u5728 KubeJS startup_scripts \u4e2d\u6ce8\u518c Espetro \u6280\u80fd\u3002");
            return false;
        }
        if (!this.canPlayerUseSkill(commander, definition)) {
            Espetro.sendToPlayer(commander, "\u00a7c\u4f60\u6ca1\u6709\u6743\u9650\u4f7f\u7528\u8be5\u6280\u80fd\uff08\u9700\u8981: " + definition.allowedRolesWire() + "\uff09");
            return false;
        }
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        if (phase != GamePhase.BATTLE && phase != GamePhase.DEPLOYING) {
            Espetro.sendToPlayer(commander, "\u00a7c\u5f53\u524d\u9636\u6bb5\u65e0\u6cd5\u4f7f\u7528\u6280\u80fd\uff01");
            return false;
        }
        if (this.isOnCooldown(commander.m_20148_(), skillId)) {
            int remaining = this.getRemainingCooldownSeconds(commander.m_20148_(), skillId);
            Espetro.sendToPlayer(commander, "\u00a7c\u6280\u80fd\u51b7\u5374\u4e2d\uff0c\u5269\u4f59 " + remaining + " \u79d2");
            return false;
        }
        if (definition.isTargetMapTrigger()) {
            success = this.beginArtilleryTargetSelection(commander, definition.id());
        } else {
            KubeCommanderSkillEvent event = EspetroCommanderSkills.event(definition, commander, this.normalizeTeam(Espetro.getPlayerTeam(commander)));
            success = EspetroCommanderSkills.execute(definition, event);
            if (success) {
                this.finishCommanderSkill(commander, skillId, definition.displayName(), (long)definition.cooldownSeconds() * 20L);
            }
        }
        return success;
    }

    public boolean canPlayerUseSkill(ServerPlayer player, KubeCommanderSkillDefinition definition) {
        if (player == null || definition == null) {
            return false;
        }
        UUID uuid = player.m_20148_();
        if (definition.allowsCommander() && VoteManager.getInstance().isCommander(uuid)) {
            return true;
        }
        return definition.allowsSquadLeader() && SquadManager.getInstance().isSquadLeader(uuid);
    }

    public boolean canPlayerUseSkill(ServerPlayer player, String skillId) {
        return this.canPlayerUseSkill(player, EspetroCommanderSkills.getDefinition(skillId));
    }

    public boolean beginArtilleryTargetSelection(ServerPlayer commander) {
        return this.beginArtilleryTargetSelection(commander, "artillery_155");
    }

    public boolean beginArtilleryTargetSelection(ServerPlayer commander, String skillId) {
        if (commander == null) {
            return false;
        }
        if (!ModList.get().isLoaded("espoints")) {
            Espetro.sendToPlayer(commander, "\u00a7c\u8be5\u6307\u6325\u5b98\u6280\u80fd\u9700\u8981\u5b89\u88c5 ESPoints \u624d\u80fd\u6253\u5f00\u6218\u672f\u5730\u56fe\u3002");
            return false;
        }
        try {
            Class<?> packetClass = Class.forName(ESPOINTS_ARTILLERY_PACKET_CLASS_NAME);
            packetClass.getMethod("sendTo", ServerPlayer.class).invoke(null, commander);
            this.pendingTargetSkillIds.put(commander.m_20148_(), skillId == null || skillId.isBlank() ? "artillery_155" : skillId);
            return true;
        }
        catch (ReflectiveOperationException e) {
            Espetro.LOGGER.warn("\u65e0\u6cd5\u901a\u8fc7 ESPoints \u6253\u5f00\u6307\u6325\u5b98\u6280\u80fd\u6218\u672f\u5730\u56fe", (Throwable)e);
            Espetro.sendToPlayer(commander, "\u00a7cESPoints \u4e0d\u652f\u6301\u6307\u6325\u5b98\u6280\u80fd\u9009\u70b9\u63a5\u53e3\uff0c\u8bf7\u786e\u8ba4\u53cc\u65b9\u6a21\u7ec4\u7248\u672c\u4e00\u81f4\u3002");
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean submitArtillerySupportTarget(ServerPlayer commander, double x, double z) {
        String skillName;
        if (commander == null || !Double.isFinite(x) || !Double.isFinite(z)) {
            return false;
        }
        UUID commanderId = commander.m_20148_();
        String skillId = this.pendingTargetSkillIds.getOrDefault(commanderId, "artillery_155");
        KubeCommanderSkillDefinition definition = EspetroCommanderSkills.getDefinition(skillId);
        String string = skillName = definition != null ? definition.displayName() : "\u9009\u70b9\u6280\u80fd";
        if (definition == null || !this.canPlayerUseSkill(commander, definition)) {
            Espetro.sendToPlayer(commander, "\u00a7c\u4f60\u6ca1\u6709\u6743\u9650\u63d0\u4ea4" + skillName + "\u5750\u6807\uff01");
            this.pendingTargetSkillIds.remove(commanderId);
            return false;
        }
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        if (phase != GamePhase.BATTLE && phase != GamePhase.DEPLOYING) {
            Espetro.sendToPlayer(commander, "\u00a7c\u5f53\u524d\u9636\u6bb5\u65e0\u6cd5\u63d0\u4ea4" + skillName + "\u5750\u6807\uff01");
            this.pendingTargetSkillIds.remove(commanderId);
            return false;
        }
        if (this.isOnCooldown(commander.m_20148_(), skillId)) {
            int remaining = this.getRemainingCooldownSeconds(commander.m_20148_(), skillId);
            Espetro.sendToPlayer(commander, "\u00a7c" + skillName + "\u51b7\u5374\u4e2d\uff0c\u5269\u4f59 " + remaining + " \u79d2");
            this.pendingTargetSkillIds.remove(commanderId);
            return false;
        }
        String commanderTeam = this.normalizeTeam(Espetro.getPlayerTeam(commander));
        if (commanderTeam == null) {
            Espetro.sendToPlayer(commander, "\u00a7c\u4f60\u4e0d\u5c5e\u4e8e\u4efb\u4f55\u9635\u8425\uff0c\u65e0\u6cd5\u63d0\u4ea4" + skillName + "\u5750\u6807\uff01");
            this.pendingTargetSkillIds.remove(commanderId);
            return false;
        }
        ServerLevel level = commander.m_284548_();
        BlockPos targetBlock = this.resolveArtilleryTargetBlock(level, x, z);
        ArtillerySupportRequest request = new ArtillerySupportRequest(commander.m_20148_(), commander.m_7755_().getString(), definition != null ? definition.id() : skillId, skillName, commanderTeam, level.m_46472_(), x, targetBlock.m_123342_(), z, targetBlock, level.m_46467_(), System.currentTimeMillis());
        if (definition == null) {
            Espetro.sendToPlayer(commander, "\u00a7c\u672a\u627e\u5230" + skillName + "\u914d\u7f6e\uff0c\u8bf7\u5728 KubeJS startup_scripts \u4e2d\u6ce8\u518c\u8be5\u6307\u6325\u5b98\u6280\u80fd\u3002");
            this.pendingTargetSkillIds.remove(commanderId);
            return false;
        }
        if (!definition.isTargetMapTrigger()) {
            Espetro.sendToPlayer(commander, "\u00a7c" + skillName + "\u4e0d\u518d\u662f\u6218\u672f\u5730\u56fe\u9009\u70b9\u6280\u80fd\uff0c\u8bf7\u91cd\u65b0\u6253\u5f00\u6280\u80fd\u754c\u9762\u3002");
            this.pendingTargetSkillIds.remove(commanderId);
            return false;
        }
        KubeCommanderSkillEvent event = EspetroCommanderSkills.targetEvent(definition, request, commander, level, targetBlock);
        if (!EspetroCommanderSkills.execute(definition, event)) {
            Espetro.sendToPlayer(commander, "\u00a7c" + skillName + "KubeJS \u56de\u8c03\u6267\u884c\u5931\u8d25\uff0c\u8bf7\u68c0\u67e5\u670d\u52a1\u7aef\u65e5\u5fd7\u3002");
            this.pendingTargetSkillIds.remove(commanderId);
            return false;
        }
        this.pendingTargetSkillIds.remove(commanderId);
        List<ArtillerySupportRequest> list = this.artillerySupportRequests;
        synchronized (list) {
            while (this.artillerySupportRequests.size() >= 128) {
                this.artillerySupportRequests.remove(0);
            }
            this.artillerySupportRequests.add(request);
        }
        this.finishCommanderSkill(commander, skillId, definition.displayName(), (long)definition.cooldownSeconds() * 20L);
        Espetro.sendToPlayer(commander, "\u00a7a" + skillName + "\u5750\u6807\u5df2\u63d0\u4ea4: " + targetBlock.m_123341_() + ", " + targetBlock.m_123342_() + ", " + targetBlock.m_123343_());
        Espetro.LOGGER.info("\u6307\u6325\u5b98 {} \u63d0\u4ea4 {} \u5750\u6807: {} {} {} ({})", new Object[]{commander.m_7755_().getString(), skillName, targetBlock.m_123341_(), targetBlock.m_123342_(), targetBlock.m_123343_(), request.getDimensionId()});
        return true;
    }

    private BlockPos resolveArtilleryTargetBlock(ServerLevel level, double x, double z) {
        int blockX = Mth.m_14107_(x);
        int blockZ = Mth.m_14107_(z);
        int y = level.m_6924_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockX, blockZ);
        return new BlockPos(blockX, y, blockZ);
    }

    private void finishCommanderSkill(ServerPlayer commander, String skillId, String displayName, long cooldownTicks) {
        this.cooldownEndTicks.computeIfAbsent(commander.m_20148_(), k -> new HashMap()).put(skillId, this.getServerTick() + cooldownTicks);
        NetworkManager.sendCommanderSkillSync(commander);
        String team = Espetro.getPlayerTeam(commander);
        if (team != null) {
            String roleLabel = VoteManager.getInstance().isCommander(commander.m_20148_()) ? "\u6307\u6325\u5b98" : (SquadManager.getInstance().isSquadLeader(commander.m_20148_()) ? "\u961f\u957f" : "\u73a9\u5bb6");
            Espetro.broadcastToTeam(team, "\u00a76\u26a1 " + roleLabel + " " + commander.m_7755_().getString() + " \u53d1\u52a8\u4e86 " + displayName + "\uff01");
        }
    }

    public boolean isOnCooldown(UUID uuid, CommanderSkillType type) {
        return type != null && this.isOnCooldown(uuid, type.getId());
    }

    public boolean isOnCooldown(UUID uuid, String skillId) {
        Map<String, Long> map = this.cooldownEndTicks.get(uuid);
        if (map == null) {
            return false;
        }
        Long endTick = map.get(skillId);
        if (endTick == null) {
            return false;
        }
        return this.getServerTick() < endTick;
    }

    public int getRemainingCooldownSeconds(UUID uuid, CommanderSkillType type) {
        return type == null ? 0 : this.getRemainingCooldownSeconds(uuid, type.getId());
    }

    public int getRemainingCooldownSeconds(UUID uuid, String skillId) {
        Map<String, Long> map = this.cooldownEndTicks.get(uuid);
        if (map == null) {
            return 0;
        }
        Long endTick = map.get(skillId);
        if (endTick == null) {
            return 0;
        }
        long remaining = endTick - this.getServerTick();
        return remaining <= 0L ? 0 : (int)Math.ceil((double)remaining / 20.0);
    }

    public Map<String, Integer> getCooldownData(UUID uuid) {
        HashMap<String, Integer> data = new HashMap<String, Integer>();
        for (SkillView view : this.getSkillViews()) {
            int remaining = this.getRemainingCooldownSeconds(uuid, view.id());
            data.put(view.id(), remaining);
        }
        return data;
    }

    public SkillStatus getSkillStatus(ServerPlayer commander, String skillId) {
        String normalizedSkillId = skillId == null ? "" : skillId.trim();
        KubeCommanderSkillDefinition definition = EspetroCommanderSkills.getDefinition(normalizedSkillId);
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        boolean commanderAllowed = commander != null && VoteManager.getInstance().isCommander(commander.m_20148_());
        boolean roleAllowed = this.canPlayerUseSkill(commander, definition);
        boolean phaseAllowed = phase == GamePhase.BATTLE || phase == GamePhase.DEPLOYING;
        int cooldownSeconds = commander == null ? 0 : this.getRemainingCooldownSeconds(commander.m_20148_(), normalizedSkillId);
        boolean onCooldown = cooldownSeconds > 0;
        boolean registered = definition != null;
        boolean canUse = registered && roleAllowed && phaseAllowed && !onCooldown;
        return new SkillStatus(normalizedSkillId, definition != null ? definition.displayName() : normalizedSkillId, registered, commanderAllowed, phaseAllowed, onCooldown, cooldownSeconds, definition != null && definition.isTargetMapTrigger(), canUse, phase.name());
    }

    public List<SkillView> getSkillViews() {
        HashMap<String, SkillView> views = new HashMap<String, SkillView>();
        for (KubeCommanderSkillDefinition definition : EspetroCommanderSkills.getDefinitions()) {
            views.put(definition.id(), CommanderSkillManager.toSkillView(definition));
        }
        return views.values().stream().sorted((a, b) -> a.id().compareTo(b.id())).toList();
    }

    public List<SkillView> getSkillViewsFor(ServerPlayer player) {
        if (player == null) {
            return List.of();
        }
        ArrayList<SkillView> views = new ArrayList<SkillView>();
        for (KubeCommanderSkillDefinition definition : EspetroCommanderSkills.getDefinitions()) {
            if (!this.canPlayerUseSkill(player, definition)) continue;
            views.add(CommanderSkillManager.toSkillView(definition));
        }
        views.sort((a, b) -> a.id().compareTo(b.id()));
        return views;
    }

    private static SkillView toSkillView(KubeCommanderSkillDefinition definition) {
        Object stats = definition.stats().isBlank() ? "\u00a78KubeJS | \u51b7\u5374: " + definition.cooldownSeconds() + "\u79d2" : definition.stats();
        return new SkillView(definition.id(), definition.displayName(), definition.description(), (String)stats, definition.icon());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ArtillerySupportRequest getLatestArtillerySupportRequest() {
        List<ArtillerySupportRequest> list = this.artillerySupportRequests;
        synchronized (list) {
            return this.artillerySupportRequests.isEmpty() ? null : this.artillerySupportRequests.get(this.artillerySupportRequests.size() - 1);
        }
    }

    public ArtillerySupportRequest getLatestCommanderSkillTargetRequest() {
        return this.getLatestArtillerySupportRequest();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<ArtillerySupportRequest> getArtillerySupportRequestsSnapshot() {
        List<ArtillerySupportRequest> list = this.artillerySupportRequests;
        synchronized (list) {
            return List.copyOf(this.artillerySupportRequests);
        }
    }

    public List<ArtillerySupportRequest> getCommanderSkillTargetRequestsSnapshot() {
        return this.getArtillerySupportRequestsSnapshot();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<ArtillerySupportRequest> drainArtillerySupportRequests() {
        List<ArtillerySupportRequest> list = this.artillerySupportRequests;
        synchronized (list) {
            List<ArtillerySupportRequest> drained = List.copyOf(this.artillerySupportRequests);
            this.artillerySupportRequests.clear();
            return drained;
        }
    }

    public List<ArtillerySupportRequest> drainCommanderSkillTargetRequests() {
        return this.drainArtillerySupportRequests();
    }

    private String normalizeTeam(String team) {
        if (team == null || team.isBlank()) {
            return null;
        }
        String normalized = team.toLowerCase(Locale.ROOT);
        if (normalized.contains("attack") || normalized.contains("attacker") || team.contains("\u8fdb\u653b") || team.contains("\u653b\u65b9")) {
            return "ATTACK";
        }
        if (normalized.contains("defend") || normalized.contains("defender") || team.contains("\u9632\u5b88") || team.contains("\u5b88\u65b9")) {
            return "DEFEND";
        }
        return null;
    }

    private long getServerTick() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return 0L;
        }
        return server.m_129921_();
    }

    public void onServerTick() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reset() {
        this.cooldownEndTicks.clear();
        this.pendingTargetSkillIds.clear();
        List<ArtillerySupportRequest> list = this.artillerySupportRequests;
        synchronized (list) {
            this.artillerySupportRequests.clear();
        }
    }

    public record ArtillerySupportRequest(UUID commanderId, String commanderName, String skillId, String skillName, String team, ResourceKey<Level> dimension, double x, double y, double z, BlockPos blockPos, long gameTime, long createdAtMillis) {
        public UUID getCommanderId() {
            return this.commanderId;
        }

        public String getCommanderName() {
            return this.commanderName;
        }

        public String getSkillId() {
            return this.skillId;
        }

        public String getSkillName() {
            return this.skillName;
        }

        public String getTeam() {
            return this.team;
        }

        public ResourceKey<Level> getDimension() {
            return this.dimension;
        }

        public String getDimensionId() {
            return this.dimension.m_135782_().toString();
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }

        public BlockPos getBlockPos() {
            return this.blockPos;
        }

        public int getBlockX() {
            return this.blockPos.m_123341_();
        }

        public int getBlockY() {
            return this.blockPos.m_123342_();
        }

        public int getBlockZ() {
            return this.blockPos.m_123343_();
        }

        public long getGameTime() {
            return this.gameTime;
        }

        public long getCreatedAtMillis() {
            return this.createdAtMillis;
        }

        public ServerPlayer getCommander() {
            MinecraftServer server = Espetro.getServer();
            return server == null ? null : server.m_6846_().m_11259_(this.commanderId);
        }
    }

    public record SkillView(String id, String displayName, String description, String stats, String icon) {
    }

    public record SkillStatus(String id, String displayName, boolean registered, boolean commander, boolean phaseAllowed, boolean onCooldown, int cooldownSeconds, boolean targetMap, boolean canUse, String phase) {
        public String getId() {
            return this.id;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public boolean isRegistered() {
            return this.registered;
        }

        public boolean isCommander() {
            return this.commander;
        }

        public boolean isPhaseAllowed() {
            return this.phaseAllowed;
        }

        public boolean isOnCooldown() {
            return this.onCooldown;
        }

        public int getCooldownSeconds() {
            return this.cooldownSeconds;
        }

        public boolean isTargetMap() {
            return this.targetMap;
        }

        public String getPhase() {
            return this.phase;
        }
    }
}

