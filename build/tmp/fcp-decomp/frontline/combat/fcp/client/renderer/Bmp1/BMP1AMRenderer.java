/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Bmp1;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Bmp1.BMP1AMModel;
import frontline.combat.fcp.entity.vehicle.Bmp1.BMP1AMEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BMP1AMRenderer
extends VehicleRenderer<BMP1AMEntity> {
    public BMP1AMRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new BMP1AMModel());
    }

    public ResourceLocation getTextureLocation(BMP1AMEntity entity) {
        return entity.getCurrentTexture();
    }
}

