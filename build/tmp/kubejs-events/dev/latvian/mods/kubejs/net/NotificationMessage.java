/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.networking.NetworkManager$PacketContext
 *  dev.architectury.networking.simple.BaseS2CMessage
 *  dev.architectury.networking.simple.MessageType
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.entity.player.Player
 */
package dev.latvian.mods.kubejs.net;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.net.KubeJSNet;
import dev.latvian.mods.kubejs.util.NotificationBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class NotificationMessage
extends BaseS2CMessage {
    private final NotificationBuilder notification;

    public NotificationMessage(NotificationBuilder notification) {
        this.notification = notification;
    }

    NotificationMessage(FriendlyByteBuf buf) {
        this.notification = new NotificationBuilder(buf);
    }

    public MessageType getType() {
        return KubeJSNet.NOTIFICATION;
    }

    public void write(FriendlyByteBuf buf) {
        this.notification.write(buf);
    }

    public void handle(NetworkManager.PacketContext context) {
        Player p0 = KubeJS.PROXY.getClientPlayer();
        if (p0 == null) {
            return;
        }
        p0.kjs$notify(this.notification);
    }
}

