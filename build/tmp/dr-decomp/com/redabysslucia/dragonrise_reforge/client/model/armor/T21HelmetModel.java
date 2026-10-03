/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.T21Helmet;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class T21HelmetModel
extends GeoModel<T21Helmet> {
    public ResourceLocation getAnimationResource(T21Helmet object) {
        return null;
    }

    public ResourceLocation getModelResource(T21Helmet object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/t21_helmet.geo.json");
    }

    public ResourceLocation getTextureResource(T21Helmet object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/t21_helmet.png");
    }
}

