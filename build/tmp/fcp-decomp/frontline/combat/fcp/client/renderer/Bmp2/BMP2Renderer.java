/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Bmp2;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Bmp2.BMP2Model;
import frontline.combat.fcp.entity.vehicle.Bmp2.BMP2Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BMP2Renderer
extends VehicleRenderer<BMP2Entity> {
    public BMP2Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new BMP2Model());
    }

    public ResourceLocation getTextureLocation(BMP2Entity entity) {
        return entity.getCurrentTexture();
    }
}

