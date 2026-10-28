package org.espetro.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.GuiElement;

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
    private final long closesAt;
    /** 赢方 show_name，平局时为 null */
    private final String winnerShowName;
    /** 输方 show_name，平局时为 null */
    private final String loserShowName;
    private final int attackTickets;
    private final int defendTickets;
    private final int resultLevel;     // 5→0
    private final boolean attackerTimeout;
    private EspetroAuiWidgets.Text countdownText;

    public RoundEndScreen(String winner, int displaySeconds,
                          String winnerShowName, String loserShowName,
                          int attackTickets, int defendTickets,
                          int resultLevel, boolean attackerTimeout) {
        super(Component.literal("回合结束"));
        this.winner = winner == null ? "DRAW" : winner;
        this.closesAt = System.currentTimeMillis() + Math.max(1, displaySeconds) * 1000L;
        this.winnerShowName = winnerShowName;
        this.loserShowName = loserShowName;
        this.attackTickets = attackTickets;
        this.defendTickets = defendTickets;
        this.resultLevel = Math.max(0, Math.min(5, resultLevel));
        this.attackerTimeout = attackerTimeout;
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        // 倒计时文字：大号数值 + 阴影
        countdownText = EspetroAuiWidgets.centeredText(
            0, height - 30, width,
            countdownLabel(), UiTheme.TEXT_SECOND)
            .setTextScale(UiTheme.TYPE_HEADING)
            .setShadow(true);
        root.addChild(countdownText);
    }

    private String countdownLabel() {
        long seconds = Math.max(0, (closesAt - System.currentTimeMillis() + 999) / 1000);
        return seconds + " 秒后返回主城";
    }

    @Override
    public void tick() {
        super.tick();
        if (countdownText != null) countdownText.setText(countdownLabel());
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public boolean shouldCloseOnEsc() { return false; }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        CurrentMapBackgroundRenderer.render(
            graphics, width, height, ClientGameState.getCurrentMapFolder());
    }

    @Override
    protected void renderAfterMenu(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        String myTeam = ClientGameState.getPlayerTeam();
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
        int resultY = Math.max(24, (int) (height * 0.12f));
        UiPaint.label(g, "END / ROUND REPORT", resultX + UiTheme.GAP_M, resultY,
            UiTheme.TEXT_THIRD);
        UiPaint.hairline(g, resultX, resultY + 14, resultW, UiTheme.HAIRLINE_STRONG);
        UiPaint.accentBarBottom(g, resultX, resultY + 14, Math.min(48, resultW), UiTheme.ACCENT);
        UiPaint.textCentered(g, levelText, 0, (int) (height * 0.22f), width,
            levelColor, 3.5f, true);

        // ── 2. "XXX赢得了胜利"（小字）──
        if (!isDraw) {
            String victorName = winnerShowName != null ? winnerShowName : (iWon ? "我方" : "敌方");
            UiPaint.textCentered(g, victorName + "赢得了胜利", 0, (int) (height * 0.32f), width,
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
        int bottomY = (int) (height * 0.48f);
        int leftX = width / 4;
        int rightX = width * 3 / 4;
        UiPaint.hairlineV(g, width / 2, bottomY - 8, 74, UiTheme.HAIRLINE_STRONG);
        UiPaint.label(g, "TICKETS REMAINING", resultX + UiTheme.GAP_M, bottomY + 79,
            UiTheme.TEXT_THIRD);
        UiPaint.hairline(g, resultX, bottomY + 73, resultW, UiTheme.HAIRLINE);

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
