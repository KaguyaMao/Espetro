/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.EspetroMenuScreen;
import org.espetro.client.gui.ScrollableList;
import org.espetro.network.VehicleDeployScreenPacket;

public class VehicleDeployScreen
extends EspetroMenuScreen {
    private static final int PANEL_WIDTH = 560;
    private static final int PANEL_MIN_WIDTH = 300;
    private static final int PANEL_MARGIN = 28;
    private static final int HEADER_H = 46;
    private static final int PANEL_PADDING = 8;
    private static final int ROW_H = 30;
    private static final int ROW_GAP = 6;
    private static final int SCROLLBAR_RESERVED_W = 8;
    private static final int PANEL_BG = -1340860642;
    private static final int ROW_READY = -2144322254;
    private static final int ROW_READY_HOVER = -1606725830;
    private static final int ROW_BLOCKED = 1883259978;
    private static final int ROW_BORDER = 1884181344;
    private List<VehicleDeployScreenPacket.VehicleInfo> vehicles;
    private final List<RowBinding> rowBindings = new ArrayList<RowBinding>();
    private Object structureSignature;

    public VehicleDeployScreen(List<VehicleDeployScreenPacket.VehicleInfo> vehicles) {
        super(Component.m_237113_("\u8f7d\u5177\u4fe1\u606f"));
        this.vehicles = vehicles != null ? new ArrayList<VehicleDeployScreenPacket.VehicleInfo>(vehicles) : new ArrayList();
        this.structureSignature = VehicleDeployScreen.signatureOf(this.vehicles);
    }

    public void updateFromPacket(List<VehicleDeployScreenPacket.VehicleInfo> next) {
        ArrayList<VehicleDeployScreenPacket.VehicleInfo> list = next != null ? new ArrayList<VehicleDeployScreenPacket.VehicleInfo>(next) : new ArrayList();
        Object sig = VehicleDeployScreen.signatureOf(list);
        this.vehicles = list;
        if (!Objects.equals(this.structureSignature, sig)) {
            this.structureSignature = sig;
            this.rebuildMenuRoot();
        } else {
            for (int i = 0; i < this.rowBindings.size() && i < list.size(); ++i) {
                this.rowBindings.get((int)i).info = (VehicleDeployScreenPacket.VehicleInfo)list.get(i);
            }
            this.refreshRows();
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
        this.rowBindings.clear();
        int panelW = Math.min(560, Math.max(300, this.f_96543_ - 56));
        int listContentH = this.vehicles.isEmpty() ? 30 : this.vehicles.size() * 30 + Math.max(0, this.vehicles.size() - 1) * 6;
        int maxListH = Math.max(30, this.f_96544_ - 46 - 16 - 36);
        int listH = Math.min(listContentH, maxListH);
        int panelH = 46 + listH + 16;
        int panelX = (this.f_96543_ - panelW) / 2;
        int panelY = Math.max(8, (this.f_96544_ - panelH) / 2 - 18);
        root.addChild(EspetroAuiWidgets.panel(panelX, panelY, panelW, panelH, -1340860642, -10788256));
        root.addChild(EspetroAuiWidgets.centeredText(panelX, panelY + 12, panelW, "\u00a76\u00a7l\u8f7d\u5177\u4fe1\u606f", -14490));
        root.addChild(EspetroAuiWidgets.centeredText(panelX, panelY + 29, panelW, "\u00a77\u51b7\u5374\u4e0e\u5728\u573a\u6570\u91cf\u5b9e\u65f6\u66f4\u65b0", -2828064));
        root.addChild(EspetroAuiWidgets.rect(panelX + 8, panelY + 46 - 3, panelW - 16, 1, 0x35FFFFFF));
        int listX = panelX + 8;
        int listY = panelY + 46 + 8;
        int listW = panelW - 16;
        ScrollableList list = new ScrollableList(listX, listY, listW, listH).setScrollStep(36).setAlwaysShowScrollbar(true);
        root.addChild(list);
        if (this.vehicles.isEmpty()) {
            list.addChild(this.vehicleButton(0, 0, listW, "\u00a7c\u5f53\u524d\u7f16\u5236\u65e0\u8f7d\u5177\u914d\u7f6e", false, null));
            return;
        }
        int y = 0;
        for (VehicleDeployScreenPacket.VehicleInfo vehicle : this.vehicles) {
            int remaining = this.computeRemainingSeconds(vehicle);
            String label = VehicleDeployScreen.buildVehicleLabel(vehicle, remaining, false);
            EspetroAuiWidgets.ActionButton btn = this.vehicleButton(0, y, listW, label, false, null);
            list.addChild(btn);
            this.rowBindings.add(new RowBinding(btn, vehicle));
            y += 36;
        }
    }

    private void refreshRows() {
        for (RowBinding row : this.rowBindings) {
            int remaining = this.computeRemainingSeconds(row.info);
            row.button.setLabel(VehicleDeployScreen.buildVehicleLabel(row.info, remaining, false));
            row.button.setEnabled(false);
        }
    }

    private int computeRemainingSeconds(VehicleDeployScreenPacket.VehicleInfo vehicle) {
        long remaining = Math.max(0L, vehicle.readyAtEpochMs - System.currentTimeMillis());
        return (int)Math.min(Integer.MAX_VALUE, (remaining + 999L) / 1000L);
    }

    private EspetroAuiWidgets.ActionButton vehicleButton(int x, int y, int width, String label, boolean enabled, Runnable action) {
        return EspetroAuiWidgets.button(x, y, width - 8, 30, label, action).setEnabled(enabled).setColors(-2144322254, -1606725830, -1606725830).setDisabledColor(1883259978).setBorderColor(1884181344).setTextColor(enabled ? -1 : -5327681);
    }

    private static String buildVehicleLabel(VehicleDeployScreenPacket.VehicleInfo vehicle, int remaining, boolean enabled) {
        String status = remaining > 0 ? "\u00a7c\u51b7\u5374 " + remaining + "\u79d2" : (vehicle.current >= vehicle.max ? "\u00a76\u5df2\u6ee1 " + vehicle.current + "/" + vehicle.max : "\u00a7a\u5c31\u7eea " + vehicle.current + "/" + vehicle.max);
        String nameColor = remaining > 0 || vehicle.current >= vehicle.max ? "\u00a78" : "\u00a7e";
        return nameColor + vehicle.displayName + "  " + status + "  \u00a77(" + vehicle.respawnMinutes + "\u5206\u949f\u5237\u65b0)";
    }

    @Override
    public void m_86600_() {
        super.m_86600_();
        if (this.onceEverySecond()) {
            this.refreshRows();
        }
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        EspetroAuiWidgets.drawScreenShade(graphics, this.f_96543_, this.f_96544_);
    }

    @Override
    public boolean m_6913_() {
        return true;
    }

    @Override
    public boolean m_7043_() {
        return false;
    }

    private static final class RowBinding {
        private final EspetroAuiWidgets.ActionButton button;
        private VehicleDeployScreenPacket.VehicleInfo info;

        private RowBinding(EspetroAuiWidgets.ActionButton button, VehicleDeployScreenPacket.VehicleInfo info) {
            this.button = button;
            this.info = info;
        }
    }
}

