/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.audio.AudioPackId;

public final class AudioCuePacket {
    private final Cue cue;
    private final String audioPack;

    public AudioCuePacket(Cue cue, String audioPack) {
        this.cue = cue == null ? Cue.STOP : cue;
        String normalized = AudioPackId.normalize(audioPack);
        this.audioPack = normalized == null ? "" : normalized;
    }

    public Cue getCue() {
        return this.cue;
    }

    public String getAudioPack() {
        return this.audioPack;
    }

    public static AudioCuePacket read(FriendlyByteBuf buf) {
        return new AudioCuePacket(Cue.fromId(buf.readUnsignedByte()), buf.m_130136_(80));
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeByte(this.cue.id);
        buf.m_130072_(this.audioPack, 80);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleAudioCue", AudioCuePacket.class).invoke(null, this);
            }
            catch (ReflectiveOperationException reflectiveOperationException) {
                // empty catch block
            }
        });
        context.setPacketHandled(true);
    }

    public static enum Cue {
        STOP(0),
        ENTRY_ATTACK(1),
        ENTRY_DEFEND(2),
        CAPTURED(3),
        LOST(4),
        VICTORY(5),
        VICTORY_EASTER_EGG(6),
        DEFEAT(7),
        CAPTURING_POINT(8),
        LOSING_POINT(9);

        private final int id;

        private Cue(int id) {
            this.id = id;
        }

        private static Cue fromId(int id) {
            for (Cue cue : Cue.values()) {
                if (cue.id != id) continue;
                return cue;
            }
            return STOP;
        }
    }
}

