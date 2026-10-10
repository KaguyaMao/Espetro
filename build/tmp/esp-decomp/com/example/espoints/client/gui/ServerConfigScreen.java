/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraftforge.common.ForgeConfigSpec$BooleanValue
 *  net.minecraftforge.common.ForgeConfigSpec$DoubleValue
 *  net.minecraftforge.common.ForgeConfigSpec$IntValue
 *  org.espetro.client.aui.GuiElement
 */
package com.example.espoints.client.gui;

import com.example.espoints.client.gui.EspetroMenuScreen;
import com.example.espoints.client.gui.HcrAuiWidgets;
import com.example.espoints.client.gui.ScrollableList;
import com.example.espoints.config.ModConfig;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import org.espetro.client.aui.GuiElement;

public class ServerConfigScreen
extends EspetroMenuScreen {
    private static final Component TITLE = Component.m_237113_((String)"\u670d\u52a1\u7aef\u914d\u7f6e");
    private static final int MARGIN = 18;
    private static final int HEADER_H = 48;
    private static final int ROW_H = 28;
    private static final int SECTION_H = 24;
    private static final int GAP = 6;
    private static final int INPUT_W = 112;
    private static final Pattern INT_PATTERN = Pattern.compile("\\d*");
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("\\d*(\\.\\d*)?");
    private final Screen parentScreen;

    public ServerConfigScreen(Screen parentScreen) {
        super(TITLE);
        this.parentScreen = parentScreen;
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        int pageX = 18;
        int pageY = 18;
        int pageW = Math.max(360, this.f_96543_ - 36);
        int pageH = Math.max(220, this.f_96544_ - 36);
        root.addChild((GuiElement)HcrAuiWidgets.panel(pageX, pageY, pageW, pageH, -535225314, -1604623776));
        this.buildHeader(root, pageX, pageY, pageW);
        int listX = pageX + 18;
        int listY = pageY + 48 + 12;
        int listW = pageW - 36;
        int listH = pageY + pageH - listY - 18;
        ScrollableList list = new ScrollableList(listX, listY, listW, listH).setScrollStep(34).setAlwaysShowScrollbar(true);
        root.addChild((GuiElement)list);
        int y = 0;
        y = this.addSection(list, y, "HUD \u914d\u7f6e");
        y = this.addBooleanRow(list, y, "\u542f\u7528 HUD \u663e\u793a", ModConfig.enableHUD);
        y = this.addBooleanRow(list, y, "\u542f\u7528\u636e\u70b9\u4fe1\u606f\u8f6e\u64ad", ModConfig.enableCarousel);
        y = this.addSection(list, y + 6, "\u961f\u4f0d\u914d\u7f6e");
        y = this.addInfoRow(list, y, "\u961f\u4f0d\u6765\u6e90", "Espetro \u9635\u8425\uff08AAS: \u8fdb\u653b/\u9632\u5b88 \u00b7 RAAS: \u9635\u8425A/B\uff09");
        y = this.addBooleanRow(list, y, "\u663e\u793a\u654c\u6211\u6807\u8bc6", ModConfig.enableTeamIndicator);
        y = this.addSection(list, y + 6, "\u6027\u80fd\u914d\u7f6e");
        y = this.addIntRow(list, y, "\u636e\u70b9\u68c0\u67e5\u95f4\u9694 (tick)", ModConfig.checkInterval, 1, 100);
        y = this.addSection(list, y + 6, "\u5956\u52b1\u914d\u7f6e");
        y = this.addIntRow(list, y, "\u636e\u70b9\u5185\u5956\u52b1\u95f4\u9694 (\u79d2)", ModConfig.pointRewardInterval, 1, 3600);
        y = this.addIntRow(list, y, "\u636e\u70b9\u5185\u6bcf\u6b21\u5956\u52b1\u70b9\u6570", ModConfig.pointRewardAmount, 1, 1000);
        y = this.addIntRow(list, y, "\u51fb\u6740\u73a9\u5bb6\u5956\u52b1\u70b9\u6570", ModConfig.killRewardAmount, 1, 1000);
        y = this.addIntRow(list, y, "\u5360\u9886\u636e\u70b9\u5956\u52b1\u70b9\u6570", ModConfig.captureRewardAmount, 1, 1000);
        y = this.addIntRow(list, y, "\u5360\u9886\u540e\u6301\u7eed\u5956\u52b1\u95f4\u9694 (\u79d2)", ModConfig.capturedRewardInterval, 1, 3600);
        y = this.addIntRow(list, y, "\u5360\u9886\u540e\u6bcf\u6b21\u5956\u52b1\u70b9\u6570", ModConfig.capturedRewardAmount, 1, 1000);
        y = this.addIntRow(list, y, "\u5360\u9886\u540e\u5956\u52b1\u5ef6\u8fdf (\u79d2)", ModConfig.capturedRewardDelay, 1, 3600);
        y = this.addBooleanRow(list, y, "\u542f\u7528\u53cb\u519b\u51fb\u6740\u60e9\u7f5a", ModConfig.enableFriendlyFirePenalty);
        y = this.addIntRow(list, y, "\u53cb\u519b\u51fb\u6740\u6263\u9664\u70b9\u6570", ModConfig.friendlyFirePenalty, 1, 10000);
        y = this.addSection(list, y + 6, "\u884c\u52a8\u653b\u9632\u673a\u5236\u914d\u7f6e");
        y = this.addInfoRow(list, y, "\u884c\u52a8\u6a21\u5f0f", "\u56fa\u5b9a\u542f\u7528");
        this.addDoubleRow(list, y, "\u5175\u529b\u4e0d\u8db3\u9608\u503c (%)", ModConfig.lowReinforcementThreshold, 0.0, 100.0);
    }

    private void buildHeader(GuiElement root, int pageX, int pageY, int pageW) {
        root.addChild((GuiElement)HcrAuiWidgets.text(pageX + 18, pageY + 13, "\u670d\u52a1\u7aef\u914d\u7f6e", -790040));
        root.addChild((GuiElement)HcrAuiWidgets.text(pageX + 18, pageY + 29, "\u670d\u52a1\u5668\u89c4\u5219\u3001\u5956\u52b1\u4e0e\u884c\u52a8\u6a21\u5f0f\u53c2\u6570", -5722440));
        root.addChild((GuiElement)HcrAuiWidgets.button(pageX + pageW - 68, pageY + 14, 50, 18, "\u5b8c\u6210", this::m_7379_).setTextColor(-14490));
        root.addChild((GuiElement)HcrAuiWidgets.rect(pageX + 18, pageY + 48, pageW - 36, 1, 0x35FFFFFF));
    }

    private int addSection(GuiElement parent, int y, String title) {
        parent.addChild((GuiElement)HcrAuiWidgets.rect(0, y + 24 - 3, Math.max(1, parent.getWidth() - 12), 1, 0x35FFFFFF));
        parent.addChild((GuiElement)HcrAuiWidgets.text(0, y + 5, title, -14490));
        return y + 24;
    }

    private int addBooleanRow(GuiElement parent, int y, String label, ForgeConfigSpec.BooleanValue configValue) {
        this.addRowShell(parent, y, label);
        HcrAuiWidgets.ActionButton toggle = HcrAuiWidgets.button(Math.max(0, parent.getWidth() - 112 - 12), y + 4, 112, 20, (Boolean)configValue.get() != false ? "\u662f" : "\u5426", () -> {
            configValue.set((Object)((Boolean)configValue.get() == false ? 1 : 0));
            this.saveConfig();
            this.rebuildMenuRoot();
        }).setSelected((Boolean)configValue.get()).setTextColor((Boolean)configValue.get() != false ? -9054838 : -5722440);
        parent.addChild((GuiElement)toggle);
        return y + 28 + 6;
    }

    private int addInfoRow(GuiElement parent, int y, String label, String value) {
        this.addRowShell(parent, y, label);
        int x = Math.max(0, parent.getWidth() - 112 - 12);
        parent.addChild((GuiElement)HcrAuiWidgets.text(x, y + 9, value, -5722440));
        return y + 28 + 6;
    }

    private int addIntRow(GuiElement parent, int y, String label, ForgeConfigSpec.IntValue configValue, int min, int max) {
        this.addRowShell(parent, y, label);
        parent.addChild((GuiElement)new HcrAuiWidgets.TextInput(Math.max(0, parent.getWidth() - 112 - 12), y + 4, 112, 20, String.valueOf(configValue.get()), 8, INT_PATTERN, value -> {
            if (value.isEmpty()) {
                return;
            }
            try {
                int parsed = Integer.parseInt(value);
                if (parsed >= min && parsed <= max) {
                    configValue.set((Object)parsed);
                    this.saveConfig();
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }));
        return y + 28 + 6;
    }

    private int addDoubleRow(GuiElement parent, int y, String label, ForgeConfigSpec.DoubleValue configValue, double min, double max) {
        this.addRowShell(parent, y, label);
        parent.addChild((GuiElement)new HcrAuiWidgets.TextInput(Math.max(0, parent.getWidth() - 112 - 12), y + 4, 112, 20, String.valueOf(configValue.get()), 8, DOUBLE_PATTERN, value -> {
            if (value.isEmpty()) {
                return;
            }
            try {
                double parsed = Double.parseDouble(value);
                if (parsed >= min && parsed <= max) {
                    configValue.set((Object)parsed);
                    this.saveConfig();
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }));
        return y + 28 + 6;
    }

    private void addRowShell(GuiElement parent, int y, String label) {
        int rowW = Math.max(1, parent.getWidth() - 12);
        parent.addChild((GuiElement)HcrAuiWidgets.rect(0, y, rowW, 28, 0x50404040));
        parent.addChild((GuiElement)HcrAuiWidgets.text(10, y + 9, label, -790040));
    }

    private void saveConfig() {
        ModConfig.SPEC.save();
    }

    public void m_7379_() {
        this.f_96541_.m_91152_(this.parentScreen);
    }

    public boolean m_7043_() {
        return false;
    }
}

