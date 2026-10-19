package org.espetro.client.gui;

import net.minecraft.network.chat.Component;
import org.espetro.client.aui.GuiElement;

/**
 * 主城（和平区）Hub 菜单。保留世界透视，只叠一张玻璃卡片。
 */
public final class HubScreen extends EspetroMenuScreen {
    private int onlineCount;
    private String status;
    private EspetroAuiWidgets.Text onlineText;
    private EspetroAuiWidgets.Text statusText;
    private EspetroAuiWidgets.Chip statusChip;

    public HubScreen(int onlineCount, String status) {
        super(Component.literal("Espetro 主城"));
        this.onlineCount = onlineCount;
        this.status = status == null ? "" : status;
    }

    /** 不重建整页，只改在线人数与状态行。 */
    public void updateStatus(int onlineCount, String status) {
        String nextStatus = status == null ? "" : status;
        if (onlineText != null && this.onlineCount != onlineCount) {
            onlineText.setText(onlineCount + " 人在线");
        }
        if (statusText != null && !java.util.Objects.equals(this.status, nextStatus)) {
            statusText.setText(nextStatus);
        }
        if (statusChip != null && !java.util.Objects.equals(this.status, nextStatus)) {
            statusChip.setLabel(nextStatus.isEmpty()
                ? "待命" : EspetroAuiWidgets.trimToWidth(nextStatus, 90));
        }
        this.onlineCount = onlineCount;
        this.status = nextStatus;
    }

    @Override
    protected boolean shadeWorld() {
        return false;
    }

    @Override
    protected void renderBeforeMenu(net.minecraft.client.gui.GuiGraphics graphics,
                                     int mouseX, int mouseY, float partialTick) {
        // 主城保留世界透视：只在卡片周围压一圈柔和暗角，避免世界亮度冲淡文字。
        int cardW = Math.min(320, width - 40);
        int cardH = 196;
        int x = (width - cardW) / 2;
        int y = (height - cardH) / 2;
        for (int i = 0; i < 14; i++) {
            int pad = 24 + i * 6;
            graphics.fill(x - pad, y - pad, x + cardW + pad, y + cardH + pad,
                UiTheme.withAlpha(0xFF000000, 2 + i / 2));
        }
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        int panelW = Math.min(320, width - 40);
        int panelH = 196;
        int x = (width - panelW) / 2;
        int y = (height - panelH) / 2;
        int inner = panelW - 32;

        root.addChild(EspetroAuiWidgets.card(x, y, panelW, panelH)
            .setAccentColor(UiTheme.GOLD));
        root.addChild(EspetroAuiWidgets.text(x + 16, y + 15, inner,
                "和平区 · HUB", UiTheme.GOLD)
            .setTextScale(UiTheme.TYPE_MICRO)
            .setSpacing(UiTheme.LETTERSPACING_MICRO));
        root.addChild(EspetroAuiWidgets.text(x + 16, y + 29, inner,
                "Espetro 主城", UiTheme.TEXT)
            .setTextScale(UiTheme.TYPE_TITLE)
            .setShadow(true));

        EspetroAuiWidgets.Chip chip = EspetroAuiWidgets.chip(0, 0,
            status.isEmpty() ? "待命" : EspetroAuiWidgets.trimToWidth(status, 90), UiTheme.POSITIVE);
        chip.setX(x + panelW - 16 - chip.getWidth());
        chip.setY(y + 17);
        statusChip = chip;
        root.addChild(chip);

        root.addChild(EspetroAuiWidgets.divider(x + 16, y + 55, inner));

        root.addChild(EspetroAuiWidgets.text(x + 16, y + 66, inner,
                "当前在线", UiTheme.TEXT_THIRD)
            .setTextScale(UiTheme.TYPE_MICRO)
            .setSpacing(UiTheme.LETTERSPACING_MICRO));
        onlineText = EspetroAuiWidgets.text(x + 16, y + 77, inner,
            onlineCount + " 人在线", UiTheme.TEXT);
        onlineText.setTextScale(UiTheme.TYPE_HEADING).setShadow(true);
        root.addChild(onlineText);

        statusText = EspetroAuiWidgets.text(x + 16, y + 99, inner, status, UiTheme.TEXT_SECOND);
        statusText.setTextScale(0.95f);
        root.addChild(statusText);

        root.addChild(EspetroAuiWidgets.divider(x + 16, y + 121, inner));

        root.addChild(EspetroAuiWidgets.primaryButton(x + 16, y + 132, inner, 22,
            "进入新手教程",
            () -> {
                // 关闭主城菜单后由服务端推送教程（HUD + 只读阶段 GUI）
                onClose();
                org.espetro.network.NetworkManager.sendTutorialReopen();
            }));
        root.addChild(EspetroAuiWidgets.button(x + 16, y + 160, inner, 22, "关闭", this::onClose));

        root.addChild(EspetroAuiWidgets.text(x + panelW - 76, y + panelH - 12, 60,
                "ESC 关闭", UiTheme.TEXT_DISABLED)
            .setTextScale(UiTheme.TYPE_MICRO));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
