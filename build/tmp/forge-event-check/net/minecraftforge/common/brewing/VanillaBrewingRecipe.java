/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.alchemy.PotionBrewing
 */
package net.minecraftforge.common.brewing;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraftforge.common.brewing.IBrewingRecipe;

public class VanillaBrewingRecipe
implements IBrewingRecipe {
    @Override
    public boolean isInput(ItemStack stack) {
        Item item = stack.m_41720_();
        return item == Items.f_42589_ || item == Items.f_42736_ || item == Items.f_42739_ || item == Items.f_42590_;
    }

    @Override
    public boolean isIngredient(ItemStack stack) {
        return PotionBrewing.m_43506_((ItemStack)stack);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (!input.m_41619_() && !ingredient.m_41619_() && this.isIngredient(ingredient)) {
            ItemStack result = PotionBrewing.m_43529_((ItemStack)ingredient, (ItemStack)input);
            if (result != input) {
                return result;
            }
            return ItemStack.f_41583_;
        }
        return ItemStack.f_41583_;
    }
}

