/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.GazTigr;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.GazTigr.GazTigrGLModel;
import frontline.combat.fcp.entity.vehicle.GazTigr.GazTigrGLEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GazTigrGLRenderer
extends VehicleRenderer<GazTigrGLEntity> {
    public GazTigrGLRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new GazTigrGLModel());
    }

    public ResourceLocation getTextureLocation(GazTigrGLEntity entity) {
        return entity.getCurrentTexture();
    }
}

