/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.AspectFit;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.CurrentMapBackgroundRenderer;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.EspetroMenuScreen;
import org.espetro.network.ClassSelectScreenPacket;
import org.espetro.network.NetworkManager;

public class ClassSelectScreen
extends EspetroMenuScreen {
    private static final int CARD_FOOTER_PAD = 2;
    private static final int CARD_NAME_LINE = 11;
    private static final int CARD_FOOTER_H = 13;
    private static final float CARD_NAME_SCALE = 0.85f;
    private String team;
    private boolean isCommander;
    private List<ClassSelectScreenPacket.FactionInfo> factions;
    private int timeRemaining;
    private String opponentTeamName;
    private String opponentFaction;
    private int opponentTimeRemaining;
    private String lastSelectedFaction = null;
    private int scrollOffset = 0;
    private int maxScrollOffset = 0;
    private int scrollStep = 1;
    private EspetroAuiWidgets.PhaseHeader phaseHeader;
    private final Map<String, FactionCardButton> factionCards = new HashMap<String, FactionCardButton>();

    public ClassSelectScreen(String team, boolean isCommander, List<ClassSelectScreenPacket.FactionInfo> factions, int timeRemaining, String opponentTeamName, String opponentFaction, int opponentTimeRemaining, String selectedFactionId) {
        super(Component.m_237113_("\u7f16\u5236\u9009\u62e9"));
        this.team = team;
        this.isCommander = isCommander;
        this.factions = factions == null ? new ArrayList<ClassSelectScreenPacket.FactionInfo>() : new ArrayList<ClassSelectScreenPacket.FactionInfo>(factions);
        this.timeRemaining = timeRemaining;
        this.opponentTeamName = opponentTeamName;
        this.opponentFaction = opponentFaction;
        this.opponentTimeRemaining = opponentTimeRemaining;
        this.lastSelectedFaction = selectedFactionId == null || selectedFactionId.isEmpty() ? null : selectedFactionId;
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        CurrentMapBackgroundRenderer.render(graphics, this.f_96543_, this.f_96544_, ClientGameState.getCurrentMapFolder());
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        this.factionCards.clear();
        int count = this.factions == null ? 0 : this.factions.size();
        int columns = 3;
        int visibleRows = 2;
        int cardGap = 6;
        int panelW = Math.min(this.f_96543_ - 16, 24 + columns * 360 + (columns - 1) * cardGap);
        int cardW = (panelW - 24 - (columns - 1) * cardGap) / columns;
        int startY = 50;
        int availableH = Math.max(134 + cardGap, this.f_96544_ - startY - 18);
        int maxCardH = Math.max(67, (availableH - cardGap) / visibleRows);
        int imageW = Math.max(1, cardW - 6);
        int imageH = Math.max(1, imageW * 9 / 16);
        int maxImageSlotH = Math.max(36, maxCardH - 13);
        int imageSlotH = imageH + 6;
        if (imageSlotH > maxImageSlotH) {
            imageSlotH = maxImageSlotH;
            imageH = Math.max(1, imageSlotH - 6);
            imageW = Math.max(1, imageH * 16 / 9);
        }
        int cardH = imageSlotH + 13;
        int panelX = (this.f_96543_ - panelW) / 2;
        boolean selectingOpen = this.timeRemaining > 0;
        String teamPrefix = EspetroAuiWidgets.teamPrefix(this.team);
        this.phaseHeader = EspetroAuiWidgets.addMutablePhaseHeader(root, this.f_96543_, "\u00a76\u00a7l\u7f16\u5236\u6295\u7968 \u00a77| " + teamPrefix + "\u00a7l" + EspetroAuiWidgets.teamName(this.team) + " \u00a77[\u5168\u5458\u6295\u7968]", this.buildTimeText(), this.buildOpponentText(), EspetroAuiWidgets.teamColor(this.team));
        int headerH = 42;
        if (count == 0) {
            root.addChild(EspetroAuiWidgets.centeredText(panelX, headerH + 18, panelW, "\u00a7c\u6ca1\u6709\u53ef\u9009\u7f16\u5236", -39322));
            return;
        }
        int startX = panelX + 12;
        int visibleCount = Math.max(columns, visibleRows * columns);
        this.maxScrollOffset = Math.max(0, (count - visibleCount + columns - 1) / columns * columns);
        this.scrollOffset = Math.min(this.scrollOffset, this.maxScrollOffset);
        this.scrollOffset -= this.scrollOffset % columns;
        this.scrollStep = columns;
        int maxVisible = Math.min(count, this.scrollOffset + visibleCount);
        for (int i = this.scrollOffset; i < maxVisible; ++i) {
            ClassSelectScreenPacket.FactionInfo faction = this.factions.get(i);
            int localIndex = i - this.scrollOffset;
            int col = localIndex % columns;
            int row = localIndex / columns;
            int x = startX + col * (cardW + cardGap);
            int y = startY + row * (cardH + cardGap);
            boolean selected = faction.id != null && faction.id.equals(this.lastSelectedFaction);
            FactionCardButton card = new FactionCardButton(x, y, cardW, cardH, imageSlotH, faction, selectingOpen, selected, () -> this.selectFaction(faction.id));
            root.addChild(card);
            this.factionCards.put(faction.id, card);
        }
        if (this.maxScrollOffset > 0) {
            root.addChild(EspetroAuiWidgets.centeredText(panelX, Math.max(headerH, this.f_96544_ - 10), panelW, "\u00a78\u6eda\u8f6e\u6d4f\u89c8  " + (this.scrollOffset + 1) + "-" + maxVisible + "/" + count, -5327681));
        }
    }

    private void selectFaction(String factionId) {
        if (this.tutorialPreviewMode || this.timeRemaining <= 0 || factionId == null || factionId.isEmpty()) {
            return;
        }
        NetworkManager.sendClassSelect("", factionId);
        this.lastSelectedFaction = factionId;
        this.refreshDynamicElements();
    }

    private boolean isWaitingForOwnSelection() {
        return "ATTACK".equals(this.team) && this.timeRemaining <= 0 && this.opponentTimeRemaining > 0;
    }

    public void updateFromPacket(ClassSelectScreenPacket packet) {
        ArrayList<ClassSelectScreenPacket.FactionInfo> nextFactions = packet.getFactions() == null ? new ArrayList<ClassSelectScreenPacket.FactionInfo>() : new ArrayList<ClassSelectScreenPacket.FactionInfo>(packet.getFactions());
        boolean layoutChanged = !Objects.equals(this.team, packet.getTeam()) || !ClassSelectScreen.hasSameFactionLayout(this.factions, nextFactions);
        this.team = packet.getTeam();
        this.isCommander = packet.isCommander();
        this.factions = nextFactions;
        this.timeRemaining = packet.getTimeRemaining();
        this.opponentTeamName = packet.getOpponentTeamName();
        this.opponentFaction = packet.getOpponentFaction();
        this.opponentTimeRemaining = packet.getOpponentTimeRemaining();
        String string = this.lastSelectedFaction = packet.getSelectedFactionId().isEmpty() ? null : packet.getSelectedFactionId();
        if (this.root != null) {
            if (layoutChanged) {
                this.rebuildMenuRoot();
            } else {
                this.refreshDynamicElements();
            }
        }
    }

    public void updateTimer(int timeRemaining, int opponentTimeRemaining, String selectedFactionId, boolean isCommander) {
        this.timeRemaining = timeRemaining;
        this.opponentTimeRemaining = opponentTimeRemaining;
        this.isCommander = isCommander;
        String string = this.lastSelectedFaction = selectedFactionId == null || selectedFactionId.isEmpty() ? null : selectedFactionId;
        if (this.root != null) {
            this.refreshDynamicElements();
        }
    }

    private void refreshDynamicElements() {
        if (this.phaseHeader != null) {
            this.phaseHeader.setTitle("\u00a76\u00a7l\u7f16\u5236\u6295\u7968 \u00a77| " + EspetroAuiWidgets.teamPrefix(this.team) + "\u00a7l" + EspetroAuiWidgets.teamName(this.team) + " \u00a77[\u5168\u5458\u6295\u7968]");
            this.phaseHeader.setStatus(this.buildTimeText());
            this.phaseHeader.setDetail(this.buildOpponentText());
        }
        boolean enabled = this.timeRemaining > 0;
        for (ClassSelectScreenPacket.FactionInfo faction : this.factions) {
            FactionCardButton card = this.factionCards.get(faction.id);
            if (card == null) continue;
            card.update(faction, enabled, Objects.equals(faction.id, this.lastSelectedFaction));
        }
    }

    private String buildTimeText() {
        if (this.timeRemaining > 0) {
            return (this.timeRemaining <= 5 ? "\u00a7c" : "\u00a76") + "\u5269\u4f59\u65f6\u95f4: " + this.timeRemaining + "\u79d2";
        }
        return this.isWaitingForOwnSelection() ? "\u00a77\u672c\u65b9\u7f16\u5236\u6295\u7968\u5c1a\u672a\u5f00\u59cb" : "\u00a77\u672c\u65b9\u7f16\u5236\u5df2\u786e\u5b9a\uff0c\u7b49\u5f85\u5bf9\u65b9\u9009\u62e9\u7f16\u5236";
    }

    private String buildOpponentText() {
        if (this.opponentTeamName == null || this.opponentTeamName.isEmpty()) {
            return "";
        }
        String text = this.opponentFaction != null && !this.opponentFaction.isEmpty() ? ("ATTACK".equals(this.team) ? "\u00a79" : "\u00a7c") + this.opponentTeamName + " \u7f16\u5236: " + this.opponentFaction : "\u00a77" + this.opponentTeamName + " \u7f16\u5236\u5c1a\u672a\u786e\u5b9a";
        if (this.opponentTimeRemaining >= 0) {
            text = text + (this.opponentTimeRemaining <= 5 ? " \u00a7c\u00b7 " : " \u00a76\u00b7 ") + "\u5269\u4f59 " + this.opponentTimeRemaining + "\u79d2";
        }
        return text;
    }

    private static boolean hasSameFactionLayout(List<ClassSelectScreenPacket.FactionInfo> current, List<ClassSelectScreenPacket.FactionInfo> updated) {
        if (current == null || current.size() != updated.size()) {
            return false;
        }
        for (int i = 0; i < current.size(); ++i) {
            ClassSelectScreenPacket.FactionInfo left = current.get(i);
            ClassSelectScreenPacket.FactionInfo right = updated.get(i);
            if (Objects.equals(left.id, right.id) && Objects.equals(left.name, right.name) && Objects.equals(left.selectionImage, right.selectionImage)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean m_6913_() {
        return false;
    }

    @Override
    public void m_7379_() {
    }

    @Override
    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        if (this.maxScrollOffset > 0) {
            int nextOffset = this.scrollOffset + (delta < 0.0 ? this.scrollStep : -this.scrollStep);
            if ((nextOffset = Math.max(0, Math.min(this.maxScrollOffset, nextOffset))) != this.scrollOffset) {
                this.scrollOffset = nextOffset;
                this.rebuildMenuRoot();
                return true;
            }
        }
        return super.m_6050_(mouseX, mouseY, delta);
    }

    @Override
    public boolean m_7043_() {
        return false;
    }

    private static class FactionCardButton
    extends GuiElement {
        private ClassSelectScreenPacket.FactionInfo faction;
        private final int imageHeight;
        private boolean enabled;
        private boolean selected;
        private final Runnable action;
        private FactionTexture texture;
        private static final Map<String, FactionTexture> DISK_TEXTURE_CACHE = new HashMap<String, FactionTexture>();
        private static final Map<String, Boolean> DISK_TEXTURE_FAILED = new HashMap<String, Boolean>();
        private static final Map<String, FactionTexture> SERVER_IMAGE_CACHE = new HashMap<String, FactionTexture>();
        private static final Map<ResourceLocation, FactionTexture> RESOURCE_TEXTURE_CACHE = new HashMap<ResourceLocation, FactionTexture>();

        FactionCardButton(int x, int y, int width, int height, int imageHeight, ClassSelectScreenPacket.FactionInfo faction, boolean enabled, boolean selected, Runnable action) {
            super(x, y, width, height);
            this.faction = faction;
            this.imageHeight = imageHeight;
            this.enabled = enabled;
            this.selected = selected;
            this.action = action;
            this.texture = FactionCardButton.resolveTexture(faction.selectionImage, faction.imageData);
        }

        void update(ClassSelectScreenPacket.FactionInfo faction, boolean enabled, boolean selected) {
            if (!Objects.equals(this.faction.selectionImage, faction.selectionImage) || !Arrays.equals(this.faction.imageData, faction.imageData)) {
                this.texture = FactionCardButton.resolveTexture(faction.selectionImage, faction.imageData);
            }
            this.faction = faction;
            this.enabled = enabled;
            this.selected = selected;
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
            int imageBoxY = by + 3;
            int imageBoxW = Math.max(1, bw - 6);
            int imageSlotH = Math.max(1, this.imageHeight - 6);
            AspectFit.Size fitted = this.texture == null ? AspectFit.within(16, 9, imageBoxW, imageSlotH) : AspectFit.within(this.texture.width(), this.texture.height(), imageBoxW, imageSlotH);
            int imageX = bx + Math.max(0, (bw - fitted.width()) / 2);
            int imageY = imageBoxY + Math.max(0, (imageSlotH - fitted.height()) / 2);
            if (this.texture != null) {
                graphics.m_280246_(1.0f, 1.0f, 1.0f, this.enabled ? 1.0f : 0.55f);
                graphics.m_280411_(this.texture.location(), imageX, imageY, fitted.width(), fitted.height(), 0.0f, 0.0f, this.texture.width(), this.texture.height(), this.texture.width(), this.texture.height());
                graphics.m_280246_(1.0f, 1.0f, 1.0f, 1.0f);
            } else {
                String missing = "\u00a77\u8fd8\u6ca1\u914d\u7f6e\u56fe\u7247\u55b5";
                int missingW = Minecraft.m_91087_().f_91062_.m_92895_(EspetroAuiWidgets.stripFormatting(missing));
                graphics.m_280614_(Minecraft.m_91087_().f_91062_, Component.m_237113_(missing), bx + Math.max(4, (bw - missingW) / 2), imageBoxY + Math.max(8, imageSlotH / 2), -5327681, false);
            }
            String voteText = String.valueOf(this.faction.voteCount);
            int voteW = Minecraft.m_91087_().f_91062_.m_92895_(voteText);
            int voteX = bx + bw - 4 - voteW;
            int voteY = by + bh - 10;
            graphics.m_280614_(Minecraft.m_91087_().f_91062_, Component.m_237113_(voteText), voteX, voteY, this.enabled ? -1525668 : -5327681, false);
            String nameOnly = this.faction.name == null ? "" : this.faction.name;
            int nameMaxW = Math.max(8, (int)((float)(bw - 8 - voteW) / 0.85f));
            String drawnName = EspetroAuiWidgets.trimToWidth((this.selected ? "\u00a7a\u2714 " : "") + nameOnly, nameMaxW);
            int nameColor = this.enabled ? -1 : -5327681;
            int nameX = bx + 4;
            int nameY = by + bh - 10;
            EspetroAuiWidgets.drawScaledString(graphics, drawnName, nameX, nameY, nameColor, 0.85f);
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }

        private static FactionTexture resolveTexture(String value, byte[] imageData) {
            if (value == null || value.isBlank()) {
                return null;
            }
            if (imageData != null && imageData.length > 0) {
                return FactionCardButton.resolveServerImage(value, imageData);
            }
            ResourceLocation location = ResourceLocation.m_135820_(value);
            if (location != null && Minecraft.m_91087_().m_91098_().m_213713_(location).isPresent()) {
                return FactionCardButton.resolveResourceTexture(location);
            }
            return FactionCardButton.resolveEsFactionsTexture(value);
        }

        private static FactionTexture resolveServerImage(String key, byte[] imageData) {
            FactionTexture cached = SERVER_IMAGE_CACHE.get(key);
            if (cached != null) {
                return cached;
            }
            try {
                NativeImage image = NativeImage.m_85058_(new ByteArrayInputStream(imageData));
                int width = image.m_84982_();
                int height = image.m_85084_();
                DynamicTexture texture = new DynamicTexture(image);
                String safe = Integer.toHexString(key.hashCode());
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)("dynamic/srv_faction_" + safe));
                Minecraft.m_91087_().m_91097_().m_118495_(id, texture);
                FactionTexture resolved = new FactionTexture(id, width, height);
                SERVER_IMAGE_CACHE.put(key, resolved);
                return resolved;
            }
            catch (Exception e) {
                return null;
            }
        }

        /*
         * Enabled aggressive exception aggregation
         */
        private static FactionTexture resolveResourceTexture(ResourceLocation location) {
            FactionTexture cached = RESOURCE_TEXTURE_CACHE.get(location);
            if (cached != null) {
                return cached;
            }
            try {
                Optional<Resource> resource = Minecraft.m_91087_().m_91098_().m_213713_(location);
                if (resource.isEmpty()) {
                    return null;
                }
                try (InputStream in = resource.get().m_215507_();){
                    NativeImage image = NativeImage.m_85058_(in);
                    try {
                        FactionTexture resolved = new FactionTexture(location, image.m_84982_(), image.m_85084_());
                        RESOURCE_TEXTURE_CACHE.put(location, resolved);
                        FactionTexture factionTexture = resolved;
                        if (image != null) {
                            image.close();
                        }
                        return factionTexture;
                    }
                    catch (Throwable throwable) {
                        if (image != null) {
                            try {
                                image.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                }
            }
            catch (Exception e) {
                return null;
            }
        }

        private static FactionTexture resolveEsFactionsTexture(String fileName) {
            String key = fileName.trim();
            if (DISK_TEXTURE_FAILED.containsKey(key)) {
                return null;
            }
            FactionTexture cached = DISK_TEXTURE_CACHE.get(key);
            if (cached != null) {
                return cached;
            }
            Path gameDir = Minecraft.m_91087_().f_91069_.toPath();
            ArrayList<Path> candidateRoots = new ArrayList<Path>();
            candidateRoots.add(gameDir.resolve("EsFactions").normalize());
            candidateRoots.add(Path.of("EsFactions", new String[0]).toAbsolutePath().normalize());
            candidateRoots.add(Path.of("run", "EsFactions").toAbsolutePath().normalize());
            candidateRoots.add(Path.of("..", "EsFactions").toAbsolutePath().normalize());
            for (Path esFactionsDir : candidateRoots) {
                FactionTexture factionTexture;
                block11: {
                    Path imagePath = esFactionsDir.resolve(key).normalize();
                    if (!imagePath.startsWith(esFactionsDir.normalize()) || !Files.isRegularFile(imagePath, new LinkOption[0])) continue;
                    InputStream in = Files.newInputStream(imagePath, new OpenOption[0]);
                    try {
                        NativeImage image = NativeImage.m_85058_(in);
                        int width = image.m_84982_();
                        int height = image.m_85084_();
                        DynamicTexture texture = new DynamicTexture(image);
                        String safe = Integer.toHexString(key.hashCode());
                        ResourceLocation id = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)("dynamic/faction_" + safe));
                        Minecraft.m_91087_().m_91097_().m_118495_(id, texture);
                        FactionTexture resolved = new FactionTexture(id, width, height);
                        DISK_TEXTURE_CACHE.put(key, resolved);
                        factionTexture = resolved;
                        if (in == null) break block11;
                    }
                    catch (Throwable throwable) {
                        try {
                            if (in != null) {
                                try {
                                    in.close();
                                }
                                catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        }
                        catch (Exception exception) {}
                    }
                    in.close();
                }
                return factionTexture;
            }
            DISK_TEXTURE_FAILED.put(key, true);
            return null;
        }

        private record FactionTexture(ResourceLocation location, int width, int height) {
        }
    }
}

