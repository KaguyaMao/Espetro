/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 */
package net.minecraftforge.network.filters;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

class CommandTreeCleaner {
    CommandTreeCleaner() {
    }

    public static <S> RootCommandNode<S> cleanArgumentTypes(RootCommandNode<S> root, Predicate<ArgumentType<?>> argumentTypeFilter) {
        Predicate<CommandNode<?>> nodeFilter = node -> !(node instanceof ArgumentCommandNode) || argumentTypeFilter.test(((ArgumentCommandNode)node).getType());
        return (RootCommandNode)CommandTreeCleaner.processCommandNode(root, nodeFilter, new HashMap<CommandNode<S>, CommandNode<S>>());
    }

    private static <S> CommandNode<S> processCommandNode(CommandNode<S> node, Predicate<CommandNode<?>> nodeFilter, Map<CommandNode<S>, CommandNode<S>> newNodes) {
        CommandNode<S> existingNode = newNodes.get(node);
        if (existingNode == null) {
            CommandNode<S> newNode = CommandTreeCleaner.cloneNode(node, nodeFilter, newNodes);
            newNodes.put(node, newNode);
            node.getChildren().stream().filter(nodeFilter).map(child -> CommandTreeCleaner.processCommandNode(child, nodeFilter, newNodes)).forEach(arg_0 -> newNode.addChild(arg_0));
            return newNode;
        }
        return existingNode;
    }

    private static <S> CommandNode<S> cloneNode(CommandNode<S> node, Predicate<CommandNode<?>> nodeFilter, Map<CommandNode<S>, CommandNode<S>> newNodes) {
        if (node instanceof RootCommandNode) {
            return new RootCommandNode();
        }
        ArgumentBuilder builder = node.createBuilder();
        if (node.getRedirect() != null) {
            if (nodeFilter.test(node.getRedirect())) {
                builder.forward(CommandTreeCleaner.processCommandNode(node.getRedirect(), nodeFilter, newNodes), node.getRedirectModifier(), node.isFork());
            } else {
                builder.redirect(null);
            }
        }
        return builder.build();
    }
}

