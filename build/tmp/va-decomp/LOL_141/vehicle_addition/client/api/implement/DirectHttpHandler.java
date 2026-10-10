/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.client.api.implement;

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

public class DirectHttpHandler
implements IAudioStreamHandler {
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36";

    @Override
    public boolean canHandle(URL url) {
        String protocol = url.getProtocol();
        return HTTP.equalsIgnoreCase(protocol) || HTTPS.equalsIgnoreCase(protocol);
    }

    @Override
    public AudioInputStream handle(URL url) throws UnsupportedAudioFileException, IOException {
        return this.handle(url, 0L);
    }

    @Override
    public AudioInputStream handle(URL url, long startOffset) throws UnsupportedAudioFileException, IOException {
        Function<Long, HttpRequest> request = start -> HttpRequest.newBuilder(URI.create(url.toString())).header("User-Agent", USER_AGENT).header("Range", "bytes=%d-".formatted(start)).GET().build();
        ChunkedAudioStream stream = new ChunkedAudioStream(request, startOffset);
        MusicBufferedInputStream bufferedInputStream = new MusicBufferedInputStream(stream);
        Mp3Util.skipID3(bufferedInputStream);
        return AudioSystem.getAudioInputStream(bufferedInputStream);
    }
}

