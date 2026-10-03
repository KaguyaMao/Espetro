/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.CNfast;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CNfastModel
extends GeoModel<CNfast> {
    public ResourceLocation getAnimationResource(CNfast object) {
        return null;
    }

    public ResourceLocation getModelResource(CNfast object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/cnfast.geo.json");
    }

    public ResourceLocation getTextureResource(CNfast object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/cnfast.png");
    }
}

