/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.Mod
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.model.Projectile.Hellfire;

import com.atsuishio.superbwarfare.Mod;
import frontline.combat.fcp.entity.projectile.Hellfire.WireGuidedHellfireEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WireGuidedHellfireModel
extends GeoModel<WireGuidedHellfireEntity> {
    public ResourceLocation getAnimationResource(WireGuidedHellfireEntity entity) {
        return Mod.loc((String)"animations/javelin.animation.json");
    }

    public ResourceLocation getModelResource(WireGuidedHellfireEntity entity) {
        return new ResourceLocation("fcp", "geo/hellfire.geo.json");
    }

    public ResourceLocation getTextureResource(WireGuidedHellfireEntity entity) {
        return new ResourceLocation("fcp", "textures/entity/hellfire/hellfire.png");
    }
}

