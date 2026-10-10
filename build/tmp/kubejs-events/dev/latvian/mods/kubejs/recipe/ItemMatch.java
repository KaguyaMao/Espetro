/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 */
package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public interface ItemMatch
extends ReplacementMatch {
    public boolean contains(ItemStack var1);

    public boolean contains(Ingredient var1);

    @Deprecated(forRemoval=true)
    default public boolean contains(Block block) {
        Item item = block.m_5456_();
        return item != Items.f_41852_ && this.contains(item.m_7968_());
    }

    default public boolean contains(ItemLike itemLike) {
        Item item = itemLike.m_5456_();
        return item != Items.f_41852_ && this.contains(item.m_7968_());
    }

    default public boolean containsAny(ItemLike ... itemLikes) {
        for (ItemLike item : itemLikes) {
            if (!this.contains(item)) continue;
            return true;
        }
        return false;
    }

    default public boolean containsAny(Iterable<ItemLike> itemLikes) {
        for (ItemLike item : itemLikes) {
            if (!this.contains(item)) continue;
            return true;
        }
        return false;
    }
}

