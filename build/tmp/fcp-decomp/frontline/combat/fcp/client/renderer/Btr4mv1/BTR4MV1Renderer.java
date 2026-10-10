/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Btr4mv1;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Btr4mv1.BTR4MV1Model;
import frontline.combat.fcp.entity.vehicle.Btr4mv1.BTR4MV1Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BTR4MV1Renderer
extends VehicleRenderer<BTR4MV1Entity> {
    public BTR4MV1Renderer(EntityRendererProvider.Context c) {
        super(c, (GeoModel)new BTR4MV1Model());
    }

    public ResourceLocation getTextureLocation(BTR4MV1Entity e) {
        return e.getCurrentTexture();
    }
}

