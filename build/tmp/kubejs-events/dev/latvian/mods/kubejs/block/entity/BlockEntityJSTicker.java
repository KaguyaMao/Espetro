/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.block.entity.BlockEntityCallback;
import dev.latvian.mods.kubejs.block.entity.BlockEntityInfo;
import dev.latvian.mods.kubejs.block.entity.BlockEntityJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

public record BlockEntityJSTicker(BlockEntityInfo info, int frequency, int offset, BlockEntityCallback callback, boolean server) implements BlockEntityTicker<BlockEntityJS>
{
    public void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntityJS e) {
        if (this.frequency <= 1 || e.tick % this.frequency == this.offset) {
            try {
                this.callback.accept(e);
            }
            catch (Exception ex) {
                if (this.server) {
                    ConsoleJS.SERVER.error("Error while ticking KubeJS block entity '" + String.valueOf(this.info.blockBuilder.id) + "'", ex);
                }
                ConsoleJS.CLIENT.error("Error while ticking KubeJS block entity '" + String.valueOf(this.info.blockBuilder.id) + "'", ex);
            }
            e.postTick(true);
        } else {
            e.postTick(false);
        }
    }
}

