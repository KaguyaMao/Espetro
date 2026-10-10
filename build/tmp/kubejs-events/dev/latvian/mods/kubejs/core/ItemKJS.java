/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Multimap
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.Util
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.food.FoodProperties
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.DiggerItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.item.Tier
 *  net.minecraft.world.item.TieredItem
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.core.IngredientSupplierKJS;
import dev.latvian.mods.kubejs.core.ModifiableItemKJS;
import dev.latvian.mods.kubejs.core.NoMixinException;
import dev.latvian.mods.kubejs.item.FoodBuilder;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.ItemStackKey;
import dev.latvian.mods.kubejs.item.MutableToolTier;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS(value="kjs$")
public interface ItemKJS
extends IngredientSupplierKJS {
    @Nullable
    default public ItemBuilder kjs$getItemBuilder() {
        throw new NoMixinException();
    }

    default public Item kjs$self() {
        throw new NoMixinException();
    }

    default public ResourceLocation kjs$getIdLocation() {
        return UtilsJS.UNKNOWN_ID;
    }

    default public String kjs$getId() {
        return this.kjs$getIdLocation().toString();
    }

    default public String kjs$getMod() {
        return this.kjs$getIdLocation().m_135827_();
    }

    default public String kjs$getCreativeTab() {
        ResourceLocation id = RegistryInfo.ITEM.getId(this.kjs$self());
        return id == null ? "unknown" : id.m_135827_();
    }

    default public void kjs$setItemBuilder(ItemBuilder b) {
        throw new NoMixinException();
    }

    default public CompoundTag kjs$getTypeData() {
        throw new NoMixinException();
    }

    default public void kjs$setMaxStackSize(int i) {
        throw new NoMixinException();
    }

    default public void kjs$setMaxDamage(int i) {
        throw new NoMixinException();
    }

    default public void kjs$setCraftingRemainder(Item i) {
        throw new NoMixinException();
    }

    default public void kjs$setFireResistant(boolean b) {
        throw new NoMixinException();
    }

    default public void kjs$setRarity(Rarity r) {
        throw new NoMixinException();
    }

    default public void kjs$setBurnTime(int i) {
        throw new NoMixinException();
    }

    default public void kjs$overrideFood(@Nullable FoodProperties properties) {
        throw new NoMixinException();
    }

    default public void kjs$setDigSpeed(float speed) {
        ItemKJS itemKJS = this;
        if (!(itemKJS instanceof DiggerItem)) {
            throw new IllegalArgumentException("Item is not a digger item (axe, shovel, etc.)!");
        }
        DiggerItem diggerItem = (DiggerItem)itemKJS;
        diggerItem.f_40980_ = speed;
    }

    default public float kjs$getDigSpeed() {
        ItemKJS itemKJS = this;
        if (itemKJS instanceof DiggerItem) {
            DiggerItem diggerItem = (DiggerItem)itemKJS;
            return diggerItem.f_40980_;
        }
        throw new IllegalArgumentException("Item is not a digger item (axe, shovel, etc.)!");
    }

    default public void kjs$setTier(Consumer<MutableToolTier> c) {
        ItemKJS itemKJS = this;
        if (!(itemKJS instanceof TieredItem)) {
            throw new IllegalArgumentException("Item is not a tool/tiered item!");
        }
        TieredItem tiered = (TieredItem)itemKJS;
        tiered.f_43306_ = (Tier)Util.m_137469_((Object)new MutableToolTier(tiered.f_43306_), c);
    }

    default public void kjs$setFoodProperties(@Nullable Consumer<FoodBuilder> consumer) {
        if (consumer != null) {
            FoodProperties fp = this.kjs$self().m_41473_();
            FoodBuilder builder = fp == null ? new FoodBuilder() : new FoodBuilder(fp);
            consumer.accept(builder);
            this.kjs$overrideFood(builder.build());
        } else {
            this.kjs$overrideFood(null);
        }
    }

    default public void kjs$setAttackDamage(double attackDamage) {
        if (this instanceof ArmorItem) {
            throw new UnsupportedOperationException("Modifying attack damage of unsupported item: " + String.valueOf(this));
        }
        this.kjs$removeAttribute(Attributes.f_22281_, ItemWrapper.KJS_BASE_ATTACK_DAMAGE_UUID);
        this.kjs$addAttribute(Attributes.f_22281_, ItemWrapper.KJS_BASE_ATTACK_DAMAGE_UUID, "Tool modifier", attackDamage, AttributeModifier.Operation.ADDITION);
    }

    default public void kjs$setAttackSpeed(double attackSpeed) {
        if (this instanceof ArmorItem) {
            throw new UnsupportedOperationException("Modifying attack speed of unsupported item: " + String.valueOf(this));
        }
        this.kjs$removeAttribute(Attributes.f_22283_, ItemWrapper.KJS_BASE_ATTACK_SPEED_UUID);
        this.kjs$addAttribute(Attributes.f_22283_, ItemWrapper.KJS_BASE_ATTACK_SPEED_UUID, "Tool modifier", attackSpeed, AttributeModifier.Operation.ADDITION);
    }

    default public void kjs$setArmorProtection(double armorProtection) {
        ItemKJS itemKJS = this;
        if (!(itemKJS instanceof ArmorItem)) {
            throw new UnsupportedOperationException("Modifying armor value of unsupported item: " + String.valueOf(this));
        }
        ArmorItem armor = (ArmorItem)itemKJS;
        UUID uuid = ItemWrapper.KJS_ARMOR_MODIFIER_UUID_PER_SLOT[armor.m_266204_().m_266308_().m_20749_()];
        this.kjs$removeAttribute(Attributes.f_22284_, uuid);
        this.kjs$addAttribute(Attributes.f_22284_, uuid, "Armor modifier", armorProtection, AttributeModifier.Operation.ADDITION);
    }

    default public void kjs$setArmorToughness(double armorToughness) {
        ItemKJS itemKJS = this;
        if (!(itemKJS instanceof ArmorItem)) {
            throw new UnsupportedOperationException("Modifying protection of unsupported item: " + String.valueOf(this));
        }
        ArmorItem armor = (ArmorItem)itemKJS;
        UUID uuid = ItemWrapper.KJS_ARMOR_MODIFIER_UUID_PER_SLOT[armor.m_266204_().m_266308_().m_20749_()];
        this.kjs$removeAttribute(Attributes.f_22285_, uuid);
        this.kjs$addAttribute(Attributes.f_22285_, uuid, "Armor modifier", armorToughness, AttributeModifier.Operation.ADDITION);
    }

    default public void kjs$setArmorKnockbackResistance(double knockbackResistance) {
        ItemKJS itemKJS = this;
        if (!(itemKJS instanceof ArmorItem)) {
            throw new UnsupportedOperationException("Modifying protection of unsupported item: " + String.valueOf(this));
        }
        ArmorItem armor = (ArmorItem)itemKJS;
        UUID uuid = ItemWrapper.KJS_ARMOR_MODIFIER_UUID_PER_SLOT[armor.m_266204_().m_266308_().m_20749_()];
        this.kjs$removeAttribute(Attributes.f_22278_, uuid);
        this.kjs$addAttribute(Attributes.f_22278_, uuid, "Armor modifier", knockbackResistance, AttributeModifier.Operation.ADDITION);
    }

    default public void kjs$addAttribute(Attribute attribute, UUID uuid, String name, double d, AttributeModifier.Operation operation) {
        ItemKJS itemKJS = this;
        if (!(itemKJS instanceof ModifiableItemKJS)) {
            throw new UnsupportedOperationException("Adding attribute in unsupported item: " + String.valueOf(this));
        }
        ModifiableItemKJS modifiableItemKJS = (ModifiableItemKJS)((Object)itemKJS);
        Multimap<Attribute, AttributeModifier> attributes = modifiableItemKJS.kjs$getMutableAttributeMap();
        attributes.put((Object)attribute, (Object)new AttributeModifier(uuid, name, d, operation));
    }

    default public void kjs$removeAttribute(Attribute attribute, UUID uuid) {
        ItemKJS itemKJS = this;
        if (!(itemKJS instanceof ModifiableItemKJS)) {
            throw new UnsupportedOperationException("Removing attribute in unsupported item: " + String.valueOf(this));
        }
        ModifiableItemKJS modifiableItem = (ModifiableItemKJS)((Object)itemKJS);
        Multimap<Attribute, AttributeModifier> attributes = modifiableItem.kjs$getMutableAttributeMap();
        Collection modifiers = attributes.get((Object)attribute);
        Optional<AttributeModifier> value = modifiers.stream().filter(modifier -> uuid.equals(modifier.m_22209_())).findFirst();
        value.ifPresent(modifier -> attributes.remove((Object)attribute, modifier));
    }

    default public List<AttributeModifier> kjs$getAttributes(Attribute attribute) {
        ItemKJS itemKJS = this;
        if (!(itemKJS instanceof ModifiableItemKJS)) {
            throw new UnsupportedOperationException("Getting attribute in unsupported item: " + String.valueOf(this));
        }
        ModifiableItemKJS modifiableItem = (ModifiableItemKJS)((Object)itemKJS);
        Multimap<Attribute, AttributeModifier> attributes = modifiableItem.kjs$getAttributeMap();
        return ImmutableList.copyOf((Collection)attributes.get((Object)attribute));
    }

    default public void kjs$setNameKey(String key) {
        throw new NoMixinException();
    }

    default public ItemStackKey kjs$getTypeItemStackKey() {
        throw new NoMixinException();
    }
}

