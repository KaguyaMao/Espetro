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
import tech.vvp.vvp.client.model.Btr4Model;
import tech.vvp.vvp.entity.vehicle.Btr4Entity;

public class btr4Renderer
extends VehicleRenderer<Btr4Entity> {
    public btr4Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new Btr4Model());
    }

    public ResourceLocation getTextureLocation(Btr4Entity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();
        return camoType >= 0 && camoType < textures.length ? textures[camoType] : textures[0];
    }
}

