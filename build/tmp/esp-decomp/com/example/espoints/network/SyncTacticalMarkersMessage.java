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

public class SyncTacticalMarkersMessage {
    private static final int MAX_MARKERS = 64;
    private final List<TacticalMarker> markers;

    public SyncTacticalMarkersMessage(List<TacticalMarker> markers) {
        this.markers = markers == null ? List.of() : List.copyOf(markers);
    }

    public static void encode(SyncTacticalMarkersMessage message, FriendlyByteBuf buf) {
        buf.m_130130_(message.markers.size());
        long now = System.currentTimeMillis();
        if (!message.markers.isEmpty()) {
            buf.m_130072_(message.markers.get(0).team(), 16);
        }
        for (TacticalMarker marker : message.markers) {
            buf.m_130077_(marker.id());
            buf.m_130130_(marker.type().ordinal());
            buf.writeDouble(marker.x());
            buf.writeDouble(marker.y());
            buf.writeDouble(marker.z());
            buf.m_130077_(marker.ownerId());
            buf.m_130072_(marker.ownerName(), 64);
            buf.m_130103_(Math.max(0L, now - marker.createdAtMillis()));
            buf.m_130130_(marker.ownerSquadId());
            buf.writeBoolean(marker.ownerCommander());
        }
    }

    public static SyncTacticalMarkersMessage decode(FriendlyByteBuf buf) {
        int count = buf.m_130242_();
        if (count < 0 || count > 64) {
            throw new IllegalArgumentException("Invalid tactical marker count: " + count);
        }
        String team = count == 0 ? "" : buf.m_130136_(16);
        ArrayList<TacticalMarker> markers = new ArrayList<TacticalMarker>(count);
        for (int i = 0; i < count; ++i) {
            UUID id = buf.m_130259_();
            TacticalMarkerType type = TacticalMarkerType.fromNetworkId(buf.m_130242_());
            double x = PacketValidation.checkedCoordinate(buf.readDouble(), "marker x");
            double y = PacketValidation.checkedCoordinate(buf.readDouble(), "marker y");
            double z = PacketValidation.checkedCoordinate(buf.readDouble(), "marker z");
            UUID ownerId = buf.m_130259_();
            String owner = buf.m_130136_(64);
            long ageMillis = Math.max(0L, buf.m_130258_());
            int ownerSquadId = buf.m_130242_();
            boolean ownerCommander = buf.readBoolean();
            if (type == null) continue;
            markers.add(new TacticalMarker(id, type, x, y, z, team, ownerId, owner, System.currentTimeMillis() - ageMillis, ownerSquadId, ownerCommander));
        }
        return new SyncTacticalMarkersMessage(markers);
    }

    public static void handle(SyncTacticalMarkersMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                SyncTacticalMarkersMessage.handleClient(message.markers);
            }
        });
        context.setPacketHandled(true);
    }

    private static void handleClient(List<TacticalMarker> markers) {
        ClientTacticalMarkerState.setMarkers(markers);
        try {
            Class<?> hudClass = Class.forName("com.example.espoints.hud.TacticalMapHUD");
            Object hud = hudClass.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            hudClass.getMethod("syncTacticalMarkersFromServer", List.class).invoke(hud, markers);
        }
        catch (ReflectiveOperationException e) {
            ModLogger.syncError("Failed to sync tactical markers to map HUD: " + e.getMessage());
        }
    }
}

