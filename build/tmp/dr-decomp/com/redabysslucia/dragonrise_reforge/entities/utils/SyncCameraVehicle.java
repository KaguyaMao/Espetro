/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.redabysslucia.dragonrise_reforge.entities.utils;

import com.redabysslucia.dragonrise_reforge.entities.utils.FireLightVisionVehicle;
import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class SyncCameraVehicle
extends FireLightVisionVehicle {
    private final Random random = new Random();
    private int shakeCooldown = 0;
    private int shakeDuration = 0;
    private float currentShakeIntensity = 0.0f;
    private float shakePhase = 0.0f;
    private float prevShakePitch = 0.0f;
    private float prevShakeRoll = 0.0f;
    private float shakePitch = 0.0f;
    private float shakeRoll = 0.0f;
    private float vehicleBaseXRot = 0.0f;
    private float vehicleBaseRoll = 0.0f;

    public SyncCameraVehicle(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.resetShake();
    }

    @Override
    public void m_8119_() {
        super.m_8119_();
        this.prevShakePitch = this.shakePitch;
        this.prevShakeRoll = this.shakeRoll;
        this.updateVehicleShake();
        this.applyShakeToVehicle();
    }

    private void updateVehicleShake() {
        Vec3 motion = this.m_20184_();
        double speed = motion.m_82553_();
        if (speed <= 0.1) {
            if (this.shakeDuration > 0) {
                --this.shakeDuration;
                this.calculateShake();
            } else {
                this.shakePitch *= 0.9f;
                this.shakeRoll *= 0.9f;
                if (Math.abs(this.shakePitch) < 0.01f) {
                    this.shakePitch = 0.0f;
                }
                if (Math.abs(this.shakeRoll) < 0.01f) {
                    this.shakeRoll = 0.0f;
                }
            }
            return;
        }
        if (this.shakeDuration > 0) {
            --this.shakeDuration;
            this.shakePhase += 0.3f;
            this.calculateShake();
            if (this.shakeDuration == 0) {
                this.resetShake();
            }
        } else {
            --this.shakeCooldown;
            this.shakePitch *= 0.9f;
            this.shakeRoll *= 0.9f;
            if (Math.abs(this.shakePitch) < 0.01f) {
                this.shakePitch = 0.0f;
            }
            if (Math.abs(this.shakeRoll) < 0.01f) {
                this.shakeRoll = 0.0f;
            }
            if (this.shakeCooldown <= 0) {
                this.startShake();
            }
        }
    }

    private void applyShakeToVehicle() {
        float deltaPitch = this.shakePitch - this.prevShakePitch;
        float deltaRoll = this.shakeRoll - this.prevShakeRoll;
        this.m_146926_(this.m_146909_() + deltaPitch);
        this.setRoll(this.getRoll() + deltaRoll);
    }

    private void startShake() {
        float speedFactor = (float)Math.min(this.m_20184_().m_82553_() * 1.0, 2.0);
        this.currentShakeIntensity = (this.random.nextFloat() * 0.6f + 0.6f) * speedFactor;
        int durationBase = this.random.nextInt(5) + 6;
        int durationBonus = (int)(speedFactor * 6.0f);
        this.shakeDuration = durationBase + durationBonus;
        this.shakePhase = this.random.nextFloat() * (float)Math.PI * 2.0f;
    }

    private void resetShake() {
        this.shakeCooldown = this.random.nextInt(9) + 4;
        this.shakeDuration = 0;
        this.currentShakeIntensity = 0.0f;
    }

    private void calculateShake() {
        float bumpX = (float)Math.sin(this.shakePhase) * this.currentShakeIntensity;
        float direction = this.getMovementDirection();
        float bumpY = (float)Math.cos(this.shakePhase * 1.5f) * this.currentShakeIntensity * 2.0f * direction;
        this.shakeRoll = bumpX * 0.5f;
        this.shakePitch = bumpY;
    }

    private float getMovementDirection() {
        double forwardZ;
        Vec3 motion = this.m_20184_();
        double yaw = (double)this.m_146908_() * (Math.PI / 180);
        double forwardX = -Math.sin(yaw);
        double dotProduct = motion.f_82479_ * forwardX + motion.f_82481_ * (forwardZ = Math.cos(yaw));
        return dotProduct > 0.0 ? 1.0f : -1.0f;
    }

    public float getInterpolatedShakePitch(float partialTicks) {
        return Mth.m_14179_((float)partialTicks, (float)this.prevShakePitch, (float)this.shakePitch);
    }

    public float getInterpolatedShakeRoll(float partialTicks) {
        return Mth.m_14179_((float)partialTicks, (float)this.prevShakeRoll, (float)this.shakeRoll);
    }
}

