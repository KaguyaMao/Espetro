package org.espetro.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.GuiElement;
import org.espetro.network.RoundReport;

/**
 * 战局结算界面。
 * <p>
 * 上方：超大结算等级文字 + 小字 "XXX赢得了胜利"<br>
 * 下方：左/右双方阵营名 + 剩余票数
 */
public final class RoundEndScreen extends EspetroMenuScreen {

    // ── 结算等级文案 ──
    private static final String[] WIN_LABELS  = {"平局", "惨烈胜利", "险胜", "决定性胜利", "重大胜利", "完胜"};
    private static final String[] LOSE_LABELS = {"平局", "功亏一篑", "险败", "决定性战败", "重大战败", "完败"};
    private final String winner;       // "ATTACK" / "DEFEND" / "DRAW"
    private static final String[] PAGES = {"摘要", "详情", "计分板"};
    private static final long PAGE_MILLIS = 5000;
    private final long closesAt;
    private long pageStartedAt = nowMillis();
    private int page;
    private boolean manualPage;
    private final RoundEndDetails details;
    private final RoundEndScoreboard scoreboard;
    private final java.util.Map<RoundReport.Award, java.util.List<RoundReport.Player>> honorees;
    private net.minecraft.client.gui.screens.Screen previewParent;
    private boolean preview;
    /** 赢方 show_name，平局时为 null */
    private final String winnerShowName;
    /** 输方 show_name，平局时为 null */
    private final String loserShowName;
    private final int attackTickets;
    private final int defendTickets;
    private final int resultLevel;     // 5→0

    public RoundEndScreen(String winner, int displaySeconds,
                          String winnerShowName, String loserShowName,
                          int attackTickets, int defendTickets,
                          int resultLevel, boolean attackerTimeout, RoundReport report) {
        super(Component.literal("回合结束"));
        this.winner = winner == null ? "DRAW" : winner;
        this.closesAt = nowMillis() + Math.max(1, displaySeconds) * 1000L;
        this.details = new RoundEndDetails(report);
        this.scoreboard = new RoundEndScoreboard(report.players());
        this.honorees = new java.util.EnumMap<>(RoundReport.Award.class);
        for (var award : RoundReport.Award.values()) {
            long best = report.players().stream().mapToLong(player -> player.score().points(award)).max().orElse(0);
            honorees.put(award, best <= 0 ? java.util.List.of() : report.players().stream()
                .filter(player -> player.score().points(award) == best)
                .sorted(java.util.Comparator.comparing(RoundReport.Player::name, String.CASE_INSENSITIVE_ORDER))
                .toList());
        }
        this.winnerShowName = winnerShowName;
        this.loserShowName = loserShowName;
        this.attackTickets = attackTickets;
        this.defendTickets = defendTickets;
        this.resultLevel = Math.max(0, Math.min(5, resultLevel));
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {}

    private static long nowMillis() { return System.nanoTime() / 1_000_000L; }

    RoundEndScreen preview(net.minecraft.client.gui.screens.Screen parent, int initialPage) {
        preview = true;
        previewParent = parent;
        selectPage(initialPage);
        return this;
    }

    private void selectPage(int next) {
        page = next;
        pageStartedAt = nowMillis();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        int start = navStart(), step = navStep();
        if (button == 0 && y >= height - 40 && y < height - 14 && x >= start && x < start + step * 3) {
            manualPage = true;
            selectPage((int) (x - start) / step);
            return true;
        }
        return super.mouseClicked(x, y, button);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (y >= 28 && y < height - 46) {
            if (page == 2) return scoreboard.scroll(x < width / 2, delta);
            if (page == 1 && details.scroll(delta)) return true;
        }
        return super.mouseScrolled(x, y, delta);
    }

    private int navStep() { return Math.max(1, Math.min(640, width - 24) / 3); }
    private int navStart() { return (width - navStep() * 3) / 2; }

    @Override public boolean isPauseScreen() { return false; }
    @Override public boolean shouldCloseOnEsc() { return preview; }
    @Override public void onClose() { if (preview) minecraft.setScreen(previewParent); }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        CurrentMapBackgroundRenderer.render(graphics, width, height,
            preview ? "island" : ClientGameState.getCurrentMapFolder());
    }

    @Override
    protected void renderAfterMenu(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        long now = nowMillis();
        if (!preview && !manualPage && page < PAGES.length - 1 && now < closesAt && now - pageStartedAt >= PAGE_MILLIS) {
            long steps = (now - pageStartedAt) / PAGE_MILLIS;
            int next = (int) Math.min(PAGES.length - 1, page + steps);
            pageStartedAt += (next - page) * PAGE_MILLIS;
            page = next;
        }
        int contentBottom = height - 45;
        if (page == 0) renderResult(g, mouseX, mouseY, contentBottom);
        if (page == 1) details.render(g, font, width, contentBottom, mouseX, mouseY,
            Math.min(1f, (now - pageStartedAt) / 1500f));
        if (page == 2) scoreboard.render(g, minecraft, width, contentBottom);

        int step = navStep(), start = navStart();
        int navY = height - 39;
        for (int i = 0; i < PAGES.length; i++) {
            int x = start + i * step;
            boolean hovered = mouseX >= x && mouseX < x + step && mouseY >= navY && mouseY < height - 14;
            g.drawCenteredString(font, PAGES[i], x + step / 2, navY + 3,
                i == page ? UiTheme.SIGNAL_YELLOW : hovered ? UiTheme.TEXT : UiTheme.TEXT_SECOND);
            g.fill(x + 3, navY + 17, x + step - 3, navY + 19, UiTheme.HAIRLINE_STRONG);
            if (i == page) {
                float progress = preview || manualPage ? 1f : Math.min(1f, (now - pageStartedAt) / (float) PAGE_MILLIS);
                g.fill(x + 3, navY + 17, x + 3 + Math.round((step - 6) * progress), navY + 19, UiTheme.SIGNAL_YELLOW);
            }
        }
        long seconds = preview ? RoundReport.DISPLAY_SECONDS : Math.max(0, (closesAt - now + 999) / 1000);
        g.drawCenteredString(font, seconds + " 秒后返回主城", width / 2, height - 12, UiTheme.TEXT_SECOND);
    }

    private void renderResult(GuiGraphics g, int mouseX, int mouseY, int bottom) {
        String myTeam = preview ? winner : ClientGameState.getPlayerTeam();
        boolean draw = "DRAW".equals(winner);
        boolean lost = !draw && myTeam != null && !winner.equals(myTeam);
        String grade = draw ? "平局" : (lost ? LOSE_LABELS : WIN_LABELS)[resultLevel];
        int headerY = Math.max(5, bottom / 14);
        float headingScale = Math.max(0.9f, Math.min(3f, bottom / 90f));
        UiPaint.textCentered(g, grade, 0, headerY, width, UiTheme.TEXT, headingScale, true);
        if (!draw) {
            String victor = winnerShowName != null ? winnerShowName
                : org.espetro.team.TeamDisplayNames.displayName(winner);
            UiPaint.textCentered(g, victor + "获胜", 0, headerY + Math.round(10 * headingScale) + 5,
                width, UiTheme.SIGNAL_YELLOW, UiTheme.TYPE_TITLE, false);
        }

        String attackName = "ATTACK".equals(winner) ? winnerShowName : loserShowName;
        String defendName = "DEFEND".equals(winner) ? winnerShowName : loserShowName;
        int ticketY = Math.round(bottom * 0.28f);
        int ticketW = Math.min(140, Math.max(40, width / 3));
        int ticketH = Math.max(29, bottom / 6);
        drawTickets(g, width / 2 - ticketW - 8, ticketY, ticketW, ticketH, attackName, attackTickets, true);
        drawTickets(g, width / 2 + 8, ticketY, ticketW, ticketH, defendName, defendTickets, false);

        int awardsTitle = Math.max(ticketY + ticketH + 6, Math.round(bottom * 0.53f));
        g.drawCenteredString(font, "本局嘉奖", width / 2, awardsTitle, UiTheme.TEXT);
        int slot = Math.min(150, Math.max(22, (width - 16) / 4));
        int start = (width - slot * 4) / 2;
        int cardY = awardsTitle + 14, cardH = Math.max(25, Math.min(74, bottom - cardY - 4));
        java.util.List<Component> tooltip = null;
        for (var award : RoundReport.Award.values()) {
            int x = start + award.ordinal() * slot;
            int cardW = slot - 4;
            var players = honorees.get(award);
            UiPaint.glassPanel(g, x, cardY, cardW, cardH, UiTheme.GLASS_PANEL_SOFT);
            drawFitted(g, award.title, x, cardY + 4, cardW, UiTheme.SIGNAL_YELLOW);
            if (players.isEmpty()) {
                drawFitted(g, "空缺", x, cardY + 16, cardW, UiTheme.TEXT_THIRD);
                continue;
            }
            var first = players.get(0);
            int color = RoundEndDetails.teamColor(first.attack());
            g.fill(x, cardY, x + cardW, cardY + 1, color);
            if (cardH >= 47) {
                TacticalHud.drawHead(g, minecraft, first.id(), x + (cardW - 8) / 2, cardY + 15, 1f);
            }
            int nameY = cardY + (cardH >= 47 ? 26 : 14);
            String name = first.name() + (players.size() == 1 ? "" : " 等" + players.size() + "人");
            drawFitted(g, name, x, nameY, cardW, UiTheme.TEXT);
            if (cardH >= 33) drawFitted(g, first.score().points(award) + " 分", x, nameY + 11, cardW, UiTheme.TEXT_SECOND);
            if (mouseX >= x && mouseX < x + cardW && mouseY >= cardY && mouseY < cardY + cardH) {
                tooltip = new java.util.ArrayList<>();
                tooltip.add(Component.literal(award.title + " · " + first.score().points(award) + " 分"));
                for (var player : players) tooltip.add(Component.literal(player.name()));
            }
        }
        if (tooltip != null) g.renderComponentTooltip(font, tooltip, mouseX, mouseY);
    }

    private void drawTickets(GuiGraphics g, int x, int y, int w, int h, String name, int tickets, boolean attack) {
        String team = attack ? "ATTACK" : "DEFEND";
        int color = RoundEndDetails.teamColor(attack);
        UiPaint.glassPanel(g, x, y, w, h, UiTheme.GLASS_PANEL_SOFT);
        g.fill(x, y, x + w, y + 1, color);
        drawFitted(g, name == null ? org.espetro.team.TeamDisplayNames.displayName(team) : name,
            x, y + 4, w, color);
        UiPaint.textCentered(g, Integer.toString(tickets), x, y + 15, w, UiTheme.TEXT,
            Math.min(2.2f, (h - 16) / 10f), true);
    }

    private void drawFitted(GuiGraphics g, String value, int x, int y, int w, int color) {
        float scale = Math.min(1f, (w - 4f) / Math.max(1, font.width(value)));
        UiPaint.textCentered(g, value, x, y, w, color, scale, false);
    }
}
