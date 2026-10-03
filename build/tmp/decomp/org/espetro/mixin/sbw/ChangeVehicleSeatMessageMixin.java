/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.espetro.mixin.sbw;

import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.vehicle.SeatSwitchServer;
import org.espetro.vehicle.VehicleSeatAccessPolicy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets={"com.atsuishio.superbwarfare.network.message.send.ChangeVehicleSeatMessage"}, remap=false)
public abstract class ChangeVehicleSeatMessageMixin {
    @Shadow(remap=false)
    public abstract int getIndex();

    @Inject(method={"handler"}, at={@At(value="HEAD")}, cancellable=true, require=1, remap=false)
    private void espetro$checkRequestedSeat(Supplier<NetworkEvent.Context> contextSupplier, CallbackInfo ci) {
        ServerPlayer player;
        NetworkEvent.Context context = contextSupplier == null ? null : contextSupplier.get();
        ServerPlayer serverPlayer = player = context == null ? null : context.getSender();
        if (player == null) {
            return;
        }
        if (!SeatSwitchServer.isReady(player)) {
            ci.cancel();
            return;
        }
        Entity vehicle = player.m_20202_();
        if (vehicle != null && !VehicleSeatAccessPolicy.checkSeatChange(player, vehicle, this.getIndex())) {
            ci.cancel();
        }
    }

    @Inject(method={"handler"}, at={@At(value="RETURN")}, require=0, remap=false)
    private void espetro$consumeSeatChannel(Supplier<NetworkEvent.Context> contextSupplier, CallbackInfo ci) {
        ServerPlayer player;
        if (ci.isCancelled()) {
            return;
        }
        NetworkEvent.Context context = contextSupplier == null ? null : contextSupplier.get();
        ServerPlayer serverPlayer = player = context == null ? null : context.getSender();
        if (player != null) {
            SeatSwitchServer.consumeReady(player);
        }
    }
}

