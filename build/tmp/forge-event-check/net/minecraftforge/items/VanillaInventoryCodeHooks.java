/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.DropperBlock
 *  net.minecraft.world.level.block.HopperBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.DispenserBlockEntity
 *  net.minecraft.world.level.block.entity.Hopper
 *  net.minecraft.world.level.block.entity.HopperBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.apache.commons.lang3.tuple.ImmutablePair
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.items;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VanillaInventoryCodeHooks {
    @Nullable
    public static Boolean extractHook(Level level, Hopper dest) {
        return VanillaInventoryCodeHooks.getItemHandler(level, dest, Direction.UP).map(itemHandlerResult -> {
            IItemHandler handler = (IItemHandler)itemHandlerResult.getKey();
            for (int i = 0; i < handler.getSlots(); ++i) {
                ItemStack extractItem = handler.extractItem(i, 1, true);
                if (extractItem.m_41619_()) continue;
                for (int j = 0; j < dest.m_6643_(); ++j) {
                    ItemStack destStack = dest.m_8020_(j);
                    if (!dest.m_7013_(j, extractItem) || !destStack.m_41619_() && (destStack.m_41613_() >= destStack.m_41741_() || destStack.m_41613_() >= dest.m_6893_() || !ItemHandlerHelper.canItemStacksStack(extractItem, destStack))) continue;
                    extractItem = handler.extractItem(i, 1, false);
                    if (destStack.m_41619_()) {
                        dest.m_6836_(j, extractItem);
                    } else {
                        destStack.m_41769_(1);
                        dest.m_6836_(j, destStack);
                    }
                    dest.m_6596_();
                    return true;
                }
            }
            return false;
        }).orElse(null);
    }

    public static boolean dropperInsertHook(Level level, BlockPos pos, DispenserBlockEntity dropper, int slot, @NotNull ItemStack stack) {
        Direction enumfacing = (Direction)level.m_8055_(pos).m_61143_((Property)DropperBlock.f_52659_);
        BlockPos blockpos = pos.m_121945_(enumfacing);
        return VanillaInventoryCodeHooks.getItemHandler(level, blockpos.m_123341_(), blockpos.m_123342_(), blockpos.m_123343_(), enumfacing.m_122424_()).map(destinationResult -> {
            ItemStack dispensedStack;
            IItemHandler itemHandler = (IItemHandler)destinationResult.getKey();
            Object destination = destinationResult.getValue();
            ItemStack remainder = VanillaInventoryCodeHooks.putStackInInventoryAllSlots((BlockEntity)dropper, destination, itemHandler, dispensedStack = stack.m_41777_().m_41620_(1));
            if (remainder.m_41619_()) {
                remainder = stack.m_41777_();
                remainder.m_41774_(1);
            } else {
                remainder = stack.m_41777_();
            }
            dropper.m_6836_(slot, remainder);
            return false;
        }).orElse(true);
    }

    public static boolean insertHook(HopperBlockEntity hopper) {
        Direction hopperFacing = (Direction)hopper.m_58900_().m_61143_((Property)HopperBlock.f_54021_);
        return VanillaInventoryCodeHooks.getItemHandler(hopper.m_58904_(), (Hopper)hopper, hopperFacing).map(destinationResult -> {
            IItemHandler itemHandler = (IItemHandler)destinationResult.getKey();
            Object destination = destinationResult.getValue();
            if (VanillaInventoryCodeHooks.isFull(itemHandler)) {
                return false;
            }
            for (int i = 0; i < hopper.m_6643_(); ++i) {
                if (hopper.m_8020_(i).m_41619_()) continue;
                ItemStack originalSlotContents = hopper.m_8020_(i).m_41777_();
                ItemStack insertStack = hopper.m_7407_(i, 1);
                ItemStack remainder = VanillaInventoryCodeHooks.putStackInInventoryAllSlots((BlockEntity)hopper, destination, itemHandler, insertStack);
                if (remainder.m_41619_()) {
                    return true;
                }
                hopper.m_6836_(i, originalSlotContents);
            }
            return false;
        }).orElse(false);
    }

    private static ItemStack putStackInInventoryAllSlots(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack) {
        for (int slot = 0; slot < destInventory.getSlots() && !stack.m_41619_(); ++slot) {
            stack = VanillaInventoryCodeHooks.insertStack(source, destination, destInventory, stack, slot);
        }
        return stack;
    }

    private static ItemStack insertStack(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack, int slot) {
        ItemStack itemstack = destInventory.getStackInSlot(slot);
        if (destInventory.insertItem(slot, stack, true).m_41619_()) {
            HopperBlockEntity destinationHopper;
            boolean insertedItem = false;
            boolean inventoryWasEmpty = VanillaInventoryCodeHooks.isEmpty(destInventory);
            if (itemstack.m_41619_()) {
                destInventory.insertItem(slot, stack, false);
                stack = ItemStack.f_41583_;
                insertedItem = true;
            } else if (ItemHandlerHelper.canItemStacksStack(itemstack, stack)) {
                int originalSize = stack.m_41613_();
                boolean bl = insertedItem = originalSize < (stack = destInventory.insertItem(slot, stack, false)).m_41613_();
            }
            if (insertedItem && inventoryWasEmpty && destination instanceof HopperBlockEntity && !(destinationHopper = (HopperBlockEntity)destination).m_59409_()) {
                int k = 0;
                if (source instanceof HopperBlockEntity && destinationHopper.getLastUpdateTime() >= ((HopperBlockEntity)source).getLastUpdateTime()) {
                    k = 1;
                }
                destinationHopper.m_59395_(8 - k);
            }
        }
        return stack;
    }

    private static Optional<Pair<IItemHandler, Object>> getItemHandler(Level level, Hopper hopper, Direction hopperFacing) {
        double x = hopper.m_6343_() + (double)hopperFacing.m_122429_();
        double y = hopper.m_6358_() + (double)hopperFacing.m_122430_();
        double z = hopper.m_6446_() + (double)hopperFacing.m_122431_();
        return VanillaInventoryCodeHooks.getItemHandler(level, x, y, z, hopperFacing.m_122424_());
    }

    private static boolean isFull(IItemHandler itemHandler) {
        for (int slot = 0; slot < itemHandler.getSlots(); ++slot) {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (!stackInSlot.m_41619_() && stackInSlot.m_41613_() >= itemHandler.getSlotLimit(slot)) continue;
            return false;
        }
        return true;
    }

    private static boolean isEmpty(IItemHandler itemHandler) {
        for (int slot = 0; slot < itemHandler.getSlots(); ++slot) {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.m_41613_() <= 0) continue;
            return false;
        }
        return true;
    }

    public static Optional<Pair<IItemHandler, Object>> getItemHandler(Level worldIn, double x, double y, double z, Direction side) {
        BlockEntity blockEntity;
        int k;
        int j;
        int i = Mth.m_14107_((double)x);
        BlockPos blockpos = new BlockPos(i, j = Mth.m_14107_((double)y), k = Mth.m_14107_((double)z));
        BlockState state = worldIn.m_8055_(blockpos);
        if (state.m_155947_() && (blockEntity = worldIn.m_7702_(blockpos)) != null) {
            return blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, side).map(capability -> ImmutablePair.of((Object)capability, (Object)blockEntity));
        }
        return Optional.empty();
    }
}

