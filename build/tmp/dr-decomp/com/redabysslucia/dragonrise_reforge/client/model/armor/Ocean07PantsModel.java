/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.Ocean07Pants;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Ocean07PantsModel
extends GeoModel<Ocean07Pants> {
    public ResourceLocation getAnimationResource(Ocean07Pants object) {
        return null;
    }

    public ResourceLocation getModelResource(Ocean07Pants object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/ocean07_pants.geo.json");
    }

    public ResourceLocation getTextureResource(Ocean07Pants object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/ocean07_pants.png");
    }
}

