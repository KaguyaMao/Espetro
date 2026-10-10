/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.zip.CRC32;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.Espetro;
import org.espetro.compat.tacz.TaczGunPackSyncPayload;

public final class TaczGunPackSyncChunkPacket {
    public static final int MAX_CHUNK_BYTES = 700000;
    private static final int MAX_TOTAL_BYTES = 0x4000000;
    private static final int MAX_CHUNKS = 1024;
    private static final long ASSEMBLY_TIMEOUT_MILLIS = 60000L;
    private static final Map<UUID, Assembly> INCOMING = new HashMap<UUID, Assembly>();
    private final UUID transferId;
    private final int chunkIndex;
    private final int chunkCount;
    private final int totalBytes;
    private final long checksum;
    private final byte[] chunk;

    public TaczGunPackSyncChunkPacket(UUID transferId, int chunkIndex, int chunkCount, int totalBytes, long checksum, byte[] chunk) {
        this.transferId = transferId;
        this.chunkIndex = chunkIndex;
        this.chunkCount = chunkCount;
        this.totalBytes = totalBytes;
        this.checksum = checksum;
        this.chunk = chunk == null ? new byte[]{} : Arrays.copyOf(chunk, chunk.length);
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.m_130077_(this.transferId);
        buffer.m_130130_(this.chunkIndex);
        buffer.m_130130_(this.chunkCount);
        buffer.m_130130_(this.totalBytes);
        buffer.writeLong(this.checksum);
        buffer.m_130087_(this.chunk);
    }

    public static TaczGunPackSyncChunkPacket read(FriendlyByteBuf buffer) {
        UUID transferId = buffer.m_130259_();
        int chunkIndex = buffer.m_130242_();
        int chunkCount = buffer.m_130242_();
        int totalBytes = buffer.m_130242_();
        long checksum = buffer.readLong();
        byte[] chunk = buffer.m_130101_(700000);
        return new TaczGunPackSyncChunkPacket(transferId, chunkIndex, chunkCount, totalBytes, checksum, chunk);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (!context.getDirection().getReceptionSide().isClient()) {
            context.setPacketHandled(true);
            return;
        }
        boolean memoryConnection = context.getNetworkManager() != null && context.getNetworkManager().m_129531_();
        context.enqueueWork(() -> this.accept(memoryConnection));
        context.setPacketHandled(true);
    }

    private void accept(boolean memoryConnection) {
        try {
            this.validateMetadata();
            TaczGunPackSyncChunkPacket.discardExpiredAssemblies();
            Assembly assembly = INCOMING.computeIfAbsent(this.transferId, ignored -> new Assembly(this.chunkCount, this.totalBytes, this.checksum));
            if (!assembly.matches(this.chunkCount, this.totalBytes, this.checksum)) {
                INCOMING.remove(this.transferId);
                throw new IOException("Conflicting TaCZ sync chunk metadata");
            }
            assembly.add(this.chunkIndex, this.chunk);
            if (!assembly.complete()) {
                return;
            }
            INCOMING.remove(this.transferId);
            byte[] payload = TaczGunPackSyncPayload.join(assembly.orderedChunks(), this.totalBytes);
            CRC32 crc = new CRC32();
            crc.update(payload);
            if (crc.getValue() != this.checksum) {
                throw new IOException("TaCZ sync payload checksum mismatch");
            }
            Map<String, Map<String, String>> cache = TaczGunPackSyncPayload.decode(payload);
            TaczGunPackSyncChunkPacket.applyToTacz(cache, memoryConnection);
            Espetro.LOGGER.info("Applied chunked TaCZ gun-pack cache: {} bytes in {} chunk(s)", (Object)this.totalBytes, (Object)this.chunkCount);
        }
        catch (IOException | ReflectiveOperationException | RuntimeException e) {
            INCOMING.remove(this.transferId);
            Espetro.LOGGER.error("Failed to apply chunked TaCZ gun-pack synchronization", (Throwable)e);
        }
    }

    private void validateMetadata() throws IOException {
        if (this.transferId == null) {
            throw new IOException("Missing TaCZ sync transfer id");
        }
        if (this.chunkCount <= 0 || this.chunkCount > 1024) {
            throw new IOException("Invalid TaCZ sync chunk count: " + this.chunkCount);
        }
        if (this.chunkIndex < 0 || this.chunkIndex >= this.chunkCount) {
            throw new IOException("Invalid TaCZ sync chunk index: " + this.chunkIndex);
        }
        if (this.totalBytes < 0 || this.totalBytes > 0x4000000) {
            throw new IOException("Invalid TaCZ sync payload size: " + this.totalBytes);
        }
        if (this.chunk.length > 700000) {
            throw new IOException("TaCZ sync chunk exceeds safe packet size: " + this.chunk.length);
        }
    }

    private static void discardExpiredAssemblies() {
        long cutoff = System.currentTimeMillis() - 60000L;
        INCOMING.entrySet().removeIf(entry -> ((Assembly)entry.getValue()).createdAtMillis < cutoff);
    }

    private static void applyToTacz(Map<String, Map<String, String>> normalized, boolean memoryConnection) throws ReflectiveOperationException {
        Class<?> dataTypeClass = Class.forName("com.tacz.guns.resource.network.DataType");
        LinkedHashMap cache = new LinkedHashMap();
        for (Map.Entry<String, Map<String, String>> typeEntry : normalized.entrySet()) {
            Object dataType = Enum.valueOf(dataTypeClass, typeEntry.getKey());
            LinkedHashMap<ResourceLocation, String> entries = new LinkedHashMap<ResourceLocation, String>();
            for (Map.Entry<String, String> entry : typeEntry.getValue().entrySet()) {
                ResourceLocation id = ResourceLocation.m_135820_(entry.getKey());
                if (id == null) {
                    throw new IllegalArgumentException("Invalid TaCZ resource id: " + entry.getKey());
                }
                entries.put(id, entry.getValue());
            }
            cache.put(dataType, entries);
        }
        Class<?> commonAssetsClass = Class.forName("com.tacz.guns.resource.CommonAssetsManager");
        if (!memoryConnection) {
            commonAssetsClass.getMethod("clearInstance", new Class[0]).invoke(null, new Object[0]);
        }
        Class<?> networkCacheClass = Class.forName("com.tacz.guns.resource.network.CommonNetworkCache");
        Object networkCache = networkCacheClass.getField("INSTANCE").get(null);
        Method fromNetwork = networkCacheClass.getMethod("fromNetwork", Map.class);
        fromNetwork.invoke(networkCache, cache);
        Class<?> clientIndexClass = Class.forName("com.tacz.guns.client.resource.ClientIndexManager");
        clientIndexClass.getMethod("reload", new Class[0]).invoke(null, new Object[0]);
    }

    private static final class Assembly {
        private final int totalBytes;
        private final long checksum;
        private final byte[][] chunks;
        private final long createdAtMillis = System.currentTimeMillis();
        private int received;

        private Assembly(int chunkCount, int totalBytes, long checksum) {
            this.totalBytes = totalBytes;
            this.checksum = checksum;
            this.chunks = new byte[chunkCount][];
        }

        private boolean matches(int chunkCount, int candidateTotalBytes, long candidateChecksum) {
            return this.chunks.length == chunkCount && this.totalBytes == candidateTotalBytes && this.checksum == candidateChecksum;
        }

        private void add(int index, byte[] value) {
            if (this.chunks[index] == null) {
                this.chunks[index] = Arrays.copyOf(value, value.length);
                ++this.received;
            }
        }

        private boolean complete() {
            return this.received == this.chunks.length;
        }

        private List<byte[]> orderedChunks() {
            return new ArrayList<byte[]>(Arrays.asList(this.chunks));
        }
    }
}

