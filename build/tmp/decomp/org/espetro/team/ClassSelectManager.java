/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.Espetro;
import org.espetro.config.GameConfig;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.network.NetworkManager;
import org.espetro.team.ClassCountManager;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;
import org.espetro.team.TeamDisplayNames;
import org.espetro.team.VoteManager;

public class ClassSelectManager {
    private static ClassSelectManager INSTANCE;
    private boolean selectingActive = false;
    private String currentSelectingTeam = null;
    private int selectTickCounter = 0;
    private static final int TICKS_PER_SECOND = 20;
    private final Map<UUID, String> attackFactionVotes = new HashMap<UUID, String>();
    private final Map<UUID, String> defendFactionVotes = new HashMap<UUID, String>();
    private String finalAttackClass = null;
    private String finalDefendClass = null;
    private List<String> selectedFactionPool = null;

    private ClassSelectManager() {
        INSTANCE = this;
    }

    public static ClassSelectManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClassSelectManager();
        }
        return INSTANCE;
    }

    public static void init() {
        INSTANCE = new ClassSelectManager();
    }

    public void initFactionPool() {
        this.generateFactionPool();
    }

    public void startDefendSelecting() {
        this.selectingActive = true;
        this.currentSelectingTeam = "DEFEND";
        this.selectTickCounter = 0;
        this.defendFactionVotes.clear();
        int timeout = GameConfig.getDefendFactionSelectSeconds();
        Espetro.LOGGER.info("\u5b88\u65b9\u7f16\u5236\u9009\u62e9\u5f00\u59cb\uff01\u9650\u65f6{}\u79d2", (Object)timeout);
        NetworkManager.broadcastClassSelectScreenForTeam("DEFEND", timeout);
    }

    public void startAttackSelecting() {
        this.selectingActive = true;
        this.currentSelectingTeam = "ATTACK";
        this.selectTickCounter = 0;
        this.attackFactionVotes.clear();
        int timeout = GameConfig.getAttackFactionSelectSeconds();
        Espetro.LOGGER.info("\u653b\u65b9\u7f16\u5236\u9009\u62e9\u5f00\u59cb\uff01\u9650\u65f6{}\u79d2", (Object)timeout);
        NetworkManager.broadcastClassSelectScreenForTeam("ATTACK", timeout);
    }

    private void generateFactionPool() {
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        ArrayList<FactionDataLoader.FactionData> allFactions = new ArrayList<FactionDataLoader.FactionData>();
        ActiveMapConfig ctx = BattlefieldContext.getOrNull();
        Espetro.LOGGER.info("[\u7f16\u5236\u6c60\u8bca\u65ad] \u5f00\u59cb\u751f\u6210\u7f16\u5236\u6c60, BattlefieldContext={}, poolSize={}, \u603b\u7f16\u5236\u6570={}", new Object[]{ctx != null ? ctx.displayName : "null", GameConfig.getFactionPoolSize(), loader.getFactionArray().length});
        for (FactionDataLoader.FactionData faction2 : loader.getFactionArray()) {
            if (faction2 == null || faction2.id == null || faction2.id.isEmpty()) continue;
            int classCount = loader.getClassesForFaction(faction2.id).length;
            boolean compatible = loader.isCompatibleWithMap(faction2.id, ctx);
            if (classCount == 0) {
                Espetro.LOGGER.warn("[\u7f16\u5236\u6c60\u8bca\u65ad] {} \u2192 \u6392\u9664: \u804c\u4e1a\u6570\u4e3a0", (Object)faction2.id);
                continue;
            }
            if (!compatible) {
                Espetro.LOGGER.warn("[\u7f16\u5236\u6c60\u8bca\u65ad] {} \u2192 \u6392\u9664: isCompatibleWithMap \u8fd4\u56de false", (Object)faction2.id);
                continue;
            }
            Espetro.LOGGER.info("[\u7f16\u5236\u6c60\u8bca\u65ad] {} \u2192 \u901a\u8fc7 (\u804c\u4e1a\u6570={})", (Object)faction2.id, (Object)classCount);
            allFactions.add(faction2);
        }
        Espetro.LOGGER.info("[\u7f16\u5236\u6c60\u8bca\u65ad] \u517c\u5bb9\u7f16\u5236\u6570: {} (\u5171{}\u4e2a\u7f16\u5236)", (Object)allFactions.size(), (Object)loader.getFactionArray().length);
        int poolSize = GameConfig.getFactionPoolSize();
        Collections.shuffle(allFactions, new Random());
        this.selectedFactionPool = new ArrayList<String>();
        for (int i = 0; i < Math.min(poolSize, allFactions.size()); ++i) {
            this.selectedFactionPool.add(((FactionDataLoader.FactionData)allFactions.get((int)i)).id);
        }
        long distinctAffiliations = allFactions.stream().map(faction -> faction.factionId).filter(Objects::nonNull).distinct().count();
        if (distinctAffiliations < 2L) {
            Espetro.LOGGER.warn("\u53ef\u73a9\u7f16\u5236\u53ea\u6709 {} \u4e2a\u4e0d\u540c faction_id\uff0c\u653b\u5b88\u53cc\u65b9\u53ef\u80fd\u65e0\u6cd5\u9009\u62e9\u4e92\u4e0d\u51b2\u7a81\u7684\u9635\u8425", (Object)distinctAffiliations);
        }
        Espetro.LOGGER.info("\u672c\u5c40\u7f16\u5236\u6c60\uff08{}\u4e2a\uff09\uff1a{}", (Object)this.selectedFactionPool.size(), this.selectedFactionPool);
    }

    public List<String> getSelectedFactionPool() {
        return this.selectedFactionPool;
    }

    public List<String> getAvailableFactionPoolForTeam(String team) {
        List<String> source = this.selectedFactionPool != null && !this.selectedFactionPool.isEmpty() ? this.selectedFactionPool : this.getAllPlayableFactionIds();
        int targetSize = Math.max(1, GameConfig.getFactionPoolSize());
        LinkedHashSet<String> available = new LinkedHashSet<String>();
        for (String factionId : source) {
            if (!this.isFactionAvailableForTeam(team, factionId)) continue;
            available.add(factionId);
            if (available.size() < targetSize) continue;
            break;
        }
        if (available.size() < targetSize) {
            for (String factionId : this.getAllPlayableFactionIds()) {
                if (!this.isFactionAvailableForTeam(team, factionId)) continue;
                available.add(factionId);
                if (available.size() < targetSize) continue;
                break;
            }
        }
        return new ArrayList<String>(available);
    }

    public boolean isFactionSelectableForTeam(String team, String factionId) {
        if (team == null || factionId == null || factionId.isBlank()) {
            return false;
        }
        return this.getAvailableFactionPoolForTeam(team).contains(factionId);
    }

    private boolean isFactionAvailableForTeam(String team, String factionId) {
        if (factionId == null || factionId.isBlank()) {
            return false;
        }
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        if (!loader.isCompatibleWithMap(factionId, BattlefieldContext.getOrNull())) {
            return false;
        }
        String opponentFaction = "ATTACK".equals(team) ? this.finalDefendClass : ("DEFEND".equals(team) ? this.finalAttackClass : null);
        return opponentFaction == null || !this.hasSameFactionId(factionId, opponentFaction);
    }

    private boolean hasSameFactionId(String firstFormationId, String secondFormationId) {
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        FactionDataLoader.FactionData first = loader.getFaction(firstFormationId);
        FactionDataLoader.FactionData second = loader.getFaction(secondFormationId);
        if (first == null || second == null || first.factionId == null || second.factionId == null) {
            return false;
        }
        return first.factionId.equals(second.factionId);
    }

    private List<String> getAllPlayableFactionIds() {
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        ArrayList<String> ids = new ArrayList<String>();
        for (FactionDataLoader.FactionData faction : loader.getFactionArray()) {
            if (faction == null || faction.id == null || faction.id.isEmpty() || loader.getClassesForFaction(faction.id).length == 0 || !loader.isCompatibleWithMap(faction.id, BattlefieldContext.getOrNull())) continue;
            ids.add(faction.id);
        }
        Collections.sort(ids);
        return ids;
    }

    public boolean selectClass(ServerPlayer voter, String classId) {
        if (!this.selectingActive) {
            return false;
        }
        String team = Espetro.getPlayerTeam(voter);
        if (team == null || !team.equals(this.currentSelectingTeam)) {
            Espetro.sendToPlayer(voter, "\u00a7c\u5f53\u524d\u4e0d\u662f\u4f60\u6240\u5728\u9635\u8425\u7684\u7f16\u5236\u6295\u7968\u65f6\u95f4\uff01");
            return false;
        }
        if (!this.isFactionSelectableForTeam(team, classId)) {
            Espetro.sendToPlayer(voter, "\u00a7c\u8be5\u7f16\u5236\u5f53\u524d\u4e0d\u53ef\u9009\uff0c\u53ef\u80fd\u5df2\u88ab\u5bf9\u65b9\u9009\u62e9\u6216\u4e0d\u5728\u672c\u5c40\u5019\u9009\u6c60\u4e2d\uff01");
            return false;
        }
        Map<UUID, String> votes = "ATTACK".equals(team) ? this.attackFactionVotes : this.defendFactionVotes;
        votes.put(voter.m_20148_(), classId);
        Espetro.LOGGER.info("{} \u73a9\u5bb6 {} \u6295\u7968\u7f16\u5236: {}", new Object[]{team, voter.m_7755_().getString(), classId});
        NetworkManager.sendClassSelectScreenForTeam(team, this.getRemainingSeconds());
        return true;
    }

    public Map<String, Integer> getFactionVoteCounts(String team) {
        Map<UUID, String> votes = "ATTACK".equals(team) ? this.attackFactionVotes : this.defendFactionVotes;
        HashMap<String, Integer> counts = new HashMap<String, Integer>();
        for (String factionId : this.getAvailableFactionPoolForTeam(team)) {
            counts.put(factionId, 0);
        }
        for (String target : votes.values()) {
            if (!counts.containsKey(target)) continue;
            counts.merge(target, 1, Integer::sum);
        }
        return counts;
    }

    public String getPlayerFactionVote(UUID playerId, String team) {
        Map<UUID, String> votes = "ATTACK".equals(team) ? this.attackFactionVotes : this.defendFactionVotes;
        return votes.get(playerId);
    }

    public void removePlayerVote(UUID playerId) {
        if (playerId == null) {
            return;
        }
        boolean changed = this.attackFactionVotes.remove(playerId) != null;
        if ((changed |= this.defendFactionVotes.remove(playerId) != null) && this.selectingActive && this.currentSelectingTeam != null) {
            NetworkManager.sendClassSelectScreenForTeam(this.currentSelectingTeam, this.getRemainingSeconds());
        }
    }

    private String getClassDisplayName(String classId) {
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        FactionDataLoader.FactionData faction = loader.getFaction(classId);
        if (faction != null) {
            return faction.name;
        }
        FactionDataLoader.ClassKitData kit = loader.getClassKit(classId);
        return kit != null ? kit.name : classId;
    }

    public String finishCurrentSelecting() {
        if (!this.selectingActive) {
            return null;
        }
        String finishedTeam = this.currentSelectingTeam;
        this.selectingActive = false;
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return finishedTeam;
        }
        if ("DEFEND".equals(finishedTeam)) {
            this.finalDefendClass = this.getWinningFaction(finishedTeam);
            String name = this.getClassDisplayName(this.finalDefendClass);
            Espetro.broadcastToTeam("DEFEND", "\u00a76===== " + TeamDisplayNames.displayName("DEFEND") + "\u7f16\u5236\u5df2\u786e\u5b9a: \u00a79" + name + "\u00a76 =====");
            Espetro.LOGGER.info("\u5b88\u65b9\u7f16\u5236\u9009\u62e9\u7ed3\u675f\uff01\u7f16\u5236: {}", (Object)name);
        } else {
            this.finalAttackClass = this.getWinningFaction(finishedTeam);
            String name = this.getClassDisplayName(this.finalAttackClass);
            Espetro.broadcastToTeam("ATTACK", "\u00a76===== " + TeamDisplayNames.displayName("ATTACK") + "\u7f16\u5236\u5df2\u786e\u5b9a: \u00a7c" + name + "\u00a76 =====");
            Espetro.LOGGER.info("\u653b\u65b9\u7f16\u5236\u9009\u62e9\u7ed3\u675f\uff01\u7f16\u5236: {}", (Object)name);
        }
        return finishedTeam;
    }

    private String getWinningFaction(String team) {
        Map<String, Integer> counts = this.getFactionVoteCounts(team);
        int maxVotes = counts.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        if (maxVotes <= 0) {
            return this.getRandomFactionFromPool(team);
        }
        ArrayList<String> winners = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() != maxVotes) continue;
            winners.add(entry.getKey());
        }
        return (String)winners.get(new Random().nextInt(winners.size()));
    }

    private String getRandomFactionFromPool(String team) {
        List<String> available = this.getAvailableFactionPoolForTeam(team);
        if (available.isEmpty()) {
            Espetro.LOGGER.warn("{} \u65b9\u65e0\u53ef\u7528\u7f16\u5236\uff0c\u65e0\u6cd5\u968f\u673a\u9009\u62e9", (Object)team);
            return null;
        }
        return available.get(new Random().nextInt(available.size()));
    }

    public void finalizeSelection() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        String attackClassName = this.getClassDisplayName(this.finalAttackClass);
        String defendClassName = this.getClassDisplayName(this.finalDefendClass);
        Espetro.broadcastToAll("\u00a76========================================");
        Espetro.broadcastToAll("\u00a76\u2605 \u653b\u65b9\u7f16\u5236: \u00a7c" + attackClassName + " \u00a77| \u00a79\u5b88\u65b9\u7f16\u5236: " + defendClassName + " \u00a76\u2605");
        Espetro.broadcastToAll("\u00a76========================================");
        this.updatePlayerFactions(server);
    }

    private void updatePlayerFactions(MinecraftServer server) {
        ClassCountManager countManager = ClassCountManager.getInstance();
        VoteManager voteManager = VoteManager.getInstance();
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            String factionId;
            UUID uuid = player.m_20148_();
            String team = null;
            if (voteManager.getAttackPlayers().contains(uuid)) {
                team = "ATTACK";
            } else if (voteManager.getDefendPlayers().contains(uuid)) {
                team = "DEFEND";
            }
            if (team == null || (factionId = "ATTACK".equals(team) ? this.finalAttackClass : this.finalDefendClass) == null) continue;
            countManager.setPlayerFaction(uuid, factionId);
            Espetro.LOGGER.info("\u73a9\u5bb6 {} \u7684\u9635\u8425\u66f4\u65b0\u4e3a: {}", (Object)player.m_7755_().getString(), (Object)factionId);
        }
    }

    public void onServerTick() {
        if (!this.selectingActive) {
            return;
        }
        ++this.selectTickCounter;
        int timeout = this.getCurrentTimeoutSeconds();
        int secondsRemaining = timeout - this.selectTickCounter / 20;
        if (this.selectTickCounter % 20 == 0) {
            NetworkManager.broadcastClassSelectTimerForTeam(this.currentSelectingTeam, secondsRemaining);
        }
    }

    private int getCurrentTimeoutSeconds() {
        if ("DEFEND".equals(this.currentSelectingTeam)) {
            return GameConfig.getDefendFactionSelectSeconds();
        }
        if ("ATTACK".equals(this.currentSelectingTeam)) {
            return GameConfig.getAttackFactionSelectSeconds();
        }
        return 30;
    }

    public boolean isCurrentSelectTimedOut() {
        if (!this.selectingActive) {
            return false;
        }
        int timeout = this.getCurrentTimeoutSeconds();
        return this.selectTickCounter >= timeout * 20;
    }

    public boolean isSelectingActive() {
        return this.selectingActive;
    }

    public String getCurrentSelectingTeam() {
        return this.currentSelectingTeam;
    }

    public int getRemainingSeconds() {
        if (!this.selectingActive) {
            return 0;
        }
        int timeout = this.getCurrentTimeoutSeconds();
        return Math.max(0, timeout - this.selectTickCounter / 20);
    }

    public Set<String> getAttackSelectedClasses() {
        return new HashSet<String>(this.attackFactionVotes.values());
    }

    public Set<String> getDefendSelectedClasses() {
        return new HashSet<String>(this.defendFactionVotes.values());
    }

    public String getFinalAttackClass() {
        return this.finalAttackClass;
    }

    public String getFinalDefendClass() {
        return this.finalDefendClass;
    }

    public void reset() {
        this.selectingActive = false;
        this.currentSelectingTeam = null;
        this.selectTickCounter = 0;
        this.attackFactionVotes.clear();
        this.defendFactionVotes.clear();
        this.finalAttackClass = null;
        this.finalDefendClass = null;
        this.selectedFactionPool = null;
    }
}

