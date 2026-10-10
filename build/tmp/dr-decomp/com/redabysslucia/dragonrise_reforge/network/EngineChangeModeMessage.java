/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.redabysslucia.dragonrise_reforge.network;

import com.redabysslucia.dragonrise_reforge.entities.utils.VariableEngineVehicle;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class EngineChangeModeMessage {
    public static void encode(EngineChangeModeMessage message, FriendlyByteBuf buffer) {
    }

    public static EngineChangeModeMessage decode(FriendlyByteBuf buffer) {
        return new EngineChangeModeMessage();
    }

    public static void handler(EngineChangeModeMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            Entity vehicle = player.m_20202_();
            if (vehicle instanceof VariableEngineVehicle) {
                VariableEngineVehicle vtol = (VariableEngineVehicle)vehicle;
                vtol.toggleChangeMode();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

