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

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.espetro.vehicle.VehicleSeatAccessPolicy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets={"com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity"}, remap=false)
public abstract class VehicleEntitySeatAccessMixin {
    @Inject(method={"changeSeat"}, at={@At(value="HEAD")}, cancellable=true, require=1, remap=false)
    private void espetro$checkSeatRole(Entity passenger, int seatIndex, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayer player;
        if (passenger instanceof ServerPlayer && !VehicleSeatAccessPolicy.checkSeatChange(player = (ServerPlayer)passenger, (Entity)((Object)this), seatIndex)) {
            cir.setReturnValue((Object)false);
        }
    }
}

