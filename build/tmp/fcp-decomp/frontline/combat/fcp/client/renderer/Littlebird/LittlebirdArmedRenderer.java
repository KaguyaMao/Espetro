/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Littlebird;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Littlebird.LittlebirdArmedModel;
import frontline.combat.fcp.entity.vehicle.Littlebird.LittlebirdArmedEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LittlebirdArmedRenderer
extends VehicleRenderer<LittlebirdArmedEntity> {
    public LittlebirdArmedRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new LittlebirdArmedModel());
    }

    public ResourceLocation getTextureLocation(LittlebirdArmedEntity entity) {
        return entity.getCurrentTexture();
    }
}

