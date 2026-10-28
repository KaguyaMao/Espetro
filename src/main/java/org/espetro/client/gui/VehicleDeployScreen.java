package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.espetro.network.VehicleDeployScreenPacket;
import org.espetro.client.aui.GuiElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 载具部署界面：结构不变时原地刷新冷却/在场数，避免整页 rebuild 闪烁。
 */
public class VehicleDeployScreen extends EspetroMenuScreen {

    private static final int PANEL_WIDTH = 560;
    private static final int PANEL_MIN_WIDTH = 300;
    private static final int PANEL_MARGIN = 28;
    private static final int HEADER_H = 46;
    private static final int PANEL_PADDING = 8;
    private static final int ROW_H = 30;
    private static final int ROW_GAP = 6;
    private static final int SCROLLBAR_RESERVED_W = 8;

    private List<VehicleDeployScreenPacket.VehicleInfo> vehicles;
    private final List<VehicleRow> vehicleRows = new ArrayList<>();
    private Object structureSignature;

    public VehicleDeployScreen(List<VehicleDeployScreenPacket.VehicleInfo> vehicles) {
        super(Component.literal("载具信息"));
        this.vehicles = vehicles != null ? new ArrayList<>(vehicles) : new ArrayList<>();
        this.structureSignature = signatureOf(this.vehicles);
    }

    /** 服务端推送刷新：类型列表变则 rebuild，否则原地改文案。 */
    public void updateFromPacket(List<VehicleDeployScreenPacket.VehicleInfo> next) {
        List<VehicleDeployScreenPacket.VehicleInfo> list =
            next != null ? new ArrayList<>(next) : new ArrayList<>();
        Object sig = signatureOf(list);
        this.vehicles = list;
        if (!Objects.equals(structureSignature, sig)) {
            structureSignature = sig;
            rebuildMenuRoot();
        } else {
            // 冷却/在场数由行内每帧读取，这里只把新数据挂回已有行。
            for (int i = 0; i < vehicleRows.size() && i < list.size(); i++) {
                vehicleRows.get(i).info = list.get(i);
            }
        }
    }

    private static Object signatureOf(List<VehicleDeployScreenPacket.VehicleInfo> list) {
        StringBuilder sb = new StringBuilder();
        for (VehicleDeployScreenPacket.VehicleInfo v : list) {
            sb.append(v.type).append('|');
        }
        return sb.toString();
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        vehicleRows.clear();
        int panelW = Math.min(PANEL_WIDTH, Math.max(PANEL_MIN_WIDTH, this.width - PANEL_MARGIN * 2));
        int listContentH = vehicles.isEmpty()
            ? ROW_H
            : vehicles.size() * ROW_H + Math.max(0, vehicles.size() - 1) * ROW_GAP;
        int maxListH = Math.max(ROW_H, this.height - HEADER_H - PANEL_PADDING * 2 - 36);
        int listH = Math.min(listContentH, maxListH);
        int panelH = HEADER_H + listH + PANEL_PADDING * 2;
        int panelX = (this.width - panelW) / 2;
        int panelY = Math.max(8, (this.height - panelH) / 2 - 18);

        root.addChild(EspetroAuiWidgets.card(panelX, panelY, panelW, panelH));

        EspetroAuiWidgets.Text title = EspetroAuiWidgets.centeredText(
            panelX, panelY + 8, panelW, "载具信息", UiTheme.GOLD);
        title.setTextScale(UiTheme.TYPE_TITLE).setShadow(true);
        root.addChild(title);
        root.addChild(EspetroAuiWidgets.stat(panelX + panelW - 76, panelY + 6, 64,
            "VEHICLES", String.valueOf(vehicles.size()), UiTheme.TEXT));
        root.addChild(EspetroAuiWidgets.centeredText(
            panelX, panelY + 25, panelW, "冷却与在场数量实时更新", UiTheme.TEXT_THIRD)
            .setTextScale(UiTheme.TYPE_MICRO));
        root.addChild(EspetroAuiWidgets.divider(
            panelX + PANEL_PADDING, panelY + HEADER_H - 8, panelW - PANEL_PADDING * 2));

        int listX = panelX + PANEL_PADDING;
        int listY = panelY + HEADER_H + PANEL_PADDING;
        int listW = panelW - PANEL_PADDING * 2;
        // 列表底：内凹槽位，把可滚动列表与卡片底区分开。
        root.addChild(EspetroAuiWidgets.sunken(listX - 2, listY - 2, listW + 4, listH + 4));
        ScrollableList list = new ScrollableList(listX, listY, listW, listH)
            .setScrollStep(ROW_H + ROW_GAP)
            .setAlwaysShowScrollbar(true);
        root.addChild(list);

        if (vehicles.isEmpty()) {
            list.addChild(new VehicleRow(0, 0, listW - SCROLLBAR_RESERVED_W, ROW_H, null));
            return;
        }

        int y = 0;
        for (VehicleDeployScreenPacket.VehicleInfo vehicle : vehicles) {
            VehicleRow row = new VehicleRow(0, y, listW - SCROLLBAR_RESERVED_W, ROW_H, vehicle);
            list.addChild(row);
            vehicleRows.add(row);
            y += ROW_H + ROW_GAP;
        }
    }

    private static int computeRemainingSeconds(VehicleDeployScreenPacket.VehicleInfo vehicle) {
        long remaining = Math.max(0L, vehicle.readyAtEpochMs - System.currentTimeMillis());
        return (int) Math.min(Integer.MAX_VALUE, (remaining + 999L) / 1000L);
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        EspetroAuiWidgets.drawScreenShade(graphics, this.width, this.height);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /**
     * 单行载具信息：纯展示行（原为始终禁用的按钮），
     * 名称 + 冷却/就绪状态 chip + 在场/上限数字。
     */
    private static final class VehicleRow extends GuiElement {
        private VehicleDeployScreenPacket.VehicleInfo info;

        VehicleRow(int x, int y, int width, int height,
                   VehicleDeployScreenPacket.VehicleInfo info) {
            super(x, y, width, height);
            this.info = info;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height,
                         int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) return;
            int bx = x + getX();
            int by = y + getY();
            int bw = getWidth();
            int bh = getHeight();

            UiPaint.roundRect(graphics, bx, by, bw, bh, UiTheme.RADIUS_CONTROL, UiTheme.SURFACE_2);
            UiPaint.strokeRoundRect(graphics, bx, by, bw, bh, UiTheme.RADIUS_CONTROL, UiTheme.HAIRLINE);

            if (info == null) {
                EspetroAuiWidgets.drawScaledString(graphics, "当前编制无载具配置",
                    bx + 10, by + 10, UiTheme.TEXT_THIRD, 1.0f);
                return;
            }

            int remaining = computeRemainingSeconds(info);
            boolean full = info.current >= info.max;
            int stateColor = remaining > 0 ? UiTheme.WARNING
                : full ? UiTheme.NEGATIVE : UiTheme.POSITIVE;
            String stateText = remaining > 0
                ? "冷却 " + UnifiedDeployScreen.formatCooldown(remaining)
                : full ? "已满" : "就绪";
            UiPaint.accentBarLeft(graphics, bx, by, bh, stateColor);

            int nameColor = remaining > 0 || full ? UiTheme.TEXT_SECOND : UiTheme.TEXT;
            EspetroAuiWidgets.drawScaledString(graphics, info.displayName,
                bx + 10, by + 5, nameColor, 1.0f);
            EspetroAuiWidgets.drawScaledString(graphics, info.respawnMinutes + "分钟刷新",
                bx + 10, by + 18, UiTheme.TEXT_THIRD, 0.8f);

            // 状态标记：直角软色底与细线框，保持终端式信息密度。
            float chipScale = 0.85f;
            int chipTextW = (int) (Minecraft.getInstance().font.width(stateText) * chipScale);
            int chipW = Math.max(24, chipTextW + 12);
            int chipH = 13;
            int chipX = bx + bw - chipW - 8;
            int chipY = by + (bh - chipH) / 2;
            UiPaint.roundRect(graphics, chipX, chipY, chipW, chipH, UiTheme.RADIUS_CONTROL,
                UiTheme.alpha(stateColor, 0.14f));
            UiPaint.strokeRoundRect(graphics, chipX, chipY, chipW, chipH, UiTheme.RADIUS_CONTROL,
                UiTheme.alpha(stateColor, 0.55f));
            EspetroAuiWidgets.drawScaledString(graphics, stateText,
                chipX + (chipW - chipTextW) / 2, chipY + 3, stateColor, chipScale);

            // 在场/上限：右对齐数字，带阴影提升可读性。
            String count = info.current + "/" + info.max;
            int countW = (int) (Minecraft.getInstance().font.width(count) * 1.15f);
            EspetroAuiWidgets.drawScaledString(graphics, count, chipX - countW - 10, by + 9,
                UiTheme.TEXT_SECOND, 1.15f, true);
        }
    }
}
