/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.MobType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.EnchantmentCategory
 */
package net.minecraftforge.common.extensions;

import java.util.Set;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public interface IForgeEnchantment {
    private Enchantment self() {
        return (Enchantment)this;
    }

    default public float getDamageBonus(int level, MobType mobType, ItemStack enchantedItem) {
        return this.self().m_7335_(level, mobType);
    }

    default public boolean allowedInCreativeTab(Item book, Set<EnchantmentCategory> allowedCategories) {
        return this.self().isAllowedOnBooks() && allowedCategories.contains(this.self().f_44672_);
    }
}

