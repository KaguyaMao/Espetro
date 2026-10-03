/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList$Builder
 *  net.minecraft.client.color.block.BlockColor
 *  net.minecraft.client.color.block.BlockColors
 *  net.minecraft.client.color.item.ItemColor
 *  net.minecraft.client.color.item.ItemColors
 *  net.minecraft.world.level.ColorResolver
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.event.IModBusEvent
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public abstract class RegisterColorHandlersEvent
extends Event
implements IModBusEvent {
    @ApiStatus.Internal
    protected RegisterColorHandlersEvent() {
    }

    public static class ColorResolvers
    extends RegisterColorHandlersEvent {
        private final ImmutableList.Builder<ColorResolver> builder;

        @ApiStatus.Internal
        public ColorResolvers(ImmutableList.Builder<ColorResolver> builder) {
            this.builder = builder;
        }

        public void register(ColorResolver resolver) {
            this.builder.add((Object)resolver);
        }
    }

    public static class Item
    extends RegisterColorHandlersEvent {
        private final ItemColors itemColors;
        private final BlockColors blockColors;

        @ApiStatus.Internal
        public Item(ItemColors itemColors, BlockColors blockColors) {
            this.itemColors = itemColors;
            this.blockColors = blockColors;
        }

        public ItemColors getItemColors() {
            return this.itemColors;
        }

        public BlockColors getBlockColors() {
            return this.blockColors;
        }

        public void register(ItemColor itemColor, ItemLike ... items) {
            this.itemColors.m_92689_(itemColor, items);
        }
    }

    public static class Block
    extends RegisterColorHandlersEvent {
        private final BlockColors blockColors;

        @ApiStatus.Internal
        public Block(BlockColors blockColors) {
            this.blockColors = blockColors;
        }

        public BlockColors getBlockColors() {
            return this.blockColors;
        }

        public void register(BlockColor blockColor, net.minecraft.world.level.block.Block ... blocks) {
            this.blockColors.m_92589_(blockColor, blocks);
        }
    }
}

