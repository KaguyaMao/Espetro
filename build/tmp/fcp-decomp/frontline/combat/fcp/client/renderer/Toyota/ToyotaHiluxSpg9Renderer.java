/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Toyota;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Toyota.ToyotaHiluxSpg9Model;
import frontline.combat.fcp.entity.vehicle.Toyota.ToyotaHiluxSpg9Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ToyotaHiluxSpg9Renderer
extends VehicleRenderer<ToyotaHiluxSpg9Entity> {
    public ToyotaHiluxSpg9Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new ToyotaHiluxSpg9Model());
    }

    public ResourceLocation getTextureLocation(ToyotaHiluxSpg9Entity entity) {
        return entity.getCurrentTexture();
    }
}

