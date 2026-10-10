/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.item.ArmorItem$Type
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 */
package tech.vvp.vvp.item.armor;

import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public enum ModArmorMaterials implements ArmorMaterial
{
    USA_HELMET("usahelmet", 25, new int[]{3, 6, 8, 3}, 19, SoundEvents.f_11679_, 2.0f, 0.1f, () -> Ingredient.m_43929_((ItemLike[])new ItemLike[]{Items.f_42416_}));

    private final String name;
    private final int durabilityMultiplier;
    private final int[] protectionAmounts;
    private final int enchantmentValue;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;
    private static final int[] BASE_DURABILITY;

    private ModArmorMaterials(String name, int durabilityMultiplier, int[] protectionAmounts, int enchantmentValue, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts;
        this.enchantmentValue = enchantmentValue;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }

    public int m_266425_(ArmorItem.Type type) {
        return switch (type) {
            case ArmorItem.Type.HELMET -> BASE_DURABILITY[0] * this.durabilityMultiplier;
            case ArmorItem.Type.CHESTPLATE -> BASE_DURABILITY[1] * this.durabilityMultiplier;
            case ArmorItem.Type.LEGGINGS -> BASE_DURABILITY[2] * this.durabilityMultiplier;
            case ArmorItem.Type.BOOTS -> BASE_DURABILITY[3] * this.durabilityMultiplier;
            default -> 0;
        };
    }

    public int m_7366_(ArmorItem.Type type) {
        return switch (type) {
            case ArmorItem.Type.BOOTS -> this.protectionAmounts[0];
            case ArmorItem.Type.LEGGINGS -> this.protectionAmounts[1];
            case ArmorItem.Type.CHESTPLATE -> this.protectionAmounts[2];
            case ArmorItem.Type.HELMET -> this.protectionAmounts[3];
            default -> 0;
        };
    }

    public int m_6646_() {
        return this.enchantmentValue;
    }

    public SoundEvent m_7344_() {
        return this.equipSound;
    }

    public Ingredient m_6230_() {
        return this.repairIngredient.get();
    }

    public String m_6082_() {
        return "vvp:" + this.name;
    }

    public float m_6651_() {
        return this.toughness;
    }

    public float m_6649_() {
        return this.knockbackResistance;
    }

    static {
        BASE_DURABILITY = new int[]{13, 15, 16, 11};
    }
}

