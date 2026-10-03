/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Toyota;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Toyota.ToyotaHiluxMortarModel;
import frontline.combat.fcp.entity.vehicle.Toyota.ToyotaHiluxMortarEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ToyotaHiluxMortarRenderer
extends VehicleRenderer<ToyotaHiluxMortarEntity> {
    public ToyotaHiluxMortarRenderer(EntityRendererProvider.Context c) {
        super(c, (GeoModel)new ToyotaHiluxMortarModel());
    }

    public ResourceLocation getTextureLocation(ToyotaHiluxMortarEntity e) {
        return e.getCurrentTexture();
    }
}

