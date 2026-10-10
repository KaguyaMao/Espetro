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
import frontline.combat.fcp.client.model.Stryker.StrykerMk19Model;
import frontline.combat.fcp.entity.vehicle.Stryker.StrykerMk19Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StrykerMk19Renderer
extends VehicleRenderer<StrykerMk19Entity> {
    public StrykerMk19Renderer(EntityRendererProvider.Context c) {
        super(c, (GeoModel)new StrykerMk19Model());
    }

    public ResourceLocation getTextureLocation(StrykerMk19Entity e) {
        return e.getCurrentTexture();
    }
}

