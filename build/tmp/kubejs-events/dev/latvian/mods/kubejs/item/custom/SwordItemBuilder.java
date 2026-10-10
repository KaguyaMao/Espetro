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
 *  net.minecraft.world.item.SwordItem
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
import net.minecraft.world.item.SwordItem;

public class SwordItemBuilder
extends HandheldItemBuilder {
    public SwordItemBuilder(ResourceLocation i) {
        super(i, 3.0f, -2.4f);
    }

    @Override
    public Item createObject() {
        return new SwordItem(this.toolTier, (int)this.attackDamageBaseline, this.speedBaseline, this.createItemProperties()){
            private boolean modified;
            {
                this.modified = false;
                this.f_43267_ = ArrayListMultimap.create((Multimap)this.f_43267_);
            }

            public Multimap<Attribute, AttributeModifier> m_7167_(EquipmentSlot equipmentSlot) {
                if (!this.modified) {
                    this.modified = true;
                    SwordItemBuilder.this.attributes.forEach((r, m) -> this.f_43267_.put((Object)RegistryInfo.ATTRIBUTE.getValue((ResourceLocation)r), m));
                }
                return super.m_7167_(equipmentSlot);
            }
        };
    }
}

