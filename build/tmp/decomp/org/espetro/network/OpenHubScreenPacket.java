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

public class OpenHubScreenPacket {
    public final int onlineCount;
    public final String statusMessage;

    public OpenHubScreenPacket(int onlineCount, String statusMessage) {
        this.onlineCount = onlineCount;
        this.statusMessage = statusMessage == null ? "" : statusMessage;
    }

    public static OpenHubScreenPacket read(FriendlyByteBuf buf) {
        return new OpenHubScreenPacket(buf.m_130242_(), buf.m_130277_());
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130130_(this.onlineCount);
        buf.m_130070_(this.statusMessage);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleOpenHubScreen", OpenHubScreenPacket.class).invoke(null, this);
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

