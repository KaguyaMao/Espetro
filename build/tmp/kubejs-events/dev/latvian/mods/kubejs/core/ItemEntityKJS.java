/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.hooks.level.entity.ItemEntityHooks
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import dev.architectury.hooks.level.entity.ItemEntityHooks;
import dev.latvian.mods.kubejs.core.EntityKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS(value="kjs$")
public interface ItemEntityKJS
extends EntityKJS {
    default public ItemEntity kjs$self() {
        return (ItemEntity)this;
    }

    @Override
    @Nullable
    default public ItemStack kjs$getItem() {
        ItemStack stack = this.kjs$self().m_32055_();
        return stack.m_41619_() ? null : stack;
    }

    default public int kjs$getLifespan() {
        return ItemEntityHooks.lifespan((ItemEntity)this.kjs$self()).getAsInt();
    }

    default public void kjs$setLifespan(int lifespan) {
        ItemEntityHooks.lifespan((ItemEntity)this.kjs$self()).accept(lifespan);
    }

    default public void kjs$setDefaultPickUpDelay() {
        this.kjs$self().m_32010_(10);
    }

    default public void kjs$setNoPickUpDelay() {
        this.kjs$self().m_32010_(0);
    }

    default public void kjs$setInfinitePickUpDelay() {
        this.kjs$self().m_32010_(Short.MAX_VALUE);
    }

    default public void kjs$setNoDespawn() {
        this.kjs$self().m_149678_();
    }

    default public int kjs$getTicksUntilDespawn() {
        return this.kjs$getLifespan() - this.kjs$self().f_31985_;
    }

    default public void kjs$setTicksUntilDespawn(int ticks) {
        this.kjs$self().f_31985_ = this.kjs$getLifespan() - ticks;
    }
}

