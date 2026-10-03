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
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class RemoveStageMessage
extends BaseS2CMessage {
    private final UUID player;
    private final String stage;

    public RemoveStageMessage(UUID p, String s) {
        this.player = p;
        this.stage = s;
    }

    RemoveStageMessage(FriendlyByteBuf buf) {
        this.player = buf.m_130259_();
        this.stage = buf.m_130277_();
    }

    public MessageType getType() {
        return KubeJSNet.REMOVE_STAGE;
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130077_(this.player);
        buf.m_130070_(this.stage);
    }

    public void handle(NetworkManager.PacketContext context) {
        Player p;
        Player p0 = KubeJS.PROXY.getClientPlayer();
        if (p0 == null) {
            return;
        }
        Player player = p = this.player.equals(p0.m_20148_()) ? p0 : p0.m_9236_().m_46003_(this.player);
        if (p != null) {
            p.kjs$getStages().remove(this.stage);
        }
    }
}

