/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.Block
 */
package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BlockItemBuilder
extends ItemBuilder {
    public BlockBuilder blockBuilder;

    public BlockItemBuilder(ResourceLocation i) {
        super(i);
    }

    @Override
    public Item createObject() {
        return new BlockItem((Block)this.blockBuilder.get(), this.createItemProperties());
    }

    @Override
    public String getTranslationKeyGroup() {
        return "block";
    }

    @Override
    public void generateAssetJsons(AssetJsonGenerator generator) {
    }
}

