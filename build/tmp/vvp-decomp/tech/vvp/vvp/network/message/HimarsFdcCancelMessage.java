/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package tech.vvp.vvp.network.message;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import tech.vvp.vvp.entity.vehicle.HimarsEntity;

public class HimarsFdcCancelMessage {
    public static void encode(HimarsFdcCancelMessage message, FriendlyByteBuf buffer) {
    }

    public static HimarsFdcCancelMessage decode(FriendlyByteBuf buffer) {
        return new HimarsFdcCancelMessage();
    }

    public static void handler(HimarsFdcCancelMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            Entity vehicle = player.m_20202_();
            if (vehicle instanceof HimarsEntity) {
                HimarsEntity himars = (HimarsEntity)vehicle;
                himars.cancelFdcMission();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

