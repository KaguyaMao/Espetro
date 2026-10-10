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
import frontline.combat.fcp.client.model.GazTigr.GazTigrMGModel;
import frontline.combat.fcp.entity.vehicle.GazTigr.GazTigrMGEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GazTigrMGRenderer
extends VehicleRenderer<GazTigrMGEntity> {
    public GazTigrMGRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new GazTigrMGModel());
    }

    public ResourceLocation getTextureLocation(GazTigrMGEntity entity) {
        return entity.getCurrentTexture();
    }
}

