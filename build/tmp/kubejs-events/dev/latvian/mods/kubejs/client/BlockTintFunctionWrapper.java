/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.mod.util.color.Color
 *  net.minecraft.client.color.block.BlockColor
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.block.BlockTintFunction;
import dev.latvian.mods.rhino.mod.util.color.Color;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public record BlockTintFunctionWrapper(BlockTintFunction function) implements BlockColor
{
    public int m_92566_(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int index) {
        Color c = this.function.getColor(state, level, pos, index);
        return c == null ? -1 : c.getArgbJS();
    }
}

