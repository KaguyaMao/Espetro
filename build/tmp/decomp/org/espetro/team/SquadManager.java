/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.Espetro;
import org.espetro.governance.CommanderGovernanceManager;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.mapconfig.SquadTypesSnapshot;
import org.espetro.team.ClassCountManager;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;
import org.espetro.team.Fireteam;

public class SquadManager {
    public static final int NO_SQUAD = -1;
    private static final int MAX_MEMBERS = 9;
    private static final int MAX_NAME_LENGTH = 18;
    private static final Pattern FORMAT_CODE = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
    private static SquadManager INSTANCE;
    private final Map<String, LinkedHashMap<Integer, Squad>> squadsByTeam = new HashMap<String, LinkedHashMap<Integer, Squad>>();
    private final Map<UUID, Integer> playerSquads = new HashMap<UUID, Integer>();
    private final Map<String, Integer> nextDisplayIdByTeam = new HashMap<String, Integer>();
    private int nextSquadId = 1;

    private SquadManager() {
        INSTANCE = this;
    }

    public static SquadManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SquadManager();
        }
        return INSTANCE;
    }

    public static void init() {
        INSTANCE = new SquadManager();
    }

    public ActionResult createSquad(ServerPlayer player, String requestedName) {
        return this.createSquad(player, requestedName, "none");
    }

    public ActionResult createSquad(ServerPlayer player, String requestedName, String categoryId) {
        String team = Espetro.getPlayerTeam(player);
        if (team == null) {
            return ActionResult.failure(null, "\u4f60\u5c1a\u672a\u52a0\u5165\u9635\u8425\uff0c\u65e0\u6cd5\u521b\u5efa\u5c0f\u961f\u3002");
        }
        if (this.getPlayerSquadId(player.m_20148_()) != -1) {
            return ActionResult.failure(team, "\u4f60\u5df2\u7ecf\u5728\u5c0f\u961f\u4e2d\uff0c\u4e0d\u80fd\u91cd\u590d\u521b\u5efa\u5c0f\u961f\u3002");
        }
        Object name = this.sanitizeName(requestedName);
        String catId = categoryId == null || categoryId.isBlank() ? "none" : categoryId;
        String catDisplay = "\u65e0";
        ActiveMapConfig active = BattlefieldContext.getOrNull();
        if (active != null && active.squadTypes != null) {
            SquadTypesSnapshot.Category cat = active.squadTypes.find(catId);
            if (cat != null) {
                catId = cat.id();
                catDisplay = cat.displayName();
            } else if (!"none".equals(catId)) {
                return ActionResult.failure(team, "\u65e0\u6548\u7684\u5c0f\u961f\u7c7b\u522b\u3002");
            }
        } else {
            SquadTypesSnapshot.Category defaults = SquadTypesSnapshot.defaults().find(catId);
            if (defaults != null) {
                catId = defaults.id();
                catDisplay = defaults.displayName();
            }
        }
        int displayId = this.claimNextDisplayId(team);
        if (((String)name).isEmpty()) {
            name = "\u5c0f\u961f" + displayId;
        }
        Squad squad = new Squad(this.nextSquadId++, displayId, team, (String)name, player.m_20148_());
        squad.categoryId = catId;
        squad.categoryDisplayName = catDisplay;
        SquadManager.addMemberToFireteam(squad, player.m_20148_(), Fireteam.A, true);
        this.squadsByTeam.computeIfAbsent(team, ignored -> new LinkedHashMap()).put(squad.id, squad);
        this.playerSquads.put(player.m_20148_(), squad.id);
        return ActionResult.success(team, "\u5df2\u521b\u5efa\u5c0f\u961f " + (String)name + "\uff0c\u4f60\u662f\u961f\u957f\uff08\u706b\u529b\u7ec4 A \u7ec4\u957f\uff09\u3002");
    }

    public ActionResult forceJoinSquad(ServerPlayer leader, UUID targetUuid) {
        if (!this.isSquadLeader(leader.m_20148_())) {
            return ActionResult.failure(Espetro.getPlayerTeam(leader), "\u53ea\u6709\u961f\u957f\u53ef\u4ee5\u62c9\u4eba\u5165\u961f\u3002");
        }
        String team = Espetro.getPlayerTeam(leader);
        if (team == null) {
            return ActionResult.failure(null, "\u4f60\u5c1a\u672a\u52a0\u5165\u9635\u8425\u3002");
        }
        int squadId = this.getPlayerSquadId(leader.m_20148_());
        Squad squad = this.getSquad(team, squadId);
        if (squad == null) {
            return ActionResult.failure(team, "\u5c0f\u961f\u4e0d\u5b58\u5728\u3002");
        }
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return ActionResult.failure(team, "\u670d\u52a1\u5668\u4e0d\u53ef\u7528\u3002");
        }
        ServerPlayer target = server.m_6846_().m_11259_(targetUuid);
        if (target == null) {
            return ActionResult.failure(team, "\u76ee\u6807\u73a9\u5bb6\u4e0d\u5728\u7ebf\u3002");
        }
        if (!team.equals(Espetro.getPlayerTeam(target))) {
            return ActionResult.failure(team, "\u53ea\u80fd\u62c9\u540c\u9635\u8425\u73a9\u5bb6\u3002");
        }
        if (this.getPlayerSquadId(targetUuid) != -1) {
            return ActionResult.failure(team, "\u76ee\u6807\u5df2\u5728\u5c0f\u961f\u4e2d\u3002");
        }
        if (squad.members.size() >= 9) {
            return ActionResult.failure(team, "\u5c0f\u961f\u4eba\u6570\u5df2\u6ee1\u3002");
        }
        if (squad.locked) {
            return ActionResult.failure(team, "\u8be5\u5c0f\u961f\u5df2\u9501\u5b9a\uff0c\u65e0\u6cd5\u52a0\u5165\u3002");
        }
        SquadManager.addMemberToFireteam(squad, targetUuid, Fireteam.A, false);
        this.playerSquads.put(targetUuid, squad.id);
        return ActionResult.success(team, "\u5df2\u5c06 " + target.m_7755_().getString() + " \u62c9\u8fdb\u5c0f\u961f\uff08\u706b\u529b\u7ec4 A\uff09\u3002");
    }

    public ActionResult kickMember(ServerPlayer leader, UUID targetUuid) {
        if (!this.isSquadLeader(leader.m_20148_())) {
            return ActionResult.failure(Espetro.getPlayerTeam(leader), "\u53ea\u6709\u961f\u957f\u53ef\u4ee5\u8e22\u4eba\u3002");
        }
        if (leader.m_20148_().equals(targetUuid)) {
            return ActionResult.failure(Espetro.getPlayerTeam(leader), "\u4e0d\u80fd\u8e22\u51fa\u81ea\u5df1\u3002");
        }
        String team = Espetro.getPlayerTeam(leader);
        int squadId = this.getPlayerSquadId(leader.m_20148_());
        if (squadId == -1 || this.getPlayerSquadId(targetUuid) != squadId) {
            return ActionResult.failure(team, "\u76ee\u6807\u4e0d\u5728\u4f60\u7684\u5c0f\u961f\u4e2d\u3002");
        }
        String affected = this.removePlayerFromCurrentSquad(targetUuid);
        MinecraftServer server = Espetro.getServer();
        if (server != null) {
            ServerPlayer target = server.m_6846_().m_11259_(targetUuid);
            if (target != null) {
                ClassCountManager.getInstance().onPlayerLeftSquad(target);
            } else {
                ClassCountManager.getInstance().onPlayerLeftSquadOffline(targetUuid);
            }
        } else {
            ClassCountManager.getInstance().onPlayerLeftSquadOffline(targetUuid);
        }
        return ActionResult.success(affected != null ? affected : team, "\u5df2\u8e22\u51fa\u961f\u5458\u3002");
    }

    public long getLeaderSinceTick(UUID uuid) {
        Integer squadId = this.playerSquads.get(uuid);
        if (squadId == null) {
            return Long.MAX_VALUE;
        }
        for (LinkedHashMap<Integer, Squad> squads : this.squadsByTeam.values()) {
            Squad squad = squads.get(squadId);
            if (squad == null || !uuid.equals(squad.leader)) continue;
            return squad.leaderSinceTick;
        }
        return Long.MAX_VALUE;
    }

    public String getPlayerCategoryId(UUID uuid) {
        Integer squadId = this.playerSquads.get(uuid);
        if (squadId == null) {
            return null;
        }
        for (LinkedHashMap<Integer, Squad> squads : this.squadsByTeam.values()) {
            Squad squad = squads.get(squadId);
            if (squad == null) continue;
            return squad.categoryId;
        }
        return null;
    }

    public ActionResult joinSquad(ServerPlayer player, int squadId) {
        String team = Espetro.getPlayerTeam(player);
        if (team == null) {
            return ActionResult.failure(null, "\u4f60\u5c1a\u672a\u52a0\u5165\u9635\u8425\uff0c\u65e0\u6cd5\u52a0\u5165\u5c0f\u961f\u3002");
        }
        Squad squad = this.getSquad(team, squadId);
        if (squad == null) {
            return ActionResult.failure(team, "\u76ee\u6807\u5c0f\u961f\u4e0d\u5b58\u5728\u3002");
        }
        if (squad.members.contains(player.m_20148_())) {
            return ActionResult.success(team, "\u4f60\u5df2\u7ecf\u5728 " + squad.name + " \u4e2d\u3002");
        }
        if (squad.members.size() >= 9) {
            return ActionResult.failure(team, "\u76ee\u6807\u5c0f\u961f\u4eba\u6570\u5df2\u6ee1\u3002");
        }
        if (squad.locked) {
            return ActionResult.failure(team, "\u8be5\u5c0f\u961f\u5df2\u9501\u5b9a\uff0c\u65e0\u6cd5\u52a0\u5165\u3002");
        }
        int previousSquadId = this.getPlayerSquadId(player.m_20148_());
        this.removePlayerFromCurrentSquad(player.m_20148_());
        if (previousSquadId != -1) {
            ClassCountManager.getInstance().onPlayerLeftSquad(player);
        }
        SquadManager.addMemberToFireteam(squad, player.m_20148_(), Fireteam.A, false);
        this.playerSquads.put(player.m_20148_(), squad.id);
        return ActionResult.success(team, "\u5df2\u52a0\u5165\u5c0f\u961f " + squad.name + "\uff08\u706b\u529b\u7ec4 A\uff09\u3002");
    }

    public ActionResult leaveSquad(ServerPlayer player) {
        String team = Espetro.getPlayerTeam(player);
        String affectedTeam = this.removePlayerFromCurrentSquad(player.m_20148_());
        if (affectedTeam != null) {
            ClassCountManager.getInstance().onPlayerLeftSquad(player);
            return ActionResult.success(affectedTeam, "\u5df2\u9000\u51fa\u5c0f\u961f\u3002");
        }
        return ActionResult.failure(team, "\u4f60\u5f53\u524d\u4e0d\u5728\u5c0f\u961f\u4e2d\u3002");
    }

    public ActionResult deleteSquad(ServerPlayer player, int squadId) {
        Squad squad;
        String team = Espetro.getPlayerTeam(player);
        if (team == null) {
            return ActionResult.failure(null, "\u4f60\u5c1a\u672a\u52a0\u5165\u9635\u8425\uff0c\u65e0\u6cd5\u5220\u9664\u5c0f\u961f\u3002");
        }
        LinkedHashMap<Integer, Squad> squads = this.squadsByTeam.get(team);
        Squad squad2 = squad = squads != null ? squads.get(squadId) : null;
        if (squad == null) {
            return ActionResult.failure(team, "\u76ee\u6807\u5c0f\u961f\u4e0d\u5b58\u5728\u3002");
        }
        if (!player.m_20148_().equals(squad.leader)) {
            return ActionResult.failure(team, "\u53ea\u6709\u961f\u957f\u53ef\u4ee5\u5220\u9664\u5c0f\u961f\u3002");
        }
        ArrayList<UUID> formerMembers = new ArrayList<UUID>(squad.members);
        CommanderGovernanceManager.getInstance().onSquadLeaderLost(squad.leader);
        for (UUID memberUuid : formerMembers) {
            this.playerSquads.remove(memberUuid);
        }
        squads.remove(squad.id);
        MinecraftServer server = Espetro.getServer();
        ClassCountManager countManager = ClassCountManager.getInstance();
        for (UUID memberUuid : formerMembers) {
            ServerPlayer member;
            if (server != null && (member = server.m_6846_().m_11259_(memberUuid)) != null) {
                countManager.onPlayerLeftSquad(member);
                continue;
            }
            countManager.onPlayerLeftSquadOffline(memberUuid);
        }
        return ActionResult.success(team, "\u5df2\u5220\u9664\u5c0f\u961f " + squad.name + "\u3002");
    }

    public String removePlayer(UUID uuid) {
        String affectedTeam = this.removePlayerFromCurrentSquad(uuid);
        this.playerSquads.remove(uuid);
        MinecraftServer server = Espetro.getServer();
        if (server != null) {
            ServerPlayer player = server.m_6846_().m_11259_(uuid);
            if (player != null) {
                ClassCountManager.getInstance().onPlayerLeftSquad(player);
            } else {
                ClassCountManager.getInstance().onPlayerLeftSquadOffline(uuid);
            }
        } else {
            ClassCountManager.getInstance().onPlayerLeftSquadOffline(uuid);
        }
        return affectedTeam;
    }

    public void reset() {
        this.squadsByTeam.clear();
        this.playerSquads.clear();
        this.nextDisplayIdByTeam.clear();
        this.nextSquadId = 1;
    }

    public int getPlayerSquadId(UUID uuid) {
        return this.playerSquads.getOrDefault(uuid, -1);
    }

    public List<UUID> getSquadMemberUuids(String team, int squadId) {
        Squad squad = this.getSquad(team, squadId);
        if (squad == null) {
            return List.of();
        }
        return new ArrayList<UUID>(squad.members);
    }

    public boolean isSquadLeader(UUID uuid) {
        Integer squadId = this.playerSquads.get(uuid);
        if (squadId == null) {
            return false;
        }
        for (LinkedHashMap<Integer, Squad> squads : this.squadsByTeam.values()) {
            Squad squad = squads.get(squadId);
            if (squad == null) continue;
            return uuid.equals(squad.leader);
        }
        return false;
    }

    public boolean isFireteamLeader(UUID uuid) {
        Squad squad = this.getSquadOf(uuid);
        if (squad == null) {
            return false;
        }
        Fireteam ft = squad.memberFireteam.get(uuid);
        return ft != null && uuid.equals(squad.fireteamLeaders.get((Object)ft));
    }

    public ActionResult lockSquad(ServerPlayer player) {
        String team = Espetro.getPlayerTeam(player);
        if (team == null) {
            return ActionResult.failure(null, "\u4f60\u5c1a\u672a\u52a0\u5165\u9635\u8425\u3002");
        }
        UUID uuid = player.m_20148_();
        if (!this.isSquadLeader(uuid)) {
            return ActionResult.failure(team, "\u53ea\u6709\u5c0f\u961f\u957f\u53ef\u4ee5\u9501\u5b9a\u5c0f\u961f\u3002");
        }
        Squad squad = this.getSquadOf(uuid);
        if (squad == null) {
            return ActionResult.failure(team, "\u4f60\u4e0d\u5728\u5c0f\u961f\u4e2d\u3002");
        }
        if (squad.locked) {
            return ActionResult.failure(team, "\u5c0f\u961f\u5df2\u7ecf\u5904\u4e8e\u9501\u5b9a\u72b6\u6001\u3002");
        }
        squad.locked = true;
        return ActionResult.success(team, "\u5c0f\u961f\u5df2\u9501\u5b9a\uff0c\u5176\u4ed6\u4eba\u65e0\u6cd5\u52a0\u5165\u3002");
    }

    public ActionResult unlockSquad(ServerPlayer player) {
        String team = Espetro.getPlayerTeam(player);
        if (team == null) {
            return ActionResult.failure(null, "\u4f60\u5c1a\u672a\u52a0\u5165\u9635\u8425\u3002");
        }
        UUID uuid = player.m_20148_();
        if (!this.isSquadLeader(uuid)) {
            return ActionResult.failure(team, "\u53ea\u6709\u5c0f\u961f\u957f\u53ef\u4ee5\u89e3\u9501\u5c0f\u961f\u3002");
        }
        Squad squad = this.getSquadOf(uuid);
        if (squad == null) {
            return ActionResult.failure(team, "\u4f60\u4e0d\u5728\u5c0f\u961f\u4e2d\u3002");
        }
        if (!squad.locked) {
            return ActionResult.failure(team, "\u5c0f\u961f\u5f53\u524d\u672a\u9501\u5b9a\u3002");
        }
        squad.locked = false;
        return ActionResult.success(team, "\u5c0f\u961f\u5df2\u89e3\u9501\uff0c\u5176\u4ed6\u4eba\u53ef\u4ee5\u52a0\u5165\u3002");
    }

    public boolean isSquadLocked(String team, int squadId) {
        Squad squad = this.getSquad(team, squadId);
        return squad != null && squad.locked;
    }

    public Fireteam getPlayerFireteam(UUID uuid) {
        Squad squad = this.getSquadOf(uuid);
        if (squad == null) {
            return null;
        }
        return squad.memberFireteam.get(uuid);
    }

    public ActionResult transferSquadLeader(ServerPlayer actor, UUID targetUuid) {
        String team = Espetro.getPlayerTeam(actor);
        if (team == null) {
            return ActionResult.failure(null, "\u4f60\u5c1a\u672a\u52a0\u5165\u9635\u8425\u3002");
        }
        if (!this.isSquadLeader(actor.m_20148_())) {
            return ActionResult.failure(team, "\u53ea\u6709\u5c0f\u961f\u957f\u53ef\u4ee5\u8f6c\u79fb\u961f\u957f\u3002");
        }
        if (actor.m_20148_().equals(targetUuid)) {
            return ActionResult.failure(team, "\u4e0d\u80fd\u8f6c\u79fb\u7ed9\u81ea\u5df1\u3002");
        }
        Squad squad = this.getSquad(team, this.getPlayerSquadId(actor.m_20148_()));
        if (squad == null || !squad.members.contains(targetUuid)) {
            return ActionResult.failure(team, "\u76ee\u6807\u4e0d\u5728\u4f60\u7684\u5c0f\u961f\u4e2d\u3002");
        }
        UUID oldLeader = squad.leader;
        SquadManager.moveMemberToFireteam(squad, targetUuid, Fireteam.A);
        squad.leader = targetUuid;
        squad.leaderSinceTick = SquadManager.currentGameTime();
        squad.fireteamLeaders.put(Fireteam.A, targetUuid);
        if (oldLeader != null && !oldLeader.equals(targetUuid)) {
            CommanderGovernanceManager.getInstance().onSquadLeaderLost(oldLeader);
        }
        return ActionResult.success(team, "\u5df2\u5c06\u961f\u957f\u8f6c\u79fb\u7ed9 " + this.getPlayerName(Espetro.getServer(), targetUuid) + "\u3002");
    }

    public ActionResult transferFireteamLeader(ServerPlayer actor, UUID targetUuid) {
        String team = Espetro.getPlayerTeam(actor);
        if (team == null) {
            return ActionResult.failure(null, "\u4f60\u5c1a\u672a\u52a0\u5165\u9635\u8425\u3002");
        }
        Squad squad = this.getSquadOf(actor.m_20148_());
        if (squad == null) {
            return ActionResult.failure(team, "\u4f60\u4e0d\u5728\u5c0f\u961f\u4e2d\u3002");
        }
        Fireteam actorFt = squad.memberFireteam.get(actor.m_20148_());
        if (actorFt == null || !actor.m_20148_().equals(squad.fireteamLeaders.get((Object)actorFt))) {
            return ActionResult.failure(team, "\u53ea\u6709\u706b\u529b\u7ec4\u7ec4\u957f\u53ef\u4ee5\u8f6c\u79fb\u7ec4\u957f\u3002");
        }
        if (actor.m_20148_().equals(squad.leader)) {
            return ActionResult.failure(team, "\u5c0f\u961f\u957f\u9700\u8981\u4f7f\u7528\u201c\u8f6c\u79fb\u961f\u957f\u201d\u3002");
        }
        if (actor.m_20148_().equals(targetUuid)) {
            return ActionResult.failure(team, "\u4e0d\u80fd\u8f6c\u79fb\u7ed9\u81ea\u5df1\u3002");
        }
        if (!squad.members.contains(targetUuid) || squad.memberFireteam.get(targetUuid) != actorFt) {
            return ActionResult.failure(team, "\u76ee\u6807\u4e0d\u5728\u4f60\u7684\u706b\u529b\u7ec4\u4e2d\u3002");
        }
        squad.fireteamLeaders.put(actorFt, targetUuid);
        return ActionResult.success(team, "\u5df2\u5c06\u706b\u529b\u7ec4 " + actorFt.label() + " \u7ec4\u957f\u8f6c\u79fb\u7ed9 " + this.getPlayerName(Espetro.getServer(), targetUuid) + "\u3002");
    }

    public ActionResult appointFireteamLeader(ServerPlayer actor, UUID targetUuid, Fireteam fireteam) {
        String team = Espetro.getPlayerTeam(actor);
        if (team == null) {
            return ActionResult.failure(null, "\u4f60\u5c1a\u672a\u52a0\u5165\u9635\u8425\u3002");
        }
        if (!this.isSquadLeader(actor.m_20148_())) {
            return ActionResult.failure(team, "\u53ea\u6709\u5c0f\u961f\u957f\u53ef\u4ee5\u6307\u8ba4\u706b\u529b\u7ec4\u957f\u3002");
        }
        if (fireteam == null || fireteam == Fireteam.A) {
            return ActionResult.failure(team, "\u53ea\u80fd\u6307\u8ba4\u706b\u529b\u7ec4 B \u6216 C \u7684\u7ec4\u957f\u3002");
        }
        Squad squad = this.getSquad(team, this.getPlayerSquadId(actor.m_20148_()));
        if (squad == null || !squad.members.contains(targetUuid)) {
            return ActionResult.failure(team, "\u76ee\u6807\u4e0d\u5728\u4f60\u7684\u5c0f\u961f\u4e2d\u3002");
        }
        if (targetUuid.equals(squad.leader)) {
            return ActionResult.failure(team, "\u5c0f\u961f\u957f\u5fc5\u987b\u62c5\u4efb\u706b\u529b\u7ec4 A \u7ec4\u957f\u3002");
        }
        if (squad.memberFireteam.get(targetUuid) == fireteam && targetUuid.equals(squad.fireteamLeaders.get((Object)fireteam))) {
            return ActionResult.failure(team, "\u8be5\u73a9\u5bb6\u5df2\u7ecf\u662f\u706b\u529b\u7ec4 " + fireteam.label() + " \u7ec4\u957f\u3002");
        }
        SquadManager.moveMemberToFireteam(squad, targetUuid, fireteam);
        squad.fireteamLeaders.put(fireteam, targetUuid);
        return ActionResult.success(team, "\u5df2\u6307\u8ba4 " + this.getPlayerName(Espetro.getServer(), targetUuid) + " \u4e3a\u706b\u529b\u7ec4 " + fireteam.label() + " \u7ec4\u957f\u3002");
    }

    public ActionResult assignFireteam(ServerPlayer actor, UUID targetUuid, Fireteam fireteam) {
        String team = Espetro.getPlayerTeam(actor);
        if (team == null) {
            return ActionResult.failure(null, "\u4f60\u5c1a\u672a\u52a0\u5165\u9635\u8425\u3002");
        }
        if (!this.isSquadLeader(actor.m_20148_())) {
            return ActionResult.failure(team, "\u53ea\u6709\u5c0f\u961f\u957f\u53ef\u4ee5\u5206\u914d\u706b\u529b\u7ec4\u3002");
        }
        if (fireteam == null) {
            return ActionResult.failure(team, "\u65e0\u6548\u7684\u706b\u529b\u7ec4\u3002");
        }
        Squad squad = this.getSquad(team, this.getPlayerSquadId(actor.m_20148_()));
        if (squad == null || !squad.members.contains(targetUuid)) {
            return ActionResult.failure(team, "\u76ee\u6807\u4e0d\u5728\u4f60\u7684\u5c0f\u961f\u4e2d\u3002");
        }
        Fireteam current = squad.memberFireteam.get(targetUuid);
        if (current == fireteam) {
            return ActionResult.failure(team, "\u8be5\u73a9\u5bb6\u5df2\u5728\u706b\u529b\u7ec4 " + fireteam.label() + "\u3002");
        }
        if (targetUuid.equals(squad.leader) && fireteam != Fireteam.A) {
            return ActionResult.failure(team, "\u5c0f\u961f\u957f\u5fc5\u987b\u7559\u5728\u706b\u529b\u7ec4 A\u3002");
        }
        SquadManager.moveMemberToFireteam(squad, targetUuid, fireteam);
        if (targetUuid.equals(squad.leader)) {
            squad.fireteamLeaders.put(Fireteam.A, squad.leader);
        }
        return ActionResult.success(team, "\u5df2\u5c06\u73a9\u5bb6\u5206\u914d\u5230\u706b\u529b\u7ec4 " + fireteam.label() + "\u3002");
    }

    public boolean hasSquad(String team, int squadId) {
        LinkedHashMap<Integer, Squad> squads = this.squadsByTeam.get(team);
        return squads != null && squads.containsKey(squadId);
    }

    public List<SquadSnapshot> getSquadSnapshots(String team) {
        ArrayList<SquadSnapshot> result = new ArrayList<SquadSnapshot>();
        LinkedHashMap<Integer, Squad> squads = this.squadsByTeam.get(team);
        if (squads == null || squads.isEmpty()) {
            return result;
        }
        MinecraftServer server = Espetro.getServer();
        for (Squad squad : squads.values()) {
            ArrayList<MemberSnapshot> members = new ArrayList<MemberSnapshot>();
            List<UUID> ordered = SquadManager.orderedMembersByFireteam(squad);
            for (UUID memberUuid : ordered) {
                String playerName = this.getPlayerName(server, memberUuid);
                String className = this.getPlayerClassName(memberUuid);
                Fireteam ft = squad.memberFireteam.getOrDefault(memberUuid, Fireteam.A);
                boolean ftLead = memberUuid.equals(squad.fireteamLeaders.get((Object)ft));
                members.add(new MemberSnapshot(memberUuid, playerName, className, memberUuid.equals(squad.leader), ft, ftLead));
            }
            result.add(new SquadSnapshot(squad.id, squad.displayId, squad.name, this.getPlayerName(server, squad.leader), squad.leader, 9, squad.locked, squad.categoryId, squad.categoryDisplayName, members));
        }
        return result;
    }

    private Squad getSquad(String team, int squadId) {
        LinkedHashMap<Integer, Squad> squads = this.squadsByTeam.get(team);
        return squads != null ? squads.get(squadId) : null;
    }

    private int claimNextDisplayId(String team) {
        int displayId = this.nextDisplayIdByTeam.getOrDefault(team, 1);
        this.nextDisplayIdByTeam.put(team, displayId + 1);
        return displayId;
    }

    public UUID getSquadLeaderUuid(String team, int squadId) {
        Squad squad = this.getSquad(team, squadId);
        return squad != null ? squad.leader : null;
    }

    public String getSquadName(String team, int squadId) {
        Squad squad = this.getSquad(team, squadId);
        return squad != null ? squad.name : null;
    }

    private Squad getSquadOf(UUID uuid) {
        Integer squadId = this.playerSquads.get(uuid);
        if (squadId == null) {
            return null;
        }
        for (LinkedHashMap<Integer, Squad> squads : this.squadsByTeam.values()) {
            Squad squad = squads.get(squadId);
            if (squad == null || !squad.members.contains(uuid)) continue;
            return squad;
        }
        return null;
    }

    private static List<UUID> orderedMembersByFireteam(Squad squad) {
        ArrayList<UUID> ordered = new ArrayList<UUID>(squad.members);
        ordered.sort(Comparator.comparingInt(id -> squad.memberFireteam.getOrDefault(id, Fireteam.A).index()).thenComparingInt(id -> squad.members.indexOf(id)));
        return ordered;
    }

    private static void addMemberToFireteam(Squad squad, UUID uuid, Fireteam ft, boolean forceLead) {
        if (!squad.members.contains(uuid)) {
            squad.members.add(uuid);
        }
        squad.memberFireteam.put(uuid, ft);
        if (forceLead || squad.fireteamLeaders.get((Object)ft) == null) {
            squad.fireteamLeaders.put(ft, uuid);
        }
    }

    private static void moveMemberToFireteam(Squad squad, UUID uuid, Fireteam target) {
        Fireteam current = squad.memberFireteam.get(uuid);
        if (current == target) {
            return;
        }
        if (current != null && uuid.equals(squad.fireteamLeaders.get((Object)current))) {
            squad.fireteamLeaders.remove((Object)current);
            SquadManager.promoteFireteamLeader(squad, current, uuid);
        }
        squad.memberFireteam.put(uuid, target);
        if (squad.fireteamLeaders.get((Object)target) == null) {
            squad.fireteamLeaders.put(target, uuid);
        }
    }

    private static void promoteFireteamLeader(Squad squad, Fireteam ft, UUID exclude) {
        for (UUID id : squad.members) {
            if (id.equals(exclude) || squad.memberFireteam.get(id) != ft) continue;
            squad.fireteamLeaders.put(ft, id);
            return;
        }
        squad.fireteamLeaders.remove((Object)ft);
    }

    private void removeMemberFireteamState(Squad squad, UUID uuid) {
        Fireteam ft = squad.memberFireteam.remove(uuid);
        if (ft != null && uuid.equals(squad.fireteamLeaders.get((Object)ft))) {
            squad.fireteamLeaders.remove((Object)ft);
            SquadManager.promoteFireteamLeader(squad, ft, uuid);
        }
    }

    private String removePlayerFromCurrentSquad(UUID uuid) {
        Integer currentSquadId = this.playerSquads.remove(uuid);
        String affectedTeam = null;
        for (Map.Entry<String, LinkedHashMap<Integer, Squad>> teamEntry : this.squadsByTeam.entrySet()) {
            Iterator<Map.Entry<Integer, Squad>> iterator = teamEntry.getValue().entrySet().iterator();
            while (iterator.hasNext()) {
                Squad squad = iterator.next().getValue();
                if (currentSquadId != null && squad.id != currentSquadId && !squad.members.contains(uuid) || !squad.members.remove(uuid)) continue;
                this.removeMemberFireteamState(squad, uuid);
                affectedTeam = teamEntry.getKey();
                if (squad.members.isEmpty()) {
                    if (uuid.equals(squad.leader)) {
                        CommanderGovernanceManager.getInstance().onSquadLeaderLost(uuid);
                    }
                    iterator.remove();
                } else if (uuid.equals(squad.leader)) {
                    UUID newLeader;
                    CommanderGovernanceManager.getInstance().onSquadLeaderLost(uuid);
                    squad.leader = newLeader = squad.members.get(0);
                    squad.leaderSinceTick = SquadManager.currentGameTime();
                    SquadManager.moveMemberToFireteam(squad, newLeader, Fireteam.A);
                    squad.fireteamLeaders.put(Fireteam.A, newLeader);
                }
                return affectedTeam;
            }
        }
        return affectedTeam;
    }

    private String getPlayerName(MinecraftServer server, UUID uuid) {
        ServerPlayer player;
        if (server != null && (player = server.m_6846_().m_11259_(uuid)) != null) {
            return player.m_7755_().getString();
        }
        return uuid.toString().substring(0, 8);
    }

    private String getPlayerClassName(UUID uuid) {
        String classId = ClassCountManager.getInstance().getPlayerClass(uuid);
        if (classId == null || classId.isEmpty()) {
            return "\u672a\u9009\u62e9\u804c\u4e1a";
        }
        FactionDataLoader.ClassKitData kit = FactionDataProvider.getOrCreateLoader().getClassKit(classId);
        if (kit != null && kit.name != null && !kit.name.isEmpty()) {
            return kit.name;
        }
        return classId;
    }

    private String sanitizeName(String requestedName) {
        if (requestedName == null) {
            return "";
        }
        String name = FORMAT_CODE.matcher(requestedName).replaceAll("").replace('\n', ' ').replace('\r', ' ').trim();
        if (name.length() > 18) {
            name = name.substring(0, 18);
        }
        return name;
    }

    private static long currentGameTime() {
        MinecraftServer server = Espetro.getServer();
        return server != null ? server.m_129783_().m_46467_() : 0L;
    }

    public static class ActionResult {
        public final boolean success;
        public final String team;
        public final String message;

        private ActionResult(boolean success, String team, String message) {
            this.success = success;
            this.team = team;
            this.message = message;
        }

        public static ActionResult success(String team, String message) {
            return new ActionResult(true, team, message);
        }

        public static ActionResult failure(String team, String message) {
            return new ActionResult(false, team, message);
        }
    }

    private static class Squad {
        private final int id;
        private final int displayId;
        private final String team;
        private final String name;
        private UUID leader;
        private final List<UUID> members = new ArrayList<UUID>();
        private final Map<UUID, Fireteam> memberFireteam = new HashMap<UUID, Fireteam>();
        private final Map<Fireteam, UUID> fireteamLeaders = new EnumMap<Fireteam, UUID>(Fireteam.class);
        private String categoryId = "none";
        private String categoryDisplayName = "\u65e0";
        private long leaderSinceTick;
        private boolean locked;

        private Squad(int id, int displayId, String team, String name, UUID leader) {
            this.id = id;
            this.displayId = displayId;
            this.team = team;
            this.name = name;
            this.leader = leader;
            this.leaderSinceTick = SquadManager.currentGameTime();
        }
    }

    public static class MemberSnapshot {
        public final UUID uuid;
        public final String playerName;
        public final String className;
        public final boolean leader;
        public final Fireteam fireteam;
        public final boolean fireteamLeader;

        public MemberSnapshot(UUID uuid, String playerName, String className, boolean leader) {
            this(uuid, playerName, className, leader, Fireteam.A, leader);
        }

        public MemberSnapshot(UUID uuid, String playerName, String className, boolean leader, Fireteam fireteam, boolean fireteamLeader) {
            this.uuid = uuid;
            this.playerName = playerName;
            this.className = className;
            this.leader = leader;
            this.fireteam = fireteam != null ? fireteam : Fireteam.A;
            this.fireteamLeader = fireteamLeader;
        }
    }

    public static class SquadSnapshot {
        public final int id;
        public final int displayId;
        public final String name;
        public final String leaderName;
        public final UUID leaderUuid;
        public final int maxMembers;
        public final boolean locked;
        public final String categoryId;
        public final String categoryDisplayName;
        public final List<MemberSnapshot> members;

        public SquadSnapshot(int id, String name, String leaderName, int maxMembers, boolean locked, List<MemberSnapshot> members) {
            this(id, id, name, leaderName, null, maxMembers, locked, "none", "\u65e0", members);
        }

        public SquadSnapshot(int id, String name, String leaderName, UUID leaderUuid, int maxMembers, boolean locked, String categoryId, String categoryDisplayName, List<MemberSnapshot> members) {
            this(id, id, name, leaderName, leaderUuid, maxMembers, locked, categoryId, categoryDisplayName, members);
        }

        public SquadSnapshot(int id, int displayId, String name, String leaderName, UUID leaderUuid, int maxMembers, boolean locked, String categoryId, String categoryDisplayName, List<MemberSnapshot> members) {
            this.id = id;
            this.displayId = displayId;
            this.name = name;
            this.leaderName = leaderName;
            this.leaderUuid = leaderUuid;
            this.maxMembers = maxMembers;
            this.locked = locked;
            this.categoryId = categoryId != null ? categoryId : "none";
            this.categoryDisplayName = categoryDisplayName != null ? categoryDisplayName : "\u65e0";
            this.members = members != null ? members : new ArrayList();
        }
    }
}

