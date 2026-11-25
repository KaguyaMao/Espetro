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
    private final RoundEndDetails details;
    private final RoundEndScoreboard scoreboard;
    private net.minecraft.client.gui.screens.Screen previewParent;
    private boolean preview;
    /** 赢方 show_name，平局时为 null */
    private final String winnerShowName;
    /** 输方 show_name，平局时为 null */
    private final String loserShowName;
    private final int attackTickets;
    private final int defendTickets;
    private final int resultLevel;     // 5→0
    private final boolean attackerTimeout;

    public RoundEndScreen(String winner, int displaySeconds,
                          String winnerShowName, String loserShowName,
                          int attackTickets, int defendTickets,
                          int resultLevel, boolean attackerTimeout, RoundReport report) {
        super(Component.literal("回合结束"));
        this.winner = winner == null ? "DRAW" : winner;
        this.closesAt = nowMillis() + Math.max(1, displaySeconds) * 1000L;
        this.details = new RoundEndDetails(report);
        this.scoreboard = new RoundEndScoreboard(report.players());
        this.winnerShowName = winnerShowName;
        this.loserShowName = loserShowName;
        this.attackTickets = attackTickets;
        this.defendTickets = defendTickets;
        this.resultLevel = Math.max(0, Math.min(5, resultLevel));
        this.attackerTimeout = attackerTimeout;
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
        if (!preview && now < closesAt && now - pageStartedAt >= PAGE_MILLIS) {
            long steps = (now - pageStartedAt) / PAGE_MILLIS;
            page = (int) ((page + steps) % PAGES.length);
            pageStartedAt += steps * PAGE_MILLIS;
        }
        int contentBottom = height - 45;
        if (page == 0) renderResult(g);
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
                float progress = preview ? 1f : Math.min(1f, (now - pageStartedAt) / (float) PAGE_MILLIS);
                g.fill(x + 3, navY + 17, x + 3 + Math.round((step - 6) * progress), navY + 19, UiTheme.SIGNAL_YELLOW);
            }
        }
        long seconds = preview ? RoundReport.DISPLAY_SECONDS : Math.max(0, (closesAt - now + 999) / 1000);
        g.drawCenteredString(font, seconds + " 秒后返回主城", width / 2, height - 12, UiTheme.TEXT_SECOND);
    }

    private void renderResult(GuiGraphics g) {
        String myTeam = preview ? winner : ClientGameState.getPlayerTeam();
        boolean isDraw = "DRAW".equals(winner);
        boolean iWon = !isDraw && winner.equals(myTeam);
        int level = resultLevel;

        // ── 1. 超大等级文字（居中上方）──
        String levelText = isDraw ? "平局" : (iWon ? WIN_LABELS[level] : LOSE_LABELS[level]);
        int levelColor = isDraw ? UiTheme.GOLD : !iWon ? UiTheme.NEGATIVE : switch (level) {
            case 1, 2 -> UiTheme.WARNING;
            case 3, 4 -> UiTheme.ATTACK;
            case 5 -> UiTheme.NEGATIVE;
            default -> UiTheme.GOLD;
        };
        int resultW = Math.min(620, Math.max(1, width - UiTheme.GAP_L * 2));
        int resultX = (width - resultW) / 2;
        int resultY = Math.max(12, (int) ((height - 45) * 0.12f));

        UiPaint.hairline(g, resultX, resultY + 14, resultW, UiTheme.HAIRLINE_STRONG);
        UiPaint.textCentered(g, levelText, 0, (int) ((height - 45) * 0.23f), width,
            levelColor, height < 260 ? 2.4f : 3.5f, true);

        // ── 2. "XXX赢得了胜利"（小字）──
        if (!isDraw) {
            String victorName = winnerShowName != null ? winnerShowName : (iWon ? "我方" : "敌方");
            UiPaint.textCentered(g, victorName + "赢得了胜利", 0, (int) ((height - 45) * 0.38f), width,
                iWon ? UiTheme.POSITIVE : UiTheme.ATTACK, UiTheme.TYPE_TITLE, true);
        }

        // ── 3. 下半区：双方阵营名 + 票数 ──
        // 确定玩家视角下的左/右双方
        boolean isAttacker = "ATTACK".equals(myTeam);

        String leftFaction, rightFaction;
        int leftTickets, rightTickets;
        int leftAccent = UiTheme.ATTACK;
        int rightAccent = UiTheme.DEFEND;

        if (!isDraw && myTeam != null) {
            if (iWon) {
                // 我赢了：左=赢得方，右=输方
                leftFaction = winnerShowName != null ? winnerShowName : "我方";
                rightFaction = loserShowName != null ? loserShowName : "敌方";
                leftAccent = isAttacker ? UiTheme.ATTACK : UiTheme.DEFEND;
                rightAccent = isAttacker ? UiTheme.DEFEND : UiTheme.ATTACK;
                if (isAttacker) { leftTickets = attackTickets; rightTickets = defendTickets; }
                else            { leftTickets = defendTickets; rightTickets = attackTickets; }
            } else {
                // 我输了：左=我方，右=敌方（敌方是赢方）
                leftFaction = loserShowName != null ? loserShowName : "我方";
                rightFaction = winnerShowName != null ? winnerShowName : "敌方";
                leftAccent = isAttacker ? UiTheme.ATTACK : UiTheme.DEFEND;
                rightAccent = isAttacker ? UiTheme.DEFEND : UiTheme.ATTACK;
                if (isAttacker) { leftTickets = attackTickets; rightTickets = defendTickets; }
                else            { leftTickets = defendTickets; rightTickets = attackTickets; }
            }
        } else {
            // 平局或无队伍：按攻/守固定展示
            leftFaction = winnerShowName != null ? winnerShowName
                : org.espetro.team.TeamDisplayNames.displayName("ATTACK");
            rightFaction = loserShowName != null ? loserShowName
                : org.espetro.team.TeamDisplayNames.displayName("DEFEND");
            leftTickets = attackTickets;
            rightTickets = defendTickets;
        }

        // 下半区 Y 起始
        int bottomY = (int) ((height - 45) * 0.53f);
        int panelHeight = Math.max(32, Math.min(75, height - bottomY - 49));
        UiPaint.glassPanel(g, 8, bottomY - 6, width / 2 - 12, panelHeight, UiTheme.GLASS_PANEL);
        UiPaint.glassPanel(g, width / 2 + 4, bottomY - 6, width / 2 - 12, panelHeight, UiTheme.GLASS_PANEL);
        int leftX = width / 4;
        int rightX = width * 3 / 4;
        UiPaint.hairlineV(g, width / 2, bottomY - 5, panelHeight, UiTheme.HAIRLINE_STRONG);

        // 阵营名（中号 1.8x）+ 底部阵营色条
        float nameScale = 1.8f;
        UiPaint.textCentered(g, leftFaction, 0, bottomY, width / 2, leftAccent, nameScale, true);
        UiPaint.textCentered(g, rightFaction, width / 2, bottomY, width / 2, rightAccent,
            nameScale, true);
        UiPaint.accentBarBottom(g, leftX - 32, bottomY + 20, 64, leftAccent);
        UiPaint.accentBarBottom(g, rightX - 32, bottomY + 20, 64, rightAccent);

        // 票数（大号数值，在阵营名下方）
        int numberY = bottomY + 30;
        float numScale = 2.5f;
        UiPaint.textCentered(g, String.valueOf(leftTickets), 0, numberY, width / 2,
            UiTheme.GOLD, numScale, true);
        UiPaint.textCentered(g, String.valueOf(rightTickets), width / 2, numberY, width / 2,
            UiTheme.GOLD, numScale, true);
    }
}
