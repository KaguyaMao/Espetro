/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.rhythm.dragon_vehicle_deployer.network;

import com.rhythm.dragon_vehicle_deployer.block.entity.VehicleDeployerBlockEntity;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class DeployerSettingsPacket {
    private final BlockPos pos;
    private final int spawnIntervalSeconds;
    private final boolean autoSpawnEnabled;
    private final int idleClearTimeoutSeconds;

    public DeployerSettingsPacket(BlockPos pos, int spawnIntervalSeconds, boolean autoSpawnEnabled, int idleClearTimeoutSeconds) {
        this.pos = pos;
        this.spawnIntervalSeconds = spawnIntervalSeconds;
        this.autoSpawnEnabled = autoSpawnEnabled;
        this.idleClearTimeoutSeconds = idleClearTimeoutSeconds;
    }

    public static void encode(DeployerSettingsPacket msg, FriendlyByteBuf buf) {
        buf.m_130064_(msg.pos);
        buf.writeInt(msg.spawnIntervalSeconds);
        buf.writeBoolean(msg.autoSpawnEnabled);
        buf.writeInt(msg.idleClearTimeoutSeconds);
    }

    public static DeployerSettingsPacket decode(FriendlyByteBuf buf) {
        return new DeployerSettingsPacket(buf.m_130135_(), buf.readInt(), buf.readBoolean(), buf.readInt());
    }

    public static void handle(DeployerSettingsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            if (!player.m_20310_(2)) {
                return;
            }
            if (player.m_20275_((double)msg.pos.m_123341_() + 0.5, (double)msg.pos.m_123342_() + 0.5, (double)msg.pos.m_123343_() + 0.5) > 64.0) {
                return;
            }
            Level level = player.m_9236_();
            BlockEntity patt2084$temp = level.m_7702_(msg.pos);
            if (!(patt2084$temp instanceof VehicleDeployerBlockEntity)) {
                return;
            }
            VehicleDeployerBlockEntity blockEntity = (VehicleDeployerBlockEntity)patt2084$temp;
            blockEntity.spawnIntervalSeconds = Mth.m_14045_((int)msg.spawnIntervalSeconds, (int)5, (int)3600);
            blockEntity.autoSpawnEnabled = msg.autoSpawnEnabled;
            blockEntity.idleClearTimeoutSeconds = Mth.m_14045_((int)msg.idleClearTimeoutSeconds, (int)0, (int)36000);
            blockEntity.m_6596_();
            player.m_5661_((Component)Component.m_237115_((String)"gui.dragonrise_reforge.save_success").m_130940_(ChatFormatting.GREEN), true);
        });
        ctx.get().setPacketHandled(true);
    }
}

