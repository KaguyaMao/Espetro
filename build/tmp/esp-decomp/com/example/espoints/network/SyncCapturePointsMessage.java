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

import com.example.espoints.capturepoint.CapturePoint;
import com.example.espoints.capturepoint.CapturePointManager;
import com.example.espoints.client.ClientBattleState;
import com.example.espoints.hud.TacticalMapHUD;
import com.example.espoints.network.NetworkHandler;
import com.example.espoints.network.PacketValidation;
import com.example.espoints.util.ModLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class SyncCapturePointsMessage {
    public static final int MAX_CAPTURE_POINTS = 64;
    private final List<CapturePoint.SerializableCapturePoint> capturePoints;

    public SyncCapturePointsMessage(List<CapturePoint.SerializableCapturePoint> capturePoints) {
        this.capturePoints = capturePoints;
    }

    public static void encode(SyncCapturePointsMessage msg, FriendlyByteBuf buf) {
        PacketValidation.checkedCount(msg.capturePoints.size(), 64, "capture point");
        buf.m_130130_(msg.capturePoints.size());
        for (CapturePoint.SerializableCapturePoint point : msg.capturePoints) {
            point.toNetwork(buf);
        }
    }

    public static SyncCapturePointsMessage decode(FriendlyByteBuf buf) {
        int size = buf.m_130242_();
        if (size < 0 || size > 64) {
            throw new IllegalArgumentException("Invalid capture point count: " + size);
        }
        ArrayList<CapturePoint.SerializableCapturePoint> points = new ArrayList<CapturePoint.SerializableCapturePoint>(size);
        for (int i = 0; i < size; ++i) {
            try {
                points.add(CapturePoint.SerializableCapturePoint.fromNetwork(buf));
                continue;
            }
            catch (Exception e) {
                ModLogger.decodeError("Failed to decode capture point: " + e.getMessage());
                throw e;
            }
        }
        return new SyncCapturePointsMessage(points);
    }

    public static void handle(SyncCapturePointsMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                TacticalMapHUD.getInstance().syncVisibleCapturePointsFromServer(ClientBattleState.get().replaceCapturePoints(msg.capturePoints));
            }
            catch (Exception e) {
                ModLogger.syncError("Failed to handle sync message: " + e.getMessage());
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void sendToPlayer(ServerPlayer player) {
        try {
            List<CapturePoint.SerializableCapturePoint> points = CapturePointManager.getInstance().getAllSerializablePoints();
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SyncCapturePointsMessage(points));
        }
        catch (Exception e) {
            ModLogger.syncError("Failed to send sync message to player: " + e.getMessage());
        }
    }

    public static void sendToPlayer(ServerPlayer player, List<CapturePoint.SerializableCapturePoint> points) {
        try {
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SyncCapturePointsMessage(points));
        }
        catch (Exception e) {
            ModLogger.syncError("Failed to send sync message to player: " + e.getMessage());
        }
    }

    public static void broadcastToAll() {
        try {
            List<CapturePoint.SerializableCapturePoint> points = CapturePointManager.getInstance().getAllSerializablePoints();
            NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), (Object)new SyncCapturePointsMessage(points));
        }
        catch (Exception e) {
            ModLogger.syncError("Failed to broadcast sync message: " + e.getMessage());
        }
    }

    public static void broadcastToAll(List<CapturePoint.SerializableCapturePoint> points) {
        try {
            NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), (Object)new SyncCapturePointsMessage(points));
        }
        catch (Exception e) {
            ModLogger.syncError("Failed to broadcast sync message: " + e.getMessage());
        }
    }
}

