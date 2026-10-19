package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;
import org.espetro.client.aui.GuiElement;

import java.util.Arrays;

/**
 * 阵营选择界面
 * 玩家选择自己的阵营编制
 */
public class FactionSelectionScreen extends EspetroMenuScreen {

    private FactionDataLoader.FactionData[] factions = new FactionDataLoader.FactionData[0];
    private int scrollOffset = 0;
    private int maxScrollOffset = 0;

    public FactionSelectionScreen() {
        super(Component.literal("选择阵营"));
    }

    @Override
    protected void init() {
        loadFactions();
        super.init();
    }

    private void loadFactions() {
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        loader.ensureLoaded(Minecraft.getInstance().getResourceManager());
        factions = Arrays.stream(loader.getFactionArray())
            .filter(f -> f != null && f.name != null && !f.name.contains("空降兵团"))
            .toArray(FactionDataLoader.FactionData[]::new);
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        EspetroAuiWidgets.drawScreenShade(graphics, this.width, this.height);
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        int panelW = Math.min(520, Math.max(240, this.width - 28));
        int columns = this.width < 420 ? 1 : this.width < 680 ? 2 : 3;
        int gap = UiTheme.GAP_XS;
        int cardW = (panelW - 24 - (columns - 1) * gap) / columns;
        int cardH = UiTheme.CONTROL_HEIGHT_S;
        int rows = Math.max(1, (factions.length + columns - 1) / columns);
        int headerH = 78;
        int footerH = 46;
        int panelH = Math.min(this.height - 20,
            headerH + rows * cardH + Math.max(0, rows - 1) * gap + footerH);
        int panelX = (this.width - panelW) / 2;
        int panelY = Math.max(14, (this.height - panelH) / 2);

        // 主容器：玻璃卡片 + 顶部标题区
        root.addChild(EspetroAuiWidgets.card(panelX, panelY, panelW, panelH));

        EspetroAuiWidgets.Text title = EspetroAuiWidgets.centeredText(panelX, panelY + 10, panelW,
            "战术小队 · 选择阵营", UiTheme.GOLD);
        title.setTextScale(UiTheme.TYPE_TITLE).setShadow(true);
        root.addChild(title);
        root.addChild(EspetroAuiWidgets.centeredText(panelX, panelY + 29, panelW,
            "选择一个阵营编制加入战斗", UiTheme.TEXT_SECOND));

        // 可用阵营数量：数字独立放大，避免"数字混在句子里"。
        root.addChild(EspetroAuiWidgets.stat(panelX + (panelW - 64) / 2, panelY + 44, 64,
            "AVAILABLE", String.valueOf(factions.length), UiTheme.GOLD));
        root.addChild(EspetroAuiWidgets.divider(panelX + 12, panelY + 70, panelW - 24));

        int startX = panelX + 12;
        int startY = panelY + headerH;
        int visibleRows = Math.max(1, (panelH - headerH - footerH + gap) / (cardH + gap));
        int visibleCount = visibleRows * columns;
        maxScrollOffset = Math.max(0, factions.length - visibleCount);
        scrollOffset = Math.min(scrollOffset, maxScrollOffset);
        int maxVisible = Math.min(factions.length, scrollOffset + visibleCount);

        for (int i = scrollOffset; i < maxVisible; i++) {
            FactionDataLoader.FactionData faction = factions[i];
            int localIndex = i - scrollOffset;
            int col = localIndex % columns;
            int row = localIndex / columns;
            int x = startX + col * (cardW + gap);
            int y = startY + row * (cardH + gap);

            String icon = faction.icon == null ? "" : faction.icon + " ";
            String name = faction.name == null ? faction.id : faction.name;
            String label = icon + name;
            var button = EspetroAuiWidgets.button(x, y, cardW, cardH, label, () -> selectFaction(faction.id))
                .setAccentColor(UiTheme.ACCENT);
            root.addChild(button);
        }

        if (maxScrollOffset > 0) {
            root.addChild(EspetroAuiWidgets.centeredText(panelX, panelY + panelH - 38, panelW,
                "滚轮浏览  " + (scrollOffset + 1) + "-" + maxVisible + "/" + factions.length,
                UiTheme.TEXT_THIRD).setTextScale(UiTheme.TYPE_MICRO));
        }

        int backW = Math.max(64, EspetroAuiWidgets.textButtonWidth("返回"));
        root.addChild(EspetroAuiWidgets.button(panelX + panelW / 2 - backW / 2,
            panelY + panelH - 24, backW, 16, "返回", this::onClose)
            .setTextColor(UiTheme.TEXT_SECOND)
            .setAccentColor(UiTheme.WARNING));
    }

    private void selectFaction(String factionId) {
        if (factionId != null && !factionId.isEmpty()) {
            org.espetro.network.NetworkManager.requestClassSelection(factionId);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (maxScrollOffset > 0) {
            int nextOffset = scrollOffset + (delta < 0 ? 1 : -1);
            nextOffset = Math.max(0, Math.min(maxScrollOffset, nextOffset));
            if (nextOffset != scrollOffset) {
                scrollOffset = nextOffset;
                rebuildMenuRoot();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        if (shouldCloseOnEsc()) {
            super.onClose();
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        org.espetro.team.GamePhase phase = ClientGameState.getCurrentPhase();
        return !phase.isMatchActive() || phase == org.espetro.team.GamePhase.BATTLE;
    }

    public static void open() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null) {
            mc.setScreen(new FactionSelectionScreen());
        }
    }

    /**
     * 直接打开指定阵营的职业选择界面（通过服务端获取数据）
     */
    public static void openWithFaction(String factionId) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null) {
            org.espetro.network.NetworkManager.requestClassSelection(factionId);
        }
    }
}
