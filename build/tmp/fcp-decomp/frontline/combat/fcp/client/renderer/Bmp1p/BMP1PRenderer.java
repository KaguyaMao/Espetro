/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Bmp1p;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Bmp1p.BMP1PModel;
import frontline.combat.fcp.entity.vehicle.Bmp1p.BMP1PEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BMP1PRenderer
extends VehicleRenderer<BMP1PEntity> {
    public BMP1PRenderer(EntityRendererProvider.Context c) {
        super(c, (GeoModel)new BMP1PModel());
    }

    public ResourceLocation getTextureLocation(BMP1PEntity e) {
        return e.getCurrentTexture();
    }
}

