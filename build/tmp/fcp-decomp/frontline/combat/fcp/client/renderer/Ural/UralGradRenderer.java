/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Ural;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Ural.UralGradModel;
import frontline.combat.fcp.entity.vehicle.Ural.UralGradEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class UralGradRenderer
extends VehicleRenderer<UralGradEntity> {
    public UralGradRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new UralGradModel());
    }

    public ResourceLocation getTextureLocation(UralGradEntity entity) {
        return entity.getCurrentTexture();
    }
}

