/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Camera
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

@Cancelable
public abstract class RenderHighlightEvent
extends Event {
    private final LevelRenderer levelRenderer;
    private final Camera camera;
    private final HitResult target;
    private final float partialTick;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;

    @ApiStatus.Internal
    protected RenderHighlightEvent(LevelRenderer levelRenderer, Camera camera, HitResult target, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource) {
        this.levelRenderer = levelRenderer;
        this.camera = camera;
        this.target = target;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
    }

    public LevelRenderer getLevelRenderer() {
        return this.levelRenderer;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public HitResult getTarget() {
        return this.target;
    }

    public float getPartialTick() {
        return this.partialTick;
    }

    public PoseStack getPoseStack() {
        return this.poseStack;
    }

    public MultiBufferSource getMultiBufferSource() {
        return this.multiBufferSource;
    }

    public static class Entity
    extends RenderHighlightEvent {
        @ApiStatus.Internal
        public Entity(LevelRenderer levelRenderer, Camera camera, EntityHitResult target, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
            super(levelRenderer, camera, (HitResult)target, partialTick, poseStack, bufferSource);
        }

        public EntityHitResult getTarget() {
            return (EntityHitResult)this.target;
        }
    }

    @Cancelable
    public static class Block
    extends RenderHighlightEvent {
        @ApiStatus.Internal
        public Block(LevelRenderer levelRenderer, Camera camera, BlockHitResult target, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
            super(levelRenderer, camera, (HitResult)target, partialTick, poseStack, bufferSource);
        }

        public BlockHitResult getTarget() {
            return (BlockHitResult)this.target;
        }
    }
}

