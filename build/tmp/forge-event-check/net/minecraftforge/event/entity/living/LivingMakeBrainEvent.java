/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 */
package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.BrainBuilder;
import net.minecraftforge.event.entity.living.LivingEvent;

public class LivingMakeBrainEvent
extends LivingEvent {
    private final BrainBuilder<?> brainBuilder;

    public LivingMakeBrainEvent(LivingEntity entity, BrainBuilder<?> brainBuilder) {
        super(entity);
        this.brainBuilder = brainBuilder;
    }

    public <E extends LivingEntity> BrainBuilder<E> getTypedBrainBuilder(E ignoredEntity) {
        return this.brainBuilder;
    }
}

