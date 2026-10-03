/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.util;

import java.lang.ref.WeakReference;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public class BlockSnapshot {
    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("forge.debugBlockSnapshot", "false"));
    private final ResourceKey<Level> dim;
    private final BlockPos pos;
    private final int flags;
    private final BlockState block;
    @Nullable
    private final CompoundTag nbt;
    @Nullable
    private WeakReference<LevelAccessor> level;
    private String toString = null;

    private BlockSnapshot(ResourceKey<Level> dim, LevelAccessor level, BlockPos pos, BlockState state, @Nullable CompoundTag nbt, int flags) {
        this.dim = dim;
        this.pos = pos.m_7949_();
        this.block = state;
        this.flags = flags;
        this.nbt = nbt;
        this.level = new WeakReference<LevelAccessor>(level);
        if (DEBUG) {
            System.out.println("Created " + this.toString());
        }
    }

    public static BlockSnapshot create(ResourceKey<Level> dim, LevelAccessor world, BlockPos pos) {
        return BlockSnapshot.create(dim, world, pos, 3);
    }

    public static BlockSnapshot create(ResourceKey<Level> dim, LevelAccessor world, BlockPos pos, int flag) {
        return new BlockSnapshot(dim, world, pos, world.m_8055_(pos), BlockSnapshot.getBlockEntityTag(world.m_7702_(pos)), flag);
    }

    @Nullable
    private static CompoundTag getBlockEntityTag(@Nullable BlockEntity te) {
        return te == null ? null : te.m_187480_();
    }

    public BlockState getCurrentBlock() {
        LevelAccessor world = this.getLevel();
        return world == null ? Blocks.f_50016_.m_49966_() : world.m_8055_(this.pos);
    }

    @Nullable
    public LevelAccessor getLevel() {
        LevelAccessor world;
        LevelAccessor levelAccessor = world = this.level != null ? (LevelAccessor)this.level.get() : null;
        if (world == null) {
            world = ServerLifecycleHooks.getCurrentServer().m_129880_(this.dim);
            this.level = new WeakReference<LevelAccessor>(world);
        }
        return world;
    }

    public BlockState getReplacedBlock() {
        return this.block;
    }

    @Nullable
    public BlockEntity getBlockEntity() {
        return this.getTag() != null ? BlockEntity.m_155241_((BlockPos)this.getPos(), (BlockState)this.getReplacedBlock(), (CompoundTag)this.getTag()) : null;
    }

    public boolean restore() {
        return this.restore(false);
    }

    public boolean restore(boolean force) {
        return this.restore(force, true);
    }

    public boolean restore(boolean force, boolean notifyNeighbors) {
        return this.restoreToLocation(this.getLevel(), this.getPos(), force, notifyNeighbors);
    }

    public boolean restoreToLocation(LevelAccessor world, BlockPos pos, boolean force, boolean notifyNeighbors) {
        int flags;
        BlockState current = this.getCurrentBlock();
        BlockState replaced = this.getReplacedBlock();
        int n = flags = notifyNeighbors ? 3 : 2;
        if (current != replaced) {
            if (force) {
                world.m_7731_(pos, replaced, flags);
            } else {
                return false;
            }
        }
        world.m_7731_(pos, replaced, flags);
        if (world instanceof Level) {
            ((Level)world).m_7260_(pos, current, replaced, flags);
        }
        BlockEntity te = null;
        if (this.getTag() != null && (te = world.m_7702_(pos)) != null) {
            te.m_142466_(this.getTag());
            te.m_6596_();
        }
        if (DEBUG) {
            System.out.println("Restored " + this.toString());
        }
        return true;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        BlockSnapshot other = (BlockSnapshot)obj;
        return this.dim.equals(other.dim) && this.pos.equals((Object)other.pos) && this.block == other.block && this.flags == other.flags && Objects.equals(this.nbt, other.nbt);
    }

    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.dim.hashCode();
        hash = 73 * hash + this.pos.hashCode();
        hash = 73 * hash + this.block.hashCode();
        hash = 73 * hash + this.flags;
        hash = 73 * hash + Objects.hashCode(this.getTag());
        return hash;
    }

    public String toString() {
        if (this.toString == null) {
            this.toString = "BlockSnapshot[World:" + String.valueOf(this.dim.m_135782_()) + ",Pos: " + String.valueOf(this.pos) + ",State: " + String.valueOf(this.block) + ",Flags: " + this.flags + ",NBT: " + (this.nbt == null ? "null" : this.nbt.toString()) + "]";
        }
        return this.toString;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getFlag() {
        return this.flags;
    }

    @Nullable
    public CompoundTag getTag() {
        return this.nbt;
    }
}

