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

public class ClassSelectTimerPacket {
    private final int timeRemaining;
    private final int opponentTimeRemaining;
    private final String selectedFactionId;
    private final boolean isCommander;

    public ClassSelectTimerPacket(int timeRemaining, int opponentTimeRemaining, String selectedFactionId, boolean isCommander) {
        this.timeRemaining = timeRemaining;
        this.opponentTimeRemaining = opponentTimeRemaining;
        this.selectedFactionId = selectedFactionId == null ? "" : selectedFactionId;
        this.isCommander = isCommander;
    }

    public static ClassSelectTimerPacket read(FriendlyByteBuf buf) {
        return new ClassSelectTimerPacket(buf.m_130242_(), buf.m_130242_(), buf.m_130277_(), buf.readBoolean());
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130130_(this.timeRemaining);
        buf.m_130130_(this.opponentTimeRemaining);
        buf.m_130070_(this.selectedFactionId);
        buf.writeBoolean(this.isCommander);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleClassSelectTimer", ClassSelectTimerPacket.class).invoke(null, this);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public int getTimeRemaining() {
        return this.timeRemaining;
    }

    public int getOpponentTimeRemaining() {
        return this.opponentTimeRemaining;
    }

    public String getSelectedFactionId() {
        return this.selectedFactionId;
    }

    public boolean isCommander() {
        return this.isCommander;
    }
}

