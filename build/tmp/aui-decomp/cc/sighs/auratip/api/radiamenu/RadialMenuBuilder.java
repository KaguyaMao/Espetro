/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 */
package cc.sighs.auratip.api.radiamenu;

import cc.sighs.auratip.api.radiamenu.icon.IRadialIcon;
import cc.sighs.auratip.api.radiamenu.icon.ItemIcon;
import cc.sighs.auratip.api.radiamenu.icon.TextureIcon;
import cc.sighs.auratip.data.RadialMenuData;
import cc.sighs.auratip.data.action.Action;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class RadialMenuBuilder {
    private final ResourceLocation id;
    private final List<RadialMenuData.Slot> slots = new ArrayList<RadialMenuData.Slot>();
    private int innerRadius = 40;
    private int outerRadius = 80;
    private float animationSpeed = 1.0f;
    private ResourceLocation centerIcon;
    private String ringColor;
    private List<String> ringColors = new ArrayList<String>();
    private String closeKey;

    public RadialMenuBuilder(ResourceLocation id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    public ResourceLocation id() {
        return this.id;
    }

    public RadialMenuBuilder radii(int inner, int outer) {
        this.innerRadius = inner;
        this.outerRadius = outer;
        return this;
    }

    public RadialMenuBuilder animationSpeed(float speed) {
        this.animationSpeed = speed;
        return this;
    }

    public RadialMenuBuilder centerIcon(@Nullable ResourceLocation icon) {
        this.centerIcon = icon;
        return this;
    }

    public RadialMenuBuilder ringColor(@Nullable String color) {
        this.ringColor = color;
        return this;
    }

    public RadialMenuBuilder ringColors(@Nullable List<String> colors) {
        this.ringColors = colors == null ? new ArrayList<String>() : new ArrayList<String>(colors);
        return this;
    }

    public RadialMenuBuilder closeKey(@Nullable String key) {
        this.closeKey = key;
        return this;
    }

    public RadialMenuBuilder slot(String name, IRadialIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor) {
        return this.slot(name, icon, action, text, highlightColor, true, null);
    }

    public RadialMenuBuilder slot(String name, ResourceLocation icon, Action action, @Nullable Component text, @Nullable String highlightColor) {
        return icon == null ? this : this.slot(name, new TextureIcon(icon), action, text, highlightColor);
    }

    public RadialMenuBuilder slot(String name, ItemStack icon, Action action, @Nullable Component text, @Nullable String highlightColor) {
        return icon == null || icon.m_41619_() ? this : this.slot(name, new ItemIcon(icon), action, text, highlightColor);
    }

    public RadialMenuBuilder slot(String name, IRadialIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor, boolean closeAfterAction) {
        return this.slot(name, icon, action, text, highlightColor, closeAfterAction, null);
    }

    public RadialMenuBuilder slot(String name, ResourceLocation icon, Action action, @Nullable Component text, @Nullable String highlightColor, boolean closeAfterAction) {
        return icon == null ? this : this.slot(name, new TextureIcon(icon), action, text, highlightColor, closeAfterAction);
    }

    public RadialMenuBuilder slot(String name, ItemStack icon, Action action, @Nullable Component text, @Nullable String highlightColor, boolean closeAfterAction) {
        return icon == null || icon.m_41619_() ? this : this.slot(name, new ItemIcon(icon), action, text, highlightColor, closeAfterAction);
    }

    public RadialMenuBuilder slot(String name, IRadialIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor, boolean closeAfterAction, @Nullable String baseColor) {
        if (name == null || name.isEmpty() || icon == null || action == null) {
            return this;
        }
        RadialMenuData.Slot slot = new RadialMenuData.Slot(name, icon, action, Optional.ofNullable(text), Optional.ofNullable(highlightColor), closeAfterAction, Optional.ofNullable(baseColor));
        this.slots.add(slot);
        return this;
    }

    public RadialMenuBuilder slot(String name, ResourceLocation icon, Action action, @Nullable Component text, @Nullable String highlightColor, boolean closeAfterAction, @Nullable String baseColor) {
        return icon == null ? this : this.slot(name, new TextureIcon(icon), action, text, highlightColor, closeAfterAction, baseColor);
    }

    public RadialMenuBuilder slot(String name, ItemStack icon, Action action, @Nullable Component text, @Nullable String highlightColor, boolean closeAfterAction, @Nullable String baseColor) {
        return icon == null || icon.m_41619_() ? this : this.slot(name, new ItemIcon(icon), action, text, highlightColor, closeAfterAction, baseColor);
    }

    public RadialMenuBuilder persistentSlot(String name, IRadialIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor) {
        return this.slot(name, icon, action, text, highlightColor, false, null);
    }

    public RadialMenuBuilder persistentSlot(String name, ResourceLocation icon, Action action, @Nullable Component text, @Nullable String highlightColor) {
        return icon == null ? this : this.persistentSlot(name, new TextureIcon(icon), action, text, highlightColor);
    }

    public RadialMenuBuilder persistentSlot(String name, ItemStack icon, Action action, @Nullable Component text, @Nullable String highlightColor) {
        return icon == null || icon.m_41619_() ? this : this.persistentSlot(name, new ItemIcon(icon), action, text, highlightColor);
    }

    public RadialMenuBuilder persistentSlot(String name, IRadialIcon icon, Action action, @Nullable Component text, @Nullable String highlightColor, @Nullable String baseColor) {
        return this.slot(name, icon, action, text, highlightColor, false, baseColor);
    }

    public RadialMenuBuilder persistentSlot(String name, ResourceLocation icon, Action action, @Nullable Component text, @Nullable String highlightColor, @Nullable String baseColor) {
        return icon == null ? this : this.persistentSlot(name, new TextureIcon(icon), action, text, highlightColor, baseColor);
    }

    public RadialMenuBuilder persistentSlot(String name, ItemStack icon, Action action, @Nullable Component text, @Nullable String highlightColor, @Nullable String baseColor) {
        return icon == null || icon.m_41619_() ? this : this.persistentSlot(name, new ItemIcon(icon), action, text, highlightColor, baseColor);
    }

    public RadialMenuData build() {
        RadialMenuData.MenuSettings settings = new RadialMenuData.MenuSettings(this.innerRadius, this.outerRadius, this.animationSpeed, Optional.ofNullable(this.centerIcon), Optional.ofNullable(this.ringColor), this.ringColors == null || this.ringColors.isEmpty() ? Optional.empty() : Optional.of(List.copyOf(this.ringColors)), Optional.ofNullable(this.closeKey));
        return new RadialMenuData(this.id, settings, List.copyOf(this.slots));
    }
}

