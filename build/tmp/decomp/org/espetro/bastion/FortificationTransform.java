/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.bastion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;

public final class FortificationTransform {
    private FortificationTransform() {
    }

    public static BlockPos world(BlockPos anchor, BlockPos originOffset, BlockPos local, BlockPos pivot, Direction facing) {
        BlockPos relative = originOffset.m_121955_(local).m_121996_(pivot);
        BlockPos rotated = FortificationTransform.rotate(relative, facing);
        return anchor.m_121955_(rotated);
    }

    public static BlockPos rotate(BlockPos relative, Direction facing) {
        return switch (FortificationTransform.horizontal(facing)) {
            case Direction.NORTH -> relative;
            case Direction.EAST -> new BlockPos(-relative.m_123343_(), relative.m_123342_(), relative.m_123341_());
            case Direction.SOUTH -> new BlockPos(-relative.m_123341_(), relative.m_123342_(), -relative.m_123343_());
            case Direction.WEST -> new BlockPos(relative.m_123343_(), relative.m_123342_(), -relative.m_123341_());
            default -> throw new IllegalStateException("unreachable");
        };
    }

    public static Rotation rotation(Direction facing) {
        return switch (FortificationTransform.horizontal(facing)) {
            case Direction.NORTH -> Rotation.NONE;
            case Direction.EAST -> Rotation.CLOCKWISE_90;
            case Direction.SOUTH -> Rotation.CLOCKWISE_180;
            case Direction.WEST -> Rotation.COUNTERCLOCKWISE_90;
            default -> throw new IllegalStateException("unreachable");
        };
    }

    private static Direction horizontal(Direction facing) {
        if (facing == null || !facing.m_122434_().m_122479_()) {
            throw new IllegalArgumentException("facing must be horizontal");
        }
        return facing;
    }
}

