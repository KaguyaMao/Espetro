/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.inventory.Slot
 *  net.minecraftforge.items.IItemHandler
 *  net.minecraftforge.items.ItemStackHandler
 *  net.minecraftforge.items.SlotItemHandler
 */
package com.sighs.apricityui.container.datasource;

import com.sighs.apricityui.config.ApricitySavedData;
import com.sighs.apricityui.container.bind.ContainerBindType;
import com.sighs.apricityui.container.datasource.ContainerDataSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public final class SavedDataDataSource
implements ContainerDataSource {
    private final ContainerBindType bindType;
    private final ApricitySavedData savedData;
    private final String inventoryKey;
    private ItemStackHandler handler;

    public SavedDataDataSource(ContainerBindType bindType, ApricitySavedData savedData, String inventoryKey, ItemStackHandler handler) {
        this.bindType = bindType;
        this.savedData = savedData;
        this.inventoryKey = inventoryKey;
        this.handler = handler;
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
        return new SlotItemHandler((IItemHandler)this.handler, slotIndex, x, y);
    }

    @Override
    public boolean supportsResize() {
        return true;
    }

    @Override
    public int resize(int newCapacity) {
        int normalized = Math.max(1, newCapacity);
        this.handler = this.savedData.getOrCreate(this.inventoryKey, normalized);
        return this.handler.getSlots();
    }

    @Override
    public void onClose(ServerPlayer player) {
        this.savedData.m_77762_();
    }
}

