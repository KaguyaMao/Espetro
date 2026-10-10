/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package tech.vvp.vvp.client.model.item;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.item.Shell122mmCasingItem;

public class Shell122mmCasingModel
extends GeoModel<Shell122mmCasingItem> {
    public ResourceLocation getModelResource(Shell122mmCasingItem animatable) {
        return new ResourceLocation("vvp", "geo/shell_122mm_casing.geo.json");
    }

    public ResourceLocation getTextureResource(Shell122mmCasingItem animatable) {
        return new ResourceLocation("vvp", "textures/item/shell_122mm_casing.png");
    }

    public ResourceLocation getAnimationResource(Shell122mmCasingItem animatable) {
        return null;
    }
}

