/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.NonNullList
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.event.brewing;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

public class PotionBrewEvent
extends Event {
    private NonNullList<ItemStack> stacks;

    protected PotionBrewEvent(NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
    }

    @NotNull
    public ItemStack getItem(int index) {
        if (index < 0 || index >= this.stacks.size()) {
            return ItemStack.f_41583_;
        }
        return (ItemStack)this.stacks.get(index);
    }

    public void setItem(int index, @NotNull ItemStack stack) {
        if (index < this.stacks.size()) {
            this.stacks.set(index, (Object)stack);
        }
    }

    public int getLength() {
        return this.stacks.size();
    }

    public static class Post
    extends PotionBrewEvent {
        public Post(NonNullList<ItemStack> stacks) {
            super(stacks);
        }
    }

    @Cancelable
    public static class Pre
    extends PotionBrewEvent {
        public Pre(NonNullList<ItemStack> stacks) {
            super(stacks);
        }
    }
}

