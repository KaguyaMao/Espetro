/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.platform.Platform
 *  net.minecraft.core.dispenser.DispenseItemBehavior
 *  net.minecraft.core.dispenser.ShearsDispenseItemBehavior
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.ShearsItem
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.DispenserBlock
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.latvian.mods.kubejs.item.custom;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ShearsItemBuilder
extends ItemBuilder {
    public static final ResourceLocation TAG = new ResourceLocation(Platform.isForge() ? "forge:shears" : "c:shears");
    public transient float speedBaseline;

    public static boolean isCustomShears(ItemStack stack) {
        return stack.m_41720_() instanceof ShearsItemKJS;
    }

    public ShearsItemBuilder(ResourceLocation i) {
        super(i);
        this.speedBaseline(5.0f);
        this.parentModel("minecraft:item/handheld");
        this.unstackable();
        this.tag(TAG);
    }

    public ShearsItemBuilder speedBaseline(float f) {
        this.speedBaseline = f;
        return this;
    }

    @Override
    public Item createObject() {
        ShearsItemKJS item = new ShearsItemKJS(this);
        DispenserBlock.m_52672_((ItemLike)item, (DispenseItemBehavior)new ShearsDispenseItemBehavior());
        return item;
    }

    public static class ShearsItemKJS
    extends ShearsItem {
        public final ShearsItemBuilder builder;

        public ShearsItemKJS(ShearsItemBuilder builder) {
            super(builder.createItemProperties());
            this.builder = builder;
        }

        public float m_8102_(ItemStack itemStack, BlockState blockState) {
            if (blockState.m_204336_(BlockTags.f_13035_)) {
                return 15.0f;
            }
            if (blockState.m_60713_(Blocks.f_50033_)) {
                return this.builder.speedBaseline * 3.0f;
            }
            if (blockState.m_60713_(Blocks.f_50191_) || blockState.m_60713_(Blocks.f_152475_)) {
                return this.builder.speedBaseline / 2.5f;
            }
            if (blockState.m_204336_(BlockTags.f_13089_)) {
                return this.builder.speedBaseline;
            }
            return super.m_8102_(itemStack, blockState);
        }
    }
}

