/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModelInstance
 *  com.atsuishio.superbwarfare.client.renderer.entity.GeoVehicleRenderer
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BoneState
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.util.Mth
 */
package com.redabysslucia.dragonrise_reforge.client.renderer.entity;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModelInstance;
import com.atsuishio.superbwarfare.client.renderer.entity.GeoVehicleRenderer;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BoneState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;

public class RotorVehicleRenderer<T extends VehicleEntity>
extends GeoVehicleRenderer<T> {
    public RotorVehicleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    public void transformCustomModelPart(T entity, VehicleModelInstance instance, PoseStack poseStack, float entityYaw, float partialTicks) {
        BoneState tail;
        super.transformCustomModelPart(entity, instance, poseStack, entityYaw, partialTicks);
        if (entity.isWreck()) {
            return;
        }
        float rot = Mth.m_14179_((float)partialTicks, (float)entity.getPropellerRotO(), (float)entity.getPropellerRot());
        BoneState p0 = instance.getBone("move_propeller0");
        BoneState p1 = instance.getBone("move_propeller1");
        if (p0 != null && p1 != null) {
            p0.rotation.rotateY(-rot);
            p1.rotation.rotateY(rot);
            return;
        }
        if (instance.getBone("move_propeller3") != null) {
            RotorVehicleRenderer.rotateZ(instance, "move_propeller1", rot);
            RotorVehicleRenderer.rotateZ(instance, "move_propeller2", rot);
            RotorVehicleRenderer.rotateZ(instance, "move_propeller3", rot);
            RotorVehicleRenderer.rotateZ(instance, "move_propeller4", rot);
            return;
        }
        BoneState propeller = instance.getBone("move_propeller");
        if (propeller != null) {
            propeller.rotation.rotateY(-rot);
        }
        if ((tail = instance.getBone("move_tailPropeller")) == null) {
            tail = instance.getBone("move_tailpropeller");
        }
        if (tail != null) {
            tail.rotation.rotateX(6.0f * rot);
        } else {
            BoneState p2 = instance.getBone("move_propeller2");
            if (p2 != null) {
                p2.rotation.rotateY(rot);
            }
        }
    }

    private static void rotateZ(VehicleModelInstance instance, String boneName, float angle) {
        BoneState bone = instance.getBone(boneName);
        if (bone != null) {
            bone.rotation.rotateZ(angle);
        }
    }
}

