/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.Multimap
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.PickaxeItem
 */
package dev.latvian.mods.kubejs.item.custom;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.latvian.mods.kubejs.item.custom.HandheldItemBuilder;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;

public class PickaxeItemBuilder
extends HandheldItemBuilder {
    public PickaxeItemBuilder(ResourceLocation i) {
        super(i, 1.0f, -2.8f);
    }

    @Override
    public Item createObject() {
        return new PickaxeItem(this.toolTier, (int)this.attackDamageBaseline, this.speedBaseline, this.createItemProperties()){
            private boolean modified;
            {
                this.modified = false;
                this.f_40982_ = ArrayListMultimap.create((Multimap)this.f_40982_);
            }

            public Multimap<Attribute, AttributeModifier> m_7167_(EquipmentSlot equipmentSlot) {
                if (!this.modified) {
                    this.modified = true;
                    PickaxeItemBuilder.this.attributes.forEach((r, m) -> this.f_40982_.put((Object)RegistryInfo.ATTRIBUTE.getValue((ResourceLocation)r), m));
                }
                return super.m_7167_(equipmentSlot);
            }
        };
    }
}

