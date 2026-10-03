/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.client.model.VvpVehicleModel;
import tech.vvp.vvp.entity.vehicle.Ags30Entity;

public class Ags30Model
extends VvpVehicleModel<Ags30Entity> {
    public ResourceLocation getModelResource(Ags30Entity animatable) {
        return new ResourceLocation("vvp", "geo/ags_30.geo.json");
    }

    public ResourceLocation getTextureResource(Ags30Entity animatable) {
        return new ResourceLocation("vvp", "textures/entity/ags_30.png");
    }

    public ResourceLocation getAnimationResource(Ags30Entity animatable) {
        return null;
    }
}

