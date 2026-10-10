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
import tech.vvp.vvp.item.armor.mi28helmet;

public class mi28helmetModel
extends GeoModel<mi28helmet> {
    public ResourceLocation getModelResource(mi28helmet object) {
        return new ResourceLocation("vvp", "geo/mi28_helmet.geo.json");
    }

    public ResourceLocation getTextureResource(mi28helmet object) {
        return new ResourceLocation("vvp", "textures/armor/mi28_armor.png");
    }

    public ResourceLocation getAnimationResource(mi28helmet animatable) {
        return new ResourceLocation("vvp", "animations/usahelmet.animation.json");
    }
}

