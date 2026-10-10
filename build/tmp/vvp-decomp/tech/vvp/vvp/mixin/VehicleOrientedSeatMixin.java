/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.world.entity.Entity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package tech.vvp.vvp.mixin;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.vvp.vvp.entity.vehicle.util.OrientedBenchSeats;

@Mixin(value={VehicleEntity.class})
public class VehicleOrientedSeatMixin {
    @Inject(method={"copyEntityData(Lnet/minecraft/world/entity/Entity;)V"}, at={@At(value="TAIL")}, remap=false)
    private void vvp$orientedSeatBody(Entity entity, CallbackInfo ci) {
        OrientedBenchSeats.afterCopyEntityData((VehicleEntity)this, entity);
    }
}

