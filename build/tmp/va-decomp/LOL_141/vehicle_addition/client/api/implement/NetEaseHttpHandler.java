/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.client.api.implement;

import LOL_141.vehicle_addition.api.netease.NetEaseMusic;
import LOL_141.vehicle_addition.client.api.IAudioStreamHandler;
import LOL_141.vehicle_addition.client.audio.ChunkedAudioStream;
import LOL_141.vehicle_addition.client.audio.MusicBufferedInputStream;
import LOL_141.vehicle_addition.util.Mp3Util;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.util.function.Function;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class NetEaseHttpHandler
implements IAudioStreamHandler {
    private static final String HTTP = "http";
    private static final String HTTPS = "https";

    @Override
    public boolean canHandle(URL url) {
        String host = url.getHost();
        String protocol = url.getProtocol();
        return host.contains(NetEaseMusic.getHost()) && (HTTP.equalsIgnoreCase(protocol) || HTTPS.equalsIgnoreCase(protocol));
    }

    @Override
    public AudioInputStream handle(URL url) throws UnsupportedAudioFileException, IOException {
        return this.handle(url, 0L);
    }

    @Override
    public AudioInputStream handle(URL url, long startOffset) throws UnsupportedAudioFileException, IOException {
        Function<Long, HttpRequest> request = start -> HttpRequest.newBuilder(URI.create(url.toString())).header("User-Agent", NetEaseMusic.getUserAgent()).header("Referer", NetEaseMusic.getReferer()).header("Range", "bytes=%d-".formatted(start)).GET().build();
        ChunkedAudioStream stream = new ChunkedAudioStream(request, startOffset);
        MusicBufferedInputStream bufferedInputStream = new MusicBufferedInputStream(stream);
        Mp3Util.skipID3(bufferedInputStream);
        return AudioSystem.getAudioInputStream(bufferedInputStream);
    }

    @Override
    public int getPriority() {
        return 10;
    }
}

