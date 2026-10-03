/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Btr3e;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Btr3e.BTR3EModel;
import frontline.combat.fcp.entity.vehicle.Btr3e.BTR3EEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BTR3ERenderer
extends VehicleRenderer<BTR3EEntity> {
    public BTR3ERenderer(EntityRendererProvider.Context c) {
        super(c, (GeoModel)new BTR3EModel());
    }

    public ResourceLocation getTextureLocation(BTR3EEntity e) {
        return e.getCurrentTexture();
    }
}

