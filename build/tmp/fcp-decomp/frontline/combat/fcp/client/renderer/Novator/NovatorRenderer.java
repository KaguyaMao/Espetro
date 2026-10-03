/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Novator;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Novator.NovatorModel;
import frontline.combat.fcp.entity.vehicle.Novator.NovatorEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NovatorRenderer
extends VehicleRenderer<NovatorEntity> {
    public NovatorRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new NovatorModel());
    }

    public ResourceLocation getTextureLocation(NovatorEntity entity) {
        return entity.getCurrentTexture();
    }
}

