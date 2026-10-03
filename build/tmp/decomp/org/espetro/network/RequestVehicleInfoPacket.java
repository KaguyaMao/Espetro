/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.network.NetworkManager;
import org.espetro.team.ClassCountManager;

public final class RequestVehicleInfoPacket {
    public static RequestVehicleInfoPacket read(FriendlyByteBuf buf) {
        return new RequestVehicleInfoPacket();
    }

    public void write(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            String factionId = ClassCountManager.getInstance().getPlayerFaction(player.m_20148_());
            if (factionId == null) {
                player.m_213846_(Component.m_237113_("\u00a7c\u4f60\u8fd8\u6ca1\u6709\u9009\u62e9\u7f16\u5236\uff01"));
                return;
            }
            NetworkManager.sendVehicleDeployScreen(player, factionId);
        });
        context.setPacketHandled(true);
    }
}

