/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package LOL_141.vehicle_addition.radio;

import LOL_141.vehicle_addition.api.netease.NetEaseMusic;
import LOL_141.vehicle_addition.api.netease.WebApi;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LyricManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final WebApi API = new WebApi(new NetEaseMusic().getRequestPropertyData());
    private static final Pattern TIME_TAG = Pattern.compile("\\[(\\d{1,3}):(\\d{1,2})(?:[.:](\\d{1,3}))?]");
    private static final Pattern OFFSET_TAG = Pattern.compile("\\[offset:(\\d+)]");
    private static final Map<Long, List<LyricLine>> CACHE = new ConcurrentHashMap<Long, List<LyricLine>>();
    private static final Set<Long> FETCHING = ConcurrentHashMap.newKeySet();
    private static volatile List<LyricLine> currentLyrics = null;
    private static volatile long currentSongId = -1L;

    private LyricManager() {
    }

    public static boolean isActive() {
        return currentSongId > 0L && currentLyrics != null && !currentLyrics.isEmpty();
    }

    public static List<LyricLine> lyrics() {
        return currentLyrics;
    }

    public static int currentIndex(long positionMs) {
        List<LyricLine> lyrics = currentLyrics;
        if (lyrics == null || lyrics.isEmpty() || positionMs < 0L) {
            return -1;
        }
        int lo = 0;
        int hi = lyrics.size() - 1;
        int best = -1;
        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            if (lyrics.get(mid).timeMs() <= positionMs) {
                best = mid;
                lo = mid + 1;
                continue;
            }
            hi = mid - 1;
        }
        return best;
    }

    public static void onSongStart(long songId) {
        currentSongId = songId;
        currentLyrics = null;
        if (songId <= 0L) {
            return;
        }
        List<LyricLine> cached = CACHE.get(songId);
        if (cached != null) {
            currentLyrics = cached;
            return;
        }
        if (FETCHING.add(songId)) {
            CompletableFuture.runAsync(() -> {
                try {
                    String resp = API.lyric(songId);
                    List<LyricLine> lines = LyricManager.parseLrc(resp);
                    if (!lines.isEmpty()) {
                        CACHE.put(songId, lines);
                        if (currentSongId == songId) {
                            currentLyrics = lines;
                        }
                    }
                }
                catch (Exception e) {
                    LOGGER.debug("lyric fetch failed for {}: {}", (Object)songId, (Object)e.toString());
                }
                finally {
                    FETCHING.remove(songId);
                }
            });
        }
    }

    public static void clear() {
        currentLyrics = null;
        currentSongId = -1L;
    }

    static List<LyricLine> parseLrc(String jsonResp) {
        if (jsonResp == null || jsonResp.isEmpty()) {
            return List.of();
        }
        try {
            JsonObject lrc;
            JsonObject root = JsonParser.parseString((String)jsonResp).getAsJsonObject();
            if (root.has("nolyric") && root.get("nolyric").getAsBoolean()) {
                return List.of();
            }
            JsonObject jsonObject = lrc = root.has("lrc") ? root.getAsJsonObject("lrc") : null;
            if (lrc == null || !lrc.has("lyric")) {
                return List.of();
            }
            String lrcText = lrc.get("lyric").getAsString();
            if (lrcText == null || lrcText.isEmpty()) {
                return List.of();
            }
            long offset = 0L;
            Matcher om = OFFSET_TAG.matcher(lrcText);
            if (om.find()) {
                try {
                    offset = Long.parseLong(om.group(1));
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
            ArrayList<LyricLine> out = new ArrayList<LyricLine>();
            for (String rawLine : lrcText.split("\n")) {
                String text;
                Matcher m = TIME_TAG.matcher(rawLine);
                ArrayList<Long> times = new ArrayList<Long>(2);
                int lastEnd = 0;
                while (m.find()) {
                    long t = LyricManager.parseTime(m);
                    if (t >= 0L) {
                        times.add(t);
                    }
                    lastEnd = m.end();
                }
                if (times.isEmpty() || (text = rawLine.substring(lastEnd).trim()).isEmpty()) continue;
                Iterator iterator = times.iterator();
                while (iterator.hasNext()) {
                    long t = (Long)iterator.next();
                    out.add(new LyricLine(t + offset, text));
                }
            }
            out.sort(Comparator.comparingLong(LyricLine::timeMs));
            return out;
        }
        catch (Exception e) {
            LOGGER.debug("lrc parse failed: {}", (Object)e.toString());
            return List.of();
        }
    }

    private static long parseTime(Matcher m) {
        try {
            long min = Long.parseLong(m.group(1));
            long sec = Long.parseLong(m.group(2));
            long ms = 0L;
            String frac = m.group(3);
            if (frac != null && !frac.isEmpty()) {
                ms = switch (frac.length()) {
                    case 1 -> Long.parseLong(frac) * 100L;
                    case 2 -> Long.parseLong(frac) * 10L;
                    default -> Long.parseLong(frac.substring(0, 3));
                };
            }
            return min * 60000L + sec * 1000L + ms;
        }
        catch (NumberFormatException e) {
            return -1L;
        }
    }

    public record LyricLine(long timeMs, String text) {
    }
}

