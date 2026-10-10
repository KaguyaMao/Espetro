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

public class WaitingStatusPacket {
    private String message;
    private boolean isActionBar;

    public WaitingStatusPacket(String message, boolean isActionBar) {
        this.message = message;
        this.isActionBar = isActionBar;
    }

    public WaitingStatusPacket() {
    }

    public static WaitingStatusPacket read(FriendlyByteBuf buf) {
        String message = buf.m_130277_();
        boolean isActionBar = buf.readBoolean();
        return new WaitingStatusPacket(message, isActionBar);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.message);
        buf.writeBoolean(this.isActionBar);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleWaitingStatus", String.class, Boolean.TYPE).invoke(null, this.message, this.isActionBar);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isActionBar() {
        return this.isActionBar;
    }
}

