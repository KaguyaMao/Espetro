/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.bastion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RadioBlock
extends HorizontalDirectionalBlock {
    public static final String BLOCK_ID = "radio";
    public static final int BREAK_SECONDS = 30;
    private static final VoxelShape SHAPE = Block.m_49796_(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);

    public RadioBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.m_49959_((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(f_54117_, Direction.NORTH));
    }

    @Override
    protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
        builder.m_61104_(f_54117_);
    }

    @Override
    public BlockState m_5573_(BlockPlaceContext context) {
        return (BlockState)this.m_49966_().m_61124_(f_54117_, context.m_8125_().m_122424_());
    }

    @Override
    public VoxelShape m_5940_(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}

