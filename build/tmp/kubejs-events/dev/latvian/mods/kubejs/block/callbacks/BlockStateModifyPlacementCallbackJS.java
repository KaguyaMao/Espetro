/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.block.callbacks;

import dev.latvian.mods.kubejs.block.callbacks.BlockStateModifyCallbackJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BlockStateModifyPlacementCallbackJS
extends BlockStateModifyCallbackJS {
    public final BlockPlaceContext context;
    public final Block minecraftBlock;
    public BlockContainerJS block;

    public BlockStateModifyPlacementCallbackJS(BlockPlaceContext context, Block block) {
        super(BlockStateModifyPlacementCallbackJS.getBlockStateToModify(context, block));
        this.context = context;
        this.minecraftBlock = block;
        this.block = new BlockContainerJS(context.m_43725_(), context.m_8083_());
    }

    private static BlockState getBlockStateToModify(BlockPlaceContext context, Block block) {
        BlockState previous = context.m_43725_().m_8055_(context.m_8083_());
        if (previous.m_60734_() == block) {
            return previous;
        }
        return block.m_49966_();
    }

    @Info(value="Gets the clicked position in world")
    public BlockPos getClickedPos() {
        return this.context.m_8083_();
    }

    @Info(value="Gets the clicked block")
    public BlockContainerJS getClickedBlock() {
        return new BlockContainerJS(this.getLevel(), this.getClickedPos());
    }

    @Info(value="Returns if the block being placed thinks it can be placed here. This is used for replacement checks, like placing blocks in water or tall grass")
    public boolean canPlace() {
        return this.context.m_7059_();
    }

    @Info(value="Returns if the block being placed is replacing the block clicked")
    public boolean replacingClickedOnBlock() {
        return this.context.m_7058_();
    }

    @Info(value="Gets the direction closes to where the player is currently looking")
    public Direction getNearestLookingDirection() {
        return this.context.m_7820_();
    }

    @Info(value="Gets the vertical direction (UP/DOWN) closest to where the player is currently looking")
    public Direction getNearestLookingVerticalDirection() {
        return this.context.m_151260_();
    }

    @Info(value="Gets an array of all directions, ordered by which the player is looking closest to")
    public Direction[] getNearestLookingDirections() {
        return this.context.m_6232_();
    }

    @Info(value="Gets the facing direction of the clicked block face")
    public Direction getClickedFace() {
        return this.context.m_43719_();
    }

    @Info(value="Gets the position in the block-space of where it was clicked")
    public Vec3 getClickLocation() {
        return this.context.m_43720_();
    }

    @Info(value="Returns if the hit posiiton in the block-space is inside the 1x1x1 cube of the block")
    public boolean isInside() {
        return this.context.m_43721_();
    }

    @Info(value="Gets the item being placed")
    public ItemStack getItem() {
        return this.context.m_43722_();
    }

    @Info(value="Gets the player placing the block, if available")
    @Nullable
    public Player getPlayer() {
        return this.context.m_43723_();
    }

    @Info(value="Gets the hand that is placing the block")
    public InteractionHand getHand() {
        return this.context.m_43724_();
    }

    @Info(value="Gets the level")
    public Level getLevel() {
        return this.context.m_43725_();
    }

    @Info(value="Gets the nearest horizontal direction to where the player is looking. NORTH if there is no player")
    public Direction getHorizontalDirection() {
        return this.context.m_8125_();
    }

    @Info(value="Returns if the player is using the 'secondary' function of this item. Basically checks if they are holding shift")
    public boolean isSecondaryUseActive() {
        return this.context.m_7078_();
    }

    @Info(value="Get the horizontal rotation of the player")
    public float getRotation() {
        return this.context.m_7074_();
    }

    @Info(value="Gets the FluidSate at the clicked position")
    public FluidState getFluidStateAtClickedPos() {
        return this.context.m_43725_().m_6425_(this.context.m_8083_());
    }

    @Info(value="Checks if the position clicked has a specified fluid there")
    public boolean isClickedPosIn(Fluid fluid) {
        return this.getFluidStateAtClickedPos().m_192917_(fluid);
    }

    @Info(value="Set if this block is waterlogged or not")
    public BlockStateModifyPlacementCallbackJS waterlogged(boolean waterlogged) {
        this.setValue(BlockStateProperties.f_61362_, waterlogged);
        return this;
    }

    @Info(value="Set this block as waterlogged if it is in water")
    public BlockStateModifyPlacementCallbackJS waterlogged() {
        return this.waterlogged(this.isInWater());
    }

    @Info(value="Checks if this block is in water")
    public boolean isInWater() {
        return this.getFluidStateAtClickedPos().m_76152_() == Fluids.f_76193_;
    }

    @Info(value="Checks if the block currently occupying the position this is being placed in is the same block type.\nUsed for things like candles, where multiple can be in the same block-space.\n")
    public boolean isReplacingSelf() {
        return this.getLevel().m_8055_(this.getClickedPos()).m_60734_() == this.minecraftBlock;
    }
}

