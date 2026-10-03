/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.util.Mth
 */
package cc.sighs.auratip.client.render;

import cc.sighs.auratip.api.animation.HoverAnimation;
import cc.sighs.auratip.api.animation.TransitionAnimation;
import cc.sighs.auratip.client.TipClient;
import cc.sighs.auratip.client.render.PanelRenderer;
import cc.sighs.auratip.data.TipData;
import cc.sighs.auratip.data.animation.AnimationType;
import cc.sighs.auratip.editor.client.EditorClient;
import cc.sighs.auratip.util.ColorUtil;
import cc.sighs.auratip.util.ComponentSerialization;
import cc.sighs.auratip.util.ResolveUtil;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class TipOverlay {
    public static final TipOverlay INSTANCE = new TipOverlay();
    private TipData tip;
    private List<TipData.Page> pages;
    private static final int OPEN_MS_BASE = 200;
    private static final int CLOSE_MS_BASE = 140;
    private int themeColor;
    private TipData.VisualSettings visualSettings;
    private int currentPage;
    private int remainingTicks;
    private int maxDuration;
    private TipData.VisualSettings.Background background;
    private boolean hasThemeColor;
    private TransitionAnimation transitionAnimation;
    private HoverAnimation hoverAnimation;
    private float hoverAnimationSpeed;
    private boolean hoverOnlyOnHover;
    private long hoverStartMs;
    private boolean hoverActive;
    private long animationStartMs;
    private int openDurationMs;
    private int closeDurationMs;
    private boolean closing;
    private boolean hoveringInteractiveArea;
    private int panelWidth;
    private int panelHeight;
    private int panelX;
    private int panelY;
    private TipData.Position position;
    private InputConstants.Key closeKey;
    private Map<String, Component> variables;

    private TipOverlay() {
    }

    public boolean isActive() {
        return this.tip != null;
    }

    public void show(TipData data, Map<String, Component> vars) {
        this.tip = data;
        this.pages = data.pages().stream().sorted(Comparator.comparingInt(TipData.Page::pageIndex)).toList();
        this.visualSettings = data.visualSettings();
        this.background = this.visualSettings.background();
        this.hasThemeColor = false;
        this.themeColor = 0;
        Optional<String> theme = this.visualSettings.themeColor();
        if (theme.isPresent() && !theme.get().isBlank()) {
            this.themeColor = ColorUtil.parseArgb(theme.get());
            this.hasThemeColor = true;
        }
        this.currentPage = 0;
        this.remainingTicks = this.maxDuration = data.behavior().defaultDuration();
        this.transitionAnimation = AnimationType.resolve(this.visualSettings.animationStyle(), this.visualSettings.animationParams().params());
        float speed = this.visualSettings.animationSpeed();
        if (speed <= 0.0f) {
            speed = 1.0f;
        }
        this.openDurationMs = (int)(200.0f / speed);
        this.closeDurationMs = (int)(140.0f / speed);
        this.animationStartMs = Util.m_137550_();
        this.closing = false;
        this.hoveringInteractiveArea = false;
        this.panelWidth = this.visualSettings.width();
        this.panelHeight = this.visualSettings.height();
        this.position = this.visualSettings.position();
        this.variables = vars == null ? Map.of() : new HashMap<String, Component>(vars);
        this.closeKey = null;
        Optional<String> keyId = data.behavior().closableByKey();
        if (keyId.isPresent()) {
            try {
                this.closeKey = InputConstants.m_84851_((String)keyId.get());
            }
            catch (Exception ignored) {
                this.closeKey = null;
            }
        }
        this.hoverAnimation = AnimationType.resolveHover(this.visualSettings.hoverAnimationStyle(), this.visualSettings.animationParams().hoverParams());
        float hoverSpeed = this.visualSettings.hoverAnimationSpeed();
        if (hoverSpeed <= 0.0f) {
            hoverSpeed = 1.0f;
        }
        this.hoverAnimationSpeed = hoverSpeed;
        this.hoverOnlyOnHover = this.visualSettings.hoverOnlyOnHover();
        this.hoverStartMs = -1L;
        this.hoverActive = false;
    }

    public void tick(int screenWidth, int screenHeight) {
        if (this.tip == null) {
            return;
        }
        this.updatePanelBounds(screenWidth, screenHeight);
        if (!this.closing && this.maxDuration > 0 && this.remainingTicks > 0) {
            boolean pause;
            boolean bl = pause = this.tip.behavior().pauseTimerOnHover() && this.hoveringInteractiveArea;
            if (!pause) {
                --this.remainingTicks;
                if (this.remainingTicks <= 0) {
                    this.startClosing();
                }
            }
        }
    }

    public void render(GuiGraphics graphics, float partialTick, int mouseX, int mouseY, int screenWidth, int screenHeight) {
        boolean entryDone;
        int drawY;
        int drawX;
        if (this.tip == null) {
            return;
        }
        this.hoveringInteractiveArea = false;
        if (this.transitionAnimation == null) {
            this.transitionAnimation = AnimationType.resolve(null);
            this.animationStartMs = Util.m_137550_();
            this.openDurationMs = 200;
            this.closeDurationMs = 140;
        }
        if (this.hoverAnimation == null) {
            this.hoverAnimation = AnimationType.resolveHover(null);
        }
        long now = Util.m_137550_();
        float eased = this.transitionAnimation.easedProgress(now, this.animationStartMs, this.closing, this.openDurationMs, this.closeDurationMs);
        if (this.closing && eased <= 0.001f) {
            this.tip = null;
            this.closing = false;
            this.hoveringInteractiveArea = false;
            this.variables = Map.of();
            TipClient.onTipClosed();
            return;
        }
        int x = this.panelX;
        int y = this.panelY;
        int w = this.panelWidth;
        int h = this.panelHeight;
        Optional<TipData.Position> from = this.visualSettings.animationFrom();
        Optional<TipData.Position> to = this.visualSettings.animationTo();
        if (from.isPresent()) {
            TipData.Position startPos = from.get();
            TipData.Position endPos = to.orElse(this.position);
            int[] start = this.resolvePanelPosition(startPos, screenWidth, screenHeight);
            int[] end = this.resolvePanelPosition(endPos, screenWidth, screenHeight);
            float fx = Mth.m_14179_((float)eased, (float)start[0], (float)end[0]);
            float fy = Mth.m_14179_((float)eased, (float)start[1], (float)end[1]);
            drawX = Mth.m_14143_((float)fx);
            drawY = Mth.m_14143_((float)fy);
        } else {
            int offsetX = this.transitionAnimation.offsetX(eased, this.panelWidth, this.panelHeight);
            int offsetY = this.transitionAnimation.offsetY(eased, this.panelWidth, this.panelHeight);
            drawX = x + offsetX;
            drawY = y + offsetY;
        }
        Minecraft mc = Minecraft.m_91087_();
        boolean cursorVisible = mc.f_91080_ != null;
        boolean pointerInBaseArea = cursorVisible && mouseX >= drawX && mouseX <= drawX + w && mouseY >= drawY && mouseY <= drawY + h;
        boolean bl = entryDone = !this.closing && eased >= 0.999f;
        if (!entryDone || this.closing) {
            this.hoverActive = false;
            this.hoverStartMs = -1L;
        } else if (this.hoverOnlyOnHover) {
            if (pointerInBaseArea) {
                if (!this.hoverActive) {
                    this.hoverActive = true;
                    this.hoverStartMs = now;
                }
            } else {
                this.hoverActive = false;
            }
        } else if (!this.hoverActive) {
            this.hoverActive = true;
            this.hoverStartMs = now;
        }
        int hoverOffsetX = 0;
        int hoverOffsetY = 0;
        if (this.hoverActive && this.hoverAnimation != null && this.hoverStartMs >= 0L) {
            hoverOffsetX = this.hoverAnimation.offsetX(now, this.hoverStartMs, w, h, this.hoverAnimationSpeed);
            hoverOffsetY = this.hoverAnimation.offsetY(now, this.hoverStartMs, w, h, this.hoverAnimationSpeed);
        }
        this.renderPanelShadow(graphics, drawX += hoverOffsetX, drawY += hoverOffsetY, w, h, eased);
        this.renderPanelBackground(graphics, drawX, drawY, w, h, eased);
        if (this.pages == null || this.pages.isEmpty()) {
            this.tip = null;
            this.closing = false;
            this.hoveringInteractiveArea = false;
            this.variables = Map.of();
            TipClient.onTipClosed();
            return;
        }
        if (this.currentPage < 0 || this.currentPage >= this.pages.size()) {
            this.currentPage = 0;
        }
        TipData.Page page = this.pages.get(this.currentPage);
        TipData.LayoutConfig layout = this.visualSettings.layout();
        int paddingL = layout.padding().left();
        int paddingR = layout.padding().right();
        int paddingT = layout.padding().top();
        int paddingB = layout.padding().bottom();
        int spacing = layout.elementSpacing();
        int contentX = drawX + paddingL;
        int contentY = drawY + paddingT;
        if (page.title().isPresent()) {
            ComponentSerialization.TextElement title = page.title().get();
            contentY = this.drawTextElement(graphics, title, contentX, contentY);
            if (title.divider().isPresent()) {
                contentY = this.drawDivider(graphics, title.divider().get(), contentX, drawX + w - paddingR, contentY);
            }
        }
        if (page.subtitle().isPresent()) {
            ComponentSerialization.TextElement subtitle = page.subtitle().get();
            contentY = this.drawTextElement(graphics, subtitle, contentX, contentY + 4);
            if (page.title().isEmpty() && subtitle.divider().isPresent()) {
                contentY = this.drawDivider(graphics, subtitle.divider().get(), contentX, drawX + w - paddingR, contentY);
            }
        }
        if (page.content().isPresent()) {
            contentY = this.drawTextElement(graphics, page.content().get(), contentX, contentY + spacing * 2);
        }
        if (page.image().isPresent()) {
            this.drawImage(graphics, page.image().get(), drawX, drawY, w);
        }
        if (page.badge().isPresent()) {
            this.drawBadge(graphics, page.badge().get(), drawX, drawY, w, h);
        }
        if (this.tip.behavior().showCloseButton()) {
            int closeSize = 10;
            int closeX = drawX + w - closeSize - paddingR;
            int closeY = drawY + paddingT;
            graphics.m_280488_(Minecraft.m_91087_().f_91062_, "X", closeX, closeY, -1);
        }
        int indicatorY = drawY + h - paddingB;
        if (this.tip.behavior().showPageIndicator()) {
            String pageInfo = this.currentPage + 1 + "/" + this.pages.size();
            int pageInfoWidth = Minecraft.m_91087_().f_91062_.m_92895_(pageInfo);
            graphics.m_280488_(Minecraft.m_91087_().f_91062_, pageInfo, drawX + (w - pageInfoWidth) / 2, indicatorY, -1);
        }
        if (this.tip.behavior().showPageIndicator() && this.tip.behavior().allowPaging() && this.pages.size() > 1) {
            String left = "<";
            String right = ">";
            graphics.m_280488_(Minecraft.m_91087_().f_91062_, left, drawX + paddingL, indicatorY, -1);
            graphics.m_280488_(Minecraft.m_91087_().f_91062_, right, drawX + w - paddingR - Minecraft.m_91087_().f_91062_.m_92895_(right), indicatorY, -1);
        }
        this.hoveringInteractiveArea = cursorVisible && mouseX >= drawX && mouseX <= drawX + w && mouseY >= drawY && mouseY <= drawY + h;
    }

    public boolean keyPressed(int keyCode) {
        if (this.tip == null) {
            return false;
        }
        if (keyCode == 256) {
            this.startClosing();
            return true;
        }
        if (this.closeKey != null && keyCode == this.closeKey.m_84873_()) {
            this.startClosing();
            return true;
        }
        if (this.tip.behavior().allowPaging() && this.pages.size() > 1) {
            if (keyCode == 263) {
                this.previousPage();
                return true;
            }
            if (keyCode == 262) {
                this.nextPage();
                return true;
            }
        }
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.tip == null) {
            return false;
        }
        if (button != 0) {
            return false;
        }
        if (EditorClient.isOpen()) {
            return true;
        }
        if (this.transitionAnimation == null) {
            this.transitionAnimation = AnimationType.resolve(null);
            this.animationStartMs = Util.m_137550_();
            this.openDurationMs = 200;
            this.closeDurationMs = 140;
        }
        long now = Util.m_137550_();
        float eased = this.transitionAnimation.easedProgress(now, this.animationStartMs, this.closing, this.openDurationMs, this.closeDurationMs);
        int offsetX = this.transitionAnimation.offsetX(eased, this.panelWidth, this.panelHeight);
        int offsetY = this.transitionAnimation.offsetY(eased, this.panelWidth, this.panelHeight);
        int hoverOffsetX = 0;
        int hoverOffsetY = 0;
        if (this.hoverActive && this.hoverAnimation != null && this.hoverStartMs >= 0L) {
            hoverOffsetX = this.hoverAnimation.offsetX(now, this.hoverStartMs, this.panelWidth, this.panelHeight, this.hoverAnimationSpeed);
            hoverOffsetY = this.hoverAnimation.offsetY(now, this.hoverStartMs, this.panelWidth, this.panelHeight, this.hoverAnimationSpeed);
        }
        int x = this.panelX + offsetX + hoverOffsetX;
        int y = this.panelY + offsetY + hoverOffsetY;
        int w = this.panelWidth;
        TipData.LayoutConfig layout = this.visualSettings.layout();
        int paddingR = layout.padding().right();
        int paddingT = layout.padding().top();
        int paddingB = layout.padding().bottom();
        int paddingL = layout.padding().left();
        if (this.tip.behavior().showCloseButton()) {
            int closeSize = 10;
            int closeX = x + w - closeSize - paddingR;
            int closeY = y + paddingT;
            if (mouseX >= (double)closeX && mouseX <= (double)(closeX + closeSize * 2) && mouseY >= (double)closeY && mouseY <= (double)(closeY + closeSize * 2)) {
                this.startClosing();
                return true;
            }
        }
        if (this.tip.behavior().showPageIndicator() && this.tip.behavior().allowPaging() && this.pages.size() > 1) {
            int indicatorY = y + this.panelHeight - paddingB;
            String left = "<";
            String right = ">";
            int leftWidth = Minecraft.m_91087_().f_91062_.m_92895_(left);
            int rightWidth = Minecraft.m_91087_().f_91062_.m_92895_(right);
            int leftX = x + paddingL;
            int rightX = x + w - paddingR - rightWidth;
            if (mouseY >= (double)indicatorY) {
                Objects.requireNonNull(Minecraft.m_91087_().f_91062_);
                if (mouseY <= (double)(indicatorY + 9)) {
                    if (mouseX >= (double)leftX && mouseX <= (double)(leftX + leftWidth)) {
                        this.previousPage();
                        return true;
                    }
                    if (mouseX >= (double)rightX && mouseX <= (double)(rightX + rightWidth)) {
                        this.nextPage();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void closeImmediately() {
        this.tip = null;
        this.closing = false;
        this.hoveringInteractiveArea = false;
    }

    public void requestClose() {
        this.startClosing();
    }

    private void updatePanelBounds(int screenWidth, int screenHeight) {
        if (this.panelWidth <= 0 || this.panelHeight <= 0) {
            return;
        }
        TipData.Position pos = this.position;
        if (pos == null && this.visualSettings != null) {
            pos = this.visualSettings.position();
        }
        int[] resolved = this.resolvePanelPosition(pos, screenWidth, screenHeight);
        this.panelX = resolved[0];
        this.panelY = resolved[1];
    }

    private int[] resolvePanelPosition(TipData.Position pos, int screenWidth, int screenHeight) {
        int x;
        if (pos != null && pos.absolute()) {
            return new int[]{pos.x(), pos.y()};
        }
        String preset = pos != null && pos.preset() != null ? pos.preset() : "CENTER";
        String normalized = preset.toUpperCase(Locale.ROOT);
        int margin = 16;
        return new int[]{x, switch (normalized) {
            case "TOP_LEFT" -> {
                x = margin;
                yield margin;
            }
            case "TOP_CENTER" -> {
                x = (screenWidth - this.panelWidth) / 2;
                yield margin;
            }
            case "TOP_RIGHT" -> {
                x = screenWidth - this.panelWidth - margin;
                yield margin;
            }
            case "LEFT_CENTER" -> {
                x = margin;
                yield (screenHeight - this.panelHeight) / 2;
            }
            case "RIGHT_CENTER" -> {
                x = screenWidth - this.panelWidth - margin;
                yield (screenHeight - this.panelHeight) / 2;
            }
            case "BOTTOM_LEFT" -> {
                x = margin;
                yield screenHeight - this.panelHeight - margin;
            }
            case "BOTTOM_CENTER" -> {
                x = (screenWidth - this.panelWidth) / 2;
                yield screenHeight - this.panelHeight - margin;
            }
            case "BOTTOM_RIGHT" -> {
                x = screenWidth - this.panelWidth - margin;
                yield screenHeight - this.panelHeight - margin;
            }
            default -> {
                x = (screenWidth - this.panelWidth) / 2;
                yield (screenHeight - this.panelHeight) / 2;
            }
        }};
    }

    private void renderPanelShadow(GuiGraphics graphics, int x, int y, int w, int h, float eased) {
        int radius = this.background != null ? Math.max(0, this.background.borderRadius()) : 0;
        boolean rounded = this.background == null || this.background.rounded();
        float radiusPixels = rounded ? (float)radius : 0.0f;
        Optional<Object> shadowOpt = this.background != null ? this.background.shadow() : Optional.empty();
        TipData.VisualSettings.ShadowConfig cfg = shadowOpt.orElse(null);
        if (cfg == null) {
            return;
        }
        if (!cfg.enabled()) {
            return;
        }
        float alpha = eased * (float)(cfg.color() >>> 24 & 0xFF) / 255.0f;
        if (alpha <= 0.0f) {
            return;
        }
        int color = 0xFF000000 | cfg.color() & 0xFFFFFF;
        int sx = x + cfg.offsetX();
        int sy = y + cfg.offsetY();
        if (cfg.size() > 0) {
            for (int i = 0; i < cfg.size(); ++i) {
                float p = (float)i / (float)cfg.size();
                float lAlpha = Mth.m_14036_((float)(alpha * (1.0f - p * 0.5f)), (float)0.0f, (float)1.0f);
                PanelRenderer.drawRoundedPanel(graphics, sx - i, sy - i, w + i * 2, h + i * 2, color, color, radiusPixels, 2.0f, lAlpha);
            }
        } else {
            PanelRenderer.drawRoundedPanel(graphics, sx, sy, w, h, color, color, radiusPixels, 2.0f, alpha);
        }
    }

    private void renderPanelBackground(GuiGraphics graphics, int x, int y, int w, int h, float eased) {
        int configuredWidth;
        int bottomColor;
        int topColor;
        float alphaFactor;
        int alpha;
        int radius = 0;
        TipData.VisualSettings.BackgroundType type = null;
        boolean rounded = true;
        if (this.background != null) {
            radius = Math.max(0, this.background.borderRadius());
            type = this.background.type();
            rounded = this.background.rounded();
        }
        if ((alpha = (int)((alphaFactor = eased) * 255.0f)) <= 0) {
            return;
        }
        if (type == TipData.VisualSettings.BackgroundType.GRADIENT && this.background.colors() != null && !this.background.colors().isEmpty()) {
            List<String> colors = this.background.colors();
            String fromHex = colors.get(0);
            String toHex = colors.get(colors.size() - 1);
            topColor = ColorUtil.parseArgb(fromHex);
            bottomColor = ColorUtil.parseArgb(toHex);
        } else if (type == TipData.VisualSettings.BackgroundType.SOLID && this.background.colors() != null && !this.background.colors().isEmpty()) {
            String hex = this.background.colors().get(0);
            bottomColor = topColor = ColorUtil.parseArgb(hex);
        } else if (this.hasThemeColor) {
            topColor = this.themeColor;
            bottomColor = this.themeColor;
        } else {
            bottomColor = topColor = ColorUtil.parseArgb(null);
        }
        float radiusPixels = rounded ? (float)radius : 0.0f;
        PanelRenderer.drawRoundedPanel(graphics, x, y, w, h, topColor, bottomColor, radiusPixels, 2.0f, (float)alpha / 255.0f);
        if (this.hasThemeColor && (configuredWidth = this.visualSettings.stripeWidth()) > 0) {
            int available;
            int actual;
            int stripeBase = this.themeColor;
            int stripeColor = ColorUtil.multiplyAlpha(stripeBase, alphaFactor);
            int stripeTop = y + Math.round(radiusPixels);
            int stripeBottom = y + h - Math.round(radiusPixels);
            float lengthFactor = this.visualSettings.stripeLengthFactor();
            if (lengthFactor < 0.0f) {
                lengthFactor = 0.0f;
            }
            if (lengthFactor > 1.0f) {
                lengthFactor = 1.0f;
            }
            if ((stripeBottom = stripeTop + (actual = Math.round((float)(available = stripeBottom - stripeTop) * lengthFactor))) > stripeTop) {
                graphics.m_280509_(x, stripeTop, x + configuredWidth, stripeBottom, stripeColor);
            }
        }
    }

    private void previousPage() {
        if (this.pages.size() <= 1) {
            return;
        }
        this.currentPage = this.currentPage > 0 ? --this.currentPage : this.pages.size() - 1;
        if (this.maxDuration > 0) {
            this.remainingTicks = this.maxDuration;
        }
    }

    private void nextPage() {
        if (this.pages.size() <= 1) {
            return;
        }
        this.currentPage = this.currentPage < this.pages.size() - 1 ? ++this.currentPage : 0;
        if (this.maxDuration > 0) {
            this.remainingTicks = this.maxDuration;
        }
    }

    private int drawTextElement(GuiGraphics graphics, ComponentSerialization.TextElement element, int x, int y) {
        float scale = element.scale();
        Component text = ResolveUtil.resolveVariables(element.text(), this.variables);
        int lineSpacing = element.lineSpacing();
        Font font = Minecraft.m_91087_().f_91062_;
        List lines = font.m_92923_((FormattedText)text, Integer.MAX_VALUE);
        if (lines.isEmpty()) {
            return y;
        }
        Objects.requireNonNull(font);
        int baseLineHeight = 9 + lineSpacing;
        graphics.m_280168_().m_85836_();
        graphics.m_280168_().m_252880_((float)x, (float)y, 0.0f);
        graphics.m_280168_().m_85841_(scale, scale, 1.0f);
        int drawY = 0;
        for (FormattedCharSequence line : lines) {
            graphics.m_280649_(font, line, 0, drawY, -1, true);
            drawY += baseLineHeight;
        }
        graphics.m_280168_().m_85849_();
        return y + (int)((float)(lines.size() * baseLineHeight) * scale);
    }

    private int drawDivider(GuiGraphics graphics, ComponentSerialization.Divider divider, int leftX, int rightX, int y) {
        int thickness = divider.thickness();
        int marginTop = divider.marginTop();
        int marginBottom = divider.marginBottom();
        float length = divider.length();
        int lineY = y + marginTop;
        int x1 = leftX;
        int x2 = Math.max(leftX, rightX);
        int span = x2 - x1;
        if (span <= 0) {
            return y;
        }
        float clamped = Mth.m_14036_((float)length, (float)0.0f, (float)1.0f);
        int lineWidth = Math.max(1, (int)((float)span * clamped));
        int startX = x1;
        int endX = x1 + lineWidth;
        String colorHex = divider.color();
        int argb = colorHex != null && !colorHex.isBlank() ? ColorUtil.parseArgb(colorHex) : (this.hasThemeColor ? ColorUtil.multiplyAlpha(this.themeColor, 0.8f) : 0x60FFFFFF);
        graphics.m_280509_(startX, lineY, endX, lineY + thickness, argb);
        return lineY + thickness + marginBottom;
    }

    private void drawImage(GuiGraphics graphics, TipData.ImageElement image, int panelX, int panelY, int panelWidth) {
        int y;
        int x;
        TipData.Position pos;
        ResourceLocation texture = new ResourceLocation(image.path());
        int[] size = image.size();
        int imgW = size.length > 0 ? size[0] : 64;
        int imgH = size.length > 1 ? size[1] : 64;
        float scale = image.scale();
        if (scale > 0.0f && scale != 1.0f) {
            imgW = Math.max(1, Math.round((float)imgW * scale));
            imgH = Math.max(1, Math.round((float)imgH * scale));
        }
        if ((pos = image.position()) != null && pos.absolute()) {
            x = panelX + pos.x();
            y = panelY + pos.y();
        } else {
            String normalized;
            String preset = pos != null && pos.preset() != null ? pos.preset() : "TOP_CENTER";
            switch (normalized = preset.toUpperCase(Locale.ROOT)) {
                case "TOP_LEFT": {
                    x = panelX;
                    y = panelY;
                    break;
                }
                case "TOP_RIGHT": {
                    x = panelX + panelWidth - imgW;
                    y = panelY;
                    break;
                }
                case "BOTTOM_LEFT": {
                    x = panelX;
                    y = panelY + this.panelHeight - imgH;
                    break;
                }
                case "BOTTOM_RIGHT": {
                    x = panelX + panelWidth - imgW;
                    y = panelY + this.panelHeight - imgH;
                    break;
                }
                case "BOTTOM_CENTER": {
                    x = panelX + (panelWidth - imgW) / 2;
                    y = panelY + this.panelHeight - imgH - 4;
                    break;
                }
                case "CENTER": 
                case "MIDDLE": {
                    x = panelX + (panelWidth - imgW) / 2;
                    y = panelY + (this.panelHeight - imgH) / 2;
                    break;
                }
                default: {
                    x = panelX + (panelWidth - imgW) / 2;
                    y = panelY + 8;
                }
            }
        }
        RenderSystem.setShader(GameRenderer::m_172817_);
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)texture);
        graphics.m_280163_(texture, x, y, 0.0f, 0.0f, imgW, imgH, imgW, imgH);
    }

    private void drawBadge(GuiGraphics graphics, TipData.Badge badge, int panelX, int panelY, int panelW, int panelH) {
        int by;
        int bx;
        ComponentSerialization.TextElement el = badge.text();
        Font font = Minecraft.m_91087_().f_91062_;
        Component text = ResolveUtil.resolveVariables(el.text(), this.variables);
        float scale = Math.max(0.1f, el.scale());
        List lines = font.m_92923_((FormattedText)text, Integer.MAX_VALUE);
        if (lines.isEmpty()) {
            return;
        }
        Objects.requireNonNull(font);
        int baseLineHeight = 9 + el.lineSpacing();
        int textW = 0;
        for (FormattedCharSequence line : lines) {
            int w = font.m_92724_(line);
            if (w <= textW) continue;
            textW = w;
        }
        int scaledW = (int)((float)textW * scale);
        int scaledH = (int)((float)(lines.size() * baseLineHeight) * scale);
        int padX = 6;
        int padY = 3;
        int bgW = scaledW + padX * 2;
        int bgH = scaledH + padY * 2;
        int radius = Math.max(0, badge.radius());
        TipData.Position pos = badge.position();
        if (pos != null && pos.absolute()) {
            bx = panelX + pos.x();
            by = panelY + pos.y();
        } else {
            String preset = pos != null && pos.preset() != null ? pos.preset() : "BOTTOM_RIGHT";
            switch (preset.toUpperCase(Locale.ROOT)) {
                case "TOP_LEFT": {
                    bx = panelX;
                    by = panelY;
                    break;
                }
                case "TOP_CENTER": {
                    bx = panelX + (panelW - bgW) / 2;
                    by = panelY;
                    break;
                }
                case "TOP_RIGHT": {
                    bx = panelX + panelW - bgW;
                    by = panelY;
                    break;
                }
                case "LEFT_CENTER": {
                    bx = panelX;
                    by = panelY + (panelH - bgH) / 2;
                    break;
                }
                case "CENTER": 
                case "MIDDLE": {
                    bx = panelX + (panelW - bgW) / 2;
                    by = panelY + (panelH - bgH) / 2;
                    break;
                }
                case "RIGHT_CENTER": {
                    bx = panelX + panelW - bgW;
                    by = panelY + (panelH - bgH) / 2;
                    break;
                }
                case "BOTTOM_LEFT": {
                    bx = panelX;
                    by = panelY + panelH - bgH;
                    break;
                }
                case "BOTTOM_CENTER": {
                    bx = panelX + (panelW - bgW) / 2;
                    by = panelY + panelH - bgH;
                    break;
                }
                default: {
                    bx = panelX + panelW - bgW;
                    by = panelY + panelH - bgH;
                }
            }
        }
        int bgColor = badge.backgroundColor();
        float bgAlpha = (float)(bgColor >>> 24 & 0xFF) / 255.0f;
        PanelRenderer.drawRoundedPanel(graphics, bx, by, bgW, bgH, bgColor, bgColor, radius, 1.0f, bgAlpha);
        this.drawTextElement(graphics, el, bx + padX, by + padY);
    }

    private void startClosing() {
        if (!this.closing) {
            this.closing = true;
            this.animationStartMs = Util.m_137550_();
        }
    }
}

