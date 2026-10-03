/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.CompositeByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.util.ReferenceCountUtil
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 */
package com.sighs.apricityui.network.chunk;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.network.api.CustomPacketPayload;
import com.sighs.apricityui.network.api.INetworkContext;
import com.sighs.apricityui.network.api.INetworkPacket;
import com.sighs.apricityui.network.api.NetworkPacketTypes;
import com.sighs.apricityui.network.codec.StreamCodec;
import com.sighs.apricityui.network.serialization.NetworkSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import java.util.BitSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public final class GenericChunkAssembler {
    private static final Map<UUID, Session> sessions = new ConcurrentHashMap<UUID, Session>();
    private static final AtomicLong BYTES_IN_ASSEMBLY = new AtomicLong(0L);
    private static final long QUOTA_BYTES = 0x4000000L;
    private static final long EXPIRE_MS = 30000L;
    private static final ScheduledExecutorService CLEANUP = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "GenericChunkAssembler-Cleanup");
        t.setDaemon(true);
        return t;
    });

    private GenericChunkAssembler() {
    }

    public static void receiveChunk(UUID sessionId, int totalSize, short chunkIndex, short totalChunks, ResourceLocation typeId, byte[] chunkData, INetworkContext context) {
        block8: {
            Session s = sessions.computeIfAbsent(sessionId, id -> {
                long after = BYTES_IN_ASSEMBLY.addAndGet(totalSize);
                if (after > 0x4000000L) {
                    BYTES_IN_ASSEMBLY.addAndGet(-totalSize);
                    ApricityUI.LOGGER.warn("Chunk assembly quota exceeded: {} bytes in assembly, reject session {}", (Object)after, id);
                    return null;
                }
                return new Session(totalChunks, totalSize, typeId);
            });
            if (s == null) {
                return;
            }
            if (s.add(chunkIndex, chunkData)) {
                try {
                    CompositeByteBuf composite = s.assembleComposite();
                    Class<?> clazz = NetworkPacketTypes.classOf(typeId);
                    if (clazz == null || !CustomPacketPayload.class.isAssignableFrom(clazz)) {
                        ApricityUI.LOGGER.warn("Chunk target {} is not a registered CustomPacketPayload", (Object)typeId);
                        ReferenceCountUtil.release((Object)composite);
                        sessions.remove(sessionId);
                        return;
                    }
                    Class<?> c = clazz;
                    StreamCodec<FriendlyByteBuf, ?> codec = NetworkSerialization.autoCodec(c);
                    FriendlyByteBuf buf = new FriendlyByteBuf((ByteBuf)composite);
                    CustomPacketPayload payload = (CustomPacketPayload)codec.decode(buf);
                    ReferenceCountUtil.release((Object)composite);
                    sessions.remove(sessionId);
                    try {
                        if (payload instanceof INetworkPacket) {
                            INetworkPacket p = (INetworkPacket)payload;
                            context.enqueueWork(() -> p.handle(context));
                            break block8;
                        }
                        ApricityUI.LOGGER.warn("Decoded payload {} does not implement INetworkPacket", (Object)typeId);
                    }
                    catch (Throwable t) {
                        ApricityUI.LOGGER.error("Failed to dispatch reassembled payload {}", (Object)typeId, (Object)t);
                    }
                }
                catch (Throwable t) {
                    ApricityUI.LOGGER.error("Failed to reassemble payload {}", (Object)typeId, (Object)t);
                    sessions.remove(sessionId);
                }
            }
        }
    }

    static {
        CLEANUP.scheduleAtFixedRate(() -> {
            try {
                sessions.entrySet().removeIf(e -> {
                    Session s = (Session)e.getValue();
                    if (s.expired()) {
                        ApricityUI.LOGGER.debug("Cleanup expired chunk session {}", e.getKey());
                        s.releaseAll();
                        return true;
                    }
                    return false;
                });
            }
            catch (Throwable t) {
                ApricityUI.LOGGER.warn("Cleanup task failed", t);
            }
        }, 30L, 30L, TimeUnit.SECONDS);
    }

    private static final class Session {
        final int total;
        final int totalSize;
        final ResourceLocation typeId;
        final BitSet received;
        final ByteBuf[] parts;
        final long startMs;
        int count;

        Session(int totalChunks, int totalSize, ResourceLocation typeId) {
            this.total = totalChunks;
            this.totalSize = totalSize;
            this.typeId = typeId;
            this.received = new BitSet(totalChunks);
            this.parts = new ByteBuf[totalChunks];
            this.count = 0;
            this.startMs = System.currentTimeMillis();
        }

        synchronized boolean add(int idx, byte[] data) {
            if (idx < 0 || idx >= this.total) {
                ApricityUI.LOGGER.warn("Invalid chunk index {} of {} for {}", new Object[]{idx, this.total, this.typeId});
                return false;
            }
            if (!this.received.get(idx)) {
                this.parts[idx] = Unpooled.wrappedBuffer((byte[])data);
                this.received.set(idx);
                ++this.count;
            }
            return this.count == this.total;
        }

        CompositeByteBuf assembleComposite() {
            CompositeByteBuf composite = Unpooled.compositeBuffer((int)this.total);
            for (int i = 0; i < this.total; ++i) {
                if (this.parts[i] == null) {
                    throw new IllegalStateException("Missing chunk " + i + " for " + this.typeId);
                }
                composite.addComponent(true, this.parts[i].retain());
            }
            BYTES_IN_ASSEMBLY.addAndGet(-this.totalSize);
            return composite;
        }

        boolean expired() {
            return System.currentTimeMillis() - this.startMs > 30000L;
        }

        void releaseAll() {
            long released = 0L;
            for (int i = 0; i < this.total; ++i) {
                if (this.parts[i] == null) continue;
                released += (long)this.parts[i].readableBytes();
                ReferenceCountUtil.release((Object)this.parts[i]);
                this.parts[i] = null;
            }
            BYTES_IN_ASSEMBLY.addAndGet(-Math.max(0L, (long)this.totalSize - released));
        }
    }
}

