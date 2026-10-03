/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.example.espoints.network;

import com.example.espoints.network.PacketValidation;
import com.example.espoints.tactical.TacticalMarkerManager;
import com.example.espoints.tactical.TacticalMarkerType;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class PlaceTacticalMarkerMessage {
    private final int typeId;
    private final double x;
    private final double y;
    private final double z;

    public PlaceTacticalMarkerMessage(TacticalMarkerType type, double x, double z) {
        this(type.ordinal(), x, Double.NaN, z);
    }

    public PlaceTacticalMarkerMessage(TacticalMarkerType type, double x, double y, double z) {
        this(type.ordinal(), x, y, z);
    }

    private PlaceTacticalMarkerMessage(int typeId, double x, double y, double z) {
        this.typeId = typeId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void encode(PlaceTacticalMarkerMessage message, FriendlyByteBuf buf) {
        buf.m_130130_(message.typeId);
        buf.writeDouble(message.x);
        buf.writeDouble(message.y);
        buf.writeDouble(message.z);
    }

    public static PlaceTacticalMarkerMessage decode(FriendlyByteBuf buf) {
        int typeId = buf.m_130242_();
        double x = PacketValidation.checkedCoordinate(buf.readDouble(), "x");
        double y = PacketValidation.checkedOptionalY(buf.readDouble());
        double z = PacketValidation.checkedCoordinate(buf.readDouble(), "z");
        if (TacticalMarkerType.fromNetworkId(typeId) == null) {
            throw new IllegalArgumentException("Invalid tactical marker type");
        }
        return new PlaceTacticalMarkerMessage(typeId, x, y, z);
    }

    public static void handle(PlaceTacticalMarkerMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer sender = context.getSender();
        context.enqueueWork(() -> {
            if (sender == null) {
                return;
            }
            TacticalMarkerType type = TacticalMarkerType.fromNetworkId(message.typeId);
            if (Double.isNaN(message.y)) {
                TacticalMarkerManager.place(sender, type, message.x, message.z);
            } else {
                TacticalMarkerManager.placeFromView(sender, type, message.x, message.y, message.z);
            }
        });
        context.setPacketHandled(true);
    }
}

