package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.network.NetworkManager;
import org.espetro.network.OpenClassSelectionPacket;
import org.lwjgl.glfw.GLFW;
import org.espetro.client.aui.GuiElement;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 职业选择界面
 * 支持两种数据来源：
 * 1. 服务端通过网络包发送的完整数据（局域网联机客户端）
 * 2. 本地 FactionDataLoader 加载（J键手动打开时的回退）
 */
public class ClassSelectionScreen extends EspetroMenuScreen {

    private final String factionId;
    // 服务端发来的数据（优先使用）
    private final String serverFactionName;
    private final String serverFactionDesc;
    private final String serverFactionIcon;
    private final List<OpenClassSelectionPacket.ClassInfo> serverClasses;

    // 从本地加载的数据（回退）
    private org.espetro.team.FactionDataLoader.FactionData localFaction;
    private org.espetro.team.FactionDataLoader.ClassKitData[] localClasses;

    // 统一渲染用的职业列表
    private ClassDisplay[] displayClasses;
    private int hoveredClassIndex = -1;

    private final Map<String, Integer> classCounts = new HashMap<>();
    private final Map<String, Map<String, Integer>> variantCounts = new HashMap<>();
    private final List<EspetroAuiWidgets.ActionButton> classButtons = new java.util.ArrayList<>();

    // 错误消息
    private String errorMessage = null;
    private int errorDisplayTime = 0;
    // 按钮布局参数
    private final int buttonWidth = 150;
    private final int buttonHeight = 24;
    private final int vSpacing = 2;
    private final int columns = 1;
    private int startX;
    private int startY = 55;
    private int popupClassIndex = -1;
    private int popupX, popupY, popupH, popupScroll;
    private int lastMouseX, lastMouseY;
    private static final int POPUP_W = 180;
    private static final int POPUP_HEADER_H = 18;
    private static final int POPUP_ROW_H = 28;
    private static final int POPUP_MAX_VISIBLE = 6;

    /** 服务端数据构造函数 —— 局域网联机时使用 */
    public ClassSelectionScreen(String factionId, String factionName, String factionDescription,
                                 String factionIcon, List<OpenClassSelectionPacket.ClassInfo> classes) {
        super(Component.literal("选择职业"));
        this.factionId = factionId;
        this.serverFactionName = factionName;
        this.serverFactionDesc = factionDescription;
        this.serverFactionIcon = factionIcon;
        this.serverClasses = classes;
        this.localFaction = null;
        this.localClasses = null;
    }

    /** 本地数据构造函数 —— J键手动打开时使用 */
    public ClassSelectionScreen(String factionId) {
        super(Component.literal("选择职业"));
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
        if (displayClasses == null) {
            if (serverClasses != null && !serverClasses.isEmpty()) {
                // 使用服务端数据
                initFromServerData();
            } else {
                // 回退：从本地加载
                initFromLocalData();
            }
            NetworkManager.requestClassCounts(factionId);
        }
        createButtons(root);
    }

    private void initFromServerData() {
        displayClasses = new ClassDisplay[serverClasses.size()];
        for (int i = 0; i < serverClasses.size(); i++) {
            OpenClassSelectionPacket.ClassInfo ci = serverClasses.get(i);
            List<VariantDisplay> variants = ci.variants.stream()
                .map(v -> new VariantDisplay(v.variantId, v.name, v.description, v.maxPlayers))
                .toList();
            displayClasses[i] = new ClassDisplay(ci.classId, ci.name, ci.description, ci.role, ci.icon,
                ci.maxPlayers, ci.strictCount, ci.troopValue, ci.healthBonus, ci.speedBonus, variants);
            classCounts.put(ci.classId, 0);
        }

    }

    private void initFromLocalData() {
        org.espetro.team.FactionDataLoader loader = org.espetro.team.FactionDataProvider.getOrCreateLoader();
        loader.ensureLoaded(Minecraft.getInstance().getResourceManager());

        localFaction = loader.getFaction(factionId);
        localClasses = loader.getClassesForFaction(factionId);

        if (localClasses != null && localClasses.length > 0) {
            displayClasses = new ClassDisplay[localClasses.length];
            for (int i = 0; i < localClasses.length; i++) {
                org.espetro.team.FactionDataLoader.ClassKitData kit = localClasses[i];
                List<VariantDisplay> variants = kit.variants.values().stream()
                    .map(v -> new VariantDisplay(v.id, v.name, v.description, v.maxPlayers))
                    .toList();
                displayClasses[i] = new ClassDisplay(kit.id, kit.name, kit.description, kit.role, kit.icon,
                    kit.maxPlayers, kit.strictCount, kit.troopValue, kit.healthBonus, kit.speedBonus, variants);
                classCounts.put(kit.id, 0);
            }
        } else {
            displayClasses = new ClassDisplay[0];
        }

    }

    private void createButtons(GuiElement root) {
        startX = 10;
        startY = 50;

        if (displayClasses == null || displayClasses.length == 0) return;

        classButtons.clear();
        for (int i = 0; i < displayClasses.length; i++) {
            final int classIndex = i;

            int col = i % columns;
            int row = i / columns;
            int x = startX + col * buttonWidth;
            int y = startY + row * (buttonHeight + vSpacing);

            int currentCount = classCounts.getOrDefault(displayClasses[i].classId, 0);
            boolean full = currentCount >= displayClasses[i].maxPlayers;
            String buttonText =
                "    " + displayClasses[i].name + "  " + currentCount
                    + "/" + displayClasses[i].maxPlayers;
            EspetroAuiWidgets.ActionButton button = EspetroAuiWidgets.button(
                x, y, buttonWidth, buttonHeight, buttonText, () -> selectClass(classIndex));
            button.setTextColor(full ? UiTheme.NEGATIVE
                : getRoleAccent(displayClasses[i].role));
            button.setEnabled(!full);
            root.addChild(button);
            classButtons.add(button);
        }
    }

    private void refreshButtons() {
        if (displayClasses == null) return;
        for (int i = 0; i < displayClasses.length && i < classButtons.size(); i++) {
            ClassDisplay cls = displayClasses[i];
            int currentCount = classCounts.getOrDefault(cls.classId, 0);
            boolean full = currentCount >= cls.maxPlayers;
            String label = "    " + cls.name + "  " + currentCount + "/" + cls.maxPlayers;
            classButtons.get(i).setLabel(label)
                .setEnabled(!full);
            classButtons.get(i).setTextColor(full ? UiTheme.NEGATIVE
                : getRoleAccent(cls.role));
        }
    }

    /**
     * 从服务器更新职业人数
     */
    public void updateClassCounts(Map<String, Integer> counts) {
        updateClassCounts(counts, null);
    }

    public void updateClassCounts(Map<String, Integer> counts,
                                  Map<String, Map<String, Integer>> updatedVariantCounts) {
        // 数据未变早退：避免每包都全量刷按钮标签
        boolean countsUnchanged = this.classCounts.equals(counts);
        boolean variantsUnchanged = updatedVariantCounts == null
            || this.variantCounts.equals(updatedVariantCounts);
        if (countsUnchanged && variantsUnchanged) {
            return;
        }
        this.classCounts.clear();
        this.classCounts.putAll(counts);
        if (updatedVariantCounts != null) {
            this.variantCounts.clear();
            for (Map.Entry<String, Map<String, Integer>> entry : updatedVariantCounts.entrySet()) {
                this.variantCounts.put(entry.getKey(), new HashMap<>(entry.getValue()));
            }
        }
        refreshButtons();
    }

    /**
     * 显示错误消息
     */
    public void showError(String message) {
        this.errorMessage = message;
        this.errorDisplayTime = 100;
    }

    /** 职业定位 → 语义色：突击=ATTACK、医疗=POSITIVE、狙击=DEFEND、工程=WARNING、侦察=PURPLE、运输=GOLD。 */
    private int getRoleAccent(String role) {
        if (role == null) return UiTheme.WARNING;
        role = role.toLowerCase();
        if (role.contains("突击") || role.contains("战斗") || role.contains("主力")) {
            return UiTheme.ATTACK;
        } else if (role.contains("医疗") || role.contains("医护")) {
            return UiTheme.POSITIVE;
        } else if (role.contains("狙击") || role.contains("远程")) {
            return UiTheme.DEFEND;
        } else if (role.contains("工程") || role.contains("工兵") || role.contains("支援")) {
            return UiTheme.WARNING;
        } else if (role.contains("侦察")) {
            return UiTheme.PURPLE;
        } else if (role.contains("运输") || role.contains("护卫")) {
            return UiTheme.GOLD;
        }
        return UiTheme.TEXT;
    }

    private void selectClass(int index) {
        if (index >= 0 && index < displayClasses.length && displayClasses[index] != null) {
            ClassDisplay cls = displayClasses[index];

            int current = classCounts.getOrDefault(cls.classId, 0);
            if (current >= cls.maxPlayers) {
                showError(cls.name + " 人数已满！请选择其他职业。");
                return;
            }

            if (cls.variants.size() == 1) {
                ClassSelectionGui.selectClass(factionId, cls.classId, cls.variants.get(0).variantId);
                this.onClose();
            } else if (cls.variants.size() > 1) {
                openVariantPopup(index, lastMouseX, lastMouseY);
            }
        }
    }

    private void updateHoveredButton(int mouseX, int mouseY) {
        hoveredClassIndex = -1;
        if (displayClasses == null) return;
        for (int i = 0; i < displayClasses.length; i++) {
            int col = i % columns;
            int row = i / columns;
            int x = startX + col * buttonWidth;
            int y = startY + row * (buttonHeight + vSpacing);

            if (mouseX >= x && mouseX <= x + buttonWidth && mouseY >= y && mouseY <= y + buttonHeight) {
                hoveredClassIndex = i;
                break;
            }
        }
    }

    private void renderEquipmentPanel(GuiGraphics graphics, ClassDisplay cls) {
        int panelX = startX + buttonWidth + 20;
        int panelY = 35;
        int panelWidth = Math.max(24, this.width - panelX - 10);

        // 右侧装备详情：玻璃卡片（阴影 + 渐变 + 极细描边）
        UiPaint.card(graphics, panelX - 8, panelY - 8, panelWidth + 16, 124);

        int pad = 8;
        int lineY = panelY + 4;

        int currentCount = classCounts.getOrDefault(cls.classId, 0);
        int countColor = currentCount >= cls.maxPlayers ? UiTheme.NEGATIVE : UiTheme.POSITIVE;
        String name = cls.name == null ? "" : cls.name;

        // 职业名（标题）+ 角色（次要信息）
        EspetroAuiWidgets.drawScaledString(graphics, name,
            panelX + pad, lineY, UiTheme.GOLD, UiTheme.TYPE_HEADING, true);
        int nameW = (int) (this.font.width(EspetroAuiWidgets.stripFormatting(name))
            * UiTheme.TYPE_HEADING);
        if (cls.role != null && !cls.role.isEmpty()) {
            EspetroAuiWidgets.drawScaledString(graphics, "· " + cls.role,
                panelX + pad + nameW + 4, lineY + 2, UiTheme.TEXT_SECOND, 0.9f);
        }
        lineY += 17;

        // 人数：micro 标签 + 大号数字
        EspetroAuiWidgets.drawScaledString(graphics, "人数", panelX + pad, lineY + 3,
            UiTheme.TEXT_THIRD, UiTheme.TYPE_MICRO);
        EspetroAuiWidgets.drawScaledString(graphics, currentCount + " / " + cls.maxPlayers,
            panelX + pad + 24, lineY, countColor, UiTheme.TYPE_HEADING, true);
        lineY += 19;

        if (cls.description != null && !cls.description.isEmpty()) {
            EspetroAuiWidgets.drawScaledString(graphics,
                EspetroAuiWidgets.trimToWidth(cls.description, panelWidth - pad * 2),
                panelX + pad, lineY, UiTheme.TEXT_SECOND, 0.9f);
            lineY += 14;
        }

        UiPaint.divider(graphics, panelX + pad, lineY + 1, panelWidth - pad * 2);
        lineY += 10;

        EspetroAuiWidgets.drawScaledString(graphics, "属性", panelX + pad, lineY,
            UiTheme.TEXT_THIRD, UiTheme.TYPE_MICRO);
        lineY += 12;
        if (cls.healthBonus != 0) {
            EspetroAuiWidgets.drawScaledString(graphics, "生命 +" + cls.healthBonus,
                panelX + pad, lineY, UiTheme.ATTACK, 0.95f);
            lineY += 11;
        }
        if (cls.speedBonus != 0) {
            EspetroAuiWidgets.drawScaledString(graphics, "速度 +" + String.format("%.1f", cls.speedBonus),
                panelX + pad, lineY, UiTheme.DEFEND, 0.95f);
        }
    }

    private void renderClassIcons(GuiGraphics graphics) {
        if (displayClasses == null) return;
        for (int i = 0; i < displayClasses.length && i < classButtons.size(); i++) {
            ResourceLocation icon = displayClasses[i].iconResource;
            if (icon == null) continue;
            EspetroAuiWidgets.ActionButton button = classButtons.get(i);
            int iconSize = 16;
            graphics.blit(icon, button.getX() + 4,
                button.getY() + (button.getHeight() - iconSize) / 2,
                iconSize, iconSize, 0.0f, 0.0f,
                RoleIconResources.TEXTURE_SIZE, RoleIconResources.TEXTURE_SIZE,
                RoleIconResources.TEXTURE_SIZE, RoleIconResources.TEXTURE_SIZE);
        }
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        CurrentMapBackgroundRenderer.render(
            graphics, this.width, this.height, ClientGameState.getCurrentMapFolder());
        if (!hasVariantPopup()) {
            updateHoveredButton(mouseX, mouseY);
        }

        // 阵营标题
        if (serverFactionName != null) {
            // 服务端数据
            String icon = serverFactionIcon != null ? serverFactionIcon : "";
            EspetroAuiWidgets.drawScaledString(graphics,
                (icon.isEmpty() ? "" : icon + " ") + serverFactionName,
                10, 7, UiTheme.GOLD, UiTheme.TYPE_HEADING, true);
            if (serverFactionDesc != null && !serverFactionDesc.isEmpty()) {
                EspetroAuiWidgets.drawScaledString(graphics,
                    EspetroAuiWidgets.trimToWidth(serverFactionDesc, Math.max(80, this.width / 2 - 20)),
                    10, 22, UiTheme.TEXT_SECOND, 0.9f);
            }
        } else if (localFaction != null) {
            // 本地数据
            String icon = localFaction.icon == null ? "" : localFaction.icon;
            EspetroAuiWidgets.drawScaledString(graphics,
                (icon.isEmpty() ? "" : icon + " ") + localFaction.name,
                10, 7, UiTheme.GOLD, UiTheme.TYPE_HEADING, true);
            if (localFaction.description != null && !localFaction.description.isEmpty()) {
                EspetroAuiWidgets.drawScaledString(graphics,
                    EspetroAuiWidgets.trimToWidth(localFaction.description, Math.max(80, this.width / 2 - 20)),
                    10, 22, UiTheme.TEXT_SECOND, 0.9f);
            }
        }

        // 战局倒计时（右上角）
        int btRemaining = ClientGameState.getBattleTimeRemaining();
        if (btRemaining > 0) {
            int min = btRemaining / 60;
            int sec = btRemaining % 60;
            int timerColor = btRemaining <= 60 ? UiTheme.NEGATIVE
                : btRemaining <= 300 ? UiTheme.WARNING : UiTheme.POSITIVE;
            String timerText = String.format("倒计时 %02d:%02d", min, sec);
            int timerW = (int) (this.font.width(timerText) * 0.95f);
            EspetroAuiWidgets.drawScaledString(graphics, timerText,
                this.width - timerW - 10, 8, timerColor, 0.95f, true);
        }

        EspetroAuiWidgets.drawScaledString(graphics, "选择职业（悬停查看装备）",
            10, 36, UiTheme.TEXT_SECOND, 0.9f);

        // 错误消息：胶囊底 + 语义红，居中于屏幕
        if (errorMessage != null && errorDisplayTime > 0) {
            String error = EspetroAuiWidgets.stripFormatting(errorMessage);
            int errorW = this.font.width(error) + 16;
            int errorX = this.width / 2 - errorW / 2;
            int errorY = this.height / 2 - 8;
            UiPaint.roundRect(graphics, errorX, errorY, errorW, 16, 8, UiTheme.NEGATIVE_SOFT);
            UiPaint.strokeRoundRect(graphics, errorX, errorY, errorW, 16, 8,
                UiTheme.alpha(UiTheme.NEGATIVE, 0.55f));
            graphics.drawCenteredString(this.font, Component.literal(error),
                this.width / 2, errorY + 4, UiTheme.NEGATIVE);
            errorDisplayTime--;
        }

        // 悬停详情面板
        if (hoveredClassIndex >= 0 && displayClasses != null && hoveredClassIndex < displayClasses.length) {
            renderEquipmentPanel(graphics, displayClasses[hoveredClassIndex]);
        }
    }

    @Override
    protected void renderAfterMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderClassIcons(graphics);
        renderVariantPopup(graphics, mouseX, mouseY);
    }

    private void renderVariantPopup(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!hasVariantPopup()) return;
        ClassDisplay cls = displayClasses[popupClassIndex];
        int visible = Math.min(POPUP_MAX_VISIBLE, cls.variants.size());

        UiPaint.card(graphics, popupX, popupY, POPUP_W, popupH);
        String clsName = cls.name == null ? "" : cls.name;
        EspetroAuiWidgets.drawScaledString(graphics, clsName,
            popupX + 6, popupY + 4, UiTheme.GOLD, 1.0f, true);
        int nameW = this.font.width(EspetroAuiWidgets.stripFormatting(clsName));
        EspetroAuiWidgets.drawScaledString(graphics, "装备变体",
            popupX + 11 + nameW, popupY + 5, UiTheme.TEXT_SECOND, 0.9f);

        int closeX = popupX + POPUP_W - 16;
        int closeY = popupY + 2;
        boolean closeHovered = inside(mouseX, mouseY, closeX, closeY, 13, 13);
        UiPaint.roundRect(graphics, closeX, closeY, 13, 13, UiTheme.RADIUS_INNER,
            closeHovered ? UiTheme.NEGATIVE_SOFT : UiTheme.HAIRLINE);
        UiPaint.strokeRoundRect(graphics, closeX, closeY, 13, 13, UiTheme.RADIUS_INNER,
            closeHovered ? UiTheme.NEGATIVE : UiTheme.HAIRLINE);
        EspetroAuiWidgets.drawScaledString(graphics, "X", closeX + 4, closeY + 3,
            closeHovered ? UiTheme.NEGATIVE : UiTheme.TEXT_SECOND, 0.9f);

        int rowH = POPUP_ROW_H - 1;
        for (int row = 0; row < visible; row++) {
            int variantIndex = popupScroll + row;
            if (variantIndex >= cls.variants.size()) break;
            VariantDisplay variant = cls.variants.get(variantIndex);
            int rowX = popupX + 3;
            int rowY = popupY + POPUP_HEADER_H + row * POPUP_ROW_H;
            int rowW = POPUP_W - 6;
            int count = variantCounts.getOrDefault(cls.classId, Collections.emptyMap())
                .getOrDefault(variant.variantId(), 0);
            // 非严格模式下不检查变体独立人数上限
            boolean full = cls.strictCount && count >= variant.maxPlayers();
            boolean hovered = inside(mouseX, mouseY, rowX, rowY, rowW, rowH);

            int rowFill = full ? UiTheme.NEGATIVE_SOFT
                : hovered ? UiTheme.SURFACE_2 : UiTheme.alpha(UiTheme.SURFACE_0, 0.55f);
            UiPaint.roundRect(graphics, rowX, rowY, rowW, rowH, UiTheme.RADIUS_CONTROL, rowFill);
            UiPaint.strokeRoundRect(graphics, rowX, rowY, rowW, rowH, UiTheme.RADIUS_CONTROL,
                hovered && !full ? UiTheme.ACCENT_LINE : UiTheme.HAIRLINE);

            String variantName = variant.name() == null ? "" : variant.name();
            EspetroAuiWidgets.drawScaledString(graphics,
                EspetroAuiWidgets.trimToWidth(variantName, rowW - 84),
                rowX + 6, rowY + 5, full ? UiTheme.TEXT_THIRD : UiTheme.TEXT, 0.95f);

            String countText = cls.strictCount
                ? count + "/" + variant.maxPlayers()
                : count + "人";
            int countW = (int) (this.font.width(countText) * UiTheme.TYPE_HEADING);
            EspetroAuiWidgets.drawScaledString(graphics, countText,
                rowX + rowW - countW - 6, rowY + 4,
                full ? UiTheme.NEGATIVE : UiTheme.POSITIVE, UiTheme.TYPE_HEADING, true);
            if (variant.description() != null && !variant.description().isBlank()) {
                EspetroAuiWidgets.drawScaledString(graphics,
                    EspetroAuiWidgets.trimToWidth(variant.description(), rowW - 12),
                    rowX + 6, rowY + 17, UiTheme.TEXT_THIRD, UiTheme.TYPE_MICRO);
            }
        }

        if (cls.variants.size() > POPUP_MAX_VISIBLE) {
            EspetroAuiWidgets.drawScaledString(graphics, "滚轮浏览",
                popupX + POPUP_W - 48, popupY + popupH - 9, UiTheme.TEXT_THIRD, UiTheme.TYPE_MICRO);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        lastMouseX = (int) mouseX;
        lastMouseY = (int) mouseY;
        if (hasVariantPopup()) {
            if (button == 0) {
                handleVariantPopupClick((int) mouseX, (int) mouseY);
            } else {
                closeVariantPopup();
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (hasVariantPopup() && inside((int) mouseX, (int) mouseY,
            popupX, popupY, POPUP_W, popupH)) {
            int maxScroll = Math.max(0,
                displayClasses[popupClassIndex].variants.size() - POPUP_MAX_VISIBLE);
            popupScroll = Math.max(0, Math.min(maxScroll,
                popupScroll + (delta < 0 ? 1 : -1)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && hasVariantPopup()) {
            closeVariantPopup();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void openVariantPopup(int classIndex, int mouseX, int mouseY) {
        popupClassIndex = classIndex;
        popupScroll = 0;
        int count = displayClasses[classIndex].variants.size();
        int visible = Math.min(POPUP_MAX_VISIBLE, count);
        int footer = count > POPUP_MAX_VISIBLE ? 10 : 3;
        popupH = POPUP_HEADER_H + visible * POPUP_ROW_H + footer;
        popupX = mouseX + 9;
        if (popupX + POPUP_W > this.width - 3) {
            popupX = mouseX - POPUP_W - 9;
        }
        popupX = Math.max(3, Math.min(this.width - POPUP_W - 3, popupX));
        popupY = Math.max(3, Math.min(this.height - popupH - 3, mouseY + 7));
    }

    private void handleVariantPopupClick(int mouseX, int mouseY) {
        int closeX = popupX + POPUP_W - 16;
        int closeY = popupY + 2;
        if (inside(mouseX, mouseY, closeX, closeY, 13, 13)
            || !inside(mouseX, mouseY, popupX, popupY, POPUP_W, popupH)) {
            closeVariantPopup();
            return;
        }

        ClassDisplay cls = displayClasses[popupClassIndex];
        int row = (mouseY - (popupY + POPUP_HEADER_H)) / POPUP_ROW_H;
        if (mouseY >= popupY + POPUP_HEADER_H && row >= 0 && row < POPUP_MAX_VISIBLE) {
            int variantIndex = popupScroll + row;
            if (variantIndex < cls.variants.size()) {
                VariantDisplay variant = cls.variants.get(variantIndex);
                int count = variantCounts.getOrDefault(cls.classId, Collections.emptyMap())
                    .getOrDefault(variant.variantId(), 0);
                // 非严格模式下不检查变体独立人数上限
                if (!cls.strictCount || count < variant.maxPlayers()) {
                    ClassSelectionGui.selectClass(factionId, cls.classId, variant.variantId());
                    closeVariantPopup();
                    this.onClose();
                }
            }
        }
    }

    private boolean hasVariantPopup() {
        return displayClasses != null && popupClassIndex >= 0 && popupClassIndex < displayClasses.length;
    }

    private void closeVariantPopup() {
        popupClassIndex = -1;
        popupScroll = 0;
    }

    private static boolean inside(int x, int y, int left, int top, int width, int height) {
        return x >= left && x < left + width && y >= top && y < top + height;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // ========== 内部显示用 DTO ==========

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

        ClassDisplay(String classId, String name, String description, String role, String icon,
                     int maxPlayers, boolean strictCount, int troopValue, int healthBonus, float speedBonus,
                     List<VariantDisplay> variants) {
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

    private record VariantDisplay(String variantId, String name, String description, int maxPlayers) {}
}
