/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class OpenMapVoteScreenPacket {
    public static OpenMapVoteScreenPacket read(FriendlyByteBuf buf) {
        return new OpenMapVoteScreenPacket();
    }

    public void write(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleOpenMapVoteScreen", new Class[0]).invoke(null, new Object[0]);
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

