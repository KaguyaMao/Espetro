/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 */
package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.item.PlayerMainInvWrapper;
import dev.latvian.mods.kubejs.platform.LevelPlatformHelper;
import java.util.Objects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemHandlerUtils {
    public static void giveItemToPlayer(Player player, @NotNull ItemStack stack, int preferredSlot) {
        if (stack.m_41619_()) {
            return;
        }
        PlayerMainInvWrapper inventory = new PlayerMainInvWrapper(player.m_150109_());
        Level level = player.m_9236_();
        ItemStack remainder = stack;
        if (preferredSlot >= 0 && preferredSlot < inventory.kjs$getSlots()) {
            remainder = inventory.kjs$insertItem(preferredSlot, stack, false);
        }
        if (!remainder.m_41619_()) {
            remainder = ItemHandlerUtils.insertItemStacked(inventory, remainder, false);
        }
        if (remainder.m_41619_() || remainder.m_41613_() != stack.m_41613_()) {
            level.m_6263_(null, player.m_20185_(), player.m_20186_() + 0.5, player.m_20189_(), SoundEvents.f_12019_, SoundSource.PLAYERS, 0.2f, ((level.f_46441_.m_188501_() - level.f_46441_.m_188501_()) * 0.7f + 1.0f) * 2.0f);
        }
        if (!remainder.m_41619_() && !level.f_46443_) {
            ItemEntity itemEntity = new ItemEntity(level, player.m_20185_(), player.m_20186_() + 0.5, player.m_20189_(), remainder);
            itemEntity.m_32010_(40);
            itemEntity.m_20256_(itemEntity.m_20184_().m_82542_(0.0, 1.0, 0.0));
            level.m_7967_((Entity)itemEntity);
        }
    }

    @NotNull
    public static ItemStack insertItemStacked(InventoryKJS inventory, @NotNull ItemStack stack, boolean simulate) {
        ItemStack slot;
        int i;
        if (inventory == null || stack.m_41619_()) {
            return stack;
        }
        if (!stack.m_41753_()) {
            return ItemHandlerUtils.insertItem(inventory, stack, simulate);
        }
        int sizeInventory = inventory.kjs$getSlots();
        for (i = 0; !(i >= sizeInventory || ItemHandlerUtils.canItemStacksStackRelaxed(slot = inventory.kjs$getStackInSlot(i), stack) && (stack = inventory.kjs$insertItem(i, stack, simulate)).m_41619_()); ++i) {
        }
        if (!stack.m_41619_()) {
            for (i = 0; !(i >= sizeInventory || inventory.kjs$getStackInSlot(i).m_41619_() && (stack = inventory.kjs$insertItem(i, stack, simulate)).m_41619_()); ++i) {
            }
        }
        return stack;
    }

    @NotNull
    public static ItemStack insertItem(InventoryKJS dest, @NotNull ItemStack stack, boolean simulate) {
        if (dest == null || stack.m_41619_()) {
            return stack;
        }
        for (int i = 0; i < dest.kjs$getSlots(); ++i) {
            if (!(stack = dest.kjs$insertItem(i, stack, simulate)).m_41619_()) continue;
            return ItemStack.f_41583_;
        }
        return stack;
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
        if (!a.m_41782_() || Objects.equals(a.m_41783_(), b.m_41783_())) {
            return LevelPlatformHelper.get().areCapsCompatible(a, b);
        }
        return false;
    }

    public static boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b) {
        if (a.m_41619_() || !ItemStack.m_41656_((ItemStack)a, (ItemStack)b) || a.m_41782_() != b.m_41782_()) {
            return false;
        }
        if (!a.m_41782_() || Objects.equals(a.m_41783_(), b.m_41783_())) {
            return LevelPlatformHelper.get().areCapsCompatible(a, b);
        }
        return false;
    }
}

