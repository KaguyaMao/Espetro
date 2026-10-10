/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemHandlerHelper {
    @NotNull
    public static ItemStack insertItem(IItemHandler dest, @NotNull ItemStack stack, boolean simulate) {
        if (dest == null || stack.m_41619_()) {
            return stack;
        }
        for (int i = 0; i < dest.getSlots(); ++i) {
            if (!(stack = dest.insertItem(i, stack, simulate)).m_41619_()) continue;
            return ItemStack.f_41583_;
        }
        return stack;
    }

    public static boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b) {
        if (a.m_41619_() || !ItemStack.m_41656_((ItemStack)a, (ItemStack)b) || a.m_41782_() != b.m_41782_()) {
            return false;
        }
        return (!a.m_41782_() || a.m_41783_().equals((Object)b.m_41783_())) && a.areCapsCompatible((CapabilityProvider)b);
    }

    public static boolean canItemStacksStackRelaxed(@NotNull ItemStack a, @NotNull ItemStack b) {
        if (a.m_41619_() || b.m_41619_() || a.m_41720_() != b.m_41720_()) {
            return false;
        }
        if (!a.m_41753_()) {
            return false;
        }
        if (a.m_41782_() != b.m_41782_()) {
            return false;
        }
        return (!a.m_41782_() || a.m_41783_().equals((Object)b.m_41783_())) && a.areCapsCompatible((CapabilityProvider)b);
    }

    @NotNull
    public static ItemStack copyStackWithSize(@NotNull ItemStack itemStack, int size) {
        if (size == 0) {
            return ItemStack.f_41583_;
        }
        ItemStack copy = itemStack.m_41777_();
        copy.m_41764_(size);
        return copy;
    }

    @NotNull
    public static ItemStack insertItemStacked(IItemHandler inventory, @NotNull ItemStack stack, boolean simulate) {
        ItemStack slot;
        int i;
        if (inventory == null || stack.m_41619_()) {
            return stack;
        }
        if (!stack.m_41753_()) {
            return ItemHandlerHelper.insertItem(inventory, stack, simulate);
        }
        int sizeInventory = inventory.getSlots();
        for (i = 0; !(i >= sizeInventory || ItemHandlerHelper.canItemStacksStackRelaxed(slot = inventory.getStackInSlot(i), stack) && (stack = inventory.insertItem(i, stack, simulate)).m_41619_()); ++i) {
        }
        if (!stack.m_41619_()) {
            for (i = 0; !(i >= sizeInventory || inventory.getStackInSlot(i).m_41619_() && (stack = inventory.insertItem(i, stack, simulate)).m_41619_()); ++i) {
            }
        }
        return stack;
    }

    public static void giveItemToPlayer(Player player, @NotNull ItemStack stack) {
        ItemHandlerHelper.giveItemToPlayer(player, stack, -1);
    }

    public static void giveItemToPlayer(Player player, @NotNull ItemStack stack, int preferredSlot) {
        if (stack.m_41619_()) {
            return;
        }
        PlayerMainInvWrapper inventory = new PlayerMainInvWrapper(player.m_150109_());
        Level level = player.m_9236_();
        ItemStack remainder = stack;
        if (preferredSlot >= 0 && preferredSlot < inventory.getSlots()) {
            remainder = inventory.insertItem(preferredSlot, stack, false);
        }
        if (!remainder.m_41619_()) {
            remainder = ItemHandlerHelper.insertItemStacked(inventory, remainder, false);
        }
        if (remainder.m_41619_() || remainder.m_41613_() != stack.m_41613_()) {
            level.m_6263_(null, player.m_20185_(), player.m_20186_() + 0.5, player.m_20189_(), SoundEvents.f_12019_, SoundSource.PLAYERS, 0.2f, ((level.f_46441_.m_188501_() - level.f_46441_.m_188501_()) * 0.7f + 1.0f) * 2.0f);
        }
        if (!remainder.m_41619_() && !level.f_46443_) {
            ItemEntity entityitem = new ItemEntity(level, player.m_20185_(), player.m_20186_() + 0.5, player.m_20189_(), remainder);
            entityitem.m_32010_(40);
            entityitem.m_20256_(entityitem.m_20184_().m_82542_(0.0, 1.0, 0.0));
            level.m_7967_((Entity)entityitem);
        }
    }

    public static int calcRedstoneFromInventory(@Nullable IItemHandler inv) {
        if (inv == null) {
            return 0;
        }
        int itemsFound = 0;
        float proportion = 0.0f;
        for (int j = 0; j < inv.getSlots(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (itemstack.m_41619_()) continue;
            proportion += (float)itemstack.m_41613_() / (float)Math.min(inv.getSlotLimit(j), itemstack.m_41741_());
            ++itemsFound;
        }
        return Mth.m_14143_((float)((proportion /= (float)inv.getSlots()) * 14.0f)) + (itemsFound > 0 ? 1 : 0);
    }
}

