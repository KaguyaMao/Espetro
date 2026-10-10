/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands$CommandSelection
 *  net.minecraftforge.eventbus.api.Event
 */
package net.minecraftforge.event;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.eventbus.api.Event;

public class RegisterCommandsEvent
extends Event {
    private final CommandDispatcher<CommandSourceStack> dispatcher;
    private final Commands.CommandSelection environment;
    private final CommandBuildContext context;

    public RegisterCommandsEvent(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection environment, CommandBuildContext context) {
        this.dispatcher = dispatcher;
        this.environment = environment;
        this.context = context;
    }

    public CommandDispatcher<CommandSourceStack> getDispatcher() {
        return this.dispatcher;
    }

    public Commands.CommandSelection getCommandSelection() {
        return this.environment;
    }

    public CommandBuildContext getBuildContext() {
        return this.context;
    }
}

