/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.governance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.ToLongFunction;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.Espetro;
import org.espetro.config.GameConfig;
import org.espetro.network.NetworkManager;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;
import org.espetro.team.SquadManager;
import org.espetro.team.VoteManager;

public final class CommanderGovernanceManager {
    private static CommanderGovernanceManager INSTANCE;
    private long impeachmentCooldownUntilTick = 0L;
    private final Map<String, TeamGovernance> byTeam = new HashMap<String, TeamGovernance>();
    private final Map<UUID, Long> recentCommanderDisconnect = new HashMap<UUID, Long>();
    private final Map<UUID, String> recentCommanderTeam = new HashMap<UUID, String>();
    private static final long RECONNECT_GRACE_MILLIS = 120000L;

    private CommanderGovernanceManager() {
        INSTANCE = this;
        this.byTeam.put("ATTACK", new TeamGovernance());
        this.byTeam.put("DEFEND", new TeamGovernance());
    }

    public static CommanderGovernanceManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CommanderGovernanceManager();
        }
        return INSTANCE;
    }

    public static void init() {
        INSTANCE = new CommanderGovernanceManager();
    }

    public TeamGovernance getTeam(String team) {
        return this.byTeam.computeIfAbsent(team, t -> new TeamGovernance());
    }

    public void reset() {
        this.impeachmentCooldownUntilTick = 0L;
        for (TeamGovernance g : this.byTeam.values()) {
            g.state = State.IDLE;
            g.commander = null;
            g.challenger = null;
            g.votes.clear();
            g.volunteers.clear();
            g.tickCounter = 0;
            g.endGameTime = 0L;
        }
        this.recentCommanderDisconnect.clear();
        this.recentCommanderTeam.clear();
        NetworkManager.broadcastGovernanceState(this);
    }

    public void syncCommandersFromVoteManager() {
        VoteManager vm = VoteManager.getInstance();
        this.getTeam((String)"ATTACK").commander = vm.getAttackCommander();
        this.getTeam((String)"DEFEND").commander = vm.getDefendCommander();
        NetworkManager.broadcastGovernanceState(this);
        if (GameStateManager.getInstance().getCurrentPhase() != GamePhase.BATTLE) {
            return;
        }
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        for (String team : List.of("ATTACK", "DEFEND")) {
            UUID commander = this.getTeam((String)team).commander;
            if (commander == null || server.m_6846_().m_11259_(commander) != null) continue;
            this.clearCommander(team, "offline_at_battle_start");
            this.startVacancy(team);
        }
    }

    public void acceptElectionResult(String team, @Nullable UUID commander) {
        if (!"ATTACK".equals(team) && !"DEFEND".equals(team)) {
            return;
        }
        TeamGovernance governance = this.getTeam(team);
        governance.state = State.IDLE;
        governance.commander = commander;
        governance.challenger = null;
        governance.votes.clear();
        governance.volunteers.clear();
        governance.tickCounter = 0;
        governance.endGameTime = 0L;
        NetworkManager.syncSquadsToTeam(team);
        NetworkManager.broadcastGovernanceState(this);
        Espetro.LOGGER.info("\u6307\u6325\u5b98\u9009\u4e3e\u7ed3\u679c\u5df2\u540c\u6b65\u5230\u6cbb\u7406\u72b6\u6001: team={}, commander={}", (Object)team, (Object)commander);
    }

    public boolean tryStartImpeachment(ServerPlayer initiator) {
        if (GameStateManager.getInstance().getCurrentPhase() != GamePhase.BATTLE) {
            return this.fail(initiator, "\u4ec5\u6218\u6597\u9636\u6bb5\u53ef\u5f39\u52be\u6307\u6325\u5b98");
        }
        String team = Espetro.getPlayerTeam(initiator);
        if (team == null) {
            return this.fail(initiator, "\u4f60\u5c1a\u672a\u52a0\u5165\u9635\u8425");
        }
        if (!SquadManager.getInstance().isSquadLeader(initiator.m_20148_())) {
            return this.fail(initiator, "\u53ea\u6709\u5c0f\u961f\u957f\u53ef\u4ee5\u53d1\u8d77\u5f39\u52be");
        }
        TeamGovernance g = this.getTeam(team);
        if (g.state != State.IDLE) {
            return this.fail(initiator, "\u5f53\u524d\u5df2\u6709\u6cbb\u7406\u6d41\u7a0b\u8fdb\u884c\u4e2d");
        }
        if (initiator.m_20148_().equals(g.commander)) {
            return this.fail(initiator, "\u6307\u6325\u5b98\u4e0d\u80fd\u5f39\u52be\u81ea\u5df1");
        }
        MinecraftServer server = initiator.f_8924_;
        if (server.m_129783_().m_46467_() < this.impeachmentCooldownUntilTick) {
            long left = (this.impeachmentCooldownUntilTick - server.m_129783_().m_46467_()) / 20L;
            return this.fail(initiator, "\u5f39\u52be\u51b7\u5374\u4e2d\uff0c\u5269\u4f59 " + left + " \u79d2");
        }
        if (g.commander == null) {
            return this.fail(initiator, "\u5f53\u524d\u6ca1\u6709\u6307\u6325\u5b98\u53ef\u5f39\u52be");
        }
        this.impeachmentCooldownUntilTick = server.m_129783_().m_46467_() + (long)GameConfig.getImpeachmentCooldownSeconds() * 20L;
        g.state = State.IMPEACHMENT_VOTE;
        g.challenger = initiator.m_20148_();
        g.votes.clear();
        g.tickCounter = 0;
        g.timeoutSeconds = GameConfig.getImpeachmentVoteSeconds();
        g.endGameTime = server.m_129783_().m_46467_() + (long)g.timeoutSeconds * 20L;
        Espetro.broadcastToTeam(team, "\u00a7e\u2694 \u5c0f\u961f\u957f " + initiator.m_7755_().getString() + " \u53d1\u8d77\u5f39\u52be\uff01\u6309 J \u6253\u5f00\u6218\u672f\u9762\u677f\u6295\u7968\uff08" + g.timeoutSeconds + "\u79d2\uff09");
        NetworkManager.broadcastGovernanceState(this);
        return true;
    }

    public boolean castImpeachmentVote(ServerPlayer voter, UUID candidate) {
        String team = Espetro.getPlayerTeam(voter);
        if (team == null) {
            return this.fail(voter, "\u4f60\u5c1a\u672a\u52a0\u5165\u9635\u8425");
        }
        TeamGovernance g = this.getTeam(team);
        if (g.state != State.IMPEACHMENT_VOTE) {
            return this.fail(voter, "\u5f53\u524d\u6ca1\u6709\u8fdb\u884c\u4e2d\u7684\u5f39\u52be\u6295\u7968");
        }
        if (candidate == null || !candidate.equals(g.commander) && !candidate.equals(g.challenger)) {
            return this.fail(voter, "\u65e0\u6548\u7684\u5019\u9009\u4eba");
        }
        g.votes.put(voter.m_20148_(), candidate);
        String name = this.playerName(candidate);
        voter.m_5661_(Component.m_237113_("\u00a7a\u5df2\u6295\u7968\u7ed9 " + name), true);
        NetworkManager.broadcastGovernanceState(this);
        return true;
    }

    public boolean volunteerForVacancy(ServerPlayer player) {
        String team = Espetro.getPlayerTeam(player);
        if (team == null) {
            return this.fail(player, "\u4f60\u5c1a\u672a\u52a0\u5165\u9635\u8425");
        }
        TeamGovernance g = this.getTeam(team);
        if (g.state != State.VACANCY_VOLUNTEER) {
            return this.fail(player, "\u5f53\u524d\u4e0d\u5728\u6307\u6325\u5b98\u7a7a\u7f3a\u5fd7\u613f\u9636\u6bb5");
        }
        if (!SquadManager.getInstance().isSquadLeader(player.m_20148_())) {
            return this.fail(player, "\u53ea\u6709\u5c0f\u961f\u957f\u53ef\u4ee5\u5fd7\u613f\u8865\u4f4d");
        }
        g.volunteers.add(player.m_20148_());
        player.m_5661_(Component.m_237113_("\u00a7a\u5df2\u767b\u8bb0\u5fd7\u613f\u8865\u4f4d"), true);
        NetworkManager.broadcastGovernanceState(this);
        return true;
    }

    public boolean castVacancyVote(ServerPlayer voter, UUID candidate) {
        String team = Espetro.getPlayerTeam(voter);
        if (team == null) {
            return this.fail(voter, "\u4f60\u5c1a\u672a\u52a0\u5165\u9635\u8425");
        }
        TeamGovernance g = this.getTeam(team);
        if (g.state != State.VACANCY_VOTE) {
            return this.fail(voter, "\u5f53\u524d\u6ca1\u6709\u8fdb\u884c\u4e2d\u7684\u7a7a\u7f3a\u516c\u6295");
        }
        if (candidate == null || !g.volunteers.contains(candidate)) {
            return this.fail(voter, "\u65e0\u6548\u7684\u5019\u9009\u4eba");
        }
        g.votes.put(voter.m_20148_(), candidate);
        voter.m_5661_(Component.m_237113_("\u00a7a\u5df2\u6295\u7968\u7ed9 " + this.playerName(candidate)), true);
        NetworkManager.broadcastGovernanceState(this);
        return true;
    }

    public void onPlayerLeft(@Nullable String team, UUID uuid) {
        if (uuid == null) {
            return;
        }
        if (team != null) {
            this.handlePlayerLeftTeam(team, uuid);
            return;
        }
        for (String t : List.of("ATTACK", "DEFEND")) {
            this.handlePlayerLeftTeam(t, uuid);
        }
    }

    @Deprecated
    public void onCommanderDisconnected(String team, UUID commanderUuid) {
        this.onPlayerLeft(team, commanderUuid);
    }

    private void handlePlayerLeftTeam(String team, UUID uuid) {
        boolean isCommander;
        TeamGovernance g = this.getTeam(team);
        boolean bl = isCommander = uuid.equals(g.commander) || VoteManager.getInstance().isCommanderOf(uuid, team);
        if (g.state == State.IMPEACHMENT_VOTE && uuid.equals(g.challenger) && !isCommander) {
            this.cancelImpeachment(team, g, "\u00a7c\u5f39\u52be\u53d1\u8d77\u8005\u79bb\u7ebf\uff0c\u5f39\u52be\u5931\u8d25\uff0c\u6307\u6325\u5b98\u7559\u4efb");
            return;
        }
        if (isCommander) {
            this.recentCommanderDisconnect.put(uuid, System.currentTimeMillis());
            this.recentCommanderTeam.put(uuid, team);
            if (g.state == State.IMPEACHMENT_VOTE) {
                g.challenger = null;
                g.votes.clear();
            }
            this.clearCommander(team, "disconnect");
            this.startVacancy(team);
            return;
        }
        boolean changed = g.volunteers.remove(uuid);
        int beforeVotes = g.votes.size();
        g.votes.entrySet().removeIf(e -> ((UUID)e.getKey()).equals(uuid) || ((UUID)e.getValue()).equals(uuid));
        boolean bl2 = changed = changed || g.votes.size() != beforeVotes;
        if (g.state == State.VACANCY_VOTE) {
            this.collapseVacancyVoteIfNeeded(team, g);
            return;
        }
        if (changed) {
            NetworkManager.broadcastGovernanceState(this);
        }
    }

    public void onSquadLeaderLost(UUID uuid) {
        for (Map.Entry<String, TeamGovernance> e : this.byTeam.entrySet()) {
            String team = e.getKey();
            TeamGovernance g = e.getValue();
            if (g.state == State.IMPEACHMENT_VOTE && uuid.equals(g.challenger) && !uuid.equals(g.commander)) {
                this.cancelImpeachment(team, g, "\u00a7c\u5f39\u52be\u53d1\u8d77\u8005\u5931\u53bb\u961f\u957f\u8d44\u683c\uff0c\u5f39\u52be\u5931\u8d25");
                continue;
            }
            boolean removed = g.volunteers.remove(uuid);
            g.votes.entrySet().removeIf(v -> ((UUID)v.getKey()).equals(uuid) || ((UUID)v.getValue()).equals(uuid));
            if (g.state == State.VACANCY_VOTE) {
                this.collapseVacancyVoteIfNeeded(team, g);
                continue;
            }
            if (!removed) continue;
            NetworkManager.broadcastGovernanceState(this);
        }
    }

    public boolean tryRestoreCommanderOnRejoin(ServerPlayer player) {
        UUID uuid = player.m_20148_();
        Long disconnectTime = this.recentCommanderDisconnect.get(uuid);
        String recordedTeam = this.recentCommanderTeam.get(uuid);
        if (disconnectTime == null || recordedTeam == null) {
            return false;
        }
        if (System.currentTimeMillis() - disconnectTime > 120000L) {
            this.recentCommanderDisconnect.remove(uuid);
            this.recentCommanderTeam.remove(uuid);
            return false;
        }
        VoteManager vm = VoteManager.getInstance();
        String currentTeam = null;
        if (vm.getAttackPlayers().contains(uuid)) {
            currentTeam = "ATTACK";
        } else if (vm.getDefendPlayers().contains(uuid)) {
            currentTeam = "DEFEND";
        }
        if (!recordedTeam.equals(currentTeam)) {
            this.recentCommanderDisconnect.remove(uuid);
            this.recentCommanderTeam.remove(uuid);
            return false;
        }
        TeamGovernance g = this.getTeam(recordedTeam);
        if (g.state == State.VACANCY_VOLUNTEER || g.state == State.VACANCY_VOTE) {
            g.state = State.IDLE;
            g.volunteers.clear();
            g.votes.clear();
            g.challenger = null;
            g.tickCounter = 0;
            g.endGameTime = 0L;
        }
        this.assignCommander(recordedTeam, uuid, "reconnect");
        this.recentCommanderDisconnect.remove(uuid);
        this.recentCommanderTeam.remove(uuid);
        Espetro.broadcastToTeam(recordedTeam, "\u00a7a\u6307\u6325\u5b98 " + player.m_7755_().getString() + " \u5df2\u91cd\u65b0\u4e0a\u7ebf\uff0c\u6062\u590d\u6307\u6325\u6743");
        return true;
    }

    private void cancelImpeachment(String team, TeamGovernance g, String message) {
        g.state = State.IDLE;
        g.challenger = null;
        g.votes.clear();
        g.tickCounter = 0;
        g.endGameTime = 0L;
        Espetro.broadcastToTeam(team, message);
        NetworkManager.broadcastGovernanceState(this);
    }

    private void collapseVacancyVoteIfNeeded(String team, TeamGovernance g) {
        if (g.state != State.VACANCY_VOTE) {
            return;
        }
        ArrayList<UUID> remaining = new ArrayList<UUID>(g.volunteers);
        remaining.removeIf(u -> !this.isOnline((UUID)u));
        g.volunteers.clear();
        g.volunteers.addAll(remaining);
        if (remaining.isEmpty()) {
            UUID fallback = this.findSuccessor(team);
            if (fallback != null) {
                this.assignCommander(team, fallback, "vacancy_vote_all_left");
            } else {
                Espetro.broadcastToTeam(team, "\u00a7c\u7a7a\u7f3a\u516c\u6295\u5019\u9009\u4eba\u5168\u90e8\u79bb\u7ebf\uff0c\u6682\u65e0\u6307\u6325\u5b98");
            }
            g.state = State.IDLE;
            g.votes.clear();
            g.volunteers.clear();
            NetworkManager.broadcastGovernanceState(this);
            return;
        }
        if (remaining.size() == 1) {
            this.assignCommander(team, (UUID)remaining.get(0), "vacancy_vote_last_remaining");
            g.state = State.IDLE;
            g.votes.clear();
            g.volunteers.clear();
            NetworkManager.broadcastGovernanceState(this);
            return;
        }
        NetworkManager.broadcastGovernanceState(this);
    }

    private void startVacancy(String team) {
        TeamGovernance g = this.getTeam(team);
        g.state = State.VACANCY_VOLUNTEER;
        g.volunteers.clear();
        g.votes.clear();
        g.challenger = null;
        g.tickCounter = 0;
        g.timeoutSeconds = GameConfig.getCommanderVacancySeconds();
        MinecraftServer server = Espetro.getServer();
        g.endGameTime = server != null ? server.m_129783_().m_46467_() + (long)g.timeoutSeconds * 20L : 0L;
        Espetro.broadcastToTeam(team, "\u00a7e\u6307\u6325\u5b98\u7a7a\u7f3a\uff01\u5c0f\u961f\u957f\u53ef\u5fd7\u613f\u8865\u4f4d\uff08" + g.timeoutSeconds + "\u79d2\uff09");
        NetworkManager.broadcastGovernanceState(this);
    }

    public void onServerTick(MinecraftServer server) {
        long now = server.m_129783_().m_46467_();
        for (Map.Entry<String, TeamGovernance> e : this.byTeam.entrySet()) {
            String team = e.getKey();
            TeamGovernance g = e.getValue();
            if (g.state == State.IDLE) continue;
            ++g.tickCounter;
            boolean bl = g.endGameTime > 0L ? now >= g.endGameTime : (long)g.tickCounter >= (long)g.timeoutSeconds * 20L;
            boolean timedOut = bl;
            if (!timedOut) continue;
            switch (g.state) {
                case IMPEACHMENT_VOTE: {
                    this.finishImpeachment(team, g);
                    break;
                }
                case VACANCY_VOLUNTEER: {
                    this.finishVacancyVolunteer(team, g);
                    break;
                }
                case VACANCY_VOTE: {
                    this.finishVacancyVote(team, g);
                    break;
                }
            }
        }
    }

    private void finishImpeachment(String team, TeamGovernance g) {
        UUID winner = CommanderGovernanceManager.resolveImpeachmentWinner(g.commander, g.challenger, g.votes);
        if (winner != null && !winner.equals(g.commander)) {
            this.assignCommander(team, winner, "impeachment");
            Espetro.broadcastToTeam(team, "\u00a7a\u5f39\u52be\u6210\u529f\uff0c\u65b0\u6307\u6325\u5b98\u5df2\u5c31\u4efb");
        } else {
            Espetro.broadcastToTeam(team, "\u00a7e\u5f39\u52be\u5931\u8d25\uff0c\u6307\u6325\u5b98\u7559\u4efb");
        }
        g.state = State.IDLE;
        g.challenger = null;
        g.votes.clear();
        g.endGameTime = 0L;
        NetworkManager.broadcastGovernanceState(this);
    }

    static UUID resolveImpeachmentWinner(@Nullable UUID commander, @Nullable UUID challenger, Map<UUID, UUID> votes) {
        int chVotes;
        HashMap<UUID, Integer> tally = new HashMap<UUID, Integer>();
        if (commander != null) {
            tally.put(commander, 0);
        }
        if (challenger != null) {
            tally.put(challenger, 0);
        }
        if (votes != null) {
            for (UUID c : votes.values()) {
                tally.computeIfPresent(c, (k, v) -> v + 1);
            }
        }
        int cmdVotes = commander != null ? tally.getOrDefault(commander, 0) : 0;
        int n = chVotes = challenger != null ? tally.getOrDefault(challenger, 0) : 0;
        if (chVotes > cmdVotes) {
            return challenger;
        }
        return commander;
    }

    static UUID resolveVacancyVoteWinner(Set<UUID> volunteers, Map<UUID, UUID> votes, ToLongFunction<UUID> leaderSince) {
        HashMap<UUID, Integer> tally = new HashMap<UUID, Integer>();
        if (volunteers != null) {
            for (UUID v2 : volunteers) {
                tally.put(v2, 0);
            }
        }
        if (votes != null) {
            for (UUID c : votes.values()) {
                tally.computeIfPresent(c, (k, v) -> v + 1);
            }
        }
        int best = -1;
        ArrayList<UUID> tied = new ArrayList<UUID>();
        for (Map.Entry e : tally.entrySet()) {
            if ((Integer)e.getValue() > best) {
                best = (Integer)e.getValue();
                tied.clear();
                tied.add((UUID)e.getKey());
                continue;
            }
            if ((Integer)e.getValue() != best) continue;
            tied.add((UUID)e.getKey());
        }
        if (tied.isEmpty()) {
            return null;
        }
        if (tied.size() == 1) {
            return (UUID)tied.get(0);
        }
        return tied.stream().min((a, b) -> {
            long lb;
            long la = leaderSince != null ? leaderSince.applyAsLong((UUID)a) : 0L;
            int cmp = Long.compare(la, lb = leaderSince != null ? leaderSince.applyAsLong((UUID)b) : 0L);
            if (cmp != 0) {
                return cmp;
            }
            return a.compareTo((UUID)b);
        }).orElse(null);
    }

    private void finishVacancyVolunteer(String team, TeamGovernance g) {
        ArrayList<UUID> vols = new ArrayList<UUID>(g.volunteers);
        vols.removeIf(u -> !SquadManager.getInstance().isSquadLeader((UUID)u) || !this.isOnline((UUID)u));
        if (vols.isEmpty()) {
            UUID successor = this.findSuccessor(team);
            if (successor != null) {
                this.assignCommander(team, successor, "vacancy_auto_fallback");
                Espetro.broadcastToTeam(team, "\u00a7e\u65e0\u4eba\u5fd7\u613f\uff0c\u7cfb\u7edf\u81ea\u52a8\u4efb\u547d\u7ee7\u4efb\u6307\u6325\u5b98");
            } else {
                Espetro.broadcastToTeam(team, "\u00a7c\u65e0\u4eba\u53ef\u7ee7\u4efb\u6307\u6325\u5b98\uff0c\u804c\u4f4d\u6682\u65f6\u7a7a\u7f3a");
            }
            g.state = State.IDLE;
            g.volunteers.clear();
            g.endGameTime = 0L;
            NetworkManager.broadcastGovernanceState(this);
            return;
        }
        if (vols.size() == 1) {
            this.assignCommander(team, (UUID)vols.get(0), "vacancy_single_volunteer");
            g.state = State.IDLE;
            g.volunteers.clear();
            g.endGameTime = 0L;
            NetworkManager.broadcastGovernanceState(this);
            return;
        }
        g.volunteers.clear();
        g.volunteers.addAll(vols);
        g.state = State.VACANCY_VOTE;
        g.votes.clear();
        g.tickCounter = 0;
        g.timeoutSeconds = GameConfig.getImpeachmentVoteSeconds();
        MinecraftServer server = Espetro.getServer();
        g.endGameTime = server != null ? server.m_129783_().m_46467_() + (long)g.timeoutSeconds * 20L : 0L;
        Espetro.broadcastToTeam(team, "\u00a7e\u591a\u540d\u5c0f\u961f\u957f\u5fd7\u613f\u8865\u4f4d\uff0c\u5f00\u59cb\u5168\u5458\u516c\u6295\uff08" + g.timeoutSeconds + "\u79d2\uff09");
        NetworkManager.broadcastGovernanceState(this);
    }

    private void finishVacancyVote(String team, TeamGovernance g) {
        UUID winner = CommanderGovernanceManager.resolveVacancyVoteWinner(g.volunteers, g.votes, u -> SquadManager.getInstance().getLeaderSinceTick((UUID)u));
        if (winner == null) {
            winner = this.findSuccessor(team);
        }
        if (winner != null) {
            this.assignCommander(team, winner, "vacancy_vote");
        } else {
            Espetro.broadcastToTeam(team, "\u00a7c\u7a7a\u7f3a\u516c\u6295\u672a\u80fd\u4ea7\u751f\u6307\u6325\u5b98");
        }
        g.state = State.IDLE;
        g.volunteers.clear();
        g.votes.clear();
        g.endGameTime = 0L;
        NetworkManager.broadcastGovernanceState(this);
    }

    public void assignCommander(String team, UUID uuid, String reason) {
        ServerPlayer p;
        VoteManager vm = VoteManager.getInstance();
        if ("ATTACK".equals(team)) {
            vm.setAttackCommander(uuid);
        } else {
            vm.setDefendCommander(uuid);
        }
        this.getTeam((String)team).commander = uuid;
        MinecraftServer server = Espetro.getServer();
        String name = uuid.toString();
        if (server != null && (p = server.m_6846_().m_11259_(uuid)) != null) {
            name = p.m_7755_().getString();
            NetworkManager.sendCommanderSkillSync(p);
        }
        NetworkManager.syncSquadsToTeam(team);
        NetworkManager.broadcastGovernanceState(this);
        Espetro.LOGGER.info("\u4efb\u547d\u6307\u6325\u5b98 {} \u4e3a {} ({})", new Object[]{name, team, reason});
        Espetro.broadcastToTeam(team, "\u00a7a\u65b0\u6307\u6325\u5b98\u5df2\u5c31\u4efb\uff1a" + name);
    }

    private void clearCommander(String team, String reason) {
        VoteManager vm = VoteManager.getInstance();
        if ("ATTACK".equals(team)) {
            vm.setAttackCommander(null);
        } else {
            vm.setDefendCommander(null);
        }
        this.getTeam((String)team).commander = null;
        Espetro.LOGGER.info("\u6e05\u9664 {} \u6307\u6325\u5b98 ({})", (Object)team, (Object)reason);
    }

    @Nullable
    private UUID findSuccessor(String team) {
        UUID leader = this.findEarliestSquadLeader(team);
        if (leader != null) {
            return leader;
        }
        return this.findAnyOnlineTeammate(team);
    }

    @Nullable
    private UUID findEarliestSquadLeader(String team) {
        long best = Long.MAX_VALUE;
        UUID bestId = null;
        for (SquadManager.SquadSnapshot snap : SquadManager.getInstance().getSquadSnapshots(team)) {
            for (SquadManager.MemberSnapshot m : snap.members) {
                long since;
                if (!m.leader || !this.isOnline(m.uuid) || (since = SquadManager.getInstance().getLeaderSinceTick(m.uuid)) >= best && (since != best || bestId != null && m.uuid.compareTo(bestId) >= 0)) continue;
                best = since;
                bestId = m.uuid;
            }
        }
        return bestId;
    }

    @Nullable
    private UUID findAnyOnlineTeammate(String team) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return null;
        }
        UUID best = null;
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            if (!team.equals(Espetro.getPlayerTeam(player)) || best != null && player.m_20148_().compareTo(best) >= 0) continue;
            best = player.m_20148_();
        }
        return best;
    }

    private boolean isOnline(UUID uuid) {
        MinecraftServer server = Espetro.getServer();
        return server != null && server.m_6846_().m_11259_(uuid) != null;
    }

    private String playerName(UUID uuid) {
        ServerPlayer p;
        MinecraftServer server = Espetro.getServer();
        if (server != null && (p = server.m_6846_().m_11259_(uuid)) != null) {
            return p.m_7755_().getString();
        }
        return uuid != null ? uuid.toString().substring(0, 8) : "?";
    }

    private boolean fail(ServerPlayer player, String msg) {
        player.m_213846_(Component.m_237113_("\u00a7c" + msg));
        return false;
    }

    public static final class TeamGovernance {
        public State state = State.IDLE;
        public UUID commander;
        public UUID challenger;
        public final Map<UUID, UUID> votes = new HashMap<UUID, UUID>();
        public final Set<UUID> volunteers = new HashSet<UUID>();
        public int tickCounter;
        public int timeoutSeconds;
        public long endGameTime;
    }

    public static enum State {
        IDLE,
        IMPEACHMENT_VOTE,
        VACANCY_VOLUNTEER,
        VACANCY_VOTE;

    }
}

