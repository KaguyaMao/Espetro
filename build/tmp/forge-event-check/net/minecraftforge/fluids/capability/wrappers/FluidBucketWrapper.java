/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.BucketItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.MilkBucketItem
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.material.Fluids
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.fluids.capability.wrappers;

import net.minecraft.core.Direction;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidBucketWrapper
implements IFluidHandlerItem,
ICapabilityProvider {
    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);
    @NotNull
    protected ItemStack container;

    public FluidBucketWrapper(@NotNull ItemStack container) {
        this.container = container;
    }

    @Override
    @NotNull
    public ItemStack getContainer() {
        return this.container;
    }

    public boolean canFillFluidType(FluidStack fluid) {
        if (fluid.getFluid() == Fluids.f_76193_ || fluid.getFluid() == Fluids.f_76195_) {
            return true;
        }
        return !fluid.getFluid().getFluidType().getBucket(fluid).m_41619_();
    }

    @NotNull
    public FluidStack getFluid() {
        Item item = this.container.m_41720_();
        if (item instanceof BucketItem) {
            return new FluidStack(((BucketItem)item).getFluid(), 1000);
        }
        if (item instanceof MilkBucketItem && ForgeMod.MILK.isPresent()) {
            return new FluidStack(ForgeMod.MILK.get(), 1000);
        }
        return FluidStack.EMPTY;
    }

    protected void setFluid(@NotNull FluidStack fluidStack) {
        this.container = fluidStack.isEmpty() ? new ItemStack((ItemLike)Items.f_42446_) : FluidUtil.getFilledBucket(fluidStack);
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
        return 1000;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (this.container.m_41613_() != 1 || resource.getAmount() < 1000 || this.container.m_41720_() instanceof MilkBucketItem || !this.getFluid().isEmpty() || !this.canFillFluidType(resource)) {
            return 0;
        }
        if (action.execute()) {
            this.setFluid(resource);
        }
        return 1000;
    }

    @Override
    @NotNull
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (this.container.m_41613_() != 1 || resource.getAmount() < 1000) {
            return FluidStack.EMPTY;
        }
        FluidStack fluidStack = this.getFluid();
        if (!fluidStack.isEmpty() && fluidStack.isFluidEqual(resource)) {
            if (action.execute()) {
                this.setFluid(FluidStack.EMPTY);
            }
            return fluidStack;
        }
        return FluidStack.EMPTY;
    }

    @Override
    @NotNull
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        if (this.container.m_41613_() != 1 || maxDrain < 1000) {
            return FluidStack.EMPTY;
        }
        FluidStack fluidStack = this.getFluid();
        if (!fluidStack.isEmpty()) {
            if (action.execute()) {
                this.setFluid(FluidStack.EMPTY);
            }
            return fluidStack;
        }
        return FluidStack.EMPTY;
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
        return ForgeCapabilities.FLUID_HANDLER_ITEM.orEmpty(capability, this.holder);
    }
}

