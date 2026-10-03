package com.atsuishio.superbwarfare.entity.projectile;

import com.atsuishio.superbwarfare.world.phys.EntityResult;
import java.util.Comparator;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = {"\u0000\n\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u00022\u000e\u0010\u0003\u001a\n \u0004*\u0004\u0018\u0001H\u0002H\u00022\u000e\u0010\u0005\u001a\n \u0004*\u0004\u0018\u0001H\u0002H\u0002H\n¢\u0006\u0004\b\u0006\u0010\u0007¨\u0006\b"},
   d2 = {"<anonymous>", "", "T", "a", "kotlin.jvm.PlatformType", "b", "compare", "(Ljava/lang/Object;Ljava/lang/Object;)I", "kotlin/comparisons/ComparisonsKt__ComparisonsKt$compareBy$2"}
)
@SourceDebugExtension({"SMAP\nComparisons.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Comparisons.kt\nkotlin/comparisons/ComparisonsKt__ComparisonsKt$compareBy$2\n+ 2 ProjectileEntity.kt\ncom/atsuishio/superbwarfare/entity/projectile/ProjectileEntity\n*L\n1#1,102:1\n354#2:103\n*E\n"})
public final class ProjectileEntity$tick$$inlined$sortBy$1<T> implements Comparator {
   // $FF: synthetic field
   final ProjectileEntity this$0;

   public ProjectileEntity$tick$$inlined$sortBy$1(ProjectileEntity var1) {
      this.this$0 = var1;
   }

   public final int compare(T a, T b) {
      EntityResult it = (EntityResult)a;
      int $i$a$-sortBy-ProjectileEntity$tick$3 = 0;
      Vec3 var10000 = it.getHitPos();
      Entity var10001 = this.this$0.m_19749_();
      Intrinsics.checkNotNull(var10001);
      Comparable var8 = (Comparable)var10000.m_82554_(var10001.m_20182_());
      it = (EntityResult)b;
      Comparable var5 = var8;
      $i$a$-sortBy-ProjectileEntity$tick$3 = 0;
      Vec3 var9 = it.getHitPos();
      var10001 = this.this$0.m_19749_();
      Intrinsics.checkNotNull(var10001);
      return ComparisonsKt.compareValues(var5, (Comparable)var9.m_82554_(var10001.m_20182_()));
   }
}
