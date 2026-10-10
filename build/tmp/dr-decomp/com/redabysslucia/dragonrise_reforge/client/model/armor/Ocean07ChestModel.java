/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.Ocean07Chest;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Ocean07ChestModel
extends GeoModel<Ocean07Chest> {
    public ResourceLocation getAnimationResource(Ocean07Chest object) {
        return null;
    }

    public ResourceLocation getModelResource(Ocean07Chest object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/ocean07_chest.geo.json");
    }

    public ResourceLocation getTextureResource(Ocean07Chest object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/ocean07_chest.png");
    }
}

