/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.CNchest;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CNchestModel
extends GeoModel<CNchest> {
    public ResourceLocation getAnimationResource(CNchest object) {
        return null;
    }

    public ResourceLocation getModelResource(CNchest object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/cnchest.geo.json");
    }

    public ResourceLocation getTextureResource(CNchest object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/cnchest.png");
    }
}

