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

public class ClassSelectScreenPacket {
    private final String team;
    private final boolean isCommander;
    private final List<FactionInfo> factions;
    private final int timeRemaining;
    private final String opponentTeamName;
    private final String opponentFaction;
    private final int opponentTimeRemaining;
    private final String selectedFactionId;

    public ClassSelectScreenPacket(String team, boolean isCommander, List<FactionInfo> factions, int timeRemaining, String opponentTeamName, String opponentFaction, int opponentTimeRemaining, String selectedFactionId) {
        this.team = team;
        this.isCommander = isCommander;
        this.factions = factions;
        this.timeRemaining = timeRemaining;
        this.opponentTeamName = opponentTeamName;
        this.opponentFaction = opponentFaction;
        this.opponentTimeRemaining = opponentTimeRemaining;
        this.selectedFactionId = selectedFactionId != null ? selectedFactionId : "";
    }

    public String getTeam() {
        return this.team;
    }

    public boolean isCommander() {
        return this.isCommander;
    }

    public List<FactionInfo> getFactions() {
        return this.factions;
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

    public String getSelectedFactionId() {
        return this.selectedFactionId;
    }

    public static ClassSelectScreenPacket read(FriendlyByteBuf buf) {
        String team = buf.m_130277_();
        boolean isCommander = buf.readBoolean();
        int timeRemaining = buf.m_130242_();
        int count = buf.m_130242_();
        ArrayList<FactionInfo> factions = new ArrayList<FactionInfo>(count);
        for (int i = 0; i < count; ++i) {
            String id = buf.m_130277_();
            String name = buf.m_130277_();
            String selectionImage = buf.m_130277_();
            int voteCount = buf.m_130242_();
            byte[] imageData = buf.readBoolean() ? buf.m_130052_() : null;
            factions.add(new FactionInfo(id, name, selectionImage, voteCount, imageData));
        }
        String opponentTeamName = buf.m_130277_();
        String opponentFaction = buf.m_130277_();
        int opponentTimeRemaining = buf.readInt();
        String selectedFactionId = buf.m_130277_();
        return new ClassSelectScreenPacket(team, isCommander, factions, timeRemaining, opponentTeamName, opponentFaction, opponentTimeRemaining, selectedFactionId);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.team);
        buf.writeBoolean(this.isCommander);
        buf.m_130130_(this.timeRemaining);
        buf.m_130130_(this.factions.size());
        for (FactionInfo info : this.factions) {
            buf.m_130070_(info.id);
            buf.m_130070_(info.name);
            buf.m_130070_(info.selectionImage);
            buf.m_130130_(info.voteCount);
            boolean hasImage = info.imageData != null;
            buf.writeBoolean(hasImage);
            if (!hasImage) continue;
            buf.m_130087_(info.imageData);
        }
        buf.m_130070_(this.opponentTeamName != null ? this.opponentTeamName : "");
        buf.m_130070_(this.opponentFaction != null ? this.opponentFaction : "");
        buf.writeInt(this.opponentTimeRemaining);
        buf.m_130070_(this.selectedFactionId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleClassSelectScreen", ClassSelectScreenPacket.class).invoke(null, this);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static class FactionInfo {
        public final String id;
        public final String name;
        public final String selectionImage;
        public final int voteCount;
        public final byte[] imageData;

        public FactionInfo(String id, String name, String selectionImage, int voteCount) {
            this(id, name, selectionImage, voteCount, null);
        }

        public FactionInfo(String id, String name, String selectionImage, int voteCount, byte[] imageData) {
            this.id = id;
            this.name = name;
            this.selectionImage = selectionImage != null ? selectionImage : "";
            this.voteCount = voteCount;
            this.imageData = imageData;
        }
    }
}

