/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.Pants21;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Pants21Model
extends GeoModel<Pants21> {
    public ResourceLocation getAnimationResource(Pants21 object) {
        return null;
    }

    public ResourceLocation getModelResource(Pants21 object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/pants21.geo.json");
    }

    public ResourceLocation getTextureResource(Pants21 object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/pants21.png");
    }
}

