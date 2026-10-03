/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  software.bernie.geckolib.core.animatable.GeoAnimatable
 *  software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
 *  software.bernie.geckolib.core.animation.AnimatableManager$ControllerRegistrar
 *  software.bernie.geckolib.core.animation.AnimationController
 *  software.bernie.geckolib.core.object.PlayState
 *  software.bernie.geckolib.util.GeckoLibUtil
 */
package frontline.combat.fcp.entity.vehicle.Trailers.ExampleTrailer;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import frontline.combat.fcp.entity.vehicle.Trailers.AbstractTrailerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ExampleTrailerEntity
extends AbstractTrailerEntity {
    private static final ResourceLocation[] CAMO_TEXTURES = new ResourceLocation[]{new ResourceLocation("fcp", "textures/entity/lav/lav25_camo1.png"), new ResourceLocation("fcp", "textures/entity/lav/lav25_camo2.png"), new ResourceLocation("fcp", "textures/entity/lav/lav25_camo3.png"), new ResourceLocation("fcp", "textures/entity/lav/lav25_od.png"), new ResourceLocation("fcp", "textures/entity/lav/lav25_tan.png"), new ResourceLocation("fcp", "textures/entity/lav/lav25_camo1_wrecked.png"), new ResourceLocation("fcp", "textures/entity/lav/lav25_camo2_wrecked.png"), new ResourceLocation("fcp", "textures/entity/lav/lav25_camo3_wrecked.png"), new ResourceLocation("fcp", "textures/entity/lav/lav25_od_wrecked.png"), new ResourceLocation("fcp", "textures/entity/lav/lav25_tan_wrecked.png")};
    private static final String[] CAMO_NAMES = new String[]{"Camo Variant 1", "Camo Variant 2", "Camo Variant 3", "No-Camo", "Tan"};
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache((GeoAnimatable)this);

    public ExampleTrailerEntity(EntityType<ExampleTrailerEntity> type, Level world) {
        super((EntityType<? extends GeoVehicleEntity>)type, world);
    }

    @Override
    public ResourceLocation[] getCamoTextures() {
        return CAMO_TEXTURES;
    }

    @Override
    public String[] getCamoNames() {
        return CAMO_NAMES;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar reg) {
        reg.add(new AnimationController[]{new AnimationController((GeoAnimatable)this, "base", 0, state -> PlayState.STOP)});
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}

