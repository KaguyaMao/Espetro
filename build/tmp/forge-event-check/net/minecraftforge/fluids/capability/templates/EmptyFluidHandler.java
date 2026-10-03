/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.fluids.capability.templates;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class EmptyFluidHandler
implements IFluidHandler {
    public static final EmptyFluidHandler INSTANCE = new EmptyFluidHandler();

    protected EmptyFluidHandler() {
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    @NotNull
    public FluidStack getFluidInTank(int tank) {
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        return 0;
    }

    @Override
    @NotNull
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    @NotNull
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return FluidStack.EMPTY;
    }
}

