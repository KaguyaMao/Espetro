/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyboardHandler
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.client.KubeJSClient;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={KeyboardHandler.class})
public abstract class KeyboardHandlerMixin {
    @Inject(method={"handleDebugKeys"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/Minecraft;reloadResourcePacks()Ljava/util/concurrent/CompletableFuture;", shift=At.Shift.BEFORE)})
    private void reloadResources(int i, CallbackInfoReturnable<Boolean> ci) {
        KubeJSClient.reloadClientScripts();
    }
}

