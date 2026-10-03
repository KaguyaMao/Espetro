/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.MED21Chest;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MED21ChestModel
extends GeoModel<MED21Chest> {
    public ResourceLocation getAnimationResource(MED21Chest object) {
        return null;
    }

    public ResourceLocation getModelResource(MED21Chest object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/med21_chest.geo.json");
    }

    public ResourceLocation getTextureResource(MED21Chest object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/med21_chest.png");
    }
}

