/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.KR06Pants;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KR06PantsModel
extends GeoModel<KR06Pants> {
    public ResourceLocation getAnimationResource(KR06Pants object) {
        return null;
    }

    public ResourceLocation getModelResource(KR06Pants object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/kr06_pants.geo.json");
    }

    public ResourceLocation getTextureResource(KR06Pants object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/kr06_pants.png");
    }
}

