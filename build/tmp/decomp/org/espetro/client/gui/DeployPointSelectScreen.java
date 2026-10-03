/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.EspetroMenuScreen;
import org.espetro.network.DeployPointSelectPacket;

public class DeployPointSelectScreen
extends EspetroMenuScreen {
    private final boolean hasDeployPoint;
    private final String deployPointPos;
    private final List<DeployPointSelectPacket.BastionItem> bastions;
    private static final int BUTTON_WIDTH = 260;
    private static final int BUTTON_HEIGHT = 26;
    private static final int VERTICAL_SPACING = 5;
    private static final int START_Y = 55;
    private int startX;

    public DeployPointSelectScreen(boolean hasDeployPoint, String deployPointPos, List<DeployPointSelectPacket.BastionItem> bastions) {
        super(Component.m_237113_("\u9009\u62e9\u590d\u6d3b\u70b9"));
        this.hasDeployPoint = hasDeployPoint;
        this.deployPointPos = deployPointPos;
        this.bastions = bastions != null ? bastions : new ArrayList();
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        this.startX = (this.f_96543_ - 260) / 2;
        EspetroAuiWidgets.addPhaseHeader(root, this.f_96543_, "\u00a76\u00a7l\u9009\u62e9\u590d\u6d3b\u4f4d\u7f6e", "\u00a7e\u8bf7\u9009\u62e9\u4f60\u8981\u590d\u6d3b\u7684\u4f4d\u7f6e", "", -14490);
        int y = 55;
        if (this.hasDeployPoint) {
            String label = "\u00a7e\u539f\u90e8\u7f72\u70b9 \u00a77(" + this.deployPointPos + ")";
            root.addChild(EspetroAuiWidgets.button(this.startX, y, 260, 26, label, () -> {
                if (Minecraft.m_91087_().f_91074_ != null) {
                    Minecraft.m_91087_().f_91074_.f_108617_.m_246623_("bastion deploy");
                }
            }));
            y += 31;
        }
        for (DeployPointSelectPacket.BastionItem b : this.bastions) {
            String label = "\u00a7a" + b.name + " \u00a77(" + b.pos + ")";
            root.addChild(EspetroAuiWidgets.button(this.startX, y, 260, 26, label, () -> {
                if (Minecraft.m_91087_().f_91074_ != null) {
                    Minecraft.m_91087_().f_91074_.f_108617_.m_246623_("bastion select " + b.id.toString());
                }
            }));
            y += 31;
        }
        if (!this.hasDeployPoint && this.bastions.isEmpty()) {
            root.addChild(EspetroAuiWidgets.button(this.startX, y, 260, 30, "\u00a7c\u6ca1\u6709\u53ef\u7528\u7684\u590d\u6d3b\u70b9\uff01", null).setEnabled(false));
        }
    }

    @Override
    public boolean m_6913_() {
        return false;
    }

    @Override
    public void m_7379_() {
    }

    @Override
    public boolean m_7043_() {
        return false;
    }
}

