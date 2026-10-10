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
import tech.vvp.vvp.item.armor.multicamchest;

public class multicamchestModel
extends GeoModel<multicamchest> {
    public ResourceLocation getModelResource(multicamchest object) {
        return new ResourceLocation("vvp", "geo/multicamchest.geo.json");
    }

    public ResourceLocation getTextureResource(multicamchest object) {
        return new ResourceLocation("vvp", "textures/armor/multicamhelmet.png");
    }

    public ResourceLocation getAnimationResource(multicamchest animatable) {
        return new ResourceLocation("vvp", "animations/usachest.animation.json");
    }
}

