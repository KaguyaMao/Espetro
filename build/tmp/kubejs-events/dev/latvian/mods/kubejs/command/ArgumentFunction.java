/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.commands.CommandSourceStack
 */
package dev.latvian.mods.kubejs.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;

interface ArgumentFunction<U> {
    public U getResult(CommandContext<CommandSourceStack> var1, String var2) throws CommandSyntaxException;
}

