/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.event.EventResult
 *  dev.architectury.event.events.common.BlockEvent
 *  dev.architectury.event.events.common.InteractionEvent
 *  dev.architectury.utils.value.IntValue
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.block;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.utils.value.IntValue;
import dev.latvian.mods.kubejs.bindings.event.BlockEvents;
import dev.latvian.mods.kubejs.block.BlockBrokenEventJS;
import dev.latvian.mods.kubejs.block.BlockLeftClickedEventJS;
import dev.latvian.mods.kubejs.block.BlockPlacedEventJS;
import dev.latvian.mods.kubejs.block.BlockRightClickedEventJS;
import dev.latvian.mods.kubejs.block.FarmlandTrampledEventJS;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class KubeJSBlockEventHandler {
    public static void init() {
        InteractionEvent.RIGHT_CLICK_BLOCK.register(KubeJSBlockEventHandler::rightClick);
        InteractionEvent.LEFT_CLICK_BLOCK.register(KubeJSBlockEventHandler::leftClick);
        BlockEvent.BREAK.register(KubeJSBlockEventHandler::blockBreak);
        BlockEvent.PLACE.register(KubeJSBlockEventHandler::blockPlace);
        InteractionEvent.FARMLAND_TRAMPLE.register(KubeJSBlockEventHandler::farmlandTrample);
    }

    private static EventResult rightClick(Player player, InteractionHand hand, BlockPos pos, Direction direction) {
        if (BlockEvents.RIGHT_CLICKED.hasListeners() && !player.m_36335_().m_41519_(player.m_21120_(hand).m_41720_())) {
            return BlockEvents.RIGHT_CLICKED.post((ScriptTypeHolder)player, (Object)player.m_9236_().m_8055_(pos), new BlockRightClickedEventJS(player, hand, pos, direction)).arch();
        }
        return EventResult.pass();
    }

    private static EventResult leftClick(Player player, InteractionHand hand, BlockPos pos, Direction direction) {
        return BlockEvents.LEFT_CLICKED.hasListeners() ? BlockEvents.LEFT_CLICKED.post((ScriptTypeHolder)player, (Object)player.m_9236_().m_8055_(pos), new BlockLeftClickedEventJS(player, hand, pos, direction)).arch() : EventResult.pass();
    }

    private static EventResult blockBreak(Level level, BlockPos pos, BlockState state, ServerPlayer player, @Nullable IntValue xp) {
        return BlockEvents.BROKEN.hasListeners(state.m_60734_()) ? BlockEvents.BROKEN.post((ScriptTypeHolder)level, (Object)state.m_60734_(), new BlockBrokenEventJS(player, level, pos, state, xp)).arch() : EventResult.pass();
    }

    private static EventResult blockPlace(Level level, BlockPos pos, BlockState state, @Nullable Entity placer) {
        return BlockEvents.PLACED.hasListeners(state.m_60734_()) ? BlockEvents.PLACED.post((ScriptTypeHolder)level, (Object)state.m_60734_(), new BlockPlacedEventJS(placer, level, pos, state)).arch() : EventResult.pass();
    }

    private static EventResult farmlandTrample(Level level, BlockPos pos, BlockState state, float distance, @Nullable Entity entity) {
        return BlockEvents.FARMLAND_TRAMPLED.hasListeners(state.m_60734_()) ? BlockEvents.FARMLAND_TRAMPLED.post((ScriptTypeHolder)level, (Object)state.m_60734_(), new FarmlandTrampledEventJS(level, pos, state, distance, entity)).arch() : EventResult.pass();
    }
}

