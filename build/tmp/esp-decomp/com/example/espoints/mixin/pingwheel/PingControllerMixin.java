/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  nx.pingwheel.common.core.PingController
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.espoints.mixin.pingwheel;

import com.example.espoints.client.TacticalMarkRadialController;
import nx.pingwheel.common.core.PingController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PingController.class}, remap=false)
public abstract class PingControllerMixin {
    @Inject(method={"pollPingAction"}, at={@At(value="HEAD")}, cancellable=true)
    private static void espoints$suppressDefaultBattlePing(float tickDelta, CallbackInfo ci) {
        if (TacticalMarkRadialController.shouldSuppressDefaultPing()) {
            PingController.revokePingAction();
            ci.cancel();
        }
    }
}

