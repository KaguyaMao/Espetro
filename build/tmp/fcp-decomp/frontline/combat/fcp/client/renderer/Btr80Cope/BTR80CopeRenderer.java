/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Btr80Cope;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Btr80Cope.BTR80CopeModel;
import frontline.combat.fcp.entity.vehicle.Btr80Cope.BTR80CopeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BTR80CopeRenderer
extends VehicleRenderer<BTR80CopeEntity> {
    public BTR80CopeRenderer(EntityRendererProvider.Context c) {
        super(c, (GeoModel)new BTR80CopeModel());
    }

    public ResourceLocation getTextureLocation(BTR80CopeEntity e) {
        return e.getCurrentTexture();
    }
}

