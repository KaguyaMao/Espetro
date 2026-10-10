/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.Mod
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.model.Projectile.Malyutka;

import com.atsuishio.superbwarfare.Mod;
import frontline.combat.fcp.entity.projectile.Malyutka.MalyutkaEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MalyutkaModel
extends GeoModel<MalyutkaEntity> {
    public ResourceLocation getAnimationResource(MalyutkaEntity entity) {
        return Mod.loc((String)"animations/javelin.animation.json");
    }

    public ResourceLocation getModelResource(MalyutkaEntity entity) {
        return new ResourceLocation("fcp", "geo/malyutka.geo.json");
    }

    public ResourceLocation getTextureResource(MalyutkaEntity entity) {
        return new ResourceLocation("fcp", "textures/entity/malyutka/malyutka.png");
    }
}

