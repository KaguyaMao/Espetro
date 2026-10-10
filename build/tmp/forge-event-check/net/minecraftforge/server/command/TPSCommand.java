/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.DimensionArgument
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 */
package net.minecraftforge.server.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.text.DecimalFormat;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

class TPSCommand {
    private static final DecimalFormat TIME_FORMATTER = new DecimalFormat("########0.000");
    private static final long[] UNLOADED = new long[]{0L};

    TPSCommand() {
    }

    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"tps").requires(cs -> cs.m_6761_(0))).then(Commands.m_82129_((String)"dim", (ArgumentType)DimensionArgument.m_88805_()).executes(ctx -> TPSCommand.sendTime((CommandSourceStack)ctx.getSource(), DimensionArgument.m_88808_((CommandContext)ctx, (String)"dim"))))).executes(ctx -> {
            for (ServerLevel dim : ((CommandSourceStack)ctx.getSource()).m_81377_().m_129785_()) {
                TPSCommand.sendTime((CommandSourceStack)ctx.getSource(), dim);
            }
            double meanTickTime = (double)TPSCommand.mean(((CommandSourceStack)ctx.getSource()).m_81377_().f_129748_) * 1.0E-6;
            double meanTPS = Math.min(1000.0 / meanTickTime, 20.0);
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237110_((String)"commands.forge.tps.summary.all", (Object[])new Object[]{TIME_FORMATTER.format(meanTickTime), TIME_FORMATTER.format(meanTPS)}), false);
            return 0;
        });
    }

    private static int sendTime(CommandSourceStack cs, ServerLevel dim) throws CommandSyntaxException {
        long[] times = cs.m_81377_().getTickTime(dim.m_46472_());
        if (times == null) {
            times = UNLOADED;
        }
        Registry reg = cs.m_5894_().m_175515_(Registries.f_256787_);
        double worldTickTime = (double)TPSCommand.mean(times) * 1.0E-6;
        double worldTPS = Math.min(1000.0 / worldTickTime, 20.0);
        cs.m_288197_(() -> Component.m_237110_((String)"commands.forge.tps.summary.named", (Object[])new Object[]{dim.m_46472_().m_135782_().toString(), reg.m_7981_((Object)dim.m_6042_()), TIME_FORMATTER.format(worldTickTime), TIME_FORMATTER.format(worldTPS)}), false);
        return 1;
    }

    private static long mean(long[] values) {
        long sum = 0L;
        for (long v : values) {
            sum += v;
        }
        return sum / (long)values.length;
    }
}

