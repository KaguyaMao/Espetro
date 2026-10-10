/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.Gorka3Leggings;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Gorka3LeggingsModel
extends GeoModel<Gorka3Leggings> {
    public ResourceLocation getAnimationResource(Gorka3Leggings object) {
        return null;
    }

    public ResourceLocation getModelResource(Gorka3Leggings object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/gorka3_leggings.geo.json");
    }

    public ResourceLocation getTextureResource(Gorka3Leggings object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/gorka3_leggings.png");
    }
}

