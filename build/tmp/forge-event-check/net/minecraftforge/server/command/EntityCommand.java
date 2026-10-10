/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.SharedSuggestionProvider
 *  net.minecraft.commands.arguments.DimensionArgument
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  org.apache.commons.lang3.tuple.MutablePair
 *  org.apache.commons.lang3.tuple.Pair
 */
package net.minecraftforge.server.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

class EntityCommand {
    EntityCommand() {
    }

    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.m_82127_((String)"entity").then(EntityListCommand.register());
    }

    private static class EntityListCommand {
        private static final SimpleCommandExceptionType INVALID_FILTER = new SimpleCommandExceptionType((Message)Component.m_237115_((String)"commands.forge.entity.list.invalid"));
        private static final DynamicCommandExceptionType INVALID_DIMENSION = new DynamicCommandExceptionType(dim -> Component.m_237110_((String)"commands.forge.entity.list.invalidworld", (Object[])new Object[]{dim}));
        private static final SimpleCommandExceptionType NO_ENTITIES = new SimpleCommandExceptionType((Message)Component.m_237115_((String)"commands.forge.entity.list.none"));

        private EntityListCommand() {
        }

        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"list").requires(cs -> cs.m_6761_(2))).then(((RequiredArgumentBuilder)Commands.m_82129_((String)"filter", (ArgumentType)StringArgumentType.string()).suggests((ctx, builder) -> SharedSuggestionProvider.m_82981_(ForgeRegistries.ENTITY_TYPES.getKeys().stream().map(ResourceLocation::toString).map(StringArgumentType::escapeIfRequired), (SuggestionsBuilder)builder)).then(Commands.m_82129_((String)"dim", (ArgumentType)DimensionArgument.m_88805_()).executes(ctx -> EntityListCommand.execute((CommandSourceStack)ctx.getSource(), StringArgumentType.getString((CommandContext)ctx, (String)"filter"), (ResourceKey<Level>)DimensionArgument.m_88808_((CommandContext)ctx, (String)"dim").m_46472_())))).executes(ctx -> EntityListCommand.execute((CommandSourceStack)ctx.getSource(), StringArgumentType.getString((CommandContext)ctx, (String)"filter"), (ResourceKey<Level>)((CommandSourceStack)ctx.getSource()).m_81372_().m_46472_())))).executes(ctx -> EntityListCommand.execute((CommandSourceStack)ctx.getSource(), "*", (ResourceKey<Level>)((CommandSourceStack)ctx.getSource()).m_81372_().m_46472_()));
        }

        private static int execute(CommandSourceStack sender, String filter, ResourceKey<Level> dim) throws CommandSyntaxException {
            String cleanFilter = filter.replace("?", ".?").replace("*", ".*?");
            Set names = ForgeRegistries.ENTITY_TYPES.getKeys().stream().filter(n -> n.toString().matches(cleanFilter)).collect(Collectors.toSet());
            if (names.isEmpty()) {
                throw INVALID_FILTER.create();
            }
            ServerLevel level = sender.m_81377_().m_129880_(dim);
            if (level == null) {
                throw INVALID_DIMENSION.create(dim);
            }
            HashMap list = Maps.newHashMap();
            level.m_142646_().m_142273_().forEach(e -> {
                MutablePair info = list.computeIfAbsent(ForgeRegistries.ENTITY_TYPES.getKey(e.m_6095_()), k -> MutablePair.of((Object)0, (Object)Maps.newHashMap()));
                ChunkPos chunk = new ChunkPos(e.m_20183_());
                MutablePair mutablePair = info;
                Integer n = (Integer)mutablePair.left;
                mutablePair.left = (Integer)mutablePair.left + 1;
                ((Map)info.right).put(chunk, ((Map)info.right).getOrDefault(chunk, 0) + 1);
            });
            if (names.size() == 1) {
                ResourceLocation name = (ResourceLocation)names.iterator().next();
                Pair info = (Pair)list.get(name);
                if (info == null) {
                    throw NO_ENTITIES.create();
                }
                sender.m_288197_(() -> Component.m_237110_((String)"commands.forge.entity.list.single.header", (Object[])new Object[]{name, info.getLeft()}), false);
                ArrayList toSort = new ArrayList();
                toSort.addAll(((Map)info.getRight()).entrySet());
                toSort.sort((a, b) -> {
                    if (Objects.equals(a.getValue(), b.getValue())) {
                        return ((ChunkPos)a.getKey()).toString().compareTo(((ChunkPos)b.getKey()).toString());
                    }
                    return (Integer)b.getValue() - (Integer)a.getValue();
                });
                long limit = 10L;
                for (Map.Entry entry : toSort) {
                    if (limit-- == 0L) break;
                    sender.m_288197_(() -> Component.m_237113_((String)("  " + String.valueOf(e2.getValue()) + ": " + ((ChunkPos)e.getKey()).f_45578_ + ", " + ((ChunkPos)e.getKey()).f_45579_)), false);
                }
                return toSort.size();
            }
            ArrayList info = new ArrayList();
            list.forEach((key, value) -> {
                if (names.contains(key)) {
                    Pair of = Pair.of((Object)key, (Object)((Integer)value.left));
                    info.add(of);
                }
            });
            info.sort((a, b) -> {
                if (Objects.equals(a.getRight(), b.getRight())) {
                    return ((ResourceLocation)a.getKey()).toString().compareTo(((ResourceLocation)b.getKey()).toString());
                }
                return (Integer)b.getRight() - (Integer)a.getRight();
            });
            if (info.size() == 0) {
                throw NO_ENTITIES.create();
            }
            int count = info.stream().mapToInt(Pair::getRight).sum();
            sender.m_288197_(() -> Component.m_237110_((String)"commands.forge.entity.list.multiple.header", (Object[])new Object[]{count}), false);
            info.forEach(e -> sender.m_288197_(() -> Component.m_237113_((String)("  " + String.valueOf(e.getValue()) + ": " + String.valueOf(e.getKey()))), false));
            return info.size();
        }
    }
}

