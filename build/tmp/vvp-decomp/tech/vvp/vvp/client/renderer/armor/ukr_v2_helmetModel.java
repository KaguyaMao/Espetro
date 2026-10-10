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
import tech.vvp.vvp.item.armor.ukr_v2_helmet;

public class ukr_v2_helmetModel
extends GeoModel<ukr_v2_helmet> {
    public ResourceLocation getModelResource(ukr_v2_helmet object) {
        return new ResourceLocation("vvp", "geo/armor/ukr_v2_helmet.geo.json");
    }

    public ResourceLocation getTextureResource(ukr_v2_helmet object) {
        return new ResourceLocation("vvp", "textures/armor/ukr_v2.png");
    }

    public ResourceLocation getAnimationResource(ukr_v2_helmet animatable) {
        return new ResourceLocation("vvp", "animations/usachest.animation.json");
    }
}

