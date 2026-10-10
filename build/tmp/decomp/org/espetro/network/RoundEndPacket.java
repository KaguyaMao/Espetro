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

public class RoundEndPacket {
    public final String winner;
    public final int displaySeconds;
    public final String winnerShowName;
    public final String loserShowName;
    public final int attackTickets;
    public final int defendTickets;
    public final int resultLevel;
    public final boolean attackerTimeout;

    public RoundEndPacket(String winner, int displaySeconds, String winnerShowName, String loserShowName, int attackTickets, int defendTickets, int resultLevel, boolean attackerTimeout) {
        this.winner = winner == null ? "DRAW" : winner;
        this.displaySeconds = Math.max(1, displaySeconds);
        this.winnerShowName = winnerShowName == null || winnerShowName.isEmpty() ? null : winnerShowName;
        this.loserShowName = loserShowName == null || loserShowName.isEmpty() ? null : loserShowName;
        this.attackTickets = Math.max(0, attackTickets);
        this.defendTickets = Math.max(0, defendTickets);
        this.resultLevel = Math.max(0, Math.min(5, resultLevel));
        this.attackerTimeout = attackerTimeout;
    }

    public static RoundEndPacket read(FriendlyByteBuf buf) {
        return new RoundEndPacket(buf.m_130277_(), buf.m_130242_(), buf.readBoolean() ? buf.m_130277_() : null, buf.readBoolean() ? buf.m_130277_() : null, buf.m_130242_(), buf.m_130242_(), buf.m_130242_(), buf.readBoolean());
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.winner);
        buf.m_130130_(this.displaySeconds);
        buf.writeBoolean(this.winnerShowName != null);
        if (this.winnerShowName != null) {
            buf.m_130070_(this.winnerShowName);
        }
        buf.writeBoolean(this.loserShowName != null);
        if (this.loserShowName != null) {
            buf.m_130070_(this.loserShowName);
        }
        buf.m_130130_(this.attackTickets);
        buf.m_130130_(this.defendTickets);
        buf.m_130130_(this.resultLevel);
        buf.writeBoolean(this.attackerTimeout);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleRoundEnd", RoundEndPacket.class).invoke(null, this);
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

