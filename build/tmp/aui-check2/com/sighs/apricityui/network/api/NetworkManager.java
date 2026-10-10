/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.phys.Vec3
 */
package com.sighs.apricityui.network.api;

import com.sighs.apricityui.network.api.CustomPacketPayload;
import com.sighs.apricityui.network.api.INetworkPacket;
import com.sighs.apricityui.network.spi.INetworkManager;
import java.util.ServiceLoader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

public class NetworkManager {
    private static final INetworkManager IMPL = ServiceLoader.load(INetworkManager.class).findFirst().orElseThrow(() -> new IllegalStateException("No INetworkManager implementation found"));

    public static <T extends INetworkPacket<T> & CustomPacketPayload> void sendToPlayer(T packet, ServerPlayer player) {
        IMPL.sendToPlayer(packet, player);
    }

    public static <T extends INetworkPacket<T> & CustomPacketPayload> void sendToAll(T packet) {
        IMPL.sendToAll(packet);
    }

    public static <T extends INetworkPacket<T> & CustomPacketPayload> void sendToServer(T packet) {
        IMPL.sendToServer(packet);
    }

    public static <T extends INetworkPacket<T> & CustomPacketPayload> void sendToWorld(T packet, ServerLevel level) {
        IMPL.sendToWorld(packet, level);
    }

    public static <T extends INetworkPacket<T> & CustomPacketPayload> void sendToNear(T packet, ServerLevel level, Vec3 pos, double radius) {
        IMPL.sendToNear(packet, level, pos, radius);
    }

    public static <T extends INetworkPacket<T> & CustomPacketPayload> void sendToNearExcept(T packet, ServerLevel level, Vec3 pos, double radius, ServerPlayer excluded) {
        IMPL.sendToNearExcept(packet, level, pos, radius, excluded);
    }

    public static <T extends INetworkPacket<T> & CustomPacketPayload> void sendToTrackingEntity(T packet, Entity entity) {
        IMPL.sendToTrackingEntity(packet, entity);
    }

    public static <T extends INetworkPacket<T> & CustomPacketPayload> void sendToTrackingEntityAndSelf(T packet, Entity entity) {
        IMPL.sendToTrackingEntityAndSelf(packet, entity);
    }

    public static <T extends INetworkPacket<T> & CustomPacketPayload> void sendToTrackingChunk(T packet, ServerLevel level, ChunkPos chunkPos) {
        IMPL.sendToTrackingChunk(packet, level, chunkPos);
    }
}

