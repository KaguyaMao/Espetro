/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

@Cancelable
public class RenderBlockScreenEffectEvent
extends Event {
    private final Player player;
    private final PoseStack poseStack;
    private final OverlayType overlayType;
    private final BlockState blockState;
    private final BlockPos blockPos;

    @ApiStatus.Internal
    public RenderBlockScreenEffectEvent(Player player, PoseStack poseStack, OverlayType type, BlockState block, BlockPos blockPos) {
        this.player = player;
        this.poseStack = poseStack;
        this.overlayType = type;
        this.blockState = block;
        this.blockPos = blockPos;
    }

    public Player getPlayer() {
        return this.player;
    }

    public PoseStack getPoseStack() {
        return this.poseStack;
    }

    public OverlayType getOverlayType() {
        return this.overlayType;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public static enum OverlayType {
        FIRE,
        BLOCK,
        WATER;

    }
}

