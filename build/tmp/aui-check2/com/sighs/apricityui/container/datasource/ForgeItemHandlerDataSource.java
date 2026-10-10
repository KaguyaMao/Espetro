/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.inventory.Slot
 *  net.minecraftforge.items.IItemHandler
 *  net.minecraftforge.items.SlotItemHandler
 */
package com.sighs.apricityui.container.datasource;

import com.sighs.apricityui.container.bind.ContainerBindType;
import com.sighs.apricityui.container.datasource.ContainerDataSource;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public final class ForgeItemHandlerDataSource
implements ContainerDataSource {
    private final ContainerBindType bindType;
    private final IItemHandler handler;
    private final Predicate<ServerPlayer> validityChecker;

    public ForgeItemHandlerDataSource(ContainerBindType bindType, IItemHandler handler, Predicate<ServerPlayer> validityChecker) {
        this.bindType = Objects.requireNonNull(bindType, "bindType");
        this.handler = Objects.requireNonNull(handler, "handler");
        this.validityChecker = validityChecker == null ? player -> true : validityChecker;
    }

    @Override
    public ContainerBindType bindType() {
        return this.bindType;
    }

    @Override
    public int capacity() {
        return this.handler.getSlots();
    }

    @Override
    public Slot createSlot(int slotIndex, int x, int y) {
        return new SlotItemHandler(this.handler, slotIndex, x, y);
    }

    @Override
    public boolean stillValid(ServerPlayer player) {
        return this.validityChecker.test(player);
    }
}

