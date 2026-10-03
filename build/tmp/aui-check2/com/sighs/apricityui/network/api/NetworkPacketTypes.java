/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package com.sighs.apricityui.network.api;

import com.sighs.apricityui.network.api.CustomPacketPayload;
import com.sighs.apricityui.network.api.INetworkPacket;
import com.sighs.apricityui.network.api.NetworkPacket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;

public final class NetworkPacketTypes {
    private static final Map<Class<?>, CustomPacketPayload.Type<?>> CACHE = new ConcurrentHashMap();
    private static final Map<ResourceLocation, Class<?>> REVERSE = new ConcurrentHashMap();

    private NetworkPacketTypes() {
    }

    public static <T extends INetworkPacket<T> & CustomPacketPayload> CustomPacketPayload.Type<T> typeOf(Class<T> packetClass) {
        CustomPacketPayload.Type type = CACHE.computeIfAbsent(packetClass, cls -> {
            NetworkPacket meta = packetClass.getAnnotation(NetworkPacket.class);
            if (meta == null) {
                throw new IllegalArgumentException("Packet class " + packetClass.getName() + " is missing @NetworkPacket");
            }
            String modId = meta.modId();
            String id = meta.id();
            if (modId == null || modId.isEmpty() || id == null || id.isEmpty()) {
                throw new IllegalArgumentException("Packet class " + packetClass.getName() + " has empty NetworkPacket.modId or id");
            }
            ResourceLocation resourceLocation = new ResourceLocation(modId, id);
            return new CustomPacketPayload.Type(resourceLocation);
        });
        REVERSE.putIfAbsent(type.id(), packetClass);
        return type;
    }

    public static Class<?> classOf(ResourceLocation id) {
        return REVERSE.get(id);
    }
}

