/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.chunk.ImposterProtoChunk
 *  net.minecraft.world.level.chunk.LevelChunk
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.client.model.data.ModelDataManager;
import org.jetbrains.annotations.Nullable;

public interface IForgeBlockGetter {
    private BlockGetter self() {
        return (BlockGetter)this;
    }

    @Nullable
    default public BlockEntity getExistingBlockEntity(BlockPos pos) {
        IForgeBlockGetter iForgeBlockGetter = this;
        if (iForgeBlockGetter instanceof Level) {
            Level level = (Level)iForgeBlockGetter;
            if (!level.m_7232_(SectionPos.m_123171_((int)pos.m_123341_()), SectionPos.m_123171_((int)pos.m_123343_()))) {
                return null;
            }
            return level.m_46865_(pos).getExistingBlockEntity(pos);
        }
        iForgeBlockGetter = this;
        if (iForgeBlockGetter instanceof LevelChunk) {
            LevelChunk chunk = (LevelChunk)iForgeBlockGetter;
            return (BlockEntity)chunk.m_62954_().get(pos);
        }
        iForgeBlockGetter = this;
        if (iForgeBlockGetter instanceof ImposterProtoChunk) {
            ImposterProtoChunk chunk = (ImposterProtoChunk)iForgeBlockGetter;
            return chunk.m_62768_().getExistingBlockEntity(pos);
        }
        return this.self().m_7702_(pos);
    }

    @Nullable
    default public ModelDataManager getModelDataManager() {
        return null;
    }
}

