package org.espetro.team;

import org.espetro.Espetro;
import org.espetro.network.RoundReport;
import org.espetro.stats.PlayerMatchStatsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/** Confined to the server thread. Only actual changes to the authoritative scoreboard are recorded. */
public final class RoundReportRecorder {
    private final List<RoundReport.Ticket> tickets = new ArrayList<>();
    private int startTick;
    private int initialAttack;
    private int initialDefend;
    private boolean active;

    public void begin(int attack, int defend) {
        tickets.clear();
        startTick = now();
        initialAttack = attack;
        initialDefend = defend;
        active = true;
        org.espetro.stats.RoundScoring.begin();
    }

    public void record(boolean attack, int before, int after, RoundReport.Reason reason) {
        if (active && before != after && GameStateManager.getInstance().getCurrentPhase() == GamePhase.BATTLE) {
            tickets.add(new RoundReport.Ticket(Math.max(0, now() - startTick), attack, before, after, reason));
        }
    }

    public void reset() {
        active = false;
        tickets.clear();
        org.espetro.stats.RoundScoring.reset();
    }

    public RoundReport finish(int attack, int defend) {
        record Membership(SquadManager.SquadSnapshot squad, SquadManager.MemberSnapshot member) {}
        var memberships = new HashMap<UUID, Membership>();
        for (String team : List.of("ATTACK", "DEFEND")) {
            for (var squad : SquadManager.getInstance().getSquadSnapshots(team)) {
                for (var member : squad.members) memberships.put(member.uuid, new Membership(squad, member));
            }
        }
        var rows = new ArrayList<RoundReport.Player>();
        var loader = FactionDataProvider.getOrCreateLoader();
        for (var stat : PlayerMatchStatsManager.getInstance().snapshot()) {
            String team = stat.team == null ? stat.lastTeam : stat.team;
            if (!"ATTACK".equals(team) && !"DEFEND".equals(team)) continue;
            var membership = memberships.get(stat.uuid);
            var member = membership == null ? null : membership.member();
            var kit = stat.classId == null ? null : loader.getClassKit(stat.classId);
            String role = member == null ? (kit == null ? "" : safe(kit.name)) : safe(member.className);
            rows.add(new RoundReport.Player(stat.uuid, stat.name, "ATTACK".equals(team),
                membership == null ? 0 : membership.squad().displayId,
                membership == null ? "" : safe(membership.squad().name),
                member == null ? (byte) -1 : member.fireteam.toNetwork(),
                member != null && member.leader, member != null && member.fireteamLeader, role,
                safe(stat.classIcon), safe(stat.classIconImage), safe(stat.classId), org.espetro.stats.RoundScoring.score(stat.uuid)));
        }
        List<RoundReport.Ticket> visibleTickets = tickets;
        if (org.espetro.stats.RoundScoreConfig.hidden()) {
            visibleTickets = tickets.stream().map(ticket -> switch (ticket.reason().cause()) {
                case DEATH, VEHICLE, FOB -> new RoundReport.Ticket(ticket.tick(), ticket.attack(),
                    ticket.before(), ticket.after(), RoundReport.Reason.OTHER);
                default -> ticket;
            }).toList();
        }
        var report = new RoundReport(active ? Math.max(0, now() - startTick) : 0,
            active ? initialAttack : attack, active ? initialDefend : defend, visibleTickets, rows);
        active = false;
        return report;
    }

    private static String safe(String value) { return value == null ? "" : value; }

    private static int now() {
        var server = Espetro.getServer();
        return server == null ? 0 : server.getTickCount();
    }
}
