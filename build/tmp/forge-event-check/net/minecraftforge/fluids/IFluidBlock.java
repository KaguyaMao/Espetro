/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.material.Fluid
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public interface IFluidBlock {
    public Fluid getFluid();

    public int place(Level var1, BlockPos var2, @NotNull FluidStack var3, IFluidHandler.FluidAction var4);

    @NotNull
    public FluidStack drain(Level var1, BlockPos var2, IFluidHandler.FluidAction var3);

    public boolean canDrain(Level var1, BlockPos var2);

    public float getFilledPercentage(Level var1, BlockPos var2);
}

