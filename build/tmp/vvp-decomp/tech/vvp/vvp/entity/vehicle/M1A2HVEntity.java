/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import tech.vvp.vvp.entity.vehicle.VvpVehicleBase;

public class M1A2HVEntity
extends VvpVehicleBase {
    public M1A2HVEntity(EntityType<M1A2HVEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((source, damage) -> Float.valueOf(this.getSourceAngle((DamageSource)source, 0.4f) * damage.floatValue()));
    }

    public int getTrackAnimationLength() {
        return 92;
    }

    public void m_6075_() {
        super.m_6075_();
    }
}

