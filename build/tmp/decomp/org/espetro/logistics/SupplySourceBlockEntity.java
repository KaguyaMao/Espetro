/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.logistics;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.espetro.logistics.LogisticsBlocks;

public class SupplySourceBlockEntity
extends BlockEntity {
    private String sourceId = "default";

    public SupplySourceBlockEntity(BlockPos pos, BlockState state) {
        super(LogisticsBlocks.SUPPLY_SOURCE_BLOCK_ENTITY, pos, state);
    }

    public String getSourceId() {
        return this.sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId == null || sourceId.isBlank() ? "default" : sourceId;
        this.m_6596_();
    }

    @Override
    protected void m_183515_(CompoundTag tag) {
        super.m_183515_(tag);
        tag.m_128359_("source_id", this.sourceId);
    }

    @Override
    public void m_142466_(CompoundTag tag) {
        super.m_142466_(tag);
        if (tag.m_128441_("source_id")) {
            this.sourceId = tag.m_128461_("source_id");
        }
    }
}

