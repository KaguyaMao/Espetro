package com.atsuishio.superbwarfare.entity.projectile;

import com.atsuishio.superbwarfare.client.animation.entity.BasicProjectileAnimationInstance;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModMobEffects;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.DamageHandlerKt;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.atsuishio.superbwarfare.tools.SeekTool;
import com.atsuishio.superbwarfare.tools.TraceTool;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BakedModelInstance;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000|\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0016\u0018\u00002\u00020\u00012\u00020\u0002:\u00015B\u001f\u0012\u000e\u0010\u0003\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00000\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\b\u0010\u0010\u001a\u00020\u0011H\u0014J\u000e\u0010\u0012\u001a\u00020\u00002\u0006\u0010\u0012\u001a\u00020\fJ\u0018\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0016J\b\u0010\u0019\u001a\u00020\u0014H\u0016J\u0010\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dH\u0016J\u0010\u0010\u001e\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dH\u0016J\u0010\u0010\u001f\u001a\u00020\u001b2\u0006\u0010 \u001a\u00020!H\u0016J\u0010\u0010\"\u001a\u00020\u001b2\u0006\u0010 \u001a\u00020#H\u0016J\u0018\u0010$\u001a\u00020\u001b2\u0006\u0010%\u001a\u00020&2\u0006\u0010'\u001a\u00020(H\u0016J\b\u0010)\u001a\u00020\u001bH\u0016J\u0012\u0010*\u001a\u00020\u001b2\b\u0010'\u001a\u0004\u0018\u00010(H\u0016J\u0012\u0010+\u001a\u00020\u001b2\b\u0010'\u001a\u0004\u0018\u00010(H\u0016J\b\u0010,\u001a\u00020\u0014H\u0016J\b\u0010-\u001a\u00020\u0014H\u0016J\b\u0010.\u001a\u00020/H\u0016J\b\u00100\u001a\u00020\nH\u0016J\u0010\u00101\u001a\u00020\u001b2\b\u0010\u0003\u001a\u0004\u0018\u00010\rJ\u000e\u00102\u001a\u00020\u001b2\u0006\u0010\u000e\u001a\u00020\fJ\u000e\u00103\u001a\u00020\u001b2\u0006\u0010\u000f\u001a\u00020\fJ\b\u00104\u001a\u00020\fH\u0016R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u00066"},
   d2 = {"Lcom/atsuishio/superbwarfare/entity/projectile/CannonShellEntity;", "Lcom/atsuishio/superbwarfare/entity/projectile/FastThrowableProjectile;", "Lcom/atsuishio/superbwarfare/entity/projectile/BasicGeoProjectileEntity;", "type", "Lnet/minecraft/world/entity/EntityType;", "level", "Lnet/minecraft/world/level/Level;", "<init>", "(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", "fireProbability", "", "fireTime", "", "Lcom/atsuishio/superbwarfare/entity/projectile/CannonShellEntity$Type;", "spreadAmount", "spreadAngle", "getDefaultItem", "Lnet/minecraft/world/item/Item;", "durability", "isColliding", "", "pPos", "Lnet/minecraft/core/BlockPos;", "pState", "Lnet/minecraft/world/level/block/state/BlockState;", "canPassThroughFluid", "addAdditionalSaveData", "", "compound", "Lnet/minecraft/nbt/CompoundTag;", "readAdditionalSaveData", "afterHitBlock", "result", "Lnet/minecraft/world/phys/BlockHitResult;", "afterHitEntity", "Lnet/minecraft/world/phys/EntityHitResult;", "causeWPEffect", "pos", "Lnet/minecraft/world/phys/Vec3;", "shooter", "Lnet/minecraft/world/entity/Entity;", "tick", "releaseClusterMunitions", "releaseWp", "discardAfterExplode", "forceLoadChunk", "getSound", "Lnet/minecraft/sounds/SoundEvent;", "getVolume", "setType", "setSpreadAmount", "setSpreadAngle", "getHiddenTicks", "Type", "superbwarfare"}
)
@SourceDebugExtension({"SMAP\nCannonShellEntity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CannonShellEntity.kt\ncom/atsuishio/superbwarfare/entity/projectile/CannonShellEntity\n+ 2 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,384:1\n1317#2,2:385\n*S KotlinDebug\n*F\n+ 1 CannonShellEntity.kt\ncom/atsuishio/superbwarfare/entity/projectile/CannonShellEntity\n*L\n256#1:385,2\n*E\n"})
public class CannonShellEntity extends FastThrowableProjectile implements BasicGeoProjectileEntity {
   private float fireProbability;
   private int fireTime;
   @Nullable
   private CannonShellEntity.Type type;
   private int spreadAmount;
   private int spreadAngle;

   public CannonShellEntity(@NotNull EntityType<? extends CannonShellEntity> type, @NotNull Level level) {
      Intrinsics.checkNotNullParameter(type, "type");
      Intrinsics.checkNotNullParameter(level, "level");
      super(type, level);
      this.type = CannonShellEntity.Type.AP;
      this.spreadAmount = 50;
      this.spreadAngle = 15;
   }

   @NotNull
   protected Item m_7881_() {
      Object var10000 = ModItems.LARGE_SHELL_HE.get();
      Intrinsics.checkNotNullExpressionValue(var10000, "get(...)");
      return (Item)var10000;
   }

   @NotNull
   public final CannonShellEntity durability(int durability) {
      this.setDurability(durability);
      return this;
   }

   public boolean m_20039_(@NotNull BlockPos pPos, @NotNull BlockState pState) {
      Intrinsics.checkNotNullParameter(pPos, "pPos");
      Intrinsics.checkNotNullParameter(pState, "pState");
      return true;
   }

   public boolean canPassThroughFluid() {
      return this.type == CannonShellEntity.Type.AP;
   }

   public void m_7380_(@NotNull CompoundTag compound) {
      Intrinsics.checkNotNullParameter(compound, "compound");
      super.m_7380_(compound);
      compound.m_128350_("FireProbability", this.fireProbability);
      compound.m_128405_("FireTime", this.fireTime);
      compound.m_128405_("SpreadAmount", this.spreadAmount);
      compound.m_128405_("SpreadAngle", this.spreadAngle);
      if (this.type != null) {
         CannonShellEntity.Type var10002 = this.type;
         Intrinsics.checkNotNull(this.type);
         compound.m_128359_("Type", var10002.getTagName());
      }

   }

   public void m_7378_(@NotNull CompoundTag compound) {
      Intrinsics.checkNotNullParameter(compound, "compound");
      super.m_7378_(compound);
      if (compound.m_128441_("FireProbability")) {
         this.fireProbability = compound.m_128457_("FireProbability");
      }

      if (compound.m_128441_("FireTime")) {
         this.fireTime = compound.m_128451_("FireTime");
      }

      if (compound.m_128441_("SpreadAmount")) {
         this.spreadAmount = compound.m_128451_("SpreadAmount");
      }

      if (compound.m_128441_("SpreadAngle")) {
         this.spreadAngle = compound.m_128451_("SpreadAngle");
      }

      if (compound.m_128441_("Type")) {
         String type = compound.m_128461_("Type");
         Intrinsics.checkNotNull(type);
         this.type = CannonShellEntity.Type.valueOf(type);
      }

   }

   public void afterHitBlock(@NotNull BlockHitResult result) {
      Intrinsics.checkNotNullParameter(result, "result");
      Level level = this.m_9236_();
      if (level instanceof ServerLevel) {
         if (this.type == CannonShellEntity.Type.WP && this.m_19749_() != null) {
            Vec3 var10001 = result.m_82450_();
            Intrinsics.checkNotNullExpressionValue(var10001, "getLocation(...)");
            Entity var10002 = this.m_19749_();
            Intrinsics.checkNotNull(var10002);
            this.causeWPEffect(var10001, var10002);
            var10001 = result.m_82450_();
            Intrinsics.checkNotNullExpressionValue(var10001, "getLocation(...)");
            this.causeExplode(var10001);
            this.m_146870_();
         }

         if (this.type != CannonShellEntity.Type.AP) {
            Vec3 var14 = result.m_82450_();
            Intrinsics.checkNotNullExpressionValue(var14, "getLocation(...)");
            this.causeExplode(var14);
            this.m_146870_();
         } else if (ExplosionConfig.EXPLOSION_DESTROY.get() && ExplosionConfig.EXTRA_EXPLOSION_EFFECT.get() && this.getExplosionDestroyValue()) {
            Vec3 direction = this.m_20184_().m_82541_();
            Vec3 rayPos = result.m_82450_().m_82549_(direction.m_82490_(0.01D));
            Set processedBlocks = (Set)(new LinkedHashSet());

            for(int step = 0; step < 30; ++step) {
               if (this.getDurability() <= 0) {
                  Intrinsics.checkNotNull(rayPos);
                  this.causeExplode(rayPos);
                  this.m_146870_();
                  return;
               }

               BlockPos blockPos = BlockPos.m_274446_((Position)rayPos);
               Intrinsics.checkNotNull(blockPos);
               if (!processedBlocks.add(blockPos)) {
                  rayPos = rayPos.m_82549_(direction.m_82490_(0.5D));
               } else {
                  BlockState blockState = ((ServerLevel)level).m_8055_(blockPos);
                  float hardness = blockState.m_60734_().m_155943_();
                  if (hardness == -1.0F) {
                     Intrinsics.checkNotNull(rayPos);
                     this.causeExplode(rayPos);
                     this.m_146870_();
                     return;
                  }

                  if (blockState.m_60795_()) {
                     rayPos = rayPos.m_82549_(direction.m_82490_(0.5D));
                  } else {
                     int cost = 0;
                     if (blockState.m_60815_() || Intrinsics.areEqual(blockState.m_60827_(), SoundType.f_56744_)) {
                        cost += 5 + (int)hardness;
                     }

                     if (Intrinsics.areEqual(blockState.m_60827_(), SoundType.f_56742_)) {
                        cost += 5;
                     }

                     if (Intrinsics.areEqual(blockState.m_60827_(), SoundType.f_56743_) || Intrinsics.areEqual(blockState.m_60827_(), SoundType.f_154663_) || Intrinsics.areEqual(blockState.m_60827_(), SoundType.f_56725_)) {
                        cost += 25;
                     }

                     if (this.getDurability() < cost) {
                        Intrinsics.checkNotNull(rayPos);
                        this.causeExplode(rayPos);
                        this.m_146870_();
                        return;
                     }

                     ((ServerLevel)level).m_46961_(blockPos, true);
                     this.setDurability(this.getDurability() - cost);
                     double resistance = 0.95D - (double)RangesKt.coerceIn(hardness / (float)100, 0.0F, 1.0F);
                     this.m_20256_(this.m_20184_().m_82490_(resistance));
                     this.setDamage((float)((double)this.getDamageValue() * resistance));
                     this.setExplosionDamage((float)((double)this.getExplosionDamageValue() * resistance));
                     this.setExplosionRadius((float)((double)this.getExplosionRadiusValue() * resistance));
                     ServerLevel var10000 = (ServerLevel)level;
                     Vec3 var15 = Vec3.m_82512_((Vec3i)blockPos);
                     Intrinsics.checkNotNullExpressionValue(var15, "atCenterOf(...)");
                     ParticleTool.cannonHitParticles(var10000, var15);
                     rayPos = rayPos.m_82549_(direction.m_82490_(0.5D));
                  }
               }
            }

            this.m_6034_(rayPos.f_82479_, rayPos.f_82480_, rayPos.f_82481_);
         } else {
            this.destroyBlock(result);
         }

      }
   }

   public void afterHitEntity(@NotNull EntityHitResult result) {
      Intrinsics.checkNotNullParameter(result, "result");
      Level level = this.m_9236_();
      if (level instanceof ServerLevel) {
         Entity entity = result.m_82443_();
         if (this.m_19749_() != null) {
            Entity var10001 = this.m_19749_();
            Intrinsics.checkNotNull(var10001);
            if (Intrinsics.areEqual(entity, var10001.m_20202_())) {
               return;
            }
         }

         if (this.type == CannonShellEntity.Type.WP) {
            Vec3 var11 = result.m_82450_();
            Intrinsics.checkNotNullExpressionValue(var11, "getLocation(...)");
            Entity var10002 = this.m_19749_();
            Intrinsics.checkNotNull(var10002);
            this.causeWPEffect(var11, var10002);
         }

         if (entity instanceof VehicleEntity) {
            Vec3 var15 = result.m_82450_();
            Intrinsics.checkNotNullExpressionValue(var15, "getLocation(...)");
            this.causeExplode(var15);
            this.m_146870_();
         } else {
            if (this.type == CannonShellEntity.Type.AP) {
               Vec3 pos = entity.m_20191_().m_82399_();
               Intrinsics.checkNotNull(pos);
               Vec3 var16 = this.m_20184_();
               Intrinsics.checkNotNullExpressionValue(var16, "getDeltaMovement(...)");
               List resultEntities = TraceTool.getEntitiesAlongVector(level, pos, var16, CannonShellEntity::afterHitEntity$lambda$0);
               double resistance = 1.0D;

               for(TraceTool.RayTraceResultEntity rayTraceResultEntity : resultEntities) {
                  if (rayTraceResultEntity.getEntity() != null) {
                     resistance *= 0.95D;
                     Entity target = rayTraceResultEntity.getEntity();
                     if (rayTraceResultEntity.getEntity() != entity) {
                        RegistryAccess var12 = this.m_9236_().m_9598_();
                        Intrinsics.checkNotNullExpressionValue(var12, "registryAccess(...)");
                        DamageHandlerKt.forceHurt(target, ModDamageTypes.causeProjectileHitDamage(var12, (Entity)this, this.m_19749_()), (float)((double)this.getDamageValue() * resistance));
                        if (target instanceof LivingEntity) {
                           ((LivingEntity)target).f_19802_ = 0;
                        }

                        if (target instanceof VehicleEntity) {
                           Vec3 var13 = ((VehicleEntity)target).m_20191_().m_82399_();
                           Intrinsics.checkNotNullExpressionValue(var13, "getCenter(...)");
                           this.causeExplode(var13);
                           this.m_146870_();
                           return;
                        }
                     }
                  }
               }

               this.m_20256_(this.m_20184_().m_82490_(resistance));
               this.setDamage((float)((double)this.getDamageValue() * resistance));
            } else {
               Vec3 var14 = result.m_82450_();
               Intrinsics.checkNotNullExpressionValue(var14, "getLocation(...)");
               this.causeExplode(var14);
               this.m_146870_();
            }

         }
      }
   }

   public void causeWPEffect(@NotNull Vec3 pos, @NotNull Entity shooter) {
      Intrinsics.checkNotNullParameter(pos, "pos");
      Intrinsics.checkNotNullParameter(shooter, "shooter");
      if (this.m_9236_() instanceof ServerLevel) {
         List entities = (new SeekTool.Builder(shooter, false, 2, (DefaultConstructorMarker)null)).withinRange(pos, (double)this.getExplosionRadiusValue()).notItsVehicle().baseFilter().noVehicle().build();
         Sequence $this$forEach$iv = SequencesKt.filter(CollectionsKt.asSequence((Iterable)entities), CannonShellEntity::causeWPEffect$lambda$1);
         int $i$f$forEach = 0;

         for(Object element$iv : $this$forEach$iv) {
            Entity it = (Entity)element$iv;
            int $i$a$-forEach-CannonShellEntity$causeWPEffect$2 = 0;
            double dis = pos.m_82554_(it.m_20182_());
            if (this.checkNoClip(it, pos)) {
               Intrinsics.checkNotNull(it, "null cannot be cast to non-null type net.minecraft.world.entity.LivingEntity");
               ((LivingEntity)it).m_147207_(new MobEffectInstance((MobEffect)ModMobEffects.PHOSPHORUS_FIRE.get(), (int)((double)300 - (double)30 * dis), (int)Math.max((double)this.getExplosionRadiusValue() - dis, 0.0D)), this.m_19749_());
            }
         }
      }

   }

   public void m_8119_() {
      super.m_8119_();
      this.shellTrail();
      if ((this.type == CannonShellEntity.Type.CM || this.type == CannonShellEntity.Type.WP) && super.f_19797_ > 3) {
         int spreadTime = 8;
         BlockHitResult hitResult = this.m_9236_().m_45547_(new ClipContext(this.m_20182_(), this.m_20182_().m_82549_(this.m_20184_().m_82490_((double)spreadTime)), Block.OUTLINE, Fluid.ANY, (Entity)this));
         if (hitResult.m_6662_() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            if (this.type == CannonShellEntity.Type.CM) {
               this.releaseClusterMunitions(this.m_19749_());
            } else {
               this.releaseWp(this.m_19749_());
            }
         }

         Entity target = TraceTool.findLookingEntity((Entity)this, this.m_20184_().m_82490_((double)spreadTime).m_82553_());
         if (target != null && !Intrinsics.areEqual(target, this)) {
            if (this.type == CannonShellEntity.Type.CM) {
               this.releaseClusterMunitions(this.m_19749_());
            } else {
               this.releaseWp(this.m_19749_());
            }
         }
      }

   }

   public void releaseClusterMunitions(@Nullable Entity shooter) {
      Level level = this.m_9236_();
      if (level instanceof ServerLevel) {
         Vec3 var10001 = this.m_20182_();
         Intrinsics.checkNotNullExpressionValue(var10001, "position(...)");
         ParticleTool.spawnMediumExplosionParticles(level, var10001);
         int var3 = this.spreadAmount;

         for(int var4 = 0; var4 < var3; ++var4) {
            int $i$a$-repeat-CannonShellEntity$releaseClusterMunitions$1 = 0;
            GunGrenadeEntity gunGrenadeEntity = new GunGrenadeEntity(shooter, level, (float)6 * this.getDamageValue() / (float)this.spreadAmount, (float)5 * this.getExplosionDamageValue() / (float)this.spreadAmount, this.getExplosionRadiusValue() / (float)2);
            gunGrenadeEntity.m_6034_(this.m_20182_().f_82479_, this.m_20182_().f_82480_, this.m_20182_().f_82481_);
            gunGrenadeEntity.m_6686_(this.m_20184_().f_82479_, this.m_20184_().f_82480_, this.m_20184_().f_82481_, (float)((double)(super.f_19796_.m_188501_() * 0.2F) + (double)0.4F * this.m_20184_().m_82553_()), (float)this.spreadAngle);
            ((ServerLevel)level).m_7967_((Entity)gunGrenadeEntity);
         }

         this.m_146870_();
      }

   }

   public void releaseWp(@Nullable Entity shooter) {
      Level level = this.m_9236_();
      if (level instanceof ServerLevel) {
         Vec3 var10001 = this.m_20182_();
         Intrinsics.checkNotNullExpressionValue(var10001, "position(...)");
         ParticleTool.spawnMediumExplosionParticles(level, var10001);
         int var3 = this.spreadAmount;

         for(int var4 = 0; var4 < var3; ++var4) {
            int $i$a$-repeat-CannonShellEntity$releaseWp$1 = 0;
            WhitePhosphorusProjectileEntity whitePhosphorusProjectileEntity = new WhitePhosphorusProjectileEntity(shooter, level);
            whitePhosphorusProjectileEntity.m_6034_(this.m_20182_().f_82479_, this.m_20182_().f_82480_, this.m_20182_().f_82481_);
            whitePhosphorusProjectileEntity.m_6686_(this.m_20184_().f_82479_, this.m_20184_().f_82480_, this.m_20184_().f_82481_, (float)((double)(super.f_19796_.m_188501_() * 0.02F) + (double)0.3F * this.m_20184_().m_82553_()), (float)this.spreadAngle);
            ((ServerLevel)level).m_7967_((Entity)whitePhosphorusProjectileEntity);
         }

         this.m_146870_();
      }

   }

   public boolean discardAfterExplode() {
      return true;
   }

   public boolean forceLoadChunk() {
      return true;
   }

   @NotNull
   public SoundEvent getSound() {
      Object var10000 = ModSounds.SHELL_FLY.get();
      Intrinsics.checkNotNullExpressionValue(var10000, "get(...)");
      return (SoundEvent)var10000;
   }

   public float getVolume() {
      return 0.07F;
   }

   public final void setType(@Nullable CannonShellEntity.Type type) {
      this.type = type;
   }

   public final void setSpreadAmount(int spreadAmount) {
      this.spreadAmount = spreadAmount;
   }

   public final void setSpreadAngle(int spreadAngle) {
      this.spreadAngle = spreadAngle;
   }

   public int getHiddenTicks() {
      return 1;
   }

   /** @deprecated */
   @Deprecated(
      message = "Model location is auto loaded now"
   )
   @NotNull
   public ResourceLocation getModel() {
      return BasicGeoProjectileEntity.DefaultImpls.getModel(this);
   }

   /** @deprecated */
   @Deprecated(
      message = "Animation location is auto loaded now"
   )
   @Nullable
   public ResourceLocation getAnimation() {
      return BasicGeoProjectileEntity.DefaultImpls.getAnimation(this);
   }

   @Nullable
   public BasicProjectileAnimationInstance<?> getAnimationInstance() {
      return BasicGeoProjectileEntity.DefaultImpls.getAnimationInstance(this);
   }

   @Nullable
   public ResourceLocation getEmissiveTexture() {
      return BasicGeoProjectileEntity.DefaultImpls.getEmissiveTexture(this);
   }

   public int getFlareHiddenTicks() {
      return BasicGeoProjectileEntity.DefaultImpls.getFlareHiddenTicks(this);
   }

   @Nullable
   public BakedModelInstance getModelInstance() {
      return BasicGeoProjectileEntity.DefaultImpls.getModelInstance(this);
   }

   private static final boolean afterHitEntity$lambda$0(Entity it) {
      Intrinsics.checkNotNullParameter(it, "it");
      return true;
   }

   private static final boolean causeWPEffect$lambda$1(Entity it) {
      Intrinsics.checkNotNullParameter(it, "it");
      return it instanceof LivingEntity && (!(it instanceof Player) || !((Player)it).m_7500_());
   }

   @Metadata(
      mv = {2, 0, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0011\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000b¨\u0006\f"},
      d2 = {"Lcom/atsuishio/superbwarfare/entity/projectile/CannonShellEntity$Type;", "", "tagName", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;)V", "getTagName", "()Ljava/lang/String;", "AP", "HE", "CM", "WP", "superbwarfare"}
   )
   public static enum Type {
      @NotNull
      private final String tagName;
      AP("AP"),
      HE("HE"),
      CM("CM"),
      WP("WP");

      // $FF: synthetic field
      private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);

      private Type(String tagName) {
         this.tagName = tagName;
      }

      @NotNull
      public final String getTagName() {
         return this.tagName;
      }

      @NotNull
      public static EnumEntries<CannonShellEntity.Type> getEntries() {
         return $ENTRIES;
      }

      // $FF: synthetic method
      private static final CannonShellEntity.Type[] $values() {
         return new CannonShellEntity.Type[]{AP, HE, CM, WP};
      }
   }
}
