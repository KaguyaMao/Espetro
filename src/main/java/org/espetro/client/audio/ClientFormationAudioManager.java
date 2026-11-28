package org.espetro.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import org.espetro.Espetro;
import org.espetro.audio.AudioPackId;
import org.espetro.client.gui.ClientGameState;
import org.espetro.network.AudioCuePacket;
import org.espetro.network.AudioCuePacket.Cue;
import org.espetro.team.FactionDataProvider;
import org.espetro.team.GamePhase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/** Client lifecycle for formation music and voice channels. */
public final class ClientFormationAudioManager {
    private static final int DELAYED_VOICE_TICKS = 3 * 20;
    private static final long CAPTURE_FALLBACK_DELAY_MS = 450;
    private static final long CAPTURE_DEDUP_MS = 1750;
    private static final EnumMap<Cue, Long> captureFallbacks = new EnumMap<>(Cue.class);
    private static final EnumMap<Cue, Long> serverCaptureCues = new EnumMap<>(Cue.class);
    private static final EnumMap<Cue, Long> playedFallbacks = new EnumMap<>(Cue.class);
    private static final Set<String> WARNED_MISSING = ConcurrentHashMap.newKeySet();

    private static ExternalOggSoundInstance music;
    private static ExternalOggSoundInstance voice;
    private static PendingVoice pendingVoice;
    private static boolean hadClientSession;
    private static boolean roundResult;
    private static String serverPack;
    private static String serverPackFaction;
    private static Properties factionAudioPacks;

    private ClientFormationAudioManager() {
    }

    public static void handle(AudioCuePacket packet) {
        if (packet == null) return;
        if (packet.getCue() == AudioCuePacket.Cue.STOP) {
            stopAll();
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Cue cue = packet.getCue();
        Path packRoot = ClientAudioSetResolver.resolvePackRoot(
            minecraft.gameDirectory.toPath(), packet.getAudioPack());
        if (packRoot == null) {
            stopAll();
            warnOnce(packet.getAudioPack(),
                "找不到客户端音频套装目录 EsAudio/" + packet.getAudioPack());
            return;
        }
        serverPack = packet.getAudioPack();
        serverPackFaction = ClientGameState.getPlayerFactionId();

        if (captureVoiceGroup(cue) != null) {
            long now = net.minecraft.Util.getMillis();
            serverCaptureCues.put(cue, now);
            captureFallbacks.remove(cue);
            if (recent(playedFallbacks, cue, now)) return;
        }

        switch (packet.getCue()) {
            case ENTRY_ATTACK -> playMusicThenVoice(packRoot,
                ClientAudioSetResolver.ENTRY_MUSIC,
                ClientAudioSetResolver.ENTRY_ATTACK_VOICE);
            case ENTRY_DEFEND -> playMusicThenVoice(packRoot,
                ClientAudioSetResolver.ENTRY_MUSIC,
                ClientAudioSetResolver.ENTRY_DEFEND_VOICE);
            case CAPTURED, LOST, CAPTURING_POINT, LOSING_POINT ->
                playImmediateVoice(packRoot, captureVoiceGroup(cue));
            case VICTORY -> playResult(packRoot, false,
                ClientAudioSetResolver.VICTORY_VOICE);
            case VICTORY_EASTER_EGG -> playResult(packRoot, true,
                ClientAudioSetResolver.VICTORY_VOICE);
            case DEFEAT -> playDefeat(packRoot);
            case STOP -> stopAll();
        }
    }

    public static void queueCaptureFallback(String cueName) {
        if (ClientGameState.getCurrentPhase() != GamePhase.BATTLE) return;
        Cue cue = Cue.valueOf(cueName);
        long now = net.minecraft.Util.getMillis();
        if (!recent(serverCaptureCues, cue, now) && !recent(playedFallbacks, cue, now)
                && captureVoiceGroup(cue) != null) {
            captureFallbacks.putIfAbsent(cue, now + CAPTURE_FALLBACK_DELAY_MS);
        }
    }

    private static boolean recent(EnumMap<Cue, Long> history, Cue cue, long now) {
        Long time = history.get(cue);
        return time != null && now - time < CAPTURE_DEDUP_MS;
    }

    private static String captureVoiceGroup(Cue cue) {
        return switch (cue) {
            case CAPTURED -> ClientAudioSetResolver.CAPTURE_VOICE;
            case LOST -> ClientAudioSetResolver.LOST_VOICE;
            case CAPTURING_POINT -> ClientAudioSetResolver.CAPTURING_VOICE;
            case LOSING_POINT -> ClientAudioSetResolver.LOSING_VOICE;
            default -> null;
        };
    }

    private static void tickCaptureFallbacks(Minecraft minecraft) {
        if (ClientGameState.getCurrentPhase() != GamePhase.BATTLE) {
            captureFallbacks.clear();
            return;
        }
        long now = net.minecraft.Util.getMillis();
        var pending = captureFallbacks.entrySet().iterator();
        while (pending.hasNext()) {
            var event = pending.next();
            if (now < event.getValue()) continue;
            Cue cue = event.getKey();
            pending.remove();
            if (recent(serverCaptureCues, cue, now)) continue;
            Path root = ClientAudioSetResolver.resolvePackRoot(minecraft.gameDirectory.toPath(), capturePack(minecraft));
            if (root != null) {
                playedFallbacks.put(cue, now);
                playImmediateVoice(root, captureVoiceGroup(cue));
                break;
            }
        }
    }

    private static String capturePack(Minecraft minecraft) {
        String factionId = ClientGameState.getPlayerFactionId();
        if (serverPack != null && Objects.equals(factionId, serverPackFaction)) return serverPack;
        if (factionId == null) return null;
        var faction = FactionDataProvider.getOrCreateLoader().getFaction(factionId);
        if (faction != null && faction.audioPack != null) return faction.audioPack;
        if (factionAudioPacks == null) {
            factionAudioPacks = new Properties();
            Path file = minecraft.gameDirectory.toPath().resolve("EsAudio").resolve("faction-packs.properties");
            if (Files.isRegularFile(file)) {
                try (var reader = Files.newBufferedReader(file)) {
                    factionAudioPacks.load(reader);
                } catch (IOException exception) {
                    Espetro.LOGGER.warn("Unable to read faction voice pack mapping", exception);
                }
            }
        }
        String pack = AudioPackId.normalize(factionAudioPacks.getProperty(factionId));
        if (pack == null) warnOnce("capture:" + factionId, "找不到编制 " + factionId + " 的占点语音包配置");
        return pack;
    }

    public static void tick(Minecraft minecraft) {
        boolean inSession = minecraft != null && minecraft.player != null && minecraft.level != null;
        if (!inSession) {
            // A dimension change can briefly replace the local player/world on the same connection.
            if (minecraft != null && minecraft.getConnection() != null) return;
            if (hadClientSession) stopAll();
            hadClientSession = false;
            return;
        }
        hadClientSession = true;
        tickCaptureFallbacks(minecraft);

        // Formation music uses the RECORDS channel so it can be adjusted independently
        // from Minecraft's ambient background music.
        if (music != null) {
            minecraft.getMusicManager().stopPlaying();
            if (!minecraft.getSoundManager().isActive(music)) {
                music = null;
                roundResult = false;
            }
        }

        if (pendingVoice != null && --pendingVoice.ticksRemaining <= 0) {
            PendingVoice ready = pendingVoice;
            pendingVoice = null;
            playRandomVoice(ready.packRoot, ready.voiceGroup);
        }
    }

    /** Leaving the round clears voices and unrelated music; let an active result track finish. */
    public static void onRoundCleanup() {
        clearAudioState(roundResult);
    }

    public static void stopAll() {
        clearAudioState(false);
    }

    private static void clearAudioState(boolean preserveResultMusic) {
        if (!preserveResultMusic) roundResult=false;
        pendingVoice = null;
        captureFallbacks.clear();
        serverCaptureCues.clear();
        playedFallbacks.clear();
        serverPack = null;
        serverPackFaction = null;
        Minecraft minecraft = Minecraft.getInstance();
        if (!preserveResultMusic && music != null) {
            minecraft.getSoundManager().stop(music);
            music = null;
        }
        if (voice != null) {
            minecraft.getSoundManager().stop(voice);
            voice = null;
        }
    }

    private static void playMusicThenVoice(Path packRoot, String musicFile, String voiceGroup) {
        roundResult=false;
        pendingVoice = null;
        stopVoice();
        playMusic(ClientAudioSetResolver.resolveMusic(packRoot, musicFile),
            packRoot, "music/" + musicFile);
        pendingVoice = new PendingVoice(DELAYED_VOICE_TICKS, packRoot, voiceGroup);
    }

    private static void playResult(Path packRoot, boolean easterEgg, String voiceGroup) {
        roundResult=true;
        pendingVoice = null;
        stopVoice();
        Path selected = ClientAudioSetResolver.resolveVictoryMusic(packRoot, easterEgg);
        playMusic(selected, packRoot, easterEgg
            ? "music/victory_easter_egg.ogg（缺失时回退 victory.ogg）"
            : "music/victory.ogg");
        pendingVoice = new PendingVoice(DELAYED_VOICE_TICKS, packRoot, voiceGroup);
    }

    private static void playDefeat(Path packRoot) {
        roundResult=true;
        pendingVoice = null;
        stopVoice();
        playMusic(ClientAudioSetResolver.resolveMusic(
            packRoot, ClientAudioSetResolver.DEFEAT_MUSIC),
            packRoot, "music/" + ClientAudioSetResolver.DEFEAT_MUSIC);
        pendingVoice = new PendingVoice(DELAYED_VOICE_TICKS,
            packRoot, ClientAudioSetResolver.DEFEAT_VOICE);
    }

    private static void playImmediateVoice(Path packRoot, String voiceGroup) {
        pendingVoice = null;
        playRandomVoice(packRoot, voiceGroup);
    }

    private static void playMusic(Path file, Path packRoot, String expectedPath) {
        Minecraft minecraft = Minecraft.getInstance();
        if (music != null) minecraft.getSoundManager().stop(music);
        music = null;
        if (file == null) {
            warnOnce(packRoot + "/" + expectedPath,
                "音频套装缺少 " + expectedPath + ": " + packRoot);
            return;
        }
        minecraft.getMusicManager().stopPlaying();
        music = new ExternalOggSoundInstance(file, SoundSource.RECORDS, "music");
        minecraft.getSoundManager().play(music);
    }

    private static void playRandomVoice(Path packRoot, String voiceGroup) {
        stopVoice();
        List<Path> files = ClientAudioSetResolver.listVoiceFiles(packRoot, voiceGroup);
        if (files.isEmpty()) {
            warnOnce(packRoot + "/voice/" + voiceGroup,
                "语音目录为空或不存在: " + packRoot.resolve("voice").resolve(voiceGroup));
            return;
        }
        Path selected = files.get(ThreadLocalRandom.current().nextInt(files.size()));
        voice = new ExternalOggSoundInstance(selected, SoundSource.VOICE, "voice");
        Minecraft.getInstance().getSoundManager().play(voice);
    }

    private static void stopVoice() {
        if (voice != null) {
            Minecraft.getInstance().getSoundManager().stop(voice);
            voice = null;
        }
    }

    private static void warnOnce(String key, String message) {
        if (WARNED_MISSING.add(key)) {
            Espetro.LOGGER.warn("[客户端编制音频] {}", message);
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
