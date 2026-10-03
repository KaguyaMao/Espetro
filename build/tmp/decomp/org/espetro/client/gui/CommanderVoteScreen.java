/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.CurrentMapBackgroundRenderer;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.EspetroMenuScreen;
import org.espetro.network.NetworkManager;

public class CommanderVoteScreen
extends EspetroMenuScreen {
    private final String team;
    private List<String> players;
    private int timeRemaining;
    private String opponentTeamName;
    private String opponentFaction;
    private int opponentTimeRemaining;
    private Map<String, Integer> voteCounts = new HashMap<String, Integer>();
    private String currentVote = null;
    private int scrollOffset = 0;
    private int maxScrollOffset = 0;
    private EspetroAuiWidgets.PhaseHeader phaseHeader;
    private final Map<String, EspetroAuiWidgets.ActionButton> voteButtons = new HashMap<String, EspetroAuiWidgets.ActionButton>();
    private EspetroAuiWidgets.Text voteStatusText;
    private static final int VISIBLE_NAME_COUNT = 60;
    private String lastVoteStatusRendered;
    private int lastTimeRemainingRendered = Integer.MIN_VALUE;
    private int lastOpponentTimeRendered = Integer.MIN_VALUE;

    public CommanderVoteScreen(String team, List<String> players, int timeRemaining, String opponentTeamName, String opponentFaction, int opponentTimeRemaining) {
        super(Component.m_237113_("\u6307\u6325\u5b98\u6295\u7968"));
        this.team = team;
        this.players = players == null ? new ArrayList<String>() : new ArrayList<String>(players);
        this.timeRemaining = timeRemaining;
        this.opponentTeamName = opponentTeamName;
        this.opponentFaction = opponentFaction;
        this.opponentTimeRemaining = opponentTimeRemaining;
    }

    public static void open(String team, List<String> players, int timeRemaining, String opponentTeamName, String opponentFaction, int opponentTimeRemaining) {
        Minecraft mc = Minecraft.m_91087_();
        mc.m_91152_(new CommanderVoteScreen(team, players, timeRemaining, opponentTeamName, opponentFaction, opponentTimeRemaining));
    }

    public static void updateVoteData(Map<String, Integer> voteCounts, int timeRemaining, int opponentTimeRemaining) {
        Minecraft mc = Minecraft.m_91087_();
        Screen screen = mc.f_91080_;
        if (screen instanceof CommanderVoteScreen) {
            CommanderVoteScreen screen2 = (CommanderVoteScreen)screen;
            screen2.voteCounts = voteCounts == null ? new HashMap<String, Integer>() : new HashMap<String, Integer>(voteCounts);
            screen2.timeRemaining = timeRemaining;
            screen2.opponentTimeRemaining = opponentTimeRemaining;
            screen2.refreshDynamicElements();
        }
    }

    public boolean isForTeam(String updatedTeam) {
        return Objects.equals(this.team, updatedTeam);
    }

    public void updatePhaseData(List<String> updatedPlayers, int updatedTimeRemaining, String updatedOpponentTeamName, String updatedOpponentFaction, int updatedOpponentTimeRemaining) {
        ArrayList<String> nextPlayers = updatedPlayers == null ? new ArrayList<String>() : new ArrayList<String>(updatedPlayers);
        boolean playerLayoutChanged = !this.players.equals(nextPlayers);
        this.players = nextPlayers;
        this.timeRemaining = updatedTimeRemaining;
        this.opponentTeamName = updatedOpponentTeamName;
        this.opponentFaction = updatedOpponentFaction;
        this.opponentTimeRemaining = updatedOpponentTimeRemaining;
        if (this.root != null) {
            if (playerLayoutChanged) {
                this.rebuildMenuRoot();
            } else {
                this.refreshDynamicElements();
            }
        }
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        CurrentMapBackgroundRenderer.render(graphics, this.f_96543_, this.f_96544_, ClientGameState.getCurrentMapFolder());
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        this.voteButtons.clear();
        this.voteStatusText = null;
        int playerCount = this.players == null ? 0 : this.players.size();
        boolean votingOpen = this.timeRemaining > 0;
        String teamPrefix = EspetroAuiWidgets.teamPrefix(this.team);
        this.phaseHeader = EspetroAuiWidgets.addMutablePhaseHeader(root, this.f_96543_, "\u00a76\u00a7l\u6307\u6325\u5b98\u6295\u7968 \u00a77| " + teamPrefix + "\u00a7l" + EspetroAuiWidgets.teamName(this.team), this.buildTimeText(), this.buildOpponentText(), EspetroAuiWidgets.teamColor(this.team));
        int phaseHeaderH = 42;
        int columns = 4;
        int gap = this.f_96544_ < 320 ? 2 : 4;
        int cardH = this.f_96544_ < 320 ? 12 : 14;
        int targetRows = 60 / columns;
        int listHeaderH = 6;
        int footerH = 24;
        int minCardW = 150;
        int minPanelW = 24 + columns * minCardW + (columns - 1) * gap;
        int panelW = Math.min(this.f_96543_ - 16, Math.max(minPanelW, this.f_96543_ - 28));
        int cardW = (panelW - 24 - (columns - 1) * gap) / columns;
        int requestedPanelH = listHeaderH + targetRows * cardH + Math.max(0, targetRows - 1) * gap + footerH;
        int availablePanelH = Math.max(cardH + listHeaderH + footerH, this.f_96544_ - phaseHeaderH - 12);
        int panelH = Math.min(availablePanelH, requestedPanelH);
        int panelX = (this.f_96543_ - panelW) / 2;
        int panelY = phaseHeaderH + 6 + Math.max(0, (this.f_96544_ - phaseHeaderH - 12 - panelH) / 2);
        root.addChild(EspetroAuiWidgets.panel(panelX, panelY, panelW, panelH, 0, 0));
        if (playerCount == 0) {
            root.addChild(EspetroAuiWidgets.centeredText(panelX, panelY + 12, panelW, "\u00a7c\u5f53\u524d\u961f\u4f0d\u6ca1\u6709\u53ef\u6295\u7968\u73a9\u5bb6", -39322));
            return;
        }
        String selfName = Minecraft.m_91087_().f_91074_ == null ? "" : Minecraft.m_91087_().f_91074_.m_7755_().getString();
        int startX = panelX + 12;
        int startY = panelY + listHeaderH;
        int listH = Math.max(cardH, panelH - listHeaderH - footerH);
        int usableRows = Math.max(1, Math.min(targetRows, (listH + gap) / (cardH + gap)));
        int visibleCount = Math.min(60, usableRows * columns);
        this.maxScrollOffset = Math.max(0, playerCount - visibleCount);
        this.scrollOffset = Math.min(this.scrollOffset, this.maxScrollOffset);
        int maxVisible = Math.min(playerCount, this.scrollOffset + visibleCount);
        for (int i = this.scrollOffset; i < maxVisible; ++i) {
            String playerName = this.players.get(i);
            int votes = this.voteCounts.getOrDefault(playerName, 0);
            boolean isSelf = playerName.equals(selfName);
            boolean isSelected = playerName.equals(this.currentVote);
            int localIndex = i - this.scrollOffset;
            int col = localIndex % columns;
            int row = localIndex / columns;
            int x = startX + col * (cardW + gap);
            int y = startY + row * (cardH + gap);
            String prefix = isSelected ? "\u00a7a\u2713 " : (isSelf ? "\u00a78" : "\u00a7f");
            String label = prefix + playerName + " \u00a7e[" + votes + "]";
            EspetroAuiWidgets.ActionButton button = EspetroAuiWidgets.button(x, y, cardW, cardH, label, () -> this.voteFor(playerName)).setEnabled(votingOpen && !isSelf).setSelected(isSelected).setColors(0, 539833412, 807680551).setBorderColor(0);
            if (isSelf) {
                button.setTextColor(-5327681);
            }
            root.addChild(button);
            this.voteButtons.put(playerName, button);
        }
        if (this.maxScrollOffset > 0) {
            root.addChild(EspetroAuiWidgets.centeredText(panelX, panelY + panelH - 26, panelW, "\u00a78\u9f20\u6807\u6eda\u8f6e\u5207\u6362\u5217\u8868  " + (this.scrollOffset + 1) + "-" + maxVisible + "/" + playerCount, -5327681));
        }
        this.voteStatusText = EspetroAuiWidgets.centeredText(panelX, panelY + panelH - 13, panelW, this.buildVoteStatusText(), -1);
        root.addChild(this.voteStatusText);
    }

    private void refreshDynamicElements() {
        String next;
        if (this.phaseHeader != null && (this.timeRemaining != this.lastTimeRemainingRendered || this.opponentTimeRemaining != this.lastOpponentTimeRendered)) {
            this.lastTimeRemainingRendered = this.timeRemaining;
            this.lastOpponentTimeRendered = this.opponentTimeRemaining;
            this.phaseHeader.setStatus(this.buildTimeText());
            this.phaseHeader.setDetail(this.buildOpponentText());
        }
        boolean votingOpen = this.timeRemaining > 0;
        String selfName = Minecraft.m_91087_().f_91074_ == null ? "" : Minecraft.m_91087_().f_91074_.m_7755_().getString();
        for (Map.Entry<String, EspetroAuiWidgets.ActionButton> entry : this.voteButtons.entrySet()) {
            String playerName = entry.getKey();
            boolean isSelf = playerName.equals(selfName);
            boolean selected = playerName.equals(this.currentVote);
            entry.getValue().setLabel(this.buildPlayerLabel(playerName, isSelf, selected)).setEnabled(votingOpen && !isSelf).setSelected(selected).setTextColor(isSelf ? -5327681 : -1);
        }
        if (this.voteStatusText != null && !Objects.equals(this.lastVoteStatusRendered, next = this.buildVoteStatusText())) {
            this.lastVoteStatusRendered = next;
            this.voteStatusText.setText(next);
        }
    }

    private String buildTimeText() {
        if (this.timeRemaining > 0) {
            return (this.timeRemaining <= 10 ? "\u00a7c" : "\u00a76") + "\u5269\u4f59\u65f6\u95f4: " + this.timeRemaining + "\u79d2";
        }
        return this.isWaitingForOwnVote() ? "\u00a77\u672c\u65b9\u6307\u6325\u5b98\u6295\u7968\u5c1a\u672a\u5f00\u59cb" : "\u00a77\u672c\u65b9\u6307\u6325\u5b98\u6295\u7968\u5df2\u7ed3\u675f";
    }

    private String buildOpponentText() {
        if (this.opponentTeamName == null || this.opponentTeamName.isEmpty()) {
            return "";
        }
        String text = ("ATTACK".equals(this.team) ? "\u00a79" : "\u00a7c") + this.opponentTeamName;
        if (this.opponentTimeRemaining >= 0) {
            text = text + (this.opponentTimeRemaining <= 5 ? " \u00a7c" : " \u00a76") + "\u6307\u6325\u5b98\u6295\u7968\u5269\u4f59: " + this.opponentTimeRemaining + "\u79d2";
        }
        if (this.opponentFaction != null && !this.opponentFaction.isEmpty()) {
            text = text + " \u00a77\u00b7 \u7f16\u5236: " + this.opponentFaction;
        }
        return text;
    }

    private String buildPlayerLabel(String playerName, boolean isSelf, boolean selected) {
        String prefix = selected ? "\u00a7a\u2713 " : (isSelf ? "\u00a78" : "\u00a7f");
        return prefix + playerName + " \u00a7e[" + this.voteCounts.getOrDefault(playerName, 0) + "]";
    }

    private String buildVoteStatusText() {
        if (this.isWaitingForOwnVote()) {
            return "\u00a78\u7b49\u5f85\u672c\u65b9\u6307\u6325\u5b98\u6295\u7968\u5f00\u59cb";
        }
        if (this.timeRemaining <= 0) {
            return "\u00a78\u7b49\u5f85\u5bf9\u65b9\u5b8c\u6210\u6307\u6325\u5b98\u6295\u7968";
        }
        return this.currentVote == null ? "\u00a78\u5c1a\u672a\u6295\u7968" : EspetroAuiWidgets.teamPrefix(this.team) + "\u5f53\u524d\u6295\u7968: \u00a7a" + this.currentVote;
    }

    private void voteFor(String playerName) {
        if (this.tutorialPreviewMode || playerName == null || this.timeRemaining <= 0) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ != null && playerName.equals(mc.f_91074_.m_7755_().getString())) {
            return;
        }
        if (!playerName.equals(this.currentVote)) {
            this.currentVote = playerName;
            NetworkManager.sendCastVote(playerName);
            this.refreshDynamicElements();
        }
    }

    private boolean isWaitingForOwnVote() {
        return "ATTACK".equals(this.team) && this.timeRemaining <= 0 && this.opponentTimeRemaining > 0;
    }

    @Override
    public boolean m_6913_() {
        return false;
    }

    @Override
    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        if (this.maxScrollOffset > 0) {
            int nextOffset = this.scrollOffset + (delta < 0.0 ? 1 : -1);
            if ((nextOffset = Math.max(0, Math.min(this.maxScrollOffset, nextOffset))) != this.scrollOffset) {
                this.scrollOffset = nextOffset;
                this.rebuildMenuRoot();
                return true;
            }
        }
        return super.m_6050_(mouseX, mouseY, delta);
    }

    @Override
    public void m_7379_() {
        Minecraft mc = Minecraft.m_91087_();
        if (mc != null) {
            mc.m_91152_(new CommanderVoteScreen(this.team, this.players, this.timeRemaining, this.opponentTeamName, this.opponentFaction, this.opponentTimeRemaining));
        }
    }

    @Override
    public boolean m_7043_() {
        return false;
    }
}

