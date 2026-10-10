/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  org.espetro.client.aui.GuiElement
 */
package com.example.espoints.client.gui;

import com.example.espoints.client.gui.EspetroMenuScreen;
import com.example.espoints.client.gui.HcrAuiWidgets;
import com.example.espoints.config.MapImageQuality;
import com.example.espoints.config.TacticalMapConfig;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.GuiElement;

public class TacticalMapConfigScreen
extends EspetroMenuScreen {
    private static final Component TITLE = Component.m_237113_((String)"\u6218\u672f\u5730\u56fe\u914d\u7f6e");
    private static final Pattern HEX_PATTERN = Pattern.compile("#?[0-9A-Fa-f]*");
    private static final Pattern VALID_HEX_PATTERN = Pattern.compile("#?[0-9A-Fa-f]{3}([0-9A-Fa-f]{3})?");
    private final Screen parent;
    private final Map<MapImageQuality, HcrAuiWidgets.ActionButton> qualityButtons = new EnumMap<MapImageQuality, HcrAuiWidgets.ActionButton>(MapImageQuality.class);
    private HcrAuiWidgets.TextInput attackerColorInput;
    private HcrAuiWidgets.TextInput defenderColorInput;

    public TacticalMapConfigScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        int panelW = Math.min(420, Math.max(320, this.f_96543_ - 36));
        int panelH = 232;
        int panelX = (this.f_96543_ - panelW) / 2;
        int panelY = Math.max(18, (this.f_96544_ - panelH) / 2);
        root.addChild((GuiElement)HcrAuiWidgets.panel(panelX, panelY, panelW, panelH, -535225314, -1604623776));
        root.addChild((GuiElement)HcrAuiWidgets.text(panelX + 18, panelY + 14, "\u6218\u672f\u5730\u56fe\u914d\u7f6e", -790040));
        root.addChild((GuiElement)HcrAuiWidgets.text(panelX + 18, panelY + 30, "\u56fe\u50cf\u8d28\u91cf\u5373\u65f6\u751f\u6548\uff1b\u8fdb\u5ea6\u6761\u989c\u8272\u4f7f\u7528\u5341\u516d\u8fdb\u5236 RGB", -5722440));
        root.addChild((GuiElement)HcrAuiWidgets.rect(panelX + 18, panelY + 50, panelW - 36, 1, 0x35FFFFFF));
        int labelX = panelX + 24;
        int inputX = panelX + panelW - 146;
        this.addQualityRow(root, labelX, panelY + 65, panelW);
        this.addColorRow(root, labelX, inputX, panelY + 112, "\u653b\u65b9\u8fdb\u5ea6\u6761\u989c\u8272", true);
        this.addColorRow(root, labelX, inputX, panelY + 148, "\u5b88\u65b9\u8fdb\u5ea6\u6761\u989c\u8272", false);
        root.addChild((GuiElement)HcrAuiWidgets.button(panelX + panelW - 156, panelY + panelH - 32, 64, 18, "\u4fdd\u5b58", () -> {
            this.saveConfig();
            this.m_7379_();
        }).setTextColor(-14490));
        root.addChild((GuiElement)HcrAuiWidgets.button(panelX + panelW - 84, panelY + panelH - 32, 58, 18, "\u53d6\u6d88", this::m_7379_).setTextColor(-5722440));
    }

    private void addQualityRow(GuiElement root, int labelX, int y, int panelW) {
        root.addChild((GuiElement)HcrAuiWidgets.text(labelX, y + 5, "\u5730\u56fe\u56fe\u50cf\u8d28\u91cf", -790040));
        int buttonWidth = 58;
        int gap = 6;
        int buttonsWidth = buttonWidth * MapImageQuality.values().length + gap * (MapImageQuality.values().length - 1);
        int buttonX = Math.max(labelX + 92, (this.f_96543_ - panelW) / 2 + panelW - 24 - buttonsWidth);
        this.qualityButtons.clear();
        for (MapImageQuality quality : MapImageQuality.values()) {
            HcrAuiWidgets.ActionButton button = HcrAuiWidgets.button(buttonX, y, buttonWidth, 20, quality.displayName(), () -> this.selectQuality(quality));
            button.setSelected(TacticalMapConfig.mapImageQuality.get() == quality);
            root.addChild((GuiElement)button);
            this.qualityButtons.put(quality, button);
            buttonX += buttonWidth + gap;
        }
        root.addChild((GuiElement)HcrAuiWidgets.text(labelX, y + 26, "\u7a33\u5b9a 250 ms \u540e\u6e10\u8fdb\u52a0\u8f7d\u66f4\u6e05\u6670\u74e6\u7247", -9209465));
    }

    private void selectQuality(MapImageQuality quality) {
        TacticalMapConfig.mapImageQuality.set((Object)quality);
        TacticalMapConfig.SPEC.save();
        this.qualityButtons.forEach((value, button) -> button.setSelected(value == quality));
    }

    private void addColorRow(GuiElement root, int labelX, int inputX, int y, String label, boolean attacker) {
        String current = attacker ? (String)TacticalMapConfig.attackerProgressBarColor.get() : (String)TacticalMapConfig.defenderProgressBarColor.get();
        int swatchColor = this.parseColor(current, attacker ? -43776 : -16755201);
        root.addChild((GuiElement)HcrAuiWidgets.rect(labelX, y + 5, 10, 10, swatchColor));
        root.addChild((GuiElement)HcrAuiWidgets.text(labelX + 18, y + 5, label, -790040));
        HcrAuiWidgets.TextInput input = new HcrAuiWidgets.TextInput(inputX, y, 120, 20, current, 7, HEX_PATTERN, value -> {});
        root.addChild((GuiElement)input);
        if (attacker) {
            this.attackerColorInput = input;
        } else {
            this.defenderColorInput = input;
        }
    }

    private void saveConfig() {
        String defenderColor;
        String attackerColor = this.normalizeColor(this.attackerColorInput == null ? "" : this.attackerColorInput.getValue());
        if (attackerColor != null) {
            TacticalMapConfig.attackerProgressBarColor.set((Object)attackerColor);
        }
        if ((defenderColor = this.normalizeColor(this.defenderColorInput == null ? "" : this.defenderColorInput.getValue())) != null) {
            TacticalMapConfig.defenderProgressBarColor.set((Object)defenderColor);
        }
        TacticalMapConfig.SPEC.save();
    }

    private String normalizeColor(String colorInput) {
        if (colorInput == null || !VALID_HEX_PATTERN.matcher(colorInput).matches()) {
            return null;
        }
        return colorInput.startsWith("#") ? colorInput : "#" + colorInput;
    }

    private int parseColor(String value, int fallback) {
        String normalized = this.normalizeColor(value);
        if (normalized == null) {
            return fallback;
        }
        try {
            return 0xFF000000 | Integer.parseInt(normalized.substring(1), 16);
        }
        catch (NumberFormatException e) {
            return fallback;
        }
    }

    public void m_7379_() {
        this.f_96541_.m_91152_(this.parent);
    }

    public boolean m_7043_() {
        return false;
    }
}

