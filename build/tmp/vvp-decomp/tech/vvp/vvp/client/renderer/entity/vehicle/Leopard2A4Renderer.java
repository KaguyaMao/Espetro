/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.client.model.Leopard2A4Model;
import tech.vvp.vvp.entity.vehicle.Leopard2A4Entity;

public class Leopard2A4Renderer
extends VehicleRenderer<Leopard2A4Entity> {
    public Leopard2A4Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new Leopard2A4Model());
    }

    public ResourceLocation getTextureLocation(Leopard2A4Entity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();
        if (camoType >= 0 && camoType < textures.length) {
            return textures[camoType];
        }
        return super.getTextureLocation((VehicleEntity)entity);
    }
}

