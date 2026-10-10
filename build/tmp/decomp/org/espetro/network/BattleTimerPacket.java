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

public class BattleTimerPacket {
    private final int remainingSeconds;

    public BattleTimerPacket(int remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    public static BattleTimerPacket read(FriendlyByteBuf buf) {
        return new BattleTimerPacket(buf.m_130242_());
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130130_(this.remainingSeconds);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleBattleTimer", BattleTimerPacket.class).invoke(null, this);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public int getRemainingSeconds() {
        return this.remainingSeconds;
    }
}

