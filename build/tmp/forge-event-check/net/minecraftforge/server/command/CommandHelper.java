/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 */
package net.minecraftforge.server.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Map;
import java.util.function.Function;

public final class CommandHelper {
    private CommandHelper() {
    }

    public static <S, T> void mergeCommandNode(CommandNode<S> sourceNode, CommandNode<T> resultNode, Map<CommandNode<S>, CommandNode<T>> sourceToResult, S canUse, Command<T> execute, Function<SuggestionProvider<S>, SuggestionProvider<T>> sourceToResultSuggestion) {
        sourceToResult.put(sourceNode, resultNode);
        for (CommandNode sourceChild : sourceNode.getChildren()) {
            if (!sourceChild.canUse(canUse)) continue;
            resultNode.addChild(CommandHelper.toResult(sourceChild, sourceToResult, canUse, execute, sourceToResultSuggestion));
        }
    }

    private static <S, T> CommandNode<T> toResult(CommandNode<S> sourceNode, Map<CommandNode<S>, CommandNode<T>> sourceToResult, S canUse, Command<T> execute, Function<SuggestionProvider<S>, SuggestionProvider<T>> sourceToResultSuggestion) {
        LiteralArgumentBuilder resultBuilder;
        if (sourceToResult.containsKey(sourceNode)) {
            return sourceToResult.get(sourceNode);
        }
        if (sourceNode instanceof ArgumentCommandNode) {
            ArgumentCommandNode sourceArgument = (ArgumentCommandNode)sourceNode;
            RequiredArgumentBuilder resultArgumentBuilder = RequiredArgumentBuilder.argument((String)sourceArgument.getName(), (ArgumentType)sourceArgument.getType());
            if (sourceArgument.getCustomSuggestions() != null) {
                resultArgumentBuilder.suggests(sourceToResultSuggestion.apply(sourceArgument.getCustomSuggestions()));
            }
            resultBuilder = resultArgumentBuilder;
        } else if (sourceNode instanceof LiteralCommandNode) {
            LiteralCommandNode sourceLiteral = (LiteralCommandNode)sourceNode;
            resultBuilder = LiteralArgumentBuilder.literal((String)sourceLiteral.getLiteral());
        } else {
            if (sourceNode instanceof RootCommandNode) {
                RootCommandNode resultNode = new RootCommandNode();
                CommandHelper.mergeCommandNode(sourceNode, resultNode, sourceToResult, canUse, execute, sourceToResultSuggestion);
                return resultNode;
            }
            throw new IllegalStateException("Node type " + String.valueOf(sourceNode) + " is not a standard node type");
        }
        if (sourceNode.getCommand() != null) {
            resultBuilder.executes(execute);
        }
        if (sourceNode.getRedirect() != null) {
            resultBuilder.redirect(CommandHelper.toResult(sourceNode.getRedirect(), sourceToResult, canUse, execute, sourceToResultSuggestion));
        }
        CommandNode resultNode = resultBuilder.build();
        CommandHelper.mergeCommandNode(sourceNode, resultNode, sourceToResult, canUse, execute, sourceToResultSuggestion);
        return resultNode;
    }
}

