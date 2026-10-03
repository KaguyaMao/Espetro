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

public class OpenFactionScreenPacket {
    public static OpenFactionScreenPacket read(FriendlyByteBuf buf) {
        return new OpenFactionScreenPacket();
    }

    public void write(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleOpenFactionScreen", new Class[0]).invoke(null, new Object[0]);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

