/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.ChatFormatting
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Component
 *  net.minecraftforge.fml.config.ConfigTracker
 *  net.minecraftforge.fml.config.ModConfig$Type
 */
package net.minecraftforge.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.io.File;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.server.command.EnumArgument;
import net.minecraftforge.server.command.ModIdArgument;

public class ConfigCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)Commands.m_82127_((String)"config").then(ShowFile.register()));
    }

    public static class ShowFile {
        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return ((LiteralArgumentBuilder)Commands.m_82127_((String)"showfile").requires(cs -> cs.m_6761_(0))).then(Commands.m_82129_((String)"mod", (ArgumentType)ModIdArgument.modIdArgument()).then(Commands.m_82129_((String)"type", EnumArgument.enumArgument(ModConfig.Type.class)).executes(ShowFile::showFile)));
        }

        private static int showFile(CommandContext<CommandSourceStack> context) {
            ModConfig.Type type;
            String modId = (String)context.getArgument("mod", String.class);
            String configFileName = ConfigTracker.INSTANCE.getConfigFileName(modId, type = (ModConfig.Type)context.getArgument("type", ModConfig.Type.class));
            if (configFileName != null) {
                File f = new File(configFileName);
                ((CommandSourceStack)context.getSource()).m_288197_(() -> Component.m_237110_((String)"commands.config.getwithtype", (Object[])new Object[]{modId, type, Component.m_237113_((String)f.getName()).m_130940_(ChatFormatting.UNDERLINE).m_130938_(style -> style.m_131142_(new ClickEvent(ClickEvent.Action.OPEN_FILE, f.getAbsolutePath())))}), true);
            } else {
                ((CommandSourceStack)context.getSource()).m_288197_(() -> Component.m_237110_((String)"commands.config.noconfig", (Object[])new Object[]{modId, type}), true);
            }
            return 0;
        }
    }
}

