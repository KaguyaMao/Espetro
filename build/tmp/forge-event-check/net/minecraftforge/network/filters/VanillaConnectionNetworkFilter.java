/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.tree.RootCommandNode
 *  com.mojang.logging.LogUtils
 *  io.netty.channel.ChannelHandler$Sharable
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.synchronization.ArgumentTypeInfo
 *  net.minecraft.commands.synchronization.ArgumentTypeInfos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.data.registries.VanillaRegistries
 *  net.minecraft.gametest.framework.TestClassNameArgument
 *  net.minecraft.gametest.framework.TestFunctionArgument
 *  net.minecraft.network.Connection
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundCommandsPacket
 *  net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket
 *  net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagNetworkSerialization$NetworkPayload
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 */
package net.minecraftforge.network.filters;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import io.netty.channel.ChannelHandler;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.gametest.framework.TestClassNameArgument;
import net.minecraft.gametest.framework.TestFunctionArgument;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.filters.CommandTreeCleaner;
import net.minecraftforge.network.filters.VanillaPacketFilter;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@ChannelHandler.Sharable
public class VanillaConnectionNetworkFilter
extends VanillaPacketFilter {
    private static final Logger LOGGER = LogUtils.getLogger();

    public VanillaConnectionNetworkFilter() {
        super((Map<Class<? extends Packet<?>>, BiConsumer<Packet<?>, List<? super Packet<?>>>>)ImmutableMap.builder().put(VanillaConnectionNetworkFilter.handler(ClientboundUpdateAttributesPacket.class, VanillaConnectionNetworkFilter::filterEntityProperties)).put(VanillaConnectionNetworkFilter.handler(ClientboundCommandsPacket.class, VanillaConnectionNetworkFilter::filterCommandList)).put(VanillaConnectionNetworkFilter.handler(ClientboundUpdateTagsPacket.class, VanillaConnectionNetworkFilter::filterCustomTagTypes)).build());
    }

    @Override
    protected boolean isNecessary(Connection manager) {
        return NetworkHooks.isVanillaConnection(manager);
    }

    @NotNull
    private static ClientboundUpdateAttributesPacket filterEntityProperties(ClientboundUpdateAttributesPacket msg) {
        ClientboundUpdateAttributesPacket newPacket = new ClientboundUpdateAttributesPacket(msg.m_133588_(), Collections.emptyList());
        msg.m_133591_().stream().filter(snapshot -> {
            ResourceLocation key = ForgeRegistries.ATTRIBUTES.getKey(snapshot.m_133601_());
            return key != null && key.m_135827_().equals("minecraft");
        }).forEach(snapshot -> newPacket.m_133591_().add(snapshot));
        return newPacket;
    }

    @NotNull
    private static ClientboundCommandsPacket filterCommandList(ClientboundCommandsPacket packet) {
        CommandBuildContext commandBuildContext = Commands.m_255082_((HolderLookup.Provider)VanillaRegistries.m_255371_());
        RootCommandNode root = packet.m_237624_(commandBuildContext);
        RootCommandNode newRoot = CommandTreeCleaner.cleanArgumentTypes(root, argType -> {
            if (argType instanceof TestFunctionArgument || argType instanceof TestClassNameArgument) {
                return false;
            }
            ArgumentTypeInfo info = ArgumentTypeInfos.m_235382_((ArgumentType)argType);
            ResourceLocation id = BuiltInRegistries.f_256979_.m_7981_((Object)info);
            return id != null && (id.m_135827_().equals("minecraft") || id.m_135827_().equals("brigadier"));
        });
        return new ClientboundCommandsPacket(newRoot);
    }

    private static ClientboundUpdateTagsPacket filterCustomTagTypes(ClientboundUpdateTagsPacket packet) {
        Map<ResourceKey, TagNetworkSerialization.NetworkPayload> tags = packet.m_179482_().entrySet().stream().filter(e -> VanillaConnectionNetworkFilter.isVanillaRegistry(((ResourceKey)e.getKey()).m_135782_())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new ClientboundUpdateTagsPacket(tags);
    }

    private static boolean isVanillaRegistry(ResourceLocation location) {
        return RegistryManager.getVanillaRegistryKeys().contains(location) || VanillaRegistries.DATAPACK_REGISTRY_KEYS.stream().anyMatch(k -> k.m_135782_().equals((Object)location));
    }
}

