/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.redabysslucia.dragonrise_reforge.network.message;

import com.redabysslucia.dragonrise_reforge.entities.vehicle.IndirectFireVehicleBase;
import com.redabysslucia.dragonrise_reforge.firecontrol.TrajectoryMode;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class SetFireControlMessage {
    private final int entityId;
    private final boolean clear;
    private final BlockPos target;
    private final int radius;
    private final TrajectoryMode trajectoryMode;
    private final boolean takeover;

    public SetFireControlMessage(int entityId, boolean clear, BlockPos target, int radius, TrajectoryMode trajectoryMode, boolean takeover) {
        this.entityId = entityId;
        this.clear = clear;
        this.target = target;
        this.radius = radius;
        this.trajectoryMode = trajectoryMode;
        this.takeover = takeover;
    }

    public static SetFireControlMessage apply(int entityId, BlockPos target, int radius, TrajectoryMode trajectoryMode, boolean takeover) {
        return new SetFireControlMessage(entityId, false, target, radius, trajectoryMode, takeover);
    }

    public static SetFireControlMessage clear(int entityId) {
        return new SetFireControlMessage(entityId, true, BlockPos.f_121853_, 0, TrajectoryMode.LOW, false);
    }

    public static void encode(SetFireControlMessage msg, FriendlyByteBuf buf) {
        buf.m_130130_(msg.entityId);
        buf.writeBoolean(msg.clear);
        if (!msg.clear) {
            buf.m_130064_(msg.target);
            buf.m_130130_(msg.radius);
            buf.m_130068_((Enum)msg.trajectoryMode);
            buf.writeBoolean(msg.takeover);
        }
    }

    public static SetFireControlMessage decode(FriendlyByteBuf buf) {
        int entityId = buf.m_130242_();
        boolean clear = buf.readBoolean();
        if (clear) {
            return SetFireControlMessage.clear(entityId);
        }
        BlockPos target = buf.m_130135_();
        int radius = buf.m_130242_();
        TrajectoryMode trajectoryMode = (TrajectoryMode)buf.m_130066_(TrajectoryMode.class);
        boolean takeover = buf.readBoolean();
        return new SetFireControlMessage(entityId, false, target, radius, trajectoryMode, takeover);
    }

    public static void handle(SetFireControlMessage msg, Supplier<NetworkEvent.Context> ctx) {
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
            if (msg.clear) {
                vehicle.clearFireControl((Entity)player);
            } else {
                vehicle.applyFireControl(msg.target, msg.radius, msg.trajectoryMode, (Entity)player);
                if (msg.takeover) {
                    vehicle.setFireControlTakeover(true, (Entity)player);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

