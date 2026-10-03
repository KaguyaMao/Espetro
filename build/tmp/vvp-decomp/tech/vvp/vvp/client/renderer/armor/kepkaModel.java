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
import tech.vvp.vvp.item.armor.kepka;

public class kepkaModel
extends GeoModel<kepka> {
    public ResourceLocation getModelResource(kepka object) {
        return new ResourceLocation("vvp", "geo/kepka.geo.json");
    }

    public ResourceLocation getTextureResource(kepka object) {
        return new ResourceLocation("vvp", "textures/armor/kepki.png");
    }

    public ResourceLocation getAnimationResource(kepka animatable) {
        return new ResourceLocation("vvp", "animations/usahelmet.animation.json");
    }
}

