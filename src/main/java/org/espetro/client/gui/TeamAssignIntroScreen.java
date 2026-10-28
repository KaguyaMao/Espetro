package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.GuiElement;

/**
 * 队伍分配提示页：自动分配完成后显示 5 秒。
 * 中上部白字「您已被分配至」，中间大字号蓝「防守方」/红「进攻方」，
 * 中下部黄字「游戏即将开始，正在进入投票阶段」。
 */
public class TeamAssignIntroScreen extends EspetroMenuScreen {

    private final String team;
    private int ticksLeft;
    private boolean done;

    public TeamAssignIntroScreen(String team, int durationSeconds) {
        super(Component.literal("队伍分配"));
        this.team = "DEFEND".equals(team) ? "DEFEND" : "ATTACK";
        this.ticksLeft = Math.max(1, durationSeconds) * 20;
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 与地图投票页保持一致的「模糊地图图」背景；地图未定/加载失败时渲染器自动回退为暗幕。
        CurrentMapBackgroundRenderer.render(
            graphics, this.width, this.height, resolveBackgroundMapFolder());
    }

    /** 优先本局已装载的地图，其次走投票页的「本人投票 → 胜出 → 首个候选」回退链。 */
    private static String resolveBackgroundMapFolder() {
        String current = ClientGameState.getCurrentMapFolder();
        if (current != null && !current.isBlank()) {
            return current;
        }
        return MapVoteScreen.resolveBackgroundMapFolder();
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        int cx = this.width / 2;

        // 大字（正中间）与底部黄字之间的间距，用于对齐上方白字：白字放在大字上方同样间距处。
        // 大字中心 0.50H，黄字 top 0.68H → 间距约 0.18H，白字 top 取 0.50H - 0.18H ≈ 0.32H。
        int introY = (int) (this.height * 0.32f);
        EspetroAuiWidgets.Text assigned = EspetroAuiWidgets.centeredText(cx - 260, introY, 520,
            "您已被分配至", UiTheme.TEXT_SECOND);
        assigned.setTextScale(UiTheme.TYPE_HEADING).setShadow(true);
        root.addChild(assigned);

        // 标题下的队伍色细线：把「分配结果」与下方大字连成一组。
        int dividerW = 160;
        root.addChild(EspetroAuiWidgets.divider(cx - dividerW / 2, introY + 20, dividerW)
            .setColor(UiTheme.alpha(EspetroAuiWidgets.teamColor(team), 0.55f)));

        // 中间（垂直正中）：大字号 进攻方/防守方（红色/蓝色）
        root.addChild(new BigTeamText(cx, (int) (this.height * 0.5f), team));

        // 中下部：黄色预告（状态胶囊）
        EspetroAuiWidgets.Chip entering = EspetroAuiWidgets.chip(0,
            (int) (this.height * 0.68f), "游戏即将开始，正在进入投票阶段", UiTheme.WARNING);
        entering.setX(cx - entering.getWidth() / 2);
        root.addChild(entering);
    }

    /** 大字号队伍名（独立 GuiElement，用 PoseStack 放大绘制，避免小字号重叠）。 */
    private static final class BigTeamText extends GuiElement {
        private final String team;
        private final int cx;

        BigTeamText(int cx, int y, String team) {
            super(0, y, 1, 1);
            this.cx = cx;
            this.team = team;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height,
                         int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) return;
            String label = "DEFEND".equals(team) ? "防守方" : "进攻方";
            int color = "DEFEND".equals(team) ? UiTheme.DEFEND : UiTheme.ATTACK;
            float scale = 4.0f;
            // 注意：参数 y 是父布局传入的 refY（通常为 0），绝对位置必须用 getY()。
            int absY = y + getY();
            int textW = Math.round(Minecraft.getInstance().font.width(label) * scale);
            int textH = Math.round(Minecraft.getInstance().font.lineHeight * scale);
            // 工业角标标记分配结果，保留阵营色文字与单一黄色确认信号。
            UiPaint.brackets(graphics, cx - textW / 2 - 12, absY - 8,
                textW + 24, textH + 16, UiTheme.ACCENT_LINE);
            UiPaint.label(graphics, "TEAM / ASSIGNED", cx - textW / 2 - 12,
                absY - 21, UiTheme.TEXT_THIRD);
            UiPaint.textCentered(graphics, label, 0, absY, cx * 2,
                color, scale, false);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (done) return;
        ticksLeft--;
        if (ticksLeft <= 0 && Minecraft.getInstance().screen == this && !tutorialPreviewMode) {
            done = true;
            // 服务端 5 秒后推进到指挥官投票并推送 CommanderVoteScreen；
            // 若已在淡出中（数据包先行）则等待其完成，避免追加冲突关闭动作。
            if (!isFadingOut()) {
                org.espetro.client.aui.AuiScreen.closeWithFade(this);
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
