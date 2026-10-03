/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.T72av;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.T72av.T72AVModel;
import frontline.combat.fcp.entity.vehicle.T72av.T72AVEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class T72AVRenderer
extends VehicleRenderer<T72AVEntity> {
    public T72AVRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new T72AVModel());
    }

    public ResourceLocation getTextureLocation(T72AVEntity entity) {
        return entity.getCurrentTexture();
    }
}

