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
import frontline.combat.fcp.client.model.Stryker.StrykerM2Model;
import frontline.combat.fcp.entity.vehicle.Stryker.StrykerM2Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StrykerM2Renderer
extends VehicleRenderer<StrykerM2Entity> {
    public StrykerM2Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new StrykerM2Model());
    }

    public ResourceLocation getTextureLocation(StrykerM2Entity entity) {
        return entity.getCurrentTexture();
    }
}

