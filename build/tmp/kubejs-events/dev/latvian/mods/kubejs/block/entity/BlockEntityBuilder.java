/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.BlockEntityType$Builder
 */
package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.block.entity.BlockEntityInfo;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityBuilder
extends BuilderBase<BlockEntityType<?>> {
    public BlockEntityInfo info;

    public BlockEntityBuilder(ResourceLocation i, BlockEntityInfo info) {
        super(i);
        this.info = info;
    }

    @Override
    public RegistryInfo getRegistryType() {
        return RegistryInfo.BLOCK_ENTITY_TYPE;
    }

    @Override
    public BlockEntityType<?> createObject() {
        this.info.entityType = BlockEntityType.Builder.m_155273_(this.info::createBlockEntity, (Block[])new Block[]{(Block)this.info.blockBuilder.get()}).m_58966_(null);
        return this.info.entityType;
    }
}

