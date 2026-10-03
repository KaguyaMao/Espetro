/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile
 *  com.atsuishio.superbwarfare.entity.projectile.MediumRocketEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package tech.vvp.vvp.mixin;

import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import com.atsuishio.superbwarfare.entity.projectile.MediumRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.vvp.vvp.client.effect.GmlrsRocketTrailClient;

@Mixin(value={FastThrowableProjectile.class})
public class GmlrsRocketTrailMixin {
    @Inject(method={"largeTrail"}, at={@At(value="HEAD")}, remap=false)
    private void vvp$himarsExtraTrail(CallbackInfo ci) {
        FastThrowableProjectile projectile = (FastThrowableProjectile)this;
        if (!projectile.m_9236_().m_5776_() || !(projectile instanceof MediumRocketEntity)) {
            return;
        }
        MediumRocketEntity rocket = (MediumRocketEntity)projectile;
        if (!GmlrsRocketTrailClient.isHimarsGmlrsRocket(rocket)) {
            return;
        }
        GmlrsRocketTrailClient.spawnLargeTrail(rocket);
    }
}

