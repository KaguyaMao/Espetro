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
import tech.vvp.vvp.client.model.M1A2HVModel;
import tech.vvp.vvp.entity.vehicle.M1A2HVEntity;

public class M1A2HVRenderer
extends VehicleRenderer<M1A2HVEntity> {
    public M1A2HVRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new M1A2HVModel());
    }
}

