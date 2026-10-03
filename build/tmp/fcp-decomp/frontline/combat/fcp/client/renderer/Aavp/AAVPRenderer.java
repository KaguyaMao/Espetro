/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Aavp;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Aavp.AAVPModel;
import frontline.combat.fcp.entity.vehicle.Aavp.AAVPEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AAVPRenderer
extends VehicleRenderer<AAVPEntity> {
    public AAVPRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new AAVPModel());
    }

    public ResourceLocation getTextureLocation(AAVPEntity entity) {
        return entity.getCurrentTexture();
    }
}

