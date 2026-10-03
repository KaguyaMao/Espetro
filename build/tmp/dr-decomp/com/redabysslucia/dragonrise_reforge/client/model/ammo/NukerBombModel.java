/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package com.redabysslucia.dragonrise_reforge.client.model.ammo;

import com.redabysslucia.dragonrise_reforge.entities.projectile.NukerBombEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NukerBombModel
extends GeoModel<NukerBombEntity> {
    public ResourceLocation getAnimationResource(NukerBombEntity entity) {
        return null;
    }

    public ResourceLocation getModelResource(NukerBombEntity entity) {
        return new ResourceLocation("dragonrise_reforge", "geo/nuclearbomb.geo.json");
    }

    public ResourceLocation getTextureResource(NukerBombEntity entity) {
        return new ResourceLocation("dragonrise_reforge", "textures/entity/nuclearbomb.png");
    }
}

