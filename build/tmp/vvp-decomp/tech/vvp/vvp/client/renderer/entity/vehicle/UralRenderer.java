/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  software.bernie.geckolib.model.GeoModel
 */
package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.client.model.UralModel;
import tech.vvp.vvp.entity.vehicle.UralEntity;

public class UralRenderer
extends VehicleRenderer<UralEntity> {
    public UralRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new UralModel());
    }
}

