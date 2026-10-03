/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraftforge.eventbus.api.Cancelable
 */
package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class LivingChangeTargetEvent
extends LivingEvent {
    private final ILivingTargetType targetType;
    private final LivingEntity originalTarget;
    private LivingEntity newTarget;

    public LivingChangeTargetEvent(LivingEntity entity, LivingEntity originalTarget, ILivingTargetType targetType) {
        super(entity);
        this.originalTarget = originalTarget;
        this.newTarget = originalTarget;
        this.targetType = targetType;
    }

    public LivingEntity getNewTarget() {
        return this.newTarget;
    }

    public void setNewTarget(LivingEntity newTarget) {
        this.newTarget = newTarget;
    }

    public ILivingTargetType getTargetType() {
        return this.targetType;
    }

    public LivingEntity getOriginalTarget() {
        return this.originalTarget;
    }

    public static interface ILivingTargetType {
    }

    public static enum LivingTargetType implements ILivingTargetType
    {
        MOB_TARGET,
        BEHAVIOR_TARGET;

    }
}

