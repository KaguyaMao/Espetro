/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.entity.projectile;

import com.redabysslucia.dragonrise_reforge.entities.projectile.AAshellEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AAshellModel
extends GeoModel<AAshellEntity> {
    public ResourceLocation getAnimationResource(AAshellEntity entity) {
        return new ResourceLocation("dragonrise_reforge", "animations/airbomb500kg.animation.json");
    }

    public ResourceLocation getModelResource(AAshellEntity entity) {
        return new ResourceLocation("dragonrise_reforge", "geo/small_cannon_shell.geo.json");
    }

    public ResourceLocation getTextureResource(AAshellEntity entity) {
        return new ResourceLocation("dragonrise_reforge", "textures/entity/airbomb500kg.png");
    }
}

