/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Uaz;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Uaz.UAZSPG9Model;
import frontline.combat.fcp.entity.vehicle.Uaz.UAZSPG9Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class UAZSPG9Renderer
extends VehicleRenderer<UAZSPG9Entity> {
    public UAZSPG9Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new UAZSPG9Model());
    }

    public ResourceLocation getTextureLocation(UAZSPG9Entity entity) {
        return entity.getCurrentTexture();
    }
}

