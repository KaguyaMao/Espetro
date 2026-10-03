/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import org.espetro.network.UnifiedDeployScreenPacket;

public final class ClientTacticalState {
    private static final int NO_SQUAD = -1;
    private static final byte FIRETEAM_NONE = -1;
    private static final Map<String, MarkerInfo> markersByName = new HashMap<String, MarkerInfo>();
    private static final Set<String> commanderNames = new HashSet<String>();
    private static int mySquadId = -1;
    private static byte myFireteam = (byte)-1;
    private static double teammateNameTagDistance = 10.0;

    private ClientTacticalState() {
    }

    public static void updateSquads(List<UnifiedDeployScreenPacket.SquadInfo> squads, int updatedMySquadId, List<String> updatedCommanderNames, double updatedNameTagDistance) {
        markersByName.clear();
        commanderNames.clear();
        mySquadId = updatedMySquadId;
        myFireteam = (byte)-1;
        double d = teammateNameTagDistance = updatedNameTagDistance > 0.0 ? updatedNameTagDistance : 10.0;
        if (updatedCommanderNames != null) {
            for (String commanderName : updatedCommanderNames) {
                if (commanderName == null || commanderName.isEmpty()) continue;
                commanderNames.add(ClientTacticalState.key(commanderName));
            }
        }
        if (squads == null) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        String localName = mc.f_91074_ != null ? ClientTacticalState.key(mc.f_91074_.m_7755_().getString()) : "";
        for (UnifiedDeployScreenPacket.SquadInfo squad : squads) {
            for (UnifiedDeployScreenPacket.SquadMemberInfo member : squad.members) {
                String memberKey = ClientTacticalState.key(member.playerName);
                markersByName.put(memberKey, new MarkerInfo(squad.id, squad.displayId, member.leader, member.fireteamLeader, member.commander, member.fireteam));
                if (member.commander) {
                    commanderNames.add(memberKey);
                }
                if (!memberKey.equals(localName)) continue;
                myFireteam = member.fireteam;
            }
        }
    }

    public static int getNameColor(String playerName) {
        String key = ClientTacticalState.key(playerName);
        MarkerInfo info = markersByName.get(key);
        if (commanderNames.contains(key) || info != null && info.commander) {
            return -14490;
        }
        if (info != null && info.squadId == mySquadId && mySquadId != -1) {
            return info.leader ? -2847489 : -9984001;
        }
        return -1;
    }

    public static int getSquadMemberColor(int squadId, UnifiedDeployScreenPacket.SquadMemberInfo member) {
        if (member.commander || commanderNames.contains(ClientTacticalState.key(member.playerName))) {
            return -14490;
        }
        if (squadId == mySquadId && mySquadId != -1) {
            return member.leader ? -2847489 : -9984001;
        }
        return -1;
    }

    public static double getTeammateNameTagDistance() {
        return teammateNameTagDistance;
    }

    public static boolean isInSquad() {
        return mySquadId != -1;
    }

    public static int getMySquadId() {
        return mySquadId;
    }

    public static boolean isLocalSquadLeader(String playerName) {
        MarkerInfo info = markersByName.get(ClientTacticalState.key(playerName));
        return info != null && info.squadId == mySquadId && mySquadId != -1 && info.leader;
    }

    public static boolean canLocalPlayerOpenTacticalRadial(String playerName) {
        MarkerInfo info = markersByName.get(ClientTacticalState.key(playerName));
        return ClientTacticalState.hasSquadLeaderAccess(mySquadId, info);
    }

    public static boolean canLocalPlayerPlacePing(String playerName) {
        String normalized = ClientTacticalState.key(playerName);
        MarkerInfo info = markersByName.get(normalized);
        return commanderNames.contains(normalized) || info != null && (info.commander || info.leader || info.fireteamLeader);
    }

    static boolean hasSquadLeaderAccess(int localSquadId, MarkerInfo info) {
        return localSquadId != -1 && info != null && info.squadId == localSquadId && info.leader;
    }

    private static String key(String name) {
        return name == null ? "" : name.toLowerCase(Locale.ROOT);
    }

    public static MarkerInfo getMarker(String playerName) {
        return markersByName.get(ClientTacticalState.key(playerName));
    }

    public static boolean isCommander(String playerName) {
        return commanderNames.contains(ClientTacticalState.key(playerName));
    }

    public static byte getMyFireteam() {
        return myFireteam;
    }

    public record MarkerInfo(int squadId, int displayId, boolean leader, boolean fireteamLeader, boolean commander, byte fireteam) {
        public MarkerInfo(int squadId, boolean leader, boolean fireteamLeader, boolean commander, byte fireteam) {
            this(squadId, squadId, leader, fireteamLeader, commander, fireteam);
        }

        public MarkerInfo(int squadId, boolean leader, boolean fireteamLeader, boolean commander) {
            this(squadId, squadId, leader, fireteamLeader, commander, 0);
        }
    }
}

