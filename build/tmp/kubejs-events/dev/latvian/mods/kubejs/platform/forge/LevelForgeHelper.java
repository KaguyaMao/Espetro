/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.common.ForgeMod
 *  net.minecraftforge.common.capabilities.CapabilityProvider
 *  net.minecraftforge.common.capabilities.ForgeCapabilities
 *  net.minecraftforge.items.IItemHandler
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.platform.forge;

import dev.latvian.mods.kubejs.block.entity.BlockEntityInfo;
import dev.latvian.mods.kubejs.block.entity.BlockEntityJS;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.platform.LevelPlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class LevelForgeHelper
implements LevelPlatformHelper {
    @Override
    @Nullable
    public InventoryKJS getInventoryFromBlockEntity(BlockEntity tileEntity, Direction facing) {
        IItemHandler handler = (IItemHandler)tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, facing).orElse(null);
        if (handler instanceof InventoryKJS) {
            InventoryKJS inv = (InventoryKJS)handler;
            return inv;
        }
        return null;
    }

    @Override
    public boolean areCapsCompatible(ItemStack a, ItemStack b) {
        return a.areCapsCompatible((CapabilityProvider)b);
    }

    @Override
    public double getReachDistance(LivingEntity livingEntity) {
        return livingEntity.m_21051_((Attribute)ForgeMod.ENTITY_REACH.get()).m_22135_();
    }

    @Override
    public BlockEntityJS createBlockEntity(BlockPos pos, BlockState state, BlockEntityInfo info) {
        return new BlockEntityJS(pos, state, info);
    }
}

