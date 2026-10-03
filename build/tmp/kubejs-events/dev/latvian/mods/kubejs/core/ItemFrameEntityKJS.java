/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.world.entity.decoration.ItemFrame
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.core.EntityKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS(value="kjs$")
public interface ItemFrameEntityKJS
extends EntityKJS {
    default public ItemFrame kjs$self() {
        return (ItemFrame)this;
    }

    @Override
    default public boolean kjs$isFrame() {
        return true;
    }

    @Override
    @Nullable
    default public ItemStack kjs$getItem() {
        ItemStack stack = this.kjs$self().m_31822_();
        return stack.m_41619_() ? null : stack;
    }
}

