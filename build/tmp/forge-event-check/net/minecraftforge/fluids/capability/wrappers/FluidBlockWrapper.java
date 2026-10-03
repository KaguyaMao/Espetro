/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.fluids.capability.wrappers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class FluidBlockWrapper
implements IFluidHandler {
    protected final IFluidBlock fluidBlock;
    protected final Level world;
    protected final BlockPos blockPos;

    public FluidBlockWrapper(IFluidBlock fluidBlock, Level world, BlockPos blockPos) {
        this.fluidBlock = fluidBlock;
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    @NotNull
    public FluidStack getFluidInTank(int tank) {
        return tank == 0 ? this.fluidBlock.drain(this.world, this.blockPos, IFluidHandler.FluidAction.SIMULATE) : FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        float filledPercentage;
        FluidStack stored = this.getFluidInTank(tank);
        if (!stored.isEmpty() && (filledPercentage = this.fluidBlock.getFilledPercentage(this.world, this.blockPos)) > 0.0f) {
            return (int)((float)stored.getAmount() / filledPercentage);
        }
        return 1000;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return stack.getFluid() == this.fluidBlock.getFluid();
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        return this.fluidBlock.place(this.world, this.blockPos, resource, action);
    }

    @Override
    @NotNull
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        FluidStack simulatedDrained;
        if (!resource.isEmpty() && this.fluidBlock.canDrain(this.world, this.blockPos) && resource.getFluid() == this.fluidBlock.getFluid() && (simulatedDrained = this.fluidBlock.drain(this.world, this.blockPos, IFluidHandler.FluidAction.SIMULATE)).getAmount() <= resource.getAmount() && resource.isFluidEqual(simulatedDrained)) {
            if (action.execute()) {
                return this.fluidBlock.drain(this.world, this.blockPos, IFluidHandler.FluidAction.EXECUTE).copy();
            }
            return simulatedDrained.copy();
        }
        return FluidStack.EMPTY;
    }

    @Override
    @NotNull
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        FluidStack simulatedDrained;
        if (maxDrain > 0 && this.fluidBlock.canDrain(this.world, this.blockPos) && (simulatedDrained = this.fluidBlock.drain(this.world, this.blockPos, IFluidHandler.FluidAction.SIMULATE)).getAmount() <= maxDrain) {
            if (action.execute()) {
                return this.fluidBlock.drain(this.world, this.blockPos, IFluidHandler.FluidAction.EXECUTE).copy();
            }
            return simulatedDrained.copy();
        }
        return FluidStack.EMPTY;
    }
}

