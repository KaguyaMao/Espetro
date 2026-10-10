/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.Desert07Chest;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Desert07ChestModel
extends GeoModel<Desert07Chest> {
    public ResourceLocation getAnimationResource(Desert07Chest object) {
        return null;
    }

    public ResourceLocation getModelResource(Desert07Chest object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/desert07_chest.geo.json");
    }

    public ResourceLocation getTextureResource(Desert07Chest object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/desert07_chest.png");
    }
}

