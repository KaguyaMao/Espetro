/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.animation.AnimationPlayType
 *  com.atsuishio.superbwarfare.client.animation.entity.VehicleAnimationContext
 *  com.atsuishio.superbwarfare.client.animation.entity.VehicleAnimationInstance
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.redabysslucia.dragonrise_reforge.entities.utils;

import com.atsuishio.superbwarfare.client.animation.AnimationPlayType;
import com.atsuishio.superbwarfare.client.animation.entity.VehicleAnimationContext;
import com.atsuishio.superbwarfare.client.animation.entity.VehicleAnimationInstance;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class DragonriseVehicleBase
extends VehicleEntity {
    private boolean wasSplash = this.isSplashDefaultOpen();
    private boolean wasEngineOn;
    private int splashCheckCooldown;

    public DragonriseVehicleBase(EntityType<?> type, Level level) {
        super(type, level);
    }

    protected boolean isSplashDefaultOpen() {
        return false;
    }

    public void m_6075_() {
        super.m_6075_();
        this.tickGearAnimation();
        this.tickSplashAnimation();
        this.tickEngineAnimation();
    }

    private void tickGearAnimation() {
        if (!this.m_9236_().m_5776_()) {
            return;
        }
        VehicleAnimationInstance animationInstance = this.getAnim();
        if (animationInstance == null) {
            return;
        }
        VehicleAnimationContext ctx = animationInstance.getContext();
        boolean gearUp = this.getGearUp() && this.getSynchedGearRot() > 0.0f && this.getSynchedGearRot() < 1.0f || this.getSynchedGearRot() == 1.0f;
        boolean gearDown = !this.getGearUp() && this.getSynchedGearRot() > 0.0f && this.getSynchedGearRot() < 1.0f || this.getSynchedGearRot() == 0.0f;
        String prefix = "animation." + EntityType.m_20613_((EntityType)this.m_6095_()).m_135815_();
        String gearUpAnim = prefix + ".gear_up";
        String gearDownAnim = prefix + ".gear_down";
        if (gearUp && !this.getWasGearUp()) {
            ctx.stopAnimation(gearDownAnim, 0);
            ctx.playAnimation(gearUpAnim, AnimationPlayType.PLAY_ONCE_HOLD, 0);
        } else if (gearDown && this.getWasGearUp()) {
            ctx.stopAnimation(gearUpAnim, 0);
            ctx.playAnimation(gearDownAnim, AnimationPlayType.PLAY_ONCE_HOLD, 0);
        }
        this.setWasGearUp(gearUp);
    }

    private void tickSplashAnimation() {
        if (!this.m_9236_().m_5776_()) {
            return;
        }
        if (--this.splashCheckCooldown > 0) {
            return;
        }
        this.splashCheckCooldown = 10;
        VehicleAnimationInstance animationInstance = this.getAnim();
        if (animationInstance == null) {
            return;
        }
        VehicleAnimationContext ctx = animationInstance.getContext();
        boolean inWater = this.m_20069_();
        String prefix = "animation." + EntityType.m_20613_((EntityType)this.m_6095_()).m_135815_();
        String splashOn = prefix + ".splash_on";
        String splashOff = prefix + ".splash_off";
        if (inWater && !this.wasSplash) {
            ctx.stopAnimation(splashOff, 0);
            ctx.playAnimation(splashOn, AnimationPlayType.PLAY_ONCE_HOLD, 0);
        } else if (!inWater && this.wasSplash) {
            ctx.stopAnimation(splashOn, 0);
            ctx.playAnimation(splashOff, AnimationPlayType.PLAY_ONCE_HOLD, 0);
        }
        this.wasSplash = inWater;
    }

    private void tickEngineAnimation() {
        if (!this.m_9236_().m_5776_()) {
            return;
        }
        VehicleAnimationInstance animationInstance = this.getAnim();
        if (animationInstance == null) {
            return;
        }
        VehicleAnimationContext ctx = animationInstance.getContext();
        boolean engineOn = !this.m_20197_().isEmpty() && this.sprintInputDown();
        String prefix = "animation." + EntityType.m_20613_((EntityType)this.m_6095_()).m_135815_();
        String engineOnAnim = prefix + ".engine_on";
        String engineOffAnim = prefix + ".engine_off";
        if (engineOn && !this.wasEngineOn) {
            ctx.stopAnimation(engineOffAnim, 0);
            ctx.playAnimation(engineOnAnim, AnimationPlayType.PLAY_ONCE_HOLD, 0);
        } else if (!engineOn && this.wasEngineOn) {
            ctx.stopAnimation(engineOnAnim, 0);
            ctx.playAnimation(engineOffAnim, AnimationPlayType.PLAY_ONCE_HOLD, 0);
        }
        this.wasEngineOn = engineOn;
    }
}

