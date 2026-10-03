/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package org.espetro.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.network.NetworkManager;
import org.espetro.team.ClassCountManager;

public class ClassCountSyncPacket {
    private String factionId;
    private Map<String, Integer> classCounts;
    private Map<String, Integer> squadClassCounts;
    private Map<String, Map<String, Integer>> variantCounts;
    private boolean isError;
    private String errorMessage;

    public ClassCountSyncPacket(String factionId) {
        this.factionId = factionId;
        this.classCounts = new HashMap<String, Integer>();
        this.squadClassCounts = new HashMap<String, Integer>();
        this.variantCounts = new HashMap<String, Map<String, Integer>>();
        this.isError = false;
        this.errorMessage = "";
    }

    public ClassCountSyncPacket(Map<String, Integer> counts, String factionId) {
        this(counts, new HashMap<String, Map<String, Integer>>(), factionId);
    }

    public ClassCountSyncPacket(Map<String, Integer> counts, Map<String, Map<String, Integer>> variantCounts, String factionId) {
        this(counts, new HashMap<String, Integer>(), variantCounts, factionId);
    }

    public ClassCountSyncPacket(Map<String, Integer> counts, Map<String, Integer> squadCounts, Map<String, Map<String, Integer>> variantCounts, String factionId) {
        this.factionId = factionId;
        this.classCounts = counts;
        this.squadClassCounts = squadCounts;
        this.variantCounts = variantCounts;
        this.isError = false;
        this.errorMessage = "";
    }

    public ClassCountSyncPacket(String message, boolean isError) {
        this.factionId = "";
        this.classCounts = new HashMap<String, Integer>();
        this.squadClassCounts = new HashMap<String, Integer>();
        this.variantCounts = new HashMap<String, Map<String, Integer>>();
        this.isError = true;
        this.errorMessage = message;
    }

    public static ClassCountSyncPacket read(FriendlyByteBuf buf) {
        String factionId = buf.m_130277_();
        boolean isError = buf.readBoolean();
        if (isError) {
            String message = buf.m_130277_();
            return new ClassCountSyncPacket(message, true);
        }
        HashMap<String, Integer> counts = new HashMap<String, Integer>();
        int size = buf.readInt();
        for (int i = 0; i < size; ++i) {
            String classId = buf.m_130277_();
            int count = buf.readInt();
            counts.put(classId, count);
        }
        HashMap<String, Integer> squadCounts = new HashMap<String, Integer>();
        int squadSize = buf.readInt();
        for (int i = 0; i < squadSize; ++i) {
            squadCounts.put(buf.m_130277_(), buf.readInt());
        }
        HashMap<String, Map<String, Integer>> variantCounts = new HashMap<String, Map<String, Integer>>();
        int classVariantSize = buf.readInt();
        for (int i = 0; i < classVariantSize; ++i) {
            String classId = buf.m_130277_();
            int variantSize = buf.readInt();
            HashMap<String, Integer> perClass = new HashMap<String, Integer>();
            for (int j = 0; j < variantSize; ++j) {
                perClass.put(buf.m_130277_(), buf.readInt());
            }
            variantCounts.put(classId, perClass);
        }
        return new ClassCountSyncPacket(counts, squadCounts, variantCounts, factionId);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.factionId);
        buf.writeBoolean(this.isError);
        if (this.isError) {
            buf.m_130070_(this.errorMessage);
        } else {
            buf.writeInt(this.classCounts.size());
            for (Map.Entry<String, Integer> entry : this.classCounts.entrySet()) {
                buf.m_130070_(entry.getKey());
                buf.writeInt(entry.getValue());
            }
            buf.writeInt(this.squadClassCounts.size());
            for (Map.Entry<String, Integer> entry : this.squadClassCounts.entrySet()) {
                buf.m_130070_(entry.getKey());
                buf.writeInt(entry.getValue());
            }
            buf.writeInt(this.variantCounts.size());
            for (Map.Entry<String, Object> entry : this.variantCounts.entrySet()) {
                buf.m_130070_(entry.getKey());
                buf.writeInt(((Map)entry.getValue()).size());
                for (Map.Entry variantEntry : ((Map)entry.getValue()).entrySet()) {
                    buf.m_130070_((String)variantEntry.getKey());
                    buf.writeInt((Integer)variantEntry.getValue());
                }
            }
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player != null) {
                ClassCountManager countManager = ClassCountManager.getInstance();
                String team = countManager.getEffectivePlayerTeam(player.m_20148_());
                Map<String, Integer> counts = countManager.getCountsForFaction(team, this.factionId);
                Map<String, Integer> squadCounts = countManager.getSquadCountsForViewer(player.m_20148_(), team, this.factionId);
                Map<String, Map<String, Integer>> variants = countManager.getVariantCountsForViewer(player.m_20148_(), team, this.factionId);
                ClassCountSyncPacket response = new ClassCountSyncPacket(counts, squadCounts, variants, this.factionId);
                NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)response);
            } else {
                try {
                    Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleClassCountSync", ClassCountSyncPacket.class).invoke(null, this);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public boolean isError() {
        return this.isError;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public Map<String, Integer> getClassCounts() {
        return this.classCounts;
    }

    public Map<String, Integer> getSquadClassCounts() {
        return this.squadClassCounts;
    }

    public Map<String, Map<String, Integer>> getVariantCounts() {
        return this.variantCounts;
    }
}

