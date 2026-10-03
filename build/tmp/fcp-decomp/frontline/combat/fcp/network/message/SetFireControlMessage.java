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
package frontline.combat.fcp.network.message;

import frontline.combat.fcp.entity.vehicle.IndirectFireVehicleBase;
import frontline.combat.fcp.firecontrol.TrajectoryMode;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public record SetFireControlMessage(int entityId, boolean clear, BlockPos target, int radius, TrajectoryMode trajectoryMode) {
    public static SetFireControlMessage apply(int entityId, BlockPos target, int radius, TrajectoryMode trajectoryMode) {
        return new SetFireControlMessage(entityId, false, target, radius, trajectoryMode);
    }

    public static SetFireControlMessage clear(int entityId) {
        return new SetFireControlMessage(entityId, true, BlockPos.f_121853_, 0, TrajectoryMode.LOW);
    }

    public static void encode(SetFireControlMessage message, FriendlyByteBuf buffer) {
        buffer.m_130130_(message.entityId);
        buffer.writeBoolean(message.clear);
        buffer.m_130064_(message.target);
        buffer.m_130130_(message.radius);
        buffer.writeByte(message.trajectoryMode.ordinal());
    }

    public static SetFireControlMessage decode(FriendlyByteBuf buffer) {
        return new SetFireControlMessage(buffer.m_130242_(), buffer.readBoolean(), buffer.m_130135_(), buffer.m_130242_(), TrajectoryMode.fromId(buffer.readByte()));
    }

    public static void handle(SetFireControlMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            IndirectFireVehicleBase vehicle;
            ServerPlayer player;
            block7: {
                block6: {
                    player = context.getSender();
                    if (player == null) {
                        return;
                    }
                    Entity entity = player.m_9236_().m_6815_(message.entityId);
                    if (!(entity instanceof IndirectFireVehicleBase)) break block6;
                    vehicle = (IndirectFireVehicleBase)entity;
                    if (player.m_20202_() == vehicle && vehicle.getSeatIndex((Entity)player) == vehicle.getTurretControllerIndex()) break block7;
                }
                return;
            }
            if (message.clear) {
                vehicle.clearFireControl((Entity)player);
            } else {
                vehicle.applyFireControl(message.target, message.radius, message.trajectoryMode, (Entity)player);
            }
        });
        context.setPacketHandled(true);
    }
}

