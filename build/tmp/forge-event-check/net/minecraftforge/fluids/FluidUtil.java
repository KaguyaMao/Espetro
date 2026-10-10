/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.BucketPickup
 *  net.minecraft.world.level.block.LiquidBlockContainer
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.fluids;

import com.google.common.base.Preconditions;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper;
import net.minecraftforge.fluids.capability.wrappers.BucketPickupHandlerWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidUtil {
    private FluidUtil() {
    }

    public static boolean interactWithFluidHandler(@NotNull Player player, @NotNull InteractionHand hand, @NotNull Level level, @NotNull BlockPos pos, @Nullable Direction side) {
        Preconditions.checkNotNull((Object)level);
        Preconditions.checkNotNull((Object)pos);
        return FluidUtil.getFluidHandler(level, pos, side).map(handler -> FluidUtil.interactWithFluidHandler(player, hand, handler)).orElse(false);
    }

    public static boolean interactWithFluidHandler(@NotNull Player player, @NotNull InteractionHand hand, @NotNull IFluidHandler handler) {
        Preconditions.checkNotNull((Object)player);
        Preconditions.checkNotNull((Object)hand);
        Preconditions.checkNotNull((Object)handler);
        ItemStack heldItem = player.m_21120_(hand);
        if (!heldItem.m_41619_()) {
            return player.getCapability(ForgeCapabilities.ITEM_HANDLER).map(playerInventory -> {
                FluidActionResult fluidActionResult = FluidUtil.tryFillContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
                if (!fluidActionResult.isSuccess()) {
                    fluidActionResult = FluidUtil.tryEmptyContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
                }
                if (fluidActionResult.isSuccess()) {
                    player.m_21008_(hand, fluidActionResult.getResult());
                    return true;
                }
                return false;
            }).orElse(false);
        }
        return false;
    }

    @NotNull
    public static FluidActionResult tryFillContainer(@NotNull ItemStack container, IFluidHandler fluidSource, int maxAmount, @Nullable Player player, boolean doFill) {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1);
        return FluidUtil.getFluidHandler(containerCopy).map(containerFluidHandler -> {
            FluidStack simulatedTransfer = FluidUtil.tryFluidTransfer((IFluidHandler)containerFluidHandler, fluidSource, maxAmount, false);
            if (!simulatedTransfer.isEmpty()) {
                if (doFill) {
                    SoundEvent soundevent;
                    FluidUtil.tryFluidTransfer((IFluidHandler)containerFluidHandler, fluidSource, maxAmount, true);
                    if (player != null && (soundevent = simulatedTransfer.getFluid().getFluidType().getSound(simulatedTransfer, SoundActions.BUCKET_FILL)) != null) {
                        player.m_9236_().m_6263_(null, player.m_20185_(), player.m_20186_() + 0.5, player.m_20189_(), soundevent, SoundSource.BLOCKS, 1.0f, 1.0f);
                    }
                } else {
                    containerFluidHandler.fill(simulatedTransfer, IFluidHandler.FluidAction.EXECUTE);
                }
                ItemStack resultContainer = containerFluidHandler.getContainer();
                return new FluidActionResult(resultContainer);
            }
            return FluidActionResult.FAILURE;
        }).orElse(FluidActionResult.FAILURE);
    }

    @NotNull
    public static FluidActionResult tryEmptyContainer(@NotNull ItemStack container, IFluidHandler fluidDestination, int maxAmount, @Nullable Player player, boolean doDrain) {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1);
        return FluidUtil.getFluidHandler(containerCopy).map(containerFluidHandler -> {
            SoundEvent soundevent;
            FluidStack transfer = FluidUtil.tryFluidTransfer(fluidDestination, (IFluidHandler)containerFluidHandler, maxAmount, doDrain);
            if (transfer.isEmpty()) {
                return FluidActionResult.FAILURE;
            }
            if (!doDrain) {
                containerFluidHandler.drain(transfer, IFluidHandler.FluidAction.EXECUTE);
            }
            if (doDrain && player != null && (soundevent = transfer.getFluid().getFluidType().getSound(transfer, SoundActions.BUCKET_EMPTY)) != null) {
                player.m_9236_().m_6263_(null, player.m_20185_(), player.m_20186_() + 0.5, player.m_20189_(), soundevent, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            ItemStack resultContainer = containerFluidHandler.getContainer();
            return new FluidActionResult(resultContainer);
        }).orElse(FluidActionResult.FAILURE);
    }

    @NotNull
    public static FluidActionResult tryFillContainerAndStow(@NotNull ItemStack container, IFluidHandler fluidSource, IItemHandler inventory, int maxAmount, @Nullable Player player, boolean doFill) {
        if (container.m_41619_()) {
            return FluidActionResult.FAILURE;
        }
        if (player != null && player.m_150110_().f_35937_) {
            FluidActionResult filledReal = FluidUtil.tryFillContainer(container, fluidSource, maxAmount, player, doFill);
            if (filledReal.isSuccess()) {
                return new FluidActionResult(container);
            }
        } else if (container.m_41613_() == 1) {
            FluidActionResult filledReal = FluidUtil.tryFillContainer(container, fluidSource, maxAmount, player, doFill);
            if (filledReal.isSuccess()) {
                return filledReal;
            }
        } else {
            ItemStack remainder;
            FluidActionResult filledSimulated = FluidUtil.tryFillContainer(container, fluidSource, maxAmount, player, false);
            if (filledSimulated.isSuccess() && ((remainder = ItemHandlerHelper.insertItemStacked(inventory, filledSimulated.getResult(), true)).m_41619_() || player != null)) {
                FluidActionResult filledReal = FluidUtil.tryFillContainer(container, fluidSource, maxAmount, player, doFill);
                remainder = ItemHandlerHelper.insertItemStacked(inventory, filledReal.getResult(), !doFill);
                if (!remainder.m_41619_() && player != null && doFill) {
                    ItemHandlerHelper.giveItemToPlayer(player, remainder);
                }
                ItemStack containerCopy = container.m_41777_();
                containerCopy.m_41774_(1);
                return new FluidActionResult(containerCopy);
            }
        }
        return FluidActionResult.FAILURE;
    }

    @NotNull
    public static FluidActionResult tryEmptyContainerAndStow(@NotNull ItemStack container, IFluidHandler fluidDestination, IItemHandler inventory, int maxAmount, @Nullable Player player, boolean doDrain) {
        if (container.m_41619_()) {
            return FluidActionResult.FAILURE;
        }
        if (player != null && player.m_150110_().f_35937_) {
            FluidActionResult emptiedReal = FluidUtil.tryEmptyContainer(container, fluidDestination, maxAmount, player, doDrain);
            if (emptiedReal.isSuccess()) {
                return new FluidActionResult(container);
            }
        } else if (container.m_41613_() == 1) {
            FluidActionResult emptiedReal = FluidUtil.tryEmptyContainer(container, fluidDestination, maxAmount, player, doDrain);
            if (emptiedReal.isSuccess()) {
                return emptiedReal;
            }
        } else {
            ItemStack remainder;
            FluidActionResult emptiedSimulated = FluidUtil.tryEmptyContainer(container, fluidDestination, maxAmount, player, false);
            if (emptiedSimulated.isSuccess() && ((remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedSimulated.getResult(), true)).m_41619_() || player != null)) {
                FluidActionResult emptiedReal = FluidUtil.tryEmptyContainer(container, fluidDestination, maxAmount, player, doDrain);
                remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedReal.getResult(), !doDrain);
                if (!remainder.m_41619_() && player != null && doDrain) {
                    ItemHandlerHelper.giveItemToPlayer(player, remainder);
                }
                ItemStack containerCopy = container.m_41777_();
                containerCopy.m_41774_(1);
                return new FluidActionResult(containerCopy);
            }
        }
        return FluidActionResult.FAILURE;
    }

    @NotNull
    public static FluidStack tryFluidTransfer(IFluidHandler fluidDestination, IFluidHandler fluidSource, int maxAmount, boolean doTransfer) {
        FluidStack drainable = fluidSource.drain(maxAmount, IFluidHandler.FluidAction.SIMULATE);
        if (!drainable.isEmpty()) {
            return FluidUtil.tryFluidTransfer_Internal(fluidDestination, fluidSource, drainable, doTransfer);
        }
        return FluidStack.EMPTY;
    }

    @NotNull
    public static FluidStack tryFluidTransfer(IFluidHandler fluidDestination, IFluidHandler fluidSource, FluidStack resource, boolean doTransfer) {
        FluidStack drainable = fluidSource.drain(resource, IFluidHandler.FluidAction.SIMULATE);
        if (!drainable.isEmpty() && resource.isFluidEqual(drainable)) {
            return FluidUtil.tryFluidTransfer_Internal(fluidDestination, fluidSource, drainable, doTransfer);
        }
        return FluidStack.EMPTY;
    }

    @NotNull
    private static FluidStack tryFluidTransfer_Internal(IFluidHandler fluidDestination, IFluidHandler fluidSource, FluidStack drainable, boolean doTransfer) {
        int fillableAmount = fluidDestination.fill(drainable, IFluidHandler.FluidAction.SIMULATE);
        if (fillableAmount > 0) {
            drainable.setAmount(fillableAmount);
            if (doTransfer) {
                FluidStack drained = fluidSource.drain(drainable, IFluidHandler.FluidAction.EXECUTE);
                if (!drained.isEmpty()) {
                    drained.setAmount(fluidDestination.fill(drained, IFluidHandler.FluidAction.EXECUTE));
                    return drained;
                }
            } else {
                return drainable;
            }
        }
        return FluidStack.EMPTY;
    }

    public static LazyOptional<IFluidHandlerItem> getFluidHandler(@NotNull ItemStack itemStack) {
        return itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
    }

    public static Optional<FluidStack> getFluidContained(@NotNull ItemStack container) {
        Optional<FluidStack> fluidContained;
        if (!container.m_41619_() && (fluidContained = FluidUtil.getFluidHandler(container = ItemHandlerHelper.copyStackWithSize(container, 1)).map(handler -> handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE))).isPresent() && !fluidContained.get().isEmpty()) {
            return fluidContained;
        }
        return Optional.empty();
    }

    public static LazyOptional<IFluidHandler> getFluidHandler(Level level, BlockPos blockPos, @Nullable Direction side) {
        BlockEntity blockEntity;
        BlockState state = level.m_8055_(blockPos);
        if (state.m_155947_() && (blockEntity = level.m_7702_(blockPos)) != null) {
            return blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, side);
        }
        return LazyOptional.empty();
    }

    @NotNull
    public static FluidActionResult tryPickUpFluid(@NotNull ItemStack emptyContainer, @Nullable Player playerIn, Level level, BlockPos pos, Direction side) {
        IFluidHandler targetFluidHandler;
        if (emptyContainer.m_41619_() || level == null || pos == null) {
            return FluidActionResult.FAILURE;
        }
        BlockState state = level.m_8055_(pos);
        Block block = state.m_60734_();
        if (block instanceof IFluidBlock) {
            targetFluidHandler = new FluidBlockWrapper((IFluidBlock)block, level, pos);
        } else if (block instanceof BucketPickup) {
            targetFluidHandler = new BucketPickupHandlerWrapper((BucketPickup)block, level, pos);
        } else {
            Optional<IFluidHandler> fluidHandler = FluidUtil.getFluidHandler(level, pos, side).resolve();
            if (!fluidHandler.isPresent()) {
                return FluidActionResult.FAILURE;
            }
            targetFluidHandler = fluidHandler.get();
        }
        return FluidUtil.tryFillContainer(emptyContainer, targetFluidHandler, Integer.MAX_VALUE, playerIn, true);
    }

    @NotNull
    public static FluidActionResult tryPlaceFluid(@Nullable Player player, Level level, InteractionHand hand, BlockPos pos, @NotNull ItemStack container, FluidStack resource) {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1);
        return FluidUtil.getFluidHandler(containerCopy).filter(handler -> FluidUtil.tryPlaceFluid(player, level, hand, pos, handler, resource)).map(IFluidHandlerItem::getContainer).map(FluidActionResult::new).orElse(FluidActionResult.FAILURE);
    }

    public static boolean tryPlaceFluid(@Nullable Player player, Level level, InteractionHand hand, BlockPos pos, IFluidHandler fluidSource, FluidStack resource) {
        boolean canDestContainFluid;
        if (level == null || pos == null) {
            return false;
        }
        Fluid fluid = resource.getFluid();
        if (fluid == Fluids.f_76191_ || !fluid.getFluidType().canBePlacedInLevel((BlockAndTintGetter)level, pos, resource)) {
            return false;
        }
        if (fluidSource.drain(resource, IFluidHandler.FluidAction.SIMULATE).isEmpty()) {
            return false;
        }
        BlockPlaceContext context = new BlockPlaceContext(level, player, hand, player == null ? ItemStack.f_41583_ : player.m_21120_(hand), new BlockHitResult(Vec3.f_82478_, Direction.UP, pos, false));
        BlockState destBlockState = level.m_8055_(pos);
        boolean isDestNonSolid = !destBlockState.m_280296_();
        boolean isDestReplaceable = destBlockState.m_60629_(context);
        boolean bl = canDestContainFluid = destBlockState.m_60734_() instanceof LiquidBlockContainer && ((LiquidBlockContainer)destBlockState.m_60734_()).m_6044_((BlockGetter)level, pos, destBlockState, fluid);
        if (!(level.m_46859_(pos) || isDestNonSolid || isDestReplaceable || canDestContainFluid)) {
            return false;
        }
        if (fluid.getFluidType().isVaporizedOnPlacement(level, pos, resource)) {
            FluidStack result = fluidSource.drain(resource, IFluidHandler.FluidAction.EXECUTE);
            if (!result.isEmpty()) {
                result.getFluid().getFluidType().onVaporize(player, level, pos, result);
                return true;
            }
        } else {
            IFluidHandler handler = canDestContainFluid ? new BlockWrapper.LiquidContainerBlockWrapper((LiquidBlockContainer)destBlockState.m_60734_(), level, pos) : FluidUtil.getFluidBlockHandler(fluid, level, pos);
            FluidStack result = FluidUtil.tryFluidTransfer(handler, fluidSource, resource, true);
            if (!result.isEmpty()) {
                SoundEvent soundevent = resource.getFluid().getFluidType().getSound(resource, SoundActions.BUCKET_EMPTY);
                if (soundevent != null) {
                    level.m_5594_(player, pos, soundevent, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
                return true;
            }
        }
        return false;
    }

    private static IFluidHandler getFluidBlockHandler(Fluid fluid, Level level, BlockPos pos) {
        BlockState state = fluid.getFluidType().getBlockForFluidState((BlockAndTintGetter)level, pos, fluid.m_76145_());
        return new BlockWrapper(state, level, pos);
    }

    public static void destroyBlockOnFluidPlacement(Level level, BlockPos pos) {
        if (!level.f_46443_) {
            BlockState destBlockState = level.m_8055_(pos);
            boolean isDestNonSolid = !destBlockState.m_280296_();
            boolean isDestReplaceable = false;
            if ((isDestNonSolid || isDestReplaceable) && !destBlockState.m_278721_()) {
                level.m_46961_(pos, true);
            }
        }
    }

    @NotNull
    public static ItemStack getFilledBucket(@NotNull FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        if (!fluidStack.hasTag() || fluidStack.getTag().m_128456_()) {
            if (fluid == Fluids.f_76193_) {
                return new ItemStack((ItemLike)Items.f_42447_);
            }
            if (fluid == Fluids.f_76195_) {
                return new ItemStack((ItemLike)Items.f_42448_);
            }
        }
        return fluid.getFluidType().getBucket(fluidStack);
    }
}

