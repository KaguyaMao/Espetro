/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.team.MapVoteManager;

public class MapVoteCastPacket {
    private final String mapFolder;

    public MapVoteCastPacket(String mapFolder) {
        this.mapFolder = mapFolder == null ? "" : mapFolder;
    }

    public static MapVoteCastPacket read(FriendlyByteBuf buf) {
        return new MapVoteCastPacket(buf.m_130277_());
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.mapFolder);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            MapVoteManager.getInstance().castVote(player, this.mapFolder);
        });
        ctx.get().setPacketHandled(true);
    }
}

