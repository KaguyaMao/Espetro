package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.espetro.network.DeployPointSelectPacket;
import org.espetro.client.aui.GuiElement;

import java.util.ArrayList;
import java.util.List;

/**
 * 复活点选择界面
 * 玩家死亡后打开，选择在原部署点或兵站复活
 */
public class DeployPointSelectScreen extends EspetroMenuScreen {

    private final boolean hasDeployPoint;
    private final String deployPointPos;
    private final List<DeployPointSelectPacket.BastionItem> bastions;

    private static final int BUTTON_WIDTH = 260;
    private static final int BUTTON_HEIGHT = 26;
    private static final int VERTICAL_SPACING = 5;
    private static final int START_Y = 55;
    private int startX;

    public DeployPointSelectScreen(boolean hasDeployPoint, String deployPointPos,
                                   List<DeployPointSelectPacket.BastionItem> bastions) {
        super(Component.literal("选择复活点"));
        this.hasDeployPoint = hasDeployPoint;
        this.deployPointPos = deployPointPos;
        this.bastions = bastions != null ? bastions : new ArrayList<>();
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        startX = (this.width - BUTTON_WIDTH) / 2;
        int optionCount = (hasDeployPoint ? 1 : 0) + bastions.size();
        EspetroAuiWidgets.addPhaseHeader(root, this.width,
            "选择复活位置", "请选择你要复活的位置",
            "共 " + optionCount + " 个可用复活点", UiTheme.GOLD);

        // 选项容器：玻璃卡片，按钮保持原坐标语义，仅四周留出内边距。
        int cardX = startX - 10;
        int cardW = BUTTON_WIDTH + 20;
        int cardY = START_Y - 7;
        int rows = Math.max(1, optionCount);
        int cardH = rows * (BUTTON_HEIGHT + VERTICAL_SPACING) - VERTICAL_SPACING + 14;
        root.addChild(EspetroAuiWidgets.card(cardX, cardY, cardW, cardH));

        int y = START_Y;

        // 主基地按钮（主行动，用主按钮样式）
        if (hasDeployPoint) {
            String label = "主基地 · " + deployPointPos;
            root.addChild(EspetroAuiWidgets.primaryButton(startX, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                label, () -> {
                if (Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.connection.sendCommand("bastion deploy");
                }
            }));
            y += BUTTON_HEIGHT + VERTICAL_SPACING;
        }

        // 兵站按钮（正向语义色）
        for (DeployPointSelectPacket.BastionItem b : bastions) {
            String label = b.name + " · " + b.pos;
            root.addChild(EspetroAuiWidgets.button(startX, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                label, () -> {
                if (Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.connection.sendCommand(
                        "bastion select " + b.id.toString());
                }
            }).setTextColor(UiTheme.POSITIVE).setAccentColor(UiTheme.POSITIVE));
            y += BUTTON_HEIGHT + VERTICAL_SPACING;
        }

        // 没有可选复活点
        if (!hasDeployPoint && bastions.isEmpty()) {
            root.addChild(EspetroAuiWidgets.button(startX, y, BUTTON_WIDTH, 30,
                "没有可用的复活点", null).setEnabled(false));
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false; // 死亡时不可关闭
    }

    @Override
    public void onClose() {
        // 兼容旧复活点包：成功选择复活点前始终保持本界面。
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
