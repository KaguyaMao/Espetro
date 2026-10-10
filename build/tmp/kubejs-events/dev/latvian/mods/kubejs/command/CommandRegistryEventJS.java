/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.Commands$CommandSelection
 *  net.minecraft.commands.SharedSuggestionProvider
 */
package dev.latvian.mods.kubejs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.latvian.mods.kubejs.command.ArgumentTypeWrappers;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.util.ClassWrapper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

public class CommandRegistryEventJS
extends EventJS {
    public final CommandDispatcher<CommandSourceStack> dispatcher;
    public final CommandBuildContext context;
    public final Commands.CommandSelection selection;

    public CommandRegistryEventJS(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        this.dispatcher = dispatcher;
        this.context = context;
        this.selection = selection;
    }

    public boolean isForSinglePlayer() {
        return this.selection.f_82144_;
    }

    public boolean isForMultiPlayer() {
        return this.selection.f_82145_;
    }

    public CommandBuildContext getRegistry() {
        return this.context;
    }

    public LiteralCommandNode<CommandSourceStack> register(LiteralArgumentBuilder<CommandSourceStack> command) {
        return this.dispatcher.register(command);
    }

    public ClassWrapper<Commands> getCommands() {
        return new ClassWrapper<Commands>(Commands.class);
    }

    public ClassWrapper<ArgumentTypeWrappers> getArguments() {
        return new ClassWrapper<ArgumentTypeWrappers>(ArgumentTypeWrappers.class);
    }

    public ClassWrapper<SharedSuggestionProvider> getBuiltinSuggestions() {
        return new ClassWrapper<SharedSuggestionProvider>(SharedSuggestionProvider.class);
    }
}

