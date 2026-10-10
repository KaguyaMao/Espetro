/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.CommandRuntimeException
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package net.minecraftforge.server.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.timings.ForgeTimings;
import net.minecraftforge.server.timings.TimeTracker;

class TrackCommand {
    private static final DecimalFormat TIME_FORMAT = new DecimalFormat("#####0.00");

    TrackCommand() {
    }

    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"track").then(StartTrackingCommand.register())).then(ResetTrackingCommand.register())).then(TrackResultsEntity.register())).then(TrackResultsBlockEntity.register())).then(StartTrackingCommand.register());
    }

    private static class StartTrackingCommand {
        private StartTrackingCommand() {
        }

        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"start").requires(cs -> cs.m_6761_(2))).then(Commands.m_82127_((String)"te").then(Commands.m_82129_((String)"duration", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(ctx -> {
                int duration = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"duration");
                TimeTracker.BLOCK_ENTITY_UPDATE.reset();
                TimeTracker.BLOCK_ENTITY_UPDATE.enable(duration);
                ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237110_((String)"commands.forge.tracking.be.enabled", (Object[])new Object[]{duration}), true);
                return 0;
            })))).then(Commands.m_82127_((String)"entity").then(Commands.m_82129_((String)"duration", (ArgumentType)IntegerArgumentType.integer((int)1)).executes(ctx -> {
                int duration = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"duration");
                TimeTracker.ENTITY_UPDATE.reset();
                TimeTracker.ENTITY_UPDATE.enable(duration);
                ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237110_((String)"commands.forge.tracking.entity.enabled", (Object[])new Object[]{duration}), true);
                return 0;
            })));
        }
    }

    private static class ResetTrackingCommand {
        private ResetTrackingCommand() {
        }

        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"reset").requires(cs -> cs.m_6761_(2))).then(Commands.m_82127_((String)"te").executes(ctx -> {
                TimeTracker.BLOCK_ENTITY_UPDATE.reset();
                ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237115_((String)"commands.forge.tracking.be.reset"), true);
                return 0;
            }))).then(Commands.m_82127_((String)"entity").executes(ctx -> {
                TimeTracker.ENTITY_UPDATE.reset();
                ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237115_((String)"commands.forge.tracking.entity.reset"), true);
                return 0;
            }));
        }
    }

    private static class TrackResultsEntity {
        private TrackResultsEntity() {
        }

        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.m_82127_((String)"entity").executes(ctx -> TrackResults.execute((CommandSourceStack)ctx.getSource(), TimeTracker.ENTITY_UPDATE, data -> {
                Entity entity = (Entity)data.getObject().get();
                if (entity == null) {
                    return Component.m_237115_((String)"commands.forge.tracking.invalid");
                }
                BlockPos pos = entity.m_20183_();
                double averageTimings = data.getAverageTimings();
                String tickTime = (averageTimings > 1000.0 ? TIME_FORMAT.format(averageTimings / 1000.0) : TIME_FORMAT.format(averageTimings)) + (averageTimings < 1000.0 ? "\u03bcs" : "ms");
                return Component.m_237110_((String)"commands.forge.tracking.timing_entry", (Object[])new Object[]{ForgeRegistries.ENTITY_TYPES.getKey(entity.m_6095_()), entity.m_9236_().m_46472_().m_135782_().toString(), pos.m_123341_(), pos.m_123342_(), pos.m_123343_(), tickTime});
            }));
        }
    }

    private static class TrackResultsBlockEntity {
        private TrackResultsBlockEntity() {
        }

        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.m_82127_((String)"te").executes(ctx -> TrackResults.execute((CommandSourceStack)ctx.getSource(), TimeTracker.BLOCK_ENTITY_UPDATE, data -> {
                BlockEntity be = (BlockEntity)data.getObject().get();
                if (be == null) {
                    return Component.m_237115_((String)"commands.forge.tracking.invalid");
                }
                BlockPos pos = be.m_58899_();
                double averageTimings = data.getAverageTimings();
                String tickTime = (averageTimings > 1000.0 ? TIME_FORMAT.format(averageTimings / 1000.0) : TIME_FORMAT.format(averageTimings)) + (averageTimings < 1000.0 ? "\u03bcs" : "ms");
                return Component.m_237110_((String)"commands.forge.tracking.timing_entry", (Object[])new Object[]{ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(be.m_58903_()), be.m_58904_().m_46472_().m_135782_().toString(), pos.m_123341_(), pos.m_123342_(), pos.m_123343_(), tickTime});
            }));
        }
    }

    private static class TrackResults {
        private TrackResults() {
        }

        private static <T> List<ForgeTimings<T>> getSortedTimings(TimeTracker<T> tracker) {
            ArrayList<ForgeTimings<T>> list = new ArrayList<ForgeTimings<T>>();
            list.addAll((Collection<ForgeTimings<T>>)tracker.getTimingData());
            list.sort(Comparator.comparingDouble(ForgeTimings::getAverageTimings));
            Collections.reverse(list);
            return list;
        }

        private static <T> int execute(CommandSourceStack source, TimeTracker<T> tracker, Function<ForgeTimings<T>, Component> toString) throws CommandRuntimeException {
            List<ForgeTimings<T>> timingsList = TrackResults.getSortedTimings(tracker);
            if (timingsList.isEmpty()) {
                source.m_288197_(() -> Component.m_237115_((String)"commands.forge.tracking.no_data"), true);
            } else {
                timingsList.stream().filter(timings -> timings.getObject().get() != null).limit(10L).forEach(timings -> source.m_288197_(() -> (Component)toString.apply((ForgeTimings)timings), true));
            }
            return 0;
        }
    }
}

