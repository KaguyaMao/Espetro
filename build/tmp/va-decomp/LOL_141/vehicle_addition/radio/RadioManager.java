/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package LOL_141.vehicle_addition.radio;

import LOL_141.vehicle_addition.api.netease.NetEaseSongInfo;
import LOL_141.vehicle_addition.client.audio.RadioSoundInstance;
import LOL_141.vehicle_addition.client.audio.VehicleRadioBroadcaster;
import LOL_141.vehicle_addition.client.event.RadioHudRenderer;
import LOL_141.vehicle_addition.radio.LyricManager;
import LOL_141.vehicle_addition.radio.NetEaseSongManager;
import LOL_141.vehicle_addition.radio.RadioChannel;
import LOL_141.vehicle_addition.radio.RadioStationManager;
import LOL_141.vehicle_addition.util.SuperbWarfareCompat;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RadioManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int OFF_INDEX = 0;
    private static final int NEVER_PLAYED = -1;
    private static int currentIndex = 0;
    private static String currentName = "";
    private static String currentUrl = "";
    private static boolean currentLooping = false;
    private static long currentSongId = -1L;
    private static long currentSkipMs = 0L;
    private static long audioStartMs = 0L;
    private static RadioSoundInstance currentInstance = null;
    private static long broadcastStartTime = 0L;
    private static long instanceStartTime = 0L;
    private static final long MIN_PLAY_MS = 1500L;
    private static int lastVehicleId = Integer.MIN_VALUE;
    private static Entity lastVehicleEntity = null;
    private static boolean dismountHandled = false;
    private static boolean lastPlaying = false;
    private static int lastIndex = -1;
    private static long savedSongId = -1L;
    private static long savedOffsetMs = 0L;
    private static String lastSavedName = "";
    private static boolean vehicleRadioContinues = false;
    private static int hostedVehicleId = Integer.MIN_VALUE;
    private static final List<Long> recentSongIds = new ArrayList<Long>();
    private static final Random RANDOM = new Random();

    private RadioManager() {
    }

    public static List<RadioChannel> entries() {
        ArrayList<RadioChannel> list = new ArrayList<RadioChannel>();
        list.add(new RadioChannel("\u5173\u95ed", ""));
        list.addAll(RadioStationManager.getStations());
        return list;
    }

    private static RadioChannel getEntry(int index) {
        List<RadioChannel> entries = RadioManager.entries();
        if (index < 0 || index >= entries.size()) {
            return null;
        }
        return entries.get(index);
    }

    public static void clientTick() {
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer player = mc.f_91074_;
        if (player == null || mc.f_91073_ == null) {
            return;
        }
        RadioManager.updateAudioStart(mc);
        Entity vehicle = player.m_20202_();
        if (vehicle == null) {
            if (lastVehicleId != Integer.MIN_VALUE && !dismountHandled) {
                dismountHandled = true;
                RadioManager.handleDismount((Player)player);
                if (!vehicleRadioContinues) {
                    lastVehicleId = Integer.MIN_VALUE;
                    lastVehicleEntity = null;
                }
            }
            if (vehicleRadioContinues) {
                RadioManager.hostingTick(mc);
            }
            return;
        }
        if (!SuperbWarfareCompat.isRidingVehicle(vehicle)) {
            return;
        }
        if (!SuperbWarfareCompat.isMainDriver((Player)player)) {
            return;
        }
        dismountHandled = false;
        int vid = vehicle.m_19879_();
        if (lastVehicleId != vid || vehicleRadioContinues) {
            RadioManager.onMount(vid);
            lastVehicleId = vid;
        }
        lastVehicleEntity = vehicle;
        RadioManager.advanceIfSongEnded(mc);
    }

    private static void handleDismount(Player player) {
        boolean hasOtherPlayers = false;
        if (lastVehicleEntity != null) {
            for (Entity p : lastVehicleEntity.m_20197_()) {
                if (!(p instanceof Player) || p == player) continue;
                hasOtherPlayers = true;
                break;
            }
        }
        if (hasOtherPlayers) {
            RadioManager.continueVehicleRadio();
        } else {
            RadioManager.pauseSound();
        }
    }

    private static void continueVehicleRadio() {
        boolean wasPlaying;
        vehicleRadioContinues = wasPlaying = currentIndex > 0 && !currentUrl.isEmpty();
        hostedVehicleId = lastVehicleId;
        lastPlaying = false;
        lastIndex = 0;
        if (wasPlaying) {
            long skip = currentSongId > 0L ? Math.max(0L, RadioManager.playbackPositionMs()) : Math.max(0L, System.currentTimeMillis() - broadcastStartTime);
            RadioManager.play(currentIndex, currentName, currentUrl, true, currentSongId, skip);
        } else {
            RadioManager.stopInstance();
            currentIndex = 0;
        }
    }

    private static void hostingTick(Minecraft mc) {
        if (lastVehicleEntity == null || mc.f_91073_.m_6815_(hostedVehicleId) == null) {
            RadioManager.endHosting();
            return;
        }
        boolean hasPlayers = false;
        for (Entity p : lastVehicleEntity.m_20197_()) {
            if (!(p instanceof Player)) continue;
            hasPlayers = true;
            break;
        }
        if (!hasPlayers) {
            RadioManager.endHosting();
            return;
        }
        if (currentInstance != null) {
            currentInstance.setPosition((float)lastVehicleEntity.m_20185_(), (float)lastVehicleEntity.m_20186_(), (float)lastVehicleEntity.m_20189_());
        }
        RadioManager.advanceIfSongEnded(mc);
    }

    private static void endHosting() {
        if (vehicleRadioContinues) {
            vehicleRadioContinues = false;
            RadioManager.stopInstance();
            currentIndex = 0;
            RadioManager.broadcastVehicleRadio("", 0L);
        }
        hostedVehicleId = Integer.MIN_VALUE;
        lastVehicleId = Integer.MIN_VALUE;
        lastVehicleEntity = null;
    }

    private static void onMount(int vehicleId) {
        if (vehicleRadioContinues) {
            if (vehicleId == hostedVehicleId) {
                RadioManager.resumeHostedRadio();
                return;
            }
            RadioManager.endHosting();
        }
        if (lastPlaying && lastIndex > 0) {
            NetEaseSongInfo song;
            if (savedSongId > 0L && (song = RadioManager.findSongById(savedSongId)) != null) {
                RadioManager.play(lastIndex, lastSavedName, song.url(), true, song.id(), savedOffsetMs);
                return;
            }
            RadioManager.play(lastIndex);
            return;
        }
        if (lastIndex == -1) {
            RadioManager.startRandomEntry();
            return;
        }
        RadioManager.stopRadio();
    }

    private static void resumeHostedRadio() {
        vehicleRadioContinues = false;
        hostedVehicleId = Integer.MIN_VALUE;
        long skip = currentSongId > 0L ? Math.max(0L, RadioManager.playbackPositionMs()) : Math.max(0L, System.currentTimeMillis() - broadcastStartTime);
        RadioManager.play(currentIndex, currentName, currentUrl, true, currentSongId, skip);
    }

    private static void pauseSound() {
        lastPlaying = currentIndex > 0;
        lastIndex = currentIndex;
        lastSavedName = currentName;
        if (currentSongId > 0L && currentInstance != null && !currentInstance.isCancelled()) {
            savedSongId = currentSongId;
            savedOffsetMs = Math.max(0L, RadioManager.playbackPositionMs());
        } else {
            savedSongId = -1L;
            savedOffsetMs = 0L;
        }
        vehicleRadioContinues = false;
        RadioManager.stopInstance();
        currentIndex = 0;
        RadioManager.broadcastVehicleRadio("", 0L);
    }

    public static void play(int index) {
        List<RadioChannel> entries = RadioManager.entries();
        if (index < 0 || index >= entries.size()) {
            return;
        }
        if (index == 0) {
            RadioManager.stopRadio();
            return;
        }
        RadioChannel entry = entries.get(index);
        String url = entry.url();
        boolean loop = false;
        long songId = -1L;
        Object name = entry.name();
        if (url.startsWith("netease_playlist:")) {
            long playlistId = RadioManager.parsePlaylistId(url);
            NetEaseSongInfo song = RadioManager.pickRandomSong(NetEaseSongManager.getSongsForPlaylist(playlistId));
            if (song == null) {
                RadioManager.showMessage("playlist_empty", new Object[0]);
                return;
            }
            url = song.url();
            songId = song.id();
            loop = true;
            name = entry.name() + " - " + song.name();
        } else if (url.startsWith("netease_song:")) {
            long sid = RadioManager.parseSongId(url);
            NetEaseSongInfo song = RadioManager.findSongById(sid);
            if (song == null) {
                RadioManager.showMessage("playlist_empty", new Object[0]);
                return;
            }
            url = song.url();
            songId = song.id();
            loop = true;
        }
        RadioManager.play(index, (String)name, url, loop, songId);
    }

    public static void play(int index, String name, String url, boolean looping, long songId) {
        RadioManager.play(index, name, url, looping, songId, 0L);
    }

    public static void play(int index, String name, String url, boolean looping, long songId, long skipMs) {
        Minecraft mc = Minecraft.m_91087_();
        RadioManager.stopInstance();
        currentIndex = index;
        currentName = name;
        currentUrl = url;
        currentLooping = looping;
        currentSongId = songId;
        currentSkipMs = skipMs;
        audioStartMs = 0L;
        if (url.isEmpty()) {
            RadioManager.stopRadio();
            return;
        }
        LyricManager.onSongStart(songId);
        try {
            RadioSoundInstance instance;
            if (vehicleRadioContinues) {
                Entity veh = lastVehicleEntity;
                float x = veh != null ? (float)veh.m_20185_() : (float)mc.f_91074_.m_20185_();
                float y = veh != null ? (float)veh.m_20186_() : (float)mc.f_91074_.m_20186_();
                float z = veh != null ? (float)veh.m_20189_() : (float)mc.f_91074_.m_20189_();
                instance = RadioSoundInstance.fromUrlPositional(url, x, y, z, true);
            } else {
                instance = new RadioSoundInstance(new URL(url), looping);
            }
            instance.setSongId(songId);
            if (skipMs > 0L) {
                instance.setSkipMs(skipMs);
            }
            currentInstance = instance;
            instanceStartTime = System.currentTimeMillis();
            mc.m_91106_().m_120367_((SoundInstance)instance);
            broadcastStartTime = System.currentTimeMillis();
            RadioManager.broadcastVehicleRadio(url, broadcastStartTime);
            RadioManager.showMessage("now_playing", name);
        }
        catch (MalformedURLException e) {
            LOGGER.error("Invalid radio url: {}", (Object)url, (Object)e);
            RadioManager.showMessage("play_error", new Object[0]);
        }
    }

    public static void stopRadio() {
        lastPlaying = false;
        lastIndex = 0;
        RadioManager.stopInstance();
        currentIndex = 0;
        currentName = "";
        currentUrl = "";
        currentLooping = false;
        currentSongId = -1L;
        RadioManager.broadcastVehicleRadio("", 0L);
        RadioManager.showMessage("radio_off", new Object[0]);
    }

    private static void stopInstance() {
        LyricManager.clear();
        if (currentInstance != null) {
            currentInstance.setCancelled(true);
            Minecraft.m_91087_().m_91106_().m_120399_((SoundInstance)currentInstance);
            currentInstance = null;
        }
    }

    public static long getCurrentSongId() {
        return currentSongId;
    }

    public static long playbackPositionMs() {
        if (currentSongId <= 0L || currentInstance == null || audioStartMs <= 0L) {
            return -1L;
        }
        return Math.max(0L, System.currentTimeMillis() - audioStartMs + currentSkipMs);
    }

    private static void updateAudioStart(Minecraft mc) {
        if (currentInstance != null && currentSongId > 0L && audioStartMs == 0L && mc.m_91106_().m_120403_((SoundInstance)currentInstance)) {
            audioStartMs = System.currentTimeMillis();
        }
    }

    public static void onScroll(double delta) {
        if (!RadioManager.isController()) {
            return;
        }
        List<RadioChannel> entries = RadioManager.entries();
        if (entries.isEmpty()) {
            return;
        }
        int size = entries.size();
        int next = currentIndex;
        if (delta > 0.0) {
            next = (next + 1) % size;
        } else if (delta < 0.0) {
            next = (next - 1 + size) % size;
        } else {
            return;
        }
        if (next == 0) {
            RadioManager.stopRadio();
        } else {
            RadioManager.play(next);
        }
    }

    public static void nextSong() {
        NetEaseSongInfo song;
        if (!RadioManager.isController() || currentIndex <= 0) {
            return;
        }
        RadioChannel entry = RadioManager.getEntry(currentIndex);
        if (entry == null) {
            return;
        }
        if (entry.url().startsWith("netease_playlist:")) {
            long playlistId = RadioManager.parsePlaylistId(entry.url());
            NetEaseSongInfo song2 = RadioManager.pickRandomSong(NetEaseSongManager.getSongsForPlaylist(playlistId));
            if (song2 != null) {
                RadioManager.play(currentIndex, entry.name() + " - " + song2.name(), song2.url(), true, song2.id());
            } else {
                RadioManager.showMessage("playlist_empty", new Object[0]);
            }
            return;
        }
        if (entry.url().startsWith("netease_song:") && (song = RadioManager.pickRandomSong(NetEaseSongManager.getSongs())) != null) {
            RadioManager.play(currentIndex, entry.name(), song.url(), true, song.id());
        }
    }

    private static NetEaseSongInfo pickRandomSong(List<NetEaseSongInfo> pool) {
        if (pool == null || pool.isEmpty()) {
            return null;
        }
        int avoid = Math.min(recentSongIds.size(), Math.max(0, pool.size() - 2));
        List<Long> avoidIds = recentSongIds.subList(0, avoid);
        ArrayList<Object> candidates = new ArrayList();
        for (NetEaseSongInfo song : pool) {
            if (avoidIds.contains(song.id())) continue;
            candidates.add(song);
        }
        if (candidates.isEmpty()) {
            candidates = new ArrayList<NetEaseSongInfo>(pool);
        }
        NetEaseSongInfo chosen = (NetEaseSongInfo)candidates.get(RANDOM.nextInt(candidates.size()));
        recentSongIds.add(chosen.id());
        if (recentSongIds.size() > 8) {
            recentSongIds.remove(0);
        }
        return chosen;
    }

    private static void startRandomEntry() {
        List<RadioChannel> entries = RadioManager.entries();
        if (entries.size() <= 1) {
            RadioManager.stopRadio();
            return;
        }
        int index = RANDOM.nextInt(entries.size() - 1) + 1;
        RadioManager.play(index);
    }

    private static void advanceIfSongEnded(Minecraft mc) {
        if (currentInstance == null || currentSongId <= 0L) {
            return;
        }
        if (currentInstance.isCancelled()) {
            return;
        }
        if (System.currentTimeMillis() - instanceStartTime < 1500L) {
            return;
        }
        if (mc.m_91106_().m_120403_((SoundInstance)currentInstance)) {
            return;
        }
        LOGGER.info("Song {} finished, advancing to next", (Object)currentSongId);
        RadioManager.autoNext();
    }

    private static void autoNext() {
        RadioChannel entry = RadioManager.getEntry(currentIndex);
        if (entry == null) {
            RadioManager.stopRadio();
            return;
        }
        if (entry.url().startsWith("netease_playlist:")) {
            long playlistId = RadioManager.parsePlaylistId(entry.url());
            NetEaseSongInfo song = RadioManager.pickRandomSong(NetEaseSongManager.getSongsForPlaylist(playlistId));
            if (song != null) {
                RadioManager.play(currentIndex, entry.name() + " - " + song.name(), song.url(), true, song.id());
            } else {
                RadioManager.stopRadio();
            }
            return;
        }
        if (entry.url().startsWith("netease_song:")) {
            NetEaseSongInfo song = RadioManager.pickRandomSong(NetEaseSongManager.getSongs());
            if (song != null) {
                RadioManager.play(currentIndex, entry.name(), song.url(), true, song.id());
            } else {
                RadioManager.stopRadio();
            }
            return;
        }
        RadioManager.stopRadio();
    }

    public static boolean isController() {
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer player = mc.f_91074_;
        if (player == null) {
            return false;
        }
        Entity vehicle = player.m_20202_();
        if (vehicle == null) {
            return false;
        }
        return SuperbWarfareCompat.isRidingVehicle(vehicle) && SuperbWarfareCompat.isMainDriver((Player)player);
    }

    private static void broadcastVehicleRadio(String url, long startTime) {
        if (lastVehicleId == Integer.MIN_VALUE) {
            return;
        }
        VehicleRadioBroadcaster.broadcast(lastVehicleId, url, startTime);
    }

    private static long parsePlaylistId(String url) {
        try {
            return Long.parseLong(url.substring("netease_playlist:".length()));
        }
        catch (Exception e) {
            return -1L;
        }
    }

    private static long parseSongId(String url) {
        try {
            return Long.parseLong(url.substring("netease_song:".length()));
        }
        catch (Exception e) {
            return -1L;
        }
    }

    private static NetEaseSongInfo findSongById(long id) {
        for (NetEaseSongInfo song : NetEaseSongManager.getSongs()) {
            if (song.id() != id) continue;
            return song;
        }
        return null;
    }

    private static void showMessage(String key, Object ... args) {
        RadioHudRenderer.show((Component)Component.m_237110_((String)("message.vehicle_addition." + key), (Object[])args));
    }
}

