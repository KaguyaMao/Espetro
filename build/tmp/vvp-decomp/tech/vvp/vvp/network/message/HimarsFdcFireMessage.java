/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package tech.vvp.vvp.network.message;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import tech.vvp.vvp.entity.vehicle.HimarsEntity;

public class HimarsFdcFireMessage {
    public static void encode(HimarsFdcFireMessage message, FriendlyByteBuf buffer) {
    }

    public static HimarsFdcFireMessage decode(FriendlyByteBuf buffer) {
        return new HimarsFdcFireMessage();
    }

    public static void handler(HimarsFdcFireMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            Entity vehicle = player.m_20202_();
            if (!(vehicle instanceof HimarsEntity)) {
                return;
            }
            HimarsEntity himars = (HimarsEntity)vehicle;
            if (himars.getSeatIndex((Entity)player) != 2) {
                return;
            }
            himars.authorizeFdcFire((LivingEntity)player);
        });
        ctx.get().setPacketHandled(true);
    }
}

