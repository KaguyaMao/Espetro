/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  org.apache.commons.lang3.StringUtils
 */
package LOL_141.vehicle_addition.client.api.implement;

import LOL_141.vehicle_addition.api.NetWorker;
import LOL_141.vehicle_addition.client.api.IAudioStreamHandler;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.m3u8.M3U8InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.spi.javasound.TSAudioFileReader;
import LOL_141.vehicle_addition.util.StreamUrlUtil;
import com.google.common.base.Splitter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import org.apache.commons.lang3.StringUtils;

public class CnrM3u8Handler
implements IAudioStreamHandler {
    private static final Duration M3U8_TIMEOUT = Duration.ofSeconds(5L);
    private static final Duration TS_TIMEOUT = Duration.ofSeconds(10L);
    private static final String PLAY_URL = "https://apicnrapp.cnr.cn/html/play.html";
    private static final String API = "https://pacc.cnr.cn/ygw/getlivechannel?channelId=%s&111";
    private static final String CHANNEL_ID = "channelId";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36";

    @Override
    public boolean canHandle(URL url) {
        return url.toString().startsWith(PLAY_URL);
    }

    @Override
    public AudioInputStream handle(URL url) throws UnsupportedAudioFileException, IOException {
        URI m3u8Uri = this.getM3u8Uri(url);
        Supplier<HttpRequest> playlistRequest = () -> HttpRequest.newBuilder(m3u8Uri).timeout(M3U8_TIMEOUT).header("User-Agent", USER_AGENT).GET().build();
        Function<URI, HttpRequest> tsSegmentRequest = tsUri -> HttpRequest.newBuilder(tsUri).timeout(TS_TIMEOUT).header("User-Agent", USER_AGENT).GET().build();
        M3U8InputStream m3U8InputStream = new M3U8InputStream(NetWorker.HTTP_CLIENT, playlistRequest, tsSegmentRequest);
        BufferedInputStream bis = new BufferedInputStream(m3U8InputStream, 0x500000);
        return new TSAudioFileReader().getAudioInputStream(bis);
    }

    private URI getM3u8Uri(URL url) throws IOException {
        String query = url.getQuery();
        if (StringUtils.isBlank((CharSequence)query)) {
            throw new IOException("URL must contain query parameters");
        }
        Map params = Splitter.on((char)'&').withKeyValueSeparator('=').split((CharSequence)query);
        if (!params.containsKey(CHANNEL_ID)) {
            throw new IOException("URL must contain channelId parameter");
        }
        try {
            String apiUrl = String.format(API, params.get(CHANNEL_ID));
            String text = NetWorker.get(apiUrl, Map.of("User-Agent", USER_AGENT));
            JsonObject root = JsonParser.parseString((String)text).getAsJsonObject();
            String m3u8Url = root.getAsJsonObject("data").getAsJsonArray("categories").get(0).getAsJsonObject().getAsJsonArray("detail").get(0).getAsJsonObject().getAsJsonArray("other_info11").get(0).getAsJsonObject().get("url").getAsString();
            if (StreamUrlUtil.isValidStreamUrl(m3u8Url)) {
                return URI.create(m3u8Url);
            }
            throw new IOException("Invalid M3U8 URL from API: " + m3u8Url);
        }
        catch (Throwable e) {
            throw new IOException("Failed to get M3U8 URL from API", e);
        }
    }

    @Override
    public int getPriority() {
        return 200;
    }
}

