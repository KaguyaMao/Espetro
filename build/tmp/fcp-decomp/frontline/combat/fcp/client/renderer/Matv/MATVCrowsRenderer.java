/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Matv;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Matv.MATVCrowsModel;
import frontline.combat.fcp.entity.vehicle.Matv.MATVCrowsEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MATVCrowsRenderer
extends VehicleRenderer<MATVCrowsEntity> {
    public MATVCrowsRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new MATVCrowsModel());
    }

    public ResourceLocation getTextureLocation(MATVCrowsEntity entity) {
        return entity.getCurrentTexture();
    }
}

