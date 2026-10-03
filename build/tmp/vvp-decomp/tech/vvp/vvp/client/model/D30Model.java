/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.core.animatable.model.CoreGeoBone
 *  software.bernie.geckolib.core.animation.AnimationState
 */
package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import tech.vvp.vvp.client.model.VvpVehicleModel;
import tech.vvp.vvp.entity.vehicle.D30Entity;

public class D30Model
extends VvpVehicleModel<D30Entity> {
    private int lastRenderedEntityId = Integer.MIN_VALUE;
    private float vertelYawRotation = 0.0f;
    private float vertelPitchRotation = 0.0f;
    private float prevTurretYaw = 0.0f;
    private float prevTurretPitch = 0.0f;

    public ResourceLocation getModelResource(D30Entity entity) {
        return new ResourceLocation("vvp", "geo/d30.geo.json");
    }

    public ResourceLocation getTextureResource(D30Entity entity) {
        return new ResourceLocation("vvp", "textures/entity/d30.png");
    }

    public ResourceLocation getAnimationResource(D30Entity entity) {
        return new ResourceLocation("vvp", "animations/d30.animation.json");
    }

    @Override
    public void setCustomAnimations(D30Entity vehicle, long instanceId, AnimationState<D30Entity> animationState) {
        CoreGeoBone bonePitch;
        float deltaYaw;
        super.setCustomAnimations(vehicle, instanceId, animationState);
        int entityId = vehicle.m_19879_();
        if (entityId != this.lastRenderedEntityId) {
            this.lastRenderedEntityId = entityId;
            this.vertelYawRotation = 0.0f;
            this.vertelPitchRotation = 0.0f;
            this.prevTurretYaw = vehicle.getTurretYRot();
            this.prevTurretPitch = vehicle.getTurretXRot();
        }
        float currentYaw = vehicle.getTurretYRot();
        float currentPitch = vehicle.getTurretXRot();
        for (deltaYaw = currentYaw - this.prevTurretYaw; deltaYaw > 180.0f; deltaYaw -= 360.0f) {
        }
        while (deltaYaw < -180.0f) {
            deltaYaw += 360.0f;
        }
        this.vertelYawRotation += deltaYaw * 0.5f;
        this.prevTurretYaw = currentYaw;
        float deltaPitch = currentPitch - this.prevTurretPitch;
        this.vertelPitchRotation += deltaPitch * 0.5f;
        this.prevTurretPitch = currentPitch;
        CoreGeoBone boneYaw = this.getAnimationProcessor().getBone("vertelkanekrutoi");
        if (boneYaw != null) {
            boneYaw.setRotZ(this.vertelYawRotation * ((float)Math.PI / 180));
        }
        if ((bonePitch = this.getAnimationProcessor().getBone("vertelkakrytai")) != null) {
            bonePitch.setRotX(this.vertelPitchRotation * ((float)Math.PI / 180));
        }
    }
}

