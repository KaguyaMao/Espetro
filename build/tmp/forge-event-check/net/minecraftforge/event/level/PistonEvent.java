/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.piston.PistonStructureResolver
 *  net.minecraftforge.eventbus.api.Cancelable
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.event.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.Nullable;

public abstract class PistonEvent
extends BlockEvent {
    private final Direction direction;
    private final PistonMoveType moveType;

    public PistonEvent(Level world, BlockPos pos, Direction direction, PistonMoveType moveType) {
        super((LevelAccessor)world, pos, world.m_8055_(pos));
        this.direction = direction;
        this.moveType = moveType;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public BlockPos getFaceOffsetPos() {
        return this.getPos().m_121945_(this.direction);
    }

    public PistonMoveType getPistonMoveType() {
        return this.moveType;
    }

    @Nullable
    public PistonStructureResolver getStructureHelper() {
        if (this.getLevel() instanceof Level) {
            return new PistonStructureResolver((Level)this.getLevel(), this.getPos(), this.getDirection(), this.getPistonMoveType().isExtend);
        }
        return null;
    }

    public static enum PistonMoveType {
        EXTEND(true),
        RETRACT(false);

        public final boolean isExtend;

        private PistonMoveType(boolean isExtend) {
            this.isExtend = isExtend;
        }
    }

    @Cancelable
    public static class Pre
    extends PistonEvent {
        public Pre(Level world, BlockPos pos, Direction direction, PistonMoveType moveType) {
            super(world, pos, direction, moveType);
        }
    }

    public static class Post
    extends PistonEvent {
        public Post(Level world, BlockPos pos, Direction direction, PistonMoveType moveType) {
            super(world, pos, direction, moveType);
        }
    }
}

