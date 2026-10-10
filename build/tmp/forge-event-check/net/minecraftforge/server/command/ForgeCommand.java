/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.commands.CommandSourceStack
 */
package net.minecraftforge.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.server.command.DimensionsCommand;
import net.minecraftforge.server.command.EntityCommand;
import net.minecraftforge.server.command.GenerateCommand;
import net.minecraftforge.server.command.ModListCommand;
import net.minecraftforge.server.command.TPSCommand;
import net.minecraftforge.server.command.TagsCommand;
import net.minecraftforge.server.command.TrackCommand;

public class ForgeCommand {
    public ForgeCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)LiteralArgumentBuilder.literal((String)"forge").then(TPSCommand.register())).then(TrackCommand.register())).then(EntityCommand.register())).then(GenerateCommand.register())).then(DimensionsCommand.register())).then(ModListCommand.register())).then(TagsCommand.register()));
    }
}

