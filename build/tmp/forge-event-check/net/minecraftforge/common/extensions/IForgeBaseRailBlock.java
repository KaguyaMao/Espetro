/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.entity.vehicle.MinecartFurnace
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.RailShape
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.jetbrains.annotations.Nullable;

public interface IForgeBaseRailBlock {
    public boolean isFlexibleRail(BlockState var1, BlockGetter var2, BlockPos var3);

    default public boolean canMakeSlopes(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    public RailShape getRailDirection(BlockState var1, BlockGetter var2, BlockPos var3, @Nullable AbstractMinecart var4);

    default public float getRailMaxSpeed(BlockState state, Level level, BlockPos pos, AbstractMinecart cart) {
        if (cart instanceof MinecartFurnace) {
            return cart.m_20069_() ? 0.15f : 0.2f;
        }
        return cart.m_20069_() ? 0.2f : 0.4f;
    }

    default public void onMinecartPass(BlockState state, Level level, BlockPos pos, AbstractMinecart cart) {
    }

    default public boolean isValidRailShape(RailShape shape) {
        return true;
    }
}

