/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cc.sighs.oelib.registry.extra.CommandRegister
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.Commands$CommandSelection
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 */
package cc.sighs.auratip.command;

import cc.sighs.auratip.api.tip.TipServer;
import cc.sighs.auratip.dev.DevEnvironment;
import cc.sighs.oelib.registry.extra.CommandRegister;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Map;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ShowTipCommand {
    public static void register() {
        if (DevEnvironment.isDev()) {
            CommandRegister.registerServer(ShowTipCommand::registerCommand);
        }
    }

    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection environment) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"showtip").requires(source -> source.m_6761_(2))).executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).m_81375_();
            TipServer.trigger(new ResourceLocation("auratip", "showtip_command"), player, ShowTipCommand.buildVariables(player));
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)"\u5df2\u5c1d\u8bd5\u89e6\u53d1 Tip \u6f14\u793a\u6848\u4f8b"), true);
            return 1;
        }));
    }

    private static Map<String, Object> buildVariables(ServerPlayer player) {
        return Map.of("player", player.m_5446_(), "x", player.m_146903_(), "y", player.m_146904_(), "z", player.m_146907_());
    }
}

