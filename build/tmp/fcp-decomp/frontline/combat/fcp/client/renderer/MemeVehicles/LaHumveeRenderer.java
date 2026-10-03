/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.MemeVehicles;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.MemeVehicles.LaHumveeModel;
import frontline.combat.fcp.entity.vehicle.MemeVehicles.LaHumveeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LaHumveeRenderer
extends VehicleRenderer<LaHumveeEntity> {
    public LaHumveeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new LaHumveeModel());
    }

    public ResourceLocation getTextureLocation(LaHumveeEntity entity) {
        return entity.getCurrentTexture();
    }
}

