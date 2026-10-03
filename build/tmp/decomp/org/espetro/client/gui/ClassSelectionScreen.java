/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.ClassSelectionGui;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.CurrentMapBackgroundRenderer;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.EspetroMenuScreen;
import org.espetro.client.gui.RoleIconResources;
import org.espetro.network.NetworkManager;
import org.espetro.network.OpenClassSelectionPacket;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;

public class ClassSelectionScreen
extends EspetroMenuScreen {
    private final String factionId;
    private final String serverFactionName;
    private final String serverFactionDesc;
    private final String serverFactionIcon;
    private final List<OpenClassSelectionPacket.ClassInfo> serverClasses;
    private FactionDataLoader.FactionData localFaction;
    private FactionDataLoader.ClassKitData[] localClasses;
    private ClassDisplay[] displayClasses;
    private int hoveredClassIndex = -1;
    private final Map<String, Integer> classCounts = new HashMap<String, Integer>();
    private final Map<String, Map<String, Integer>> variantCounts = new HashMap<String, Map<String, Integer>>();
    private final List<EspetroAuiWidgets.ActionButton> classButtons = new ArrayList<EspetroAuiWidgets.ActionButton>();
    private String errorMessage = null;
    private int errorDisplayTime = 0;
    private final int buttonWidth = 150;
    private final int buttonHeight = 24;
    private final int vSpacing = 2;
    private final int columns = 1;
    private int startX;
    private int startY = 55;
    private int popupClassIndex = -1;
    private int popupX;
    private int popupY;
    private int popupH;
    private int popupScroll;
    private int lastMouseX;
    private int lastMouseY;
    private static final int POPUP_W = 180;
    private static final int POPUP_HEADER_H = 18;
    private static final int POPUP_ROW_H = 28;
    private static final int POPUP_MAX_VISIBLE = 6;

    public ClassSelectionScreen(String factionId, String factionName, String factionDescription, String factionIcon, List<OpenClassSelectionPacket.ClassInfo> classes) {
        super(Component.m_237113_("\u9009\u62e9\u804c\u4e1a"));
        this.factionId = factionId;
        this.serverFactionName = factionName;
        this.serverFactionDesc = factionDescription;
        this.serverFactionIcon = factionIcon;
        this.serverClasses = classes;
        this.localFaction = null;
        this.localClasses = null;
    }

    public ClassSelectionScreen(String factionId) {
        super(Component.m_237113_("\u9009\u62e9\u804c\u4e1a"));
        this.factionId = factionId;
        this.serverFactionName = null;
        this.serverFactionDesc = null;
        this.serverFactionIcon = null;
        this.serverClasses = null;
        this.localFaction = null;
        this.localClasses = null;
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        if (this.displayClasses == null) {
            if (this.serverClasses != null && !this.serverClasses.isEmpty()) {
                this.initFromServerData();
            } else {
                this.initFromLocalData();
            }
            NetworkManager.requestClassCounts(this.factionId);
        }
        this.createButtons(root);
    }

    private void initFromServerData() {
        this.displayClasses = new ClassDisplay[this.serverClasses.size()];
        for (int i = 0; i < this.serverClasses.size(); ++i) {
            OpenClassSelectionPacket.ClassInfo ci = this.serverClasses.get(i);
            List<VariantDisplay> variants = ci.variants.stream().map(v -> new VariantDisplay(v.variantId, v.name, v.description, v.maxPlayers)).toList();
            this.displayClasses[i] = new ClassDisplay(ci.classId, ci.name, ci.description, ci.role, ci.icon, ci.maxPlayers, ci.strictCount, ci.troopValue, ci.healthBonus, ci.speedBonus, variants);
            this.classCounts.put(ci.classId, 0);
        }
    }

    private void initFromLocalData() {
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        loader.ensureLoaded(Minecraft.m_91087_().m_91098_());
        this.localFaction = loader.getFaction(this.factionId);
        this.localClasses = loader.getClassesForFaction(this.factionId);
        if (this.localClasses != null && this.localClasses.length > 0) {
            this.displayClasses = new ClassDisplay[this.localClasses.length];
            for (int i = 0; i < this.localClasses.length; ++i) {
                FactionDataLoader.ClassKitData kit = this.localClasses[i];
                List<VariantDisplay> variants = kit.variants.values().stream().map(v -> new VariantDisplay(v.id, v.name, v.description, v.maxPlayers)).toList();
                this.displayClasses[i] = new ClassDisplay(kit.id, kit.name, kit.description, kit.role, kit.icon, kit.maxPlayers, kit.strictCount, kit.troopValue, kit.healthBonus, kit.speedBonus, variants);
                this.classCounts.put(kit.id, 0);
            }
        } else {
            this.displayClasses = new ClassDisplay[0];
        }
    }

    private void createButtons(GuiElement root) {
        this.startX = 10;
        this.startY = 50;
        if (this.displayClasses == null || this.displayClasses.length == 0) {
            return;
        }
        this.classButtons.clear();
        for (int i = 0; i < this.displayClasses.length; ++i) {
            int classIndex = i;
            int col = i % 1;
            int row = i / 1;
            int x = this.startX + col * 150;
            int y = this.startY + row * 26;
            int currentCount = this.classCounts.getOrDefault(this.displayClasses[i].classId, 0);
            boolean full = currentCount >= this.displayClasses[i].maxPlayers;
            String roleColor = full ? "\u00a7c" : this.getRoleColor(this.displayClasses[i].role);
            String buttonText = "    " + roleColor + this.displayClasses[i].name + " \u00a77[" + currentCount + "/" + this.displayClasses[i].maxPlayers + "]";
            EspetroAuiWidgets.ActionButton button = EspetroAuiWidgets.button(x, y, 150, 24, buttonText, () -> this.selectClass(classIndex)).setEnabled(!full);
            root.addChild(button);
            this.classButtons.add(button);
        }
    }

    private void refreshButtons() {
        if (this.displayClasses == null) {
            return;
        }
        for (int i = 0; i < this.displayClasses.length && i < this.classButtons.size(); ++i) {
            ClassDisplay cls = this.displayClasses[i];
            int currentCount = this.classCounts.getOrDefault(cls.classId, 0);
            boolean full = currentCount >= cls.maxPlayers;
            String roleColor = full ? "\u00a7c" : this.getRoleColor(cls.role);
            this.classButtons.get(i).setLabel("    " + roleColor + cls.name + " \u00a77[" + currentCount + "/" + cls.maxPlayers + "]").setEnabled(!full);
        }
    }

    public void updateClassCounts(Map<String, Integer> counts) {
        this.updateClassCounts(counts, null);
    }

    public void updateClassCounts(Map<String, Integer> counts, Map<String, Map<String, Integer>> updatedVariantCounts) {
        boolean variantsUnchanged;
        boolean countsUnchanged = this.classCounts.equals(counts);
        boolean bl = variantsUnchanged = updatedVariantCounts == null || this.variantCounts.equals(updatedVariantCounts);
        if (countsUnchanged && variantsUnchanged) {
            return;
        }
        this.classCounts.clear();
        this.classCounts.putAll(counts);
        if (updatedVariantCounts != null) {
            this.variantCounts.clear();
            for (Map.Entry<String, Map<String, Integer>> entry : updatedVariantCounts.entrySet()) {
                this.variantCounts.put(entry.getKey(), new HashMap<String, Integer>(entry.getValue()));
            }
        }
        this.refreshButtons();
    }

    public void showError(String message) {
        this.errorMessage = message;
        this.errorDisplayTime = 100;
    }

    private String getRoleColor(String role) {
        if (role == null) {
            return "\u00a7e";
        }
        if ((role = role.toLowerCase()).contains("\u7a81\u51fb") || role.contains("\u6218\u6597") || role.contains("\u4e3b\u529b")) {
            return "\u00a7c";
        }
        if (role.contains("\u533b\u7597") || role.contains("\u533b\u62a4")) {
            return "\u00a7a";
        }
        if (role.contains("\u72d9\u51fb") || role.contains("\u8fdc\u7a0b")) {
            return "\u00a79";
        }
        if (role.contains("\u5de5\u7a0b") || role.contains("\u5de5\u5175") || role.contains("\u652f\u63f4")) {
            return "\u00a7e";
        }
        if (role.contains("\u4fa6\u5bdf")) {
            return "\u00a7d";
        }
        if (role.contains("\u8fd0\u8f93") || role.contains("\u62a4\u536b")) {
            return "\u00a76";
        }
        return "\u00a7f";
    }

    private void selectClass(int index) {
        if (index >= 0 && index < this.displayClasses.length && this.displayClasses[index] != null) {
            ClassDisplay cls = this.displayClasses[index];
            int current = this.classCounts.getOrDefault(cls.classId, 0);
            if (current >= cls.maxPlayers) {
                this.showError("\u00a7c" + cls.name + " \u4eba\u6570\u5df2\u6ee1\uff01\u8bf7\u9009\u62e9\u5176\u4ed6\u804c\u4e1a\u3002");
                return;
            }
            if (cls.variants.size() == 1) {
                ClassSelectionGui.selectClass(this.factionId, cls.classId, cls.variants.get((int)0).variantId);
                this.m_7379_();
            } else if (cls.variants.size() > 1) {
                this.openVariantPopup(index, this.lastMouseX, this.lastMouseY);
            }
        }
    }

    private void updateHoveredButton(int mouseX, int mouseY) {
        this.hoveredClassIndex = -1;
        if (this.displayClasses == null) {
            return;
        }
        for (int i = 0; i < this.displayClasses.length; ++i) {
            int col = i % 1;
            int row = i / 1;
            int x = this.startX + col * 150;
            int y = this.startY + row * 26;
            if (mouseX < x || mouseX > x + 150 || mouseY < y || mouseY > y + 24) continue;
            this.hoveredClassIndex = i;
            break;
        }
    }

    private void renderEquipmentPanel(GuiGraphics graphics, ClassDisplay cls) {
        int panelX = this.startX + 150 + 20;
        int panelY = 35;
        int panelWidth = this.f_96543_ - panelX - 10;
        graphics.m_280509_(panelX - 4, panelY - 4, panelX + panelWidth, panelY + 180, 0);
        int lineY = panelY;
        int lineHeight = 11;
        int margin = 8;
        int currentCount = this.classCounts.getOrDefault(cls.classId, 0);
        String countColor = currentCount >= cls.maxPlayers ? "\u00a7c" : "\u00a7a";
        graphics.m_280430_(this.f_96547_, Component.m_237113_("\u00a76\u00a7l" + cls.name + " \u00a77- " + cls.role), panelX + margin, lineY, 0xFFFFFF);
        graphics.m_280430_(this.f_96547_, Component.m_237113_(countColor + "\u4eba\u6570: " + currentCount + "/" + cls.maxPlayers), panelX + margin, lineY += lineHeight + 2, 0xFFFFFF);
        lineY += lineHeight + 6;
        if (cls.description != null && !cls.description.isEmpty()) {
            graphics.m_280430_(this.f_96547_, Component.m_237113_("\u00a77" + cls.description), panelX + margin, lineY, 0xAAAAAA);
            lineY += lineHeight + 3;
        }
        lineY += 3;
        if (cls.healthBonus != 0) {
            graphics.m_280430_(this.f_96547_, Component.m_237113_("\u00a7c\u751f\u547d +" + cls.healthBonus), panelX + margin, lineY, 0xFF8888);
            lineY += lineHeight;
        }
        if (cls.speedBonus != 0.0f) {
            graphics.m_280430_(this.f_96547_, Component.m_237113_("\u00a7b\u901f\u5ea6 +" + String.format("%.1f", Float.valueOf(cls.speedBonus))), panelX + margin, lineY, 0x88CCFF);
        }
    }

    private void renderClassIcons(GuiGraphics graphics) {
        if (this.displayClasses == null) {
            return;
        }
        for (int i = 0; i < this.displayClasses.length && i < this.classButtons.size(); ++i) {
            ResourceLocation icon = this.displayClasses[i].iconResource;
            if (icon == null) continue;
            EspetroAuiWidgets.ActionButton button = this.classButtons.get(i);
            int iconSize = 16;
            graphics.m_280411_(icon, button.getX() + 4, button.getY() + (button.getHeight() - iconSize) / 2, iconSize, iconSize, 0.0f, 0.0f, 128, 128, 128, 128);
        }
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
        CurrentMapBackgroundRenderer.render(graphics, this.f_96543_, this.f_96544_, ClientGameState.getCurrentMapFolder());
        if (!this.hasVariantPopup()) {
            this.updateHoveredButton(mouseX, mouseY);
        }
        if (this.serverFactionName != null) {
            String icon = this.serverFactionIcon != null ? this.serverFactionIcon : "";
            graphics.m_280430_(this.f_96547_, Component.m_237113_("\u00a76\u00a7l" + icon + " " + this.serverFactionName), 10, 8, 0xFFFFFF);
            if (this.serverFactionDesc != null && !this.serverFactionDesc.isEmpty()) {
                graphics.m_280430_(this.f_96547_, Component.m_237113_("\u00a77" + this.serverFactionDesc), 10, 22, 0xAAAAAA);
            }
        } else if (this.localFaction != null) {
            graphics.m_280430_(this.f_96547_, Component.m_237113_("\u00a76\u00a7l" + this.localFaction.icon + " " + this.localFaction.name), 10, 8, 0xFFFFFF);
            graphics.m_280430_(this.f_96547_, Component.m_237113_("\u00a77" + this.localFaction.description), 10, 22, 0xAAAAAA);
        }
        int btRemaining = ClientGameState.getBattleTimeRemaining();
        if (btRemaining > 0) {
            int min = btRemaining / 60;
            int sec = btRemaining % 60;
            String timerColor = btRemaining <= 60 ? "\u00a7c" : (btRemaining <= 300 ? "\u00a7e" : "\u00a7a");
            String timerText = timerColor + "\u00a7l\u5012\u8ba1\u65f6 " + String.format("%02d:%02d", min, sec);
            int timerW = this.f_96547_.m_92895_(timerText);
            graphics.m_280430_(this.f_96547_, Component.m_237113_(timerText), this.f_96543_ - timerW - 10, 8, 0xFFFFFF);
        }
        graphics.m_280430_(this.f_96547_, Component.m_237113_("\u00a7e\u9009\u62e9\u804c\u4e1a \u00a77(\u60ac\u505c\u67e5\u770b\u88c5\u5907)"), 10, 36, 0xFFFFFF);
        if (this.errorMessage != null && this.errorDisplayTime > 0) {
            graphics.m_280653_(this.f_96547_, Component.m_237113_(this.errorMessage), this.f_96543_ / 2, this.f_96544_ / 2, 0xFF5555);
            --this.errorDisplayTime;
        }
        if (this.hoveredClassIndex >= 0 && this.displayClasses != null && this.hoveredClassIndex < this.displayClasses.length) {
            this.renderEquipmentPanel(graphics, this.displayClasses[this.hoveredClassIndex]);
        }
    }

    @Override
    protected void renderAfterMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderClassIcons(graphics);
        this.renderVariantPopup(graphics, mouseX, mouseY);
    }

    private void renderVariantPopup(GuiGraphics graphics, int mouseX, int mouseY) {
        int variantIndex;
        if (!this.hasVariantPopup()) {
            return;
        }
        ClassDisplay cls = this.displayClasses[this.popupClassIndex];
        int visible = Math.min(6, cls.variants.size());
        graphics.m_280509_(this.popupX, this.popupY, this.popupX + 180, this.popupY + this.popupH, -267316200);
        graphics.m_280637_(this.popupX, this.popupY, 180, this.popupH, -1525668);
        graphics.m_280614_(this.f_96547_, Component.m_237113_("\u00a76\u00a7l" + cls.name + " \u00a77\u88c5\u5907\u53d8\u4f53"), this.popupX + 6, this.popupY + 5, 0xFFFFFF, false);
        int closeX = this.popupX + 180 - 16;
        int closeY = this.popupY + 2;
        boolean closeHovered = ClassSelectionScreen.inside(mouseX, mouseY, closeX, closeY, 13, 13);
        graphics.m_280509_(closeX, closeY, closeX + 13, closeY + 13, closeHovered ? -3122598 : -11193035);
        graphics.m_280653_(this.f_96547_, Component.m_237113_("\u00a7fX"), closeX + 6, closeY + 2, 0xFFFFFF);
        for (int row = 0; row < visible && (variantIndex = this.popupScroll + row) < cls.variants.size(); ++row) {
            VariantDisplay variant = cls.variants.get(variantIndex);
            int rowX = this.popupX + 3;
            int rowY = this.popupY + 18 + row * 28;
            int rowW = 174;
            int count = this.variantCounts.getOrDefault(cls.classId, Collections.emptyMap()).getOrDefault(variant.variantId(), 0);
            boolean full = cls.strictCount && count >= variant.maxPlayers();
            boolean hovered = ClassSelectionScreen.inside(mouseX, mouseY, rowX, rowY, rowW, 27);
            graphics.m_280509_(rowX, rowY, rowX + rowW, rowY + 28 - 1, full ? -802480091 : (hovered ? -532459195 : -803529184));
            graphics.m_280637_(rowX, rowY, rowW, 27, hovered && !full ? -4732488 : -2141626274);
            String countText = cls.strictCount ? (full ? "\u00a7c" : "\u00a7a") + "[" + count + "/" + variant.maxPlayers() + "]" : "\u00a7a" + count + "\u4eba";
            graphics.m_280614_(this.f_96547_, Component.m_237113_((full ? "\u00a78" : "\u00a7f") + variant.name()), rowX + 5, rowY + 4, 0xFFFFFF, false);
            int countW = this.f_96547_.m_92895_(EspetroAuiWidgets.stripFormatting(countText));
            graphics.m_280614_(this.f_96547_, Component.m_237113_(countText), rowX + rowW - countW - 5, rowY + 4, 0xFFFFFF, false);
            if (variant.description() == null || variant.description().isBlank()) continue;
            graphics.m_280614_(this.f_96547_, Component.m_237113_("\u00a77" + EspetroAuiWidgets.trimToWidth(variant.description(), rowW - 10)), rowX + 5, rowY + 16, -5327681, false);
        }
        if (cls.variants.size() > 6) {
            graphics.m_280614_(this.f_96547_, Component.m_237113_("\u00a78\u6eda\u8f6e\u6d4f\u89c8"), this.popupX + 180 - 49, this.popupY + this.popupH - 10, -5327681, false);
        }
    }

    @Override
    public boolean m_6375_(double mouseX, double mouseY, int button) {
        this.lastMouseX = (int)mouseX;
        this.lastMouseY = (int)mouseY;
        if (this.hasVariantPopup()) {
            if (button == 0) {
                this.handleVariantPopupClick((int)mouseX, (int)mouseY);
            } else {
                this.closeVariantPopup();
            }
            return true;
        }
        return super.m_6375_(mouseX, mouseY, button);
    }

    @Override
    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        if (this.hasVariantPopup() && ClassSelectionScreen.inside((int)mouseX, (int)mouseY, this.popupX, this.popupY, 180, this.popupH)) {
            int maxScroll = Math.max(0, this.displayClasses[this.popupClassIndex].variants.size() - 6);
            this.popupScroll = Math.max(0, Math.min(maxScroll, this.popupScroll + (delta < 0.0 ? 1 : -1)));
            return true;
        }
        return super.m_6050_(mouseX, mouseY, delta);
    }

    @Override
    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && this.hasVariantPopup()) {
            this.closeVariantPopup();
            return true;
        }
        return super.m_7933_(keyCode, scanCode, modifiers);
    }

    private void openVariantPopup(int classIndex, int mouseX, int mouseY) {
        this.popupClassIndex = classIndex;
        this.popupScroll = 0;
        int count = this.displayClasses[classIndex].variants.size();
        int visible = Math.min(6, count);
        int footer = count > 6 ? 10 : 3;
        this.popupH = 18 + visible * 28 + footer;
        this.popupX = mouseX + 9;
        if (this.popupX + 180 > this.f_96543_ - 3) {
            this.popupX = mouseX - 180 - 9;
        }
        this.popupX = Math.max(3, Math.min(this.f_96543_ - 180 - 3, this.popupX));
        this.popupY = Math.max(3, Math.min(this.f_96544_ - this.popupH - 3, mouseY + 7));
    }

    private void handleVariantPopupClick(int mouseX, int mouseY) {
        int variantIndex;
        int closeX = this.popupX + 180 - 16;
        int closeY = this.popupY + 2;
        if (ClassSelectionScreen.inside(mouseX, mouseY, closeX, closeY, 13, 13) || !ClassSelectionScreen.inside(mouseX, mouseY, this.popupX, this.popupY, 180, this.popupH)) {
            this.closeVariantPopup();
            return;
        }
        ClassDisplay cls = this.displayClasses[this.popupClassIndex];
        int row = (mouseY - (this.popupY + 18)) / 28;
        if (mouseY >= this.popupY + 18 && row >= 0 && row < 6 && (variantIndex = this.popupScroll + row) < cls.variants.size()) {
            VariantDisplay variant = cls.variants.get(variantIndex);
            int count = this.variantCounts.getOrDefault(cls.classId, Collections.emptyMap()).getOrDefault(variant.variantId(), 0);
            if (!cls.strictCount || count < variant.maxPlayers()) {
                ClassSelectionGui.selectClass(this.factionId, cls.classId, variant.variantId());
                this.closeVariantPopup();
                this.m_7379_();
            }
        }
    }

    private boolean hasVariantPopup() {
        return this.displayClasses != null && this.popupClassIndex >= 0 && this.popupClassIndex < this.displayClasses.length;
    }

    private void closeVariantPopup() {
        this.popupClassIndex = -1;
        this.popupScroll = 0;
    }

    private static boolean inside(int x, int y, int left, int top, int width, int height) {
        return x >= left && x < left + width && y >= top && y < top + height;
    }

    @Override
    public boolean m_7043_() {
        return false;
    }

    private static class ClassDisplay {
        final String classId;
        final String name;
        final String description;
        final String role;
        final String icon;
        final ResourceLocation iconResource;
        final int maxPlayers;
        final boolean strictCount;
        final int troopValue;
        final int healthBonus;
        final float speedBonus;
        final List<VariantDisplay> variants;

        ClassDisplay(String classId, String name, String description, String role, String icon, int maxPlayers, boolean strictCount, int troopValue, int healthBonus, float speedBonus, List<VariantDisplay> variants) {
            this.classId = classId;
            this.name = name;
            this.description = description;
            this.role = role;
            this.icon = icon;
            this.iconResource = RoleIconResources.resolve(icon);
            this.maxPlayers = maxPlayers;
            this.strictCount = strictCount;
            this.troopValue = troopValue;
            this.healthBonus = healthBonus;
            this.speedBonus = speedBonus;
            this.variants = variants;
        }
    }

    private record VariantDisplay(String variantId, String name, String description, int maxPlayers) {
    }
}

