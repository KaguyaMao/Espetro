/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package org.espetro.mixin.sbw;

import net.minecraft.world.entity.Entity;
import org.espetro.client.vehicle.SeatSwitchGate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets={"com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity"}, remap=false)
public abstract class VehicleChangeSeatDelayMixin {
    @Inject(method={"changeSeat"}, at={@At(value="HEAD")}, cancellable=true, require=0, remap=false)
    private void espetro$gateClientSeat(Entity entity, int index, CallbackInfoReturnable<Boolean> cir) {
        if (entity == null || entity.m_9236_() == null || !entity.m_9236_().f_46443_) {
            return;
        }
        if (SeatSwitchGate.isArmed()) {
            SeatSwitchGate.consumeArmed();
            return;
        }
        cir.setReturnValue((Object)false);
    }
}

