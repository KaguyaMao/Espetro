/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.item.armor.panama;

public class panamaModel
extends GeoModel<panama> {
    public ResourceLocation getModelResource(panama object) {
        return new ResourceLocation("vvp", "geo/panama.geo.json");
    }

    public ResourceLocation getTextureResource(panama object) {
        return new ResourceLocation("vvp", "textures/armor/kepki.png");
    }

    public ResourceLocation getAnimationResource(panama animatable) {
        return new ResourceLocation("vvp", "animations/usahelmet.animation.json");
    }
}

