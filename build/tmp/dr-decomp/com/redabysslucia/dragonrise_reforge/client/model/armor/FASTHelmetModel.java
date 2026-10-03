/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.FASTHelmet;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FASTHelmetModel
extends GeoModel<FASTHelmet> {
    public ResourceLocation getAnimationResource(FASTHelmet object) {
        return null;
    }

    public ResourceLocation getModelResource(FASTHelmet object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/fast_helmet.geo.json");
    }

    public ResourceLocation getTextureResource(FASTHelmet object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/fast_helmet.png");
    }
}

