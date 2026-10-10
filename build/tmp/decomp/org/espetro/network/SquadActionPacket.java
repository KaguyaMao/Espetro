/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.Espetro;
import org.espetro.network.NetworkManager;
import org.espetro.stats.PlayerMatchStatsManager;
import org.espetro.team.ClassCountManager;
import org.espetro.team.Fireteam;
import org.espetro.team.SquadManager;
import org.espetro.team.TeamPackManager;

public class SquadActionPacket {
    private final Action action;
    private final int squadId;
    private final String squadName;
    private final UUID targetUuid;
    private final byte fireteamIndex;

    public SquadActionPacket(Action action, int squadId, String squadName) {
        this(action, squadId, squadName, null, -1);
    }

    public SquadActionPacket(Action action, int squadId, String squadName, UUID targetUuid, byte fireteamIndex) {
        this.action = action;
        this.squadId = squadId;
        this.squadName = squadName == null ? "" : squadName;
        this.targetUuid = targetUuid;
        this.fireteamIndex = fireteamIndex;
    }

    public static SquadActionPacket create(String squadName) {
        return new SquadActionPacket(Action.CREATE, -1, squadName);
    }

    public static SquadActionPacket join(int squadId) {
        return new SquadActionPacket(Action.JOIN, squadId, "");
    }

    public static SquadActionPacket leave() {
        return new SquadActionPacket(Action.LEAVE, -1, "");
    }

    public static SquadActionPacket delete(int squadId) {
        return new SquadActionPacket(Action.DELETE, squadId, "");
    }

    public static SquadActionPacket transferSquadLeader(UUID targetUuid) {
        return new SquadActionPacket(Action.TRANSFER_SQUAD_LEADER, -1, "", targetUuid, -1);
    }

    public static SquadActionPacket transferFireteamLeader(UUID targetUuid) {
        return new SquadActionPacket(Action.TRANSFER_FIRETEAM_LEADER, -1, "", targetUuid, -1);
    }

    public static SquadActionPacket appointFireteamLeader(UUID targetUuid, Fireteam fireteam) {
        return new SquadActionPacket(Action.APPOINT_FIRETEAM_LEADER, -1, "", targetUuid, fireteam != null ? (byte)fireteam.toNetwork() : (byte)-1);
    }

    public static SquadActionPacket assignFireteam(UUID targetUuid, Fireteam fireteam) {
        return new SquadActionPacket(Action.ASSIGN_FIRETEAM, -1, "", targetUuid, fireteam != null ? (byte)fireteam.toNetwork() : (byte)-1);
    }

    public static SquadActionPacket lock() {
        return new SquadActionPacket(Action.LOCK, -1, "");
    }

    public static SquadActionPacket unlock() {
        return new SquadActionPacket(Action.UNLOCK, -1, "");
    }

    public static SquadActionPacket read(FriendlyByteBuf buf) {
        Action action;
        try {
            action = Action.valueOf(buf.m_130277_());
        }
        catch (IllegalArgumentException ignored) {
            action = Action.JOIN;
        }
        int squadId = buf.m_130242_();
        String squadName = buf.m_130277_();
        UUID targetUuid = buf.readBoolean() ? buf.m_130259_() : null;
        byte fireteamIndex = buf.readByte();
        return new SquadActionPacket(action, squadId, squadName, targetUuid, fireteamIndex);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.action.name());
        buf.m_130130_(this.squadId);
        buf.m_130070_(this.squadName);
        if (this.targetUuid != null) {
            buf.writeBoolean(true);
            buf.m_130077_(this.targetUuid);
        } else {
            buf.writeBoolean(false);
        }
        buf.writeByte(this.fireteamIndex);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            String previousTeam = Espetro.getPlayerTeam(player);
            int previousSquadId = SquadManager.getInstance().getPlayerSquadId(player.m_20148_());
            boolean wasLeader = SquadManager.getInstance().isSquadLeader(player.m_20148_());
            SquadManager.ActionResult result = switch (this.action) {
                default -> throw new IncompatibleClassChangeError();
                case Action.CREATE -> SquadManager.getInstance().createSquad(player, this.squadName);
                case Action.JOIN -> SquadManager.getInstance().joinSquad(player, this.squadId);
                case Action.LEAVE -> SquadManager.getInstance().leaveSquad(player);
                case Action.DELETE -> SquadManager.getInstance().deleteSquad(player, this.squadId);
                case Action.TRANSFER_SQUAD_LEADER -> {
                    if (this.targetUuid == null) {
                        yield SquadManager.ActionResult.failure(previousTeam, "\u7f3a\u5c11\u76ee\u6807\u73a9\u5bb6\u3002");
                    }
                    yield SquadManager.getInstance().transferSquadLeader(player, this.targetUuid);
                }
                case Action.TRANSFER_FIRETEAM_LEADER -> {
                    if (this.targetUuid == null) {
                        yield SquadManager.ActionResult.failure(previousTeam, "\u7f3a\u5c11\u76ee\u6807\u73a9\u5bb6\u3002");
                    }
                    yield SquadManager.getInstance().transferFireteamLeader(player, this.targetUuid);
                }
                case Action.APPOINT_FIRETEAM_LEADER -> {
                    if (this.targetUuid == null || this.fireteamIndex < 1 || this.fireteamIndex > 2) {
                        yield SquadManager.ActionResult.failure(previousTeam, "\u65e0\u6548\u7684\u706b\u529b\u7ec4\u957f\u6307\u8ba4\u3002");
                    }
                    yield SquadManager.getInstance().appointFireteamLeader(player, this.targetUuid, Fireteam.fromIndex(this.fireteamIndex));
                }
                case Action.ASSIGN_FIRETEAM -> {
                    if (this.targetUuid == null || this.fireteamIndex < 0 || this.fireteamIndex > 2) {
                        yield SquadManager.ActionResult.failure(previousTeam, "\u65e0\u6548\u7684\u706b\u529b\u7ec4\u5206\u914d\u3002");
                    }
                    yield SquadManager.getInstance().assignFireteam(player, this.targetUuid, Fireteam.fromIndex(this.fireteamIndex));
                }
                case Action.LOCK -> SquadManager.getInstance().lockSquad(player);
                case Action.UNLOCK -> SquadManager.getInstance().unlockSquad(player);
            };
            player.m_213846_(Component.m_237113_((result.success ? "\u00a7a" : "\u00a7c") + result.message));
            if (result.success) {
                String currentTeam = Espetro.getPlayerTeam(player);
                int currentSquadId = SquadManager.getInstance().getPlayerSquadId(player.m_20148_());
                boolean isLeaderNow = SquadManager.getInstance().isSquadLeader(player.m_20148_());
                TeamPackManager.getInstance().handleSquadLeaderTransition(player, previousTeam, previousSquadId, wasLeader, currentTeam, currentSquadId, isLeaderNow);
                if (this.action == Action.TRANSFER_SQUAD_LEADER && this.targetUuid != null) {
                    MinecraftServerBridge.promoteNewLeaderPack(this.targetUuid, previousTeam, previousSquadId);
                    MinecraftServerBridge.refreshSkillSync(this.targetUuid);
                } else if ((this.action == Action.TRANSFER_FIRETEAM_LEADER || this.action == Action.APPOINT_FIRETEAM_LEADER || this.action == Action.ASSIGN_FIRETEAM) && this.targetUuid != null) {
                    MinecraftServerBridge.reconcileTargetPack(this.targetUuid);
                }
                if (wasLeader != isLeaderNow || this.action == Action.CREATE || this.action == Action.LEAVE || this.action == Action.DELETE || this.action == Action.TRANSFER_SQUAD_LEADER) {
                    NetworkManager.sendCommanderSkillSync(player);
                }
                NetworkManager.broadcastMatchStats(PlayerMatchStatsManager.getInstance());
            }
            if (result.team != null) {
                TeamPackManager.getInstance().reconcileTeam(result.team);
                NetworkManager.syncSquadsToTeam(result.team);
                NetworkManager.broadcastClassCounts(result.team, ClassCountManager.getInstance().getPlayerFaction(player.m_20148_()));
                if (this.action == Action.CREATE || this.action == Action.JOIN) {
                    NetworkManager.syncUnifiedDeployScreen(player, -1);
                }
            } else {
                TeamPackManager.getInstance().syncTeamPackItem(player);
                NetworkManager.sendSquadSync(player);
                NetworkManager.syncUnifiedDeployScreen(player, -1);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static enum Action {
        CREATE,
        JOIN,
        LEAVE,
        DELETE,
        TRANSFER_SQUAD_LEADER,
        TRANSFER_FIRETEAM_LEADER,
        APPOINT_FIRETEAM_LEADER,
        ASSIGN_FIRETEAM,
        LOCK,
        UNLOCK;

    }

    private static final class MinecraftServerBridge {
        private MinecraftServerBridge() {
        }

        static void reconcileTargetPack(UUID targetUuid) {
            MinecraftServer server = Espetro.getServer();
            if (server == null) {
                return;
            }
            ServerPlayer target = server.m_6846_().m_11259_(targetUuid);
            if (target != null) {
                TeamPackManager.getInstance().syncTeamPackItem(target);
            }
        }

        static void promoteNewLeaderPack(UUID targetUuid, String team, int squadId) {
            MinecraftServer server = Espetro.getServer();
            if (server == null) {
                return;
            }
            ServerPlayer target = server.m_6846_().m_11259_(targetUuid);
            if (target == null) {
                return;
            }
            TeamPackManager.getInstance().handleSquadLeaderTransition(target, team, squadId, false, team, squadId, true);
            NetworkManager.syncUnifiedDeployScreen(target, -1);
        }

        static void refreshSkillSync(UUID targetUuid) {
            MinecraftServer server = Espetro.getServer();
            if (server == null || targetUuid == null) {
                return;
            }
            ServerPlayer target = server.m_6846_().m_11259_(targetUuid);
            if (target != null) {
                NetworkManager.sendCommanderSkillSync(target);
            }
        }
    }
}

