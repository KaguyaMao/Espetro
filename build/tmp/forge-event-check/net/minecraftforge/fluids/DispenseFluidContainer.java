/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockSource
 *  net.minecraft.core.Direction
 *  net.minecraft.core.dispenser.DefaultDispenseItemBehavior
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.DispenserBlock
 *  net.minecraft.world.level.block.entity.DispenserBlockEntity
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

public class DispenseFluidContainer
extends DefaultDispenseItemBehavior {
    private static final DispenseFluidContainer INSTANCE = new DispenseFluidContainer();
    private final DefaultDispenseItemBehavior dispenseBehavior = new DefaultDispenseItemBehavior();

    public static DispenseFluidContainer getInstance() {
        return INSTANCE;
    }

    private DispenseFluidContainer() {
    }

    @NotNull
    public ItemStack m_7498_(@NotNull BlockSource source, @NotNull ItemStack stack) {
        if (FluidUtil.getFluidContained(stack).isPresent()) {
            return this.dumpContainer(source, stack);
        }
        return this.fillContainer(source, stack);
    }

    @NotNull
    private ItemStack fillContainer(@NotNull BlockSource source, @NotNull ItemStack stack) {
        ServerLevel level = source.m_7727_();
        Direction dispenserFacing = (Direction)source.m_6414_().m_61143_((Property)DispenserBlock.f_52659_);
        BlockPos blockpos = source.m_7961_().m_121945_(dispenserFacing);
        FluidActionResult actionResult = FluidUtil.tryPickUpFluid(stack, null, (Level)level, blockpos, dispenserFacing.m_122424_());
        ItemStack resultStack = actionResult.getResult();
        if (!actionResult.isSuccess() || resultStack.m_41619_()) {
            return super.m_7498_(source, stack);
        }
        if (stack.m_41613_() == 1) {
            return resultStack;
        }
        if (((DispenserBlockEntity)source.m_8118_()).m_59237_(resultStack) < 0) {
            this.dispenseBehavior.m_6115_(source, resultStack);
        }
        ItemStack stackCopy = stack.m_41777_();
        stackCopy.m_41774_(1);
        return stackCopy;
    }

    @NotNull
    private ItemStack dumpContainer(BlockSource source, @NotNull ItemStack stack) {
        ItemStack singleStack = stack.m_41777_();
        singleStack.m_41764_(1);
        IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(singleStack).orElse(null);
        if (fluidHandler == null) {
            return super.m_7498_(source, stack);
        }
        FluidStack fluidStack = fluidHandler.drain(1000, IFluidHandler.FluidAction.EXECUTE);
        Direction dispenserFacing = (Direction)source.m_6414_().m_61143_((Property)DispenserBlock.f_52659_);
        BlockPos blockpos = source.m_7961_().m_121945_(dispenserFacing);
        FluidActionResult result = FluidUtil.tryPlaceFluid(null, (Level)source.m_7727_(), InteractionHand.MAIN_HAND, blockpos, stack, fluidStack);
        if (result.isSuccess()) {
            ItemStack drainedStack = result.getResult();
            if (drainedStack.m_41613_() == 1) {
                return drainedStack;
            }
            if (!drainedStack.m_41619_() && ((DispenserBlockEntity)source.m_8118_()).m_59237_(drainedStack) < 0) {
                this.dispenseBehavior.m_6115_(source, drainedStack);
            }
            ItemStack stackCopy = drainedStack.m_41777_();
            stackCopy.m_41774_(1);
            return stackCopy;
        }
        return this.dispenseBehavior.m_6115_(source, stack);
    }
}

