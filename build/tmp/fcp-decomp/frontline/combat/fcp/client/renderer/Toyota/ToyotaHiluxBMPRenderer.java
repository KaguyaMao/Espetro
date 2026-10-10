/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Toyota;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Toyota.ToyotaHiluxBMPModel;
import frontline.combat.fcp.entity.vehicle.Toyota.ToyotaHiluxBMPEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ToyotaHiluxBMPRenderer
extends VehicleRenderer<ToyotaHiluxBMPEntity> {
    public ToyotaHiluxBMPRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new ToyotaHiluxBMPModel());
    }

    public ResourceLocation getTextureLocation(ToyotaHiluxBMPEntity entity) {
        return entity.getCurrentTexture();
    }
}

