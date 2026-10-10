/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class CommanderVotePacket {
    private final String team;
    private final List<String> players;
    private final int timeRemaining;
    private final String opponentTeamName;
    private final String opponentFaction;
    private final int opponentTimeRemaining;

    public CommanderVotePacket(String team, List<String> players, int timeRemaining, String opponentTeamName, String opponentFaction, int opponentTimeRemaining) {
        this.team = team;
        this.players = players;
        this.timeRemaining = timeRemaining;
        this.opponentTeamName = opponentTeamName;
        this.opponentFaction = opponentFaction;
        this.opponentTimeRemaining = opponentTimeRemaining;
    }

    public static CommanderVotePacket read(FriendlyByteBuf buf) {
        String team = buf.m_130277_();
        int size = buf.readInt();
        ArrayList<String> players = new ArrayList<String>();
        for (int i = 0; i < size; ++i) {
            players.add(buf.m_130277_());
        }
        int timeRemaining = buf.readInt();
        String opponentTeamName = buf.m_130277_();
        String opponentFaction = buf.m_130277_();
        int opponentTimeRemaining = buf.readInt();
        return new CommanderVotePacket(team, players, timeRemaining, opponentTeamName, opponentFaction, opponentTimeRemaining);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.team);
        buf.writeInt(this.players.size());
        for (String name : this.players) {
            buf.m_130070_(name);
        }
        buf.writeInt(this.timeRemaining);
        buf.m_130070_(this.opponentTeamName != null ? this.opponentTeamName : "");
        buf.m_130070_(this.opponentFaction != null ? this.opponentFaction : "");
        buf.writeInt(this.opponentTimeRemaining);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleCommanderVote", CommanderVotePacket.class).invoke(null, this);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public String getTeam() {
        return this.team;
    }

    public List<String> getPlayers() {
        return this.players;
    }

    public int getTimeRemaining() {
        return this.timeRemaining;
    }

    public String getOpponentTeamName() {
        return this.opponentTeamName;
    }

    public String getOpponentFaction() {
        return this.opponentFaction;
    }

    public int getOpponentTimeRemaining() {
        return this.opponentTimeRemaining;
    }
}

