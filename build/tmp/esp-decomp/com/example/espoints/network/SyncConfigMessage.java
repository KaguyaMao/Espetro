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

import com.example.espoints.config.ModConfig;
import com.example.espoints.network.NetworkHandler;
import com.example.espoints.util.ModLogger;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class SyncConfigMessage {
    private final boolean enableTeams;
    private final boolean enableOperationMode;
    private final boolean enableFriendlyFirePenalty;
    private final int friendlyFirePenalty;
    private final double lowReinforcementThreshold;

    public SyncConfigMessage() {
        this.enableTeams = true;
        this.enableOperationMode = true;
        this.enableFriendlyFirePenalty = (Boolean)ModConfig.enableFriendlyFirePenalty.get();
        this.friendlyFirePenalty = (Integer)ModConfig.friendlyFirePenalty.get();
        this.lowReinforcementThreshold = (Double)ModConfig.lowReinforcementThreshold.get();
    }

    public SyncConfigMessage(FriendlyByteBuf buf) {
        this.enableTeams = buf.readBoolean();
        this.enableOperationMode = buf.readBoolean();
        this.enableFriendlyFirePenalty = buf.readBoolean();
        this.friendlyFirePenalty = buf.readInt();
        this.lowReinforcementThreshold = buf.readDouble();
        SyncConfigMessage.validate(this.friendlyFirePenalty, this.lowReinforcementThreshold);
    }

    public static void encode(SyncConfigMessage msg, FriendlyByteBuf buf) {
        SyncConfigMessage.validate(msg.friendlyFirePenalty, msg.lowReinforcementThreshold);
        buf.writeBoolean(msg.enableTeams);
        buf.writeBoolean(msg.enableOperationMode);
        buf.writeBoolean(msg.enableFriendlyFirePenalty);
        buf.writeInt(msg.friendlyFirePenalty);
        buf.writeDouble(msg.lowReinforcementThreshold);
    }

    private static void validate(int friendlyFirePenalty, double threshold) {
        if (friendlyFirePenalty < 0 || friendlyFirePenalty > 10000000 || !Double.isFinite(threshold) || threshold < 0.0 || threshold > 100.0) {
            throw new IllegalArgumentException("Invalid common configuration payload");
        }
    }

    public static SyncConfigMessage decode(FriendlyByteBuf buf) {
        return new SyncConfigMessage(buf);
    }

    public static void handle(SyncConfigMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            msg.updateConfig(true, true, msg.enableFriendlyFirePenalty, msg.friendlyFirePenalty, msg.lowReinforcementThreshold);
            ModLogger.info("\u5ba2\u6237\u7aef\u914d\u7f6e\u5df2\u540c\u6b65\uff1a\u884c\u52a8\u6a21\u5f0f\u56fa\u5b9a\u542f\u7528\uff0c\u961f\u4f0d\u56fa\u5b9a\u4f7f\u7528 Espetro \u9635\u8425");
        });
        ctx.get().setPacketHandled(true);
    }

    private void updateConfig(boolean enableTeams, boolean enableOperationMode, boolean enableFriendlyFirePenalty, int friendlyFirePenalty, double lowReinforcementThreshold) {
        try {
            ModConfig.enableTeams.set((Object)enableTeams);
            ModConfig.enableOperationMode.set((Object)enableOperationMode);
            ModConfig.enableFriendlyFirePenalty.set((Object)enableFriendlyFirePenalty);
            ModConfig.friendlyFirePenalty.set((Object)friendlyFirePenalty);
            ModConfig.lowReinforcementThreshold.set((Object)lowReinforcementThreshold);
        }
        catch (Exception e) {
            ModLogger.error("\u66f4\u65b0\u914d\u7f6e\u65f6\u53d1\u751f\u5f02\u5e38: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void sendToPlayer(ServerPlayer player) {
        try {
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SyncConfigMessage());
            ModLogger.info("\u5df2\u5411\u73a9\u5bb6 " + player.m_7755_().getString() + " \u53d1\u9001\u914d\u7f6e\u540c\u6b65\u6d88\u606f");
        }
        catch (Exception e) {
            ModLogger.error("\u5411\u73a9\u5bb6\u53d1\u9001\u914d\u7f6e\u540c\u6b65\u6d88\u606f\u5931\u8d25: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void broadcastToAll() {
        try {
            NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), (Object)new SyncConfigMessage());
            ModLogger.info("\u5df2\u5411\u6240\u6709\u73a9\u5bb6\u5e7f\u64ad\u914d\u7f6e\u540c\u6b65\u6d88\u606f");
        }
        catch (Exception e) {
            ModLogger.error("\u5e7f\u64ad\u914d\u7f6e\u540c\u6b65\u6d88\u606f\u5931\u8d25: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

