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
import tech.vvp.vvp.client.model.CV90Model;
import tech.vvp.vvp.entity.vehicle.CV90Entity;

public class CV90Renderer
extends VehicleRenderer<CV90Entity> {
    public CV90Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new CV90Model());
    }
}

