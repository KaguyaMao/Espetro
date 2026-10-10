/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Lav;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Lav.Lav25Model;
import frontline.combat.fcp.entity.vehicle.Lav.Lav25Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Lav25Renderer
extends VehicleRenderer<Lav25Entity> {
    public Lav25Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new Lav25Model());
    }

    public ResourceLocation getTextureLocation(Lav25Entity entity) {
        return entity.getCurrentTexture();
    }
}

