/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.eventbus.api.Event
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.event;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.util.Collection;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

public class ItemAttributeModifierEvent
extends Event {
    private final ItemStack stack;
    private final EquipmentSlot slotType;
    private final Multimap<Attribute, AttributeModifier> originalModifiers;
    private Multimap<Attribute, AttributeModifier> unmodifiableModifiers;
    @Nullable
    private Multimap<Attribute, AttributeModifier> modifiableModifiers;

    public ItemAttributeModifierEvent(ItemStack stack, EquipmentSlot slotType, Multimap<Attribute, AttributeModifier> modifiers) {
        this.stack = stack;
        this.slotType = slotType;
        this.originalModifiers = modifiers;
        this.unmodifiableModifiers = this.originalModifiers;
    }

    public Multimap<Attribute, AttributeModifier> getModifiers() {
        return this.unmodifiableModifiers;
    }

    public Multimap<Attribute, AttributeModifier> getOriginalModifiers() {
        return this.originalModifiers;
    }

    private Multimap<Attribute, AttributeModifier> getModifiableMap() {
        if (this.modifiableModifiers == null) {
            this.modifiableModifiers = HashMultimap.create(this.originalModifiers);
            this.unmodifiableModifiers = Multimaps.unmodifiableMultimap(this.modifiableModifiers);
        }
        return this.modifiableModifiers;
    }

    public boolean addModifier(Attribute attribute, AttributeModifier modifier) {
        return this.getModifiableMap().put((Object)attribute, (Object)modifier);
    }

    public boolean removeModifier(Attribute attribute, AttributeModifier modifier) {
        return this.getModifiableMap().remove((Object)attribute, (Object)modifier);
    }

    public Collection<AttributeModifier> removeAttribute(Attribute attribute) {
        return this.getModifiableMap().removeAll((Object)attribute);
    }

    public void clearModifiers() {
        this.getModifiableMap().clear();
    }

    public EquipmentSlot getSlotType() {
        return this.slotType;
    }

    public ItemStack getItemStack() {
        return this.stack;
    }
}

