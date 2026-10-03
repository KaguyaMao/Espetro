/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.bindings.BlockWrapper;
import dev.latvian.mods.kubejs.block.BlockBrokenEventJS;
import dev.latvian.mods.kubejs.block.BlockLeftClickedEventJS;
import dev.latvian.mods.kubejs.block.BlockModificationEventJS;
import dev.latvian.mods.kubejs.block.BlockPlacedEventJS;
import dev.latvian.mods.kubejs.block.BlockRightClickedEventJS;
import dev.latvian.mods.kubejs.block.DetectorBlockEventJS;
import dev.latvian.mods.kubejs.block.FarmlandTrampledEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockEvents {
    public static final EventGroup GROUP = EventGroup.of("BlockEvents");
    public static final Extra SUPPORTS_BLOCK = new Extra().transformer(BlockEvents::transformBlock).toString(o -> ((Block)o).kjs$getId()).identity().describeType(context -> context.javaType(Block.class));
    public static final EventHandler MODIFICATION = GROUP.startup("modification", () -> BlockModificationEventJS.class);
    public static final EventHandler RIGHT_CLICKED = GROUP.common("rightClicked", () -> BlockRightClickedEventJS.class).extra(SUPPORTS_BLOCK).hasResult();
    public static final EventHandler LEFT_CLICKED = GROUP.common("leftClicked", () -> BlockLeftClickedEventJS.class).extra(SUPPORTS_BLOCK).hasResult();
    public static final EventHandler PLACED = GROUP.common("placed", () -> BlockPlacedEventJS.class).extra(SUPPORTS_BLOCK).hasResult();
    public static final EventHandler BROKEN = GROUP.common("broken", () -> BlockBrokenEventJS.class).extra(SUPPORTS_BLOCK).hasResult();
    public static final EventHandler DETECTOR_CHANGED = GROUP.common("detectorChanged", () -> DetectorBlockEventJS.class).extra(Extra.STRING);
    public static final EventHandler DETECTOR_POWERED = GROUP.common("detectorPowered", () -> DetectorBlockEventJS.class).extra(Extra.STRING);
    public static final EventHandler DETECTOR_UNPOWERED = GROUP.common("detectorUnpowered", () -> DetectorBlockEventJS.class).extra(Extra.STRING);
    public static final EventHandler FARMLAND_TRAMPLED = GROUP.common("farmlandTrampled", () -> FarmlandTrampledEventJS.class).extra(SUPPORTS_BLOCK).hasResult();

    private static Block transformBlock(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Block) {
            Block block = (Block)o;
            return block;
        }
        if (o instanceof BlockItem) {
            BlockItem item = (BlockItem)o;
            return item.m_40614_();
        }
        if (o instanceof BlockState) {
            BlockState state = (BlockState)o;
            return state.m_60734_();
        }
        ResourceLocation id = ResourceLocation.m_135820_((String)o.toString());
        Block block = id == null ? null : BlockWrapper.getBlock(id);
        return block == Blocks.f_50016_ ? null : block;
    }
}

