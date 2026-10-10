/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.espetro.mixin.sbw;

import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.vehicle.DismountServer;
import org.espetro.vehicle.VehicleInteractionConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets={"com.atsuishio.superbwarfare.network.message.send.PlayerStopRidingMessage"}, remap=false)
public abstract class PlayerStopRidingMixin {
    @Inject(method={"handler"}, at={@At(value="HEAD")}, cancellable=true, require=0, remap=false)
    private void espetro$requireDismountChannel(Supplier<NetworkEvent.Context> contextSupplier, CallbackInfo ci) {
        ServerPlayer player;
        if (VehicleInteractionConfig.dismountDelayTicks() <= 0) {
            return;
        }
        NetworkEvent.Context context = contextSupplier == null ? null : contextSupplier.get();
        ServerPlayer serverPlayer = player = context == null ? null : context.getSender();
        if (player == null) {
            return;
        }
        if (!DismountServer.consumeReady(player)) {
            ci.cancel();
        }
    }
}

