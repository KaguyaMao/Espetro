/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.EspetroMenuScreen;
import org.espetro.network.NetworkManager;

public final class HubScreen
extends EspetroMenuScreen {
    private int onlineCount;
    private String status;
    private EspetroAuiWidgets.Text onlineText;
    private EspetroAuiWidgets.Text statusText;

    public HubScreen(int onlineCount, String status) {
        super(Component.m_237113_("Espetro \u4e3b\u57ce"));
        this.onlineCount = onlineCount;
        this.status = status == null ? "" : status;
    }

    public void updateStatus(int onlineCount, String status) {
        String nextStatus;
        String string = nextStatus = status == null ? "" : status;
        if (this.onlineText != null && this.onlineCount != onlineCount) {
            this.onlineText.setText("\u00a7e\u5728\u7ebf\u4eba\u6570\uff1a\u00a7f" + onlineCount);
        }
        if (this.statusText != null && !Objects.equals(this.status, nextStatus)) {
            this.statusText.setText(nextStatus);
        }
        this.onlineCount = onlineCount;
        this.status = nextStatus;
    }

    @Override
    protected boolean shadeWorld() {
        return false;
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        int panelW = Math.min(260, this.f_96543_ - 24);
        int panelH = 126;
        int x = (this.f_96543_ - panelW) / 2;
        int y = (this.f_96544_ - panelH) / 2;
        root.addChild(EspetroAuiWidgets.panel(x, y, panelW, panelH, -535225314, -10788256));
        root.addChild(EspetroAuiWidgets.centeredText(x + 8, y + 13, panelW - 16, "\u00a76\u00a7lEspetro \u4e3b\u57ce", -14490));
        this.onlineText = EspetroAuiWidgets.centeredText(x + 8, y + 37, panelW - 16, "\u00a7e\u5728\u7ebf\u4eba\u6570\uff1a\u00a7f" + this.onlineCount, -1);
        this.statusText = EspetroAuiWidgets.centeredText(x + 8, y + 52, panelW - 16, this.status, -2828064);
        root.addChild(this.onlineText);
        root.addChild(this.statusText);
        root.addChild(EspetroAuiWidgets.button(x + (panelW - 140) / 2, y + 76, 140, 20, "\u8fdb\u5165\u65b0\u624b\u6559\u7a0b", () -> {
            this.m_7379_();
            NetworkManager.sendTutorialReopen();
        }));
        root.addChild(EspetroAuiWidgets.button(x + (panelW - 140) / 2, y + 101, 140, 20, "\u5173\u95ed", this::m_7379_));
    }

    @Override
    public boolean m_7043_() {
        return false;
    }
}

