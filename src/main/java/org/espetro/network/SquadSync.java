package org.espetro.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.espetro.Espetro;
import org.espetro.config.GameConfig;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.team.*;
import org.espetro.network.core.PacketRegistrar;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 小队、指挥官名单与组队（Party）的服务端同步。
 */
public final class SquadSync {

    private SquadSync() {
    }

    /**
     * 同步指定玩家的班组小队数据。
     */
    public static void sendSquadSync(ServerPlayer player) {
        String team = Espetro.getPlayerTeam(player);
        if (team == null) return;

        sendSquadSync(player, team, buildSquadInfoList(team), getCommanderNames(team));
    }

    /**
     * 同步某一攻/守方所有在线玩家的班组小队数据。
     */
    public static void syncSquadsToTeam(String team) {
        MinecraftServer server = Espetro.getServer();
        if (server == null || team == null) return;

        java.util.List<UnifiedDeployScreenPacket.SquadInfo> squadList = buildSquadInfoList(team);
        java.util.List<String> commanderNames = getCommanderNames(team);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (team.equals(Espetro.getPlayerTeam(player))) {
                sendSquadSync(player, team, squadList, commanderNames);
            }
        }
    }

    private static void sendSquadSync(ServerPlayer player, String team,
                                      java.util.List<UnifiedDeployScreenPacket.SquadInfo> squadList,
                                      java.util.List<String> commanderNames) {
        SquadSyncPacket packet = new SquadSyncPacket(
            team,
            squadList,
            SquadManager.getInstance().getPlayerSquadId(player.getUUID()),
            commanderNames,
            GameConfig.getTeammateNameTagDistance(),
            java.util.Objects.toString(
                ClassCountManager.getInstance().getPlayerClass(player.getUUID()), "")
        );
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    static java.util.List<UnifiedDeployScreenPacket.SquadInfo> buildSquadInfoList(String team) {
        java.util.List<UnifiedDeployScreenPacket.SquadInfo> squadList = new java.util.ArrayList<>();
        java.util.Set<UUID> commanderUuids = getCommanderUuids(team);
        for (SquadManager.SquadSnapshot squad : SquadManager.getInstance().getSquadSnapshots(team)) {
            java.util.List<UnifiedDeployScreenPacket.SquadMemberInfo> members = new java.util.ArrayList<>();
            for (SquadManager.MemberSnapshot member : squad.members) {
                members.add(new UnifiedDeployScreenPacket.SquadMemberInfo(
                    member.uuid, member.playerName, member.className,
                    member.leader, commanderUuids.contains(member.uuid),
                    member.fireteam.toNetwork(), member.fireteamLeader));
            }
            squadList.add(new UnifiedDeployScreenPacket.SquadInfo(
                squad.id, squad.displayId, squad.name, members.size(), squad.maxMembers, squad.locked,
                squad.leaderName, squad.categoryId, squad.categoryDisplayName, members
            ));
        }
        return squadList;
    }

    static java.util.List<String> getCommanderNames(String team) {
        MinecraftServer server = Espetro.getServer();
        java.util.List<String> names = new java.util.ArrayList<>();
        if (server == null) return names;

        for (UUID uuid : getCommanderUuids(team)) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) {
                names.add(player.getName().getString());
            }
        }
        return names;
    }

    private static java.util.Set<UUID> getCommanderUuids(String team) {
        java.util.Set<UUID> result = new java.util.HashSet<>();
        VoteManager voteManager = VoteManager.getInstance();
        if ("ATTACK".equals(team)) {
            UUID uuid = voteManager.getAttackCommander();
            if (uuid != null) result.add(uuid);
        } else if ("DEFEND".equals(team)) {
            UUID uuid = voteManager.getDefendCommander();
            if (uuid != null) result.add(uuid);
        }
        return result;
    }

    /** 服务端：向所有在线玩家广播队伍列表。 */
    public static void broadcastPartyList(org.espetro.team.PartyManager pm) {
        var server = org.espetro.Espetro.getServer();
        if (server == null) return;
        for (var player : server.getPlayerList().getPlayers()) {
            sendPartyListTo(player);
        }
    }

    /** 服务端：向指定玩家发送队伍列表。 */
    public static void sendPartyListTo(net.minecraft.server.level.ServerPlayer player) {
        NetworkManager.NET.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
            PartyListPacket.from(org.espetro.team.PartyManager.getInstance(), player.getUUID()));
    }
}
