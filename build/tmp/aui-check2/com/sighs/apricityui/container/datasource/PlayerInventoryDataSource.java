/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.Slot
 */
package com.sighs.apricityui.container.datasource;

import com.sighs.apricityui.container.bind.ContainerBindType;
import com.sighs.apricityui.container.datasource.ContainerDataSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public final class PlayerInventoryDataSource
implements ContainerDataSource {
    private final ServerPlayer owner;
    private final Inventory inventory;

    public PlayerInventoryDataSource(ServerPlayer owner) {
        this.owner = owner;
        this.inventory = owner.m_150109_();
    }

    @Override
    public ContainerBindType bindType() {
        return ContainerBindType.PLAYER;
    }

    @Override
    public int capacity() {
        return 36;
    }

    @Override
    public Slot createSlot(int slotIndex, int x, int y) {
        return new Slot((Container)this.inventory, slotIndex, x, y);
    }

    @Override
    public boolean stillValid(ServerPlayer player) {
        return player != null && player == this.owner && player.m_6084_();
    }
}

