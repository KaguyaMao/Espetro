/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.gun.SeekWeaponInfo
 *  com.atsuishio.superbwarfare.event.ClientEventHandler
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.redabysslucia.dragonrise_reforge.mixin;

import com.atsuishio.superbwarfare.data.gun.SeekWeaponInfo;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={ClientEventHandler.class}, remap=false)
public class ClientEventHandlerDualSeekMixin {
    @Redirect(method={"vehicleWeaponSeeking"}, at=@At(value="INVOKE", target="Lcom/atsuishio/superbwarfare/data/gun/SeekWeaponInfo;getOnlyLockBlock()Z"))
    private boolean dragonrise$dualModeSeek(SeekWeaponInfo info) {
        if (info.getOnlyLockEntity() && info.getOnlyLockBlock()) {
            return ClientEventHandler.nearestEntityVehicle == null;
        }
        return info.getOnlyLockBlock();
    }
}

