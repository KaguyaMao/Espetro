/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package LOL_141.vehicle_addition.radio;

import LOL_141.vehicle_addition.api.netease.NetEaseSongInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NetEaseSongManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_FILE = Paths.get("config", "vehicle_addition", "net_ease_songs.json");
    private static final List<NetEaseSongInfo> SONGS = new ArrayList<NetEaseSongInfo>();

    private NetEaseSongManager() {
    }

    public static void load() {
        SONGS.clear();
        if (Files.exists(CONFIG_FILE, new LinkOption[0])) {
            try (BufferedReader reader = Files.newBufferedReader(CONFIG_FILE, StandardCharsets.UTF_8);){
                NetEaseSongInfo[] arr = (NetEaseSongInfo[])GSON.fromJson((Reader)reader, NetEaseSongInfo[].class);
                if (arr != null) {
                    for (NetEaseSongInfo info : arr) {
                        if (info == null || info.vip()) continue;
                        SONGS.add(info);
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error("Failed to load netease songs", (Throwable)e);
                SONGS.clear();
            }
        }
        NetEaseSongManager.saveSilently();
        LOGGER.info("NetEase songs ready: {} tracks", (Object)SONGS.size());
    }

    public static List<NetEaseSongInfo> getSongs() {
        return Collections.unmodifiableList(SONGS);
    }

    public static List<NetEaseSongInfo> getSongsForPlaylist(long playlistId) {
        ArrayList<NetEaseSongInfo> result = new ArrayList<NetEaseSongInfo>();
        for (NetEaseSongInfo info : SONGS) {
            if (info.playlistId() != playlistId) continue;
            result.add(info);
        }
        return result;
    }

    public static boolean addSong(NetEaseSongInfo info) {
        if (info == null || info.vip()) {
            return false;
        }
        for (int i = 0; i < SONGS.size(); ++i) {
            NetEaseSongInfo old = SONGS.get(i);
            if (old.id() != info.id()) continue;
            long playlistId = info.playlistId() > 0L ? info.playlistId() : old.playlistId();
            SONGS.set(i, new NetEaseSongInfo(info.id(), info.name(), info.artist(), info.vip(), info.url(), playlistId));
            NetEaseSongManager.saveSilently();
            return true;
        }
        SONGS.add(info);
        NetEaseSongManager.saveSilently();
        return true;
    }

    public static void removeSong(long songId) {
        SONGS.removeIf(info -> info.id() == songId);
        NetEaseSongManager.saveSilently();
    }

    public static void refreshSongUrl(long songId, String newUrl) {
        for (int i = 0; i < SONGS.size(); ++i) {
            NetEaseSongInfo info = SONGS.get(i);
            if (info.id() != songId) continue;
            SONGS.set(i, new NetEaseSongInfo(info.id(), info.name(), info.artist(), info.vip(), newUrl, info.playlistId()));
            break;
        }
        NetEaseSongManager.saveSilently();
    }

    public static void saveSilently() {
        try {
            Files.createDirectories(CONFIG_FILE.getParent(), new FileAttribute[0]);
            try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_FILE, StandardCharsets.UTF_8, new OpenOption[0]);){
                GSON.toJson(SONGS, (Appendable)writer);
            }
        }
        catch (IOException e) {
            LOGGER.error("Failed to save netease songs", (Throwable)e);
        }
    }
}

