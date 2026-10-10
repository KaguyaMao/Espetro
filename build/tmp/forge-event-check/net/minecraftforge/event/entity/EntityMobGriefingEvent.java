/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.eventbus.api.Event$HasResult
 */
package net.minecraftforge.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Event;

@Event.HasResult
public class EntityMobGriefingEvent
extends EntityEvent {
    public EntityMobGriefingEvent(Entity entity) {
        super(entity);
    }
}

