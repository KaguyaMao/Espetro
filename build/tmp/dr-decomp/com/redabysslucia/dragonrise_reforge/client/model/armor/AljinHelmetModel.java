/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.AljinHelmet;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AljinHelmetModel
extends GeoModel<AljinHelmet> {
    public ResourceLocation getAnimationResource(AljinHelmet object) {
        return null;
    }

    public ResourceLocation getModelResource(AljinHelmet object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/aljin_helmet.geo.json");
    }

    public ResourceLocation getTextureResource(AljinHelmet object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/aljin_helmet.png");
    }
}

