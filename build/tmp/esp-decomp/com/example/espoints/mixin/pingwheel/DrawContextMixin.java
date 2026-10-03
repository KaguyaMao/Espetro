/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  nx.pingwheel.common.render.DrawContext
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.espoints.mixin.pingwheel;

import com.example.espoints.client.PingWheelMarkerBridge;
import net.minecraft.world.item.ItemStack;
import nx.pingwheel.common.render.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={DrawContext.class}, remap=false)
public abstract class DrawContextMixin {
    @Inject(method={"renderPing"}, at={@At(value="HEAD")}, cancellable=true)
    private void espoints$renderTacticalIcon(ItemStack stack, boolean drawItem, int ignoredColor, CallbackInfo ci) {
        if (PingWheelMarkerBridge.currentType() == null) {
            return;
        }
        ((DrawContext)this).renderTexture(PingWheelMarkerBridge.currentTexture(), 12, PingWheelMarkerBridge.currentTint());
        ci.cancel();
    }
}

