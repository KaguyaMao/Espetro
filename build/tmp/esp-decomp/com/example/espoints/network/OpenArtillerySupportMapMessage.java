/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package com.example.espoints.network;

import com.example.espoints.network.NetworkHandler;
import com.example.espoints.util.ModLogger;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class OpenArtillerySupportMapMessage {
    private static final String SCREEN_CLASS = "com.example.espoints.client.gui.ArtillerySupportMapScreen";

    public static void encode(OpenArtillerySupportMapMessage message, FriendlyByteBuf buf) {
    }

    public static OpenArtillerySupportMapMessage decode(FriendlyByteBuf buf) {
        return new OpenArtillerySupportMapMessage();
    }

    public static void handle(OpenArtillerySupportMapMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                OpenArtillerySupportMapMessage.openClientScreen();
            }
        });
        context.setPacketHandled(true);
    }

    private static void openClientScreen() {
        try {
            Class<?> screenClass = Class.forName(SCREEN_CLASS);
            screenClass.getMethod("open", new Class[0]).invoke(null, new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            ModLogger.syncError("Failed to open artillery support tactical map: " + e.getMessage());
        }
    }

    public static void sendTo(ServerPlayer player) {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), (Object)new OpenArtillerySupportMapMessage());
    }
}

