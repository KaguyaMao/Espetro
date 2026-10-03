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
package dev.latvian.mods.kubejs.core.mixin.common.mod;

import dev.latvian.mods.kubejs.script.PlatformWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets={"dev/emi/emi/EmiUtil"})
public abstract class EMITooltipMixin {
    @Inject(method={"getModName(Ljava/lang/String;)Ljava/lang/String;"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void kjs$modId(String modId, CallbackInfoReturnable<String> cir) {
        PlatformWrapper.ModInfo r = PlatformWrapper.getMods().get(modId);
        if (r != null && !r.getCustomName().isEmpty()) {
            cir.setReturnValue((Object)r.getCustomName());
        }
    }
}

