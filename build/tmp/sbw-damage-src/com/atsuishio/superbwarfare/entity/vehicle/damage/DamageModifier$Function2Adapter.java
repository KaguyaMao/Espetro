package com.atsuishio.superbwarfare.entity.vehicle.damage;

import kotlin.jvm.functions.Function2;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class DamageModifier$Function2Adapter implements DamageModifier.CustomDamageModifier {
   private final Function2 fn;

   public DamageModifier$Function2Adapter(Function2 var1) {
      this.fn = var1;
   }

   public float compute(Entity var1, DamageSource var2, float var3) {
      Float var10000 = (Float)this.fn.invoke(var2, var3);
      return var10000 != null ? var10000 : var3;
   }
}
