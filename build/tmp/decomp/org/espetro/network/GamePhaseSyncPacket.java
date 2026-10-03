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
import org.espetro.team.GamePhase;

public class GamePhaseSyncPacket {
    private final String phaseName;
    private final String mapFolder;
    private final String objectiveMode;

    public GamePhaseSyncPacket(GamePhase phase) {
        this(phase, "");
    }

    public GamePhaseSyncPacket(GamePhase phase, String mapFolder) {
        this(phase, mapFolder, "");
    }

    public GamePhaseSyncPacket(GamePhase phase, String mapFolder, String objectiveMode) {
        this.phaseName = phase.name();
        this.mapFolder = mapFolder == null ? "" : mapFolder;
        this.objectiveMode = objectiveMode == null ? "" : objectiveMode;
    }

    public static GamePhaseSyncPacket read(FriendlyByteBuf buf) {
        String phaseName = buf.m_130277_();
        String mapFolder = buf.m_130277_();
        String objectiveMode = buf.m_130277_();
        try {
            return new GamePhaseSyncPacket(GamePhase.valueOf(phaseName), mapFolder, objectiveMode);
        }
        catch (IllegalArgumentException e) {
            return new GamePhaseSyncPacket(GamePhase.LOBBY, mapFolder, objectiveMode);
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.phaseName);
        buf.m_130070_(this.mapFolder);
        buf.m_130070_(this.objectiveMode);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        String phaseNameRef = this.phaseName;
        String mapFolderRef = this.mapFolder;
        String objectiveModeRef = this.objectiveMode;
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleGamePhase", String.class, String.class, String.class).invoke(null, phaseNameRef, mapFolderRef, objectiveModeRef);
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

    public String getMapFolder() {
        return this.mapFolder;
    }

    public String getObjectiveMode() {
        return this.objectiveMode;
    }
}

