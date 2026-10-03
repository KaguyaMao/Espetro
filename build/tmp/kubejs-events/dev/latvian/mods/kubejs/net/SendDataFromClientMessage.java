/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.networking.NetworkManager$PacketContext
 *  dev.architectury.networking.simple.BaseC2SMessage
 *  dev.architectury.networking.simple.MessageType
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.net;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import dev.latvian.mods.kubejs.bindings.event.NetworkEvents;
import dev.latvian.mods.kubejs.net.KubeJSNet;
import dev.latvian.mods.kubejs.net.NetworkEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class SendDataFromClientMessage
extends BaseC2SMessage {
    private final String channel;
    private final CompoundTag data;

    public SendDataFromClientMessage(String c, @Nullable CompoundTag d) {
        this.channel = c;
        this.data = d;
    }

    SendDataFromClientMessage(FriendlyByteBuf buf) {
        this.channel = buf.m_130136_(120);
        this.data = buf.m_130260_();
    }

    public MessageType getType() {
        return KubeJSNet.SEND_DATA_FROM_CLIENT;
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130072_(this.channel, 120);
        buf.m_130079_(this.data);
    }

    public void handle(NetworkManager.PacketContext context) {
        Player player;
        if (!this.channel.isEmpty() && (player = context.getPlayer()) instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            if (NetworkEvents.DATA_RECEIVED.hasListeners(this.channel)) {
                NetworkEvents.DATA_RECEIVED.post((ScriptTypeHolder)ScriptType.SERVER, (Object)this.channel, new NetworkEventJS((Player)serverPlayer, this.channel, this.data));
            }
        }
    }
}

