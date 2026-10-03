/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.mod.util.color.Color
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.FallingBlock
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.rhino.mod.util.color.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FallingBlockBuilder
extends BlockBuilder {
    private int dustColor = -8356741;

    public FallingBlockBuilder(ResourceLocation i) {
        super(i);
    }

    @Override
    public Block createObject() {
        return new FallingBlock(this.createProperties()){

            public int m_6248_(BlockState state, BlockGetter level, BlockPos pos) {
                return FallingBlockBuilder.this.dustColor;
            }
        };
    }

    public FallingBlockBuilder dustColor(Color color) {
        this.dustColor = color.getRgbJS();
        return this;
    }
}

