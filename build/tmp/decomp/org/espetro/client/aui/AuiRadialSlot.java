/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.aui;

import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record AuiRadialSlot(String id, Component label, ResourceLocation texture, ItemStack item, String glyph, String accent, boolean enabled, Runnable action) {
    public AuiRadialSlot {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(label, "label");
        Objects.requireNonNull(action, "action");
        item = item == null ? ItemStack.f_41583_ : item;
        accent = accent == null || accent.isBlank() ? "#FFD5B25C" : accent;
    }

    public static AuiRadialSlot texture(String id, Component label, ResourceLocation texture, String accent, Runnable action) {
        return AuiRadialSlot.texture(id, label, texture, accent, true, action);
    }

    public static AuiRadialSlot texture(String id, Component label, ResourceLocation texture, String accent, boolean enabled, Runnable action) {
        return new AuiRadialSlot(id, label, texture, ItemStack.f_41583_, null, accent, enabled, action);
    }

    public static AuiRadialSlot item(String id, Component label, ItemStack stack, String accent, boolean enabled, Runnable action) {
        return new AuiRadialSlot(id, label, null, stack, null, accent, enabled, action);
    }

    public static AuiRadialSlot glyph(String id, Component label, String glyph, String accent, Runnable action) {
        return new AuiRadialSlot(id, label, null, ItemStack.f_41583_, glyph, accent, true, action);
    }
}

