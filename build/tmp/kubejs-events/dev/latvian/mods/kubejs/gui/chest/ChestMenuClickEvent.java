/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.inventory.ClickType
 */
package dev.latvian.mods.kubejs.gui.chest;

import dev.latvian.mods.kubejs.gui.chest.ChestMenuSlot;
import net.minecraft.world.inventory.ClickType;

public class ChestMenuClickEvent {
    public final ChestMenuSlot slot;
    public final ClickType type;
    public final int button;
    public transient boolean handled;

    public ChestMenuClickEvent(ChestMenuSlot slot, ClickType type, int button) {
        this.slot = slot;
        this.type = type;
        this.button = button;
        this.handled = false;
    }

    public void setHandled() {
        this.handled = true;
    }

    public static interface Callback {
        public void onClick(ChestMenuClickEvent var1);
    }
}

