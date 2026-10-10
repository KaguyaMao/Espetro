/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.commands.arguments.selector.EntitySelector
 *  net.minecraft.commands.arguments.selector.EntitySelectorParser
 *  net.minecraft.network.chat.Component
 */
package net.minecraftforge.common.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;

public interface IEntitySelectorType {
    public EntitySelector build(EntitySelectorParser var1) throws CommandSyntaxException;

    public Component getSuggestionTooltip();
}

