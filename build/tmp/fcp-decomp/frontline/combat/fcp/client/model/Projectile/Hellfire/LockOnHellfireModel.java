/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.model.Projectile.Hellfire;

import frontline.combat.fcp.entity.projectile.Hellfire.LockOnHellfireEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LockOnHellfireModel
extends GeoModel<LockOnHellfireEntity> {
    public ResourceLocation getAnimationResource(LockOnHellfireEntity entity) {
        return new ResourceLocation("superbwarfare", "animations/javelin.animation.json");
    }

    public ResourceLocation getModelResource(LockOnHellfireEntity entity) {
        return new ResourceLocation("fcp", "geo/hellfire.geo.json");
    }

    public ResourceLocation getTextureResource(LockOnHellfireEntity entity) {
        return new ResourceLocation("fcp", "textures/entity/hellfire/hellfire.png");
    }
}

