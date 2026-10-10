/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier
 *  com.mojang.math.Axis
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4d
 *  org.joml.Matrix4dc
 *  org.joml.Quaternionfc
 *  org.joml.Vector4d
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Quaternionfc;
import org.joml.Vector4d;

public class DarkbearEntity
extends VehicleEntity {
    public DarkbearEntity(EntityType<DarkbearEntity> type, Level world) {
        super(type, world);
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> this.getSourceAngle(source, 0.3f) * damage);
    }

    public Vec3 getShootPos(String weaponName, float ticks) {
        GunData data;
        if ("subcannon".equals(weaponName) && (data = this.getGunData(weaponName)) != null) {
            return this.getSubcannonWorldPos(data.firePosition(), ticks);
        }
        return super.getShootPos(weaponName, ticks);
    }

    public Vec3 getShootPos(Entity entity, float ticks) {
        GunData data;
        if (entity != null && "subcannon".equals(this.getGunName(this.getSeatIndex(entity))) && (data = this.getGunData(this.getSeatIndex(entity))) != null) {
            return this.getSubcannonWorldPos(data.firePosition(), ticks);
        }
        return super.getShootPos(entity, ticks);
    }

    public Vec3 getShootPosForHud(Entity entity, float ticks) {
        GunData data;
        if (entity != null && "subcannon".equals(this.getGunName(this.getSeatIndex(entity))) && (data = this.getGunData(this.getSeatIndex(entity))) != null) {
            Vec3 pos = data.firePositionForHud();
            if (pos == null) {
                pos = data.firePosition();
            }
            return this.getSubcannonWorldPos(pos, ticks);
        }
        return super.getShootPosForHud(entity, ticks);
    }

    private Vec3 getSubcannonWorldPos(Vec3 localPos, float ticks) {
        Matrix4d transform = new Matrix4d((Matrix4dc)this.getTurretTransform(ticks));
        float pitch = Mth.m_14179_((float)ticks, (float)this.getTurretXRotO(), (float)this.getTurretXRot());
        transform.rotate((Quaternionfc)Axis.f_252529_.m_252977_(pitch));
        Vector4d worldPos = this.transformPosition(transform, localPos.f_82479_, localPos.f_82480_, localPos.f_82481_);
        return new Vec3(worldPos.x, worldPos.y, worldPos.z);
    }
}

