/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.Espetro;
import org.espetro.governance.CommanderGovernanceManager;

public class GovernanceStatePacket {
    public final List<TeamState> teams;

    public GovernanceStatePacket(List<TeamState> teams) {
        this.teams = teams != null ? teams : List.of();
    }

    public static GovernanceStatePacket from(CommanderGovernanceManager mgr, UUID viewer) {
        ArrayList<TeamState> list = new ArrayList<TeamState>();
        long now = 0L;
        try {
            MinecraftServer server = Espetro.getServer();
            if (server != null) {
                now = server.m_129783_().m_46467_();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        for (String team : List.of("ATTACK", "DEFEND")) {
            CommanderGovernanceManager.TeamGovernance g = mgr.getTeam(team);
            HashMap<String, Integer> counts = new HashMap<String, Integer>();
            if (g.state == CommanderGovernanceManager.State.IMPEACHMENT_VOTE) {
                if (g.commander != null) {
                    counts.putIfAbsent(g.commander.toString(), 0);
                }
                if (g.challenger != null) {
                    counts.putIfAbsent(g.challenger.toString(), 0);
                }
            } else if (g.state == CommanderGovernanceManager.State.VACANCY_VOTE) {
                for (UUID v : g.volunteers) {
                    counts.putIfAbsent(v.toString(), 0);
                }
            }
            for (UUID c : g.votes.values()) {
                counts.merge(c.toString(), 1, Integer::sum);
            }
            int remaining = 0;
            if (g.state != CommanderGovernanceManager.State.IDLE) {
                remaining = g.endGameTime > 0L && now > 0L ? (int)Math.max(0L, (g.endGameTime - now + 19L) / 20L) : Math.max(0, g.timeoutSeconds - g.tickCounter / 20);
            }
            UUID myVote = viewer != null ? g.votes.get(viewer) : null;
            list.add(new TeamState(team, g.state.name(), g.commander, g.challenger, remaining, g.endGameTime, counts, new ArrayList<UUID>(g.volunteers), myVote));
        }
        return new GovernanceStatePacket(list);
    }

    public static GovernanceStatePacket read(FriendlyByteBuf buf) {
        int n = buf.m_130242_();
        ArrayList<TeamState> teams = new ArrayList<TeamState>(n);
        for (int i = 0; i < n; ++i) {
            String team = buf.m_130277_();
            String state = buf.m_130277_();
            UUID commander = buf.readBoolean() ? buf.m_130259_() : null;
            UUID challenger = buf.readBoolean() ? buf.m_130259_() : null;
            int remaining = buf.m_130242_();
            long end = buf.readLong();
            int m = buf.m_130242_();
            HashMap<String, Integer> counts = new HashMap<String, Integer>();
            for (int j = 0; j < m; ++j) {
                counts.put(buf.m_130277_(), buf.m_130242_());
            }
            int v = buf.m_130242_();
            ArrayList<UUID> vols = new ArrayList<UUID>(v);
            for (int j = 0; j < v; ++j) {
                vols.add(buf.m_130259_());
            }
            UUID myVote = buf.readBoolean() ? buf.m_130259_() : null;
            teams.add(new TeamState(team, state, commander, challenger, remaining, end, counts, vols, myVote));
        }
        return new GovernanceStatePacket(teams);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130130_(this.teams.size());
        for (TeamState t : this.teams) {
            buf.m_130070_(t.team);
            buf.m_130070_(t.state);
            buf.writeBoolean(t.commander != null);
            if (t.commander != null) {
                buf.m_130077_(t.commander);
            }
            buf.writeBoolean(t.challenger != null);
            if (t.challenger != null) {
                buf.m_130077_(t.challenger);
            }
            buf.m_130130_(t.remainingSeconds);
            buf.writeLong(t.endGameTime);
            buf.m_130130_(t.voteCounts.size());
            for (Map.Entry<String, Integer> e : t.voteCounts.entrySet()) {
                buf.m_130070_(e.getKey());
                buf.m_130130_(e.getValue());
            }
            buf.m_130130_(t.volunteers.size());
            for (UUID u : t.volunteers) {
                buf.m_130077_(u);
            }
            buf.writeBoolean(t.myVote != null);
            if (t.myVote == null) continue;
            buf.m_130077_(t.myVote);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleGovernanceState", GovernanceStatePacket.class).invoke(null, this);
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static final class TeamState {
        public final String team;
        public final String state;
        public final UUID commander;
        public final UUID challenger;
        public final int remainingSeconds;
        public final long endGameTime;
        public final Map<String, Integer> voteCounts;
        public final List<UUID> volunteers;
        public final UUID myVote;

        public TeamState(String team, String state, UUID commander, UUID challenger, int remainingSeconds, long endGameTime, Map<String, Integer> voteCounts, List<UUID> volunteers, UUID myVote) {
            this.team = team;
            this.state = state;
            this.commander = commander;
            this.challenger = challenger;
            this.remainingSeconds = remainingSeconds;
            this.endGameTime = endGameTime;
            this.voteCounts = voteCounts != null ? voteCounts : Map.of();
            this.volunteers = volunteers != null ? volunteers : List.of();
            this.myVote = myVote;
        }
    }
}

