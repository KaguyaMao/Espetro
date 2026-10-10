/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.FogShape
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.FogRenderer$FogMode
 *  net.minecraft.client.renderer.ScreenEffectRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package net.minecraftforge.client.extensions.common;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public interface IClientFluidTypeExtensions {
    public static final IClientFluidTypeExtensions DEFAULT = new IClientFluidTypeExtensions(){};

    public static IClientFluidTypeExtensions of(FluidState state) {
        return IClientFluidTypeExtensions.of(state.getFluidType());
    }

    public static IClientFluidTypeExtensions of(Fluid fluid) {
        return IClientFluidTypeExtensions.of(fluid.getFluidType());
    }

    public static IClientFluidTypeExtensions of(FluidType type) {
        IClientFluidTypeExtensions props;
        Object object = type.getRenderPropertiesInternal();
        return object instanceof IClientFluidTypeExtensions ? (props = (IClientFluidTypeExtensions)object) : DEFAULT;
    }

    default public int getTintColor() {
        return -1;
    }

    default public ResourceLocation getStillTexture() {
        return null;
    }

    default public ResourceLocation getFlowingTexture() {
        return null;
    }

    @Nullable
    default public ResourceLocation getOverlayTexture() {
        return null;
    }

    @Nullable
    default public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
        return null;
    }

    default public void renderOverlay(Minecraft mc, PoseStack poseStack) {
        ResourceLocation texture = this.getRenderOverlayTexture(mc);
        if (texture != null) {
            ScreenEffectRenderer.renderFluid((Minecraft)mc, (PoseStack)poseStack, (ResourceLocation)texture);
        }
    }

    @NotNull
    default public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
        return fluidFogColor;
    }

    default public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
    }

    default public ResourceLocation getStillTexture(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return this.getStillTexture();
    }

    default public ResourceLocation getFlowingTexture(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return this.getFlowingTexture();
    }

    default public ResourceLocation getOverlayTexture(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return this.getOverlayTexture();
    }

    default public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return this.getTintColor();
    }

    default public int getTintColor(FluidStack stack) {
        return this.getTintColor();
    }

    default public ResourceLocation getStillTexture(FluidStack stack) {
        return this.getStillTexture();
    }

    default public ResourceLocation getFlowingTexture(FluidStack stack) {
        return this.getFlowingTexture();
    }

    default public ResourceLocation getOverlayTexture(FluidStack stack) {
        return this.getOverlayTexture();
    }
}

