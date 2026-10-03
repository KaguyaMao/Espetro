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
import tech.vvp.vvp.item.armor.rus_helmet;

public class rus_helmetModel
extends GeoModel<rus_helmet> {
    public ResourceLocation getModelResource(rus_helmet object) {
        return new ResourceLocation("vvp", "geo/armor/rus_helmet.geo.json");
    }

    public ResourceLocation getTextureResource(rus_helmet object) {
        return new ResourceLocation("vvp", "textures/armor/rus_armor.png");
    }

    public ResourceLocation getAnimationResource(rus_helmet animatable) {
        return new ResourceLocation("vvp", "animations/usachest.animation.json");
    }
}

