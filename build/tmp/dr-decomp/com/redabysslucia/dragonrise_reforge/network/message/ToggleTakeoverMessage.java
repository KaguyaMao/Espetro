/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.redabysslucia.dragonrise_reforge.network.message;

import com.redabysslucia.dragonrise_reforge.entities.vehicle.IndirectFireVehicleBase;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class ToggleTakeoverMessage {
    private final int entityId;
    private final boolean takeover;

    public ToggleTakeoverMessage(int entityId, boolean takeover) {
        this.entityId = entityId;
        this.takeover = takeover;
    }

    public static void encode(ToggleTakeoverMessage msg, FriendlyByteBuf buf) {
        buf.m_130130_(msg.entityId);
        buf.writeBoolean(msg.takeover);
    }

    public static ToggleTakeoverMessage decode(FriendlyByteBuf buf) {
        int entityId = buf.m_130242_();
        boolean takeover = buf.readBoolean();
        return new ToggleTakeoverMessage(entityId, takeover);
    }

    public static void handle(ToggleTakeoverMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            Entity entity = player.m_9236_().m_6815_(msg.entityId);
            if (!(entity instanceof IndirectFireVehicleBase)) {
                return;
            }
            IndirectFireVehicleBase vehicle = (IndirectFireVehicleBase)entity;
            if (vehicle.getSeatIndex((Entity)player) != vehicle.getTurretControllerIndex()) {
                return;
            }
            vehicle.setFireControlTakeover(msg.takeover, (Entity)player);
        });
        ctx.get().setPacketHandled(true);
    }
}

