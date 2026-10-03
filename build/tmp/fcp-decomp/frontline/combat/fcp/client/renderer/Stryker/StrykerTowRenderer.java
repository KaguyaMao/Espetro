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
import frontline.combat.fcp.client.model.Stryker.StrykerTowModel;
import frontline.combat.fcp.entity.vehicle.Stryker.StrykerTowEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StrykerTowRenderer
extends VehicleRenderer<StrykerTowEntity> {
    public StrykerTowRenderer(EntityRendererProvider.Context c) {
        super(c, (GeoModel)new StrykerTowModel());
    }

    public ResourceLocation getTextureLocation(StrykerTowEntity e) {
        return e.getCurrentTexture();
    }
}

