/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.data.gun.GunProp
 *  com.atsuishio.superbwarfare.data.gun.ProjectileInfo
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.event.ClientEventHandler
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.redabysslucia.dragonrise_reforge.mixin;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.GunProp;
import com.atsuishio.superbwarfare.data.gun.ProjectileInfo;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientEventHandler.class}, remap=false)
public class ClientEventHandlerRadarLowAltLockMixin {
    private static final Set<String> RADAR_MISSILES = Set.of("dragonrise_reforge:aim120", "dragonrise_reforge:pl_12", "dragonrise_reforge:pl_15", "dragonrise_reforge:r_77");
    private static final double MIN_LOCK_HEIGHT = 25.0;

    @Inject(method={"vehicleWeaponSeeking"}, at={@At(value="RETURN")})
    private void dragonrise$filterLowAltRadarTarget(Player player, CallbackInfo ci) {
        Entity candidate = ClientEventHandler.nearestEntityVehicle;
        if (candidate == null) {
            return;
        }
        if (player == null || player.m_20202_() == null) {
            return;
        }
        Entity entity = player.m_20202_();
        if (!(entity instanceof VehicleEntity)) {
            return;
        }
        VehicleEntity vehicle = (VehicleEntity)entity;
        GunData data = vehicle.getGunData((Entity)player);
        if (data == null) {
            return;
        }
        Object projectile = data.get(GunProp.PROJECTILE);
        if (!(projectile instanceof ProjectileInfo)) {
            return;
        }
        ProjectileInfo info = (ProjectileInfo)projectile;
        if (!RADAR_MISSILES.contains(info.getItemId())) {
            return;
        }
        ClientLevel level = Minecraft.m_91087_().f_91073_;
        if (level == null) {
            return;
        }
        int surfaceY = level.m_6924_(Heightmap.Types.WORLD_SURFACE, candidate.m_20183_().m_123341_(), candidate.m_20183_().m_123343_());
        double heightAboveGround = candidate.m_20186_() - (double)surfaceY;
        if (heightAboveGround < 25.0) {
            ClientEventHandler.nearestEntityVehicle = null;
        }
    }
}

