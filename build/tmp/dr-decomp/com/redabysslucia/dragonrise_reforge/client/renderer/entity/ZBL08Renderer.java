/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModelInstance
 *  com.atsuishio.superbwarfare.client.renderer.entity.GeoVehicleRenderer
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BoneState
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 */
package com.redabysslucia.dragonrise_reforge.client.renderer.entity;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModelInstance;
import com.atsuishio.superbwarfare.client.renderer.entity.GeoVehicleRenderer;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BoneState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redabysslucia.dragonrise_reforge.entities.ZBL08Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ZBL08Renderer
extends GeoVehicleRenderer<ZBL08Entity> {
    public ZBL08Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    public void transformCustomModelPart(ZBL08Entity entity, VehicleModelInstance instance, PoseStack poseStack, float entityYaw, float partialTicks) {
        BoneState missile4;
        BoneState missile3;
        super.transformCustomModelPart((VehicleEntity)entity, instance, poseStack, entityYaw, partialTicks);
        boolean show3 = false;
        boolean show4 = false;
        GunData gunData = entity.getGunData("Missile");
        if (gunData != null) {
            int ammo = gunData.ammo.get();
            show3 = ammo >= 1;
            boolean bl = show4 = ammo >= 2;
        }
        if ((missile3 = instance.getBone("move_missile3")) != null) {
            missile3.visible = show3;
        }
        if ((missile4 = instance.getBone("move_missile4")) != null) {
            missile4.visible = show4;
        }
    }
}

