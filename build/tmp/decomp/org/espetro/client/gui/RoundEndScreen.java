/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.CurrentMapBackgroundRenderer;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.EspetroMenuScreen;
import org.espetro.team.TeamDisplayNames;

public final class RoundEndScreen
extends EspetroMenuScreen {
    private static final String[] WIN_LABELS = new String[]{"\u5e73\u5c40", "\u60e8\u70c8\u80dc\u5229", "\u9669\u80dc", "\u51b3\u5b9a\u6027\u80dc\u5229", "\u91cd\u5927\u80dc\u5229", "\u5b8c\u80dc"};
    private static final String[] LOSE_LABELS = new String[]{"\u5e73\u5c40", "\u529f\u4e8f\u4e00\u7bd1", "\u9669\u8d25", "\u51b3\u5b9a\u6027\u6218\u8d25", "\u91cd\u5927\u6218\u8d25", "\u5b8c\u8d25"};
    private static final int[] LEVEL_COLORS = new int[]{-10496, -22016, -30720, -39424, -52480, -65536};
    private final String winner;
    private final long closesAt;
    private final String winnerShowName;
    private final String loserShowName;
    private final int attackTickets;
    private final int defendTickets;
    private final int resultLevel;
    private final boolean attackerTimeout;
    private EspetroAuiWidgets.Text countdownText;

    public RoundEndScreen(String winner, int displaySeconds, String winnerShowName, String loserShowName, int attackTickets, int defendTickets, int resultLevel, boolean attackerTimeout) {
        super(Component.m_237113_("\u56de\u5408\u7ed3\u675f"));
        this.winner = winner == null ? "DRAW" : winner;
        this.closesAt = System.currentTimeMillis() + (long)Math.max(1, displaySeconds) * 1000L;
        this.winnerShowName = winnerShowName;
        this.loserShowName = loserShowName;
        this.attackTickets = attackTickets;
        this.defendTickets = defendTickets;
        this.resultLevel = Math.max(0, Math.min(5, resultLevel));
        this.attackerTimeout = attackerTimeout;
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        this.countdownText = EspetroAuiWidgets.centeredText(0, this.f_96544_ - 28, this.f_96543_, this.countdownLabel(), -2828064);
        root.addChild(this.countdownText);
    }

    private String countdownLabel() {
        long seconds = Math.max(0L, (this.closesAt - System.currentTimeMillis() + 999L) / 1000L);
        return "\u00a77" + seconds + " \u79d2\u540e\u8fd4\u56de\u4e3b\u57ce";
    }

    @Override
    public void m_86600_() {
        super.m_86600_();
        if (this.countdownText != null) {
            this.countdownText.setText(this.countdownLabel());
        }
    }

    @Override
    public boolean m_7043_() {
        return false;
    }

    @Override
    public boolean m_6913_() {
        return false;
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        CurrentMapBackgroundRenderer.render(graphics, this.f_96543_, this.f_96544_, ClientGameState.getCurrentMapFolder());
    }

    @Override
    protected void renderAfterMenu(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int rightTickets;
        int leftTickets;
        String rightFaction;
        String leftFaction;
        String levelText;
        Minecraft mc = Minecraft.m_91087_();
        String myTeam = ClientGameState.getPlayerTeam();
        boolean isDraw = "DRAW".equals(this.winner);
        boolean iWon = !isDraw && this.winner.equals(myTeam);
        int level = this.resultLevel;
        String string = isDraw ? "\u5e73\u5c40" : (levelText = iWon ? WIN_LABELS[level] : LOSE_LABELS[level]);
        int levelColor = isDraw ? LEVEL_COLORS[0] : (iWon ? LEVEL_COLORS[level] : -52429);
        float levelScale = 3.5f;
        PoseStack pose = g.m_280168_();
        pose.m_85836_();
        pose.m_252880_((float)this.f_96543_ / 2.0f, (float)this.f_96544_ * 0.22f, 0.0f);
        pose.m_85841_(levelScale, levelScale, 1.0f);
        int lw = mc.f_91062_.m_92895_(levelText);
        g.m_280430_(mc.f_91062_, Component.m_237113_(levelText), -lw / 2, 0, levelColor);
        pose.m_85849_();
        if (!isDraw) {
            String victorName = this.winnerShowName != null ? this.winnerShowName : (iWon ? "\u6211\u65b9" : "\u654c\u65b9");
            String subText = victorName + "\u8d62\u5f97\u4e86\u80dc\u5229";
            float subScale = 1.2f;
            pose.m_85836_();
            pose.m_252880_((float)this.f_96543_ / 2.0f, (float)this.f_96544_ * 0.32f, 0.0f);
            pose.m_85841_(subScale, subScale, 1.0f);
            int sw = mc.f_91062_.m_92895_(subText);
            g.m_280430_(mc.f_91062_, Component.m_237113_(subText), -sw / 2, 0, iWon ? -86 : -21846);
            pose.m_85849_();
        }
        boolean isAttacker = "ATTACK".equals(myTeam);
        boolean isDefender = "DEFEND".equals(myTeam);
        if (!isDraw && myTeam != null) {
            if (iWon) {
                leftFaction = this.winnerShowName != null ? this.winnerShowName : "\u6211\u65b9";
                String string2 = rightFaction = this.loserShowName != null ? this.loserShowName : "\u654c\u65b9";
                if (isAttacker) {
                    leftTickets = this.attackTickets;
                    rightTickets = this.defendTickets;
                } else {
                    leftTickets = this.defendTickets;
                    rightTickets = this.attackTickets;
                }
            } else {
                leftFaction = this.loserShowName != null ? this.loserShowName : "\u6211\u65b9";
                String string3 = rightFaction = this.winnerShowName != null ? this.winnerShowName : "\u654c\u65b9";
                if (isAttacker) {
                    leftTickets = this.attackTickets;
                    rightTickets = this.defendTickets;
                } else {
                    leftTickets = this.defendTickets;
                    rightTickets = this.attackTickets;
                }
            }
        } else {
            leftFaction = this.winnerShowName != null ? this.winnerShowName : TeamDisplayNames.displayName("ATTACK");
            rightFaction = this.loserShowName != null ? this.loserShowName : TeamDisplayNames.displayName("DEFEND");
            leftTickets = this.attackTickets;
            rightTickets = this.defendTickets;
        }
        int bottomY = (int)((float)this.f_96544_ * 0.48f);
        int leftX = this.f_96543_ / 4;
        int rightX = this.f_96543_ * 3 / 4;
        float nameScale = 1.8f;
        RoundEndScreen.drawScaledCentered(g, mc.f_91062_, leftFaction, leftX, bottomY, nameScale, -1);
        RoundEndScreen.drawScaledCentered(g, mc.f_91062_, rightFaction, rightX, bottomY, nameScale, -1);
        int numberY = bottomY + 32;
        float numScale = 2.5f;
        RoundEndScreen.drawScaledCentered(g, mc.f_91062_, String.valueOf(leftTickets), leftX, numberY, numScale, -154);
        RoundEndScreen.drawScaledCentered(g, mc.f_91062_, String.valueOf(rightTickets), rightX, numberY, numScale, -154);
    }

    private static void drawScaledCentered(GuiGraphics g, Font font, String text, int centerX, int centerY, float scale, int color) {
        PoseStack pose = g.m_280168_();
        pose.m_85836_();
        pose.m_252880_(centerX, centerY, 0.0f);
        pose.m_85841_(scale, scale, 1.0f);
        int w = font.m_92895_(text);
        g.m_280430_(font, Component.m_237113_(text), -w / 2, 0, color);
        pose.m_85849_();
    }
}

