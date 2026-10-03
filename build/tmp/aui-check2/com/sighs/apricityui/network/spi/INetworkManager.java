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
package com.sighs.apricityui.network.spi;

import com.sighs.apricityui.network.api.CustomPacketPayload;
import com.sighs.apricityui.network.api.INetworkPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

public interface INetworkManager {
    public <T extends INetworkPacket<T> & CustomPacketPayload> void sendToPlayer(T var1, ServerPlayer var2);

    public <T extends INetworkPacket<T> & CustomPacketPayload> void sendToAll(T var1);

    public <T extends INetworkPacket<T> & CustomPacketPayload> void sendToServer(T var1);

    public <T extends INetworkPacket<T> & CustomPacketPayload> void sendToWorld(T var1, ServerLevel var2);

    public <T extends INetworkPacket<T> & CustomPacketPayload> void sendToNear(T var1, ServerLevel var2, Vec3 var3, double var4);

    public <T extends INetworkPacket<T> & CustomPacketPayload> void sendToNearExcept(T var1, ServerLevel var2, Vec3 var3, double var4, ServerPlayer var6);

    public <T extends INetworkPacket<T> & CustomPacketPayload> void sendToTrackingEntity(T var1, Entity var2);

    public <T extends INetworkPacket<T> & CustomPacketPayload> void sendToTrackingEntityAndSelf(T var1, Entity var2);

    public <T extends INetworkPacket<T> & CustomPacketPayload> void sendToTrackingChunk(T var1, ServerLevel var2, ChunkPos var3);
}

