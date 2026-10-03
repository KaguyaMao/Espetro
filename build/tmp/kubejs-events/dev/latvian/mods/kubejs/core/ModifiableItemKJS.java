/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.Multimap
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 */
package dev.latvian.mods.kubejs.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.latvian.mods.kubejs.core.NoMixinException;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public interface ModifiableItemKJS {
    default public Multimap<Attribute, AttributeModifier> kjs$getAttributeMap() {
        throw new NoMixinException();
    }

    default public void kjs$setAttributeMap(Multimap<Attribute, AttributeModifier> attributes) {
        throw new NoMixinException();
    }

    default public Multimap<Attribute, AttributeModifier> kjs$getMutableAttributeMap() {
        ArrayListMultimap attributes = this.kjs$getAttributeMap();
        if (attributes instanceof ImmutableMultimap) {
            attributes = ArrayListMultimap.create(attributes);
            this.kjs$setAttributeMap((Multimap<Attribute, AttributeModifier>)attributes);
        }
        return attributes;
    }
}

