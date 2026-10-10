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
 *  net.minecraft.server.level.ServerPlayer
 */
package cc.sighs.auratip.command;

import cc.sighs.auratip.network.OpenEditorPacket;
import cc.sighs.oelib.registry.extra.CommandRegister;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class AuraTipEditorCommand {
    private AuraTipEditorCommand() {
    }

    public static void register() {
        CommandRegister.registerServer(AuraTipEditorCommand::registerCommand);
    }

    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection environment) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"auratip").requires(source -> source.m_6761_(2))).then(Commands.m_82127_((String)"editor").executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).m_81375_();
            new OpenEditorPacket("tip").sendTo(player);
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)"AuraTip editor opened on client."), false);
            return 1;
        })));
    }
}

