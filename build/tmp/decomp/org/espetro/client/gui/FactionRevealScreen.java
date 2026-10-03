/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.HashMap;
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
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.EspetroMenuScreen;
import org.espetro.client.gui.FactionSelectionImageResolver;

public class FactionRevealScreen
extends EspetroMenuScreen {
    private static final ResourceLocation DEFAULT_ATTACK_TEXTURE = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/attack_faction.png");
    private static final ResourceLocation DEFAULT_DEFEND_TEXTURE = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/defend_faction.png");
    private static final Map<String, ResolvedTexture> TEXTURE_CACHE = new HashMap<String, ResolvedTexture>();
    private static final int IMG_MAX_W = 192;
    private static final int IMG_MAX_H = 108;
    private static final int IMG_GAP = 48;
    private final String attackFactionName;
    private final String defendFactionName;
    private final ResourceLocation attackTexture;
    private final ResourceLocation defendTexture;
    private final int attackTexW;
    private final int attackTexH;
    private final int defendTexW;
    private final int defendTexH;
    private int ticksRemaining;
    private EspetroAuiWidgets.PhaseHeader phaseHeader;

    public FactionRevealScreen(String attackFactionName, String defendFactionName, String attackFactionImage, String defendFactionImage, int durationSeconds) {
        super(Component.m_237113_("\u7f16\u5236\u63ed\u793a"));
        this.attackFactionName = FactionRevealScreen.normalizeName(attackFactionName);
        this.defendFactionName = FactionRevealScreen.normalizeName(defendFactionName);
        this.ticksRemaining = Math.max(1, durationSeconds) * 20;
        ResolvedTexture attack = FactionRevealScreen.resolveTexture(attackFactionImage);
        ResolvedTexture defend = FactionRevealScreen.resolveTexture(defendFactionImage);
        if (attack == null) {
            attack = FactionRevealScreen.resolveResourceTexture(DEFAULT_ATTACK_TEXTURE);
        }
        if (defend == null) {
            defend = FactionRevealScreen.resolveResourceTexture(DEFAULT_DEFEND_TEXTURE);
        }
        this.attackTexture = attack != null ? attack.location() : DEFAULT_ATTACK_TEXTURE;
        this.defendTexture = defend != null ? defend.location() : DEFAULT_DEFEND_TEXTURE;
        this.attackTexW = attack != null ? attack.width() : 128;
        this.attackTexH = attack != null ? attack.height() : 128;
        this.defendTexW = defend != null ? defend.width() : 128;
        this.defendTexH = defend != null ? defend.height() : 128;
    }

    private static ResolvedTexture resolveTexture(String configuredPath) {
        ResolvedTexture resolvedTexture;
        block13: {
            if (configuredPath == null || configuredPath.isBlank()) {
                return null;
            }
            String key = configuredPath.trim();
            ResolvedTexture cached = TEXTURE_CACHE.get(key);
            if (cached != null) {
                return cached;
            }
            ResourceLocation resourceLocation = ResourceLocation.m_135820_(key);
            ResolvedTexture resource = FactionRevealScreen.resolveResourceTexture(resourceLocation);
            if (resource != null) {
                TEXTURE_CACHE.put(key, resource);
                return resource;
            }
            Minecraft mc = Minecraft.m_91087_();
            if (mc == null) {
                return null;
            }
            Path imagePath = FactionSelectionImageResolver.resolveClientFile(mc.f_91069_.toPath(), key);
            if (imagePath == null) {
                return null;
            }
            InputStream in = Files.newInputStream(imagePath, new OpenOption[0]);
            try {
                NativeImage image = NativeImage.m_85058_(in);
                int width = image.m_84982_();
                int height = image.m_85084_();
                DynamicTexture texture = new DynamicTexture(image);
                ResourceLocation dynamicLocation = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)("dynamic/faction_reveal_" + Integer.toHexString(key.hashCode())));
                mc.m_91097_().m_118495_(dynamicLocation, texture);
                ResolvedTexture resolved = new ResolvedTexture(dynamicLocation, width, height);
                TEXTURE_CACHE.put(key, resolved);
                resolvedTexture = resolved;
                if (in == null) break block13;
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
                catch (Exception ignored) {
                    return null;
                }
            }
            in.close();
        }
        return resolvedTexture;
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private static ResolvedTexture resolveResourceTexture(ResourceLocation location) {
        if (location == null) {
            return null;
        }
        String cacheKey = "resource:" + location;
        ResolvedTexture cached = TEXTURE_CACHE.get(cacheKey);
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
                    ResolvedTexture resolved = new ResolvedTexture(location, image.m_84982_(), image.m_85084_());
                    TEXTURE_CACHE.put(cacheKey, resolved);
                    ResolvedTexture resolvedTexture = resolved;
                    if (image != null) {
                        image.close();
                    }
                    return resolvedTexture;
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
        catch (Exception ignored) {
            return null;
        }
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        this.phaseHeader = EspetroAuiWidgets.addMutablePhaseHeader(root, this.f_96543_, "\u00a76\u00a7l\u53cc\u65b9\u7f16\u5236\u786e\u8ba4", "\u00a7f\u53cc\u65b9\u6700\u7ec8\u7f16\u5236\u5df2\u7ecf\u786e\u5b9a", "\u00a78" + this.getSecondsRemaining() + "\u79d2\u540e\u8fdb\u5165\u90e8\u7f72", -14490);
        int headerH = 42;
        boolean stacked = this.f_96543_ < 492;
        int cardW = stacked ? Math.min(224, this.f_96543_ - 28) : 224;
        int gap = 48;
        int contentW = stacked ? cardW : cardW * 2 + gap;
        int panelW = Math.min(this.f_96543_ - 18, Math.max(contentW + 20, stacked ? cardW + 40 : 430));
        int cardH = 130;
        int panelH = stacked ? cardH * 2 + gap : cardH;
        int panelX = (this.f_96543_ - panelW) / 2;
        int panelY = headerH + Math.max(8, (this.f_96544_ - headerH - panelH) / 2);
        root.addChild(EspetroAuiWidgets.panel(panelX, panelY, panelW, panelH, 0, 0));
        int startX = panelX + (panelW - contentW) / 2;
        int startY = panelY;
        if (stacked) {
            this.addFactionCard(root, startX, startY, cardW, this.attackTexture, this.attackTexW, this.attackTexH, this.attackFactionName, -41386);
            this.addFactionCard(root, startX, startY + cardH + gap, cardW, this.defendTexture, this.defendTexW, this.defendTexH, this.defendFactionName, -10514945);
        } else {
            this.addFactionCard(root, startX, startY, cardW, this.attackTexture, this.attackTexW, this.attackTexH, this.attackFactionName, -41386);
            this.addFactionCard(root, startX + cardW + gap, startY, cardW, this.defendTexture, this.defendTexW, this.defendTexH, this.defendFactionName, -10514945);
        }
    }

    private void addFactionCard(GuiElement root, int x, int y, int cardW, ResourceLocation texture, int texW, int texH, String factionName, int textColor) {
        AspectFit.Size fitted = AspectFit.within(texW, texH, 192, 108);
        int imgX = x + (cardW - fitted.width()) / 2;
        int imgY = y + (108 - fitted.height()) / 2;
        root.addChild(new FactionImageElement(imgX, imgY, fitted.width(), fitted.height(), texW, texH, texture));
        root.addChild(EspetroAuiWidgets.centeredText(x, y + 108 + 5, cardW, this.fitText("\u00a7l" + factionName, cardW - 8), textColor));
    }

    private String fitText(String text, int maxWidth) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91062_.m_92895_(EspetroAuiWidgets.stripFormatting(text)) <= maxWidth) {
            return text;
        }
        String plain = EspetroAuiWidgets.stripFormatting(text);
        return mc.f_91062_.m_92834_(plain, Math.max(0, maxWidth - mc.f_91062_.m_92895_("..."))) + "...";
    }

    private int getSecondsRemaining() {
        return Math.max(0, (this.ticksRemaining + 19) / 20);
    }

    public boolean matches(String attackName, String defendName) {
        return Objects.equals(this.attackFactionName, FactionRevealScreen.normalizeName(attackName)) && Objects.equals(this.defendFactionName, FactionRevealScreen.normalizeName(defendName));
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        EspetroAuiWidgets.drawScreenShade(graphics, this.f_96543_, this.f_96544_);
    }

    @Override
    public void m_86600_() {
        super.m_86600_();
        --this.ticksRemaining;
        if (this.ticksRemaining % 20 == 0 && this.phaseHeader != null) {
            this.phaseHeader.setDetail("\u00a78" + this.getSecondsRemaining() + "\u79d2\u540e\u8fdb\u5165\u90e8\u7f72");
        }
        if (this.ticksRemaining <= 0 && Minecraft.m_91087_().f_91080_ == this && !this.tutorialPreviewMode) {
            Minecraft.m_91087_().m_91152_(null);
        }
    }

    @Override
    public boolean m_6913_() {
        return false;
    }

    @Override
    public boolean m_7043_() {
        return false;
    }

    private static String normalizeName(String value) {
        return value == null || value.isEmpty() ? "\u672a\u786e\u5b9a" : value;
    }

    private record ResolvedTexture(ResourceLocation location, int width, int height) {
    }

    private static final class FactionImageElement
    extends GuiElement {
        private final ResourceLocation texture;
        private final int texW;
        private final int texH;
        private final int dispW;
        private final int dispH;

        FactionImageElement(int x, int y, int dispW, int dispH, int texW, int texH, ResourceLocation texture) {
            super(x, y, dispW, dispH);
            this.dispW = dispW;
            this.dispH = dispH;
            this.texW = texW;
            this.texH = texH;
            this.texture = texture;
        }

        @Override
        public void draw(GuiGraphics graphics, int refX, int refY, int screenWidth, int screenHeight, int mouseX, int mouseY, float opacity) {
            if (!this.isVisible() || this.texture == null) {
                return;
            }
            int bx = refX + this.getX();
            int by = refY + this.getY();
            graphics.m_280411_(this.texture, bx, by, this.dispW, this.dispH, 0.0f, 0.0f, this.texW, this.texH, this.texW, this.texH);
        }
    }
}

