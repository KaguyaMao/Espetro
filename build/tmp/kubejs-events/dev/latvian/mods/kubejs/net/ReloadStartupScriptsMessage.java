/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.networking.NetworkManager$PacketContext
 *  dev.architectury.networking.simple.BaseS2CMessage
 *  dev.architectury.networking.simple.MessageType
 *  net.minecraft.network.FriendlyByteBuf
 */
package dev.latvian.mods.kubejs.net;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.net.KubeJSNet;
import net.minecraft.network.FriendlyByteBuf;

public class ReloadStartupScriptsMessage
extends BaseS2CMessage {
    public final boolean dedicated;

    public ReloadStartupScriptsMessage(boolean dedicated) {
        this.dedicated = dedicated;
    }

    ReloadStartupScriptsMessage(FriendlyByteBuf buf) {
        this.dedicated = buf.readBoolean();
    }

    public MessageType getType() {
        return KubeJSNet.RELOAD_STARTUP_SCRIPTS;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.dedicated);
    }

    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> KubeJS.PROXY.reloadStartupScripts(this.dedicated));
    }
}

