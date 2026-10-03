/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.JohnDeere;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.JohnDeere.JohnDeereModel;
import frontline.combat.fcp.entity.vehicle.JohnDeere.JohnDeereEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class JohnDeereRenderer
extends VehicleRenderer<JohnDeereEntity> {
    public JohnDeereRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new JohnDeereModel());
    }

    public ResourceLocation getTextureLocation(JohnDeereEntity entity) {
        return entity.getCurrentTexture();
    }
}

