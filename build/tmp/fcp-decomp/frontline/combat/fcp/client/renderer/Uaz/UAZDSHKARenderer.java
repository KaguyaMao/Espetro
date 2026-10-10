/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Uaz;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Uaz.UAZDSHKAModel;
import frontline.combat.fcp.entity.vehicle.Uaz.UAZDSHKAEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class UAZDSHKARenderer
extends VehicleRenderer<UAZDSHKAEntity> {
    public UAZDSHKARenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new UAZDSHKAModel());
    }

    public ResourceLocation getTextureLocation(UAZDSHKAEntity entity) {
        return entity.getCurrentTexture();
    }
}

