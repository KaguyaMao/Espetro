package com.atsuishio.superbwarfare.entity.projectile;

import com.atsuishio.superbwarfare.tools.VectorTool;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b&\u0018\u00002\u00020\u0001B!\b\u0016\u0012\u000e\u0010\u0002\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bB9\b\u0016\u0012\u000e\u0010\u0002\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u0003\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\n\u0012\u0006\u0010\f\u001a\u00020\n\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\rB+\b\u0016\u0012\u000e\u0010\u0002\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u0003\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\u0010J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u0015H\u0016¨\u0006\u0016"},
   d2 = {"Lcom/atsuishio/superbwarfare/entity/projectile/BounceProjectile;", "Lcom/atsuishio/superbwarfare/entity/projectile/FastThrowableProjectile;", "type", "Lnet/minecraft/world/entity/EntityType;", "Lnet/minecraft/world/entity/projectile/ThrowableItemProjectile;", "level", "Lnet/minecraft/world/level/Level;", "<init>", "(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", "x", "", "y", "z", "(Lnet/minecraft/world/entity/EntityType;DDDLnet/minecraft/world/level/Level;)V", "shooter", "Lnet/minecraft/world/entity/Entity;", "(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;)V", "projectileMove", "", "bounce", "direction", "Lnet/minecraft/core/Direction;", "superbwarfare"}
)
public abstract class BounceProjectile extends FastThrowableProjectile {
   public BounceProjectile(@NotNull EntityType<? extends ThrowableItemProjectile> type, @NotNull Level level) {
      Intrinsics.checkNotNullParameter(type, "type");
      Intrinsics.checkNotNullParameter(level, "level");
      super(type, level);
   }

   public BounceProjectile(@NotNull EntityType<? extends ThrowableItemProjectile> type, double x, double y, double z, @NotNull Level level) {
      Intrinsics.checkNotNullParameter(type, "type");
      Intrinsics.checkNotNullParameter(level, "level");
      super(type, x, y, z, level);
   }

   public BounceProjectile(@NotNull EntityType<? extends ThrowableItemProjectile> type, @Nullable Entity shooter, @NotNull Level level) {
      Intrinsics.checkNotNullParameter(type, "type");
      Intrinsics.checkNotNullParameter(level, "level");
      super(type, shooter, level);
   }

   public void projectileMove(@NotNull Level level) {
      Intrinsics.checkNotNullParameter(level, "level");
      Vec3 vec = this.m_20184_();
      this.m_37283_();
      double friction = this.m_20069_() ? 0.8D : 1.0D;
      this.m_20256_(vec.m_82490_(friction));
      this.m_20256_(this.m_20184_().m_82520_(0.0D, -((double)this.getCustomGravity()), 0.0D));
      if (level instanceof ServerLevel && this.canPassThroughFluid()) {
         Vec3 var10001 = this.m_20182_();
         Intrinsics.checkNotNullExpressionValue(var10001, "position(...)");
         if (VectorTool.isInLiquid(level, var10001)) {
            this.m_20256_(this.m_20184_().m_82490_(RangesKt.coerceIn((double)this.getUnderwaterMotionScaleValue(), 0.0D, 1.0D)));
         }
      }

      float f = 0.98F;
      if (this.m_20096_()) {
         BlockPos pos = this.m_20099_();
         f = level.m_8055_(pos).getFriction((LevelReader)level, pos, (Entity)this) * 0.98F;
      }

      this.m_20256_(this.m_20184_().m_82542_((double)f, 0.98D, (double)f));
      this.m_6478_(MoverType.SELF, this.m_20184_());
   }

   public void bounce(@NotNull Direction direction) {
      Intrinsics.checkNotNullParameter(direction, "direction");
      double speed = this.m_20184_().m_82553_();
      if (speed < 0.15D) {
         this.m_20256_(Vec3.f_82478_);
      } else {
         Direction.Axis var10000 = direction.m_122434_();
         switch (var10000 == null ? -1 : BounceProjectile.WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) {
            case 1:
               this.m_20256_(this.m_20184_().m_82542_(-0.6D, 0.8D, 0.8D));
               break;
            case 2:
               this.m_20256_(this.m_20184_().m_82542_(0.8D, -0.5D, 0.8D));
               if (this.m_20184_().m_7098_() < (double)this.getCustomGravity()) {
                  this.m_20256_(this.m_20184_().m_82542_(1.0D, 0.0D, 1.0D));
               }
               break;
            case 3:
               this.m_20256_(this.m_20184_().m_82542_(0.8D, 0.8D, -0.6D));
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }

      }
   }

   // $FF: synthetic class
   @Metadata(
      mv = {2, 0, 0},
      k = 3,
      xi = 48
   )
   public class WhenMappings {
      // $FF: synthetic field
      public static final int[] $EnumSwitchMapping$0;

      static {
         int[] var0 = new int[Axis.values().length];

         try {
            var0[Axis.X.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[Axis.Y.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[Axis.Z.ordinal()] = 3;
         } catch (NoSuchFieldError var2) {
         }

         $EnumSwitchMapping$0 = var0;
      }
   }
}
