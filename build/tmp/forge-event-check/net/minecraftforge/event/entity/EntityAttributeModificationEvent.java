/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.DefaultAttributes
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.event.IModBusEvent
 */
package net.minecraftforge.event.entity;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityAttributeModificationEvent
extends Event
implements IModBusEvent {
    private final Map<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> entityAttributes;
    private final List<EntityType<? extends LivingEntity>> entityTypes;

    public EntityAttributeModificationEvent(Map<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> mapIn) {
        this.entityAttributes = mapIn;
        this.entityTypes = ImmutableList.copyOf((Collection)ForgeRegistries.ENTITY_TYPES.getValues().stream().filter(DefaultAttributes::m_22301_).map(entityType -> entityType).collect(Collectors.toList()));
    }

    public void add(EntityType<? extends LivingEntity> entityType, Attribute attribute, double value) {
        AttributeSupplier.Builder attributes = this.entityAttributes.computeIfAbsent(entityType, type -> new AttributeSupplier.Builder());
        attributes.m_22268_(attribute, value);
    }

    public void add(EntityType<? extends LivingEntity> entityType, Attribute attribute) {
        this.add(entityType, attribute, attribute.m_22082_());
    }

    public boolean has(EntityType<? extends LivingEntity> entityType, Attribute attribute) {
        AttributeSupplier globalMap = DefaultAttributes.m_22297_(entityType);
        return globalMap.m_22258_(attribute) || this.entityAttributes.get(entityType) != null && this.entityAttributes.get(entityType).hasAttribute(attribute);
    }

    public List<EntityType<? extends LivingEntity>> getTypes() {
        return this.entityTypes;
    }
}

