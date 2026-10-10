/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.PacketDistributor
 */
package org.espetro.team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.Espetro;
import org.espetro.config.GameConfig;
import org.espetro.governance.CommanderGovernanceManager;
import org.espetro.network.NetworkManager;
import org.espetro.network.VoteDataPacket;
import org.espetro.team.ClassCountManager;
import org.espetro.team.GameStateManager;

public class VoteManager {
    private static VoteManager INSTANCE;
    private boolean votingActive = false;
    private String currentVotingTeam = null;
    private int voteTickCounter = 0;
    private static final int TICKS_PER_SECOND = 20;
    private final Map<UUID, UUID> attackVotes = new HashMap<UUID, UUID>();
    private final Map<UUID, UUID> defendVotes = new HashMap<UUID, UUID>();
    private final Set<UUID> attackPlayers = new HashSet<UUID>();
    private final Set<UUID> defendPlayers = new HashSet<UUID>();
    private UUID attackCommander = null;
    private UUID defendCommander = null;

    private VoteManager() {
        INSTANCE = this;
    }

    public static VoteManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VoteManager();
        }
        return INSTANCE;
    }

    public static void init() {
        INSTANCE = new VoteManager();
    }

    public void initPlayers() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        this.attackPlayers.clear();
        this.defendPlayers.clear();
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            String factionId = ClassCountManager.getInstance().getPlayerFaction(player.m_20148_());
            if (factionId == null) continue;
            String team = GameStateManager.getTeamFromFactionStatic(factionId);
            if ("ATTACK".equals(team)) {
                this.attackPlayers.add(player.m_20148_());
                continue;
            }
            this.defendPlayers.add(player.m_20148_());
        }
        Espetro.LOGGER.info("\u6307\u6325\u5b98\u6295\u7968\u521d\u59cb\u5316\uff01\u653b\u65b9{}\u4eba\uff0c\u5b88\u65b9{}\u4eba", (Object)this.attackPlayers.size(), (Object)this.defendPlayers.size());
    }

    public void startDefendVote() {
        this.votingActive = true;
        this.currentVotingTeam = "DEFEND";
        this.voteTickCounter = 0;
        this.defendVotes.clear();
        int timeout = GameConfig.getDefendCommanderVoteSeconds();
        Espetro.LOGGER.info("\u5b88\u65b9\u6307\u6325\u5b98\u6295\u7968\u5f00\u59cb\uff01\u9650\u65f6{}\u79d2", (Object)timeout);
        NetworkManager.broadcastCommanderVoteScreenForTeam("DEFEND", timeout);
    }

    public void startAttackVote() {
        this.votingActive = true;
        this.currentVotingTeam = "ATTACK";
        this.voteTickCounter = 0;
        this.attackVotes.clear();
        int timeout = GameConfig.getAttackCommanderVoteSeconds();
        Espetro.LOGGER.info("\u653b\u65b9\u6307\u6325\u5b98\u6295\u7968\u5f00\u59cb\uff01\u9650\u65f6{}\u79d2", (Object)timeout);
        NetworkManager.broadcastCommanderVoteScreenForTeam("ATTACK", timeout);
    }

    public boolean castVote(ServerPlayer voter, UUID targetUUID) {
        if (!this.votingActive) {
            return false;
        }
        if (voter.m_20148_().equals(targetUUID)) {
            return false;
        }
        String voterTeam = this.getPlayerTeam(voter.m_20148_());
        if (voterTeam == null || !voterTeam.equals(this.currentVotingTeam)) {
            Espetro.sendToPlayer(voter, "\u00a7c\u5f53\u524d\u4e0d\u662f\u4f60\u7684\u6295\u7968\u65f6\u95f4\uff01");
            return false;
        }
        String targetTeam = this.getPlayerTeam(targetUUID);
        if (targetTeam == null || !targetTeam.equals(this.currentVotingTeam)) {
            return false;
        }
        if ("ATTACK".equals(this.currentVotingTeam)) {
            this.attackVotes.put(voter.m_20148_(), targetUUID);
            Espetro.LOGGER.info("\u73a9\u5bb6 {} \u6295\u7968\u7ed9\u653b\u65b9\u73a9\u5bb6 {}", (Object)voter.m_7755_().getString(), (Object)targetUUID);
        } else {
            this.defendVotes.put(voter.m_20148_(), targetUUID);
            Espetro.LOGGER.info("\u73a9\u5bb6 {} \u6295\u7968\u7ed9\u5b88\u65b9\u73a9\u5bb6 {}", (Object)voter.m_7755_().getString(), (Object)targetUUID);
        }
        this.broadcastVoteUpdate();
        return true;
    }

    private String getPlayerTeam(UUID uuid) {
        if (this.attackPlayers.contains(uuid)) {
            return "ATTACK";
        }
        if (this.defendPlayers.contains(uuid)) {
            return "DEFEND";
        }
        return null;
    }

    public UUID getVoteTarget(UUID voterUUID) {
        if (this.attackVotes.containsKey(voterUUID)) {
            return this.attackVotes.get(voterUUID);
        }
        return this.defendVotes.get(voterUUID);
    }

    public int getVoteCount(UUID playerUUID) {
        int count = 0;
        Map<UUID, UUID> votes = "ATTACK".equals(this.currentVotingTeam) ? this.attackVotes : this.defendVotes;
        for (UUID target : votes.values()) {
            if (!target.equals(playerUUID)) continue;
            ++count;
        }
        return count;
    }

    private UUID getWinningCandidate(Set<UUID> players, Map<UUID, UUID> votes) {
        if (players.isEmpty()) {
            return null;
        }
        HashMap<UUID, Integer> voteCounts = new HashMap<UUID, Integer>();
        for (UUID uUID : players) {
            voteCounts.put(uUID, 0);
        }
        for (UUID uUID : votes.values()) {
            voteCounts.merge(uUID, 1, Integer::sum);
        }
        int maxVotes = 0;
        Iterator iterator = voteCounts.values().iterator();
        while (iterator.hasNext()) {
            int count = (Integer)iterator.next();
            if (count <= maxVotes) continue;
            maxVotes = count;
        }
        ArrayList<UUID> arrayList = new ArrayList<UUID>();
        for (Map.Entry entry : voteCounts.entrySet()) {
            if ((Integer)entry.getValue() != maxVotes) continue;
            arrayList.add((UUID)entry.getKey());
        }
        if (arrayList.size() > 1) {
            return (UUID)arrayList.get(new Random().nextInt(arrayList.size()));
        }
        return arrayList.isEmpty() ? null : (UUID)arrayList.get(0);
    }

    public String finishCurrentVote() {
        if (!this.votingActive) {
            return null;
        }
        String finishedTeam = this.currentVotingTeam;
        this.votingActive = false;
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return finishedTeam;
        }
        if ("DEFEND".equals(finishedTeam)) {
            this.defendCommander = this.getWinningCandidate(this.defendPlayers, this.defendVotes);
            String name = this.getPlayerName(server, this.defendCommander);
            Espetro.broadcastToTeam("DEFEND", "\u00a76\u2605 \u4f60\u6240\u5728\u7684\u961f\u4f0d\u6307\u6325\u5b98\u4e3a\u00a79" + name + "\u00a76\uff01\u2605");
            if (this.defendCommander != null) {
                ServerPlayer cmd = server.m_6846_().m_11259_(this.defendCommander);
                Espetro.sendToPlayer(cmd, "\u00a7a\u4f60\u5df2\u88ab\u9009\u4e3a\u00a79\u5b88\u65b9\u00a7a\u6307\u6325\u5b98\uff01");
                NetworkManager.sendCommanderSkillSync(cmd);
            }
            Espetro.LOGGER.info("\u5b88\u65b9\u6307\u6325\u5b98\u6295\u7968\u7ed3\u675f\uff01\u6307\u6325\u5b98: {}", (Object)name);
            CommanderGovernanceManager.getInstance().acceptElectionResult("DEFEND", this.defendCommander);
        } else {
            this.attackCommander = this.getWinningCandidate(this.attackPlayers, this.attackVotes);
            String name = this.getPlayerName(server, this.attackCommander);
            Espetro.broadcastToTeam("ATTACK", "\u00a76\u2605 \u4f60\u6240\u5728\u7684\u961f\u4f0d\u6307\u6325\u5b98\u4e3a\u00a7c" + name + "\u00a76\uff01\u2605");
            if (this.attackCommander != null) {
                ServerPlayer cmd = server.m_6846_().m_11259_(this.attackCommander);
                Espetro.sendToPlayer(cmd, "\u00a7a\u4f60\u5df2\u88ab\u9009\u4e3a\u00a7c\u653b\u65b9\u00a7a\u6307\u6325\u5b98\uff01");
                NetworkManager.sendCommanderSkillSync(cmd);
            }
            Espetro.LOGGER.info("\u653b\u65b9\u6307\u6325\u5b98\u6295\u7968\u7ed3\u675f\uff01\u6307\u6325\u5b98: {}", (Object)name);
            CommanderGovernanceManager.getInstance().acceptElectionResult("ATTACK", this.attackCommander);
        }
        return finishedTeam;
    }

    private String getPlayerName(MinecraftServer server, UUID uuid) {
        if (uuid == null) {
            return "\u65e0";
        }
        ServerPlayer player = server.m_6846_().m_11259_(uuid);
        return player != null ? player.m_7755_().getString() : "\u79bb\u7ebf\u73a9\u5bb6";
    }

    private void broadcastVoteUpdate() {
        if (!this.votingActive || this.currentVotingTeam == null) {
            return;
        }
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        Set<UUID> activePlayers = "ATTACK".equals(this.currentVotingTeam) ? this.attackPlayers : this.defendPlayers;
        Set<UUID> waitingPlayers = "ATTACK".equals(this.currentVotingTeam) ? this.defendPlayers : this.attackPlayers;
        Map<UUID, UUID> votes = "ATTACK".equals(this.currentVotingTeam) ? this.attackVotes : this.defendVotes;
        HashMap<String, Integer> voteCounts = new HashMap<String, Integer>();
        for (UUID uuid : activePlayers) {
            ServerPlayer p = server.m_6846_().m_11259_(uuid);
            if (p == null) continue;
            int count = 0;
            for (UUID target : votes.values()) {
                if (!target.equals(uuid)) continue;
                ++count;
            }
            voteCounts.put(p.m_7755_().getString(), count);
        }
        int remaining = this.getRemainingSeconds();
        VoteDataPacket activePacket = new VoteDataPacket(voteCounts, remaining, -1);
        for (UUID uuid : activePlayers) {
            ServerPlayer p = server.m_6846_().m_11259_(uuid);
            if (p == null) continue;
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> p), (Object)activePacket);
        }
        VoteDataPacket waitingPacket = new VoteDataPacket(Collections.emptyMap(), 0, remaining);
        for (UUID uuid : waitingPlayers) {
            ServerPlayer p = server.m_6846_().m_11259_(uuid);
            if (p == null) continue;
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> p), (Object)waitingPacket);
        }
    }

    public void onServerTick() {
        if (!this.votingActive) {
            return;
        }
        ++this.voteTickCounter;
        int timeout = this.getCurrentTimeoutSeconds();
        int secondsRemaining = timeout - this.voteTickCounter / 20;
        if (this.voteTickCounter % 20 == 0) {
            this.broadcastVoteUpdate();
        }
    }

    private int getCurrentTimeoutSeconds() {
        if ("DEFEND".equals(this.currentVotingTeam)) {
            return GameConfig.getDefendCommanderVoteSeconds();
        }
        if ("ATTACK".equals(this.currentVotingTeam)) {
            return GameConfig.getAttackCommanderVoteSeconds();
        }
        return 20;
    }

    public boolean isCurrentVoteTimedOut() {
        if (!this.votingActive) {
            return false;
        }
        int timeout = this.getCurrentTimeoutSeconds();
        return this.voteTickCounter >= timeout * 20;
    }

    public boolean isVotingActive() {
        return this.votingActive;
    }

    public String getCurrentVotingTeam() {
        return this.currentVotingTeam;
    }

    public int getRemainingSeconds() {
        if (!this.votingActive) {
            return 0;
        }
        int timeout = this.getCurrentTimeoutSeconds();
        return Math.max(0, timeout - this.voteTickCounter / 20);
    }

    public Set<UUID> getAttackPlayers() {
        return new HashSet<UUID>(this.attackPlayers);
    }

    public Set<UUID> getDefendPlayers() {
        return new HashSet<UUID>(this.defendPlayers);
    }

    public UUID getAttackCommander() {
        return this.attackCommander;
    }

    public void setAttackCommander(UUID uuid) {
        this.attackCommander = uuid;
    }

    public void setDefendCommander(UUID uuid) {
        this.defendCommander = uuid;
    }

    public UUID getDefendCommander() {
        return this.defendCommander;
    }

    public boolean isCommander(UUID uuid) {
        return this.attackCommander != null && this.attackCommander.equals(uuid) || this.defendCommander != null && this.defendCommander.equals(uuid);
    }

    public boolean isCommanderOf(UUID uuid, String team) {
        if ("ATTACK".equals(team)) {
            return this.attackCommander != null && this.attackCommander.equals(uuid);
        }
        return this.defendCommander != null && this.defendCommander.equals(uuid);
    }

    public void addAttackPlayer(UUID uuid) {
        this.defendPlayers.remove(uuid);
        this.attackPlayers.add(uuid);
    }

    public void removePlayer(UUID uuid) {
        if (uuid == null) {
            return;
        }
        this.attackPlayers.remove(uuid);
        this.defendPlayers.remove(uuid);
        this.attackVotes.remove(uuid);
        this.defendVotes.remove(uuid);
        this.attackVotes.entrySet().removeIf(entry -> uuid.equals(entry.getValue()));
        this.defendVotes.entrySet().removeIf(entry -> uuid.equals(entry.getValue()));
        if (this.votingActive) {
            this.broadcastVoteUpdate();
        }
    }

    public void addDefendPlayer(UUID uuid) {
        this.attackPlayers.remove(uuid);
        this.defendPlayers.add(uuid);
    }

    public void reset() {
        this.votingActive = false;
        this.currentVotingTeam = null;
        this.voteTickCounter = 0;
        this.attackVotes.clear();
        this.defendVotes.clear();
        this.attackPlayers.clear();
        this.defendPlayers.clear();
        this.attackCommander = null;
        this.defendCommander = null;
    }
}

