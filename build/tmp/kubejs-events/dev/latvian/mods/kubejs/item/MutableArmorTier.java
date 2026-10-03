/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapForJS
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.item.ArmorItem$Type
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.crafting.Ingredient
 */
package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class MutableArmorTier
implements ArmorMaterial {
    private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
    public final ArmorMaterial parent;
    private int durabilityMultiplier;
    private int[] slotProtections;
    private int enchantmentValue;
    private SoundEvent sound;
    private float toughness;
    private float knockbackResistance;
    private Ingredient repairIngredient;
    private String name;

    public MutableArmorTier(String id, ArmorMaterial p) {
        this.parent = p;
        this.enchantmentValue = p.m_6646_();
        this.sound = p.m_7344_();
        this.repairIngredient = this.parent.m_6230_();
        this.toughness = p.m_6651_();
        this.knockbackResistance = p.m_6649_();
        this.name = id;
    }

    public int m_266425_(ArmorItem.Type equipmentSlot) {
        return this.durabilityMultiplier == 0 ? this.parent.m_266425_(equipmentSlot) : HEALTH_PER_SLOT[equipmentSlot.m_266308_().m_20749_()] * this.durabilityMultiplier;
    }

    public void setDurabilityMultiplier(int m) {
        this.durabilityMultiplier = m;
    }

    public int m_7366_(ArmorItem.Type equipmentSlot) {
        return this.slotProtections == null ? this.parent.m_7366_(equipmentSlot) : this.slotProtections[equipmentSlot.m_266308_().m_20749_()];
    }

    public void setSlotProtections(int[] p) {
        this.slotProtections = p;
    }

    @RemapForJS(value="getEnchantmentValue")
    public int m_6646_() {
        return this.enchantmentValue;
    }

    public void setEnchantmentValue(int i) {
        this.enchantmentValue = i;
    }

    @RemapForJS(value="getEquipSound")
    public SoundEvent m_7344_() {
        return this.sound;
    }

    public void setEquipSound(SoundEvent e) {
        this.sound = e;
    }

    @RemapForJS(value="getVanillaRepairIngredient")
    public Ingredient m_6230_() {
        return this.repairIngredient;
    }

    public void setRepairIngredient(Ingredient in) {
        this.repairIngredient = in;
    }

    public String m_6082_() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @RemapForJS(value="getToughness")
    public float m_6651_() {
        return this.toughness;
    }

    public void setToughness(float f) {
        this.toughness = f;
    }

    @RemapForJS(value="getKnockbackResistance")
    public float m_6649_() {
        return this.knockbackResistance;
    }

    public void setKnockbackResistance(float f) {
        this.knockbackResistance = f;
    }
}

