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
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.network.NetworkManager;
import org.espetro.stats.PlayerMatchStatsManager;
import org.espetro.team.ClassCountManager;
import org.espetro.team.SquadManager;

public class MatchStatsActionPacket {
    private final Action action;
    private final UUID target;

    public MatchStatsActionPacket(Action action, UUID target) {
        this.action = action;
        this.target = target;
    }

    public static MatchStatsActionPacket read(FriendlyByteBuf buf) {
        Action a;
        try {
            a = Action.valueOf(buf.m_130277_());
        }
        catch (Exception e) {
            a = Action.FORCE_JOIN_SQUAD;
        }
        return new MatchStatsActionPacket(a, buf.m_130259_());
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.action.name());
        buf.m_130077_(this.target);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            SquadManager.ActionResult result = switch (this.action) {
                default -> throw new IncompatibleClassChangeError();
                case Action.FORCE_JOIN_SQUAD -> SquadManager.getInstance().forceJoinSquad(player, this.target);
                case Action.KICK_FROM_SQUAD -> SquadManager.getInstance().kickMember(player, this.target);
            };
            player.m_213846_(Component.m_237113_((result.success ? "\u00a7a" : "\u00a7c") + result.message));
            if (result.success && result.team != null) {
                NetworkManager.syncSquadsToTeam(result.team);
                NetworkManager.broadcastClassCounts(result.team, ClassCountManager.getInstance().getPlayerFaction(player.m_20148_()));
                NetworkManager.broadcastMatchStats(PlayerMatchStatsManager.getInstance());
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static enum Action {
        FORCE_JOIN_SQUAD,
        KICK_FROM_SQUAD;

    }
}

