package org.espetro.mixin.sbw;

import net.minecraft.world.entity.player.Player;
import org.espetro.vehicle.VehicleInteractionConfig;
import org.espetro.vehicle.VehicleNativeWhitelist;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Block SBW native DISMOUNT double-tap. Dismount is driven by INTERACT hold.
 */
@Pseudo
@Mixin(targets = "com.atsuishio.superbwarfare.event.ClickEventHandler", remap = false)
public abstract class ClickDismountMixin {

    @Inject(method = "handleDismountPress", at = @At("HEAD"), cancellable = true,
        require = 0, remap = false)
    private void espetro$blockNativeDismount(Player player, CallbackInfo ci) {
        if (VehicleInteractionConfig.dismountDelayTicks() <= 0) {
            return;
        }
        // 白名单载具：不拦截，恢复 SBW 原生双击 DISMOUNT 下车。
        if (player != null && VehicleNativeWhitelist.isNative(player.getVehicle())) {
            return;
        }
        ci.cancel();
    }
}
