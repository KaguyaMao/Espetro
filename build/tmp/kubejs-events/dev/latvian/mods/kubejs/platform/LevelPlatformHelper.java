/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.platform;

import dev.latvian.mods.kubejs.block.entity.BlockEntityInfo;
import dev.latvian.mods.kubejs.block.entity.BlockEntityJS;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface LevelPlatformHelper {
    public static final Lazy<LevelPlatformHelper> INSTANCE = Lazy.serviceLoader(LevelPlatformHelper.class);

    public static LevelPlatformHelper get() {
        return INSTANCE.get();
    }

    @Nullable
    public InventoryKJS getInventoryFromBlockEntity(BlockEntity var1, Direction var2);

    public boolean areCapsCompatible(ItemStack var1, ItemStack var2);

    public double getReachDistance(LivingEntity var1);

    public BlockEntityJS createBlockEntity(BlockPos var1, BlockState var2, BlockEntityInfo var3);
}

