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
        VotePhase.add(root, width, height, "选择阵营编制",
            "共有 " + factions.length + " 个可选编制",
            () -> "选择一个阵营编制加入战斗", () -> VotePhase.currentStage(VotePhase.Stage.NONE));
        int panelW = Math.max(1, Math.min(660, width - 32));
        int columns = Math.max(1, Math.min(3, Math.min(Math.max(1, factions.length), (width - 32) / 150)));
        int gap = UiTheme.GAP_XS;
        int cardW = Math.max(1, (panelW - 24 - (columns - 1) * gap) / columns);
        int cardH = UiTheme.CONTROL_HEIGHT;
        int rows = Math.max(1, (factions.length + columns - 1) / columns);
        int headerH = 10;
        int footerH = 42;
        int availableH = Math.max(1, VotePhase.contentBottom(height) - VotePhase.contentTop(height) - 8);
        int panelH = Math.min(availableH,
            headerH + rows * cardH + Math.max(0, rows - 1) * gap + footerH);
        int panelX = (width - panelW) / 2;
        int panelY = VotePhase.contentTop(height) + Math.max(0, (availableH - panelH) / 2);
        root.addChild(EspetroAuiWidgets.card(panelX, panelY, panelW, panelH));

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
