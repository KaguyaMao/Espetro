/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.commands.CommandSourceStack
 */
package dev.latvian.mods.kubejs.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.latvian.mods.kubejs.command.CommandRegistryEventJS;
import net.minecraft.commands.CommandSourceStack;

public interface ArgumentTypeWrapper {
    public ArgumentType<?> create(CommandRegistryEventJS var1);

    public Object getResult(CommandContext<CommandSourceStack> var1, String var2) throws CommandSyntaxException;
}

