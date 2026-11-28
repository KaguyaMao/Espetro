package org.espetro.client.gui;

import org.espetro.network.UnifiedDeployScreenPacket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 客户端战术显示状态。
 * 头顶名牌和班组小队界面共用这里的颜色优先级。
 */
public final class ClientTacticalState {

    private static final int NO_SQUAD = -1;
    private static final byte FIRETEAM_NONE = -1;

    private static final Map<String, MarkerInfo> markersByName = new HashMap<>();
    private static final Set<String> commanderNames = new HashSet<>();
    private static Map<Integer,Integer> markerSquads = Map.of();
    public static Map<Integer,Integer> markerTargetSquads() { return markerSquads; }
    private static int mySquadId = NO_SQUAD;
    private static UnifiedDeployScreenPacket.SquadInfo localSquad;
    private static List<UnifiedDeployScreenPacket.SquadMemberInfo> rosterMembers = List.of();
    private static final Map<UUID, Integer> squadNumbers = new HashMap<>();
    private static final Map<UUID, String> classNames = new HashMap<>();
    private static byte myFireteam = FIRETEAM_NONE;
    private static double teammateNameTagDistance = 10.0;

    private ClientTacticalState() {
    }

    public static void updateSquads(List<UnifiedDeployScreenPacket.SquadInfo> squads, int updatedMySquadId,
                                    List<String> updatedCommanderNames, double updatedNameTagDistance) {
        var previousSquad = localSquad;
        markersByName.clear();
        squadNumbers.clear();
        classNames.clear();
        localSquad = null;
        rosterMembers = List.of();
        commanderNames.clear();
        mySquadId = updatedMySquadId;
        myFireteam = FIRETEAM_NONE;
        teammateNameTagDistance = updatedNameTagDistance > 0 ? updatedNameTagDistance : 10.0;

        if (updatedCommanderNames != null) {
            for (String commanderName : updatedCommanderNames) {
                if (commanderName != null && !commanderName.isEmpty()) {
                    commanderNames.add(key(commanderName));
                }
            }
        }

        if (squads == null) {
            markerSquads=Map.of();
            if (previousSquad != null) TacticalHud.rosterUpdated();
            return;
        }

        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        String localName = mc.player != null ? key(mc.player.getName().getString()) : "";

        var numbered=new java.util.TreeMap<Integer,Integer>();
        for(var squad:squads) numbered.put(squad.id,squad.displayId);
        markerSquads=java.util.Collections.unmodifiableMap(numbered);
        for (UnifiedDeployScreenPacket.SquadInfo squad : squads) {
            if (squad.id == mySquadId) localSquad = squad;
            for (UnifiedDeployScreenPacket.SquadMemberInfo member : squad.members) {
                squadNumbers.put(member.uuid, squad.displayId);
                classNames.put(member.uuid, member.className);
                String memberKey = key(member.playerName);
                markersByName.put(memberKey,
                    new MarkerInfo(squad.id, squad.displayId, member.leader, member.fireteamLeader,
                        member.commander, member.fireteam));
                if (member.commander) {
                    commanderNames.add(memberKey);
                }
                if (memberKey.equals(localName)) {
                    myFireteam = member.fireteam;
                }
            }
        }
        if (localSquad != null) {
            rosterMembers = localSquad.members.stream()
                .sorted(java.util.Comparator
                    .comparingInt((UnifiedDeployScreenPacket.SquadMemberInfo member) -> member.leader ? 0 : 1)
                    .thenComparingInt(member -> member.fireteam)
                    .thenComparingInt(member -> member.fireteamLeader ? 0 : 1))
                .toList();
        }
        if (localSquad == null ? previousSquad != null : previousSquad == null
                || localSquad.id != previousSquad.id || localSquad.displayId != previousSquad.displayId
                || !localSquad.name.equals(previousSquad.name)
                || !localSquad.members.equals(previousSquad.members)) {
            TacticalHud.rosterUpdated();
        }
    }

    static List<UnifiedDeployScreenPacket.SquadMemberInfo> rosterMembers() { return rosterMembers; }

    static UnifiedDeployScreenPacket.SquadInfo localSquad() {
        return localSquad;
    }

    static Integer squadNumber(UUID playerId) {
        return squadNumbers.get(playerId);
    }

    static String className(UUID playerId) {
        return classNames.get(playerId);
    }

    public static int getNameColor(String playerName) {
        String key = key(playerName);
        MarkerInfo info = markersByName.get(key);
        if (commanderNames.contains(key) || (info != null && info.commander)) {
            return EspetroAuiWidgets.GOLD;
        }
        if (info != null && info.squadId == mySquadId && mySquadId != NO_SQUAD) {
            return info.leader ? EspetroAuiWidgets.PURPLE : EspetroAuiWidgets.SQUAD_BLUE;
        }
        return EspetroAuiWidgets.TEXT;
    }

    public static int getSquadMemberColor(int squadId, UnifiedDeployScreenPacket.SquadMemberInfo member) {
        if (member.commander || commanderNames.contains(key(member.playerName))) {
            return EspetroAuiWidgets.GOLD;
        }
        if (squadId == mySquadId && mySquadId != NO_SQUAD) {
            return member.leader ? EspetroAuiWidgets.PURPLE : EspetroAuiWidgets.SQUAD_BLUE;
        }
        return EspetroAuiWidgets.TEXT;
    }

    public static double getTeammateNameTagDistance() {
        return teammateNameTagDistance;
    }

    public static boolean isInSquad() {
        return mySquadId != NO_SQUAD;
    }

    public static int getMySquadId() {
        return mySquadId;
    }

    public static boolean isLocalSquadLeader(String playerName) {
        MarkerInfo info = markersByName.get(key(playerName));
        return info != null && info.squadId == mySquadId && mySquadId != NO_SQUAD && info.leader;
    }

    /**
     * 本地玩家是否拥有领导类战术权限。用于在客户端打开轮盘前拦截普通队员；
     * 具体动作仍由服务端再次权威校验。
     */
    public static boolean canLocalPlayerOpenTacticalRadial(String playerName) {
        MarkerInfo info = markersByName.get(key(playerName));
        return hasSquadLeaderAccess(mySquadId, info) || hasFireteamLeaderAccess(mySquadId, info);
    }

    /** ESPoints 标点轮盘仍允许指挥官、小队长和火力组长。 */
    public static boolean canLocalPlayerPlacePing(String playerName) {
        String normalized = key(playerName);
        MarkerInfo info = markersByName.get(normalized);
        return commanderNames.contains(normalized)
            || (info != null && (info.commander || info.leader || info.fireteamLeader));
    }

    static boolean hasSquadLeaderAccess(int localSquadId, MarkerInfo info) {
        return localSquadId != NO_SQUAD
            && info != null
            && info.squadId == localSquadId
            && info.leader;
    }

    /** 本地玩家是否是所在火力组的组长（火力组长可开轮盘建工事，但不能建电台/放队包）。 */
    public static boolean isLocalFireteamLeader(String playerName) {
        MarkerInfo info = markersByName.get(key(playerName));
        return hasFireteamLeaderAccess(mySquadId, info);
    }

    static boolean hasFireteamLeaderAccess(int localSquadId, MarkerInfo info) {
        return localSquadId != NO_SQUAD
            && info != null
            && info.squadId == localSquadId
            && info.fireteamLeader;
    }

    private static String key(String name) {
        return name == null ? "" : name.toLowerCase(Locale.ROOT);
    }

    /** 公开：获取玩家的战术标记信息。 */
    public static MarkerInfo getMarker(String playerName) {
        return markersByName.get(key(playerName));
    }

    /** 公开：判断指定玩家是否为指挥官。 */
    public static boolean isCommander(String playerName) {
        return commanderNames.contains(key(playerName));
    }

    /** 公开：获取本地玩家的火力组索引（0=A, 1=B, 2=C），不在小队中返回 -1。 */
    public static byte getMyFireteam() {
        return myFireteam;
    }

    public record MarkerInfo(int squadId, int displayId, boolean leader, boolean fireteamLeader,
                             boolean commander, byte fireteam) {
        /** 兼容旧构造（显示序号与内部 ID 相同）。 */
        public MarkerInfo(int squadId, boolean leader, boolean fireteamLeader,
                          boolean commander, byte fireteam) {
            this(squadId, squadId, leader, fireteamLeader, commander, fireteam);
        }

        /** 兼容旧构造（无 fireteam 时默认 0 = A 组）。 */
        public MarkerInfo(int squadId, boolean leader, boolean fireteamLeader,
                          boolean commander) {
            this(squadId, squadId, leader, fireteamLeader, commander, (byte) 0);
        }
    }
}
