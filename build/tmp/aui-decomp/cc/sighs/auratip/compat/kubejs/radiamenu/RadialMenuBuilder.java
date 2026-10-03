/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.compat.kubejs.radiamenu;

import cc.sighs.auratip.api.radiamenu.icon.ItemIcon;
import cc.sighs.auratip.api.radiamenu.icon.TextureIcon;
import cc.sighs.auratip.data.RadialMenuData;
import cc.sighs.auratip.data.action.Action;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class RadialMenuBuilder {
    private final cc.sighs.auratip.api.radiamenu.RadialMenuBuilder delegate;

    public RadialMenuBuilder(String id) {
        this.delegate = new cc.sighs.auratip.api.radiamenu.RadialMenuBuilder(RadialMenuBuilder.normalizeId(id));
    }

    public RadialMenuBuilder radii(int inner, int outer) {
        this.delegate.radii(inner, outer);
        return this;
    }

    public RadialMenuBuilder animationSpeed(float speed) {
        this.delegate.animationSpeed(speed);
        return this;
    }

    public RadialMenuBuilder centerIcon(@Nullable String iconId) {
        if (iconId == null || iconId.isEmpty()) {
            this.delegate.centerIcon(null);
        } else {
            this.delegate.centerIcon(new ResourceLocation(iconId));
        }
        return this;
    }

    public RadialMenuBuilder ringColor(@Nullable String color) {
        this.delegate.ringColor(color);
        return this;
    }

    public RadialMenuBuilder ringColors(@Nullable List<String> colors) {
        this.delegate.ringColors(colors);
        return this;
    }

    public RadialMenuBuilder closeKey(@Nullable String key) {
        this.delegate.closeKey(key);
        return this;
    }

    public RadialMenuBuilder slot(String name, String iconId, Action action, @Nullable Component text, @Nullable String highlightColor) {
        return this.slot(name, iconId, action, text, highlightColor, true, null);
    }

    public RadialMenuBuilder slot(String name, String iconId, Action action, @Nullable Component text, @Nullable String highlightColor, boolean closeAfterAction) {
        return this.slot(name, iconId, action, text, highlightColor, closeAfterAction, null);
    }

    public RadialMenuBuilder slot(String name, String iconId, Action action, @Nullable Component text, @Nullable String highlightColor, boolean closeAfterAction, @Nullable String baseColor) {
        Objects.requireNonNull(iconId, "iconId");
        this.delegate.slot(name, new TextureIcon(new ResourceLocation(iconId), 1.0f), action, text, highlightColor, closeAfterAction, baseColor);
        return this;
    }

    public RadialMenuBuilder persistentSlot(String name, String iconId, Action action, @Nullable Component text, @Nullable String highlightColor) {
        return this.slot(name, iconId, action, text, highlightColor, false, null);
    }

    public RadialMenuBuilder persistentSlot(String name, String iconId, Action action, @Nullable Component text, @Nullable String highlightColor, @Nullable String baseColor) {
        return this.slot(name, iconId, action, text, highlightColor, false, baseColor);
    }

    public RadialMenuBuilder slotWithIcon(String name, TextureIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor) {
        return this.slotWithIcon(name, icon, action, text, highlightColor, true, null);
    }

    public RadialMenuBuilder slotWithIcon(String name, TextureIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor, boolean closeAfterAction) {
        return this.slotWithIcon(name, icon, action, text, highlightColor, closeAfterAction, null);
    }

    public RadialMenuBuilder slotWithIcon(String name, TextureIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor, boolean closeAfterAction, @Nullable String baseColor) {
        this.delegate.slot(name, icon, action, text, highlightColor, closeAfterAction, baseColor);
        return this;
    }

    public RadialMenuBuilder persistentSlotWithIcon(String name, TextureIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor) {
        return this.persistentSlotWithIcon(name, icon, action, text, highlightColor, null);
    }

    public RadialMenuBuilder persistentSlotWithIcon(String name, TextureIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor, @Nullable String baseColor) {
        return this.slotWithIcon(name, icon, action, text, highlightColor, false, baseColor);
    }

    public RadialMenuBuilder slotItem(String name, ItemIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor) {
        return this.slotItem(name, icon, action, text, highlightColor, true, null);
    }

    public RadialMenuBuilder slotItem(String name, ItemIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor, boolean closeAfterAction) {
        return this.slotItem(name, icon, action, text, highlightColor, closeAfterAction, null);
    }

    public RadialMenuBuilder slotItem(String name, ItemIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor, boolean closeAfterAction, @Nullable String baseColor) {
        this.delegate.slot(name, icon, action, text, highlightColor, closeAfterAction, baseColor);
        return this;
    }

    public RadialMenuBuilder persistentSlotItem(String name, ItemIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor) {
        return this.persistentSlotItem(name, icon, action, text, highlightColor, null);
    }

    public RadialMenuBuilder persistentSlotItem(String name, ItemIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor, @Nullable String baseColor) {
        return this.slotItem(name, icon, action, text, highlightColor, false, baseColor);
    }

    public RadialMenuData build() {
        return this.delegate.build();
    }

    private static ResourceLocation normalizeId(String id) {
        if (id == null || id.isEmpty()) {
            return new ResourceLocation("kubejs", "radial_menu");
        }
        if (id.indexOf(58) < 0) {
            return new ResourceLocation("kubejs", id);
        }
        return new ResourceLocation(id);
    }
}

