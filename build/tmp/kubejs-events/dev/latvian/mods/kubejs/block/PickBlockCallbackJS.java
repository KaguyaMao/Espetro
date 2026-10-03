/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.latvian.mods.kubejs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class PickBlockCallbackJS {
    public BlockGetter block;
    public BlockPos pos;
    public BlockState state;
    public Item item;

    public PickBlockCallbackJS(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        this.block = blockGetter;
        this.pos = blockPos;
        this.state = blockState;
    }

    public BlockState getState() {
        return this.state;
    }

    public BlockPos getBlockPos() {
        return this.pos;
    }

    public BlockGetter getBlockGetter() {
        return this.block;
    }

    public void setPickBlockItem(String item) {
        this.item = (Item)BuiltInRegistries.f_257033_.m_7745_(new ResourceLocation(item));
    }
}

