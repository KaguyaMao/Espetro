/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.MSVChest;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MSVChestModel
extends GeoModel<MSVChest> {
    public ResourceLocation getAnimationResource(MSVChest object) {
        return null;
    }

    public ResourceLocation getModelResource(MSVChest object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/msv_chest.geo.json");
    }

    public ResourceLocation getTextureResource(MSVChest object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/msv_chest.png");
    }
}

