/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.FogShape
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.FogRenderer$FogMode
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.world.level.material.FogType
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

public abstract class ViewportEvent
extends Event {
    private final GameRenderer renderer;
    private final Camera camera;
    private final double partialTick;

    @ApiStatus.Internal
    public ViewportEvent(GameRenderer renderer, Camera camera, double partialTick) {
        this.renderer = renderer;
        this.camera = camera;
        this.partialTick = partialTick;
    }

    public GameRenderer getRenderer() {
        return this.renderer;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public double getPartialTick() {
        return this.partialTick;
    }

    public static class ComputeFov
    extends ViewportEvent {
        private final boolean usedConfiguredFov;
        private double fov;

        @ApiStatus.Internal
        public ComputeFov(GameRenderer renderer, Camera camera, double renderPartialTicks, double fov, boolean usedConfiguredFov) {
            super(renderer, camera, renderPartialTicks);
            this.usedConfiguredFov = usedConfiguredFov;
            this.setFOV(fov);
        }

        public double getFOV() {
            return this.fov;
        }

        public void setFOV(double fov) {
            this.fov = fov;
        }

        public boolean usedConfiguredFov() {
            return this.usedConfiguredFov;
        }
    }

    public static class ComputeCameraAngles
    extends ViewportEvent {
        private float yaw;
        private float pitch;
        private float roll;

        @ApiStatus.Internal
        public ComputeCameraAngles(GameRenderer renderer, Camera camera, double renderPartialTicks, float yaw, float pitch, float roll) {
            super(renderer, camera, renderPartialTicks);
            this.setYaw(yaw);
            this.setPitch(pitch);
            this.setRoll(roll);
        }

        public float getYaw() {
            return this.yaw;
        }

        public void setYaw(float yaw) {
            this.yaw = yaw;
        }

        public float getPitch() {
            return this.pitch;
        }

        public void setPitch(float pitch) {
            this.pitch = pitch;
        }

        public float getRoll() {
            return this.roll;
        }

        public void setRoll(float roll) {
            this.roll = roll;
        }
    }

    public static class ComputeFogColor
    extends ViewportEvent {
        private float red;
        private float green;
        private float blue;

        @ApiStatus.Internal
        public ComputeFogColor(Camera camera, float partialTicks, float red, float green, float blue) {
            super(Minecraft.m_91087_().f_91063_, camera, partialTicks);
            this.setRed(red);
            this.setGreen(green);
            this.setBlue(blue);
        }

        public float getRed() {
            return this.red;
        }

        public void setRed(float red) {
            this.red = red;
        }

        public float getGreen() {
            return this.green;
        }

        public void setGreen(float green) {
            this.green = green;
        }

        public float getBlue() {
            return this.blue;
        }

        public void setBlue(float blue) {
            this.blue = blue;
        }
    }

    @Cancelable
    public static class RenderFog
    extends ViewportEvent {
        private final FogRenderer.FogMode mode;
        private final FogType type;
        private float farPlaneDistance;
        private float nearPlaneDistance;
        private FogShape fogShape;

        @ApiStatus.Internal
        public RenderFog(FogRenderer.FogMode mode, FogType type, Camera camera, float partialTicks, float nearPlaneDistance, float farPlaneDistance, FogShape fogShape) {
            super(Minecraft.m_91087_().f_91063_, camera, partialTicks);
            this.mode = mode;
            this.type = type;
            this.setFarPlaneDistance(farPlaneDistance);
            this.setNearPlaneDistance(nearPlaneDistance);
            this.setFogShape(fogShape);
        }

        public FogRenderer.FogMode getMode() {
            return this.mode;
        }

        public FogType getType() {
            return this.type;
        }

        public float getFarPlaneDistance() {
            return this.farPlaneDistance;
        }

        public float getNearPlaneDistance() {
            return this.nearPlaneDistance;
        }

        public FogShape getFogShape() {
            return this.fogShape;
        }

        public void setFarPlaneDistance(float distance) {
            this.farPlaneDistance = distance;
        }

        public void setNearPlaneDistance(float distance) {
            this.nearPlaneDistance = distance;
        }

        public void setFogShape(FogShape shape) {
            this.fogShape = shape;
        }

        public void scaleFarPlaneDistance(float factor) {
            this.farPlaneDistance *= factor;
        }

        public void scaleNearPlaneDistance(float factor) {
            this.nearPlaneDistance *= factor;
        }
    }
}

