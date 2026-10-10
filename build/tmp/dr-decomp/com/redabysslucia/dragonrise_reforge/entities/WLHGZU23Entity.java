/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.redabysslucia.dragonrise_reforge.entities.utils.SyncCameraVehicle;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class WLHGZU23Entity
extends SyncCameraVehicle {
    private final Float[][] PitchAdjustments = new Float[][]{{Float.valueOf(0.0f), Float.valueOf(36.0f), Float.valueOf(23.0f), Float.valueOf(0.0f), Float.valueOf(-10.0f)}, {Float.valueOf(0.0f), Float.valueOf(-36.0f), Float.valueOf(23.0f), Float.valueOf(0.0f), Float.valueOf(-10.0f)}};

    public WLHGZU23Entity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> this.getSourceAngle(source, 0.3f) * damage);
    }

    public int getTrackAnimationLength() {
        return 80;
    }

    public float getTurretMaxHealth() {
        return 80.0f;
    }

    public float getWheelMaxHealth() {
        return 60.0f;
    }

    public float getEngineMaxHealth() {
        return 100.0f;
    }
}

