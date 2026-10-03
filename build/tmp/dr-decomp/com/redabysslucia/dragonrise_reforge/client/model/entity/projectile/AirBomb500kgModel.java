/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.entity.projectile;

import com.redabysslucia.dragonrise_reforge.entities.projectile.AirBomb500kgEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AirBomb500kgModel
extends GeoModel<AirBomb500kgEntity> {
    public ResourceLocation getAnimationResource(AirBomb500kgEntity entity) {
        return new ResourceLocation("dragonrise_reforge", "animations/airbomb500kg.animation.json");
    }

    public ResourceLocation getModelResource(AirBomb500kgEntity entity) {
        return new ResourceLocation("dragonrise_reforge", "geo/airbomb500kg.geo.json");
    }

    public ResourceLocation getTextureResource(AirBomb500kgEntity entity) {
        return new ResourceLocation("dragonrise_reforge", "textures/entity/airbomb500kg.png");
    }
}

