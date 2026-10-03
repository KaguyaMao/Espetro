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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.team.VoteManager;

public class CastVotePacket {
    private final String targetPlayerName;

    public CastVotePacket(String targetPlayerName) {
        this.targetPlayerName = targetPlayerName;
    }

    public static CastVotePacket read(FriendlyByteBuf buf) {
        String targetPlayerName = buf.m_130277_();
        return new CastVotePacket(targetPlayerName);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.targetPlayerName);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer targetPlayer;
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            UUID targetUUID = null;
            MinecraftServer server = player.m_20194_();
            if (server != null && (targetPlayer = server.m_6846_().m_11255_(this.targetPlayerName)) != null) {
                targetUUID = targetPlayer.m_20148_();
            }
            if (targetUUID != null) {
                VoteManager.getInstance().castVote(player, targetUUID);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

