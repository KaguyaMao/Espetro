/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.api.event.ShootEvent$Post
 *  com.atsuishio.superbwarfare.data.gun.ShootParameters
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package frontline.combat.fcp.event;

import com.atsuishio.superbwarfare.api.event.ShootEvent;
import com.atsuishio.superbwarfare.data.gun.ShootParameters;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import frontline.combat.fcp.effects.FCPMuzzleEffects;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="fcp")
public final class MuzzleShootEventHandler {
    private MuzzleShootEventHandler() {
    }

    @SubscribeEvent
    public static void onShootPost(ShootEvent.Post event) {
        VehicleEntity vehicle;
        ShootParameters parameters = event.getParameters();
        Entity ammoSupplier = parameters.ammoSupplier;
        if (!(ammoSupplier instanceof VehicleEntity) || !FCPMuzzleEffects.isFCPVehicle(vehicle = (VehicleEntity)ammoSupplier)) {
            return;
        }
        FCPMuzzleEffects.spawnFromShoot(parameters);
    }
}

