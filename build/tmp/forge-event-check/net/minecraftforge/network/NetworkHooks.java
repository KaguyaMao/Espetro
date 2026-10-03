/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandlerContext
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.Connection
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.protocol.handshake.ClientIntentionPacket
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.network.ServerLoginPacketListenerImpl
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.config.ConfigTracker
 *  net.minecraftforge.fml.util.thread.EffectiveSide
 *  org.apache.commons.lang3.tuple.Pair
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.network.ConnectionData;
import net.minecraftforge.network.ConnectionType;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.ICustomPacket;
import net.minecraftforge.network.MCRegisterPacketHandler;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkInstance;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.filters.NetworkFilters;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class NetworkHooks {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void init() {
        LOGGER.debug("Loading Network data for FML net version: {}", (Object)NetworkConstants.init());
    }

    public static String getFMLVersion(String ip) {
        return ip.contains("\u0000") ? (Objects.equals(ip.split("\u0000")[1], "FML3") ? "FML3" : ip.split("\u0000")[1]) : "NONE";
    }

    public static ConnectionType getConnectionType(Supplier<Connection> connection) {
        return NetworkHooks.getConnectionType(connection.get().channel());
    }

    public static ConnectionType getConnectionType(ChannelHandlerContext context) {
        return NetworkHooks.getConnectionType(context.channel());
    }

    private static ConnectionType getConnectionType(Channel channel) {
        return ConnectionType.forVersionFlag((String)channel.attr(NetworkConstants.FML_NETVERSION).get());
    }

    public static Packet<ClientGamePacketListener> getEntitySpawningPacket(Entity entity) {
        return NetworkConstants.playChannel.toVanillaPacket(new PlayMessages.SpawnEntity(entity), NetworkDirection.PLAY_TO_CLIENT);
    }

    public static boolean onCustomPayload(ICustomPacket<?> packet, Connection manager) {
        return NetworkRegistry.findTarget(packet.getName()).filter(ni -> NetworkHooks.validateSideForProcessing(packet, ni, manager)).map(ni -> ni.dispatch(packet.getDirection(), packet, manager)).orElse(Boolean.FALSE);
    }

    private static boolean validateSideForProcessing(ICustomPacket<?> packet, NetworkInstance ni, Connection manager) {
        if (packet.getDirection().getReceptionSide() != EffectiveSide.get()) {
            manager.m_129507_((Component)Component.m_237113_((String)"Illegal packet received, terminating connection"));
            return false;
        }
        return true;
    }

    public static void validatePacketDirection(NetworkDirection packetDirection, Optional<NetworkDirection> expectedDirection, Connection connection) {
        if (packetDirection != expectedDirection.orElse(packetDirection)) {
            connection.m_129507_((Component)Component.m_237113_((String)"Illegal packet received, terminating connection"));
            throw new IllegalStateException("Invalid packet received, aborting connection");
        }
    }

    public static void registerServerLoginChannel(Connection manager, ClientIntentionPacket packet) {
        manager.channel().attr(NetworkConstants.FML_NETVERSION).set((Object)packet.getFMLVersion());
        HandshakeHandler.registerHandshake(manager, NetworkDirection.LOGIN_TO_CLIENT);
    }

    public static synchronized void registerClientLoginChannel(Connection manager) {
        manager.channel().attr(NetworkConstants.FML_NETVERSION).set((Object)"NONE");
        HandshakeHandler.registerHandshake(manager, NetworkDirection.LOGIN_TO_SERVER);
    }

    public static synchronized void sendMCRegistryPackets(Connection manager, String direction) {
        NetworkFilters.injectIfNecessary(manager);
        Set<ResourceLocation> resourceLocations = NetworkRegistry.buildChannelVersions().keySet().stream().filter(rl -> !Objects.equals(rl.m_135827_(), "minecraft")).collect(Collectors.toSet());
        MCRegisterPacketHandler.INSTANCE.addChannels(resourceLocations, manager);
        MCRegisterPacketHandler.INSTANCE.sendRegistry(manager, NetworkDirection.valueOf(direction));
    }

    public static boolean isVanillaConnection(Connection manager) {
        if (manager == null || manager.channel() == null) {
            throw new NullPointerException("ARGH! Network Manager is null (" + String.valueOf(manager) != null ? "CHANNEL" : "MANAGER)");
        }
        return NetworkHooks.getConnectionType(() -> manager) == ConnectionType.VANILLA;
    }

    public static void handleClientLoginSuccess(Connection manager) {
        if (NetworkHooks.isVanillaConnection(manager)) {
            LOGGER.info("Connected to a vanilla server. Catching up missing behaviour.");
            ConfigTracker.INSTANCE.loadDefaultServerConfigs();
        } else {
            LOGGER.info("Connected to a modded server.");
        }
    }

    public static boolean tickNegotiation(ServerLoginPacketListenerImpl netHandlerLoginServer, Connection networkManager, ServerPlayer player) {
        return HandshakeHandler.tickLogin(networkManager);
    }

    public static void openScreen(ServerPlayer player, MenuProvider containerSupplier) {
        NetworkHooks.openScreen(player, containerSupplier, (FriendlyByteBuf buf) -> {});
    }

    public static void openScreen(ServerPlayer player, MenuProvider containerSupplier, BlockPos pos) {
        NetworkHooks.openScreen(player, containerSupplier, (FriendlyByteBuf buf) -> buf.m_130064_(pos));
    }

    public static void openScreen(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter) {
        if (player.m_9236_().f_46443_) {
            return;
        }
        player.m_9230_();
        player.m_9217_();
        int openContainerId = player.f_8940_;
        FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
        extraDataWriter.accept(extraData);
        extraData.readerIndex(0);
        FriendlyByteBuf output = new FriendlyByteBuf(Unpooled.buffer());
        output.m_130130_(extraData.readableBytes());
        output.writeBytes((ByteBuf)extraData);
        if (output.readableBytes() > 32600 || output.readableBytes() < 1) {
            throw new IllegalArgumentException("Invalid PacketBuffer for openGui, found " + output.readableBytes() + " bytes");
        }
        AbstractContainerMenu c = containerSupplier.m_7208_(openContainerId, player.m_150109_(), (Player)player);
        if (c == null) {
            return;
        }
        MenuType type = c.m_6772_();
        PlayMessages.OpenContainer msg = new PlayMessages.OpenContainer(type, openContainerId, containerSupplier.m_5446_(), output);
        NetworkConstants.playChannel.sendTo(msg, player.f_8906_.f_9742_, NetworkDirection.PLAY_TO_CLIENT);
        player.f_36096_ = c;
        player.m_143399_(player.f_36096_);
        MinecraftForge.EVENT_BUS.post((Event)new PlayerContainerEvent.Open((Player)player, c));
    }

    static void appendConnectionData(Connection mgr, Map<String, Pair<String, String>> modData, Map<ResourceLocation, String> channels) {
        ConnectionData oldData = (ConnectionData)mgr.channel().attr(NetworkConstants.FML_CONNECTION_DATA).get();
        oldData = oldData != null ? new ConnectionData(oldData.getModData().isEmpty() ? modData : oldData.getModData(), oldData.getChannels().isEmpty() ? channels : oldData.getChannels()) : new ConnectionData(modData, channels);
        mgr.channel().attr(NetworkConstants.FML_CONNECTION_DATA).set((Object)oldData);
    }

    @Nullable
    public static ConnectionData getConnectionData(Connection mgr) {
        return (ConnectionData)mgr.channel().attr(NetworkConstants.FML_CONNECTION_DATA).get();
    }

    @Nullable
    public static ConnectionData.ModMismatchData getModMismatchData(Connection mgr) {
        return (ConnectionData.ModMismatchData)mgr.channel().attr(NetworkConstants.FML_MOD_MISMATCH_DATA).get();
    }

    @Nullable
    public static MCRegisterPacketHandler.ChannelList getChannelList(Connection mgr) {
        return (MCRegisterPacketHandler.ChannelList)mgr.channel().attr(NetworkConstants.FML_MC_REGISTRY).get();
    }
}

