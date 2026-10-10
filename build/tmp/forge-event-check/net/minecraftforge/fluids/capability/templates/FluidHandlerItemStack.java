/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.fluids.capability.templates;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidHandlerItemStack
implements IFluidHandlerItem,
ICapabilityProvider {
    public static final String FLUID_NBT_KEY = "Fluid";
    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);
    @NotNull
    protected ItemStack container;
    protected int capacity;

    public FluidHandlerItemStack(@NotNull ItemStack container, int capacity) {
        this.container = container;
        this.capacity = capacity;
    }

    @Override
    @NotNull
    public ItemStack getContainer() {
        return this.container;
    }

    @NotNull
    public FluidStack getFluid() {
        CompoundTag tagCompound = this.container.m_41783_();
        if (tagCompound == null || !tagCompound.m_128441_(FLUID_NBT_KEY)) {
            return FluidStack.EMPTY;
        }
        return FluidStack.loadFluidStackFromNBT(tagCompound.m_128469_(FLUID_NBT_KEY));
    }

    protected void setFluid(FluidStack fluid) {
        if (!this.container.m_41782_()) {
            this.container.m_41751_(new CompoundTag());
        }
        CompoundTag fluidTag = new CompoundTag();
        fluid.writeToNBT(fluidTag);
        this.container.m_41783_().m_128365_(FLUID_NBT_KEY, (Tag)fluidTag);
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
        return this.capacity;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction doFill) {
        if (this.container.m_41613_() != 1 || resource.isEmpty() || !this.canFillFluidType(resource)) {
            return 0;
        }
        FluidStack contained = this.getFluid();
        if (contained.isEmpty()) {
            int fillAmount = Math.min(this.capacity, resource.getAmount());
            if (doFill.execute()) {
                FluidStack filled = resource.copy();
                filled.setAmount(fillAmount);
                this.setFluid(filled);
            }
            return fillAmount;
        }
        if (contained.isFluidEqual(resource)) {
            int fillAmount = Math.min(this.capacity - contained.getAmount(), resource.getAmount());
            if (doFill.execute() && fillAmount > 0) {
                contained.grow(fillAmount);
                this.setFluid(contained);
            }
            return fillAmount;
        }
        return 0;
    }

    @Override
    @NotNull
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (this.container.m_41613_() != 1 || resource.isEmpty() || !resource.isFluidEqual(this.getFluid())) {
            return FluidStack.EMPTY;
        }
        return this.drain(resource.getAmount(), action);
    }

    @Override
    @NotNull
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        if (this.container.m_41613_() != 1 || maxDrain <= 0) {
            return FluidStack.EMPTY;
        }
        FluidStack contained = this.getFluid();
        if (contained.isEmpty() || !this.canDrainFluidType(contained)) {
            return FluidStack.EMPTY;
        }
        int drainAmount = Math.min(contained.getAmount(), maxDrain);
        FluidStack drained = contained.copy();
        drained.setAmount(drainAmount);
        if (action.execute()) {
            contained.shrink(drainAmount);
            if (contained.isEmpty()) {
                this.setContainerToEmpty();
            } else {
                this.setFluid(contained);
            }
        }
        return drained;
    }

    public boolean canFillFluidType(FluidStack fluid) {
        return true;
    }

    public boolean canDrainFluidType(FluidStack fluid) {
        return true;
    }

    protected void setContainerToEmpty() {
        this.container.m_41749_(FLUID_NBT_KEY);
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
        return ForgeCapabilities.FLUID_HANDLER_ITEM.orEmpty(capability, this.holder);
    }

    public static class SwapEmpty
    extends FluidHandlerItemStack {
        protected final ItemStack emptyContainer;

        public SwapEmpty(ItemStack container, ItemStack emptyContainer, int capacity) {
            super(container, capacity);
            this.emptyContainer = emptyContainer;
        }

        @Override
        protected void setContainerToEmpty() {
            super.setContainerToEmpty();
            this.container = this.emptyContainer;
        }
    }

    public static class Consumable
    extends FluidHandlerItemStack {
        public Consumable(ItemStack container, int capacity) {
            super(container, capacity);
        }

        @Override
        protected void setContainerToEmpty() {
            super.setContainerToEmpty();
            this.container.m_41774_(1);
        }
    }
}

