/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common;

import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IForgeShearable {
    default public boolean isShearable(@NotNull ItemStack item, Level level, BlockPos pos) {
        return true;
    }

    @NotNull
    default public List<ItemStack> onSheared(@Nullable Player player, @NotNull ItemStack item, Level level, BlockPos pos, int fortune) {
        return Collections.emptyList();
    }
}

