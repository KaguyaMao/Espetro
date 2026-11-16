package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.espetro.client.aui.GuiElement;
import org.espetro.team.GamePhase;

import java.util.function.Supplier;

/** Shared presentation only: phase labels and timers always come from live screen data. */
final class VotePhase extends GuiElement {
    enum Stage {
        MAP("地图投票"), COMMANDER("指挥官投票"), ATTACK("进攻方投票"),
        DEFEND("防守方投票"), RESULT("投票结果公示"), NONE("");

        final String label;
        Stage(String label) { this.label = label; }
    }

    private final int screenWidth;
    private final int screenHeight;
    private String title;
    private String subtitle;
    private String countdownOverride;
    private final Supplier<String> countdown;
    private final Supplier<Stage> stage;
    private Supplier<Stage> completedFaction = () -> Stage.NONE;

    private VotePhase(int width, int height, String title, String subtitle,
                      Supplier<String> countdown, Supplier<Stage> stage) {
        super(0, 0, width, height);
        screenWidth = width;
        screenHeight = height;
        this.title = title;
        this.subtitle = subtitle;
        this.countdown = countdown;
        this.stage = stage;
    }

    static VotePhase add(GuiElement root, int width, int height, String title, String subtitle,
                         Supplier<String> countdown, Supplier<Stage> stage) {
        VotePhase view = new VotePhase(width, height, title, subtitle, countdown, stage);
        root.addChild(view);
        return view;
    }

    void setTitle(String value) { title = value; }
    void completedFaction(Supplier<Stage> value) { completedFaction = value; }
    void setStatus(String value) { countdownOverride = value; }
    void setDetail(String value) {
        if (value != null && !value.isBlank()) countdownOverride = value;
    }

    static int contentTop(int height) { return height < 280 ? 52 : 64; }
    static int contentBottom(int height) { return height - (height < 280 ? 48 : 58); }

    static Stage currentStage(Stage fallback) {
        GamePhase phase = ClientGameState.getCurrentPhase();
        if (phase == null) return fallback;
        return switch (phase) {
            case MAP_VOTE, MAP_REVEAL, MAP_LOADING -> Stage.MAP;
            case COMMANDER_VOTE, DEFEND_COMMANDER_VOTE, ATTACK_COMMANDER_VOTE -> Stage.COMMANDER;
            case ATTACK_FACTION_SELECT -> Stage.ATTACK;
            case DEFEND_FACTION_SELECT -> Stage.DEFEND;
            case FACTION_REVEAL -> Stage.RESULT;
            default -> fallback;
        };
    }

    static Stage factionStage(String ownTeam, boolean waitingForOpponent) {
        GamePhase phase = ClientGameState.getCurrentPhase();
        if (phase != null && phase.isFactionSelectPhase()) {
            return "ATTACK".equals(phase.getActiveTeam()) ? Stage.ATTACK : Stage.DEFEND;
        }
        boolean attack = "ATTACK".equals(ownTeam);
        if (waitingForOpponent) attack = !attack;
        return attack ? Stage.ATTACK : Stage.DEFEND;
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y, int width, int height,
                     int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) return;
        int top = screenHeight < 280 ? 12 : 20;
        centered(graphics, title, top, UiTheme.TEXT, UiTheme.TYPE_TITLE);
        centered(graphics, subtitle, top + 19, UiTheme.TEXT_SECOND, UiTheme.TYPE_BODY);
        Stage active = stage.get();
        int footerTop = contentBottom(screenHeight) + 8;
        int railWidth = Math.min(640, Math.max(1, screenWidth - 24));
        int railX = (screenWidth - railWidth) / 2;
        int slotWidth = Math.max(1, railWidth / 5);
        for (int i = 0; i < 5; i++) {
            Stage item = Stage.values()[i];
            int slotX = railX + i * slotWidth;
            boolean completed = completed(item, active) || item == completedFaction.get();
            int color = active == item ? UiTheme.SIGNAL_YELLOW
                : completed ? UiTheme.TEXT_SECOND : UiTheme.TEXT_THIRD;
            int textWidth = Minecraft.getInstance().font.width(item.label);
            float scale = Math.min(0.85f, (slotWidth - 4f) / Math.max(1, textWidth));
            UiPaint.textCentered(graphics, item.label, slotX, footerTop, slotWidth,
                color, Math.max(0.3f, scale), false);
            UiPaint.hairline(graphics, slotX + 3, footerTop + 13,
                Math.max(1, slotWidth - 6), active == item ? UiTheme.SIGNAL_YELLOW
                    : completed ? UiTheme.TEXT_SECOND : UiTheme.HAIRLINE_STRONG);
        }
        String timer = countdownOverride == null ? countdown.get() : countdownOverride;
        centered(graphics, timer, footerTop + 23, UiTheme.TEXT, UiTheme.TYPE_BODY);
    }

    private static boolean completed(Stage item, Stage active) {
        if (active == Stage.NONE || active == Stage.MAP) return false;
        if (item == Stage.MAP) return true;
        if (item == Stage.COMMANDER) return active == Stage.ATTACK
            || active == Stage.DEFEND || active == Stage.RESULT;
        // RAAS randomly chooses the first faction: never infer this from label order.
        return active == Stage.RESULT && (item == Stage.ATTACK || item == Stage.DEFEND);
    }

    private void centered(GuiGraphics graphics, String value, int y, int color, float scale) {
        String text = value == null ? "" : value;
        int textWidth = Minecraft.getInstance().font.width(EspetroAuiWidgets.stripFormatting(text));
        float fitted = Math.min(scale, Math.max(0.3f, (screenWidth - 24f) / Math.max(1, textWidth)));
        UiPaint.textCentered(graphics, text, 12, y, Math.max(1, screenWidth - 24), color, fitted, false);
    }

    /** Center every row, including an incomplete final row; shrink to leave room for the rail. */
    static Grid grid(int width, int height, int count, int maxColumns, int footerHeight) {
        int actualCount = Math.max(1, count);
        int gap = width < 400 ? 6 : 10;
        int preferredColumns = actualCount == 4 ? 2 : 3;
        if (width < 300) preferredColumns = 2;
        int columns = Math.min(actualCount, Math.min(maxColumns, preferredColumns));
        columns = Math.max(1, columns);
        int rows = (actualCount + columns - 1) / columns;
        int availableWidth = Math.max(columns, Math.min(740, width - 28));
        int availableHeight = Math.max(rows, contentBottom(height) - contentTop(height) - 8);
        int cardWidth = Math.max(1, Math.min(220, (availableWidth - (columns - 1) * gap) / columns));
        int cardHeight = Math.max(1, Math.min(cardWidth * 9 / 16 + footerHeight + 8,
            (availableHeight - (rows - 1) * gap) / rows));
        cardWidth = Math.min(cardWidth, Math.max(1, (cardHeight - footerHeight - 8) * 16 / 9 + 8));
        int panelHeight = rows * cardHeight + (rows - 1) * gap;
        int startY = contentTop(height) + Math.max(0, (availableHeight - panelHeight) / 2);
        return new Grid(width, actualCount, columns, gap, cardWidth, cardHeight, startY);
    }

    record Grid(int screenWidth, int count, int columns, int gap, int cardWidth,
                int cardHeight, int startY) {
        int x(int index) {
            int rowCount = Math.min(columns, count - (index / columns) * columns);
            return (screenWidth - rowCount * cardWidth - (rowCount - 1) * gap) / 2
                + (index % columns) * (cardWidth + gap);
        }
        int y(int index) { return startY + (index / columns) * (cardHeight + gap); }
    }
}
