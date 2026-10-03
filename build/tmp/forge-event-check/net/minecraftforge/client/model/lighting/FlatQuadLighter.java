/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.color.block.BlockColors
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package net.minecraftforge.client.model.lighting;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.lighting.QuadLighter;

public class FlatQuadLighter
extends QuadLighter {
    private static final Direction[] SIDES = Direction.values();
    private static final float MAX_POSITION = 0.99f;
    private static final byte MAX_NORMAL = 127;
    private boolean isFullCube;
    private final int[] packedLight = new int[7];

    public FlatQuadLighter(BlockColors colors) {
        super(colors);
    }

    @Override
    protected void computeLightingAt(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        this.isFullCube = Block.m_49916_((VoxelShape)state.m_60812_((BlockGetter)level, pos));
        for (Direction side : SIDES) {
            this.packedLight[side.ordinal()] = LevelRenderer.m_109537_((BlockAndTintGetter)level, (BlockState)state, (BlockPos)pos.m_121945_(side));
        }
        this.packedLight[6] = LevelRenderer.m_109537_((BlockAndTintGetter)level, (BlockState)state, (BlockPos)pos);
    }

    @Override
    protected float calculateBrightness(float[] position) {
        return 1.0f;
    }

    @Override
    protected int calculateLightmap(float[] position, byte[] normal) {
        if ((this.isFullCube || position[1] < -0.99f) && normal[1] <= -127) {
            return this.packedLight[Direction.DOWN.ordinal()];
        }
        if ((this.isFullCube || position[1] > 0.99f) && normal[1] >= 127) {
            return this.packedLight[Direction.UP.ordinal()];
        }
        if ((this.isFullCube || position[2] < -0.99f) && normal[2] <= -127) {
            return this.packedLight[Direction.NORTH.ordinal()];
        }
        if ((this.isFullCube || position[2] > 0.99f) && normal[2] >= 127) {
            return this.packedLight[Direction.SOUTH.ordinal()];
        }
        if ((this.isFullCube || position[0] < -0.99f) && normal[0] <= -127) {
            return this.packedLight[Direction.WEST.ordinal()];
        }
        if ((this.isFullCube || position[0] > 0.99f) && normal[0] >= 127) {
            return this.packedLight[Direction.EAST.ordinal()];
        }
        return this.packedLight[6];
    }
}

