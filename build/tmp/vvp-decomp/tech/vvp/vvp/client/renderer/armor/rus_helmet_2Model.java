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
import tech.vvp.vvp.item.armor.rus_helmet_2;

public class rus_helmet_2Model
extends GeoModel<rus_helmet_2> {
    public ResourceLocation getModelResource(rus_helmet_2 object) {
        return new ResourceLocation("vvp", "geo/armor/rus_helmet_2.geo.json");
    }

    public ResourceLocation getTextureResource(rus_helmet_2 object) {
        return new ResourceLocation("vvp", "textures/armor/rus_armor.png");
    }

    public ResourceLocation getAnimationResource(rus_helmet_2 animatable) {
        return new ResourceLocation("vvp", "animations/usachest.animation.json");
    }
}

