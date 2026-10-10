/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  oshi.util.tuples.Pair
 *  software.bernie.geckolib.core.animatable.model.CoreGeoBone
 *  software.bernie.geckolib.core.animation.AnimationState
 */
package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import java.util.List;
import oshi.util.tuples.Pair;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import tech.vvp.vvp.client.model.util.ModelBoneTransforms;

public abstract class VvpVehicleModel<T extends VehicleEntity>
extends VehicleModel<T> {
    private int lastRenderedEntityId = Integer.MIN_VALUE;

    public void setCustomAnimations(T vehicle, long instanceId, AnimationState<T> animationState) {
        int entityId = vehicle.m_19879_();
        if (entityId != this.lastRenderedEntityId) {
            this.lastRenderedEntityId = entityId;
            this.resetSharedTransformBones();
        }
        super.setCustomAnimations(vehicle, instanceId, animationState);
    }

    private void resetSharedTransformBones() {
        List transforms = this.getTRANSFORMS();
        if (transforms.isEmpty()) {
            return;
        }
        for (Pair pair : transforms) {
            CoreGeoBone bone = this.getAnimationProcessor().getBone((String)pair.getA());
            if (bone == null) continue;
            ModelBoneTransforms.resetForVehicleRender(bone);
        }
    }
}

