/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.CN21;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CN21Model
extends GeoModel<CN21> {
    public ResourceLocation getAnimationResource(CN21 object) {
        return null;
    }

    public ResourceLocation getModelResource(CN21 object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/cn21helmet.geo.json");
    }

    public ResourceLocation getTextureResource(CN21 object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/cn21helmet.png");
    }
}

