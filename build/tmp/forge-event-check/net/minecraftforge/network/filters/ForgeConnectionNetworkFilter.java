/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  io.netty.channel.ChannelHandler$Sharable
 *  net.minecraft.network.Connection
 *  net.minecraft.network.ConnectionProtocol
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.PacketFlow
 *  net.minecraft.network.protocol.game.ClientboundLoginPacket
 *  net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket
 *  net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket
 *  net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.network.filters;

import com.google.common.collect.ImmutableMap;
import io.netty.channel.ChannelHandler;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import net.minecraftforge.network.filters.VanillaPacketFilter;
import net.minecraftforge.network.filters.VanillaPacketSplitter;
import org.jetbrains.annotations.Nullable;

@ChannelHandler.Sharable
public class ForgeConnectionNetworkFilter
extends VanillaPacketFilter {
    public ForgeConnectionNetworkFilter(@Nullable Connection manager) {
        super(ForgeConnectionNetworkFilter.buildHandlers(manager));
    }

    private static Map<Class<? extends Packet<?>>, BiConsumer<Packet<?>, List<? super Packet<?>>>> buildHandlers(@Nullable Connection manager) {
        VanillaPacketSplitter.RemoteCompatibility compatibility;
        VanillaPacketSplitter.RemoteCompatibility remoteCompatibility = compatibility = manager == null ? VanillaPacketSplitter.RemoteCompatibility.ABSENT : VanillaPacketSplitter.getRemoteCompatibility(manager);
        if (compatibility == VanillaPacketSplitter.RemoteCompatibility.ABSENT) {
            return ImmutableMap.of();
        }
        ImmutableMap.Builder builder = ImmutableMap.builder().put(ClientboundUpdateRecipesPacket.class, ForgeConnectionNetworkFilter::splitPacket).put(ClientboundUpdateTagsPacket.class, ForgeConnectionNetworkFilter::splitPacket).put(ClientboundUpdateAdvancementsPacket.class, ForgeConnectionNetworkFilter::splitPacket).put(ClientboundLoginPacket.class, ForgeConnectionNetworkFilter::splitPacket);
        return builder.build();
    }

    @Override
    protected boolean isNecessary(Connection manager) {
        return !manager.m_129531_() && VanillaPacketSplitter.isRemoteCompatible(manager);
    }

    private static void splitPacket(Packet<?> packet, List<? super Packet<?>> out) {
        VanillaPacketSplitter.appendPackets(ConnectionProtocol.PLAY, PacketFlow.CLIENTBOUND, packet, out);
    }
}

