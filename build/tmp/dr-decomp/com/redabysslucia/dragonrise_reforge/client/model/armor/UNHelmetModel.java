/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.UNHelmet;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class UNHelmetModel
extends GeoModel<UNHelmet> {
    public ResourceLocation getAnimationResource(UNHelmet object) {
        return null;
    }

    public ResourceLocation getModelResource(UNHelmet object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/un_helmet.geo.json");
    }

    public ResourceLocation getTextureResource(UNHelmet object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/un_helmet.png");
    }
}

