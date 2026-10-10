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

public class GameStateResponsePacket {
    private final String phaseName;
    private final String playerTeam;
    private final String playerFaction;
    private final String activeTeam;
    private final int timeRemaining;
    private final String mapFolder;
    private final String objectiveMode;

    public GameStateResponsePacket(String phaseName, String playerTeam, String playerFaction, String activeTeam, int timeRemaining) {
        this(phaseName, playerTeam, playerFaction, activeTeam, timeRemaining, "");
    }

    public GameStateResponsePacket(String phaseName, String playerTeam, String playerFaction, String activeTeam, int timeRemaining, String mapFolder) {
        this(phaseName, playerTeam, playerFaction, activeTeam, timeRemaining, mapFolder, "");
    }

    public GameStateResponsePacket(String phaseName, String playerTeam, String playerFaction, String activeTeam, int timeRemaining, String mapFolder, String objectiveMode) {
        this.phaseName = phaseName;
        this.playerTeam = playerTeam;
        this.playerFaction = playerFaction;
        this.activeTeam = activeTeam;
        this.timeRemaining = timeRemaining;
        this.mapFolder = mapFolder == null ? "" : mapFolder;
        this.objectiveMode = objectiveMode == null ? "" : objectiveMode;
    }

    public static GameStateResponsePacket read(FriendlyByteBuf buf) {
        return new GameStateResponsePacket(buf.m_130277_(), buf.m_130277_(), buf.m_130277_(), buf.m_130277_(), buf.m_130242_(), buf.m_130277_(), buf.m_130277_());
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.phaseName);
        buf.m_130070_(this.playerTeam != null ? this.playerTeam : "");
        buf.m_130070_(this.playerFaction != null ? this.playerFaction : "");
        buf.m_130070_(this.activeTeam != null ? this.activeTeam : "");
        buf.m_130130_(this.timeRemaining);
        buf.m_130070_(this.mapFolder);
        buf.m_130070_(this.objectiveMode);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleGameStateResponse", GameStateResponsePacket.class).invoke(null, this);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public String getPhaseName() {
        return this.phaseName;
    }

    public String getPlayerTeam() {
        return this.playerTeam;
    }

    public String getPlayerFaction() {
        return this.playerFaction;
    }

    public String getActiveTeam() {
        return this.activeTeam;
    }

    public int getTimeRemaining() {
        return this.timeRemaining;
    }

    public String getMapFolder() {
        return this.mapFolder;
    }

    public String getObjectiveMode() {
        return this.objectiveMode;
    }
}

