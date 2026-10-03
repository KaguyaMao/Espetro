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
import tech.vvp.vvp.client.model.Stryker_M1296Model;
import tech.vvp.vvp.entity.vehicle.Stryker_M1296Entity;

public class Stryker_M1296Renderer
extends VehicleRenderer<Stryker_M1296Entity> {
    public Stryker_M1296Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new Stryker_M1296Model());
    }

    public ResourceLocation getTextureLocation(Stryker_M1296Entity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();
        if (camoType >= 0 && camoType < textures.length) {
            return textures[camoType];
        }
        return textures[0];
    }
}

