/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package tech.vvp.vvp.network.message;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class SeatSwapMessage {
    private final int targetSeatIndex;

    public SeatSwapMessage(int targetSeatIndex) {
        this.targetSeatIndex = targetSeatIndex;
    }

    public static void encode(SeatSwapMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.targetSeatIndex);
    }

    public static SeatSwapMessage decode(FriendlyByteBuf buffer) {
        return new SeatSwapMessage(buffer.readInt());
    }

    public static void handler(SeatSwapMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity occupant;
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            Entity vehicle = player.m_20202_();
            if (!(vehicle instanceof VehicleEntity)) {
                return;
            }
            VehicleEntity vehicleEntity = (VehicleEntity)vehicle;
            int targetSeat = message.targetSeatIndex;
            if (targetSeat < 0 || targetSeat >= vehicleEntity.getMaxPassengers()) {
                return;
            }
            int currentSeat = vehicleEntity.getSeatIndex((Entity)player);
            if (currentSeat == targetSeat) {
                return;
            }
            List passengers = vehicleEntity.getOrderedPassengers();
            Entity entity = occupant = targetSeat < passengers.size() ? (Entity)passengers.get(targetSeat) : null;
            if (occupant != null && occupant != player) {
                return;
            }
            vehicleEntity.changeSeat((Entity)player, targetSeat);
        });
        ctx.get().setPacketHandled(true);
    }
}

