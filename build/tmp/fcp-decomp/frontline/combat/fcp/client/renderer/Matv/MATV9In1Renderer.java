/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Matv;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Matv.MATV9In1Model;
import frontline.combat.fcp.entity.vehicle.Matv.MATV9In1Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MATV9In1Renderer
extends VehicleRenderer<MATV9In1Entity> {
    public MATV9In1Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new MATV9In1Model());
    }

    public ResourceLocation getTextureLocation(MATV9In1Entity entity) {
        return entity.getCurrentTexture();
    }
}

