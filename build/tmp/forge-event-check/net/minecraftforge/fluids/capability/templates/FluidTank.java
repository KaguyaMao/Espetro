/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.fluids.capability.templates;

import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class FluidTank
implements IFluidHandler,
IFluidTank {
    protected Predicate<FluidStack> validator;
    @NotNull
    protected FluidStack fluid = FluidStack.EMPTY;
    protected int capacity;

    public FluidTank(int capacity) {
        this(capacity, e -> true);
    }

    public FluidTank(int capacity, Predicate<FluidStack> validator) {
        this.capacity = capacity;
        this.validator = validator;
    }

    public FluidTank setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public FluidTank setValidator(Predicate<FluidStack> validator) {
        if (validator != null) {
            this.validator = validator;
        }
        return this;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return this.validator.test(stack);
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    @NotNull
    public FluidStack getFluid() {
        return this.fluid;
    }

    @Override
    public int getFluidAmount() {
        return this.fluid.getAmount();
    }

    public FluidTank readFromNBT(CompoundTag nbt) {
        FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
        this.setFluid(fluid);
        return this;
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        this.fluid.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    @NotNull
    public FluidStack getFluidInTank(int tank) {
        return this.getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return this.isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty() || !this.isFluidValid(resource)) {
            return 0;
        }
        if (action.simulate()) {
            if (this.fluid.isEmpty()) {
                return Math.min(this.capacity, resource.getAmount());
            }
            if (!this.fluid.isFluidEqual(resource)) {
                return 0;
            }
            return Math.min(this.capacity - this.fluid.getAmount(), resource.getAmount());
        }
        if (this.fluid.isEmpty()) {
            this.fluid = new FluidStack(resource, Math.min(this.capacity, resource.getAmount()));
            this.onContentsChanged();
            return this.fluid.getAmount();
        }
        if (!this.fluid.isFluidEqual(resource)) {
            return 0;
        }
        int filled = this.capacity - this.fluid.getAmount();
        if (resource.getAmount() < filled) {
            this.fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            this.fluid.setAmount(this.capacity);
        }
        if (filled > 0) {
            this.onContentsChanged();
        }
        return filled;
    }

    @Override
    @NotNull
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty() || !resource.isFluidEqual(this.fluid)) {
            return FluidStack.EMPTY;
        }
        return this.drain(resource.getAmount(), action);
    }

    @Override
    @NotNull
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        int drained = maxDrain;
        if (this.fluid.getAmount() < drained) {
            drained = this.fluid.getAmount();
        }
        FluidStack stack = new FluidStack(this.fluid, drained);
        if (action.execute() && drained > 0) {
            this.fluid.shrink(drained);
            this.onContentsChanged();
        }
        return stack;
    }

    protected void onContentsChanged() {
    }

    public void setFluid(FluidStack stack) {
        this.fluid = stack;
    }

    public boolean isEmpty() {
        return this.fluid.isEmpty();
    }

    public int getSpace() {
        return Math.max(0, this.capacity - this.fluid.getAmount());
    }
}

