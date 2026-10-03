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
import com.sighs.apricityui.network.api.INetworkContext;
import com.sighs.apricityui.network.api.NetworkManager;
import com.sighs.apricityui.network.api.NetworkPacket;
import com.sighs.apricityui.network.api.NetworkPacketTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

public interface INetworkPacket<T extends INetworkPacket<T> & CustomPacketPayload>
extends CustomPacketPayload {
    public void handle(INetworkContext var1);

    default public CustomPacketPayload.Type<T> type() {
        Class<?> clazz = this.getClass();
        if (clazz.isAnnotationPresent(NetworkPacket.class)) {
            return NetworkPacketTypes.typeOf(clazz);
        }
        throw new IllegalStateException("Packet class " + clazz.getName() + " is missing @NetworkPacket");
    }

    default public void sendTo(ServerPlayer player) {
        NetworkManager.sendToPlayer(this.self(), player);
    }

    default public void sendToAll() {
        NetworkManager.sendToAll(this.self());
    }

    default public void sendToServer() {
        NetworkManager.sendToServer(this.self());
    }

    default public void sendToWorld(ServerLevel level) {
        NetworkManager.sendToWorld(this.self(), level);
    }

    default public void sendToNear(ServerLevel level, Vec3 pos, double radius) {
        NetworkManager.sendToNear(this.self(), level, pos, radius);
    }

    default public void sendToNearExcept(ServerLevel level, Vec3 pos, double radius, ServerPlayer excluded) {
        NetworkManager.sendToNearExcept(this.self(), level, pos, radius, excluded);
    }

    default public void sendToTrackingEntity(Entity entity) {
        NetworkManager.sendToTrackingEntity(this.self(), entity);
    }

    default public void sendToTrackingEntityAndSelf(Entity entity) {
        NetworkManager.sendToTrackingEntityAndSelf(this.self(), entity);
    }

    default public void sendToTrackingChunk(ServerLevel level, ChunkPos chunkPos) {
        NetworkManager.sendToTrackingChunk(this.self(), level, chunkPos);
    }

    default public T self() {
        return (T)this;
    }
}

