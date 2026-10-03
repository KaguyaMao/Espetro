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
import org.espetro.Espetro;
import org.espetro.network.UnifiedDeployScreenPacket;

public class SquadSyncPacket {
    private final String team;
    private final List<UnifiedDeployScreenPacket.SquadInfo> squads;
    private final int mySquadId;
    private final List<String> commanderNames;
    private final double teammateNameTagDistance;
    private final String selectedClassId;

    public SquadSyncPacket(String team, List<UnifiedDeployScreenPacket.SquadInfo> squads, int mySquadId) {
        this(team, squads, mySquadId, new ArrayList<String>(), 10.0);
    }

    public SquadSyncPacket(String team, List<UnifiedDeployScreenPacket.SquadInfo> squads, int mySquadId, List<String> commanderNames, double teammateNameTagDistance) {
        this(team, squads, mySquadId, commanderNames, teammateNameTagDistance, "");
    }

    public SquadSyncPacket(String team, List<UnifiedDeployScreenPacket.SquadInfo> squads, int mySquadId, List<String> commanderNames, double teammateNameTagDistance, String selectedClassId) {
        this.team = team == null ? "" : team;
        this.squads = squads != null ? squads : new ArrayList();
        this.mySquadId = mySquadId;
        this.commanderNames = commanderNames != null ? commanderNames : new ArrayList();
        this.teammateNameTagDistance = teammateNameTagDistance;
        this.selectedClassId = selectedClassId == null ? "" : selectedClassId;
    }

    public static SquadSyncPacket read(FriendlyByteBuf buf) {
        String team = buf.m_130277_();
        int size = buf.m_130242_();
        ArrayList<UnifiedDeployScreenPacket.SquadInfo> squads = new ArrayList<UnifiedDeployScreenPacket.SquadInfo>();
        for (int i = 0; i < size; ++i) {
            squads.add(new UnifiedDeployScreenPacket.SquadInfo(buf));
        }
        int mySquadId = buf.m_130242_();
        int commanderSize = buf.m_130242_();
        ArrayList<String> commanderNames = new ArrayList<String>();
        for (int i = 0; i < commanderSize; ++i) {
            commanderNames.add(buf.m_130277_());
        }
        double teammateNameTagDistance = buf.readDouble();
        String selectedClassId = buf.m_130277_();
        return new SquadSyncPacket(team, squads, mySquadId, commanderNames, teammateNameTagDistance, selectedClassId);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.team);
        buf.m_130130_(this.squads.size());
        for (UnifiedDeployScreenPacket.SquadInfo squad : this.squads) {
            squad.write(buf);
        }
        buf.m_130130_(this.mySquadId);
        buf.m_130130_(this.commanderNames.size());
        for (String commanderName : this.commanderNames) {
            buf.m_130070_(commanderName);
        }
        buf.writeDouble(this.teammateNameTagDistance);
        buf.m_130070_(this.selectedClassId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleSquadSync", SquadSyncPacket.class).invoke(null, this);
            }
            catch (Exception e) {
                Espetro.LOGGER.error("Failed to handle SquadSyncPacket", (Throwable)e);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public String getTeam() {
        return this.team;
    }

    public List<UnifiedDeployScreenPacket.SquadInfo> getSquads() {
        return this.squads;
    }

    public int getMySquadId() {
        return this.mySquadId;
    }

    public List<String> getCommanderNames() {
        return this.commanderNames;
    }

    public double getTeammateNameTagDistance() {
        return this.teammateNameTagDistance;
    }

    public String getSelectedClassId() {
        return this.selectedClassId;
    }
}

