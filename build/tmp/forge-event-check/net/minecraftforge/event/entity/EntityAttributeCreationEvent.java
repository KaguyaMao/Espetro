/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier
 *  net.minecraft.world.entity.ai.attributes.DefaultAttributes
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.event.IModBusEvent
 */
package net.minecraftforge.event.entity;

import java.util.Map;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public class EntityAttributeCreationEvent
extends Event
implements IModBusEvent {
    private final Map<EntityType<? extends LivingEntity>, AttributeSupplier> map;

    public EntityAttributeCreationEvent(Map<EntityType<? extends LivingEntity>, AttributeSupplier> map) {
        this.map = map;
    }

    public void put(EntityType<? extends LivingEntity> entity, AttributeSupplier map) {
        if (DefaultAttributes.m_22301_(entity)) {
            throw new IllegalStateException("Duplicate DefaultAttributes entry: " + String.valueOf(entity));
        }
        this.map.put(entity, map);
    }
}

