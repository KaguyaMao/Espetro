/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package org.espetro.mixin.sbw;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.espetro.protection.MainBaseProtection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={VehicleEntity.class})
public abstract class VehicleEntityMainBaseProtectionMixin {
    @Inject(method={"hurt"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void espetro$protectVehicleInMainBase(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        VehicleEntity vehicle = (VehicleEntity)this;
        if (MainBaseProtection.isProtected((Entity)vehicle)) {
            cir.setReturnValue((Object)false);
        }
    }
}

