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
import org.espetro.stats.PlayerMatchStatsManager;
import org.espetro.team.SquadManager;

public class MatchStatsSyncPacket {
    public final List<Row> rows;

    public MatchStatsSyncPacket(List<Row> rows) {
        this.rows = rows != null ? rows : List.of();
    }

    public static MatchStatsSyncPacket from(PlayerMatchStatsManager mgr) {
        ArrayList<Row> rows = new ArrayList<Row>();
        for (PlayerMatchStatsManager.PlayerMatchStats s : mgr.snapshot()) {
            String team = s.team != null ? s.team : s.lastTeam;
            int squadId = SquadManager.getInstance().getPlayerSquadId(s.uuid);
            String squadName = "";
            if (team != null && squadId != -1) {
                for (SquadManager.SquadSnapshot snap : SquadManager.getInstance().getSquadSnapshots(team)) {
                    if (snap.id != squadId) continue;
                    squadName = snap.name;
                    break;
                }
            }
            rows.add(new Row(s.uuid, s.name, team, s.kills, s.deaths, s.classId, s.classIcon, s.classIconImage, s.online, squadId, squadName));
        }
        return new MatchStatsSyncPacket(rows);
    }

    public static MatchStatsSyncPacket read(FriendlyByteBuf buf) {
        int n = buf.m_130242_();
        ArrayList<Row> rows = new ArrayList<Row>(n);
        for (int i = 0; i < n; ++i) {
            rows.add(new Row(buf.m_130259_(), buf.m_130277_(), buf.readBoolean() ? buf.m_130277_() : null, buf.m_130242_(), buf.m_130242_(), buf.readBoolean() ? buf.m_130277_() : null, buf.readBoolean() ? buf.m_130277_() : null, buf.readBoolean() ? buf.m_130277_() : null, buf.readBoolean(), buf.m_130242_(), buf.m_130277_()));
        }
        return new MatchStatsSyncPacket(rows);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130130_(this.rows.size());
        for (Row r : this.rows) {
            buf.m_130077_(r.uuid);
            buf.m_130070_(r.name != null ? r.name : "");
            buf.writeBoolean(r.team != null);
            if (r.team != null) {
                buf.m_130070_(r.team);
            }
            buf.m_130130_(r.kills);
            buf.m_130130_(r.deaths);
            buf.writeBoolean(r.classId != null);
            if (r.classId != null) {
                buf.m_130070_(r.classId);
            }
            buf.writeBoolean(r.classIcon != null);
            if (r.classIcon != null) {
                buf.m_130070_(r.classIcon);
            }
            buf.writeBoolean(r.classIconImage != null && !r.classIconImage.isBlank());
            if (r.classIconImage != null && !r.classIconImage.isBlank()) {
                buf.m_130070_(r.classIconImage);
            }
            buf.writeBoolean(r.online);
            buf.m_130130_(r.squadId);
            buf.m_130070_(r.squadName != null ? r.squadName : "");
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleMatchStats", MatchStatsSyncPacket.class).invoke(null, this);
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static final class Row {
        public final UUID uuid;
        public final String name;
        public final String team;
        public final int kills;
        public final int deaths;
        public final String classId;
        public final String classIcon;
        public final String classIconImage;
        public final boolean online;
        public final int squadId;
        public final String squadName;

        public Row(UUID uuid, String name, String team, int kills, int deaths, String classId, String classIcon, boolean online, int squadId, String squadName) {
            this(uuid, name, team, kills, deaths, classId, classIcon, null, online, squadId, squadName);
        }

        public Row(UUID uuid, String name, String team, int kills, int deaths, String classId, String classIcon, String classIconImage, boolean online, int squadId, String squadName) {
            this.uuid = uuid;
            this.name = name;
            this.team = team;
            this.kills = kills;
            this.deaths = deaths;
            this.classId = classId;
            this.classIcon = classIcon;
            this.classIconImage = classIconImage;
            this.online = online;
            this.squadId = squadId;
            this.squadName = squadName;
        }
    }
}

