/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile
 *  com.atsuishio.superbwarfare.entity.projectile.SmallRocketEntity
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package frontline.combat.fcp.mixins;

import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import com.atsuishio.superbwarfare.entity.projectile.SmallRocketEntity;
import frontline.combat.fcp.entity.vehicle.IndirectFireVehicleBase;
import java.util.Set;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={FastThrowableProjectile.class}, remap=false)
public abstract class SmallRocketChunkLoadMixin {
    @Unique
    private static final Set<String> FCP_CHUNK_LOADING_LAUNCHERS = Set.of();
    @Unique
    private boolean fcp$forceChunk = false;
    @Unique
    private boolean fcp$resolved = false;

    @Inject(method={"forceLoadChunk"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void fcp$forceLoadChunk(CallbackInfoReturnable<Boolean> cir) {
        if (!this.fcp$resolved) {
            SmallRocketChunkLoadMixin smallRocketChunkLoadMixin = this;
            if (!(smallRocketChunkLoadMixin instanceof SmallRocketEntity)) {
                this.fcp$resolved = true;
                return;
            }
            SmallRocketEntity rocket = (SmallRocketEntity)smallRocketChunkLoadMixin;
            Entity owner = rocket.m_19749_();
            if (owner == null) {
                return;
            }
            this.fcp$resolved = true;
            this.fcp$forceChunk = SmallRocketChunkLoadMixin.fcp$shouldForceLoadFromOwner(owner);
        }
        if (this.fcp$forceChunk) {
            cir.setReturnValue((Object)true);
        }
    }

    @Unique
    private static boolean fcp$shouldForceLoadFromOwner(Entity owner) {
        if (owner instanceof IndirectFireVehicleBase) {
            return true;
        }
        Entity vehicle = owner.m_20202_();
        if (vehicle instanceof IndirectFireVehicleBase) {
            return true;
        }
        Entity launcher = vehicle != null ? vehicle : owner;
        String id = EntityType.m_20613_((EntityType)launcher.m_6095_()).toString();
        return FCP_CHUNK_LOADING_LAUNCHERS.contains(id);
    }
}

