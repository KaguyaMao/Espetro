/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package LOL_141.vehicle_addition.client.api.implement;

import LOL_141.vehicle_addition.api.NetWorker;
import LOL_141.vehicle_addition.client.api.IAudioStreamHandler;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.m3u8.M3U8InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.spi.javasound.TSAudioFileReader;
import LOL_141.vehicle_addition.util.StreamUrlUtil;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class M3u8Handler
implements IAudioStreamHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Duration M3U8_TIMEOUT = Duration.ofSeconds(5L);
    private static final Duration TS_TIMEOUT = Duration.ofSeconds(10L);
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36";
    private static final String STREAM_INF = "#EXT-X-STREAM-INF";
    private static final String EXTM3U = "#EXTM3U";
    private static final String EXTINF = "#EXTINF";

    @Override
    public boolean canHandle(URL url) {
        return StreamUrlUtil.isM3u8Url(url);
    }

    @Override
    public AudioInputStream handle(URL url) throws UnsupportedAudioFileException, IOException {
        URI mediaUri = this.resolveMediaPlaylist(URI.create(url.toString()));
        Supplier<HttpRequest> playlistRequest = () -> HttpRequest.newBuilder(mediaUri).timeout(M3U8_TIMEOUT).header("User-Agent", USER_AGENT).header("Referer", M3u8Handler.refererOf(mediaUri)).header("Origin", M3u8Handler.originOf(mediaUri)).GET().build();
        Function<URI, HttpRequest> tsSegmentRequest = tsUri -> HttpRequest.newBuilder(tsUri).timeout(TS_TIMEOUT).header("User-Agent", USER_AGENT).header("Referer", M3u8Handler.refererOf(tsUri)).GET().build();
        M3U8InputStream m3U8InputStream = new M3U8InputStream(NetWorker.HTTP_CLIENT, playlistRequest, tsSegmentRequest);
        BufferedInputStream bis = new BufferedInputStream(m3U8InputStream, 0x500000);
        return new TSAudioFileReader().getAudioInputStream(bis);
    }

    private URI resolveMediaPlaylist(URI base) throws IOException {
        URI current = base;
        for (int depth = 0; depth < 8; ++depth) {
            String content = this.fetchPlaylistText(current);
            if (content == null) {
                LOGGER.warn("M3U8 playlist fetch returned null for {}", (Object)current);
                break;
            }
            if (!content.contains(EXTM3U)) {
                LOGGER.warn("M3U8 response for {} is not a standard playlist, first 300 chars: {}", (Object)current, (Object)content.substring(0, Math.min(300, content.length())).replace("\n", "\\n"));
                break;
            }
            if (!content.contains(STREAM_INF)) {
                return current;
            }
            String child = this.extractFirstVariantUrl(content);
            if (child == null) {
                LOGGER.warn("Variant playlist {} contains #EXT-X-STREAM-INF but no child URL found", (Object)current);
                return current;
            }
            URI childUri = current.resolve(child.trim());
            LOGGER.info("M3U8 variant {} -> resolved child {}", (Object)current, (Object)childUri);
            current = childUri;
        }
        return current;
    }

    private String fetchPlaylistText(URI uri) throws IOException {
        try {
            return NetWorker.get(uri.toString(), Map.of("User-Agent", USER_AGENT, "Referer", M3u8Handler.refererOf(uri), "Origin", M3u8Handler.originOf(uri)));
        }
        catch (Exception e) {
            LOGGER.error("Failed to fetch m3u8 playlist {}", (Object)uri, (Object)e);
            return null;
        }
    }

    private static String refererOf(URI uri) {
        return uri.getScheme() + "://" + uri.getAuthority();
    }

    private static String originOf(URI uri) {
        return uri.getScheme() + "://" + uri.getAuthority();
    }

    private String extractFirstVariantUrl(String content) {
        String[] lines = content.split("\\r?\\n");
        boolean expectUri = false;
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty()) continue;
            if (expectUri) {
                if (line.startsWith("#")) continue;
                return line;
            }
            if (!line.startsWith(STREAM_INF)) continue;
            expectUri = true;
        }
        return null;
    }

    @Override
    public int getPriority() {
        return 100;
    }
}

