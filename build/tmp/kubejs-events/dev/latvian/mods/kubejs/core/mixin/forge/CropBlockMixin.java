/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.common.IPlantable
 *  net.minecraftforge.common.PlantType
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.latvian.mods.kubejs.core.mixin.forge;

import dev.latvian.mods.kubejs.block.custom.BasicCropBlockJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={BasicCropBlockJS.class}, remap=false)
public abstract class CropBlockMixin
implements IPlantable {
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return PlantType.CROP;
    }

    public BlockState getPlant(BlockGetter world, BlockPos pos) {
        BlockState state = world.m_8055_(pos);
        Block thisBlock = (Block)this;
        return state.m_60734_() != thisBlock ? thisBlock.m_49966_() : state;
    }
}

