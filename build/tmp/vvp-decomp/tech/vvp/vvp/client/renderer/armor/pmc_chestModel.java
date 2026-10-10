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
import tech.vvp.vvp.item.armor.pmc_chest;

public class pmc_chestModel
extends GeoModel<pmc_chest> {
    public ResourceLocation getModelResource(pmc_chest object) {
        return new ResourceLocation("vvp", "geo/armor/pmc_armor.geo.json");
    }

    public ResourceLocation getTextureResource(pmc_chest object) {
        return new ResourceLocation("vvp", "textures/armor/pmc.png");
    }

    public ResourceLocation getAnimationResource(pmc_chest animatable) {
        return new ResourceLocation("vvp", "animations/usachest.animation.json");
    }
}

