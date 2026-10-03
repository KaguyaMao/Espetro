/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Humvee;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Humvee.HumveeModel;
import frontline.combat.fcp.entity.vehicle.Humvee.HumveeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HumveeRenderer
extends VehicleRenderer<HumveeEntity> {
    public HumveeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new HumveeModel());
    }

    public ResourceLocation getTextureLocation(HumveeEntity entity) {
        return entity.getCurrentTexture();
    }
}

