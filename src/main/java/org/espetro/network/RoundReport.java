package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;

import java.util.List;
import java.util.UUID;

/** Frozen server snapshot shared by the settlement screens and UI preview. */
public record RoundReport(int duration, int initialAttack, int initialDefend,
                          List<Ticket> tickets, List<Player> players) {
    public static final int DISPLAY_SECONDS = 30;
    public static final RoundReport EMPTY = new RoundReport(0, 0, 0, List.of(), List.of());

    public RoundReport {
        tickets = List.copyOf(tickets);
        players = List.copyOf(players);
    }

    public enum Cause { OTHER, DEATH, VEHICLE, FOB, CAPTURE }

    public record Reason(Cause cause, String label, String icon, String squad, List<String> occupants) {
        public static final Reason OTHER = new Reason(Cause.OTHER, "票数调整", "", "", List.of());
        public Reason { occupants = List.copyOf(occupants); }
        public static Reason of(Cause cause, String label) {
            return new Reason(cause, label, "", "", List.of());
        }
    }

    public record Ticket(int tick, boolean attack, int before, int after, Reason reason) {}
    public record Player(UUID id, String name, boolean attack, int squad, String squadName,
                         byte fireteam, boolean leader, boolean fireteamLeader, String role,
                         String icon, String iconImage, String classId, int kills, int deaths) {}

    void write(FriendlyByteBuf buf) {
        buf.writeVarInt(duration).writeVarInt(initialAttack).writeVarInt(initialDefend);
        buf.writeCollection(tickets, (b, t) -> {
            b.writeVarInt(t.tick());
            b.writeBoolean(t.attack());
            b.writeVarInt(t.before()).writeVarInt(t.after());
            var r = t.reason();
            b.writeEnum(r.cause()).writeUtf(r.label()).writeUtf(r.icon()).writeUtf(r.squad());
            b.writeCollection(r.occupants(), (bb, name) -> bb.writeUtf(name));
        });
        buf.writeCollection(players, (b, p) -> {
            b.writeUUID(p.id()).writeUtf(p.name());
            b.writeBoolean(p.attack());
            b.writeVarInt(p.squad()).writeUtf(p.squadName());
            b.writeByte(p.fireteam()).writeBoolean(p.leader()).writeBoolean(p.fireteamLeader());
            b.writeUtf(p.role()).writeUtf(p.icon()).writeUtf(p.iconImage()).writeUtf(p.classId())
                .writeVarInt(p.kills()).writeVarInt(p.deaths());
        });
    }

    static RoundReport read(FriendlyByteBuf buf) {
        int duration = buf.readVarInt(), attack = buf.readVarInt(), defend = buf.readVarInt();
        var tickets = buf.readList(b -> new Ticket(b.readVarInt(), b.readBoolean(), b.readVarInt(), b.readVarInt(),
            new Reason(b.readEnum(Cause.class), b.readUtf(), b.readUtf(), b.readUtf(),
                b.readList(FriendlyByteBuf::readUtf))));
        var players = buf.readList(b -> new Player(b.readUUID(), b.readUtf(), b.readBoolean(), b.readVarInt(),
            b.readUtf(), b.readByte(), b.readBoolean(), b.readBoolean(), b.readUtf(), b.readUtf(), b.readUtf(),
            b.readUtf(), b.readVarInt(), b.readVarInt()));
        return new RoundReport(duration, attack, defend, tickets, players);
    }
}
