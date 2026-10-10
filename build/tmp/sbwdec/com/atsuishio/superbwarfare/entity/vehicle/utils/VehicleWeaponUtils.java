/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.ForgeMod
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4d
 *  org.joml.Vector4d
 */
package com.atsuishio.superbwarfare.entity.vehicle.utils;

import com.atsuishio.superbwarfare.entity.projectile.FlareDecoyEntity;
import com.atsuishio.superbwarfare.entity.projectile.SmokeDecoyEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import com.atsuishio.superbwarfare.tools.InventoryTool;
import com.atsuishio.superbwarfare.tools.RangeTool;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4d;
import org.joml.Vector4d;

@Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J\u0018\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\nH\u0007J\"\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0007J\u0018\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\nH\u0007J\u0010\u0010\u0012\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J\u0018\u0010\u0013\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\nH\u0007J\u0010\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J\"\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0007J\u0018\u0010\u0016\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\nH\u0007J\u0010\u0010\u0017\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007\u00a8\u0006\u0018"}, d2={"Lcom/atsuishio/superbwarfare/entity/vehicle/utils/VehicleWeaponUtils;", "", "<init>", "()V", "adjustTurretAngle", "", "vehicle", "Lcom/atsuishio/superbwarfare/entity/vehicle/base/VehicleEntity;", "turretAutoAimFromVector", "shootVec", "Lnet/minecraft/world/phys/Vec3;", "turretAutoAimFromUuid", "uuid", "", "pLiving", "Lnet/minecraft/world/entity/LivingEntity;", "releaseSmokeDecoy", "vec3", "releaseDecoy", "shootDecoy", "reloadDecoy", "passengerWeaponAutoAimFormUuid", "passengerWeaponAutoAimFormVector", "adjustWeaponControllerAngle", "superbwarfare"})
public final class VehicleWeaponUtils {
    @NotNull
    public static final VehicleWeaponUtils INSTANCE = new VehicleWeaponUtils();

    private VehicleWeaponUtils() {
    }

    @JvmStatic
    public static final void adjustTurretAngle(@NotNull VehicleEntity vehicle) {
        Intrinsics.checkNotNullParameter((Object)vehicle, (String)"vehicle");
        if (vehicle.isWreck()) {
            return;
        }
        Entity driver = vehicle.getNthEntity(vehicle.getTurretControllerIndex());
        Vec3 pos = vehicle.getBarrelPosition();
        if (driver != null && pos != null) {
            Vec3 aimPos = vehicle.m_20191_().m_82399_().m_82549_(driver.m_20252_(1.0f).m_82490_(512.0));
            Matrix4d transform = vehicle.getTurretTransform(1.0f);
            Vector4d worldPosition = VehicleVecUtils.transformPosition(transform, pos.f_82479_, pos.f_82480_, pos.f_82481_);
            Vec3 aimVec = new Vec3(worldPosition.x, worldPosition.y, worldPosition.z).m_82505_(aimPos);
            Intrinsics.checkNotNull((Object)aimVec);
            VehicleWeaponUtils.turretAutoAimFromVector(vehicle, aimVec);
        }
    }

    @JvmStatic
    public static final void turretAutoAimFromVector(@NotNull VehicleEntity vehicle, @NotNull Vec3 shootVec) {
        Intrinsics.checkNotNullParameter((Object)vehicle, (String)"vehicle");
        Intrinsics.checkNotNullParameter((Object)shootVec, (String)"shootVec");
        if (vehicle.isWreck()) {
            return;
        }
        float ySpeed = vehicle.getTurretTurnYSpeed();
        float xSpeed = vehicle.getTurretTurnXSpeed();
        Vec3 barrelVector = vehicle.getBarrelVector(1.0f);
        float diffY = (float)Mth.m_14175_((double)(-VehicleVecUtils.getYRotFromVector(shootVec) + VehicleVecUtils.getYRotFromVector(barrelVector)));
        float diffX = (float)Mth.m_14175_((double)(-VehicleVecUtils.getXRotFromVector(shootVec) + VehicleVecUtils.getXRotFromVector(barrelVector)));
        if (((Boolean)vehicle.m_20088_().m_135370_(VehicleEntity.TURRET_DAMAGED)).booleanValue()) {
            ySpeed *= 0.2f;
            xSpeed *= 0.2f;
        }
        float min = -ySpeed;
        float max = ySpeed;
        vehicle.setTurretXRot(Mth.m_14036_((float)(vehicle.getTurretXRot() + Mth.m_14036_((float)(0.75f * diffX), (float)(-xSpeed), (float)xSpeed)), (float)(-vehicle.getTurretMaxPitch() + vehicle.getCustomTurretMaxPitch()), (float)(-vehicle.getTurretMinPitch() - vehicle.getCustomTurretMinPitch())));
        vehicle.setTurretYRot(Mth.m_14036_((float)(vehicle.getTurretYRot() - Mth.m_14036_((float)(1.0f * diffY), (float)min, (float)max)), (float)(-vehicle.getTurretMaxYaw()), (float)(-vehicle.getTurretMinYaw())));
        vehicle.turretTurnSound(vehicle.getTurretXRot() - vehicle.getTurretXRotO(), vehicle.getTurretYRot() - vehicle.getTurretYRotO(), 0.95f);
        vehicle.setTurretYRotLock(Mth.m_14036_((float)(-1.0f * diffY), (float)min, (float)max));
    }

    @JvmStatic
    public static final void turretAutoAimFromUuid(@NotNull VehicleEntity vehicle, @Nullable String uuid, @NotNull LivingEntity pLiving) {
        Intrinsics.checkNotNullParameter((Object)vehicle, (String)"vehicle");
        Intrinsics.checkNotNullParameter((Object)pLiving, (String)"pLiving");
        if (vehicle.isWreck()) {
            return;
        }
        Level level = vehicle.m_9236_();
        Intrinsics.checkNotNullExpressionValue((Object)level, (String)"level(...)");
        Entity entity = EntityFindUtil.findEntity(level, uuid);
        if (entity == null) {
            return;
        }
        Entity target = entity;
        if (target.m_20202_() != null) {
            Entity entity2 = target.m_20202_();
            Intrinsics.checkNotNull((Object)entity2);
            target = entity2;
        }
        Vec3 targetPos = target.m_20191_().m_82399_();
        Vec3 targetVel = target.m_20184_();
        if (target instanceof LivingEntity) {
            double gravity = ((LivingEntity)target).m_21133_((Attribute)ForgeMod.ENTITY_GRAVITY.get());
            targetVel = targetVel.m_82520_(0.0, gravity, 0.0);
        }
        if (target instanceof Player) {
            targetVel = targetVel.m_82542_(2.0, 1.0, 2.0);
        }
        Vec3 vec3 = vehicle.getShootPos((Entity)pLiving, 1.0f).m_82546_(vehicle.getShootVec((Entity)pLiving, 1.0f).m_82490_(vehicle.getShootPos((Entity)pLiving, 1.0f).m_82554_(pLiving.m_20182_())));
        Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"subtract(...)");
        Intrinsics.checkNotNull((Object)targetPos);
        Vec3 vec32 = targetVel;
        Intrinsics.checkNotNull((Object)vec32);
        Vec3 targetVec = RangeTool.calculateFiringSolution(vec3, targetPos, vec32, vehicle.getProjectileVelocity((Entity)pLiving), vehicle.getProjectileGravity((Entity)pLiving));
        vehicle.turretAutoAimFromVector(targetVec);
    }

    @JvmStatic
    public static final void releaseSmokeDecoy(@NotNull VehicleEntity vehicle, @NotNull Vec3 vec3) {
        Intrinsics.checkNotNullParameter((Object)vehicle, (String)"vehicle");
        Intrinsics.checkNotNullParameter((Object)vec3, (String)"vec3");
        if (vehicle.decoyInputDown()) {
            if (vehicle.getDecoyCount() > 0) {
                for (int i = 0; i < 8; ++i) {
                    Level level = vehicle.m_9236_();
                    Intrinsics.checkNotNullExpressionValue((Object)level, (String)"level(...)");
                    SmokeDecoyEntity smokeDecoyEntity = new SmokeDecoyEntity(level);
                    smokeDecoyEntity.m_6034_(vehicle.m_20185_(), vehicle.m_20186_() + (double)vehicle.m_20206_(), vehicle.m_20189_());
                    Entity entity = vehicle;
                    Vec3 vec32 = vec3.m_82524_((-78.75f + 22.5f * (float)i) * ((float)Math.PI / 180));
                    Intrinsics.checkNotNullExpressionValue((Object)vec32, (String)"yRot(...)");
                    smokeDecoyEntity.decoyShoot(entity, vec32, 4.0f, 8.0f);
                    smokeDecoyEntity.m_20184_().m_82549_(vehicle.m_20184_());
                    vehicle.m_9236_().m_7967_((Entity)smokeDecoyEntity);
                }
                vehicle.m_9236_().m_6269_(null, (Entity)vehicle, (SoundEvent)ModSounds.DECOY_RELEASE.get(), vehicle.m_5720_(), 1.0f, 1.0f);
                int n = vehicle.getDecoyCount();
                vehicle.setDecoyCount(n + -1);
                if (vehicle.getDecoyCount() == 0) {
                    vehicle.setDecoyReloadCoolDown(vehicle.getDecoyReloadTime());
                }
            }
            vehicle.setDecoyInputDown(false);
        }
    }

    @JvmStatic
    public static final void releaseDecoy(@NotNull VehicleEntity vehicle) {
        Intrinsics.checkNotNullParameter((Object)vehicle, (String)"vehicle");
        if (vehicle.decoyInputDown()) {
            if (vehicle.getDecoyCount() > 0) {
                Matrix4d transform = vehicle.getVehicleTransform(1.0f);
                Vector4d worldPositionO = VehicleVecUtils.transformPosition(transform, 0.0, 0.0, 0.0);
                Vector4d worldPosition = VehicleVecUtils.transformPosition(transform, 1.0, -0.2, 0.6);
                Vector4d worldPosition2 = VehicleVecUtils.transformPosition(transform, -1.0, -0.2, 0.6);
                Vec3 shootVecO = new Vec3(worldPositionO.x, worldPositionO.y, worldPositionO.z);
                Vec3 shootVec1 = new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
                Vec3 shootVec2 = new Vec3(worldPosition2.x, worldPosition2.y, worldPosition2.z);
                Vec3 vec3 = shootVecO.m_82505_(shootVec1).m_82541_();
                Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"normalize(...)");
                VehicleWeaponUtils.shootDecoy(vehicle, vec3);
                Vec3 vec32 = shootVecO.m_82505_(shootVec2).m_82541_();
                Intrinsics.checkNotNullExpressionValue((Object)vec32, (String)"normalize(...)");
                VehicleWeaponUtils.shootDecoy(vehicle, vec32);
                int n = vehicle.getDecoyCount();
                vehicle.setDecoyCount(n + -1);
                if (vehicle.getDecoyCount() == 0) {
                    vehicle.setDecoyReloadCoolDown(vehicle.getDecoyReloadTime());
                }
            }
            vehicle.setDecoyInputDown(false);
        }
    }

    @JvmStatic
    public static final void shootDecoy(@NotNull VehicleEntity vehicle, @NotNull Vec3 shootVec) {
        Intrinsics.checkNotNullParameter((Object)vehicle, (String)"vehicle");
        Intrinsics.checkNotNullParameter((Object)shootVec, (String)"shootVec");
        Level level = vehicle.m_9236_();
        Intrinsics.checkNotNullExpressionValue((Object)level, (String)"level(...)");
        FlareDecoyEntity flareDecoyEntity = new FlareDecoyEntity(level);
        flareDecoyEntity.m_6034_(vehicle.m_20185_() + vehicle.m_20184_().f_82479_, vehicle.m_20186_() + 0.5 + vehicle.m_20184_().f_82480_, vehicle.m_20189_() + vehicle.m_20184_().f_82481_);
        flareDecoyEntity.decoyShoot(vehicle, shootVec, (float)(vehicle.m_20184_().m_82553_() * (double)0.3f + 0.7), 8.0f);
        vehicle.m_9236_().m_7967_((Entity)flareDecoyEntity);
        vehicle.m_9236_().m_6269_(null, (Entity)vehicle, (SoundEvent)ModSounds.DECOY_RELEASE.get(), vehicle.m_5720_(), 3.0f, 1.0f);
    }

    @JvmStatic
    public static final void reloadDecoy(@NotNull VehicleEntity vehicle) {
        Intrinsics.checkNotNullParameter((Object)vehicle, (String)"vehicle");
        if (vehicle.hasSmokeDecoy()) {
            vehicle.setDecoyCount(1);
            if (!vehicle.hasCreativeAmmoBoxCached()) {
                Entity entity = vehicle;
                Object object = ModItems.VEHICLE_SMOKE_AMMO.get();
                Intrinsics.checkNotNullExpressionValue((Object)object, (String)"get(...)");
                InventoryTool.consumeItem(entity, (Item)object, 1);
            }
        } else if (vehicle.hasCreativeAmmoBoxCached()) {
            vehicle.setDecoyCount(vehicle.computed().getDecoyMagazineSize());
        } else {
            int decoyMagazineCount = RangesKt.coerceAtMost((int)vehicle.getDecoyItemCount(), (int)vehicle.computed().getDecoyMagazineSize());
            vehicle.setDecoyCount(vehicle.getDecoyCount() + decoyMagazineCount);
            Entity entity = vehicle;
            Object object = ModItems.FLYING_FLARE_AMMO.get();
            Intrinsics.checkNotNullExpressionValue((Object)object, (String)"get(...)");
            InventoryTool.consumeItem(entity, (Item)object, decoyMagazineCount);
        }
        vehicle.m_9236_().m_6269_(null, (Entity)vehicle, (SoundEvent)ModSounds.DECOY_RELOAD.get(), vehicle.m_5720_(), 2.0f, 1.0f);
    }

    @JvmStatic
    public static final void passengerWeaponAutoAimFormUuid(@NotNull VehicleEntity vehicle, @Nullable String uuid, @NotNull LivingEntity pLiving) {
        Intrinsics.checkNotNullParameter((Object)vehicle, (String)"vehicle");
        Intrinsics.checkNotNullParameter((Object)pLiving, (String)"pLiving");
        Level level = vehicle.m_9236_();
        Intrinsics.checkNotNullExpressionValue((Object)level, (String)"level(...)");
        Entity target = EntityFindUtil.findEntity(level, uuid);
        if (target != null) {
            if (target.m_20202_() != null) {
                target = target.m_20202_();
            }
            Entity entity = target;
            Intrinsics.checkNotNull((Object)entity);
            Vec3 targetPos = entity.m_20191_().m_82399_();
            Vec3 targetVel = target.m_20184_();
            if (target instanceof LivingEntity) {
                double gravity = ((LivingEntity)target).m_21133_((Attribute)ForgeMod.ENTITY_GRAVITY.get());
                targetVel = targetVel.m_82520_(0.0, gravity, 0.0);
            }
            if (target instanceof Player) {
                targetVel = targetVel.m_82542_(2.0, 1.0, 2.0);
            }
            Vec3 vec3 = vehicle.getShootPos((Entity)pLiving, 1.0f).m_82546_(vehicle.getShootVec((Entity)pLiving, 1.0f).m_82490_(vehicle.getShootPos((Entity)pLiving, 1.0f).m_82554_(pLiving.m_20182_())));
            Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"subtract(...)");
            Intrinsics.checkNotNull((Object)targetPos);
            Vec3 vec32 = targetVel;
            Intrinsics.checkNotNull((Object)vec32);
            Vec3 targetVec = RangeTool.calculateFiringSolution(vec3, targetPos, vec32, vehicle.getProjectileVelocity((Entity)pLiving), vehicle.getProjectileGravity((Entity)pLiving));
            VehicleWeaponUtils.passengerWeaponAutoAimFormVector(vehicle, targetVec);
        }
    }

    @JvmStatic
    public static final void passengerWeaponAutoAimFormVector(@NotNull VehicleEntity vehicle, @NotNull Vec3 shootVec) {
        Intrinsics.checkNotNullParameter((Object)vehicle, (String)"vehicle");
        Intrinsics.checkNotNullParameter((Object)shootVec, (String)"shootVec");
        float ySpeed = vehicle.getPassengerWeaponYSpeed();
        float xSpeed = vehicle.getPassengerWeaponXSpeed();
        float diffY = (float)Mth.m_14175_((double)(-VehicleVecUtils.getYRotFromVector(shootVec) + VehicleVecUtils.getYRotFromVector(vehicle.getPassengerWeaponStationVector(1.0f))));
        float diffX = (float)Mth.m_14175_((double)(-VehicleVecUtils.getXRotFromVector(shootVec) + VehicleVecUtils.getXRotFromVector(vehicle.getPassengerWeaponStationVector(1.0f))));
        vehicle.setGunXRot(Mth.m_14036_((float)(vehicle.getGunXRot() + Mth.m_14036_((float)diffX, (float)(-xSpeed), (float)xSpeed)), (float)(-vehicle.getPassengerWeaponMaxPitch()), (float)(-vehicle.getPassengerWeaponMinPitch())));
        vehicle.setGunYRot(Mth.m_14036_((float)(vehicle.getGunYRot() - Mth.m_14036_((float)diffY, (float)(-ySpeed), (float)ySpeed)), (float)(-vehicle.getPassengerWeaponMaxYaw()), (float)(-vehicle.getPassengerWeaponMinYaw())));
        vehicle.turretTurnSound(vehicle.getGunXRot() - vehicle.getGunXRotO(), vehicle.getGunYRot() - vehicle.getGunYRotO(), 0.95f);
    }

    @JvmStatic
    public static final void adjustWeaponControllerAngle(@NotNull VehicleEntity vehicle) {
        Intrinsics.checkNotNullParameter((Object)vehicle, (String)"vehicle");
        Entity entity = vehicle.getNthEntity(vehicle.getPassengerWeaponStationControllerIndex());
        Vec3 pos = vehicle.getPassengerWeaponStationBarrelPosition();
        if (entity != null && pos != null) {
            Vec3 aimPos = vehicle.m_20191_().m_82399_().m_82549_(entity.m_20252_(1.0f).m_82490_(512.0));
            Matrix4d transform = vehicle.getGunTransform(1.0f);
            Vector4d worldPosition = VehicleVecUtils.transformPosition(transform, pos.f_82479_, pos.f_82480_, pos.f_82481_);
            Vec3 aimVec = new Vec3(worldPosition.x, worldPosition.y, worldPosition.z).m_82505_(aimPos);
            Intrinsics.checkNotNull((Object)aimVec);
            VehicleWeaponUtils.passengerWeaponAutoAimFormVector(vehicle, aimVec);
        }
        if (entity == null) {
            vehicle.setGunYRot(vehicle.getGunYRot() + vehicle.getTurretYRotLock());
        }
    }
}
