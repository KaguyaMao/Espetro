/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.Mod
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.model.Projectile.Sidewinder;

import com.atsuishio.superbwarfare.Mod;
import frontline.combat.fcp.entity.projectile.Sidewinder.SidewinderEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SidewinderModel
extends GeoModel<SidewinderEntity> {
    public ResourceLocation getAnimationResource(SidewinderEntity entity) {
        return Mod.loc((String)"animations/javelin.animation.json");
    }

    public ResourceLocation getModelResource(SidewinderEntity entity) {
        return new ResourceLocation("fcp", "geo/sidewinder.geo.json");
    }

    public ResourceLocation getTextureResource(SidewinderEntity entity) {
        return new ResourceLocation("fcp", "textures/entity/sidewinder/sidewinder.png");
    }
}

