/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.event.OnDatapackSyncEvent
 *  net.minecraftforge.network.PacketDistributor
 */
package org.espetro.compat.tacz;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.CRC32;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.Espetro;
import org.espetro.compat.tacz.TaczGunPackSyncPayload;
import org.espetro.network.NetworkManager;
import org.espetro.network.TaczGunPackSyncChunkPacket;

public final class TaczGunPackSyncCompat {
    private static final String COMMON_ASSETS_MANAGER = "com.tacz.guns.resource.CommonAssetsManager";
    private static final int MAX_CHUNK_BYTES = 700000;

    private TaczGunPackSyncCompat() {
    }

    public static boolean sendChunked(OnDatapackSyncEvent event) {
        PreparedSync prepared;
        try {
            prepared = TaczGunPackSyncCompat.prepare();
        }
        catch (IOException | ReflectiveOperationException | RuntimeException e) {
            Espetro.LOGGER.error("Unable to prepare chunked TaCZ gun-pack synchronization", (Throwable)e);
            return false;
        }
        List<ServerPlayer> recipients = event.getPlayer() != null ? Collections.singletonList(event.getPlayer()) : new ArrayList<ServerPlayer>(event.getPlayerList().m_11314_());
        try {
            for (ServerPlayer player : recipients) {
                TaczGunPackSyncCompat.sendToPlayer(player, prepared);
            }
        }
        catch (RuntimeException e) {
            Espetro.LOGGER.error("Failed while sending chunked TaCZ gun-pack synchronization", (Throwable)e);
        }
        return true;
    }

    private static PreparedSync prepare() throws ReflectiveOperationException, IOException {
        Class<?> managerClass = Class.forName(COMMON_ASSETS_MANAGER);
        Method getInstance = managerClass.getMethod("getInstance", new Class[0]);
        Object manager = getInstance.invoke(null, new Object[0]);
        if (manager == null) {
            throw new IllegalStateException("TaCZ CommonAssetsManager is not initialized");
        }
        Method getNetworkCache = managerClass.getMethod("getNetworkCache", new Class[0]);
        Object rawCache = getNetworkCache.invoke(manager, new Object[0]);
        Map<String, Map<String, String>> cache = TaczGunPackSyncCompat.normalizeCache(rawCache);
        byte[] payload = TaczGunPackSyncPayload.encode(cache);
        List<byte[]> chunks = TaczGunPackSyncPayload.split(payload, 700000);
        CRC32 crc = new CRC32();
        crc.update(payload);
        Espetro.LOGGER.info("TaCZ gun-pack cache prepared: {} bytes in {} chunk(s)", (Object)payload.length, (Object)chunks.size());
        return new PreparedSync(payload.length, crc.getValue(), chunks);
    }

    private static void sendToPlayer(ServerPlayer player, PreparedSync prepared) {
        UUID transferId = UUID.randomUUID();
        for (int index = 0; index < prepared.chunks().size(); ++index) {
            TaczGunPackSyncChunkPacket packet = new TaczGunPackSyncChunkPacket(transferId, index, prepared.chunks().size(), prepared.payloadLength(), prepared.checksum(), prepared.chunks().get(index));
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
        }
    }

    private static Map<String, Map<String, String>> normalizeCache(Object rawCache) {
        if (!(rawCache instanceof Map)) {
            throw new IllegalArgumentException("Unexpected TaCZ network cache type");
        }
        Map outer = (Map)rawCache;
        LinkedHashMap<String, Map<String, String>> normalized = new LinkedHashMap<String, Map<String, String>>();
        for (Map.Entry typeEntry : outer.entrySet()) {
            String string;
            Object typeKey = typeEntry.getKey();
            if (typeKey instanceof Enum) {
                Enum enumKey = (Enum)typeKey;
                string = enumKey.name();
            } else {
                string = String.valueOf(typeKey);
            }
            String typeName = string;
            Object v = typeEntry.getValue();
            if (!(v instanceof Map)) {
                throw new IllegalArgumentException("Unexpected TaCZ cache entries for " + typeName);
            }
            Map inner = (Map)v;
            LinkedHashMap<String, String> entries = new LinkedHashMap<String, String>();
            for (Map.Entry entry : inner.entrySet()) {
                entries.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
            normalized.put(typeName, entries);
        }
        return normalized;
    }

    private record PreparedSync(int payloadLength, long checksum, List<byte[]> chunks) {
    }
}

