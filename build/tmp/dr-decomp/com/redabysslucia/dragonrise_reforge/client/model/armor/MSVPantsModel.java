/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.MSVPants;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MSVPantsModel
extends GeoModel<MSVPants> {
    public ResourceLocation getAnimationResource(MSVPants object) {
        return null;
    }

    public ResourceLocation getModelResource(MSVPants object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/msv_pants.geo.json");
    }

    public ResourceLocation getTextureResource(MSVPants object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/msv_pants.png");
    }
}

