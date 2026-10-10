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
package frontline.combat.fcp.mixins;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import frontline.combat.fcp.entity.vehicle.IndirectFireVehicleBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={VehicleEntity.class}, remap=false)
public abstract class FireControlTurretAimMixin {
    @Inject(method={"adjustTurretAngle"}, at={@At(value="HEAD")}, cancellable=true)
    private void fcp$skipLookAimWhenFireControlActive(CallbackInfo ci) {
        IndirectFireVehicleBase vehicle;
        VehicleEntity self = (VehicleEntity)this;
        if (self instanceof IndirectFireVehicleBase && (vehicle = (IndirectFireVehicleBase)self).isFireControlActive()) {
            ci.cancel();
        }
    }
}

