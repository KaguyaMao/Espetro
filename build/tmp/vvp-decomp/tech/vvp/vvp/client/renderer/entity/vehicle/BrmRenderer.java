/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.client.model.BrmModel;
import tech.vvp.vvp.entity.vehicle.BrmEntity;

public class BrmRenderer
extends VehicleRenderer<BrmEntity> {
    public BrmRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new BrmModel());
    }

    public ResourceLocation getTextureLocation(BrmEntity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();
        return camoType >= 0 && camoType < textures.length ? textures[camoType] : textures[0];
    }
}

