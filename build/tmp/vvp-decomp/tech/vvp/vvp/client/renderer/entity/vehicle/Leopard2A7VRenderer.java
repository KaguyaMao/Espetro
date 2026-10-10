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
import tech.vvp.vvp.client.model.Leopard2A7VModel;
import tech.vvp.vvp.entity.vehicle.Leopard2A7VEntity;

public class Leopard2A7VRenderer
extends VehicleRenderer<Leopard2A7VEntity> {
    public Leopard2A7VRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new Leopard2A7VModel());
    }

    public ResourceLocation getTextureLocation(Leopard2A7VEntity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();
        return camoType >= 0 && camoType < textures.length ? textures[camoType] : textures[0];
    }
}

