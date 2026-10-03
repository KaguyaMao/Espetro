/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.animation.AnimationPlayType
 *  com.atsuishio.superbwarfare.client.animation.entity.VehicleAnimationContext
 *  com.atsuishio.superbwarfare.client.animation.entity.VehicleAnimationInstance
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.client.animation.AnimationPlayType;
import com.atsuishio.superbwarfare.client.animation.entity.VehicleAnimationContext;
import com.atsuishio.superbwarfare.client.animation.entity.VehicleAnimationInstance;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class Mv3ArmedEntity
extends VehicleEntity {
    private static final int CANGGAI_SEAT_INDEX = 4;
    private boolean wasCanggaiOpen;

    public Mv3ArmedEntity(EntityType<Mv3ArmedEntity> type, Level world) {
        super(type, world);
    }

    public void m_6075_() {
        super.m_6075_();
        this.tickCanggaiAnimation();
    }

    private void tickCanggaiAnimation() {
        if (!this.m_9236_().m_5776_()) {
            return;
        }
        VehicleAnimationInstance animationInstance = this.getAnim();
        if (animationInstance == null) {
            return;
        }
        VehicleAnimationContext ctx = animationInstance.getContext();
        boolean occupied = false;
        for (Entity passenger : this.m_20197_()) {
            if (this.getSeatIndex(passenger) != 4) continue;
            occupied = true;
            break;
        }
        String openAnim = "animation.mv3_armed.canggai_open";
        String closeAnim = "animation.mv3_armed.canggai_close";
        if (occupied && !this.wasCanggaiOpen) {
            ctx.stopAnimation(closeAnim, 0);
            ctx.playAnimation(openAnim, AnimationPlayType.PLAY_ONCE_HOLD, 0);
        } else if (!occupied && this.wasCanggaiOpen) {
            ctx.stopAnimation(openAnim, 0);
            ctx.playAnimation(closeAnim, AnimationPlayType.PLAY_ONCE_HOLD, 0);
        }
        this.wasCanggaiOpen = occupied;
    }
}

