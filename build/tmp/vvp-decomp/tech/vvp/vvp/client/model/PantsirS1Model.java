/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.entity.Entity
 */
package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import tech.vvp.vvp.client.PantsirClientHandler;
import tech.vvp.vvp.client.model.VvpVehicleModel;
import tech.vvp.vvp.entity.vehicle.PantsirS1Entity;

public class PantsirS1Model
extends VvpVehicleModel<PantsirS1Entity> {
    private long lastUpdateTime = 0L;
    private float localRadarAngle = 0.0f;
    private static final float RADAR_ROTATION_SPEED = 0.16941176f;

    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    public VehicleModel.TransformContext<PantsirS1Entity> collectTransform(String boneName) {
        if (boneName.equals("RADAR")) {
            return (bone, vehicle, state) -> {
                Entity playerVehicle;
                Minecraft mc = Minecraft.m_91087_();
                LocalPlayer player = mc.f_91074_;
                float angle = player != null ? ((playerVehicle = player.m_20202_()) == vehicle ? PantsirClientHandler.getInterpolatedRadarAngle(vehicle.m_19879_()) : this.getLocalRadarAngle()) : this.getLocalRadarAngle();
                bone.setRotY(angle * ((float)Math.PI / 180));
            };
        }
        return super.collectTransform(boneName);
    }

    private float getLocalRadarAngle() {
        long currentTime = System.currentTimeMillis();
        if (this.lastUpdateTime == 0L) {
            this.lastUpdateTime = currentTime;
            return this.localRadarAngle;
        }
        float deltaTime = currentTime - this.lastUpdateTime;
        this.lastUpdateTime = currentTime;
        deltaTime = Math.min(deltaTime, 50.0f);
        this.localRadarAngle -= 0.16941176f * deltaTime;
        while (this.localRadarAngle < -360.0f) {
            this.localRadarAngle += 360.0f;
        }
        while (this.localRadarAngle > 0.0f) {
            this.localRadarAngle -= 360.0f;
        }
        return this.localRadarAngle;
    }
}

