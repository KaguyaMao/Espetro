/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.example.espoints.network;

import com.example.espoints.network.PacketValidation;
import com.example.espoints.tactical.ClientTacticalMarkerState;
import com.example.espoints.tactical.TacticalMarker;
import com.example.espoints.tactical.TacticalMarkerType;
import com.example.espoints.util.ModLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public final class TacticalMarkerDeltaMessage {
    private static final int MAX_CHANGES = 64;
    private static final double FIXED_POINT = 16.0;
    private final boolean clear;
    private final List<UUID> removals;
    private final List<TacticalMarker> additions;

    public TacticalMarkerDeltaMessage(boolean clear, List<UUID> removals, List<TacticalMarker> additions) {
        this.clear = clear;
        this.removals = removals == null ? List.of() : List.copyOf(removals);
        this.additions = additions == null ? List.of() : List.copyOf(additions);
    }

    public static TacticalMarkerDeltaMessage add(List<TacticalMarker> additions, List<UUID> removals) {
        return new TacticalMarkerDeltaMessage(false, removals, additions);
    }

    public static TacticalMarkerDeltaMessage clearAll() {
        return new TacticalMarkerDeltaMessage(true, List.of(), List.of());
    }

    public static void encode(TacticalMarkerDeltaMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.clear);
        buf.m_130130_(message.removals.size());
        for (UUID id : message.removals) {
            buf.m_130077_(id);
        }
        buf.m_130130_(message.additions.size());
        long now = System.currentTimeMillis();
        for (TacticalMarker marker : message.additions) {
            buf.m_130077_(marker.id());
            buf.writeByte(marker.type().ordinal());
            buf.writeInt(TacticalMarkerDeltaMessage.toFixed(marker.x()));
            buf.writeInt(TacticalMarkerDeltaMessage.toFixed(marker.y()));
            buf.writeInt(TacticalMarkerDeltaMessage.toFixed(marker.z()));
            buf.m_130072_(marker.team(), 16);
            buf.m_130077_(marker.ownerId());
            buf.m_130072_(marker.ownerName(), 64);
            buf.m_130103_(Math.max(0L, now - marker.createdAtMillis()));
            buf.m_130130_(marker.ownerSquadId() + 1);
            buf.writeBoolean(marker.ownerCommander());
        }
    }

    public static TacticalMarkerDeltaMessage decode(FriendlyByteBuf buf) {
        boolean clear = buf.readBoolean();
        int removeCount = TacticalMarkerDeltaMessage.bounded(buf.m_130242_());
        ArrayList<UUID> removals = new ArrayList<UUID>(removeCount);
        for (int i = 0; i < removeCount; ++i) {
            removals.add(buf.m_130259_());
        }
        int addCount = TacticalMarkerDeltaMessage.bounded(buf.m_130242_());
        ArrayList<TacticalMarker> additions = new ArrayList<TacticalMarker>(addCount);
        long now = System.currentTimeMillis();
        for (int i = 0; i < addCount; ++i) {
            UUID id = buf.m_130259_();
            TacticalMarkerType type = TacticalMarkerType.fromNetworkId(buf.readUnsignedByte());
            double x = (double)buf.readInt() / 16.0;
            double y = (double)buf.readInt() / 16.0;
            double z = (double)buf.readInt() / 16.0;
            String team = buf.m_130136_(16);
            UUID ownerId = buf.m_130259_();
            String ownerName = buf.m_130136_(64);
            long createdAt = now - Math.max(0L, buf.m_130258_());
            int squadId = buf.m_130242_() - 1;
            boolean commander = buf.readBoolean();
            if (type == null) continue;
            additions.add(new TacticalMarker(id, type, x, y, z, team, ownerId, ownerName, createdAt, squadId, commander));
        }
        return new TacticalMarkerDeltaMessage(clear, removals, additions);
    }

    private static int bounded(int value) {
        if (value < 0 || value > 64) {
            throw new IllegalArgumentException("Invalid tactical marker delta size: " + value);
        }
        return value;
    }

    private static int toFixed(double coordinate) {
        PacketValidation.checkedCoordinate(coordinate, "marker");
        double scaled = coordinate * 16.0;
        if (scaled < -2.147483648E9 || scaled > 2.147483647E9) {
            throw new IllegalArgumentException("Marker coordinate exceeds fixed-point range");
        }
        return (int)Math.round(scaled);
    }

    public static void handle(TacticalMarkerDeltaMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (!context.getDirection().getReceptionSide().isClient()) {
                return;
            }
            ClientTacticalMarkerState.applyDelta(message.clear, message.removals, message.additions);
            TacticalMarkerDeltaMessage.syncMapHud(ClientTacticalMarkerState.getMarkers());
        });
        context.setPacketHandled(true);
    }

    private static void syncMapHud(List<TacticalMarker> markers) {
        try {
            Class<?> hudClass = Class.forName("com.example.espoints.hud.TacticalMapHUD");
            Object hud = hudClass.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            hudClass.getMethod("syncTacticalMarkersFromServer", List.class).invoke(hud, markers);
        }
        catch (ReflectiveOperationException e) {
            ModLogger.syncError("Failed to apply tactical marker delta to map HUD: " + e.getMessage());
        }
    }
}

