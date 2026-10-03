/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package com.example.espoints.network;

import com.example.espoints.config.MapPlayerDisplayConfig;
import com.example.espoints.network.NetworkHandler;
import com.example.espoints.util.ModLogger;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class SyncMapPlayerDisplayMessage {
    private final boolean showPlayerLocations;

    public SyncMapPlayerDisplayMessage(boolean showPlayerLocations) {
        this.showPlayerLocations = showPlayerLocations;
    }

    public SyncMapPlayerDisplayMessage(FriendlyByteBuf buf) {
        this.showPlayerLocations = buf.readBoolean();
    }

    public static void encode(SyncMapPlayerDisplayMessage msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.showPlayerLocations);
    }

    public static SyncMapPlayerDisplayMessage decode(FriendlyByteBuf buf) {
        return new SyncMapPlayerDisplayMessage(buf);
    }

    public static void handle(SyncMapPlayerDisplayMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            MapPlayerDisplayConfig.getInstance().setShowPlayerLocations(msg.showPlayerLocations);
            ModLogger.debug("\u5ba2\u6237\u7aef\u5730\u56fe\u73a9\u5bb6\u663e\u793a\u914d\u7f6e\u5df2\u540c\u6b65: showPlayerLocations = " + msg.showPlayerLocations);
        });
        ctx.get().setPacketHandled(true);
    }

    public static void sendToPlayer(ServerPlayer player) {
        try {
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SyncMapPlayerDisplayMessage(MapPlayerDisplayConfig.getInstance().isShowPlayerLocations()));
        }
        catch (Exception e) {
            ModLogger.error("\u5411\u73a9\u5bb6\u53d1\u9001\u5730\u56fe\u73a9\u5bb6\u663e\u793a\u914d\u7f6e\u540c\u6b65\u6d88\u606f\u5931\u8d25: " + e.getMessage());
        }
    }

    public static void broadcastToAll() {
        try {
            NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), (Object)new SyncMapPlayerDisplayMessage(MapPlayerDisplayConfig.getInstance().isShowPlayerLocations()));
            ModLogger.debug("\u5df2\u5411\u6240\u6709\u73a9\u5bb6\u5e7f\u64ad\u5730\u56fe\u73a9\u5bb6\u663e\u793a\u914d\u7f6e\u540c\u6b65\u6d88\u606f");
        }
        catch (Exception e) {
            ModLogger.error("\u5e7f\u64ad\u5730\u56fe\u73a9\u5bb6\u663e\u793a\u914d\u7f6e\u540c\u6b65\u6d88\u606f\u5931\u8d25: " + e.getMessage());
        }
    }
}

