/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.item.BucketItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.BucketPickup
 *  net.minecraft.world.level.material.FluidState
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.fluids.capability.wrappers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class BucketPickupHandlerWrapper
implements IFluidHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final BucketPickup bucketPickupHandler;
    protected final Level world;
    protected final BlockPos blockPos;

    public BucketPickupHandlerWrapper(BucketPickup bucketPickupHandler, Level world, BlockPos blockPos) {
        this.bucketPickupHandler = bucketPickupHandler;
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
        FluidState fluidState;
        if (tank == 0 && !(fluidState = this.world.m_6425_(this.blockPos)).m_76178_()) {
            return new FluidStack(fluidState.m_76152_(), 1000);
        }
        return FluidStack.EMPTY;
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
        return 0;
    }

    @Override
    @NotNull
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        FluidState fluidState;
        if (!resource.isEmpty() && 1000 <= resource.getAmount() && !(fluidState = this.world.m_6425_(this.blockPos)).m_76178_() && resource.getFluid() == fluidState.m_76152_()) {
            if (action.execute()) {
                Item item;
                ItemStack itemStack = this.bucketPickupHandler.m_142598_((LevelAccessor)this.world, this.blockPos, this.world.m_8055_(this.blockPos));
                if (itemStack != ItemStack.f_41583_ && (item = itemStack.m_41720_()) instanceof BucketItem) {
                    BucketItem bucket = (BucketItem)item;
                    FluidStack extracted = new FluidStack(bucket.getFluid(), 1000);
                    if (!resource.isFluidEqual(extracted)) {
                        LOGGER.error("Fluid removed without successfully being picked up. Fluid {} at {} in {} matched requested type, but after performing pickup was {}.", (Object)ForgeRegistries.FLUIDS.getKey(fluidState.m_76152_()), (Object)this.blockPos, (Object)this.world.m_46472_().m_135782_(), (Object)ForgeRegistries.FLUIDS.getKey(bucket.getFluid()));
                        return FluidStack.EMPTY;
                    }
                    return extracted;
                }
            } else {
                FluidStack extracted = new FluidStack(fluidState.m_76152_(), 1000);
                if (resource.isFluidEqual(extracted)) {
                    return extracted;
                }
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    @NotNull
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        FluidState fluidState;
        if (1000 <= maxDrain && !(fluidState = this.world.m_6425_(this.blockPos)).m_76178_()) {
            Item item;
            if (action.simulate()) {
                return new FluidStack(fluidState.m_76152_(), 1000);
            }
            ItemStack itemStack = this.bucketPickupHandler.m_142598_((LevelAccessor)this.world, this.blockPos, this.world.m_8055_(this.blockPos));
            if (itemStack != ItemStack.f_41583_ && (item = itemStack.m_41720_()) instanceof BucketItem) {
                BucketItem bucket = (BucketItem)item;
                return new FluidStack(bucket.getFluid(), 1000);
            }
        }
        return FluidStack.EMPTY;
    }
}

