/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.redabysslucia.dragonrise_reforge.mixin;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.redabysslucia.dragonrise_reforge.entities.vehicle.IndirectFireVehicleBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={VehicleEntity.class}, remap=false)
public abstract class FireControlTurretAimMixin {
    @Inject(method={"adjustTurretAngle"}, at={@At(value="HEAD")}, cancellable=true)
    private void dragonrise$skipLookAimWhenFireControlActive(CallbackInfo ci) {
        IndirectFireVehicleBase vehicle;
        VehicleEntity self = (VehicleEntity)this;
        if (self instanceof IndirectFireVehicleBase && (vehicle = (IndirectFireVehicleBase)self).isFireControlActive() && vehicle.isFireControlTakeoverEnabled()) {
            ci.cancel();
        }
    }
}

