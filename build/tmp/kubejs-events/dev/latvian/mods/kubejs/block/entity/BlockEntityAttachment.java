/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.block.entity.BlockEntityJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockEntityAttachment {
    public static final BlockEntityAttachment[] EMPTY_ARRAY = new BlockEntityAttachment[0];

    default public CompoundTag writeAttachment() {
        return new CompoundTag();
    }

    default public void readAttachment(CompoundTag tag) {
    }

    default public void onRemove(BlockState newState) {
    }

    public static interface Factory {
        public BlockEntityAttachment create(BlockEntityJS var1);
    }
}

