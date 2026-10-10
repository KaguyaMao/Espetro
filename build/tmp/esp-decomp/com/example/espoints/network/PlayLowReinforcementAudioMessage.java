/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package com.example.espoints.network;

import com.example.espoints.client.AudioManager;
import com.example.espoints.network.NetworkHandler;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class PlayLowReinforcementAudioMessage {
    private final boolean playAudio;

    public PlayLowReinforcementAudioMessage(boolean playAudio) {
        this.playAudio = playAudio;
    }

    public PlayLowReinforcementAudioMessage(FriendlyByteBuf buf) {
        this.playAudio = buf.readBoolean();
    }

    public static void encode(PlayLowReinforcementAudioMessage msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.playAudio);
    }

    public static PlayLowReinforcementAudioMessage decode(FriendlyByteBuf buf) {
        return new PlayLowReinforcementAudioMessage(buf);
    }

    public static void handle(PlayLowReinforcementAudioMessage msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                AudioManager.getInstance().handleLowReinforcementAudio(msg.playAudio);
            }
        });
        context.setPacketHandled(true);
    }

    public static void broadcastToAll(boolean playAudio) {
        NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), (Object)new PlayLowReinforcementAudioMessage(playAudio));
    }
}

