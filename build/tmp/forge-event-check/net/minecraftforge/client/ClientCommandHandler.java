/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.CommandRuntimeException
 *  net.minecraft.commands.CommandSource
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.SharedSuggestionProvider
 *  net.minecraft.commands.synchronization.SuggestionProviders
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentUtils
 *  net.minecraft.network.chat.HoverEvent
 *  net.minecraft.network.chat.HoverEvent$Action
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.flag.FeatureFlagSet
 *  net.minecraftforge.eventbus.api.Event
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.IdentityHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraftforge.client.ClientCommandSourceStack;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.server.command.CommandHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

public class ClientCommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static CommandDispatcher<CommandSourceStack> commands = null;

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(ClientCommandHandler::handleClientPlayerLogin);
    }

    private static void handleClientPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientPacketListener connection = event.getPlayer().f_108617_;
        connection.f_104899_ = ClientCommandHandler.mergeServerCommands((CommandDispatcher<SharedSuggestionProvider>)new CommandDispatcher(), CommandBuildContext.m_255418_((HolderLookup.Provider)connection.m_105152_(), (FeatureFlagSet)connection.m_247016_()));
    }

    @ApiStatus.Internal
    public static CommandDispatcher<SharedSuggestionProvider> mergeServerCommands(CommandDispatcher<SharedSuggestionProvider> serverCommands, CommandBuildContext buildContext) {
        CommandDispatcher commandsTemp = new CommandDispatcher();
        MinecraftForge.EVENT_BUS.post((Event)new RegisterClientCommandsEvent((CommandDispatcher<CommandSourceStack>)commandsTemp, buildContext));
        commands = new CommandDispatcher();
        ClientCommandHandler.copy(commandsTemp.getRoot(), commands.getRoot());
        RootCommandNode serverCommandsRoot = serverCommands.getRoot();
        CommandDispatcher newServerCommands = new CommandDispatcher();
        ClientCommandHandler.copy(serverCommandsRoot, newServerCommands.getRoot());
        CommandHelper.mergeCommandNode(commands.getRoot(), newServerCommands.getRoot(), new IdentityHashMap(), ClientCommandHandler.getSource(), context -> 0, suggestions -> {
            SuggestionProvider suggestionProvider = SuggestionProviders.m_121664_((SuggestionProvider)suggestions);
            if (suggestionProvider == SuggestionProviders.f_121641_) {
                suggestionProvider = (context, builder) -> {
                    ClientCommandSourceStack source = ClientCommandHandler.getSource();
                    StringReader reader = new StringReader(context.getInput());
                    if (reader.canRead() && reader.peek() == '/') {
                        reader.skip();
                    }
                    ParseResults parse = commands.parse(reader, (Object)source);
                    return commands.getCompletionSuggestions(parse);
                };
            }
            return suggestionProvider;
        });
        return newServerCommands;
    }

    public static CommandDispatcher<CommandSourceStack> getDispatcher() {
        return commands;
    }

    public static ClientCommandSourceStack getSource() {
        LocalPlayer player = Minecraft.m_91087_().f_91074_;
        return new ClientCommandSourceStack((CommandSource)player, player.m_20182_(), player.m_20155_(), player.m_8088_(), player.m_7755_().getString(), player.m_5446_(), (Entity)player);
    }

    private static <S> void copy(CommandNode<S> sourceNode, CommandNode<S> resultNode) {
        IdentityHashMap<Object, Object> newNodes = new IdentityHashMap<Object, Object>();
        newNodes.put(sourceNode, resultNode);
        for (CommandNode child : sourceNode.getChildren()) {
            CommandNode copy = newNodes.computeIfAbsent(child, innerChild -> {
                ArgumentBuilder builder = innerChild.createBuilder();
                CommandNode innerCopy = builder.build();
                ClientCommandHandler.copy(innerChild, innerCopy);
                return innerCopy;
            });
            resultNode.addChild(copy);
        }
    }

    public static boolean runCommand(String command) {
        StringReader reader = new StringReader(command);
        ClientCommandSourceStack source = ClientCommandHandler.getSource();
        try {
            commands.execute(reader, (Object)source);
        }
        catch (CommandRuntimeException execution) {
            Minecraft.m_91087_().f_91074_.m_213846_((Component)Component.m_237113_((String)"").m_7220_(execution.m_79226_()).m_130940_(ChatFormatting.RED));
        }
        catch (CommandSyntaxException syntax) {
            if (syntax.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand() || syntax.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()) {
                return false;
            }
            Minecraft.m_91087_().f_91074_.m_213846_((Component)Component.m_237113_((String)"").m_7220_(ComponentUtils.m_130729_((Message)syntax.getRawMessage())).m_130940_(ChatFormatting.RED));
            if (syntax.getInput() != null && syntax.getCursor() >= 0) {
                int position = Math.min(syntax.getInput().length(), syntax.getCursor());
                MutableComponent details = Component.m_237113_((String)"").m_130940_(ChatFormatting.GRAY).m_130938_(style -> style.m_131142_(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, reader.getString())));
                if (position > 10) {
                    details.m_130946_("...");
                }
                details.m_130946_(syntax.getInput().substring(Math.max(0, position - 10), position));
                if (position < syntax.getInput().length()) {
                    details.m_7220_((Component)Component.m_237113_((String)syntax.getInput().substring(position)).m_130944_(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.UNDERLINE}));
                }
                details.m_7220_((Component)Component.m_237115_((String)"command.context.here").m_130944_(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.ITALIC}));
                Minecraft.m_91087_().f_91074_.m_213846_((Component)Component.m_237113_((String)"").m_7220_((Component)details).m_130940_(ChatFormatting.RED));
            }
        }
        catch (Exception generic) {
            MutableComponent message = Component.m_237113_((String)(generic.getMessage() == null ? generic.getClass().getName() : generic.getMessage()));
            Minecraft.m_91087_().f_91074_.m_213846_((Component)Component.m_237115_((String)"command.failed").m_130940_(ChatFormatting.RED).m_130938_(style -> style.m_131144_(new HoverEvent(HoverEvent.Action.f_130831_, (Object)message))));
            LOGGER.error("Error executing client command \"{}\"", (Object)command, (Object)generic);
        }
        return true;
    }
}

