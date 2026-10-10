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
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.logistics.resupply.ResupplySessionManager;
import org.espetro.logistics.resupply.ResupplySourceRef;

public record SelectResupplyEntryPacket(UUID token, long catalogRevision, long actionSeq, int entryIndex, ResupplySourceRef source) {
    public static SelectResupplyEntryPacket read(FriendlyByteBuf buf) {
        return new SelectResupplyEntryPacket(buf.m_130259_(), buf.readLong(), buf.readLong(), buf.m_130242_(), ResupplySourceRef.read(buf));
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130077_(this.token);
        buf.writeLong(this.catalogRevision);
        buf.writeLong(this.actionSeq);
        buf.m_130130_(this.entryIndex);
        this.source.write(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ResupplySessionManager.select(player, this);
            }
        });
        context.setPacketHandled(true);
    }
}

