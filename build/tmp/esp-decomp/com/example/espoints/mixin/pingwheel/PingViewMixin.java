/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  nx.pingwheel.common.core.PingView
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.example.espoints.mixin.pingwheel;

import com.example.espoints.client.PingWheelMarkerBridge;
import nx.pingwheel.common.core.PingView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={PingView.class}, remap=false)
public abstract class PingViewMixin {
    @Inject(method={"isExpired"}, at={@At(value="HEAD")}, cancellable=true)
    private void espoints$serverOwnsLifetime(CallbackInfoReturnable<Boolean> cir) {
        if (PingWheelMarkerBridge.isManaged((PingView)this)) {
            cir.setReturnValue((Object)false);
        }
    }
}

