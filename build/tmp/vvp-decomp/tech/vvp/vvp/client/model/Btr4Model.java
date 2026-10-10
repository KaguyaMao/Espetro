/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 */
package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import java.util.regex.Pattern;
import tech.vvp.vvp.client.model.VvpVehicleModel;
import tech.vvp.vvp.entity.vehicle.Btr4Entity;

public class Btr4Model
extends VvpVehicleModel<Btr4Entity> {
    private static final Pattern WHEEL_PATTERN = Pattern.compile("^wheel[LR].*$");

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    public VehicleModel.TransformContext<Btr4Entity> collectTransform(String boneName) {
        VehicleModel.TransformContext base = super.collectTransform(boneName);
        if (base != null && WHEEL_PATTERN.matcher(boneName).matches() && !boneName.endsWith("Turn")) {
            return (bone, vehicle, animationState) -> {
                base.transform(bone, vehicle, animationState);
                bone.setRotX(-bone.getRotX());
            };
        }
        return base;
    }
}

