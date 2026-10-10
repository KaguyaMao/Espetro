/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Stryker;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Stryker.StrykerMortarModel;
import frontline.combat.fcp.entity.vehicle.Stryker.StrykerMortarEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StrykerMortarRenderer
extends VehicleRenderer<StrykerMortarEntity> {
    public StrykerMortarRenderer(EntityRendererProvider.Context c) {
        super(c, (GeoModel)new StrykerMortarModel());
    }

    public ResourceLocation getTextureLocation(StrykerMortarEntity e) {
        return e.getCurrentTexture();
    }
}

