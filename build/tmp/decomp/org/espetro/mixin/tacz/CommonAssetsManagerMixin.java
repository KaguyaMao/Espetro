/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.event.OnDatapackSyncEvent
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.espetro.mixin.tacz;

import net.minecraftforge.event.OnDatapackSyncEvent;
import org.espetro.compat.tacz.TaczGunPackSyncCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets={"com.tacz.guns.resource.CommonAssetsManager"}, remap=false)
public abstract class CommonAssetsManagerMixin {
    @Inject(method={"OnDatapackSync"}, at={@At(value="HEAD")}, cancellable=true, require=0, remap=false)
    private static void espetro$sendChunkedGunPackCache(OnDatapackSyncEvent event, CallbackInfo callback) {
        if (TaczGunPackSyncCompat.sendChunked(event)) {
            callback.cancel();
        }
    }
}

