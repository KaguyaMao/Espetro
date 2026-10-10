/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.Multimap
 *  dev.architectury.registry.fuel.FuelRegistry
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package dev.latvian.mods.kubejs.item.custom;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.architectury.registry.fuel.FuelRegistry;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class BasicItemJS
extends Item {
    private final ItemBuilder itemBuilder;
    private final Multimap<Attribute, AttributeModifier> attributes;
    private boolean modified = false;

    public BasicItemJS(ItemBuilder p) {
        super(p.createItemProperties());
        this.itemBuilder = p;
        if (p.burnTime > 0) {
            FuelRegistry.register((int)p.burnTime, (ItemLike[])new ItemLike[]{this});
        }
        this.attributes = ArrayListMultimap.create();
    }

    public ItemBuilder kjs$getItemBuilder() {
        return this.itemBuilder;
    }

    public Component m_7626_(ItemStack itemStack) {
        if (this.itemBuilder.displayName != null && this.itemBuilder.formattedDisplayName) {
            return this.itemBuilder.displayName;
        }
        return super.m_7626_(itemStack);
    }

    public Multimap<Attribute, AttributeModifier> m_7167_(EquipmentSlot slot) {
        if (!this.modified) {
            this.itemBuilder.attributes.forEach((r, m) -> this.attributes.put((Object)RegistryInfo.ATTRIBUTE.getValue((ResourceLocation)r), m));
            this.modified = true;
        }
        return slot == EquipmentSlot.MAINHAND ? this.attributes : super.m_7167_(slot);
    }

    public static class Builder
    extends ItemBuilder {
        public Builder(ResourceLocation i) {
            super(i);
        }

        @Override
        public Item createObject() {
            return new BasicItemJS(this);
        }
    }
}

