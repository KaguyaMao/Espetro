/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.fluids;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public interface IFluidTank {
    @NotNull
    public FluidStack getFluid();

    public int getFluidAmount();

    public int getCapacity();

    public boolean isFluidValid(FluidStack var1);

    public int fill(FluidStack var1, IFluidHandler.FluidAction var2);

    @NotNull
    public FluidStack drain(int var1, IFluidHandler.FluidAction var2);

    @NotNull
    public FluidStack drain(FluidStack var1, IFluidHandler.FluidAction var2);
}

