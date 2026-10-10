/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.Attribute
 *  io.netty.util.AttributeKey
 *  net.minecraft.network.Connection
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.PacketListener
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.TickTask
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.network.ServerGamePacketListenerImpl
 *  net.minecraft.util.thread.BlockableEventLoop
 *  net.minecraftforge.eventbus.api.Event
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.network;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.network.ICustomPacket;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public class NetworkEvent
extends Event {
    private final FriendlyByteBuf payload;
    private final Supplier<Context> source;
    private final int loginIndex;

    private NetworkEvent(ICustomPacket<?> payload, Supplier<Context> source) {
        this.payload = payload.getInternalData();
        this.source = source;
        this.loginIndex = payload.getIndex();
    }

    private NetworkEvent(FriendlyByteBuf payload, Supplier<Context> source, int loginIndex) {
        this.payload = payload;
        this.source = source;
        this.loginIndex = loginIndex;
    }

    public NetworkEvent(Supplier<Context> source) {
        this.source = source;
        this.payload = null;
        this.loginIndex = -1;
    }

    public FriendlyByteBuf getPayload() {
        return this.payload;
    }

    public Supplier<Context> getSource() {
        return this.source;
    }

    public int getLoginIndex() {
        return this.loginIndex;
    }

    public static class PacketDispatcher {
        BiConsumer<ResourceLocation, FriendlyByteBuf> packetSink;

        PacketDispatcher(BiConsumer<ResourceLocation, FriendlyByteBuf> packetSink) {
            this.packetSink = packetSink;
        }

        private PacketDispatcher() {
        }

        public void sendPacket(ResourceLocation resourceLocation, FriendlyByteBuf buffer) {
            this.packetSink.accept(resourceLocation, buffer);
        }

        static class NetworkManagerDispatcher
        extends PacketDispatcher {
            private final Connection manager;
            private final int packetIndex;
            private final BiFunction<Pair<FriendlyByteBuf, Integer>, ResourceLocation, ICustomPacket<?>> customPacketSupplier;

            NetworkManagerDispatcher(Connection manager, int packetIndex, BiFunction<Pair<FriendlyByteBuf, Integer>, ResourceLocation, ICustomPacket<?>> customPacketSupplier) {
                this.packetSink = this::dispatchPacket;
                this.manager = manager;
                this.packetIndex = packetIndex;
                this.customPacketSupplier = customPacketSupplier;
            }

            private void dispatchPacket(ResourceLocation resourceLocation, FriendlyByteBuf buffer) {
                ICustomPacket<?> packet = this.customPacketSupplier.apply((Pair<FriendlyByteBuf, Integer>)Pair.of((Object)buffer, (Object)this.packetIndex), resourceLocation);
                this.manager.m_129512_(packet.getThis());
            }
        }
    }

    public static class Context {
        private final Connection networkManager;
        private final NetworkDirection networkDirection;
        private final PacketDispatcher packetDispatcher;
        private boolean packetHandled;

        Context(Connection netHandler, NetworkDirection networkDirection, int index) {
            this(netHandler, networkDirection, new PacketDispatcher.NetworkManagerDispatcher(netHandler, index, networkDirection.reply()::buildPacket));
        }

        Context(Connection networkManager, NetworkDirection networkDirection, BiConsumer<ResourceLocation, FriendlyByteBuf> packetSink) {
            this(networkManager, networkDirection, new PacketDispatcher(packetSink));
        }

        Context(Connection networkManager, NetworkDirection networkDirection, PacketDispatcher dispatcher) {
            this.networkManager = networkManager;
            this.networkDirection = networkDirection;
            this.packetDispatcher = dispatcher;
        }

        public NetworkDirection getDirection() {
            return this.networkDirection;
        }

        public PacketDispatcher getPacketDispatcher() {
            return this.packetDispatcher;
        }

        public <T> Attribute<T> attr(AttributeKey<T> key) {
            return this.networkManager.channel().attr(key);
        }

        public void setPacketHandled(boolean packetHandled) {
            this.packetHandled = packetHandled;
        }

        public boolean getPacketHandled() {
            return this.packetHandled;
        }

        public CompletableFuture<Void> enqueueWork(Runnable runnable) {
            BlockableEventLoop<? super TickTask> executor = LogicalSidedProvider.WORKQUEUE.get(this.getDirection().getReceptionSide());
            if (!executor.m_18695_()) {
                return executor.m_18689_(runnable);
            }
            runnable.run();
            return CompletableFuture.completedFuture(null);
        }

        @Nullable
        public ServerPlayer getSender() {
            PacketListener netHandler = this.networkManager.m_129538_();
            if (netHandler instanceof ServerGamePacketListenerImpl) {
                ServerGamePacketListenerImpl netHandlerPlayServer = (ServerGamePacketListenerImpl)netHandler;
                return netHandlerPlayServer.f_9743_;
            }
            return null;
        }

        public Connection getNetworkManager() {
            return this.networkManager;
        }
    }

    public static class ChannelRegistrationChangeEvent
    extends NetworkEvent {
        private final RegistrationChangeType changeType;

        ChannelRegistrationChangeEvent(Supplier<Context> source, RegistrationChangeType changeType) {
            super(source);
            this.changeType = changeType;
        }

        public RegistrationChangeType getRegistrationChangeType() {
            return this.changeType;
        }
    }

    public static enum RegistrationChangeType {
        REGISTER,
        UNREGISTER;

    }

    public static class LoginPayloadEvent
    extends NetworkEvent {
        LoginPayloadEvent(FriendlyByteBuf payload, Supplier<Context> source, int loginIndex) {
            super(payload, source, loginIndex);
        }
    }

    public static class GatherLoginPayloadsEvent
    extends Event {
        private final List<NetworkRegistry.LoginPayload> collected;
        private final boolean isLocal;

        public GatherLoginPayloadsEvent(List<NetworkRegistry.LoginPayload> loginPayloadList, boolean isLocal) {
            this.collected = loginPayloadList;
            this.isLocal = isLocal;
        }

        public void add(FriendlyByteBuf buffer, ResourceLocation channelName, String context) {
            this.collected.add(new NetworkRegistry.LoginPayload(buffer, channelName, context));
        }

        public void add(FriendlyByteBuf buffer, ResourceLocation channelName, String context, boolean needsResponse) {
            this.collected.add(new NetworkRegistry.LoginPayload(buffer, channelName, context, needsResponse));
        }

        public boolean isLocal() {
            return this.isLocal;
        }
    }

    public static class ClientCustomPayloadLoginEvent
    extends ClientCustomPayloadEvent {
        ClientCustomPayloadLoginEvent(ICustomPacket<?> payload, Supplier<Context> source) {
            super(payload, source);
        }
    }

    public static class ServerCustomPayloadLoginEvent
    extends ServerCustomPayloadEvent {
        ServerCustomPayloadLoginEvent(ICustomPacket<?> payload, Supplier<Context> source) {
            super(payload, source);
        }
    }

    public static class ClientCustomPayloadEvent
    extends NetworkEvent {
        ClientCustomPayloadEvent(ICustomPacket<?> payload, Supplier<Context> source) {
            super(payload, source);
        }
    }

    public static class ServerCustomPayloadEvent
    extends NetworkEvent {
        ServerCustomPayloadEvent(ICustomPacket<?> payload, Supplier<Context> source) {
            super(payload, source);
        }
    }
}

