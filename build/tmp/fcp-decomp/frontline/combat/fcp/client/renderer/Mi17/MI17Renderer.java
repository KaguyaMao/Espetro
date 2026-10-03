/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Mi17;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Mi17.MI17Model;
import frontline.combat.fcp.entity.vehicle.Mi17.MI17Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MI17Renderer
extends VehicleRenderer<MI17Entity> {
    public MI17Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new MI17Model());
    }

    public ResourceLocation getTextureLocation(MI17Entity entity) {
        return entity.getCurrentTexture();
    }
}

