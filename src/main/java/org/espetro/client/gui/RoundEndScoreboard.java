package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.espetro.network.RoundReport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class RoundEndScoreboard {
    private static final int ROW = 13;
    private static final int GROUP_GAP = 2;
    private static final int HEADER = 14;
    private static final String[] SCORE_LABELS = {"战斗", "后勤", "救助", "占领", "总分"};
    private record Row(String heading, RoundReport.Player player, boolean joinsPrevious, boolean joinsNext, int groupSize) {}

    private final List<Row> attack;
    private final List<Row> defend;
    private final Map<RoundReport.Player, ResourceLocation> icons = new HashMap<>();
    private int attackScroll;
    private int defendScroll;
    private int viewport;

    RoundEndScoreboard(List<RoundReport.Player> players) {
        attack = layout(players, true);
        defend = layout(players, false);
    }

    private static List<Row> layout(List<RoundReport.Player> players, boolean attack) {
        var rows = new ArrayList<Row>();
        var squads = new java.util.TreeMap<Integer, List<RoundReport.Player>>();
        for (var player : players) {
            if (player.attack() == attack) {
                squads.computeIfAbsent(player.squad() == 0 ? Integer.MAX_VALUE : player.squad(),
                    unused -> new ArrayList<>()).add(player);
            }
        }
        Comparator<RoundReport.Player> order = Comparator
            .comparingInt((RoundReport.Player p) -> p.leader() ? 0 : 1)
            .thenComparingInt(RoundReport.Player::fireteam)
            .thenComparingInt(p -> p.fireteamLeader() ? 0 : 1)
            .thenComparing(Comparator.comparingLong((RoundReport.Player p) -> p.score().total()).reversed())
            .thenComparing(RoundReport.Player::name, String.CASE_INSENSITIVE_ORDER);
        for (var squad : squads.values()) {
            squad.sort(order);
            var first = squad.get(0);
            rows.add(new Row(first.squad() == 0 ? "未加入小队" : first.squad() + ". " + first.squadName(),
                null, false, false, 0));
            for (int i = 0; i < squad.size(); i++) {
                var player = squad.get(i);
                boolean previous = i > 0 && squad.get(i - 1).fireteam() == player.fireteam();
                boolean next = i + 1 < squad.size() && squad.get(i + 1).fireteam() == player.fireteam();
                int count = 1;
                if (!previous) while (i + count < squad.size() && squad.get(i + count).fireteam() == player.fireteam()) count++;
                // Draw the centered group letter from the first row using its actual group length.
                rows.add(new Row("", player, previous, next, previous ? 0 : count));
            }
        }
        return List.copyOf(rows);
    }

    void render(GuiGraphics g, Minecraft mc, int width, int bottom) {
        g.drawCenteredString(mc.font, "玩家计分板", width / 2, 7, UiTheme.TEXT);
        int column = (width - 22) / 2;
        if (column < 75 || bottom < 65) return;
        viewport = bottom - 58;
        attackScroll = Math.min(attackScroll, maxScroll(attack));
        defendScroll = Math.min(defendScroll, maxScroll(defend));
        renderColumn(g, mc, 8, column, bottom, true, attack, attackScroll);
        renderColumn(g, mc, 14 + column, column, bottom, false, defend, defendScroll);
    }

    private void renderColumn(GuiGraphics g, Minecraft mc, int x, int width, int bottom,
                              boolean isAttack, List<Row> rows, int scroll) {
        var font = mc.font;
        int teamColor = RoundEndDetails.teamColor(isAttack);
        UiPaint.glassPanel(g, x, 20, width, bottom - 20, UiTheme.GLASS_PANEL_SOFT);
        int end = x + width - 5;
        int numericW = Math.max(10, Math.min(28, (width - 72) / 5));
        int statStart = end - numericW * 5;
        g.fill(x, 20, x + width, 21, teamColor);
        g.drawString(font, isAttack ? "进攻方" : "防守方", x + 5, 26, teamColor, false);
        for (int i = 0; i < SCORE_LABELS.length; i++) {
            drawStat(g, font, SCORE_LABELS[i], statStart + numericW * i, 43, numericW, i == 4);
        }
        g.enableScissor(x, 56, x + width, bottom - 2);
        try {
            int y = 56 - scroll;
            for (var row : rows) {
                var player = row.player();
                if (player == null) {
                    if (y + HEADER >= 56 && y < bottom) {
                        g.fill(x + 3, y, end, y + HEADER - 2, isAttack ? UiTheme.ATTACK_SOFT : UiTheme.DEFEND_SOFT);
                        RoundEndDetails.draw(g, font, row.heading(), x + 6, y + 2, width - 12, UiTheme.TEXT);
                    }
                    y += HEADER;
                    continue;
                }
                byte group = player.fireteam();
                int color = TacticalHud.fireteamColor(group);
                if (row.groupSize() > 0 && color != 0) {
                    g.drawString(font, String.valueOf((char) ('A' + group)), x + 5,
                        y + (ROW * row.groupSize() - font.lineHeight) / 2, color, false);
                }
                if (y + ROW >= 56 && y < bottom) {
                    TacticalHud.drawFireteamBar(g, x + 13, y, ROW, color, row.joinsPrevious(), row.joinsNext(), 1);
                    TacticalHud.drawHead(g, mc, player.id(), x + 18, y + 2, 1);
                    if (!icons.containsKey(player)) icons.put(player,
                        RoleIconResources.resolveForScoreboard(player.iconImage(), player.icon(), player.classId()));
                    TacticalHud.drawRoleIcon(g, icons.get(player), x + 29, y + 1, UiTheme.TEXT);
                    String role = player.role().isBlank() ? "" : " | " + player.role();
                    RoundEndDetails.draw(g, font, player.name() + role, x + 42, y + 2,
                        statStart - x - 44, UiTheme.TEXT);
                    var score = player.score();
                    drawStat(g, font, Long.toString(score.combat()), statStart, y + 2, numericW, false);
                    drawStat(g, font, Long.toString(score.logistics()), statStart + numericW, y + 2, numericW, false);
                    drawStat(g, font, Long.toString(score.rescue()), statStart + numericW * 2, y + 2, numericW, false);
                    drawStat(g, font, Long.toString(score.capture()), statStart + numericW * 3, y + 2, numericW, false);
                    drawStat(g, font, Long.toString(score.total()), statStart + numericW * 4, y + 2, numericW, true);
                }
                y += ROW + (row.joinsNext() ? 0 : GROUP_GAP);
            }
        } finally {
            g.disableScissor();
        }
        int max = maxScroll(rows);
        if (max > 0) {
            int handle = Math.max(5, viewport * viewport / (viewport + max));
            int y = 56 + (viewport - handle) * scroll / max;
            g.fill(x + width - 3, y, x + width - 1, y + handle, UiTheme.TEXT_THIRD);
        }
    }

    private static void drawStat(GuiGraphics g, net.minecraft.client.gui.Font font, String text,
                                 int x, int y, int width, boolean total) {
        UiPaint.textCentered(g, text, x, y, width, total ? UiTheme.SIGNAL_YELLOW : UiTheme.TEXT,
            Math.min(1f, (width - 2f) / Math.max(1, font.width(text))), false);
    }

    private int maxScroll(List<Row> rows) {
        int content = 0;
        for (var row : rows) content += row.player() == null ? HEADER : ROW + (row.joinsNext() ? 0 : GROUP_GAP);
        return Math.max(0, content - viewport);
    }

    boolean scroll(boolean isAttack, double delta) {
        int old = isAttack ? attackScroll : defendScroll;
        int next = Math.max(0, Math.min(maxScroll(isAttack ? attack : defend),
            old + (delta > 0 ? -ROW * 3 : ROW * 3)));
        if (isAttack) attackScroll = next;
        else defendScroll = next;
        return old != next;
    }
}
