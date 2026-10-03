/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.init.ModItems
 *  net.minecraft.Util
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.item.ArmorItem$Type
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.minecraftforge.common.util.Lazy
 */
package tech.vvp.vvp.tiers;

import com.atsuishio.superbwarfare.init.ModItems;
import java.util.EnumMap;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.util.Lazy;

public enum ModArmorMaterial implements ArmorMaterial
{
    CEMENTED_CARBIDE("cemented_carbide", 50, (EnumMap)Util.m_137469_(new EnumMap<ArmorItem.Type, V>(ArmorItem.Type.class), p -> {
        p.put(ArmorItem.Type.BOOTS, 3);
        p.put(ArmorItem.Type.LEGGINGS, 6);
        p.put(ArmorItem.Type.CHESTPLATE, 8);
        p.put(ArmorItem.Type.HELMET, 3);
    }), 10, SoundEvents.f_11677_, 4.0f, 0.05f, () -> Ingredient.m_43929_((ItemLike[])new ItemLike[]{(ItemLike)ModItems.CEMENTED_CARBIDE_INGOT.get()})),
    MULTICAM("multicam", 50, (EnumMap)Util.m_137469_(new EnumMap<ArmorItem.Type, V>(ArmorItem.Type.class), p -> {
        p.put(ArmorItem.Type.CHESTPLATE, 10);
        p.put(ArmorItem.Type.HELMET, 5);
    }), 10, SoundEvents.f_11677_, 5.0f, 0.1f, () -> Ingredient.m_43929_((ItemLike[])new ItemLike[]{(ItemLike)ModItems.CEMENTED_CARBIDE_INGOT.get()})),
    MI28("mi28", 50, (EnumMap)Util.m_137469_(new EnumMap<ArmorItem.Type, V>(ArmorItem.Type.class), p -> {
        p.put(ArmorItem.Type.CHESTPLATE, 5);
        p.put(ArmorItem.Type.LEGGINGS, 2);
        p.put(ArmorItem.Type.HELMET, 3);
    }), 10, SoundEvents.f_11677_, 1.0f, 0.0f, () -> Ingredient.m_43929_((ItemLike[])new ItemLike[]{(ItemLike)ModItems.CEMENTED_CARBIDE_INGOT.get()})),
    KEPKI("kepki", 50, (EnumMap)Util.m_137469_(new EnumMap<ArmorItem.Type, V>(ArmorItem.Type.class), p -> p.put(ArmorItem.Type.HELMET, 1)), 10, SoundEvents.f_11677_, 0.0f, 0.0f, () -> Ingredient.m_43929_((ItemLike[])new ItemLike[]{(ItemLike)ModItems.CEMENTED_CARBIDE_INGOT.get()})),
    UKR("ukr", 50, (EnumMap)Util.m_137469_(new EnumMap<ArmorItem.Type, V>(ArmorItem.Type.class), p -> {
        p.put(ArmorItem.Type.CHESTPLATE, 10);
        p.put(ArmorItem.Type.LEGGINGS, 3);
        p.put(ArmorItem.Type.HELMET, 6);
    }), 10, SoundEvents.f_11677_, 1.5f, 0.2f, () -> Ingredient.m_43929_((ItemLike[])new ItemLike[]{(ItemLike)ModItems.CEMENTED_CARBIDE_INGOT.get()})),
    RUS("rus", 50, (EnumMap)Util.m_137469_(new EnumMap<ArmorItem.Type, V>(ArmorItem.Type.class), p -> {
        p.put(ArmorItem.Type.CHESTPLATE, 10);
        p.put(ArmorItem.Type.LEGGINGS, 3);
        p.put(ArmorItem.Type.HELMET, 6);
    }), 10, SoundEvents.f_11677_, 1.5f, 0.2f, () -> Ingredient.m_43929_((ItemLike[])new ItemLike[]{(ItemLike)ModItems.CEMENTED_CARBIDE_INGOT.get()})),
    PMC("pmc", 50, (EnumMap)Util.m_137469_(new EnumMap<ArmorItem.Type, V>(ArmorItem.Type.class), p -> {
        p.put(ArmorItem.Type.CHESTPLATE, 10);
        p.put(ArmorItem.Type.LEGGINGS, 3);
        p.put(ArmorItem.Type.HELMET, 6);
    }), 10, SoundEvents.f_11677_, 1.5f, 0.2f, () -> Ingredient.m_43929_((ItemLike[])new ItemLike[]{(ItemLike)ModItems.CEMENTED_CARBIDE_INGOT.get()}));

    private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE;
    private final String name;
    private final int durabilityMultiplier;
    private final EnumMap<ArmorItem.Type, Integer> protectionFunctionForType;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final Lazy<Ingredient> repairIngredient;

    private ModArmorMaterial(String pName, int pDurabilityMultiplier, EnumMap<ArmorItem.Type, Integer> pProtectionFunctionForType, int pEnchantmentValue, SoundEvent pSound, float pToughness, float pKnockbackResistance, Supplier<Ingredient> pRepairIngredient) {
        this.name = pName;
        this.durabilityMultiplier = pDurabilityMultiplier;
        this.protectionFunctionForType = pProtectionFunctionForType;
        this.enchantmentValue = pEnchantmentValue;
        this.sound = pSound;
        this.toughness = pToughness;
        this.knockbackResistance = pKnockbackResistance;
        this.repairIngredient = Lazy.of(pRepairIngredient);
    }

    public int m_266425_(ArmorItem.Type pType) {
        return HEALTH_FUNCTION_FOR_TYPE.getOrDefault(pType, 0) * this.durabilityMultiplier;
    }

    public int m_7366_(ArmorItem.Type pType) {
        return this.protectionFunctionForType.getOrDefault(pType, 0);
    }

    public int m_6646_() {
        return this.enchantmentValue;
    }

    public SoundEvent m_7344_() {
        return this.sound;
    }

    public Ingredient m_6230_() {
        return (Ingredient)this.repairIngredient.get();
    }

    public String m_6082_() {
        return this.name;
    }

    public float m_6651_() {
        return this.toughness;
    }

    public float m_6649_() {
        return this.knockbackResistance;
    }

    static {
        HEALTH_FUNCTION_FOR_TYPE = (EnumMap)Util.m_137469_(new EnumMap(ArmorItem.Type.class), p_266653_ -> {
            p_266653_.put(ArmorItem.Type.BOOTS, 13);
            p_266653_.put(ArmorItem.Type.LEGGINGS, 15);
            p_266653_.put(ArmorItem.Type.CHESTPLATE, 16);
            p_266653_.put(ArmorItem.Type.HELMET, 11);
        });
    }
}

