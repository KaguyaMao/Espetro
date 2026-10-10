/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 */
package cc.sighs.auratip.client.render;

import cc.sighs.auratip.api.radiamenu.icon.IRadialIcon;
import cc.sighs.auratip.client.render.RadialMenuInputPolicy;
import cc.sighs.auratip.client.render.RadialMenuOverlayState;
import cc.sighs.auratip.client.render.RadialMenuSlotColors;
import cc.sighs.auratip.client.render.RingRenderer;
import cc.sighs.auratip.data.RadialMenuData;
import cc.sighs.auratip.data.action.Action;
import cc.sighs.auratip.data.action.ActionExecutor;
import cc.sighs.auratip.editor.client.EditorClient;
import cc.sighs.auratip.util.ColorUtil;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RadialMenuOverlay {
    public static final RadialMenuOverlay INSTANCE = new RadialMenuOverlay();
    private RadialMenuData menu;
    private List<RadialMenuData.Slot> slots;
    private final RadialMenuOverlayState state = new RadialMenuOverlayState();
    private int centerX;
    private int centerY;
    private float innerRadius;
    private float outerRadius;
    private int ringInnerArgb;
    private int ringOuterArgb;
    private int[] ringArgbStops;
    private long animationStartMs;
    private static final int OPEN_MS_BASE = 200;
    private static final int CLOSE_MS_BASE = 140;
    private int openDurationMs;
    private int closeDurationMs;
    private float activeFill;
    private long lastFillUpdateMs;
    private Minecraft mc;
    private InputConstants.Key closeKey;

    private RadialMenuOverlay() {
    }

    public boolean isActive() {
        return this.state.isActive();
    }

    public Optional<ResourceLocation> activeMenuId() {
        return this.state.activeMenuId();
    }

    public int hoveredSlotIndex() {
        return this.state.hoveredIndex();
    }

    public void open(RadialMenuData menu, int screenWidth, int screenHeight, Minecraft mc) {
        this.mc = mc;
        this.menu = menu;
        this.slots = menu.slots();
        this.state.open(menu.id(), this.slots.size());
        this.centerX = screenWidth / 2;
        this.centerY = screenHeight / 2;
        this.animationStartMs = Util.m_137550_();
        this.applyMenuSettings(menu);
        this.activeFill = 0.0f;
        this.lastFillUpdateMs = this.animationStartMs;
        mc.f_91067_.m_91602_();
    }

    public boolean replace(RadialMenuData replacement) {
        if (replacement == null || !this.state.isActive()) {
            return false;
        }
        boolean wasClosing = this.state.isClosing();
        if (!this.state.replace(replacement.id(), replacement.slots().size())) {
            return false;
        }
        this.menu = replacement;
        this.slots = replacement.slots();
        this.applyMenuSettings(replacement);
        if (this.state.activeIndex() < 0) {
            this.activeFill = 0.0f;
        }
        if (wasClosing) {
            this.animationStartMs = Util.m_137550_() - (long)this.openDurationMs;
            if (this.mc != null && this.mc.f_91080_ == null) {
                this.mc.f_91067_.m_91602_();
            }
        }
        this.lastFillUpdateMs = Util.m_137550_();
        return true;
    }

    public void close() {
        if (!this.state.isActive() || this.state.isClosing()) {
            return;
        }
        this.state.beginClose();
        this.animationStartMs = Util.m_137550_();
        if (this.mc != null && this.mc.f_91080_ == null) {
            this.mc.f_91067_.m_91601_();
        }
    }

    public void tick() {
    }

    public void render(GuiGraphics graphics, float partialTick, int mouseX, int mouseY, int screenWidth, int screenHeight) {
        float progress;
        int duration;
        if (this.menu == null || this.slots == null) {
            return;
        }
        long now = Util.m_137550_();
        int elapsed = (int)Math.max(0L, now - this.animationStartMs);
        boolean closing = this.state.isClosing();
        int n = duration = closing ? this.closeDurationMs : this.openDurationMs;
        if (duration <= 0) {
            duration = closing ? 140 : 200;
        }
        float t = (float)elapsed / (float)duration;
        t = Mth.m_14036_((float)t, (float)0.0f, (float)1.0f);
        float f = progress = closing ? 1.0f - t : t;
        if (closing && t >= 1.0f) {
            this.menu = null;
            this.slots = null;
            this.state.finishClose();
            this.activeFill = 0.0f;
            return;
        }
        float eased = progress * progress;
        float ringInner = this.innerRadius * eased;
        float ringOuter = this.outerRadius * eased;
        if (this.ringArgbStops == null || this.ringArgbStops.length < 2) {
            int finalInner = ColorUtil.multiplyAlpha(this.ringInnerArgb, eased);
            int finalOuter = ColorUtil.multiplyAlpha(this.ringOuterArgb, eased);
            RingRenderer.drawRing(graphics, this.centerX, this.centerY, ringInner, ringOuter, 0.0f, 360.0f, finalInner, finalOuter, 1.5f);
        } else {
            int segments = this.ringArgbStops.length - 1;
            float total = ringOuter - ringInner;
            for (int i = 0; i < segments; ++i) {
                float segInner = ringInner + total * ((float)i / (float)segments);
                float segOuter = ringInner + total * ((float)(i + 1) / (float)segments);
                int c0 = ColorUtil.multiplyAlpha(this.ringArgbStops[i], eased);
                int c1 = ColorUtil.multiplyAlpha(this.ringArgbStops[i + 1], eased);
                RingRenderer.drawRing(graphics, this.centerX, this.centerY, segInner, segOuter, 0.0f, 360.0f, c0, c1, 1.5f);
            }
        }
        int count = this.slots.size();
        if (count == 0) {
            return;
        }
        double dx = mouseX - this.centerX;
        double dy = mouseY - this.centerY;
        double dist = Math.sqrt(dx * dx + dy * dy);
        int hoveredIndex = -1;
        if (dist >= (double)ringInner && dist <= (double)ringOuter && dist > 0.0) {
            double rad = Math.atan2(dx, -dy);
            double deg = Math.toDegrees(rad);
            if (deg < 0.0) {
                deg += 360.0;
            }
            double slice = 360.0 / (double)count;
            double halfSlice = slice / 2.0;
            hoveredIndex = (int)((deg + halfSlice) / slice) % count;
        }
        this.state.hoveredIndex(hoveredIndex);
        if (hoveredIndex >= 0 && hoveredIndex != this.state.activeIndex()) {
            this.state.activeIndex(hoveredIndex);
            this.activeFill = 0.0f;
            this.lastFillUpdateMs = now;
        }
        float targetFill = hoveredIndex >= 0 ? 1.0f : 0.0f;
        long dtMs = Math.max(0L, now - this.lastFillUpdateMs);
        dtMs = Math.min(dtMs, 250L);
        double dtSec = (double)dtMs / 1000.0;
        double alpha = 1.0 - Math.exp(-34.5218 * dtSec);
        this.activeFill += (float)((double)(targetFill - this.activeFill) * alpha);
        this.activeFill = Mth.m_14036_((float)this.activeFill, (float)0.0f, (float)1.0f);
        this.lastFillUpdateMs = now;
        if (hoveredIndex < 0 && this.activeFill <= 0.001f) {
            this.state.activeIndex(-1);
        }
        double sliceDegrees = 360.0 / (double)count;
        for (int i = 0; i < count; ++i) {
            RadialMenuData.Slot slot = this.slots.get(i);
            float startDeg = (float)(-sliceDegrees / 2.0 + (double)i * sliceDegrees);
            float endDeg = startDeg + (float)sliceDegrees;
            for (RadialMenuSlotColors.Layer layer : RadialMenuSlotColors.layers(slot, i == this.state.activeIndex(), this.activeFill, eased)) {
                RingRenderer.drawRing(graphics, this.centerX, this.centerY, ringInner, ringOuter, startDeg, endDeg, layer.innerArgb(), layer.outerArgb(), 1.5f, layer.fill());
            }
        }
        double iconRadius = (double)ringInner + (double)(ringOuter - ringInner) * 0.65;
        for (int i = 0; i < count; ++i) {
            RadialMenuData.Slot slot = this.slots.get(i);
            double slice = Math.PI * 2 / (double)count;
            double angle = slice * (double)i - 1.5707963267948966;
            int iconX = this.centerX + (int)(iconRadius * Math.cos(angle));
            int iconY = this.centerY + (int)(iconRadius * Math.sin(angle));
            float hoverScale = i == this.state.activeIndex() ? this.activeFill : 0.0f;
            float scale = 1.0f + 0.25f * hoverScale;
            this.drawIcon(graphics, slot.icon(), iconX, iconY, scale * eased, eased);
        }
        if (hoveredIndex >= 0 && hoveredIndex < this.slots.size()) {
            RadialMenuData.Slot hovered = this.slots.get(hoveredIndex);
            Component text = hovered.text().orElseGet(() -> Component.m_237115_((String)hovered.name()));
            int textWidth = Minecraft.m_91087_().f_91062_.m_92852_((FormattedText)text);
            Objects.requireNonNull(Minecraft.m_91087_().f_91062_);
            int textY = this.centerY - 9 / 2;
            graphics.m_280430_(Minecraft.m_91087_().f_91062_, text, this.centerX - textWidth / 2, textY, -1);
        }
    }

    public boolean keyPressed(int keyCode) {
        int customKeyCode;
        if (!this.state.isActive()) {
            return false;
        }
        int n = customKeyCode = this.closeKey == null ? -1 : this.closeKey.m_84873_();
        if (RadialMenuInputPolicy.isCloseKey(keyCode, customKeyCode)) {
            this.close();
            return true;
        }
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.state.isActive() || button != 0) {
            return false;
        }
        if (EditorClient.isOpen()) {
            return true;
        }
        int clickedIndex = this.state.hoveredIndex();
        if (RadialMenuInputPolicy.isOutsideClick(clickedIndex, this.slots.size())) {
            this.close();
            return true;
        }
        RadialMenuData.Slot slot = this.slots.get(clickedIndex);
        long actionGeneration = this.state.generation();
        this.state.press(clickedIndex);
        Action action = slot.action();
        ActionExecutor.execute(action);
        if (this.state.shouldCloseAfterAction(slot.closeAfterAction(), actionGeneration)) {
            this.close();
        }
        return true;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!this.state.isActive() || button != 0) {
            return false;
        }
        this.state.release();
        return true;
    }

    private void applyMenuSettings(RadialMenuData menu) {
        this.innerRadius = menu.menuSettings().innerRadius();
        this.outerRadius = menu.menuSettings().outerRadius();
        this.ringInnerArgb = -15722974;
        this.ringOuterArgb = -15722974;
        this.ringArgbStops = null;
        Optional<List<String>> colorsOpt = menu.menuSettings().ringColors();
        if (colorsOpt.isPresent() && !colorsOpt.get().isEmpty()) {
            List<String> list = colorsOpt.get();
            this.ringArgbStops = new int[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                this.ringArgbStops[i] = ColorUtil.parseArgb(list.get(i));
            }
            this.ringInnerArgb = this.ringArgbStops[0];
            this.ringOuterArgb = this.ringArgbStops[this.ringArgbStops.length - 1];
        } else {
            menu.menuSettings().ringColor().ifPresent(color -> {
                int argb;
                this.ringInnerArgb = argb = ColorUtil.parseArgb(color);
                this.ringOuterArgb = argb;
                this.ringArgbStops = new int[]{argb, argb};
            });
        }
        float speed = menu.menuSettings().animationSpeed();
        if (speed <= 0.0f) {
            speed = 1.0f;
        }
        this.openDurationMs = Math.max(1, (int)(200.0f / speed));
        this.closeDurationMs = Math.max(1, (int)(140.0f / speed));
        this.closeKey = null;
        menu.menuSettings().closeKey().ifPresent(key -> {
            try {
                this.closeKey = InputConstants.m_84851_((String)key);
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
    }

    private void drawIcon(GuiGraphics graphics, IRadialIcon icon, int x, int y, float scale, float alpha) {
        icon.render(graphics, x, y, scale, alpha);
    }
}

