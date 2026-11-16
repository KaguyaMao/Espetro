package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.espetro.network.NetworkManager;
import org.espetro.client.aui.GuiElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 指挥官投票界面
 */
public class CommanderVoteScreen extends EspetroMenuScreen {

    private final String team;
    private List<String> players;
    private int timeRemaining;
    private String opponentTeamName;
    private String opponentFaction;
    private int opponentTimeRemaining;

    private Map<String, Integer> voteCounts = new HashMap<>();
    private String currentVote = null;
    private int scrollOffset = 0;
    private int maxScrollOffset = 0;
    private VotePhase phaseHeader;
    private final Map<String, EspetroAuiWidgets.ActionButton> voteButtons = new HashMap<>();
    private EspetroAuiWidgets.Text voteStatusText;

    public CommanderVoteScreen(String team, List<String> players, int timeRemaining,
                                String opponentTeamName, String opponentFaction,
                                int opponentTimeRemaining) {
        super(Component.literal("指挥官投票"));
        this.team = team;
        this.players = players == null ? new ArrayList<>() : new ArrayList<>(players);
        this.timeRemaining = timeRemaining;
        this.opponentTeamName = opponentTeamName;
        this.opponentFaction = opponentFaction;
        this.opponentTimeRemaining = opponentTimeRemaining;
    }

    public static void open(String team, List<String> players, int timeRemaining,
                            String opponentTeamName, String opponentFaction,
                            int opponentTimeRemaining) {
        Minecraft mc = Minecraft.getInstance();
        org.espetro.client.aui.AuiScreen.openWithFade(
            new CommanderVoteScreen(team, players, timeRemaining,
                opponentTeamName, opponentFaction, opponentTimeRemaining),
            mc.screen);
    }

    public static void updateVoteData(Map<String, Integer> voteCounts, int timeRemaining, int opponentTimeRemaining) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof CommanderVoteScreen screen) {
            screen.voteCounts = voteCounts == null ? new HashMap<>() : new HashMap<>(voteCounts);
            screen.timeRemaining = timeRemaining;
            screen.opponentTimeRemaining = opponentTimeRemaining;
            screen.refreshDynamicElements();
        }
    }

    public boolean isForTeam(String updatedTeam) {
        return Objects.equals(team, updatedTeam);
    }

    /** Updates an already-open screen without recreating the screen instance. */
    public void updatePhaseData(List<String> updatedPlayers, int updatedTimeRemaining,
                                String updatedOpponentTeamName, String updatedOpponentFaction,
                                int updatedOpponentTimeRemaining) {
        List<String> nextPlayers = updatedPlayers == null ? new ArrayList<>() : new ArrayList<>(updatedPlayers);
        boolean playerLayoutChanged = !this.players.equals(nextPlayers);
        this.players = nextPlayers;
        this.timeRemaining = updatedTimeRemaining;
        this.opponentTeamName = updatedOpponentTeamName;
        this.opponentFaction = updatedOpponentFaction;
        this.opponentTimeRemaining = updatedOpponentTimeRemaining;
        if (this.root != null) {
            if (playerLayoutChanged) {
                rebuildMenuRoot();
            } else {
                refreshDynamicElements();
            }
        }
    }

    /** 固定显示60个候选人名字 */
    private static final int VISIBLE_NAME_COUNT = 60;

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        CurrentMapBackgroundRenderer.render(
            graphics, this.width, this.height, ClientGameState.getCurrentMapFolder());
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        voteButtons.clear();
        voteStatusText = null;
        int playerCount = players == null ? 0 : players.size();
        boolean votingOpen = timeRemaining > 0;
        phaseHeader = VotePhase.add(root, width, height, "指挥官投票",
            EspetroAuiWidgets.teamName(team) + " · 选择一名玩家担任指挥官",
            this::buildTimeText, () -> VotePhase.Stage.COMMANDER);
        int phaseHeaderH = VotePhase.contentTop(height);
        int columns = 4;
        int gap = height < 320 ? 6 : 8;
        int listHeaderH = 12;
        int footerH = 32;
        int panelW = Math.max(1, Math.min(800, width - 32));
        int columnW = Math.max(1, (panelW - 24 - (columns - 1) * gap) / columns);
        int availablePanelH = Math.max(1, VotePhase.contentBottom(height) - phaseHeaderH - 8);
        int panelH = availablePanelH;
        int panelX = (width - panelW) / 2;
        int panelY = phaseHeaderH;
        int listH = Math.max(1, panelH - listHeaderH - footerH);
        Minecraft minecraft = Minecraft.getInstance();
        String selfName = minecraft.player == null ? "" : minecraft.player.getName().getString();
        int labelWidth = 1;
        for (String playerName : players) {
            String self = playerName.equals(selfName) ? " (自己)" : "";
            int largestCount = Math.max(playerCount, voteCounts.getOrDefault(playerName, 0));
            labelWidth = Math.max(labelWidth, minecraft.font.width(
                "✓ " + playerName + self + " [" + largestCount + "]"));
        }
        float labelScale = Math.max(0.5F, Math.min(1.0F, (columnW - 12) / (float) labelWidth));
        int cardW = Math.min(columnW, Math.max(1, Math.round(labelWidth * labelScale) + 12));
        int cardH = Math.max(1, Math.min(Math.round(minecraft.font.lineHeight * labelScale) + 6, listH));
        int targetRows = (VISIBLE_NAME_COUNT + columns - 1) / columns;
        // A soft pane fills the available body without a heavy empty black frame.
        root.addChild(new CandidatePanel(panelX, panelY, panelW, panelH));

        if (playerCount == 0) {
            root.addChild(EspetroAuiWidgets.centeredText(panelX, panelY + panelH / 2 - 4, panelW,
                "当前队伍没有可投票玩家", UiTheme.NEGATIVE));
            return;
        }

        int startX = panelX + 12;
        int startY = panelY + listHeaderH;

        // Keep up to sixty visible entries, with scrolling when the available body is full.
        int usableRows = Math.max(1, Math.min(targetRows, (listH + gap) / (cardH + gap)));
        int visibleCount = Math.min(VISIBLE_NAME_COUNT, usableRows * columns);
        maxScrollOffset = Math.max(0, playerCount - visibleCount);
        scrollOffset = Math.min(scrollOffset, maxScrollOffset);
        int maxVisible = Math.min(playerCount, scrollOffset + visibleCount);

        for (int i = scrollOffset; i < maxVisible; i++) {
            String playerName = players.get(i);
            boolean isSelf = playerName.equals(selfName);
            boolean isSelected = playerName.equals(currentVote);

            int localIndex = i - scrollOffset;
            int col = localIndex % columns;
            int row = localIndex / columns;
            int x = startX + col * (cardW + gap);
            int y = startY + row * (cardH + gap);

            String label = buildPlayerLabel(playerName, isSelf, isSelected);

            var button = EspetroAuiWidgets.button(x, y, cardW, cardH, label, () -> voteFor(playerName))
                .setEnabled(votingOpen)
                .setTextScale(labelScale)
                .setSelected(isSelected);
            root.addChild(button);
            voteButtons.put(playerName, button);
        }

        if (maxScrollOffset > 0) {
            root.addChild(EspetroAuiWidgets.centeredText(panelX, panelY + panelH - 26, panelW,
                "鼠标滚轮切换列表  " + (scrollOffset + 1) + "-" + maxVisible + "/" + playerCount,
                UiTheme.TEXT_THIRD));
        }


        voteStatusText = EspetroAuiWidgets.centeredText(
            panelX, panelY + panelH - 13, panelW,
            buildVoteStatusText(), UiTheme.TEXT);
        root.addChild(voteStatusText);
    }

    private static final class CandidatePanel extends GuiElement {
        CandidatePanel(int x, int y, int width, int height) { super(x, y, width, height); }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height,
                         int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) return;
            UiPaint.glassPanel(graphics, x + getX(), y + getY(), getWidth(), getHeight(),
                UiTheme.GLASS_PANEL_SOFT);
        }
    }

    private String lastVoteStatusRendered;
    private int lastTimeRemainingRendered = Integer.MIN_VALUE;
    private int lastOpponentTimeRendered = Integer.MIN_VALUE;

    private void refreshDynamicElements() {
        if (phaseHeader != null) {
            // PhaseHeader 内部 setIfChanged；此处仍避免无意义拼装外的重复写。
            if (timeRemaining != lastTimeRemainingRendered
                || opponentTimeRemaining != lastOpponentTimeRendered) {
                lastTimeRemainingRendered = timeRemaining;
                lastOpponentTimeRendered = opponentTimeRemaining;
                phaseHeader.setStatus(buildTimeText());
                phaseHeader.setDetail("");
            }
        }

        boolean votingOpen = timeRemaining > 0;
        String selfName = Minecraft.getInstance().player == null
            ? "" : Minecraft.getInstance().player.getName().getString();
        for (Map.Entry<String, EspetroAuiWidgets.ActionButton> entry : voteButtons.entrySet()) {
            String playerName = entry.getKey();
            boolean isSelf = playerName.equals(selfName);
            boolean selected = playerName.equals(currentVote);
            entry.getValue()
                .setLabel(buildPlayerLabel(playerName, isSelf, selected))
                .setEnabled(votingOpen)
                .setSelected(selected)
                .setTextColor(UiTheme.TEXT);
        }
        if (voteStatusText != null) {
            String next = buildVoteStatusText();
            if (!Objects.equals(lastVoteStatusRendered, next)) {
                lastVoteStatusRendered = next;
                voteStatusText.setText(next);
            }
        }
    }

    private String buildTimeText() {
        if (timeRemaining > 0) {
            return "剩余时间: " + timeRemaining + "秒";
        }
        return isWaitingForOwnVote()
            ? "本方等待开始 · 对方剩余 " + Math.max(0, opponentTimeRemaining) + " 秒"
            : "本方指挥官投票已结束";
    }

    private String buildOpponentText() {
        if (opponentTeamName == null || opponentTeamName.isEmpty()) {
            return "";
        }
        String text = opponentTeamName;
        if (opponentTimeRemaining >= 0) {
            text += " 指挥官投票剩余: " + opponentTimeRemaining + "秒";
        }
        if (opponentFaction != null && !opponentFaction.isEmpty()) {
            text += " · 编制: " + opponentFaction;
        }
        return text;
    }

    private String buildPlayerLabel(String playerName, boolean isSelf, boolean selected) {
        return (selected ? "✓ " : "") + playerName + (isSelf ? " (自己)" : "")
            + " [" + voteCounts.getOrDefault(playerName, 0) + "]";
    }

    private String buildVoteStatusText() {
        if (isWaitingForOwnVote()) {
            return "等待本方指挥官投票开始";
        }
        if (timeRemaining <= 0) {
            return "等待对方完成指挥官投票";
        }
        return currentVote == null
            ? "尚未投票"
            : EspetroAuiWidgets.teamName(team) + " 当前投票: " + currentVote;
    }

    private void voteFor(String playerName) {
        if (tutorialPreviewMode || playerName == null || timeRemaining <= 0) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        // 允许给自己投票（指挥官候选人自投）。
        if (!playerName.equals(currentVote)) {
            currentVote = playerName;
            NetworkManager.sendCastVote(playerName);
            refreshDynamicElements();
        }
    }

    private boolean isWaitingForOwnVote() {
        // 当前流程固定守方先投票、攻方后投票；攻方收到对方倒计时表示本方尚未开始。
        return "ATTACK".equals(team) && timeRemaining <= 0 && opponentTimeRemaining > 0;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (maxScrollOffset > 0) {
            int nextOffset = scrollOffset + (delta < 0 ? 1 : -1);
            nextOffset = Math.max(0, Math.min(maxScrollOffset, nextOffset));
            if (nextOffset != scrollOffset) {
                scrollOffset = nextOffset;
                rebuildMenuRoot();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        // 淡出切换目标屏时放行；否则保持页面（玩家不能手动跳过投票流程）。
        if (isFadeOutClosing()) {
            super.onClose();
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc != null) {
            mc.setScreen(new CommanderVoteScreen(team, players, timeRemaining,
                opponentTeamName, opponentFaction, opponentTimeRemaining));
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
