/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class M270Entity
extends VehicleEntity {
    public M270Entity(EntityType<M270Entity> type, Level world) {
        super(type, world);
    }

    private boolean isAngleRestricted() {
        float turretYaw = this.getTurretYRot();
        float turretPitch = this.getTurretXRot();
        return Mth.m_14154_((float)turretYaw) <= 50.0f && -turretPitch < 30.0f;
    }

    public boolean canShoot(LivingEntity living) {
        if (this.isAngleRestricted()) {
            return false;
        }
        return super.canShoot(living);
    }

    public void vehicleShoot(LivingEntity living, UUID uuid, Vec3 targetPos) {
        if (this.isAngleRestricted()) {
            if (living instanceof Player) {
                Player player = (Player)living;
                player.m_5661_((Component)Component.m_237115_((String)"message.dragonrise_reforge.m270_angle_restricted"), true);
            }
            return;
        }
        super.vehicleShoot(living, uuid, targetPos);
    }
}

