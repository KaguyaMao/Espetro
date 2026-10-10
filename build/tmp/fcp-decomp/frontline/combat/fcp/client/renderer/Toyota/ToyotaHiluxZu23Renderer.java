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
import frontline.combat.fcp.client.model.Toyota.ToyotaHiluxZu23Model;
import frontline.combat.fcp.entity.vehicle.Toyota.ToyotaHiluxZu23Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ToyotaHiluxZu23Renderer
extends VehicleRenderer<ToyotaHiluxZu23Entity> {
    public ToyotaHiluxZu23Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new ToyotaHiluxZu23Model());
    }

    public ResourceLocation getTextureLocation(ToyotaHiluxZu23Entity entity) {
        return entity.getCurrentTexture();
    }
}

