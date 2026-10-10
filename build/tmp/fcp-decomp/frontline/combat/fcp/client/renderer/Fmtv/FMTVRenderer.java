/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Fmtv;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Fmtv.FMTVModel;
import frontline.combat.fcp.entity.vehicle.Fmtv.FMTVEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FMTVRenderer
extends VehicleRenderer<FMTVEntity> {
    public FMTVRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new FMTVModel());
    }

    public ResourceLocation getTextureLocation(FMTVEntity entity) {
        return entity.getCurrentTexture();
    }
}

