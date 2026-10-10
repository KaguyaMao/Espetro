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
import tech.vvp.vvp.item.armor.crewhelmet;

public class crewModel
extends GeoModel<crewhelmet> {
    public ResourceLocation getModelResource(crewhelmet object) {
        return new ResourceLocation("vvp", "geo/crew.geo.json");
    }

    public ResourceLocation getTextureResource(crewhelmet object) {
        return new ResourceLocation("vvp", "textures/armor/crew.png");
    }

    public ResourceLocation getAnimationResource(crewhelmet animatable) {
        return new ResourceLocation("vvp", "animations/usahelmet.animation.json");
    }
}

