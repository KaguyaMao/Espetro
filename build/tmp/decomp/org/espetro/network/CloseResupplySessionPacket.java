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

public record CloseResupplySessionPacket(UUID token) {
    public static CloseResupplySessionPacket read(FriendlyByteBuf buf) {
        return new CloseResupplySessionPacket(buf.m_130259_());
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130077_(this.token);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ResupplySessionManager.close(player.m_20148_(), this.token);
            }
        });
        context.setPacketHandled(true);
    }
}

