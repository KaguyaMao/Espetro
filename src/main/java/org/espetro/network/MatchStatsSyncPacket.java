package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.governance.CommanderGovernanceManager;
import org.espetro.stats.PlayerMatchStatsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/** S→C match scoreboard snapshot. */
public class MatchStatsSyncPacket {

    public static final class Row {
        public final UUID uuid;
        public final String name;
        public final String team;
        public final int kills;
        public final int deaths;
        public final String classId;
        /** Jar role slug (e.g. rifleman). */
        public final String classIcon;
        /** Absolute IconImage path; may be null. */
        public final String classIconImage;
        public final boolean online;
        public final int squadId;
        public final String squadName;

        public Row(UUID uuid, String name, String team, int kills, int deaths,
                   String classId, String classIcon, boolean online, int squadId, String squadName) {
            this(uuid, name, team, kills, deaths, classId, classIcon, null, online, squadId, squadName);
        }

        public Row(UUID uuid, String name, String team, int kills, int deaths,
                   String classId, String classIcon, String classIconImage,
                   boolean online, int squadId, String squadName) {
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

    public final List<Row> rows;
    private final Map<UUID, Integer> squadNumbers;

    public MatchStatsSyncPacket(List<Row> rows) {
        this(rows, Map.of());
    }

    private MatchStatsSyncPacket(List<Row> rows, Map<UUID, Integer> squadNumbers) {
        this.rows = rows != null ? rows : List.of();
        this.squadNumbers = Map.copyOf(squadNumbers);
    }

    public Integer squadNumber(UUID uuid) {
        return squadNumbers.get(uuid);
    }

    public static MatchStatsSyncPacket from(PlayerMatchStatsManager mgr) {
        List<Row> rows = new ArrayList<>();
        Map<UUID, Integer> squadNumbers = new HashMap<>();
        for (var s : mgr.snapshot()) {
            String team = s.team != null ? s.team : s.lastTeam;
            int squadId = org.espetro.team.SquadManager.getInstance().getPlayerSquadId(s.uuid);
            String squadName = "";
            if (team != null && squadId != org.espetro.team.SquadManager.NO_SQUAD) {
                for (var snap : org.espetro.team.SquadManager.getInstance().getSquadSnapshots(team)) {
                    if (snap.id == squadId) {
                        squadName = snap.name;
                        squadNumbers.put(s.uuid, snap.displayId);
                        break;
                    }
                }
            }
            rows.add(new Row(s.uuid, s.name, team, s.kills, s.deaths,
                s.classId, s.classIcon, s.classIconImage,
                s.online, squadId, squadName));
        }
        return new MatchStatsSyncPacket(rows, squadNumbers);
    }

    public static MatchStatsSyncPacket read(FriendlyByteBuf buf) {
        int n = buf.readVarInt();
        List<Row> rows = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            rows.add(new Row(
                buf.readUUID(),
                buf.readUtf(),
                buf.readBoolean() ? buf.readUtf() : null,
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readBoolean() ? buf.readUtf() : null,
                buf.readBoolean() ? buf.readUtf() : null,
                buf.readBoolean() ? buf.readUtf() : null,
                buf.readBoolean(),
                buf.readVarInt(),
                buf.readUtf()
            ));
        }
        // Optional tail, indexed by the unchanged row order, for mixed client/server versions.
        Map<UUID, Integer> squadNumbers = new HashMap<>();
        for (Row row : rows) {
            if (!buf.isReadable()) break;
            int number = buf.readVarInt();
            if (number > 0) squadNumbers.put(row.uuid, number);
        }
        return new MatchStatsSyncPacket(rows, squadNumbers);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(rows.size());
        for (Row r : rows) {
            buf.writeUUID(r.uuid);
            buf.writeUtf(r.name != null ? r.name : "");
            buf.writeBoolean(r.team != null);
            if (r.team != null) buf.writeUtf(r.team);
            buf.writeVarInt(r.kills);
            buf.writeVarInt(r.deaths);
            buf.writeBoolean(r.classId != null);
            if (r.classId != null) buf.writeUtf(r.classId);
            buf.writeBoolean(r.classIcon != null);
            if (r.classIcon != null) buf.writeUtf(r.classIcon);
            buf.writeBoolean(r.classIconImage != null && !r.classIconImage.isBlank());
            if (r.classIconImage != null && !r.classIconImage.isBlank()) {
                buf.writeUtf(r.classIconImage);
            }
            buf.writeBoolean(r.online);
            buf.writeVarInt(r.squadId);
            buf.writeUtf(r.squadName != null ? r.squadName : "");
        }
        if (!squadNumbers.isEmpty()) {
            for (Row row : rows) buf.writeVarInt(squadNumbers.getOrDefault(row.uuid, 0));
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers")
                    .getMethod("handleMatchStats", MatchStatsSyncPacket.class)
                    .invoke(null, this);
            } catch (Exception ignored) {
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
