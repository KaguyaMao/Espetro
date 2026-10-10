/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Tier
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.block.Block
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.common;

import java.util.function.Supplier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public final class ForgeTier
implements Tier {
    private final int level;
    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int enchantmentValue;
    @NotNull
    private final TagKey<Block> tag;
    @NotNull
    private final Supplier<Ingredient> repairIngredient;

    public ForgeTier(int level, int uses, float speed, float attackDamageBonus, int enchantmentValue, @NotNull TagKey<Block> tag, @NotNull Supplier<Ingredient> repairIngredient) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.attackDamageBonus = attackDamageBonus;
        this.enchantmentValue = enchantmentValue;
        this.tag = tag;
        this.repairIngredient = repairIngredient;
    }

    public int m_6609_() {
        return this.uses;
    }

    public float m_6624_() {
        return this.speed;
    }

    public float m_6631_() {
        return this.attackDamageBonus;
    }

    public int m_6604_() {
        return this.level;
    }

    public int m_6601_() {
        return this.enchantmentValue;
    }

    @NotNull
    public TagKey<Block> getTag() {
        return this.tag;
    }

    @NotNull
    public Ingredient m_6282_() {
        return this.repairIngredient.get();
    }

    public String toString() {
        return "ForgeTier[level=" + this.level + ", uses=" + this.uses + ", speed=" + this.speed + ", attackDamageBonus=" + this.attackDamageBonus + ", enchantmentValue=" + this.enchantmentValue + ", tag=" + String.valueOf(this.tag) + ", repairIngredient=" + String.valueOf(this.repairIngredient) + "]";
    }
}

