/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.vehicle.VehicleMountServer;

public final class MountRequestPacket {
    private final Action action;
    private final UUID vehicleId;

    public MountRequestPacket(Action action, UUID vehicleId) {
        this.action = action;
        this.vehicleId = vehicleId;
    }

    public static MountRequestPacket read(FriendlyByteBuf buf) {
        Action action = buf.m_130066_(Action.class);
        UUID id = buf.readBoolean() ? buf.m_130259_() : null;
        return new MountRequestPacket(action, id);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130068_(this.action);
        buf.writeBoolean(this.vehicleId != null);
        if (this.vehicleId != null) {
            buf.m_130077_(this.vehicleId);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer sender = ctx.get().getSender();
        ctx.get().enqueueWork(() -> {
            if (sender == null) {
                return;
            }
            switch (this.action) {
                case BEGIN: {
                    VehicleMountServer.begin(sender, this.vehicleId);
                    break;
                }
                case CANCEL: {
                    VehicleMountServer.cancel(sender);
                    break;
                }
                case COMPLETE: {
                    VehicleMountServer.complete(sender, this.vehicleId);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static enum Action {
        BEGIN,
        CANCEL,
        COMPLETE;

    }
}

