/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 */
package com.sighs.apricityui.network.util;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.network.api.CustomPacketPayload;
import com.sighs.apricityui.network.api.INetworkPacket;
import com.sighs.apricityui.network.chunk.GenericChunkPacket;
import com.sighs.apricityui.network.codec.StreamCodec;
import io.netty.buffer.Unpooled;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class NetworkUtil {
    private NetworkUtil() {
    }

    public static void sendChunkedPacket(byte[] data, ResourceLocation typeId, Iterable<ServerPlayer> players, int chunkSize) {
        try {
            NetworkUtil.forEachChunk(data, typeId, chunkSize, chunk -> {
                for (ServerPlayer player : players) {
                    chunk.sendTo(player);
                }
            });
        }
        catch (Exception e) {
            ApricityUI.LOGGER.error("Failed to send chunked packet {}: {}", new Object[]{typeId, e.getMessage(), e});
        }
    }

    public static void sendChunkedPacketToAll(byte[] data, ResourceLocation typeId, int chunkSize) {
        try {
            NetworkUtil.forEachChunk(data, typeId, chunkSize, INetworkPacket::sendToAll);
        }
        catch (Exception e) {
            ApricityUI.LOGGER.error("Failed broadcast chunked {}: {}", new Object[]{typeId, e.getMessage(), e});
        }
    }

    public static void sendChunkedPacketToServer(byte[] data, ResourceLocation typeId, int chunkSize) {
        try {
            NetworkUtil.forEachChunk(data, typeId, chunkSize, INetworkPacket::sendToServer);
        }
        catch (Exception e) {
            ApricityUI.LOGGER.error("Failed client chunked {}: {}", new Object[]{typeId, e.getMessage(), e});
        }
    }

    public static <T extends INetworkPacket<T>> byte[] encodePacket(T packet, PacketInfo<T> packetInfo, FriendlyByteBuf buf) {
        packetInfo.codec().encode(buf, packet);
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        return data;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T extends INetworkPacket<T>> void sendWithChunking(T packet, PacketInfo<T> packetInfo, FriendlyByteBuf buf, int threshold, Runnable directSendAction, Consumer<byte[]> chunkedSendAction) {
        try {
            byte[] data = NetworkUtil.encodePacket(packet, packetInfo, buf);
            if (threshold <= 0 || data.length <= threshold) {
                directSendAction.run();
            } else {
                chunkedSendAction.accept(data);
            }
        }
        finally {
            buf.release();
        }
    }

    public static FriendlyByteBuf createClientBuffer() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    public static FriendlyByteBuf createServerBuffer(ServerPlayer player) {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    public static FriendlyByteBuf createBufferFromFirstPlayer(Collection<ServerPlayer> players) {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    public static void forEachChunk(byte[] data, ResourceLocation typeId, int chunkSize, Consumer<GenericChunkPacket> consumer) {
        UUID sessionId = UUID.randomUUID();
        int totalChunks = (int)Math.ceil((double)data.length / (double)chunkSize);
        for (int i = 0; i < totalChunks; ++i) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, data.length);
            byte[] chunkData = Arrays.copyOfRange(data, start, end);
            GenericChunkPacket chunk = new GenericChunkPacket(sessionId, data.length, (short)i, (short)totalChunks, typeId, chunkData);
            consumer.accept(chunk);
        }
    }

    public record PacketInfo<T extends INetworkPacket<T> & CustomPacketPayload>(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> codec) {
    }
}

