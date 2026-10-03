/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event
 */
package net.minecraftforge.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public abstract class GrindstoneEvent
extends Event {
    private final ItemStack top;
    private final ItemStack bottom;
    private int xp;

    protected GrindstoneEvent(ItemStack top, ItemStack bottom, int xp) {
        this.top = top;
        this.bottom = bottom;
        this.xp = xp;
    }

    public ItemStack getTopItem() {
        return this.top;
    }

    public ItemStack getBottomItem() {
        return this.bottom;
    }

    public int getXp() {
        return this.xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    @Cancelable
    public static class OnTakeItem
    extends GrindstoneEvent {
        private ItemStack newTop = ItemStack.f_41583_;
        private ItemStack newBottom = ItemStack.f_41583_;

        public OnTakeItem(ItemStack top, ItemStack bottom, int xp) {
            super(top, bottom, xp);
        }

        public ItemStack getNewTopItem() {
            return this.newTop;
        }

        public ItemStack getNewBottomItem() {
            return this.newBottom;
        }

        public void setNewTopItem(ItemStack newTop) {
            this.newTop = newTop;
        }

        public void setNewBottomItem(ItemStack newBottom) {
            this.newBottom = newBottom;
        }

        @Override
        public int getXp() {
            return super.getXp();
        }
    }

    @Cancelable
    public static class OnPlaceItem
    extends GrindstoneEvent {
        private ItemStack output = ItemStack.f_41583_;

        public OnPlaceItem(ItemStack top, ItemStack bottom, int xp) {
            super(top, bottom, xp);
        }

        public ItemStack getOutput() {
            return this.output;
        }

        public void setOutput(ItemStack output) {
            this.output = output;
        }
    }
}

