/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.armor;

import com.redabysslucia.dragonrise_reforge.item.armor.Sniper21Helmet;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Sniper21HelmetModel
extends GeoModel<Sniper21Helmet> {
    public ResourceLocation getAnimationResource(Sniper21Helmet object) {
        return null;
    }

    public ResourceLocation getModelResource(Sniper21Helmet object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"geo/sniper21_helmet.geo.json");
    }

    public ResourceLocation getTextureResource(Sniper21Helmet object) {
        return ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)"textures/armor/sniper21_helmet.png");
    }
}

