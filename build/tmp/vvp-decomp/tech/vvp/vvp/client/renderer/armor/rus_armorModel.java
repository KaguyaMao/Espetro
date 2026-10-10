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
import tech.vvp.vvp.item.armor.rus_armor;

public class rus_armorModel
extends GeoModel<rus_armor> {
    public ResourceLocation getModelResource(rus_armor object) {
        return new ResourceLocation("vvp", "geo/armor/rus_armor.geo.json");
    }

    public ResourceLocation getTextureResource(rus_armor object) {
        return new ResourceLocation("vvp", "textures/armor/rus_armor.png");
    }

    public ResourceLocation getAnimationResource(rus_armor animatable) {
        return new ResourceLocation("vvp", "animations/usachest.animation.json");
    }
}

