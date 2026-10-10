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
import frontline.combat.fcp.client.model.Humvee.HumveeTOWModel;
import frontline.combat.fcp.entity.vehicle.Humvee.HumveeTOWEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HumveeTOWRenderer
extends VehicleRenderer<HumveeTOWEntity> {
    public HumveeTOWRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new HumveeTOWModel());
    }

    public ResourceLocation getTextureLocation(HumveeTOWEntity entity) {
        return entity.getCurrentTexture();
    }
}

