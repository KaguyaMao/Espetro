/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.inventory.ClickType
 */
package dev.latvian.mods.kubejs.gui.chest;

import dev.latvian.mods.kubejs.gui.chest.ChestMenuClickEvent;
import net.minecraft.world.inventory.ClickType;

public record ChestMenuClickHandler(ClickType type, int button, ChestMenuClickEvent.Callback callback, boolean autoHandle) {
    public boolean test(ChestMenuClickEvent event) {
        return !(this.type != null && this.type != event.type || this.button != -1 && this.button != event.button);
    }
}

