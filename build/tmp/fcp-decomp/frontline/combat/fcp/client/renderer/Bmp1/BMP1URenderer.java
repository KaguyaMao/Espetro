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
import frontline.combat.fcp.client.model.Bmp1.BMP1UModel;
import frontline.combat.fcp.entity.vehicle.Bmp1.BMP1UEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BMP1URenderer
extends VehicleRenderer<BMP1UEntity> {
    public BMP1URenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new BMP1UModel());
    }

    public ResourceLocation getTextureLocation(BMP1UEntity entity) {
        return entity.getCurrentTexture();
    }
}

