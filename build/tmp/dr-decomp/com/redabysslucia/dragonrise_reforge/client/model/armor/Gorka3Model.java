/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.Gorka3;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Gorka3Model
extends GeoModel<Gorka3> {
    public ResourceLocation getAnimationResource(Gorka3 object) {
        return null;
    }

    public ResourceLocation getModelResource(Gorka3 object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/gorka3.geo.json");
    }

    public ResourceLocation getTextureResource(Gorka3 object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/gorka3.png");
    }
}

