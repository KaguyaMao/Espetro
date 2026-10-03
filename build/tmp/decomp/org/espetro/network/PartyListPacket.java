/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.team.PartyManager;

public class PartyListPacket {
    public final List<PartyInfo> parties;
    public final int maxPartySize;
    public final UUID myPartyId;
    public final boolean isOwner;

    public PartyListPacket(List<PartyInfo> parties, int maxPartySize, UUID myPartyId, boolean isOwner) {
        this.parties = parties != null ? parties : List.of();
        this.maxPartySize = maxPartySize;
        this.myPartyId = myPartyId;
        this.isOwner = isOwner;
    }

    public static PartyListPacket from(PartyManager pm, UUID viewerId) {
        ArrayList<PartyInfo> list = new ArrayList<PartyInfo>();
        UUID myPartyId = null;
        boolean isOwner = false;
        for (PartyManager.PartyData p : pm.getParties()) {
            boolean isViewerOwner = p.ownerId.equals(viewerId);
            boolean viewerInParty = p.members.contains(viewerId);
            if (viewerInParty) {
                myPartyId = p.partyId;
                isOwner = isViewerOwner;
            }
            list.add(new PartyInfo(p.partyId, p.ownerName, p.members.size(), p.locked, p.password != null && !p.password.isEmpty(), viewerInParty ? p.partyId : null));
        }
        return new PartyListPacket(list, PartyManager.getMaxPartySize(), myPartyId, isOwner);
    }

    public static PartyListPacket read(FriendlyByteBuf buf) {
        int maxSize = buf.m_130242_();
        int n = buf.m_130242_();
        ArrayList<PartyInfo> list = new ArrayList<PartyInfo>(n);
        for (int i = 0; i < n; ++i) {
            UUID id = buf.m_130259_();
            String owner = buf.m_130136_(32);
            int count = buf.m_130242_();
            boolean locked = buf.readBoolean();
            boolean hasPw = buf.readBoolean();
            UUID myPid = buf.readBoolean() ? buf.m_130259_() : null;
            list.add(new PartyInfo(id, owner, count, locked, hasPw, myPid));
        }
        UUID myPid = buf.readBoolean() ? buf.m_130259_() : null;
        boolean isOwner = buf.readBoolean();
        return new PartyListPacket(list, maxSize, myPid, isOwner);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130130_(this.maxPartySize);
        buf.m_130130_(this.parties.size());
        for (PartyInfo p : this.parties) {
            buf.m_130077_(p.partyId);
            buf.m_130072_(p.ownerName, 32);
            buf.m_130130_(p.memberCount);
            buf.writeBoolean(p.locked);
            buf.writeBoolean(p.hasPassword);
            buf.writeBoolean(p.myPartyId != null);
            if (p.myPartyId == null) continue;
            buf.m_130077_(p.myPartyId);
        }
        buf.writeBoolean(this.myPartyId != null);
        if (this.myPartyId != null) {
            buf.m_130077_(this.myPartyId);
        }
        buf.writeBoolean(this.isOwner);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handlePartyList", PartyListPacket.class).invoke(null, this);
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static final class PartyInfo {
        public final UUID partyId;
        public final String ownerName;
        public final int memberCount;
        public final boolean locked;
        public final boolean hasPassword;
        public final UUID myPartyId;

        public PartyInfo(UUID partyId, String ownerName, int memberCount, boolean locked, boolean hasPassword, UUID myPartyId) {
            this.partyId = partyId;
            this.ownerName = ownerName;
            this.memberCount = memberCount;
            this.locked = locked;
            this.hasPassword = hasPassword;
            this.myPartyId = myPartyId;
        }
    }
}

