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
import tech.vvp.vvp.item.armor.multicamhelmet;

public class multicamhelmetModel
extends GeoModel<multicamhelmet> {
    public ResourceLocation getModelResource(multicamhelmet object) {
        return new ResourceLocation("vvp", "geo/multicamhelmet.geo.json");
    }

    public ResourceLocation getTextureResource(multicamhelmet object) {
        return new ResourceLocation("vvp", "textures/armor/multicamhelmet.png");
    }

    public ResourceLocation getAnimationResource(multicamhelmet animatable) {
        return new ResourceLocation("vvp", "animations/usahelmet.animation.json");
    }
}

