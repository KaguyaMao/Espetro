/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier
 *  com.mojang.math.Axis
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4d
 *  org.joml.Quaternionfc
 *  org.joml.Vector4d
 */
package com.redabysslucia.dragonrise_reforge.entities.special;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Quaternionfc;
import org.joml.Vector4d;

public class CyborgTankEntity
extends VehicleEntity {
    public CyborgTankEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> this.getSourceAngle(source, 0.3f) * damage);
    }

    public float getTurretMaxHealth() {
        return 420.0f;
    }

    public float getWheelMaxHealth() {
        return 150.0f;
    }

    public float getEngineMaxHealth() {
        return 150.0f;
    }

    public Matrix4d getGunTransform(float partialTicks) {
        Matrix4d transformT = this.getVehicleTransform(partialTicks);
        Matrix4d transform = new Matrix4d();
        Vec3 pos = this.getPassengerWeaponStationPosition();
        Vector4d worldPosition = this.transformPosition(transform, pos.f_82479_, pos.f_82480_, pos.f_82481_);
        transformT.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformT.rotate((Quaternionfc)Axis.f_252436_.m_252977_(Mth.m_14179_((float)partialTicks, (float)this.getGunYRotO(), (float)this.getGunYRot())));
        return transformT;
    }

    public Matrix4d getPassengerWeaponStationBarrelTransform(float partialTicks) {
        Matrix4d transformG = this.getGunTransform(partialTicks);
        Matrix4d transform = new Matrix4d();
        Vec3 pos = this.getPassengerWeaponStationBarrelPosition();
        Vector4d worldPosition = this.transformPosition(transform, pos.f_82479_, pos.f_82480_, pos.f_82481_);
        transformG.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        float a = this.getGunYRot(partialTicks);
        float r = (Mth.m_14154_((float)a) - 90.0f) / 90.0f;
        float r2 = Mth.m_14154_((float)a) <= 90.0f ? a / 90.0f : (a < 0.0f ? -(180.0f + a) / 90.0f : (180.0f - a) / 90.0f);
        float x = Mth.m_14179_((float)partialTicks, (float)this.getGunXRotO(), (float)this.getGunXRot());
        float xV = Mth.m_14179_((float)partialTicks, (float)this.f_19860_, (float)this.m_146909_());
        float z = Mth.m_14179_((float)partialTicks, (float)this.getPrevRoll(), (float)this.getRoll());
        transformG.rotate((Quaternionfc)Axis.f_252529_.m_252977_(x + r * xV + r2 * z));
        return transformG;
    }

    public void adjustWeaponControllerAngle() {
        float ySpeed = this.getPassengerWeaponYSpeed();
        float xSpeed = this.getPassengerWeaponXSpeed();
        Entity entity = this.getNthEntity(this.getPassengerWeaponStationControllerIndex());
        float diffY = 0.0f;
        float diffX = 0.0f;
        if (entity instanceof Player) {
            float gunAngle = -Mth.m_14177_((float)(entity.m_6080_() - this.m_146908_()));
            diffY = Mth.m_14177_((float)(gunAngle - this.getGunYRot()));
            diffX = Mth.m_14177_((float)(entity.m_146909_() - this.getGunXRot()));
            this.turretTurnSound(diffX, diffY, 0.95f);
        }
        this.setGunXRot(this.getGunXRot() + Mth.m_14036_((float)(0.95f * diffX), (float)(-xSpeed), (float)xSpeed));
        this.setGunYRot(this.getGunYRot() + Mth.m_14036_((float)(0.9f * diffY), (float)(-ySpeed), (float)ySpeed));
    }
}

