/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.TurretWreckEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.espetro.mixin.sbw;

import com.atsuishio.superbwarfare.entity.vehicle.TurretWreckEntity;
import org.espetro.Espetro;
import org.espetro.vehicle.WreckDecayService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={TurretWreckEntity.class})
public abstract class TurretWreckEntityMixin {
    @Inject(method={"baseTick"}, at={@At(value="TAIL")}, require=0)
    private void espetro$decayTurretWreck(CallbackInfo ci) {
        try {
            WreckDecayService.tickTurretWreck((TurretWreckEntity)this);
        }
        catch (Throwable t) {
            Espetro.LOGGER.error("Espetro \u70ae\u5854\u6b8b\u9ab8\u52a0\u901f\u6d88\u5931\u5931\u8d25", t);
        }
    }
}

