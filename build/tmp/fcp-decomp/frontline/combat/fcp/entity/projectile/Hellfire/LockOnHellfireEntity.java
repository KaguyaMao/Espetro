/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.config.server.ExplosionConfig
 *  com.atsuishio.superbwarfare.entity.projectile.MissileProjectile
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.init.ModDamageTypes
 *  com.atsuishio.superbwarfare.init.ModItems
 *  com.atsuishio.superbwarfare.init.ModSounds
 *  com.atsuishio.superbwarfare.init.ModTags$EntityTypes
 *  com.atsuishio.superbwarfare.tools.DamageHandler
 *  com.atsuishio.superbwarfare.tools.EntityFindUtil
 *  com.atsuishio.superbwarfare.tools.ParticleTool
 *  com.atsuishio.superbwarfare.tools.ProjectileTool
 *  com.atsuishio.superbwarfare.tools.RangeTool
 *  com.atsuishio.superbwarfare.tools.SeekTool
 *  com.atsuishio.superbwarfare.tools.VectorTool
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.animal.Pig
 *  net.minecraft.world.entity.boss.enderdragon.EnderDragon
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Math
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
package frontline.combat.fcp.entity.projectile.Hellfire;

import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.projectile.MissileProjectile;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.init.ModTags;
import com.atsuishio.superbwarfare.tools.DamageHandler;
import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.atsuishio.superbwarfare.tools.ProjectileTool;
import com.atsuishio.superbwarfare.tools.RangeTool;
import com.atsuishio.superbwarfare.tools.SeekTool;
import com.atsuishio.superbwarfare.tools.VectorTool;
import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class LockOnHellfireEntity
extends MissileProjectile
implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache((GeoAnimatable)this);
    private static Field damageField;
    private static Field explosionDamageField;
    private static Field explosionRadiusField;
    private static Field durabilityField;

    public LockOnHellfireEntity(EntityType<? extends LockOnHellfireEntity> type, Level level) {
        super(type, level);
        this.f_19811_ = true;
        this.setDamage(1100);
        this.setExplosionDamage(180);
        this.setExplosionRadius(12);
        this.distracted = false;
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
        return (Item)ModItems.LARGE_ANTI_GROUND_MISSILE.get();
    }

    protected void m_5790_(@NotNull EntityHitResult result) {
        super.m_5790_(result);
        Entity entity = result.m_82443_();
        if (entity == this.m_19749_() || this.m_19749_() != null && entity == this.m_19749_().m_20202_() || entity instanceof LockOnHellfireEntity) {
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

    public void m_8119_() {
        super.m_8119_();
        this.largeTrail();
        Entity entity = EntityFindUtil.findEntity((Level)this.m_9236_(), (String)((String)this.f_19804_.m_135370_(TARGET_UUID)));
        List decoy = SeekTool.seekLivingEntities((Entity)this, (double)32.0, (double)90.0);
        for (Entity e : decoy) {
            if (!e.m_6095_().m_204039_(ModTags.EntityTypes.DECOY) || this.distracted) continue;
            this.f_19804_.m_135381_(TARGET_UUID, (Object)e.m_20149_());
            this.distracted = true;
            break;
        }
        Vec3 toVec = this.m_20154_();
        if (this.guideType == 0) {
            if (!((String)this.f_19804_.m_135370_(TARGET_UUID)).equals("none") && entity != null && this.m_9236_() instanceof ServerLevel) {
                double dis;
                if ((!entity.m_20197_().isEmpty() || entity instanceof VehicleEntity) && entity.f_19797_ % (int)Math.max((double)(0.04 * (double)this.m_20270_(entity)), (double)2.0) == 0) {
                    entity.m_9236_().m_5594_(null, entity.m_20097_(), entity instanceof Pig ? SoundEvents.f_12235_ : (SoundEvent)ModSounds.MISSILE_WARNING.get(), SoundSource.PLAYERS, 2.0f, 1.0f);
                }
                double height = (dis = entity.m_20182_().m_82505_(this.m_20182_()).m_165924_()) > 30.0 ? 0.4 * (dis - 30.0) : 0.0;
                Vec3 targetPos = new Vec3(entity.m_20185_(), entity.m_20186_() + (double)(entity instanceof EnderDragon ? -2 : 0) + height, entity.m_20189_());
                toVec = RangeTool.calculateFiringSolution((Vec3)this.m_20182_(), (Vec3)targetPos, (Vec3)entity.m_20184_(), (double)this.m_20184_().m_82553_(), (double)0.0);
            }
        } else if (this.m_9236_() instanceof ServerLevel) {
            double dis = this.targetPos.m_82505_(this.m_20182_()).m_165924_();
            double height = dis > 30.0 ? 0.4 * (dis - 30.0) : 0.0;
            Vec3 targetPos = this.targetPos.m_82520_(0.0, height, 0.0);
            toVec = RangeTool.calculateFiringSolution((Vec3)this.m_20182_(), (Vec3)targetPos, (Vec3)Vec3.f_82478_, (double)this.m_20184_().m_82553_(), (double)0.0);
        }
        if (this.f_19797_ > 8) {
            boolean lostTarget;
            this.m_20256_(this.m_20184_().m_82490_(0.05).m_82549_(this.m_20154_().m_82490_(8.0)));
            this.m_20256_(this.m_20184_().m_82542_(0.85, 0.85, 0.85));
            boolean bl = lostTarget = VectorTool.calculateAngle((Vec3)this.m_20154_(), (Vec3)toVec) > 170.0;
            if (!lostTarget) {
                this.turn(toVec, Mth.m_14036_((float)((float)(this.f_19797_ - 8) * 0.5f), (float)0.0f, (float)15.0f));
            }
        } else {
            this.m_20256_(this.m_20184_().m_82520_(0.0, -0.06, 0.0));
            this.m_20256_(this.m_20184_().m_82542_(0.99, 0.99, 0.99));
        }
        if (this.f_19797_ == 8) {
            Level level;
            this.m_9236_().m_5594_(null, BlockPos.m_274446_((Position)this.m_20182_()), (SoundEvent)ModSounds.MISSILE_START.get(), SoundSource.PLAYERS, 4.0f, 1.0f);
            if (!this.m_9236_().m_5776_() && (level = this.m_9236_()) instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
                ParticleTool.sendParticle((ServerLevel)serverLevel, (ParticleOptions)ParticleTypes.f_123796_, (double)this.f_19854_, (double)this.f_19855_, (double)this.f_19856_, (int)15, (double)0.8, (double)0.8, (double)0.8, (double)0.01, (boolean)true);
                ParticleTool.sendParticle((ServerLevel)serverLevel, (ParticleOptions)ParticleTypes.f_123777_, (double)this.f_19854_, (double)this.f_19855_, (double)this.f_19856_, (int)10, (double)0.8, (double)0.8, (double)0.8, (double)0.01, (boolean)true);
            }
        }
    }

    private PlayState movementPredicate(AnimationState<LockOnHellfireEntity> event) {
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.jvm.idle"));
    }

    public float m_7139_() {
        return this.f_19797_ < 8 ? 0.15f : super.m_7139_();
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
        return 0.7f;
    }

    public float getMaxHealth() {
        return 70.0f;
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

