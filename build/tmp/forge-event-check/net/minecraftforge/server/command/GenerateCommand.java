/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.CommandRuntimeException
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.DimensionArgument
 *  net.minecraft.commands.arguments.coordinates.BlockPosArgument
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 */
package net.minecraftforge.server.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.WorldWorkerManager;
import net.minecraftforge.server.command.ChunkGenWorker;

class GenerateCommand {
    GenerateCommand() {
    }

    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)Commands.m_82127_((String)"generate").requires(cs -> cs.m_6761_(4))).then(Commands.m_82129_((String)"pos", (ArgumentType)BlockPosArgument.m_118239_()).then(((RequiredArgumentBuilder)Commands.m_82129_((String)"count", (ArgumentType)IntegerArgumentType.integer((int)1)).then(((RequiredArgumentBuilder)Commands.m_82129_((String)"dim", (ArgumentType)DimensionArgument.m_88805_()).then(Commands.m_82129_((String)"interval", (ArgumentType)IntegerArgumentType.integer()).executes(ctx -> GenerateCommand.execute((CommandSourceStack)ctx.getSource(), BlockPosArgument.m_174395_((CommandContext)ctx, (String)"pos"), GenerateCommand.getInt((CommandContext<CommandSourceStack>)ctx, "count"), DimensionArgument.m_88808_((CommandContext)ctx, (String)"dim"), GenerateCommand.getInt((CommandContext<CommandSourceStack>)ctx, "interval"))))).executes(ctx -> GenerateCommand.execute((CommandSourceStack)ctx.getSource(), BlockPosArgument.m_174395_((CommandContext)ctx, (String)"pos"), GenerateCommand.getInt((CommandContext<CommandSourceStack>)ctx, "count"), DimensionArgument.m_88808_((CommandContext)ctx, (String)"dim"), -1)))).executes(ctx -> GenerateCommand.execute((CommandSourceStack)ctx.getSource(), BlockPosArgument.m_174395_((CommandContext)ctx, (String)"pos"), GenerateCommand.getInt((CommandContext<CommandSourceStack>)ctx, "count"), ((CommandSourceStack)ctx.getSource()).m_81372_(), -1))));
    }

    private static int getInt(CommandContext<CommandSourceStack> ctx, String name) {
        return IntegerArgumentType.getInteger(ctx, (String)name);
    }

    private static int execute(CommandSourceStack source, BlockPos pos, int count, ServerLevel dim, int interval) throws CommandRuntimeException {
        BlockPos chunkpos = new BlockPos(pos.m_123341_() >> 4, 0, pos.m_123343_() >> 4);
        ChunkGenWorker worker = new ChunkGenWorker(source, chunkpos, count, dim, interval);
        source.m_288197_(() -> worker.getStartMessage(source), true);
        WorldWorkerManager.addWorker(worker);
        return 0;
    }
}

