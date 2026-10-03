/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraftforge.eventbus.api.Event$HasResult
 */
package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event;

@Event.HasResult
public class LivingPackSizeEvent
extends LivingEvent {
    private int maxPackSize;

    public LivingPackSizeEvent(Mob entity) {
        super((LivingEntity)entity);
    }

    public int getMaxPackSize() {
        return this.maxPackSize;
    }

    public void setMaxPackSize(int maxPackSize) {
        this.maxPackSize = maxPackSize;
    }
}

