/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.sighs.apricityui.network.api;

import com.sighs.apricityui.network.api.CustomPacketPayload;
import com.sighs.apricityui.network.api.INetworkPacket;
import com.sighs.apricityui.network.api.NetworkPacket;
import com.sighs.apricityui.util.AnnotationScanUtil;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NetworkAutoRegistration {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<Class<? extends INetworkPacket<?>>> REGISTERED_PACKET_CLASSES = ConcurrentHashMap.newKeySet();
    private static final CopyOnWriteArrayList<Consumer<Class<? extends INetworkPacket<?>>>> PACKET_LISTENERS = new CopyOnWriteArrayList();

    private NetworkAutoRegistration() {
    }

    public static void addPacketRegistrationListener(Consumer<Class<? extends INetworkPacket<?>>> listener) {
        if (listener == null) {
            return;
        }
        PACKET_LISTENERS.addIfAbsent(listener);
    }

    public static void removePacketRegistrationListener(Consumer<Class<? extends INetworkPacket<?>>> listener) {
        if (listener == null) {
            return;
        }
        PACKET_LISTENERS.remove(listener);
    }

    public static Set<Class<? extends INetworkPacket<?>>> findAllAnnotatedPackets() {
        Set<Class<?>> classes;
        Predicate<Class<?>> filter = AnnotationScanUtil.nonAbstractNonInterface().and(INetworkPacket.class::isAssignableFrom).and(CustomPacketPayload.class::isAssignableFrom);
        try {
            classes = AnnotationScanUtil.findAnnotatedClasses(NetworkPacket.class, filter);
        }
        catch (Throwable t) {
            LOGGER.error("[NetworkAutoReg] Failed to scan Forge metadata", t);
            return Set.copyOf(REGISTERED_PACKET_CLASSES);
        }
        int added = 0;
        int skipped = 0;
        for (Class<?> clazz : classes) {
            Class<?> packetClass = clazz;
            if (!REGISTERED_PACKET_CLASSES.add(packetClass)) {
                ++skipped;
                continue;
            }
            ++added;
            NetworkAutoRegistration.notifyPacketDiscovered(packetClass);
            LOGGER.debug("[NetworkAutoReg] Found packet: {} (chunkThreshold={})", (Object)packetClass.getName(), (Object)NetworkAutoRegistration.getChunkThreshold(packetClass));
        }
        LOGGER.info("[NetworkAutoReg] Metadata scan found: {} | added: {} | skipped: {}", (Object)classes.size(), (Object)added, (Object)skipped);
        return Set.copyOf(REGISTERED_PACKET_CLASSES);
    }

    private static void notifyPacketDiscovered(Class<? extends INetworkPacket<?>> packetClass) {
        if (PACKET_LISTENERS.isEmpty()) {
            return;
        }
        for (Consumer<Class<INetworkPacket<?>>> listener : PACKET_LISTENERS) {
            try {
                listener.accept(packetClass);
            }
            catch (Throwable t) {
                LOGGER.warn("[NetworkAutoReg] Packet listener failed for {}", (Object)packetClass.getName(), (Object)t);
            }
        }
    }

    public static int getChunkThreshold(Class<?> clazz) {
        NetworkPacket annotation = clazz.getAnnotation(NetworkPacket.class);
        return annotation != null ? annotation.chunkThreshold() : 0;
    }
}

