/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.Connection
 *  net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelDataManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.jetbrains.annotations.NotNull;

public interface IForgeBlockEntity
extends ICapabilitySerializable<CompoundTag> {
    public static final AABB INFINITE_EXTENT_AABB = new AABB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

    private BlockEntity self() {
        return (BlockEntity)this;
    }

    @Override
    default public void deserializeNBT(CompoundTag nbt) {
        this.self().m_142466_(nbt);
    }

    @Override
    default public CompoundTag serializeNBT() {
        return this.self().m_187480_();
    }

    default public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag compoundtag = pkt.m_131708_();
        if (compoundtag != null) {
            this.self().m_142466_(compoundtag);
        }
    }

    default public void handleUpdateTag(CompoundTag tag) {
        this.self().m_142466_(tag);
    }

    public CompoundTag getPersistentData();

    default public void onChunkUnloaded() {
    }

    default public void onLoad() {
        this.requestModelDataUpdate();
    }

    default public AABB getRenderBoundingBox() {
        AABB bb = INFINITE_EXTENT_AABB;
        BlockState state = this.self().m_58900_();
        Block block = state.m_60734_();
        BlockPos pos = this.self().m_58899_();
        if (block == Blocks.f_50201_) {
            bb = new AABB(pos, pos.m_7918_(1, 1, 1));
        } else if (block == Blocks.f_50087_ || block == Blocks.f_50325_) {
            bb = new AABB(pos.m_7918_(-1, 0, -1), pos.m_7918_(2, 2, 2));
        } else if (block == Blocks.f_50677_) {
            bb = INFINITE_EXTENT_AABB;
        } else if (block != null && block != Blocks.f_50273_) {
            AABB cbb = null;
            try {
                VoxelShape collisionShape = state.m_60812_((BlockGetter)this.self().m_58904_(), pos);
                if (!collisionShape.m_83281_()) {
                    cbb = collisionShape.m_83215_().m_82338_(pos);
                }
            }
            catch (Exception e) {
                cbb = new AABB(pos.m_7918_(-1, 0, -1), pos.m_7918_(1, 1, 1));
            }
            if (cbb != null) {
                bb = cbb;
            }
        }
        return bb;
    }

    default public void requestModelDataUpdate() {
        ModelDataManager modelDataManager;
        BlockEntity te = this.self();
        Level level = te.m_58904_();
        if (level != null && level.f_46443_ && (modelDataManager = level.getModelDataManager()) != null) {
            modelDataManager.requestRefresh(te);
        }
    }

    @NotNull
    default public ModelData getModelData() {
        return ModelData.EMPTY;
    }

    default public boolean hasCustomOutlineRendering(Player player) {
        return false;
    }
}

