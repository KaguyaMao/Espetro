package com.atsuishio.superbwarfare.entity.projectile;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.animation.entity.BasicProjectileAnimationInstance;
import com.atsuishio.superbwarfare.resource.model.ProjectileModelReloadListener;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.baked.BakedBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BakedModelInstance;
import java.util.List;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\u0017J\n\u0010\u0004\u001a\u0004\u0018\u00010\u0003H\u0017J\u000e\u0010\u0005\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u0006H\u0016J\n\u0010\u0007\u001a\u0004\u0018\u00010\u0003H\u0016J\b\u0010\b\u001a\u00020\tH\u0016J\b\u0010\n\u001a\u00020\tH\u0016J\n\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0016¨\u0006\r"},
   d2 = {"Lcom/atsuishio/superbwarfare/entity/projectile/BasicGeoProjectileEntity;", "", "getModel", "Lnet/minecraft/resources/ResourceLocation;", "getAnimation", "getAnimationInstance", "Lcom/atsuishio/superbwarfare/client/animation/entity/BasicProjectileAnimationInstance;", "getEmissiveTexture", "getHiddenTicks", "", "getFlareHiddenTicks", "getModelInstance", "Lcom/github/mcmodderanchor/simplebedrockmodel/v2/common/model/runtime/BakedModelInstance;", "superbwarfare"}
)
public interface BasicGeoProjectileEntity {
   /** @deprecated */
   @Deprecated(
      message = "Model location is auto loaded now"
   )
   @NotNull
   ResourceLocation getModel();

   /** @deprecated */
   @Deprecated(
      message = "Animation location is auto loaded now"
   )
   @Nullable
   ResourceLocation getAnimation();

   @Nullable
   BasicProjectileAnimationInstance<?> getAnimationInstance();

   @Nullable
   ResourceLocation getEmissiveTexture();

   int getHiddenTicks();

   int getFlareHiddenTicks();

   @Nullable
   BakedModelInstance getModelInstance();

   @Metadata(
      mv = {2, 0, 0},
      k = 3,
      xi = 48
   )
   public static final class DefaultImpls {
      /** @deprecated */
      @Deprecated(
         message = "Model location is auto loaded now"
      )
      @NotNull
      public static ResourceLocation getModel(@NotNull BasicGeoProjectileEntity $this) {
         return Mod.Companion.loc("projectile/projectile");
      }

      /** @deprecated */
      @Deprecated(
         message = "Animation location is auto loaded now"
      )
      @Nullable
      public static ResourceLocation getAnimation(@NotNull BasicGeoProjectileEntity $this) {
         return null;
      }

      @Nullable
      public static BasicProjectileAnimationInstance<?> getAnimationInstance(@NotNull BasicGeoProjectileEntity $this) {
         return null;
      }

      @Nullable
      public static ResourceLocation getEmissiveTexture(@NotNull BasicGeoProjectileEntity $this) {
         return null;
      }

      public static int getHiddenTicks(@NotNull BasicGeoProjectileEntity $this) {
         return 0;
      }

      public static int getFlareHiddenTicks(@NotNull BasicGeoProjectileEntity $this) {
         return 3;
      }

      @Nullable
      public static BakedModelInstance getModelInstance(@NotNull BasicGeoProjectileEntity $this) {
         Intrinsics.checkNotNull($this, "null cannot be cast to non-null type net.minecraft.world.entity.Entity");
         Entity entity = (Entity)$this;
         String var10000 = entity.m_6095_().m_20675_();
         Intrinsics.checkNotNullExpressionValue(var10000, "getDescriptionId(...)");
         CharSequence var7 = (CharSequence)var10000;
         String namespace = new String[]{"."};
         List var2 = StringsKt.split$default(var7, namespace, false, 0, 6, (Object)null);
         namespace = (String)var2.get(1);
         String id = (String)var2.get(2);
         BakedBedrockModel var5 = (BakedBedrockModel)ProjectileModelReloadListener.INSTANCE.getModel(new ResourceLocation(namespace, "models/bedrock/projectile/" + id + ".geo.json"));
         return var5 != null ? var5.createInstance() : null;
      }
   }
}
