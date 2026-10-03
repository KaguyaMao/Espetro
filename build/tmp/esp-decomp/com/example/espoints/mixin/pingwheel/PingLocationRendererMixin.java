/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  nx.pingwheel.common.core.PingView
 *  nx.pingwheel.common.render.DrawContext
 *  nx.pingwheel.common.render.PingLocationRenderer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.espoints.mixin.pingwheel;

import com.example.espoints.client.PingWheelMarkerBridge;
import nx.pingwheel.common.core.PingView;
import nx.pingwheel.common.render.DrawContext;
import nx.pingwheel.common.render.PingLocationRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PingLocationRenderer.class}, remap=false)
public abstract class PingLocationRendererMixin {
    @Inject(method={"draw"}, at={@At(value="HEAD")})
    private static void espoints$begin(DrawContext context, PingView ping, CallbackInfo ci) {
        PingWheelMarkerBridge.beginRender(ping);
    }

    @Inject(method={"draw"}, at={@At(value="RETURN")})
    private static void espoints$end(DrawContext context, PingView ping, CallbackInfo ci) {
        PingWheelMarkerBridge.endRender();
    }
}

