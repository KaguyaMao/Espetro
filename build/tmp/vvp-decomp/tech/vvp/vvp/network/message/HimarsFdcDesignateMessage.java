/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package tech.vvp.vvp.network.message;

import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import tech.vvp.vvp.entity.vehicle.HimarsEntity;

public class HimarsFdcDesignateMessage {
    private final int targetX;
    private final int targetY;
    private final int targetZ;

    public HimarsFdcDesignateMessage(int targetX, int targetY, int targetZ) {
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }

    public static void encode(HimarsFdcDesignateMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.targetX);
        buffer.writeInt(message.targetY);
        buffer.writeInt(message.targetZ);
    }

    public static HimarsFdcDesignateMessage decode(FriendlyByteBuf buffer) {
        return new HimarsFdcDesignateMessage(buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public static void handler(HimarsFdcDesignateMessage message, Supplier<NetworkEvent.Context> ctx) {
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
            if (!himars.designateFdcTarget((LivingEntity)player, message.targetX, message.targetY, message.targetZ)) {
                player.m_5661_((Component)Component.m_237115_((String)"message.vvp.fdc.engage_failed").m_130940_(ChatFormatting.RED), true);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

