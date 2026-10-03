/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.CropBlock
 *  net.minecraft.world.level.block.FlowerBlock
 *  net.minecraft.world.level.block.SaplingBlock
 *  net.minecraft.world.level.block.state.BlockState
 */
package net.minecraftforge.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.PlantType;

public interface IPlantable {
    default public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        if (this instanceof CropBlock || this == Blocks.f_276665_) {
            return PlantType.CROP;
        }
        if (this instanceof SaplingBlock) {
            return PlantType.PLAINS;
        }
        if (this instanceof FlowerBlock) {
            return PlantType.PLAINS;
        }
        if (this == Blocks.f_50036_) {
            return PlantType.DESERT;
        }
        if (this == Blocks.f_50196_) {
            return PlantType.WATER;
        }
        if (this == Blocks.f_50073_) {
            return PlantType.CAVE;
        }
        if (this == Blocks.f_50072_) {
            return PlantType.CAVE;
        }
        if (this == Blocks.f_50200_) {
            return PlantType.NETHER;
        }
        if (this == Blocks.f_50359_) {
            return PlantType.PLAINS;
        }
        return PlantType.PLAINS;
    }

    public BlockState getPlant(BlockGetter var1, BlockPos var2);
}

