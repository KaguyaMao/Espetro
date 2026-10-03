/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Connection
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.eventbus.api.BusBuilder
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.eventbus.api.IEventListener
 */
package net.minecraftforge.network;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventListener;
import net.minecraftforge.network.ConnectionData;
import net.minecraftforge.network.ICustomPacket;
import net.minecraftforge.network.MCRegisterPacketHandler;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkRegistry;

public class NetworkInstance {
    private final ResourceLocation channelName;
    private final String networkProtocolVersion;
    private final Predicate<String> clientAcceptedVersions;
    private final Predicate<String> serverAcceptedVersions;
    private final IEventBus networkEventBus;

    public ResourceLocation getChannelName() {
        return this.channelName;
    }

    NetworkInstance(ResourceLocation channelName, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
        this.channelName = channelName;
        this.networkProtocolVersion = networkProtocolVersion.get();
        this.clientAcceptedVersions = clientAcceptedVersions;
        this.serverAcceptedVersions = serverAcceptedVersions;
        this.networkEventBus = BusBuilder.builder().setExceptionHandler(this::handleError).useModLauncher().build();
    }

    private void handleError(IEventBus iEventBus, Event event, IEventListener[] iEventListeners, int i, Throwable throwable) {
    }

    public <T extends NetworkEvent> void addListener(Consumer<T> eventListener) {
        this.networkEventBus.addListener(eventListener);
    }

    public void addGatherListener(Consumer<NetworkEvent.GatherLoginPayloadsEvent> eventListener) {
        this.networkEventBus.addListener(eventListener);
    }

    public void registerObject(Object object) {
        this.networkEventBus.register(object);
    }

    public void unregisterObject(Object object) {
        this.networkEventBus.unregister(object);
    }

    boolean dispatch(NetworkDirection side, ICustomPacket<?> packet, Connection manager) {
        NetworkEvent.Context context = new NetworkEvent.Context(manager, side, packet.getIndex());
        this.networkEventBus.post((Event)side.getEvent(packet, () -> context));
        return context.getPacketHandled();
    }

    String getNetworkProtocolVersion() {
        return this.networkProtocolVersion;
    }

    boolean tryServerVersionOnClient(String serverVersion) {
        return this.clientAcceptedVersions.test(serverVersion);
    }

    boolean tryClientVersionOnServer(String clientVersion) {
        return this.serverAcceptedVersions.test(clientVersion);
    }

    void dispatchGatherLogin(List<NetworkRegistry.LoginPayload> loginPayloadList, boolean isLocal) {
        this.networkEventBus.post((Event)new NetworkEvent.GatherLoginPayloadsEvent(loginPayloadList, isLocal));
    }

    void dispatchLoginPacket(NetworkEvent.LoginPayloadEvent loginPayloadEvent) {
        this.networkEventBus.post((Event)loginPayloadEvent);
    }

    void dispatchEvent(NetworkEvent networkEvent) {
        this.networkEventBus.post((Event)networkEvent);
    }

    public boolean isRemotePresent(Connection manager) {
        ConnectionData connectionData = NetworkHooks.getConnectionData(manager);
        MCRegisterPacketHandler.ChannelList channelList = NetworkHooks.getChannelList(manager);
        return connectionData != null && connectionData.getChannels().containsKey((Object)this.channelName) || channelList != null && channelList.getRemoteLocations().contains(this.channelName);
    }
}

