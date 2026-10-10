/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Bmp2Noatgm;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Bmp2Noatgm.BMP2NoAtgmModel;
import frontline.combat.fcp.entity.vehicle.Bmp2Noatgm.BMP2NoAtgmEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BMP2NoAtgmRenderer
extends VehicleRenderer<BMP2NoAtgmEntity> {
    public BMP2NoAtgmRenderer(EntityRendererProvider.Context c) {
        super(c, (GeoModel)new BMP2NoAtgmModel());
    }

    public ResourceLocation getTextureLocation(BMP2NoAtgmEntity e) {
        return e.getCurrentTexture();
    }
}

