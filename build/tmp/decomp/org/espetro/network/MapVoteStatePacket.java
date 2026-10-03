/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.team.MapVoteManager;

public class MapVoteStatePacket {
    public final boolean active;
    public final int remainingSeconds;
    public final long endGameTime;
    public final List<Candidate> candidates;
    public final Map<String, Integer> tally;
    public final String myVoteMapFolder;
    public final String winnerMapFolder;
    public final String winnerDisplayName;

    public MapVoteStatePacket(boolean active, int remainingSeconds, long endGameTime, List<Candidate> candidates, Map<String, Integer> tally, String myVoteMapFolder, String winnerMapFolder, String winnerDisplayName) {
        this.active = active;
        this.remainingSeconds = remainingSeconds;
        this.endGameTime = endGameTime;
        this.candidates = candidates != null ? candidates : List.of();
        this.tally = tally != null ? tally : Map.of();
        this.myVoteMapFolder = myVoteMapFolder;
        this.winnerMapFolder = winnerMapFolder;
        this.winnerDisplayName = winnerDisplayName;
    }

    public static MapVoteStatePacket from(MapVoteManager mgr, ServerPlayer viewer) {
        ArrayList<Candidate> list = new ArrayList<Candidate>();
        for (ActiveMapConfig c : mgr.getCandidates()) {
            list.add(new Candidate(c.mapFolder, c.displayName, c.dimensionId.toString()));
        }
        String my = viewer != null ? mgr.getPlayerVote(viewer.m_20148_()) : null;
        String winFolder = mgr.getWinner() != null ? mgr.getWinner().mapFolder : null;
        String winName = mgr.getWinner() != null ? mgr.getWinner().displayName : null;
        long end = 0L;
        if (viewer != null && viewer.f_8924_ != null) {
            end = mgr.getEndGameTime(viewer.f_8924_);
        }
        return new MapVoteStatePacket(mgr.isActive(), mgr.getRemainingSeconds(), end, list, mgr.getTally(), my, winFolder, winName);
    }

    public static MapVoteStatePacket read(FriendlyByteBuf buf) {
        boolean active = buf.readBoolean();
        int remaining = buf.m_130242_();
        long end = buf.readLong();
        int n = buf.m_130242_();
        ArrayList<Candidate> candidates = new ArrayList<Candidate>(n);
        for (int i = 0; i < n; ++i) {
            String folder = buf.m_130277_();
            String name = buf.m_130277_();
            String dimId = buf.m_130277_();
            candidates.add(new Candidate(folder, name, dimId));
        }
        int m = buf.m_130242_();
        LinkedHashMap<String, Integer> tally = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < m; ++i) {
            tally.put(buf.m_130277_(), buf.m_130242_());
        }
        String my = buf.readBoolean() ? buf.m_130277_() : null;
        String winF = buf.readBoolean() ? buf.m_130277_() : null;
        String winN = buf.readBoolean() ? buf.m_130277_() : null;
        return new MapVoteStatePacket(active, remaining, end, candidates, tally, my, winF, winN);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.active);
        buf.m_130130_(this.remainingSeconds);
        buf.writeLong(this.endGameTime);
        buf.m_130130_(this.candidates.size());
        for (Candidate candidate : this.candidates) {
            buf.m_130070_(candidate.mapFolder);
            buf.m_130070_(candidate.displayName);
            buf.m_130070_(candidate.dimensionId);
        }
        buf.m_130130_(this.tally.size());
        for (Map.Entry entry : this.tally.entrySet()) {
            buf.m_130070_((String)entry.getKey());
            buf.m_130130_((Integer)entry.getValue());
        }
        buf.writeBoolean(this.myVoteMapFolder != null);
        if (this.myVoteMapFolder != null) {
            buf.m_130070_(this.myVoteMapFolder);
        }
        buf.writeBoolean(this.winnerMapFolder != null);
        if (this.winnerMapFolder != null) {
            buf.m_130070_(this.winnerMapFolder);
        }
        buf.writeBoolean(this.winnerDisplayName != null);
        if (this.winnerDisplayName != null) {
            buf.m_130070_(this.winnerDisplayName);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleMapVoteState", MapVoteStatePacket.class).invoke(null, this);
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static final class Candidate {
        public final String mapFolder;
        public final String displayName;
        public final String dimensionId;

        public Candidate(String mapFolder, String displayName, String dimensionId) {
            this.mapFolder = mapFolder;
            this.displayName = displayName;
            this.dimensionId = dimensionId;
        }
    }
}

