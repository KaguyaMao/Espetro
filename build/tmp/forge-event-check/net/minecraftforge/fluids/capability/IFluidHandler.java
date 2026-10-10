/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.fluids.capability;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

@AutoRegisterCapability
public interface IFluidHandler {
    public int getTanks();

    @NotNull
    public FluidStack getFluidInTank(int var1);

    public int getTankCapacity(int var1);

    public boolean isFluidValid(int var1, @NotNull FluidStack var2);

    public int fill(FluidStack var1, FluidAction var2);

    @NotNull
    public FluidStack drain(FluidStack var1, FluidAction var2);

    @NotNull
    public FluidStack drain(int var1, FluidAction var2);

    public static enum FluidAction {
        EXECUTE,
        SIMULATE;


        public boolean execute() {
            return this == EXECUTE;
        }

        public boolean simulate() {
            return this == SIMULATE;
        }
    }
}

