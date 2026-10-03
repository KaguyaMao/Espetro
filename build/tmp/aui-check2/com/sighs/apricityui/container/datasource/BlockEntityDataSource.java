/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.kubejs.block.entity.BlockEntityJS
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Container
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraftforge.common.capabilities.ForgeCapabilities
 *  net.minecraftforge.items.IItemHandler
 *  net.minecraftforge.items.SlotItemHandler
 */
package com.sighs.apricityui.container.datasource;

import com.sighs.apricityui.container.bind.ContainerBindType;
import com.sighs.apricityui.container.datasource.ContainerDataSource;
import dev.latvian.mods.kubejs.block.entity.BlockEntityJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public final class BlockEntityDataSource
implements ContainerDataSource {
    private final BlockEntity blockEntity;
    private final IItemHandler itemHandler;
    private final Container container;
    private final int capacity;

    public BlockEntityDataSource(BlockEntity blockEntity, IItemHandler itemHandler, int capacity) {
        this(blockEntity, itemHandler, null, capacity);
    }

    private BlockEntityDataSource(BlockEntity blockEntity, IItemHandler itemHandler, Container container, int capacity) {
        this.blockEntity = blockEntity;
        this.itemHandler = itemHandler;
        this.container = container;
        this.capacity = Math.max(0, capacity);
    }

    @Override
    public ContainerBindType bindType() {
        return ContainerBindType.BLOCK_ENTITY;
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public Slot createSlot(int slotIndex, int x, int y) {
        return this.itemHandler != null ? new SlotItemHandler(this.itemHandler, slotIndex, x, y) : new Slot(this.container, slotIndex, x, y);
    }

    @Override
    public boolean stillValid(ServerPlayer player) {
        if (this.blockEntity.m_58901_()) {
            return false;
        }
        BlockPos pos = this.blockEntity.m_58899_();
        return player.m_20275_((double)pos.m_123341_() + 0.5, (double)pos.m_123342_() + 0.5, (double)pos.m_123343_() + 0.5) <= 64.0;
    }

    public static BlockEntityDataSource resolve(ServerPlayer player, BlockPos pos, int capacity) {
        if (player == null || pos == null) {
            return null;
        }
        ServerLevel level = player.m_284548_();
        if (!level.m_46749_(pos)) {
            return null;
        }
        BlockEntity blockEntity = level.m_7702_(pos);
        if (blockEntity == null) {
            return null;
        }
        IItemHandler handler = (IItemHandler)blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).orElse(null);
        if (handler == null) {
            handler = (IItemHandler)blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        }
        if (handler != null) {
            int handlerSlots = Math.max(0, handler.getSlots());
            int resolvedCapacity = capacity <= 0 ? handlerSlots : Math.min(Math.max(1, capacity), handlerSlots);
            return new BlockEntityDataSource(blockEntity, handler, resolvedCapacity);
        }
        if (blockEntity instanceof BlockEntityJS) {
            Container container;
            BlockEntityJS kubeBlockEntity = (BlockEntityJS)blockEntity;
            if (kubeBlockEntity.inventory != null && (container = kubeBlockEntity.inventory.kjs$asContainer()) != null) {
                int containerSlots = Math.max(0, container.m_6643_());
                int resolvedCapacity = capacity <= 0 ? containerSlots : Math.min(Math.max(1, capacity), containerSlots);
                return new BlockEntityDataSource(blockEntity, null, container, resolvedCapacity);
            }
        }
        return null;
    }
}

