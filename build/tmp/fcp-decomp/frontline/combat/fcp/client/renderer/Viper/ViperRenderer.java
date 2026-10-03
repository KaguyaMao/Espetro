/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Viper;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Viper.ViperModel;
import frontline.combat.fcp.entity.vehicle.Viper.ViperEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ViperRenderer
extends VehicleRenderer<ViperEntity> {
    public ViperRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new ViperModel());
    }

    public ResourceLocation getTextureLocation(ViperEntity entity) {
        return entity.getCurrentTexture();
    }
}

