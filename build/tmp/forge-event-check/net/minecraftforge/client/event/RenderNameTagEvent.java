/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.eventbus.api.Event$HasResult
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

@Event.HasResult
public class RenderNameTagEvent
extends EntityEvent {
    private Component nameplateContent;
    private final Component originalContent;
    private final EntityRenderer<?> entityRenderer;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;
    private final float partialTick;

    @ApiStatus.Internal
    public RenderNameTagEvent(Entity entity, Component content, EntityRenderer<?> entityRenderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, float partialTick) {
        super(entity);
        this.originalContent = content;
        this.setContent(this.originalContent);
        this.entityRenderer = entityRenderer;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
        this.partialTick = partialTick;
    }

    public void setContent(Component contents) {
        this.nameplateContent = contents;
    }

    public Component getContent() {
        return this.nameplateContent;
    }

    public Component getOriginalContent() {
        return this.originalContent;
    }

    public EntityRenderer<?> getEntityRenderer() {
        return this.entityRenderer;
    }

    public PoseStack getPoseStack() {
        return this.poseStack;
    }

    public MultiBufferSource getMultiBufferSource() {
        return this.multiBufferSource;
    }

    public int getPackedLight() {
        return this.packedLight;
    }

    public float getPartialTick() {
        return this.partialTick;
    }
}

