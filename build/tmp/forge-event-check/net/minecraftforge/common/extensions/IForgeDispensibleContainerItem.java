/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.DispensibleContainerItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.BlockHitResult
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public interface IForgeDispensibleContainerItem {
    private DispensibleContainerItem self() {
        return (DispensibleContainerItem)this;
    }

    default public boolean emptyContents(@Nullable Player player, Level level, BlockPos pos, @Nullable BlockHitResult hitResult, @Nullable ItemStack container) {
        return this.self().m_142073_(player, level, pos, hitResult);
    }
}

