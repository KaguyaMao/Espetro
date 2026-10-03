/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.audio;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import org.espetro.Espetro;
import org.espetro.client.audio.ClientAudioSetResolver;
import org.espetro.client.audio.ExternalOggSoundInstance;
import org.espetro.network.AudioCuePacket;

public final class ClientFormationAudioManager {
    private static final int DELAYED_VOICE_TICKS = 60;
    private static final Set<String> WARNED_MISSING = ConcurrentHashMap.newKeySet();
    private static ExternalOggSoundInstance music;
    private static ExternalOggSoundInstance voice;
    private static PendingVoice pendingVoice;
    private static boolean hadClientSession;

    private ClientFormationAudioManager() {
    }

    public static void handle(AudioCuePacket packet) {
        if (packet == null) {
            return;
        }
        if (packet.getCue() == AudioCuePacket.Cue.STOP) {
            ClientFormationAudioManager.stopAll();
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        Path packRoot = ClientAudioSetResolver.resolvePackRoot(minecraft.f_91069_.toPath(), packet.getAudioPack());
        if (packRoot == null) {
            ClientFormationAudioManager.stopAll();
            ClientFormationAudioManager.warnOnce(packet.getAudioPack(), "\u627e\u4e0d\u5230\u5ba2\u6237\u7aef\u97f3\u9891\u5957\u88c5\u76ee\u5f55 EsAudio/" + packet.getAudioPack());
            return;
        }
        switch (packet.getCue()) {
            case ENTRY_ATTACK: {
                ClientFormationAudioManager.playMusicThenVoice(packRoot, "entry.ogg", "entry_attack");
                break;
            }
            case ENTRY_DEFEND: {
                ClientFormationAudioManager.playMusicThenVoice(packRoot, "entry.ogg", "entry_defend");
                break;
            }
            case CAPTURED: {
                ClientFormationAudioManager.playImmediateVoice(packRoot, "capture");
                break;
            }
            case LOST: {
                ClientFormationAudioManager.playImmediateVoice(packRoot, "lost");
                break;
            }
            case CAPTURING_POINT: {
                ClientFormationAudioManager.playImmediateVoice(packRoot, "capturing");
                break;
            }
            case LOSING_POINT: {
                ClientFormationAudioManager.playImmediateVoice(packRoot, "losing");
                break;
            }
            case VICTORY: {
                ClientFormationAudioManager.playResult(packRoot, false, "victory");
                break;
            }
            case VICTORY_EASTER_EGG: {
                ClientFormationAudioManager.playResult(packRoot, true, "victory");
                break;
            }
            case DEFEAT: {
                ClientFormationAudioManager.playDefeat(packRoot);
                break;
            }
            case STOP: {
                ClientFormationAudioManager.stopAll();
            }
        }
    }

    public static void tick(Minecraft minecraft) {
        boolean inSession;
        boolean bl = inSession = minecraft != null && minecraft.f_91074_ != null && minecraft.f_91073_ != null;
        if (!inSession) {
            if (hadClientSession) {
                ClientFormationAudioManager.stopAll();
            }
            hadClientSession = false;
            return;
        }
        hadClientSession = true;
        if (music != null) {
            minecraft.m_91397_().m_120186_();
            if (!minecraft.m_91106_().m_120403_(music)) {
                music = null;
            }
        }
        if (pendingVoice != null && --ClientFormationAudioManager.pendingVoice.ticksRemaining <= 0) {
            PendingVoice ready = pendingVoice;
            pendingVoice = null;
            ClientFormationAudioManager.playRandomVoice(ready.packRoot, ready.voiceGroup);
        }
    }

    public static void stopAll() {
        pendingVoice = null;
        Minecraft minecraft = Minecraft.m_91087_();
        if (music != null) {
            minecraft.m_91106_().m_120399_(music);
            music = null;
        }
        if (voice != null) {
            minecraft.m_91106_().m_120399_(voice);
            voice = null;
        }
    }

    private static void playMusicThenVoice(Path packRoot, String musicFile, String voiceGroup) {
        pendingVoice = null;
        ClientFormationAudioManager.stopVoice();
        ClientFormationAudioManager.playMusic(ClientAudioSetResolver.resolveMusic(packRoot, musicFile), packRoot, "music/" + musicFile);
        pendingVoice = new PendingVoice(60, packRoot, voiceGroup);
    }

    private static void playResult(Path packRoot, boolean easterEgg, String voiceGroup) {
        pendingVoice = null;
        ClientFormationAudioManager.stopVoice();
        Path selected = ClientAudioSetResolver.resolveVictoryMusic(packRoot, easterEgg);
        ClientFormationAudioManager.playMusic(selected, packRoot, easterEgg ? "music/victory_easter_egg.ogg\uff08\u7f3a\u5931\u65f6\u56de\u9000 victory.ogg\uff09" : "music/victory.ogg");
        pendingVoice = new PendingVoice(60, packRoot, voiceGroup);
    }

    private static void playDefeat(Path packRoot) {
        pendingVoice = null;
        ClientFormationAudioManager.stopVoice();
        ClientFormationAudioManager.playMusic(ClientAudioSetResolver.resolveMusic(packRoot, "defeat.ogg"), packRoot, "music/defeat.ogg");
        pendingVoice = new PendingVoice(60, packRoot, "defeat");
    }

    private static void playImmediateVoice(Path packRoot, String voiceGroup) {
        pendingVoice = null;
        ClientFormationAudioManager.playRandomVoice(packRoot, voiceGroup);
    }

    private static void playMusic(Path file, Path packRoot, String expectedPath) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (music != null) {
            minecraft.m_91106_().m_120399_(music);
        }
        music = null;
        if (file == null) {
            ClientFormationAudioManager.warnOnce(packRoot + "/" + expectedPath, "\u97f3\u9891\u5957\u88c5\u7f3a\u5c11 " + expectedPath + ": " + packRoot);
            return;
        }
        minecraft.m_91397_().m_120186_();
        music = new ExternalOggSoundInstance(file, SoundSource.RECORDS, "music");
        minecraft.m_91106_().m_120367_(music);
    }

    private static void playRandomVoice(Path packRoot, String voiceGroup) {
        ClientFormationAudioManager.stopVoice();
        List<Path> files = ClientAudioSetResolver.listVoiceFiles(packRoot, voiceGroup);
        if (files.isEmpty()) {
            ClientFormationAudioManager.warnOnce(packRoot + "/voice/" + voiceGroup, "\u8bed\u97f3\u76ee\u5f55\u4e3a\u7a7a\u6216\u4e0d\u5b58\u5728: " + packRoot.resolve("voice").resolve(voiceGroup));
            return;
        }
        Path selected = files.get(ThreadLocalRandom.current().nextInt(files.size()));
        voice = new ExternalOggSoundInstance(selected, SoundSource.VOICE, "voice");
        Minecraft.m_91087_().m_91106_().m_120367_(voice);
    }

    private static void stopVoice() {
        if (voice != null) {
            Minecraft.m_91087_().m_91106_().m_120399_(voice);
            voice = null;
        }
    }

    private static void warnOnce(String key, String message) {
        if (WARNED_MISSING.add(key)) {
            Espetro.LOGGER.warn("[\u5ba2\u6237\u7aef\u7f16\u5236\u97f3\u9891] {}", (Object)message);
        }
    }

    private static final class PendingVoice {
        private int ticksRemaining;
        private final Path packRoot;
        private final String voiceGroup;

        private PendingVoice(int ticksRemaining, Path packRoot, String voiceGroup) {
            this.ticksRemaining = ticksRemaining;
            this.packRoot = packRoot;
            this.voiceGroup = voiceGroup;
        }
    }
}

