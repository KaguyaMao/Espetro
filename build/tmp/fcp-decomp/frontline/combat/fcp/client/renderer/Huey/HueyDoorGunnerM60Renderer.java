/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Huey;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Huey.HueyDoorGunnerM60Model;
import frontline.combat.fcp.entity.vehicle.Huey.HueyDoorGunnerM60Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HueyDoorGunnerM60Renderer
extends VehicleRenderer<HueyDoorGunnerM60Entity> {
    public HueyDoorGunnerM60Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new HueyDoorGunnerM60Model());
    }

    public ResourceLocation getTextureLocation(HueyDoorGunnerM60Entity entity) {
        return entity.getCurrentTexture();
    }
}

