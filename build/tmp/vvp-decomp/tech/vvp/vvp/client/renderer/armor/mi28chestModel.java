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
import tech.vvp.vvp.item.armor.mi28chest;

public class mi28chestModel
extends GeoModel<mi28chest> {
    public ResourceLocation getModelResource(mi28chest object) {
        return new ResourceLocation("vvp", "geo/mi28_armor.geo.json");
    }

    public ResourceLocation getTextureResource(mi28chest object) {
        return new ResourceLocation("vvp", "textures/armor/mi28_armor.png");
    }

    public ResourceLocation getAnimationResource(mi28chest animatable) {
        return new ResourceLocation("vvp", "animations/usachest.animation.json");
    }
}

