/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.logistics;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.espetro.logistics.SupplySourceBlockEntity;

public class SupplySourceBlock
extends BaseEntityBlock {
    public SupplySourceBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape m_7514_(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public BlockEntity m_142194_(BlockPos pos, BlockState state) {
        return new SupplySourceBlockEntity(pos, state);
    }
}

