/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Huey;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Huey.HueyModel;
import frontline.combat.fcp.entity.vehicle.Huey.HueyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HueyRenderer
extends VehicleRenderer<HueyEntity> {
    public HueyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new HueyModel());
    }

    public ResourceLocation getTextureLocation(HueyEntity entity) {
        return entity.getCurrentTexture();
    }
}

