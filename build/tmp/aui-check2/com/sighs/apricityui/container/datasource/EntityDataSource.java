/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.inventory.Slot
 *  net.minecraftforge.common.capabilities.ForgeCapabilities
 *  net.minecraftforge.items.IItemHandler
 *  net.minecraftforge.items.SlotItemHandler
 */
package com.sighs.apricityui.container.datasource;

import com.sighs.apricityui.container.bind.ContainerBindType;
import com.sighs.apricityui.container.datasource.ContainerDataSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public final class EntityDataSource
implements ContainerDataSource {
    private final Entity entity;
    private final IItemHandler itemHandler;
    private final int capacity;

    public EntityDataSource(Entity entity, IItemHandler itemHandler, int capacity) {
        this.entity = entity;
        this.itemHandler = itemHandler;
        this.capacity = Math.max(0, capacity);
    }

    @Override
    public ContainerBindType bindType() {
        return ContainerBindType.ENTITY;
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public Slot createSlot(int slotIndex, int x, int y) {
        return new SlotItemHandler(this.itemHandler, slotIndex, x, y);
    }

    @Override
    public boolean stillValid(ServerPlayer player) {
        if (!this.entity.m_6084_()) {
            return false;
        }
        return player.m_20280_(this.entity) <= 64.0;
    }

    public static EntityDataSource resolve(ServerPlayer player, int entityId, int capacity) {
        if (player == null) {
            return null;
        }
        Entity entity = player.m_284548_().m_6815_(entityId);
        if (entity == null) {
            return null;
        }
        IItemHandler handler = (IItemHandler)entity.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        if (handler == null) {
            return null;
        }
        int handlerSlots = Math.max(0, handler.getSlots());
        int resolvedCapacity = capacity <= 0 ? handlerSlots : Math.min(Math.max(1, capacity), handlerSlots);
        return new EntityDataSource(entity, handler, resolvedCapacity);
    }
}

