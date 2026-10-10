/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.config.server.ExplosionConfig
 *  com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleType
 *  com.atsuishio.superbwarfare.entity.projectile.MissileProjectile
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.init.ModDamageTypes
 *  com.atsuishio.superbwarfare.init.ModItems
 *  com.atsuishio.superbwarfare.init.ModSounds
 *  com.atsuishio.superbwarfare.tools.DamageHandler
 *  com.atsuishio.superbwarfare.tools.ProjectileTool
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  software.bernie.geckolib.animatable.GeoEntity
 *  software.bernie.geckolib.core.animatable.GeoAnimatable
 *  software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
 *  software.bernie.geckolib.core.animation.AnimatableManager$ControllerRegistrar
 *  software.bernie.geckolib.core.animation.AnimationController
 *  software.bernie.geckolib.core.animation.AnimationState
 *  software.bernie.geckolib.core.animation.RawAnimation
 *  software.bernie.geckolib.core.object.PlayState
 *  software.bernie.geckolib.util.GeckoLibUtil
 */
package frontline.combat.fcp.entity.projectile.Malyutka;

import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleType;
import com.atsuishio.superbwarfare.entity.projectile.MissileProjectile;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.DamageHandler;
import com.atsuishio.superbwarfare.tools.ProjectileTool;
import java.lang.reflect.Field;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MalyutkaEntity
extends MissileProjectile
implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache((GeoAnimatable)this);
    private static Field damageField;
    private static Field explosionDamageField;
    private static Field explosionRadiusField;
    private static Field durabilityField;
    public UUID launcherVehicle;

    public MalyutkaEntity(EntityType<? extends MalyutkaEntity> type, Level level) {
        super(type, level);
        this.f_19811_ = true;
    }

    private void setDamage(int value) {
        try {
            if (damageField != null) {
                damageField.setInt((Object)this, value);
            }
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setExplosionDamage(int value) {
        try {
            if (explosionDamageField != null) {
                explosionDamageField.setInt((Object)this, value);
            }
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setExplosionRadius(int value) {
        try {
            if (explosionRadiusField != null) {
                explosionRadiusField.setInt((Object)this, value);
            }
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private int getDamage() {
        try {
            if (damageField != null) {
                return damageField.getInt((Object)this);
            }
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getExplosionDamage() {
        try {
            if (explosionDamageField != null) {
                return explosionDamageField.getInt((Object)this);
            }
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getExplosionRadius() {
        try {
            if (explosionRadiusField != null) {
                return explosionRadiusField.getInt((Object)this);
            }
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @NotNull
    protected Item m_7881_() {
        return (Item)ModItems.MEDIUM_ANTI_GROUND_MISSILE.get();
    }

    private void explodeAndDiscard() {
        if (this.m_9236_() instanceof ServerLevel) {
            ProjectileTool.causeCustomExplode((Projectile)this, (DamageSource)ModDamageTypes.causeProjectileExplosionDamage((RegistryAccess)this.m_9236_().m_9598_(), (Entity)this, (Entity)this.m_19749_()), (Entity)this, (float)this.getExplosionDamage(), (float)this.getExplosionRadius());
        }
        this.m_146870_();
    }

    public void m_8060_(@NotNull BlockHitResult blockHitResult) {
        super.m_8060_(blockHitResult);
        if (this.m_9236_() instanceof ServerLevel) {
            BlockPos resultPos = blockHitResult.m_82425_();
            float hardness = this.m_9236_().m_8055_(resultPos).m_60734_().m_155943_();
            if (hardness != -1.0f && ((Boolean)ExplosionConfig.EXPLOSION_DESTROY.get()).booleanValue() && ((Boolean)ExplosionConfig.EXTRA_EXPLOSION_EFFECT.get()).booleanValue()) {
                this.m_9236_().m_46961_(resultPos, true);
            }
            this.explodeAndDiscard();
        }
    }

    protected void m_5790_(@NotNull EntityHitResult result) {
        super.m_5790_(result);
        Entity entity = result.m_82443_();
        if (this.m_19749_() != null && this.m_19749_().m_20202_() != null && entity == this.m_19749_().m_20202_() || entity instanceof MalyutkaEntity) {
            return;
        }
        if (this.m_9236_() instanceof ServerLevel) {
            DamageHandler.doDamage((Entity)entity, (DamageSource)ModDamageTypes.causeProjectileHitDamage((RegistryAccess)this.m_9236_().m_9598_(), (Entity)this, (Entity)this.m_19749_()), (float)this.getDamage());
            if (entity instanceof LivingEntity) {
                entity.f_19802_ = 0;
            }
            this.causeExplode(result.m_82450_());
            this.m_146870_();
        }
    }

    public void m_8119_() {
        Entity entity;
        super.m_8119_();
        this.mediumTrail();
        if (this.f_19797_ > 0 && this.m_19749_() != null && (entity = this.m_19749_().m_20202_()) instanceof VehicleEntity) {
            VehicleEntity vehicle = (VehicleEntity)entity;
            Entity shooter = this.m_19749_();
            if (this.launcherVehicle == null && this.f_19797_ < 5) {
                this.launcherVehicle = vehicle.m_20148_();
            }
            if (this.launcherVehicle != null && this.launcherVehicle.equals(vehicle.m_20148_())) {
                Vec3 lookVec = (vehicle.getVehicleType() == VehicleType.AIRPLANE || vehicle.getVehicleType() == VehicleType.HELICOPTER) && shooter == vehicle.m_146895_() ? shooter.m_20252_(1.0f).m_82490_(1.6) : vehicle.getBarrelVector(1.0f).m_82490_(1.6);
                Vec3 missileVec = vehicle.getShootPosForHud(shooter, 1.0f).m_82505_(this.m_20182_()).m_82541_();
                Vec3 toVec = missileVec.m_82505_(lookVec);
                this.turn(toVec, Mth.m_14036_((float)((float)(this.f_19797_ - 1) * 0.4f), (float)0.0f, (float)6.0f));
                this.m_20256_(this.m_20184_().m_82490_(0.05).m_82549_(this.m_20154_().m_82490_(8.0)));
                this.m_20256_(this.m_20184_().m_82542_(0.85, 0.85, 0.85));
            }
        }
    }

    private PlayState movementPredicate(AnimationState<MalyutkaEntity> event) {
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.jvm.idle"));
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController[]{new AnimationController((GeoAnimatable)this, "movement", 0, this::movementPredicate)});
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @NotNull
    public SoundEvent getSound() {
        return (SoundEvent)ModSounds.ROCKET_FLY.get();
    }

    public float getVolume() {
        return 0.4f;
    }

    public void setLauncherVehicle(UUID uuid) {
        this.launcherVehicle = uuid;
    }

    public float getMaxHealth() {
        return 20.0f;
    }

    static {
        try {
            Class<MissileProjectile> parentClass = MissileProjectile.class;
            damageField = parentClass.getDeclaredField("damage");
            damageField.setAccessible(true);
            explosionDamageField = parentClass.getDeclaredField("explosionDamage");
            explosionDamageField.setAccessible(true);
            explosionRadiusField = parentClass.getDeclaredField("explosionRadius");
            explosionRadiusField.setAccessible(true);
            durabilityField = parentClass.getDeclaredField("durability");
            durabilityField.setAccessible(true);
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}

