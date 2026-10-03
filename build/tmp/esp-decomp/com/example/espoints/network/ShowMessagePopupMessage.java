/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.network.NetworkDirection
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package com.example.espoints.network;

import com.example.espoints.hud.MessagePopup;
import com.example.espoints.network.NetworkHandler;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class ShowMessagePopupMessage {
    private final UUID playerUUID;
    private final String message;
    private final long duration;
    private final int width;
    private final int height;
    private final int backgroundColor;
    private final int textColor;
    private final int borderColor;
    private final int borderWidth;

    public ShowMessagePopupMessage(UUID playerUUID, String message, long duration, int width, int height, int backgroundColor, int textColor, int borderColor, int borderWidth) {
        this.playerUUID = playerUUID;
        this.message = message;
        this.duration = duration;
        this.width = width;
        this.height = height;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
    }

    public static void encode(ShowMessagePopupMessage msg, FriendlyByteBuf buf) {
        ShowMessagePopupMessage.validate(msg.message, msg.duration, msg.width, msg.height, msg.borderWidth);
        buf.m_130077_(msg.playerUUID);
        buf.m_130072_(msg.message, 1024);
        buf.writeLong(msg.duration);
        buf.writeInt(msg.width);
        buf.writeInt(msg.height);
        buf.writeInt(msg.backgroundColor);
        buf.writeInt(msg.textColor);
        buf.writeInt(msg.borderColor);
        buf.writeInt(msg.borderWidth);
    }

    public static ShowMessagePopupMessage decode(FriendlyByteBuf buf) {
        ShowMessagePopupMessage message = new ShowMessagePopupMessage(buf.m_130259_(), buf.m_130136_(1024), buf.readLong(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
        ShowMessagePopupMessage.validate(message.message, message.duration, message.width, message.height, message.borderWidth);
        return message;
    }

    private static void validate(String message, long duration, int width, int height, int borderWidth) {
        if (message == null || message.length() > 1024 || duration < 0L || duration > 600000L || width < 0 || width > 4096 || height < 0 || height > 4096 || borderWidth < 0 || borderWidth > 64) {
            throw new IllegalArgumentException("Invalid popup payload");
        }
    }

    public static void handle(ShowMessagePopupMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                MessagePopup.getInstance().showMessage(msg.playerUUID, msg.message, msg.duration, msg.width, msg.height, msg.backgroundColor, msg.textColor, msg.borderColor, msg.borderWidth);
            }
        });
        context.setPacketHandled(true);
    }

    public static void sendToPlayer(ServerPlayer player, UUID playerUUID, String message, long duration, int width, int height, int backgroundColor, int textColor, int borderColor, int borderWidth) {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), (Object)new ShowMessagePopupMessage(playerUUID, message, duration, width, height, backgroundColor, textColor, borderColor, borderWidth));
    }

    public static void broadcastToAll(UUID playerUUID, String message, long duration, int width, int height, int backgroundColor, int textColor, int borderColor, int borderWidth) {
        NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), (Object)new ShowMessagePopupMessage(playerUUID, message, duration, width, height, backgroundColor, textColor, borderColor, borderWidth));
    }
}

