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
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.network.FriendlyByteBuf;

public class DisplayClientErrorsMessage
extends BaseS2CMessage {
    public DisplayClientErrorsMessage() {
    }

    DisplayClientErrorsMessage(FriendlyByteBuf buf) {
    }

    public MessageType getType() {
        return KubeJSNet.DISPLAY_CLIENT_ERRORS;
    }

    public void write(FriendlyByteBuf buf) {
    }

    public void handle(NetworkManager.PacketContext context) {
        KubeJS.PROXY.openErrors(ScriptType.CLIENT);
    }
}

