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
package org.espetro.mixin.sbw;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import org.espetro.Espetro;
import org.espetro.vehicle.VehicleManager;
import org.espetro.vehicle.WreckDecayService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={VehicleEntity.class})
public abstract class VehicleEntityWreckMixin {
    @Inject(method={"destroy"}, at={@At(value="HEAD")}, require=0, remap=false)
    private void espetro$trackVehicleDestruction(CallbackInfo ci) {
        try {
            VehicleEntity vehicle = (VehicleEntity)this;
            if (!vehicle.m_9236_().f_46443_ && vehicle.m_19880_().contains("espetro_vehicle")) {
                VehicleManager.getInstance().onVehicleDeath(vehicle.m_20148_());
            }
        }
        catch (Throwable t) {
            Espetro.LOGGER.error("Espetro failed to register a destroyed vehicle", t);
        }
    }

    @Inject(method={"baseTick"}, at={@At(value="TAIL")}, require=0)
    private void espetro$decayVehicleWreck(CallbackInfo ci) {
        try {
            VehicleEntity vehicle = (VehicleEntity)this;
            if (vehicle.m_213877_()) {
                return;
            }
            if (vehicle.isWreck()) {
                WreckDecayService.tickVehicleWreck(vehicle);
            }
        }
        catch (Throwable t) {
            Espetro.LOGGER.error("Espetro \u8f7d\u5177\u6b8b\u9ab8\u52a0\u901f\u6d88\u5931\u5931\u8d25", t);
        }
    }
}

