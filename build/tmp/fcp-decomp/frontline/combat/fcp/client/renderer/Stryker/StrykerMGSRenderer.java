/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Stryker;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Stryker.StrykerMGSModel;
import frontline.combat.fcp.entity.vehicle.Stryker.StrykerMGSEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StrykerMGSRenderer
extends VehicleRenderer<StrykerMGSEntity> {
    public StrykerMGSRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new StrykerMGSModel());
    }

    public ResourceLocation getTextureLocation(StrykerMGSEntity entity) {
        return entity.getCurrentTexture();
    }
}

