/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class VoteDataPacket {
    private final Map<String, Integer> voteCounts;
    private final int timeRemaining;
    private final int opponentTimeRemaining;

    public VoteDataPacket(Map<String, Integer> voteCounts, int timeRemaining) {
        this(voteCounts, timeRemaining, -1);
    }

    public VoteDataPacket(Map<String, Integer> voteCounts, int timeRemaining, int opponentTimeRemaining) {
        this.voteCounts = voteCounts;
        this.timeRemaining = timeRemaining;
        this.opponentTimeRemaining = opponentTimeRemaining;
    }

    public static VoteDataPacket read(FriendlyByteBuf buf) {
        int size = buf.readInt();
        HashMap<String, Integer> voteCounts = new HashMap<String, Integer>();
        for (int i = 0; i < size; ++i) {
            String name = buf.m_130277_();
            int count = buf.readInt();
            voteCounts.put(name, count);
        }
        int timeRemaining = buf.readInt();
        int opponentTimeRemaining = buf.readInt();
        return new VoteDataPacket(voteCounts, timeRemaining, opponentTimeRemaining);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.voteCounts.size());
        for (Map.Entry<String, Integer> entry : this.voteCounts.entrySet()) {
            buf.m_130070_(entry.getKey());
            buf.writeInt(entry.getValue());
        }
        buf.writeInt(this.timeRemaining);
        buf.writeInt(this.opponentTimeRemaining);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleVoteData", VoteDataPacket.class).invoke(null, this);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public Map<String, Integer> getVoteCounts() {
        return this.voteCounts;
    }

    public int getTimeRemaining() {
        return this.timeRemaining;
    }

    public int getOpponentTimeRemaining() {
        return this.opponentTimeRemaining;
    }
}

