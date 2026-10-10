/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.SimpleContainer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.items;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class SlotItemHandler
extends Slot {
    private static Container emptyInventory = new SimpleContainer(0);
    private final IItemHandler itemHandler;
    private final int index;

    public SlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(emptyInventory, index, xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.index = index;
    }

    public boolean m_5857_(@NotNull ItemStack stack) {
        if (stack.m_41619_()) {
            return false;
        }
        return this.itemHandler.isItemValid(this.index, stack);
    }

    @NotNull
    public ItemStack m_7993_() {
        return this.getItemHandler().getStackInSlot(this.index);
    }

    public void m_5852_(@NotNull ItemStack stack) {
        ((IItemHandlerModifiable)this.getItemHandler()).setStackInSlot(this.index, stack);
        this.m_6654_();
    }

    public void initialize(ItemStack stack) {
        ((IItemHandlerModifiable)this.getItemHandler()).setStackInSlot(this.index, stack);
        this.m_6654_();
    }

    public void m_40234_(@NotNull ItemStack oldStackIn, @NotNull ItemStack newStackIn) {
    }

    public int m_6641_() {
        return this.itemHandler.getSlotLimit(this.index);
    }

    public int m_5866_(@NotNull ItemStack stack) {
        ItemStack maxAdd = stack.m_41777_();
        int maxInput = stack.m_41741_();
        maxAdd.m_41764_(maxInput);
        IItemHandler handler = this.getItemHandler();
        ItemStack currentStack = handler.getStackInSlot(this.index);
        if (handler instanceof IItemHandlerModifiable) {
            IItemHandlerModifiable handlerModifiable = (IItemHandlerModifiable)handler;
            handlerModifiable.setStackInSlot(this.index, ItemStack.f_41583_);
            ItemStack remainder = handlerModifiable.insertItem(this.index, maxAdd, true);
            handlerModifiable.setStackInSlot(this.index, currentStack);
            return maxInput - remainder.m_41613_();
        }
        ItemStack remainder = handler.insertItem(this.index, maxAdd, true);
        int current = currentStack.m_41613_();
        int added = maxInput - remainder.m_41613_();
        return current + added;
    }

    public boolean m_8010_(Player playerIn) {
        return !this.getItemHandler().extractItem(this.index, 1, true).m_41619_();
    }

    @NotNull
    public ItemStack m_6201_(int amount) {
        return this.getItemHandler().extractItem(this.index, amount, false);
    }

    public IItemHandler getItemHandler() {
        return this.itemHandler;
    }
}

