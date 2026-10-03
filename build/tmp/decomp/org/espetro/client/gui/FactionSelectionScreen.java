/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.EspetroMenuScreen;
import org.espetro.network.NetworkManager;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;
import org.espetro.team.GamePhase;

public class FactionSelectionScreen
extends EspetroMenuScreen {
    private FactionDataLoader.FactionData[] factions = new FactionDataLoader.FactionData[0];
    private int scrollOffset = 0;
    private int maxScrollOffset = 0;

    public FactionSelectionScreen() {
        super(Component.m_237113_("\u9009\u62e9\u9635\u8425"));
    }

    @Override
    protected void m_7856_() {
        this.loadFactions();
        super.m_7856_();
    }

    private void loadFactions() {
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        loader.ensureLoaded(Minecraft.m_91087_().m_91098_());
        this.factions = (FactionDataLoader.FactionData[])Arrays.stream(loader.getFactionArray()).filter(f -> f != null && f.name != null && !f.name.contains("\u7a7a\u964d\u5175\u56e2")).toArray(FactionDataLoader.FactionData[]::new);
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        EspetroAuiWidgets.drawScreenShade(graphics, this.f_96543_, this.f_96544_);
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        int panelW = Math.min(520, Math.max(240, this.f_96543_ - 28));
        int columns = this.f_96543_ < 420 ? 1 : (this.f_96543_ < 680 ? 2 : 3);
        int gap = 4;
        int cardW = (panelW - 24 - (columns - 1) * gap) / columns;
        int cardH = 13;
        int rows = Math.max(1, (this.factions.length + columns - 1) / columns);
        int panelH = Math.min(this.f_96544_ - 28, 58 + rows * cardH + Math.max(0, rows - 1) * gap + 24);
        int panelX = (this.f_96543_ - panelW) / 2;
        int panelY = Math.max(14, (this.f_96544_ - panelH) / 2);
        root.addChild(EspetroAuiWidgets.panel(panelX, panelY, panelW, panelH, 0, 0));
        root.addChild(EspetroAuiWidgets.centeredText(panelX, panelY + 6, panelW, "\u00a76\u00a7l\u6218\u672f\u5c0f\u961f - \u9009\u62e9\u9635\u8425", -1));
        root.addChild(EspetroAuiWidgets.centeredText(panelX, panelY + 22, panelW, "\u00a77\u9009\u62e9\u4e00\u4e2a\u9635\u8425\u7f16\u5236\u52a0\u5165\u6218\u6597", -2828064));
        root.addChild(EspetroAuiWidgets.centeredText(panelX, panelY + 36, panelW, "\u00a7e\u5f53\u524d\u53ef\u7528\u9635\u8425: " + this.factions.length + " \u4e2a", -14490));
        root.addChild(EspetroAuiWidgets.rect(panelX + 12, panelY + 51, panelW - 24, 1, 0x25FFFFFF));
        int startX = panelX + 12;
        int startY = panelY + 60;
        int visibleRows = Math.max(1, (panelH - 86) / (cardH + gap));
        int visibleCount = visibleRows * columns;
        this.maxScrollOffset = Math.max(0, this.factions.length - visibleCount);
        this.scrollOffset = Math.min(this.scrollOffset, this.maxScrollOffset);
        int maxVisible = Math.min(this.factions.length, this.scrollOffset + visibleCount);
        for (int i = this.scrollOffset; i < maxVisible; ++i) {
            FactionDataLoader.FactionData faction = this.factions[i];
            int localIndex = i - this.scrollOffset;
            int col = localIndex % columns;
            int row = localIndex / columns;
            int x = startX + col * (cardW + gap);
            int y = startY + row * (cardH + gap);
            String icon = faction.icon == null ? "" : faction.icon + " ";
            String name = faction.name == null ? faction.id : faction.name;
            String label = "\u00a7f" + icon + name;
            EspetroAuiWidgets.ActionButton button = EspetroAuiWidgets.button(x, y, cardW, cardH, label, () -> this.selectFaction(faction.id)).setColors(0, 539768132, 809119776).setBorderColor(0);
            root.addChild(button);
        }
        if (this.maxScrollOffset > 0) {
            root.addChild(EspetroAuiWidgets.centeredText(panelX, panelY + panelH - 19, panelW, "\u00a78\u9f20\u6807\u6eda\u8f6e\u5207\u6362\u5217\u8868  " + (this.scrollOffset + 1) + "-" + maxVisible + "/" + this.factions.length, -5327681));
        }
        int backW = EspetroAuiWidgets.textButtonWidth("\u00a7c\u8fd4\u56de");
        root.addChild(EspetroAuiWidgets.button(panelX + panelW / 2 - backW / 2, panelY + panelH - 16, backW, 13, "\u00a7c\u8fd4\u56de", this::m_7379_).setColors(0, 539301141, 807736597).setBorderColor(0));
    }

    private void selectFaction(String factionId) {
        if (factionId != null && !factionId.isEmpty()) {
            NetworkManager.requestClassSelection(factionId);
        }
    }

    @Override
    public boolean m_7043_() {
        return false;
    }

    @Override
    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        if (this.maxScrollOffset > 0) {
            int nextOffset = this.scrollOffset + (delta < 0.0 ? 1 : -1);
            if ((nextOffset = Math.max(0, Math.min(this.maxScrollOffset, nextOffset))) != this.scrollOffset) {
                this.scrollOffset = nextOffset;
                this.rebuildMenuRoot();
                return true;
            }
        }
        return super.m_6050_(mouseX, mouseY, delta);
    }

    @Override
    public void m_7379_() {
        if (this.m_6913_()) {
            super.m_7379_();
        }
    }

    @Override
    public boolean m_6913_() {
        GamePhase phase = ClientGameState.getCurrentPhase();
        return !phase.isMatchActive() || phase == GamePhase.BATTLE;
    }

    public static void open() {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91080_ == null) {
            mc.m_91152_(new FactionSelectionScreen());
        }
    }

    public static void openWithFaction(String factionId) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91080_ == null) {
            NetworkManager.requestClassSelection(factionId);
        }
    }
}

