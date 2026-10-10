/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.alchemy.Potion
 *  net.minecraft.world.item.alchemy.PotionUtils
 */
package net.minecraftforge.common.extensions;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

public interface IForgePotion {
    private Potion self() {
        return (Potion)this;
    }

    default public boolean isFoil(ItemStack stack) {
        return !PotionUtils.m_43547_((ItemStack)stack).isEmpty();
    }
}

