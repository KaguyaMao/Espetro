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
import org.espetro.logistics.resupply.ResupplySessionManager;
import org.espetro.logistics.resupply.ResupplySourceRef;

public record RequestResupplyCatalogPacket(ResupplySourceRef source) {
    public static RequestResupplyCatalogPacket read(FriendlyByteBuf buf) {
        return new RequestResupplyCatalogPacket(ResupplySourceRef.read(buf));
    }

    public void write(FriendlyByteBuf buf) {
        this.source.write(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ResupplySessionManager.open(player, this.source);
            }
        });
        context.setPacketHandled(true);
    }
}

