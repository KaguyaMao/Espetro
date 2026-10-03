/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.projectile.ProjectileEntity
 *  com.atsuishio.superbwarfare.init.ModEntities
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityDimensions
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.MobType
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.SpawnGroupData
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.FloatGoal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.MeleeAttackGoal
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.RandomStrollGoal
 *  net.minecraft.world.entity.ai.goal.RangedBowAttackGoal
 *  net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraft.world.entity.monster.RangedAttackMob
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.ServerLevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.network.NetworkHooks
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  software.bernie.geckolib.animatable.GeoEntity
 *  software.bernie.geckolib.core.animatable.GeoAnimatable
 *  software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
 *  software.bernie.geckolib.core.animation.AnimatableManager$ControllerRegistrar
 *  software.bernie.geckolib.util.GeckoLibUtil
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.entity.projectile.ProjectileEntity;
import com.atsuishio.superbwarfare.init.ModEntities;
import com.redabysslucia.dragonrise_reforge.init.ModSounds;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TerroristEntity
extends Monster
implements RangedAttackMob,
GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache((GeoAnimatable)this);
    public static final EntityDataAccessor<Boolean> RUNNER = SynchedEntityData.m_135353_(TerroristEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
    private int burstShotsRemaining = 0;
    private int burstTickCounter = 0;
    private int nextBurstTick = 0;
    private int shootAnimationTick = 0;

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    public TerroristEntity(EntityType<TerroristEntity> type, Level world) {
        super(type, world);
        this.f_21364_ = 40;
        this.m_21557_(false);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(RUNNER, (Object)false);
    }

    @Nullable
    public SpawnGroupData m_6518_(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.f_19804_.m_135381_(RUNNER, (Object)(Math.random() < 0.3 ? 1 : 0));
        if (((Boolean)this.f_19804_.m_135370_(RUNNER)).booleanValue()) {
            AttributeInstance attribute = this.m_21051_(Attributes.f_22279_);
            if (attribute != null) {
                attribute.m_22125_(new AttributeModifier("superbwarfare_attribute_modifier", 0.4, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        } else {
            AttributeInstance attribute = this.m_21051_(Attributes.f_22281_);
            if (attribute != null) {
                attribute.m_22125_(new AttributeModifier("superbwarfare_attribute_modifier", 3.0, AttributeModifier.Operation.ADDITION));
            }
        }
        this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)Items.f_42411_));
        return super.m_6518_(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public void m_7380_(@NotNull CompoundTag compound) {
        super.m_7380_(compound);
        compound.m_128379_("Runner", ((Boolean)this.f_19804_.m_135370_(RUNNER)).booleanValue());
    }

    public void m_7378_(@NotNull CompoundTag compound) {
        super.m_7378_(compound);
        this.f_19804_.m_135381_(RUNNER, (Object)compound.m_128471_("Runner"));
    }

    @ParametersAreNonnullByDefault
    protected float m_6431_(Pose poseIn, EntityDimensions sizeIn) {
        return 1.75f;
    }

    @NotNull
    public Packet<ClientGamePacketListener> m_5654_() {
        return NetworkHooks.getEntitySpawningPacket((Entity)this);
    }

    protected void m_8099_() {
        super.m_8099_();
        this.f_21345_.m_25352_(1, (Goal)new RangedBowAttackGoal<TerroristEntity>(this, 1.0, 30, 35.0f){

            public boolean m_8036_() {
                return super.m_8036_() && (Boolean)TerroristEntity.this.f_19804_.m_135370_(RUNNER) == false && TerroristEntity.this.nextBurstTick == 0;
            }
        });
        this.f_21345_.m_25352_(1, (Goal)new MeleeAttackGoal((PathfinderMob)this, 1.4, false){

            public boolean m_8036_() {
                return super.m_8036_() && (Boolean)TerroristEntity.this.f_19804_.m_135370_(RUNNER) != false;
            }

            protected double m_6639_(@NotNull LivingEntity entity) {
                return this.f_25540_.m_20205_() * this.f_25540_.m_20205_() + entity.m_20205_();
            }
        });
        this.f_21346_.m_25352_(2, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]).m_26044_(new Class[0]));
        this.f_21345_.m_25352_(3, (Goal)new RandomLookAroundGoal((Mob)this));
        this.f_21345_.m_25352_(4, (Goal)new FloatGoal((Mob)this));
        this.f_21345_.m_25352_(5, (Goal)new RandomStrollGoal((PathfinderMob)this, 0.8));
        this.f_21346_.m_25352_(6, (Goal)new NearestAttackableTargetGoal((Mob)this, Player.class, false, false));
    }

    @NotNull
    public MobType m_6336_() {
        return MobType.f_21643_;
    }

    protected void m_7472_(@NotNull DamageSource source, int looting, boolean recentlyHitIn) {
        super.m_7472_(source, looting, recentlyHitIn);
        double random = Math.random();
        if (random < 0.01) {
            this.m_19983_(new ItemStack((ItemLike)Items.f_42437_));
        } else if (random < 0.2) {
            this.m_19983_(new ItemStack((ItemLike)Items.f_42436_));
        } else {
            this.m_19983_(new ItemStack((ItemLike)Items.f_42410_));
        }
    }

    public SoundEvent m_7515_() {
        return (SoundEvent)ModSounds.TERRORIST_IDLE.get();
    }

    @ParametersAreNonnullByDefault
    public void m_7355_(BlockPos pos, BlockState blockIn) {
        this.m_5496_((SoundEvent)ModSounds.TERRORIST_STEP.get(), 0.25f, 1.0f);
    }

    @NotNull
    public SoundEvent m_7975_(@NotNull DamageSource ds) {
        return (SoundEvent)ModSounds.TERRORIST_HURT.get();
    }

    @NotNull
    public SoundEvent m_5592_() {
        return (SoundEvent)ModSounds.TERRORIST_DEATH.get();
    }

    public void m_6075_() {
        super.m_6075_();
        this.m_6210_();
    }

    public void m_8107_() {
        super.m_8107_();
        this.m_21203_();
        if (this.shootAnimationTick > 0) {
            --this.shootAnimationTick;
        }
        if (this.burstShotsRemaining > 0) {
            --this.burstTickCounter;
            if (this.burstTickCounter <= 0) {
                LivingEntity target = this.m_5448_();
                if (target != null) {
                    this.fireProjectile(target);
                }
                --this.burstShotsRemaining;
                if (this.burstShotsRemaining > 0) {
                    this.burstTickCounter = 2;
                }
            }
        }
        if (this.nextBurstTick > 0) {
            --this.nextBurstTick;
        }
    }

    public void m_6504_(LivingEntity target, float distanceFactor) {
        this.fireProjectile(target);
        this.burstShotsRemaining = 3;
        this.burstTickCounter = 2;
        this.shootAnimationTick = 30;
        this.nextBurstTick = 20 + this.f_19796_.m_188503_(20);
    }

    private void fireProjectile(LivingEntity target) {
        ProjectileEntity projectile = new ProjectileEntity((EntityType)ModEntities.PROJECTILE.get(), this.m_9236_());
        projectile.m_5602_((Entity)this);
        projectile.setRGB(new float[]{1.0f, 0.87058824f, 0.15294118f});
        projectile.setDamage(4.0f);
        projectile.setCustomGravity(0.0f);
        projectile.velocity(15.0f);
        projectile.m_146884_(this.m_146892_());
        double dx = target.m_20185_() - this.m_20185_();
        double dy = target.m_20188_() - this.m_20188_();
        double dz = target.m_20189_() - this.m_20189_();
        projectile.m_6686_(dx, dy, dz, 15.0f, 2.0f);
        this.m_5496_((SoundEvent)ModSounds.TERRORIST_SHOOT.get(), 5.0f, 1.0f / (this.m_217043_().m_188501_() * 0.4f + 0.8f));
        this.m_9236_().m_7967_((Entity)projectile);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.m_21552_().m_22268_(Attributes.f_22279_, 0.28).m_22268_(Attributes.f_22276_, 20.0).m_22268_(Attributes.f_22284_, 12.0).m_22268_(Attributes.f_22281_, 8.0).m_22268_(Attributes.f_22277_, 64.0).m_22268_(Attributes.f_22278_, 0.1);
    }

    public void m_6667_(@NotNull DamageSource source) {
        super.m_6667_(source);
    }

    protected void m_6153_() {
        ++this.f_20919_;
        if (this.f_20919_ == 140) {
            this.m_142687_(Entity.RemovalReason.KILLED);
            this.m_21226_();
        }
    }
}

