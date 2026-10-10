/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.bastion;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public final class OnBuildingBlock
extends Block {
    public static final String BLOCK_ID = "onbuilding";
    public static final IntegerProperty STAGE = IntegerProperty.m_61631_("stage", 0, 6);

    public OnBuildingBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.m_49959_((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(STAGE, 0));
    }

    @Override
    protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
        builder.m_61104_(STAGE);
    }
}

