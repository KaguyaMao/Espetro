/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.CurrentMapBackgroundRenderer;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.EspetroMenuScreen;
import org.espetro.client.gui.MapVotePreviewResolver;
import org.espetro.network.MapVoteStatePacket;
import org.espetro.network.NetworkManager;

public final class MapVoteScreen
extends EspetroMenuScreen {
    private static final int COLUMNS = 3;
    private static final int MAX_ROWS = 2;
    private static final int MAX_CANDIDATES = 6;
    private static final int GAP = 4;
    private static final int CARD_PAD = 4;
    private static final int FOOTER_H = 14;
    private static final int SIDE_PAD = 8;
    private static final int BOTTOM_PAD = 8;
    private static final int MIN_CARD_W = 120;
    private static final int MIN_CARD_H = 90;
    private static MapVoteStatePacket latest = new MapVoteStatePacket(false, 0, 0L, List.of(), Map.of(), null, null, null);
    private static final Map<String, PreviewTexture> previewTextureCache = new LinkedHashMap<String, PreviewTexture>();
    private final List<MapCardButton> mapButtons = new ArrayList<MapCardButton>();
    private long receivedAtMs;
    private int receivedRemaining;
    private EspetroAuiWidgets.PhaseHeader phaseHeader;
    private int lastStatusSecond = Integer.MIN_VALUE;

    public MapVoteScreen() {
        super(Component.m_237113_("\u5730\u56fe\u6295\u7968"));
        this.receivedAtMs = System.currentTimeMillis();
        this.receivedRemaining = MapVoteScreen.latest.remainingSeconds;
        MapVoteScreen.preloadPreviewTextures();
    }

    private static void preloadPreviewTextures() {
        Minecraft mc = Minecraft.m_91087_();
        if (mc == null) {
            return;
        }
        for (MapVoteStatePacket.Candidate c : MapVoteScreen.latest.candidates) {
            Path previewPath;
            String key = c.mapFolder;
            if (previewTextureCache.containsKey(key) || (previewPath = MapVotePreviewResolver.resolve(mc.f_91069_.toPath(), key)) == null) continue;
            try {
                InputStream in = Files.newInputStream(previewPath, new OpenOption[0]);
                try {
                    NativeImage image = NativeImage.m_85058_(in);
                    int texW = image.m_84982_();
                    int texH = image.m_85084_();
                    DynamicTexture texture = new DynamicTexture(image);
                    ResourceLocation rl = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)("map_preview/" + Integer.toHexString(key.hashCode())));
                    mc.m_91097_().m_118495_(rl, texture);
                    previewTextureCache.put(key, new PreviewTexture(rl, texW, texH));
                }
                finally {
                    if (in == null) continue;
                    in.close();
                }
            }
            catch (IOException iOException) {}
        }
    }

    public static void update(MapVoteStatePacket packet) {
        latest = packet;
        MapVoteScreen.preloadPreviewTextures();
        Minecraft mc = Minecraft.m_91087_();
        Screen screen = mc.f_91080_;
        if (screen instanceof MapVoteScreen) {
            MapVoteScreen screen2 = (MapVoteScreen)screen;
            boolean structureChanged = screen2.mapButtons.size() != packet.candidates.size();
            screen2.receivedAtMs = System.currentTimeMillis();
            screen2.receivedRemaining = packet.remainingSeconds;
            if (structureChanged) {
                screen2.rebuildMenuRoot();
            } else {
                screen2.refreshLabels();
            }
        }
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        CurrentMapBackgroundRenderer.render(graphics, this.f_96543_, this.f_96544_, MapVoteScreen.resolveBackgroundMapFolder());
    }

    private static String resolveBackgroundMapFolder() {
        if (MapVoteScreen.latest.myVoteMapFolder != null && !MapVoteScreen.latest.myVoteMapFolder.isBlank()) {
            return MapVoteScreen.latest.myVoteMapFolder;
        }
        if (MapVoteScreen.latest.winnerMapFolder != null && !MapVoteScreen.latest.winnerMapFolder.isBlank()) {
            return MapVoteScreen.latest.winnerMapFolder;
        }
        String current = ClientGameState.getCurrentMapFolder();
        if (current != null && !current.isBlank()) {
            return current;
        }
        return MapVoteScreen.latest.candidates.isEmpty() ? null : MapVoteScreen.latest.candidates.get((int)0).mapFolder;
    }

    private LayoutMetrics computeLayout() {
        int headerH = 42;
        int startY = headerH + 8;
        int contentW = Math.max(360, this.f_96543_ - 16);
        int contentH = Math.max(180, this.f_96544_ - startY - 8);
        int cardW = Math.max(120, (contentW - 8) / 3);
        int cardH = Math.max(90, (contentH - 4) / 2);
        int imgW = Math.max(1, cardW - 8);
        int maxImgH = Math.max(1, cardH - 14 - 4);
        int imgH = Math.max(1, imgW * 9 / 16);
        if (imgH > maxImgH) {
            imgH = Math.max(1, maxImgH);
            imgW = Math.max(1, imgH * 16 / 9);
        }
        int panelW = 3 * cardW + 8;
        int panelH = 2 * cardH + 4;
        int startX = (this.f_96543_ - panelW) / 2;
        int gridStartY = startY + Math.max(0, (contentH - panelH) / 2);
        return new LayoutMetrics(cardW, cardH, imgW, imgH, startX, gridStartY);
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        this.mapButtons.clear();
        this.phaseHeader = EspetroAuiWidgets.addMutablePhaseHeader(root, this.f_96543_, "\u00a76\u00a7l\u5168\u5c40\u5730\u56fe\u6295\u7968", this.buildResultText(this.computeDisplaySeconds()), "\u00a77\u5168\u670d\u7edf\u4e00\u8ba1\u7968\uff0c\u7968\u6570\u6700\u9ad8\u7684\u5730\u56fe\u80dc\u51fa", -14490);
        int count = Math.min(MapVoteScreen.latest.candidates.size(), 6);
        LayoutMetrics layout = this.computeLayout();
        for (int i = 0; i < count; ++i) {
            MapVoteStatePacket.Candidate candidate = MapVoteScreen.latest.candidates.get(i);
            int col = i % 3;
            int row = i / 3;
            MapCardButton button = new MapCardButton(layout.startX + col * (layout.cardW + 4), layout.startY + row * (layout.cardH + 4), layout.cardW, layout.cardH, layout.imgW, layout.imgH, candidate, () -> {
                if (this.tutorialPreviewMode) {
                    return;
                }
                NetworkManager.sendMapVoteCast(candidate.mapFolder);
            });
            this.mapButtons.add(button);
            root.addChild(button);
        }
        this.refreshLabels();
    }

    private void refreshLabels() {
        for (int i = 0; i < this.mapButtons.size() && i < MapVoteScreen.latest.candidates.size(); ++i) {
            MapVoteStatePacket.Candidate candidate = MapVoteScreen.latest.candidates.get(i);
            int votes = MapVoteScreen.latest.tally.getOrDefault(candidate.mapFolder, 0);
            boolean selected = candidate.mapFolder.equals(MapVoteScreen.latest.myVoteMapFolder);
            this.mapButtons.get(i).update(candidate, votes, selected, MapVoteScreen.latest.active);
        }
        this.updateStatusHeader(true);
    }

    private int computeDisplaySeconds() {
        int elapsed = (int)((System.currentTimeMillis() - this.receivedAtMs) / 1000L);
        return Math.max(0, this.receivedRemaining - elapsed);
    }

    private String buildResultText(int remaining) {
        return MapVoteScreen.latest.active ? "\u00a7e\u5269\u4f59 " + remaining + " \u79d2" : "\u00a7a\u80dc\u51fa\u5730\u56fe\uff1a" + (MapVoteScreen.latest.winnerDisplayName == null ? "\u968f\u673a" : MapVoteScreen.latest.winnerDisplayName);
    }

    private void updateStatusHeader(boolean force) {
        if (this.phaseHeader == null) {
            return;
        }
        int remaining = this.computeDisplaySeconds();
        if (!force && remaining == this.lastStatusSecond) {
            return;
        }
        this.lastStatusSecond = remaining;
        this.phaseHeader.setStatus(this.buildResultText(remaining));
    }

    @Override
    public void m_86600_() {
        super.m_86600_();
        this.updateStatusHeader(false);
    }

    @Override
    public void m_7379_() {
        if (!MapVoteScreen.latest.active) {
            super.m_7379_();
        }
    }

    @Override
    public boolean m_6913_() {
        return !MapVoteScreen.latest.active;
    }

    @Override
    public boolean m_7043_() {
        return false;
    }

    private record PreviewTexture(ResourceLocation location, int texW, int texH) {
    }

    private record LayoutMetrics(int cardW, int cardH, int imgW, int imgH, int startX, int startY) {
    }

    private static final class MapCardButton
    extends GuiElement {
        private MapVoteStatePacket.Candidate candidate;
        private int votes;
        private boolean selected;
        private boolean enabled;
        private final int imgW;
        private final int imgH;
        private final Runnable action;

        private MapCardButton(int x, int y, int width, int height, int imgW, int imgH, MapVoteStatePacket.Candidate candidate, Runnable action) {
            super(x, y, width, height);
            this.imgW = Math.max(1, imgW);
            this.imgH = Math.max(1, imgH);
            this.candidate = candidate;
            this.action = action;
        }

        private void update(MapVoteStatePacket.Candidate candidate, int votes, boolean selected, boolean enabled) {
            this.candidate = candidate;
            this.votes = votes;
            this.selected = selected;
            this.enabled = enabled;
        }

        @Override
        public boolean onMouseClick(int mouseX, int mouseY, int button) {
            if (!(button == 0 && this.enabled && this.isVisible() && this.hasFocus())) {
                return false;
            }
            if (this.action != null) {
                this.action.run();
            }
            return true;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            int border;
            if (!this.isVisible()) {
                return;
            }
            int bx = x + this.getX();
            int by = y + this.getY();
            int bw = this.getWidth();
            int bh = this.getHeight();
            int n = this.selected ? -1525668 : (border = this.hasFocus() && this.enabled ? -4011819 : -2141494688);
            if (this.selected) {
                graphics.m_280509_(bx, by, bx + bw, by + bh, 808334633);
            } else if (this.hasFocus() && this.enabled) {
                graphics.m_280509_(bx, by, bx + bw, by + bh, 539439160);
            }
            graphics.m_280637_(bx, by, bw, bh, border);
            int maxImgW = Math.max(1, bw - 8);
            int maxImgH = Math.max(1, bh - 14 - 4);
            int imgAreaW = Math.max(1, Math.min(this.imgW, maxImgW));
            int imgAreaH = Math.max(1, imgAreaW * 9 / 16);
            if (imgAreaH > maxImgH) {
                imgAreaH = maxImgH;
                imgAreaW = Math.max(1, imgAreaH * 16 / 9);
            }
            int imgX = bx + (bw - imgAreaW) / 2;
            int imgY = by + 4;
            PreviewTexture preview = previewTextureCache.get(this.candidate.mapFolder);
            if (preview != null) {
                graphics.m_280411_(preview.location(), imgX, imgY, imgAreaW, imgAreaH, 0.0f, 0.0f, preview.texW(), preview.texH(), preview.texW(), preview.texH());
            } else {
                graphics.m_280509_(imgX, imgY, imgX + imgAreaW, imgY + imgAreaH, 0x40303030);
                graphics.m_280637_(imgX, imgY, imgAreaW, imgAreaH, 1616601696);
                String placeholder = "\u00a78\u6682\u672a\u6dfb\u52a0\u56fe\u7247";
                graphics.m_280653_(Minecraft.m_91087_().f_91062_, Component.m_237113_(placeholder), bx + bw / 2, imgY + imgAreaH / 2 - 5, -5327681);
            }
            String voteText = String.valueOf(this.votes);
            int voteW = Minecraft.m_91087_().f_91062_.m_92895_(voteText);
            int voteX = bx + bw - 4 - voteW;
            int voteY = by + bh - 10;
            graphics.m_280614_(Minecraft.m_91087_().f_91062_, Component.m_237113_(voteText), voteX, voteY, this.enabled ? -1525668 : -5327681, false);
            int nameMaxW = Math.max(8, bw - 8 - voteW);
            String mapName = EspetroAuiWidgets.trimToWidth((this.selected ? "\u00a7a\u2714 " : "\u00a7f") + this.candidate.displayName, nameMaxW);
            int nameX = bx + 4;
            int nameY = by + bh - 10;
            graphics.m_280614_(Minecraft.m_91087_().f_91062_, Component.m_237113_(mapName), nameX, nameY, -1, false);
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }
}

