/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.Ocean07Helmet;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Ocean07HelmetModel
extends GeoModel<Ocean07Helmet> {
    public ResourceLocation getAnimationResource(Ocean07Helmet object) {
        return null;
    }

    public ResourceLocation getModelResource(Ocean07Helmet object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/ocean07_helmet.geo.json");
    }

    public ResourceLocation getTextureResource(Ocean07Helmet object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/ocean07_helmet.png");
    }
}

