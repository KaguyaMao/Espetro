/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.network.NetworkDirection
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.NetworkRegistry
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.network.PacketDistributor$TargetPoint
 *  net.minecraftforge.network.simple.SimpleChannel
 *  net.minecraftforge.server.ServerLifecycleHooks
 */
package com.sighs.apricityui.network.forge;

import com.sighs.apricityui.network.api.CustomPacketPayload;
import com.sighs.apricityui.network.api.INetworkPacket;
import com.sighs.apricityui.network.api.NetworkAutoRegistration;
import com.sighs.apricityui.network.api.NetworkPacket;
import com.sighs.apricityui.network.api.NetworkPacketTypes;
import com.sighs.apricityui.network.api.Side;
import com.sighs.apricityui.network.codec.StreamCodec;
import com.sighs.apricityui.network.forge.ForgeNetworkContext;
import com.sighs.apricityui.network.serialization.NetworkSerialization;
import com.sighs.apricityui.network.spi.INetworkManager;
import com.sighs.apricityui.network.util.NetworkUtil;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

public class NetworkManagerImpl
implements INetworkManager {
    private static final String PROTOCOL_VERSION = "1";
    private static final String SERVERBOUND_CHANNEL_NAME = "oelib_c2s";
    private static final String CLIENTBOUND_CHANNEL_NAME = "oelib_s2c";
    private static final Map<String, SimpleChannel> SERVERBOUND_CHANNELS = new ConcurrentHashMap<String, SimpleChannel>();
    private static final Map<String, SimpleChannel> CLIENTBOUND_CHANNELS = new ConcurrentHashMap<String, SimpleChannel>();
    private static final Map<CustomPacketPayload.Type<?>, NetworkUtil.PacketInfo<?>> REGISTERED = new ConcurrentHashMap();
    private static final Map<CustomPacketPayload.Type<?>, SimpleChannel> TYPE_TO_SERVERBOUND_CHANNEL = new ConcurrentHashMap();
    private static final Map<CustomPacketPayload.Type<?>, SimpleChannel> TYPE_TO_CLIENTBOUND_CHANNEL = new ConcurrentHashMap();
    private static final Map<String, Integer> NEXT_SERVERBOUND_ID = new ConcurrentHashMap<String, Integer>();
    private static final Map<String, Integer> NEXT_CLIENTBOUND_ID = new ConcurrentHashMap<String, Integer>();
    private static boolean AUTO_REGISTRATION_HOOK_INSTALLED = false;

    private static SimpleChannel serverboundChannelOf(String modId) {
        return SERVERBOUND_CHANNELS.computeIfAbsent(modId, id -> NetworkRegistry.newSimpleChannel((ResourceLocation)new ResourceLocation(id, SERVERBOUND_CHANNEL_NAME), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals));
    }

    private static SimpleChannel clientboundChannelOf(String modId) {
        return CLIENTBOUND_CHANNELS.computeIfAbsent(modId, id -> NetworkRegistry.newSimpleChannel((ResourceLocation)new ResourceLocation(id, CLIENTBOUND_CHANNEL_NAME), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals));
    }

    private static List<ServerPlayer> levelPlayers(Entity e) {
        List list;
        Level level = e.m_9236_();
        if (level instanceof ServerLevel) {
            ServerLevel sl = (ServerLevel)level;
            list = sl.m_6907_();
        } else {
            list = Collections.emptyList();
        }
        return list;
    }

    private static List<ServerPlayer> levelPlayersIncluding(Entity e) {
        ServerPlayer sp;
        List<ServerPlayer> list = NetworkManagerImpl.levelPlayers(e);
        if (e instanceof ServerPlayer && !list.contains(sp = (ServerPlayer)e)) {
            ArrayList<ServerPlayer> newList = new ArrayList<ServerPlayer>(list);
            newList.add(sp);
            return newList;
        }
        return list;
    }

    public static void installAutoRegistrationHook() {
        if (AUTO_REGISTRATION_HOOK_INSTALLED) {
            return;
        }
        AUTO_REGISTRATION_HOOK_INSTALLED = true;
        NetworkAutoRegistration.addPacketRegistrationListener(packetClass -> {
            try {
                new NetworkManagerImpl().registerAnnotated((Class<? extends INetworkPacket<?>>)packetClass);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        });
    }

    public <T extends INetworkPacket<T> & CustomPacketPayload> void registerAnnotated(Class<? extends INetworkPacket<?>> rawClass) {
        int id;
        SimpleChannel channel;
        Class<INetworkPacket<?>> clazz = rawClass;
        NetworkPacket meta = clazz.getAnnotation(NetworkPacket.class);
        if (meta == null || !clazz.isRecord()) {
            return;
        }
        CustomPacketPayload.Type<INetworkPacket<?>> type = NetworkPacketTypes.typeOf(clazz);
        if (REGISTERED.containsKey(type)) {
            return;
        }
        StreamCodec<FriendlyByteBuf, ? extends INetworkPacket<?>> codec = NetworkSerialization.autoCodec(clazz);
        REGISTERED.put(type, new NetworkUtil.PacketInfo(type, codec));
        Side side = meta.side();
        if (side == Side.SERVER || side == Side.BOTH) {
            channel = NetworkManagerImpl.serverboundChannelOf(type.id().m_135827_());
            TYPE_TO_SERVERBOUND_CHANNEL.put(type, channel);
            id = NEXT_SERVERBOUND_ID.merge(type.id().m_135827_(), 1, Integer::sum) - 1;
            channel.messageBuilder(clazz, id, NetworkDirection.PLAY_TO_SERVER).encoder((msg, buf) -> codec.encode(buf, msg)).decoder(codec::decode).consumerMainThread((msg, ctx) -> msg.handle(new ForgeNetworkContext((NetworkEvent.Context)ctx.get()))).add();
        }
        if (side == Side.CLIENT || side == Side.BOTH) {
            channel = NetworkManagerImpl.clientboundChannelOf(type.id().m_135827_());
            TYPE_TO_CLIENTBOUND_CHANNEL.put(type, channel);
            id = NEXT_CLIENTBOUND_ID.merge(type.id().m_135827_(), 1, Integer::sum) - 1;
            channel.messageBuilder(clazz, id, NetworkDirection.PLAY_TO_CLIENT).encoder((msg, buf) -> codec.encode(buf, msg)).decoder(codec::decode).consumerMainThread((msg, ctx) -> msg.handle(new ForgeNetworkContext((NetworkEvent.Context)ctx.get()))).add();
        }
    }

    @Override
    public <T extends INetworkPacket<T> & CustomPacketPayload> void sendToPlayer(T packet, ServerPlayer player) {
        if (player == null) {
            return;
        }
        this.executeSend(packet, TYPE_TO_CLIENTBOUND_CHANNEL, ch -> ch.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet), data -> NetworkUtil.sendChunkedPacket(data, this.getTypeId(packet), Collections.singletonList(player), this.getThreshold(packet)));
    }

    @Override
    public <T extends INetworkPacket<T> & CustomPacketPayload> void sendToServer(T packet) {
        this.executeSend(packet, TYPE_TO_SERVERBOUND_CHANNEL, ch -> ch.sendToServer((Object)packet), data -> NetworkUtil.sendChunkedPacketToServer(data, this.getTypeId(packet), this.getThreshold(packet)));
    }

    @Override
    public <T extends INetworkPacket<T> & CustomPacketPayload> void sendToAll(T packet) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        this.executeSend(packet, TYPE_TO_CLIENTBOUND_CHANNEL, ch -> ch.send(PacketDistributor.ALL.noArg(), (Object)packet), data -> NetworkUtil.sendChunkedPacketToAll(data, this.getTypeId(packet), this.getThreshold(packet)));
    }

    @Override
    public <T extends INetworkPacket<T> & CustomPacketPayload> void sendToWorld(T packet, ServerLevel level) {
        this.executeSend(packet, TYPE_TO_CLIENTBOUND_CHANNEL, ch -> ch.send(PacketDistributor.DIMENSION.with(() -> ((ServerLevel)level).m_46472_()), (Object)packet), data -> NetworkUtil.sendChunkedPacket(data, this.getTypeId(packet), level.m_6907_(), this.getThreshold(packet)));
    }

    @Override
    public <T extends INetworkPacket<T> & CustomPacketPayload> void sendToNear(T packet, ServerLevel level, Vec3 pos, double radius) {
        this.executeSend(packet, TYPE_TO_CLIENTBOUND_CHANNEL, ch -> ch.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p((double)pos.f_82479_, (double)pos.f_82480_, (double)pos.f_82481_, (double)radius, (ResourceKey)level.m_46472_())), (Object)packet), data -> NetworkUtil.sendChunkedPacket(data, this.getTypeId(packet), level.m_6907_(), this.getThreshold(packet)));
    }

    @Override
    public <T extends INetworkPacket<T> & CustomPacketPayload> void sendToNearExcept(T packet, ServerLevel level, Vec3 pos, double radius, ServerPlayer excluded) {
        this.executeSend(packet, TYPE_TO_CLIENTBOUND_CHANNEL, ch -> ch.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p((double)pos.f_82479_, (double)pos.f_82480_, (double)pos.f_82481_, (double)radius, (ResourceKey)level.m_46472_())), (Object)packet), data -> {
            List<ServerPlayer> players = level.m_6907_().stream().filter(p -> p != excluded).toList();
            NetworkUtil.sendChunkedPacket(data, this.getTypeId(packet), players, this.getThreshold(packet));
        });
    }

    @Override
    public <T extends INetworkPacket<T> & CustomPacketPayload> void sendToTrackingEntity(T packet, Entity entity) {
        this.executeSend(packet, TYPE_TO_CLIENTBOUND_CHANNEL, ch -> ch.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), (Object)packet), data -> NetworkUtil.sendChunkedPacket(data, this.getTypeId(packet), NetworkManagerImpl.levelPlayers(entity), this.getThreshold(packet)));
    }

    @Override
    public <T extends INetworkPacket<T> & CustomPacketPayload> void sendToTrackingEntityAndSelf(T packet, Entity entity) {
        this.executeSend(packet, TYPE_TO_CLIENTBOUND_CHANNEL, ch -> ch.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), (Object)packet), data -> NetworkUtil.sendChunkedPacket(data, this.getTypeId(packet), NetworkManagerImpl.levelPlayersIncluding(entity), this.getThreshold(packet)));
    }

    @Override
    public <T extends INetworkPacket<T> & CustomPacketPayload> void sendToTrackingChunk(T packet, ServerLevel level, ChunkPos chunkPos) {
        this.executeSend(packet, TYPE_TO_CLIENTBOUND_CHANNEL, ch -> {
            LevelChunk chunk = level.m_7726_().m_62227_(chunkPos.f_45578_, chunkPos.f_45579_, false);
            if (chunk != null) {
                ch.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), (Object)packet);
            }
        }, data -> {
            List players = level.m_7726_().f_8325_.m_183262_(chunkPos, false);
            NetworkUtil.sendChunkedPacket(data, this.getTypeId(packet), players, this.getThreshold(packet));
        });
    }

    private <T extends INetworkPacket<T> & CustomPacketPayload> void executeSend(T packet, Map<CustomPacketPayload.Type<?>, SimpleChannel> channels, Consumer<SimpleChannel> normalSender, Consumer<byte[]> chunkedSender) {
        CustomPacketPayload.Type<?> type = NetworkPacketTypes.typeOf(packet.getClass());
        NetworkUtil.PacketInfo<?> info = REGISTERED.get(type);
        if (info == null) {
            return;
        }
        int threshold = NetworkAutoRegistration.getChunkThreshold(packet.getClass());
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        NetworkUtil.sendWithChunking(packet, info, buf, threshold, () -> {
            SimpleChannel ch = (SimpleChannel)channels.get(type);
            if (ch != null) {
                normalSender.accept(ch);
            }
        }, chunkedSender);
    }

    private int getThreshold(INetworkPacket<?> packet) {
        return NetworkAutoRegistration.getChunkThreshold(packet.getClass());
    }

    private ResourceLocation getTypeId(INetworkPacket<?> packet) {
        return NetworkPacketTypes.typeOf(packet.getClass()).id();
    }
}

