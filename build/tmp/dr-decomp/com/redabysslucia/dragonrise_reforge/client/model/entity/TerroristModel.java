/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.entity;

import com.redabysslucia.dragonrise_reforge.entities.TerroristEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TerroristModel
extends GeoModel<TerroristEntity> {
    public ResourceLocation getAnimationResource(TerroristEntity entity) {
        return new ResourceLocation("dragonrise_reforge", "animations/terrorist.animation.json");
    }

    public ResourceLocation getModelResource(TerroristEntity entity) {
        return new ResourceLocation("dragonrise_reforge", "geo/terrorist.geo.json");
    }

    public ResourceLocation getTextureResource(TerroristEntity entity) {
        return new ResourceLocation("dragonrise_reforge", "textures/entity/terrorist.png");
    }
}

