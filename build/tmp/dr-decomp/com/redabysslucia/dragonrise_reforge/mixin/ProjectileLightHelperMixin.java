/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.lighting.ProjectileLightHelper
 *  net.minecraft.world.entity.Entity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.redabysslucia.dragonrise_reforge.mixin;

import com.atsuishio.superbwarfare.client.lighting.ProjectileLightHelper;
import com.redabysslucia.dragonrise_reforge.entities.projectile.GuidedBombEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ProjectileLightHelper.class}, remap=false)
public class ProjectileLightHelperMixin {
    @Inject(method={"getTrailLight"}, at={@At(value="HEAD")}, cancellable=true)
    private static void dragonrise$noTrailLightForGuidedBombs(Entity entity, CallbackInfoReturnable<Object> cir) {
        if (entity instanceof GuidedBombEntity) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method={"getLaunchFlash"}, at={@At(value="HEAD")}, cancellable=true)
    private static void dragonrise$noLaunchFlashForGuidedBombs(Entity entity, CallbackInfoReturnable<Object> cir) {
        if (entity instanceof GuidedBombEntity) {
            cir.setReturnValue(null);
        }
    }
}

