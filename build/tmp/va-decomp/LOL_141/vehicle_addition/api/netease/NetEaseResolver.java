/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package LOL_141.vehicle_addition.api.netease;

import LOL_141.vehicle_addition.api.netease.NetEaseMusic;
import LOL_141.vehicle_addition.api.netease.NetEaseSongInfo;
import LOL_141.vehicle_addition.api.netease.WebApi;
import LOL_141.vehicle_addition.radio.RadioConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NetEaseResolver {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern SONG_ID = Pattern.compile("song(?:/media/outer/url)?(?:[?#/].*?)?[?&]id=(\\d+)");
    private static final Pattern SONG_ID_HASH = Pattern.compile("#/song\\?id=(\\d+)");
    private static final Pattern LIST_ID = Pattern.compile("playlist(?:/[^?]*)?[?#].*?id=(\\d+)");
    private static final Pattern LIST_ID_HASH = Pattern.compile("#/playlist\\?id=(\\d+)");
    private static final Pattern ANY_ID = Pattern.compile("[?&]id=(\\d+)");
    private static final WebApi API = new WebApi(new NetEaseMusic().getRequestPropertyData());

    private NetEaseResolver() {
    }

    public static long parseId(String url) {
        if (url == null || !url.contains("music.163.com")) {
            return -1L;
        }
        Matcher lm = LIST_ID.matcher(url);
        if (!lm.find()) {
            lm = LIST_ID_HASH.matcher(url);
        }
        if (lm.find()) {
            return NetEaseResolver.parsePositive(lm.group(1));
        }
        Matcher sm = SONG_ID.matcher(url);
        if (!sm.find()) {
            sm = SONG_ID_HASH.matcher(url);
        }
        if (sm.find()) {
            return NetEaseResolver.parsePositive(sm.group(1));
        }
        Matcher any = ANY_ID.matcher(url);
        if (any.find()) {
            return NetEaseResolver.parsePositive(any.group(1));
        }
        return -1L;
    }

    private static long parsePositive(String s) {
        try {
            return Long.parseLong(s);
        }
        catch (NumberFormatException e) {
            return -1L;
        }
    }

    public static boolean isPlaylistUrl(String url) {
        return url != null && url.contains("playlist");
    }

    public static NetEaseSongInfo resolveSong(long id) {
        Object name = "Song " + id;
        String artist = "";
        boolean vip = false;
        try {
            String detail = API.song(id);
            JsonObject root = JsonParser.parseString((String)detail).getAsJsonObject();
            JsonArray songs = root.getAsJsonArray("songs");
            if (songs != null && songs.size() > 0) {
                JsonArray artists;
                JsonObject song = songs.get(0).getAsJsonObject();
                if (song.has("name")) {
                    name = song.get("name").getAsString();
                }
                JsonArray jsonArray = artists = song.has("ar") ? song.getAsJsonArray("ar") : song.getAsJsonArray("artists");
                if (artists != null && artists.size() > 0) {
                    artist = artists.get(0).getAsJsonObject().get("name").getAsString();
                }
                if (song.has("fee") && song.get("fee").getAsInt() == 1) {
                    vip = true;
                }
            }
        }
        catch (Exception e) {
            LOGGER.warn("Failed to get song detail for {}: {}", (Object)id, (Object)e.getMessage());
        }
        String url = NetEaseResolver.resolvePlayUrl(id);
        return new NetEaseSongInfo(id, (String)name, artist, vip, url);
    }

    public static String resolvePlayUrl(long id) {
        try {
            String u;
            JsonObject d0;
            JsonArray data;
            String mp3 = API.mp3(320000L, id);
            JsonObject root = JsonParser.parseString((String)mp3).getAsJsonObject();
            JsonArray jsonArray = data = root.has("data") ? root.getAsJsonArray("data") : null;
            if (data != null && data.size() > 0 && (d0 = data.get(0).getAsJsonObject()).has("url") && !d0.get("url").isJsonNull() && !(u = d0.get("url").getAsString()).isEmpty()) {
                return u;
            }
        }
        catch (Exception e) {
            LOGGER.debug("320k resolve failed for {}, fallback to outer url", (Object)id);
        }
        return API.getRedirectMusicUrl(id);
    }

    public static String resolvePlaylistName(long id) {
        try {
            JsonObject playlist;
            String text = API.list(id);
            JsonObject root = JsonParser.parseString((String)text).getAsJsonObject();
            JsonObject jsonObject = playlist = root.has("playlist") ? root.getAsJsonObject("playlist") : null;
            if (playlist != null && playlist.has("name")) {
                return playlist.get("name").getAsString();
            }
        }
        catch (Exception e) {
            LOGGER.warn("Failed to get playlist name for {}: {}", (Object)id, (Object)e.getMessage());
        }
        return "Playlist " + id;
    }

    public static List<NetEaseSongInfo> resolvePlaylist(long id) {
        ArrayList<NetEaseSongInfo> result = new ArrayList<NetEaseSongInfo>();
        try {
            JsonObject playlist;
            String text = API.list(id);
            JsonObject root = JsonParser.parseString((String)text).getAsJsonObject();
            JsonObject jsonObject = playlist = root.has("playlist") ? root.getAsJsonObject("playlist") : null;
            if (playlist == null) {
                LOGGER.warn("Playlist {} has no playlist field", (Object)id);
                return result;
            }
            JsonArray tracks = playlist.has("tracks") ? playlist.getAsJsonArray("tracks") : new JsonArray();
            int max = RadioConfig.getMaxPlaylistSongs();
            for (JsonElement te : tracks) {
                boolean vip;
                if (result.size() >= max) {
                    LOGGER.info("Playlist {} truncated to {} songs (config maxPlaylistSongs)", (Object)id, (Object)max);
                    break;
                }
                JsonObject t = te.getAsJsonObject();
                long sid = t.get("id").getAsLong();
                String name = t.has("name") ? t.get("name").getAsString() : "Song " + sid;
                String artist = "";
                if (t.has("ar") && t.getAsJsonArray("ar").size() > 0) {
                    artist = t.getAsJsonArray("ar").get(0).getAsJsonObject().get("name").getAsString();
                }
                boolean bl = vip = t.has("fee") && t.get("fee").getAsInt() == 1;
                if (vip) {
                    LOGGER.info("Skipping VIP song {} ({}) - no free stream", (Object)sid, (Object)name);
                    continue;
                }
                result.add(new NetEaseSongInfo(sid, name, artist, false, NetEaseResolver.resolvePlayUrl(sid), id));
            }
        }
        catch (Exception e) {
            LOGGER.error("Failed to resolve playlist {}", (Object)id, (Object)e);
        }
        return result;
    }
}

