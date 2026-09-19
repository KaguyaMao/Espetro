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
        EspetroAuiWidgets.drawScreenShade(graphics, this.width, this.height);
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        int cx = this.width / 2;

        // 大字（正中间）与底部黄字之间的间距，用于对齐上方白字：白字放在大字上方同样间距处。
        // 大字中心 0.50H，黄字 top 0.68H → 间距约 0.18H，白字 top 取 0.50H - 0.18H ≈ 0.32H。
        root.addChild(EspetroAuiWidgets.centeredText(cx - 260, (int) (this.height * 0.32f), 520,
            "§f§l您已被分配至", EspetroAuiWidgets.TEXT));

        // 中间（垂直正中）：大字号 进攻方/防守方（红色/蓝色）
        root.addChild(new BigTeamText(cx, (int) (this.height * 0.5f), team));

        // 中下部：黄色预告
        root.addChild(EspetroAuiWidgets.centeredText(cx - 300, (int) (this.height * 0.68f), 600,
            "§e§l游戏即将开始，正在进入投票阶段", EspetroAuiWidgets.GOLD));
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
            int color = "DEFEND".equals(team) ? 0xFF3FA9F5 : 0xFFE03B3B;
            float scale = 4.0f;
            // 注意：参数 y 是父布局传入的 refY（通常为 0），绝对位置必须用 getY()。
            int absY = y + getY();
            graphics.pose().pushPose();
            graphics.pose().translate(cx, absY, 0);
            graphics.pose().scale(scale, scale, 1.0f);
            graphics.drawCenteredString(Minecraft.getInstance().font,
                Component.literal(label), 0, 0, color);
            graphics.pose().popPose();
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
