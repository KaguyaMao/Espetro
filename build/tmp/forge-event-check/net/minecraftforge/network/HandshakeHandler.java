/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.core.Registry
 *  net.minecraft.network.Connection
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.network.ServerLoginPacketListenerImpl
 *  net.minecraftforge.eventbus.api.Event
 *  org.apache.commons.lang3.tuple.Pair
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 *  org.apache.logging.log4j.util.Supplier
 */
package net.minecraftforge.network;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LogMessageAdapter;
import net.minecraftforge.event.entity.player.PlayerNegotiationEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.network.ConfigSync;
import net.minecraftforge.network.ConnectionData;
import net.minecraftforge.network.ConnectionType;
import net.minecraftforge.network.HandshakeMessages;
import net.minecraftforge.network.LoginWrapper;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.registries.DataPackRegistriesHooks;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class HandshakeHandler {
    static final Marker FMLHSMARKER = MarkerManager.getMarker((String)"FMLHANDSHAKE").setParents(new Marker[]{NetworkConstants.NETWORK});
    private static final Logger LOGGER = LogManager.getLogger();
    private static final LoginWrapper loginWrapper = new LoginWrapper();
    private List<NetworkRegistry.LoginPayload> messageList;
    private List<Integer> sentMessages = new ArrayList<Integer>();
    private final NetworkDirection direction;
    private final Connection manager;
    private int packetPosition;
    private Map<ResourceLocation, ForgeRegistry.Snapshot> registrySnapshots;
    private Set<ResourceLocation> registriesToReceive;
    private Map<ResourceLocation, String> registryHashes;
    private boolean negotiationStarted = false;
    private final List<Future<Void>> pendingFutures = new ArrayList<Future<Void>>();

    static void registerHandshake(Connection manager, NetworkDirection direction) {
        manager.channel().attr(NetworkConstants.FML_HANDSHAKE_HANDLER).compareAndSet(null, (Object)new HandshakeHandler(manager, direction));
    }

    static boolean tickLogin(Connection networkManager) {
        return ((HandshakeHandler)networkManager.channel().attr(NetworkConstants.FML_HANDSHAKE_HANDLER).get()).tickServer();
    }

    private HandshakeHandler(Connection networkManager, NetworkDirection side) {
        this.direction = side;
        this.manager = networkManager;
        if (networkManager.m_129531_()) {
            this.messageList = NetworkRegistry.gatherLoginPayloads(this.direction, true);
            LOGGER.debug(FMLHSMARKER, "Starting local connection.");
        } else if (NetworkHooks.getConnectionType(() -> this.manager) == ConnectionType.VANILLA) {
            this.messageList = Collections.emptyList();
            LOGGER.debug(FMLHSMARKER, "Starting new vanilla impl connection.");
        } else {
            this.messageList = NetworkRegistry.gatherLoginPayloads(this.direction, false);
            LOGGER.debug(FMLHSMARKER, "Starting new modded impl connection. Found {} messages to dispatch.", (Object)this.messageList.size());
        }
    }

    public static <MSG extends IntSupplier> BiConsumer<MSG, Supplier<NetworkEvent.Context>> biConsumerFor(HandshakeConsumer<MSG> consumer) {
        return (m, c) -> consumer.accept(HandshakeHandler.getHandshake(c), (Object)m, (Supplier<NetworkEvent.Context>)c);
    }

    public static <MSG extends IntSupplier> BiConsumer<MSG, Supplier<NetworkEvent.Context>> indexFirst(HandshakeConsumer<MSG> next) {
        BiConsumer<IntSupplier, Supplier<NetworkEvent.Context>> loginIndexedMessageSupplierBiConsumer = HandshakeHandler.biConsumerFor(HandshakeHandler::handleIndexedMessage);
        return loginIndexedMessageSupplierBiConsumer.andThen(HandshakeHandler.biConsumerFor(next));
    }

    private static HandshakeHandler getHandshake(Supplier<NetworkEvent.Context> contextSupplier) {
        return (HandshakeHandler)contextSupplier.get().attr(NetworkConstants.FML_HANDSHAKE_HANDLER).get();
    }

    void handleServerModListOnClient(HandshakeMessages.S2CModList serverModList, Supplier<NetworkEvent.Context> c) {
        LOGGER.debug(FMLHSMARKER, "Logging into server with mod list [{}]", (Object)String.join((CharSequence)", ", serverModList.getModList()));
        Map<ResourceLocation, String> mismatchedChannels = NetworkRegistry.validateClientChannels(serverModList.getChannels());
        c.get().setPacketHandled(true);
        NetworkHooks.appendConnectionData(c.get().getNetworkManager(), serverModList.getModList().stream().collect(Collectors.toMap(Function.identity(), s -> Pair.of((Object)"", (Object)""))), serverModList.getChannels());
        if (!mismatchedChannels.isEmpty()) {
            LOGGER.error(FMLHSMARKER, "Terminating connection with server, mismatched mod list");
            c.get().getNetworkManager().channel().attr(NetworkConstants.FML_MOD_MISMATCH_DATA).set((Object)ConnectionData.ModMismatchData.channel(mismatchedChannels, NetworkHooks.getConnectionData(c.get().getNetworkManager()), true));
            c.get().getNetworkManager().m_129507_((Component)Component.m_237113_((String)"Connection closed - mismatched mod channel list"));
            return;
        }
        ArrayList<String> missingDataPackRegistries = new ArrayList<String>();
        Set<ResourceKey<Registry<?>>> clientDataPackRegistries = DataPackRegistriesHooks.getSyncedCustomRegistries();
        for (ResourceKey<? extends Registry<?>> resourceKey : serverModList.getCustomDataPackRegistries()) {
            if (clientDataPackRegistries.contains(resourceKey)) continue;
            ResourceLocation location = resourceKey.m_135782_();
            LOGGER.error(FMLHSMARKER, "Missing required datapack registry: {}", (Object)location);
            missingDataPackRegistries.add(resourceKey.m_135782_().toString());
        }
        if (!missingDataPackRegistries.isEmpty()) {
            c.get().getNetworkManager().m_129507_((Component)Component.m_237110_((String)"fml.menu.multiplayer.missingdatapackregistries", (Object[])new Object[]{String.join((CharSequence)", ", missingDataPackRegistries)}));
            return;
        }
        NetworkConstants.handshakeChannel.reply(new HandshakeMessages.C2SModListReply(), c.get());
        LOGGER.debug(FMLHSMARKER, "Accepted server connection");
        c.get().getNetworkManager().channel().attr(NetworkConstants.FML_NETVERSION).set((Object)"FML3");
        this.registriesToReceive = new HashSet<ResourceLocation>(serverModList.getRegistries());
        this.registrySnapshots = Maps.newHashMap();
        LOGGER.debug(ForgeRegistry.REGISTRIES, "Expecting {} registries: {}", new org.apache.logging.log4j.util.Supplier[]{() -> this.registriesToReceive.size(), () -> this.registriesToReceive});
    }

    void handleModData(HandshakeMessages.S2CModData serverModData, Supplier<NetworkEvent.Context> c) {
        c.get().getNetworkManager().channel().attr(NetworkConstants.FML_CONNECTION_DATA).set((Object)new ConnectionData(serverModData.getMods(), new HashMap<ResourceLocation, String>()));
        c.get().setPacketHandled(true);
    }

    <MSG extends IntSupplier> void handleIndexedMessage(MSG message, Supplier<NetworkEvent.Context> c) {
        LOGGER.debug(FMLHSMARKER, "Received client indexed reply {} of type {}", (Object)message.getAsInt(), (Object)message.getClass().getName());
        boolean removed = this.sentMessages.removeIf(i -> i.intValue() == message.getAsInt());
        if (!removed) {
            LOGGER.error(FMLHSMARKER, "Recieved unexpected index {} in client reply", (Object)message.getAsInt());
        }
    }

    void handleClientModListOnServer(HandshakeMessages.C2SModListReply clientModList, Supplier<NetworkEvent.Context> c) {
        LOGGER.debug(FMLHSMARKER, "Received client connection with modlist [{}]", (Object)String.join((CharSequence)", ", clientModList.getModList()));
        Map<ResourceLocation, String> mismatchedChannels = NetworkRegistry.validateServerChannels(clientModList.getChannels());
        c.get().getNetworkManager().channel().attr(NetworkConstants.FML_CONNECTION_DATA).set((Object)new ConnectionData(clientModList.getModList().stream().collect(Collectors.toMap(Function.identity(), s -> Pair.of((Object)"", (Object)""))), clientModList.getChannels()));
        c.get().setPacketHandled(true);
        if (!mismatchedChannels.isEmpty()) {
            LOGGER.error(FMLHSMARKER, "Terminating connection with client, mismatched mod list");
            NetworkConstants.handshakeChannel.reply(new HandshakeMessages.S2CChannelMismatchData(mismatchedChannels), c.get());
            c.get().getNetworkManager().m_129507_((Component)Component.m_237113_((String)"Connection closed - mismatched mod channel list"));
            return;
        }
        LOGGER.debug(FMLHSMARKER, "Accepted client connection mod list");
    }

    void handleModMismatchData(HandshakeMessages.S2CChannelMismatchData modMismatchData, Supplier<NetworkEvent.Context> c) {
        if (!modMismatchData.getMismatchedChannelData().isEmpty()) {
            LOGGER.error(FMLHSMARKER, "Channels [{}] rejected their client side version number", (Object)modMismatchData.getMismatchedChannelData().keySet().stream().map(Object::toString).collect(Collectors.joining(",")));
            LOGGER.error(FMLHSMARKER, "Terminating connection with server, mismatched mod list");
            c.get().setPacketHandled(true);
            c.get().getNetworkManager().channel().attr(NetworkConstants.FML_MOD_MISMATCH_DATA).set((Object)ConnectionData.ModMismatchData.channel(modMismatchData.getMismatchedChannelData(), NetworkHooks.getConnectionData(c.get().getNetworkManager()), false));
            c.get().getNetworkManager().m_129507_((Component)Component.m_237113_((String)"Connection closed - mismatched mod channel list"));
        }
    }

    void handleRegistryMessage(HandshakeMessages.S2CRegistry registryPacket, Supplier<NetworkEvent.Context> contextSupplier) {
        LOGGER.debug(FMLHSMARKER, "Received registry packet for {}", (Object)registryPacket.getRegistryName());
        this.registriesToReceive.remove(registryPacket.getRegistryName());
        this.registrySnapshots.put(registryPacket.getRegistryName(), registryPacket.getSnapshot());
        boolean continueHandshake = true;
        if (this.registriesToReceive.isEmpty()) {
            continueHandshake = this.handleRegistryLoading(contextSupplier);
        }
        contextSupplier.get().setPacketHandled(true);
        if (!continueHandshake) {
            LOGGER.error(FMLHSMARKER, "Connection closed, not continuing handshake");
        } else {
            NetworkConstants.handshakeChannel.reply(new HandshakeMessages.C2SAcknowledge(), contextSupplier.get());
        }
    }

    private boolean handleRegistryLoading(Supplier<NetworkEvent.Context> contextSupplier) {
        AtomicBoolean successfulConnection = new AtomicBoolean(false);
        AtomicReference registryMismatches = new AtomicReference();
        CountDownLatch block = new CountDownLatch(1);
        contextSupplier.get().enqueueWork(() -> {
            LOGGER.debug(FMLHSMARKER, "Injecting registry snapshot from server.");
            Multimap<ResourceLocation, ResourceLocation> missingData = GameData.injectSnapshot(this.registrySnapshots, false, false);
            LOGGER.debug(FMLHSMARKER, "Snapshot injected.");
            if (!missingData.isEmpty()) {
                LOGGER.error(FMLHSMARKER, "Missing registry data for impl connection:\n{}", (Object)LogMessageAdapter.adapt(sb -> missingData.forEach((reg, entry) -> sb.append("\t").append(reg).append(": ").append(entry).append('\n'))));
            }
            successfulConnection.set(missingData.isEmpty());
            registryMismatches.set(missingData);
            block.countDown();
        });
        LOGGER.debug(FMLHSMARKER, "Waiting for registries to load.");
        try {
            block.await();
        }
        catch (InterruptedException e) {
            Thread.interrupted();
        }
        if (successfulConnection.get()) {
            LOGGER.debug(FMLHSMARKER, "Registry load complete, continuing handshake.");
        } else {
            LOGGER.error(FMLHSMARKER, "Failed to load registry, closing connection.");
            this.manager.channel().attr(NetworkConstants.FML_MOD_MISMATCH_DATA).set((Object)ConnectionData.ModMismatchData.registry((Multimap<ResourceLocation, ResourceLocation>)((Multimap)registryMismatches.get()), NetworkHooks.getConnectionData(contextSupplier.get().getNetworkManager())));
            this.manager.m_129507_((Component)Component.m_237113_((String)"Failed to synchronize registry data from server, closing connection"));
        }
        return successfulConnection.get();
    }

    void handleClientAck(HandshakeMessages.C2SAcknowledge msg, Supplier<NetworkEvent.Context> contextSupplier) {
        LOGGER.debug(FMLHSMARKER, "Received acknowledgement from client");
        contextSupplier.get().setPacketHandled(true);
    }

    void handleConfigSync(HandshakeMessages.S2CConfigData msg, Supplier<NetworkEvent.Context> contextSupplier) {
        LOGGER.debug(FMLHSMARKER, "Received config sync from server");
        ConfigSync.INSTANCE.receiveSyncedConfig(msg, contextSupplier);
        contextSupplier.get().setPacketHandled(true);
        NetworkConstants.handshakeChannel.reply(new HandshakeMessages.C2SAcknowledge(), contextSupplier.get());
    }

    public boolean tickServer() {
        if (!this.negotiationStarted) {
            GameProfile profile = ((ServerLoginPacketListenerImpl)this.manager.m_129538_()).f_10021_;
            PlayerNegotiationEvent event = new PlayerNegotiationEvent(this.manager, profile, this.pendingFutures);
            MinecraftForge.EVENT_BUS.post((Event)event);
            this.negotiationStarted = true;
        }
        if (this.packetPosition < this.messageList.size()) {
            NetworkRegistry.LoginPayload message = this.messageList.get(this.packetPosition);
            LOGGER.debug(FMLHSMARKER, "Sending ticking packet info '{}' to '{}' sequence {}", (Object)message.getMessageContext(), (Object)message.getChannelName(), (Object)this.packetPosition);
            if (message.needsResponse()) {
                this.sentMessages.add(this.packetPosition);
            }
            loginWrapper.sendServerToClientLoginPacket(message.getChannelName(), message.getData(), this.packetPosition, this.manager);
            ++this.packetPosition;
        }
        this.pendingFutures.removeIf(future -> {
            if (!future.isDone()) {
                return false;
            }
            try {
                future.get();
            }
            catch (ExecutionException ex) {
                LOGGER.error("Error during negotiation", ex.getCause());
            }
            catch (InterruptedException | CancellationException exception) {
                // empty catch block
            }
            return true;
        });
        if (this.sentMessages.isEmpty() && this.packetPosition >= this.messageList.size() && this.pendingFutures.isEmpty()) {
            this.manager.channel().attr(NetworkConstants.FML_HANDSHAKE_HANDLER).set(null);
            LOGGER.debug(FMLHSMARKER, "Handshake complete!");
            return true;
        }
        return false;
    }

    public static boolean packetNeedsResponse(Connection mgr, int packetPosition) {
        HandshakeHandler handler = (HandshakeHandler)mgr.channel().attr(NetworkConstants.FML_HANDSHAKE_HANDLER).get();
        if (handler != null) {
            return handler.sentMessages.contains(packetPosition);
        }
        return false;
    }

    @FunctionalInterface
    public static interface HandshakeConsumer<MSG extends IntSupplier> {
        public void accept(HandshakeHandler var1, MSG var2, Supplier<NetworkEvent.Context> var3);
    }
}

