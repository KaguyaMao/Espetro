/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.KR06Helmet;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KR06HelmetModel
extends GeoModel<KR06Helmet> {
    public ResourceLocation getAnimationResource(KR06Helmet object) {
        return null;
    }

    public ResourceLocation getModelResource(KR06Helmet object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/kr06_helmet.geo.json");
    }

    public ResourceLocation getTextureResource(KR06Helmet object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/kr06_helmet.png");
    }
}

