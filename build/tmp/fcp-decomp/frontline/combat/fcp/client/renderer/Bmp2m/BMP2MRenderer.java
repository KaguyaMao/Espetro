/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Bmp2m;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Bmp2m.BMP2MModel;
import frontline.combat.fcp.entity.vehicle.Bmp2m.BMP2MEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BMP2MRenderer
extends VehicleRenderer<BMP2MEntity> {
    public BMP2MRenderer(EntityRendererProvider.Context c) {
        super(c, (GeoModel)new BMP2MModel());
    }

    public ResourceLocation getTextureLocation(BMP2MEntity e) {
        return e.getCurrentTexture();
    }
}

