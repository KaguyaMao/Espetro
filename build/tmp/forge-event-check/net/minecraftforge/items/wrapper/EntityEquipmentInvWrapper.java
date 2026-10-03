/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.EquipmentSlot$Type
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.items.wrapper;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.EntityArmorInvWrapper;
import net.minecraftforge.items.wrapper.EntityHandsInvWrapper;
import org.jetbrains.annotations.NotNull;

public abstract class EntityEquipmentInvWrapper
implements IItemHandlerModifiable {
    protected final LivingEntity entity;
    protected final List<EquipmentSlot> slots;

    public EntityEquipmentInvWrapper(LivingEntity entity, EquipmentSlot.Type slotType) {
        this.entity = entity;
        ArrayList<EquipmentSlot> slots = new ArrayList<EquipmentSlot>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.m_20743_() != slotType) continue;
            slots.add(slot);
        }
        this.slots = ImmutableList.copyOf(slots);
    }

    @Override
    public int getSlots() {
        return this.slots.size();
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot) {
        return this.entity.m_6844_(this.validateSlotIndex(slot));
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        boolean reachedLimit;
        if (stack.m_41619_()) {
            return ItemStack.f_41583_;
        }
        EquipmentSlot equipmentSlot = this.validateSlotIndex(slot);
        ItemStack existing = this.entity.m_6844_(equipmentSlot);
        int limit = this.getStackLimit(slot, stack);
        if (!existing.m_41619_()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                return stack;
            }
            limit -= existing.m_41613_();
        }
        if (limit <= 0) {
            return stack;
        }
        boolean bl = reachedLimit = stack.m_41613_() > limit;
        if (!simulate) {
            if (existing.m_41619_()) {
                this.entity.m_8061_(equipmentSlot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            } else {
                existing.m_41769_(reachedLimit ? limit : stack.m_41613_());
            }
        }
        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.m_41613_() - limit) : ItemStack.f_41583_;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.f_41583_;
        }
        EquipmentSlot equipmentSlot = this.validateSlotIndex(slot);
        ItemStack existing = this.entity.m_6844_(equipmentSlot);
        if (existing.m_41619_()) {
            return ItemStack.f_41583_;
        }
        int toExtract = Math.min(amount, existing.m_41741_());
        if (existing.m_41613_() <= toExtract) {
            if (!simulate) {
                this.entity.m_8061_(equipmentSlot, ItemStack.f_41583_);
            }
            return existing;
        }
        if (!simulate) {
            this.entity.m_8061_(equipmentSlot, ItemHandlerHelper.copyStackWithSize(existing, existing.m_41613_() - toExtract));
        }
        return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
    }

    @Override
    public int getSlotLimit(int slot) {
        EquipmentSlot equipmentSlot = this.validateSlotIndex(slot);
        return equipmentSlot.m_20743_() == EquipmentSlot.Type.ARMOR ? 1 : 64;
    }

    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return Math.min(this.getSlotLimit(slot), stack.m_41741_());
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        EquipmentSlot equipmentSlot = this.validateSlotIndex(slot);
        if (ItemStack.m_41728_((ItemStack)this.entity.m_6844_(equipmentSlot), (ItemStack)stack)) {
            return;
        }
        this.entity.m_8061_(equipmentSlot, stack);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }

    protected EquipmentSlot validateSlotIndex(int slot) {
        if (slot < 0 || slot >= this.slots.size()) {
            throw new IllegalArgumentException("Slot " + slot + " not in valid range - [0," + this.slots.size() + ")");
        }
        return this.slots.get(slot);
    }

    public static LazyOptional<IItemHandlerModifiable>[] create(LivingEntity entity) {
        LazyOptional[] ret;
        ret = new LazyOptional[]{LazyOptional.of(() -> new EntityHandsInvWrapper(entity)), LazyOptional.of(() -> new EntityArmorInvWrapper(entity)), LazyOptional.of(() -> new CombinedInvWrapper(ret[0].orElse(null), ret[1].orElse(null)))};
        return ret;
    }
}

