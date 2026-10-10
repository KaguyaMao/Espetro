/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.eventbus.api.Cancelable
 */
package net.minecraftforge.event.entity;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class EntityTravelToDimensionEvent
extends EntityEvent {
    private final ResourceKey<Level> dimension;

    public EntityTravelToDimensionEvent(Entity entity, ResourceKey<Level> dimension) {
        super(entity);
        this.dimension = dimension;
    }

    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }
}

