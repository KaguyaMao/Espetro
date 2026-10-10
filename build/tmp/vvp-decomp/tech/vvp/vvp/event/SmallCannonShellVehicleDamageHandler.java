/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.api.event.ProjectileHitEvent$HitEntity
 *  com.atsuishio.superbwarfare.entity.projectile.SmallCannonShellEntity
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package tech.vvp.vvp.event;

import com.atsuishio.superbwarfare.api.event.ProjectileHitEvent;
import com.atsuishio.superbwarfare.entity.projectile.SmallCannonShellEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="vvp")
public final class SmallCannonShellVehicleDamageHandler {
    private static final float VEHICLE_AP_DAMAGE_MULTIPLIER = 1.15f;

    private SmallCannonShellVehicleDamageHandler() {
    }

    @SubscribeEvent
    public static void onProjectileHitEntity(ProjectileHitEvent.HitEntity event) {
        Projectile projectile = event.getProjectile();
        if (!(projectile instanceof SmallCannonShellEntity)) {
            return;
        }
        SmallCannonShellEntity shell = (SmallCannonShellEntity)projectile;
        if (!(event.getTarget() instanceof VehicleEntity)) {
            return;
        }
        if (!SmallCannonShellVehicleDamageHandler.isArmorPiercingProfile(shell)) {
            return;
        }
        shell.setDamageValue(shell.getDamageValue() * 1.15f);
        shell.setExplosionDamageValue(shell.getExplosionDamageValue() * 1.15f);
    }

    private static boolean isArmorPiercingProfile(SmallCannonShellEntity shell) {
        return shell.getDamageValue() > shell.getExplosionDamageValue();
    }
}

