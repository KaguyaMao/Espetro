/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.model.GeoModel
 */
package frontline.combat.fcp.client.renderer.Kamaz;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import frontline.combat.fcp.client.model.Kamaz.KamazModel;
import frontline.combat.fcp.entity.vehicle.Kamaz.KamazEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KamazRenderer
extends VehicleRenderer<KamazEntity> {
    public KamazRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new KamazModel());
    }

    public ResourceLocation getTextureLocation(KamazEntity entity) {
        return entity.getCurrentTexture();
    }
}

