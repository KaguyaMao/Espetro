/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.Desert07Pants;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Desert07PantsModel
extends GeoModel<Desert07Pants> {
    public ResourceLocation getAnimationResource(Desert07Pants object) {
        return null;
    }

    public ResourceLocation getModelResource(Desert07Pants object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/desert07_pants.geo.json");
    }

    public ResourceLocation getTextureResource(Desert07Pants object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/desert07_pants.png");
    }
}

