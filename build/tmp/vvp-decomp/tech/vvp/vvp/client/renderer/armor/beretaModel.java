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
import tech.vvp.vvp.item.armor.bereta;

public class beretaModel
extends GeoModel<bereta> {
    public ResourceLocation getModelResource(bereta object) {
        return new ResourceLocation("vvp", "geo/bereta.geo.json");
    }

    public ResourceLocation getTextureResource(bereta object) {
        return new ResourceLocation("vvp", "textures/armor/kepki.png");
    }

    public ResourceLocation getAnimationResource(bereta animatable) {
        return new ResourceLocation("vvp", "animations/usahelmet.animation.json");
    }
}

