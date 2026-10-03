/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Btr82;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Btr82.BTR82Model;
import frontline.combat.fcp.entity.vehicle.Btr82.BTR82Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BTR82Renderer
extends VehicleRenderer<BTR82Entity> {
    public BTR82Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new BTR82Model());
    }

    public ResourceLocation getTextureLocation(BTR82Entity entity) {
        return entity.getCurrentTexture();
    }
}

