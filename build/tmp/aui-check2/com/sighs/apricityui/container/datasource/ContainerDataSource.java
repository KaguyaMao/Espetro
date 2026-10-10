/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.inventory.Slot
 */
package com.sighs.apricityui.container.datasource;

import com.sighs.apricityui.container.bind.ContainerBindType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;

public interface ContainerDataSource {
    public ContainerBindType bindType();

    public int capacity();

    public Slot createSlot(int var1, int var2, int var3);

    default public boolean stillValid(ServerPlayer player) {
        return true;
    }

    default public void onClose(ServerPlayer player) {
    }

    default public boolean supportsResize() {
        return false;
    }

    default public int resize(int newCapacity) {
        return this.capacity();
    }
}

