/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Btr80;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Btr80.BTR80Model;
import frontline.combat.fcp.entity.vehicle.Btr80.BTR80Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BTR80Renderer
extends VehicleRenderer<BTR80Entity> {
    public BTR80Renderer(EntityRendererProvider.Context c) {
        super(c, (GeoModel)new BTR80Model());
    }

    public ResourceLocation getTextureLocation(BTR80Entity e) {
        return e.getCurrentTexture();
    }
}

