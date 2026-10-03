/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Camera
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.DimensionSpecialEffects
 *  net.minecraft.client.renderer.LightTexture
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 */
package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface IForgeDimensionSpecialEffects {
    private DimensionSpecialEffects self() {
        return (DimensionSpecialEffects)this;
    }

    default public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
        return false;
    }

    default public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        return false;
    }

    default public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
        return false;
    }

    default public boolean tickRain(ClientLevel level, int ticks, Camera camera) {
        return false;
    }

    default public void adjustLightmapColors(ClientLevel level, float partialTicks, float skyDarken, float blockLightRedFlicker, float skyLight, int pixelX, int pixelY, Vector3f colors) {
    }
}

