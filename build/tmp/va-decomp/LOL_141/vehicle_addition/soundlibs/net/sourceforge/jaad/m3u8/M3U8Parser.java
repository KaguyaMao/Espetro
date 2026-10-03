/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.m3u8;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.m3u8.M3U8Playlist;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class M3U8Parser {
    public static M3U8Playlist fetchPlaylist(HttpClient client, Supplier<HttpRequest> request) throws IOException, InterruptedException {
        HttpRequest httpRequest = request.get();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch M3U8, HTTP code: " + response.statusCode());
        }
        int targetDurationSec = 5;
        long mediaSequence = 0L;
        boolean isLive = true;
        boolean isM3U8 = false;
        boolean expectChildPlaylist = false;
        ArrayList<URI> tsUrls = new ArrayList<URI>();
        List<String> lines = response.body().lines().toList();
        for (String line : lines) {
            if ((line = line.trim()).isEmpty()) continue;
            if (line.startsWith("#EXTM3U")) {
                isM3U8 = true;
                continue;
            }
            if (line.startsWith("#EXT-X-TARGETDURATION:")) {
                targetDurationSec = Integer.parseInt(line.split(":")[1].trim());
                continue;
            }
            if (line.startsWith("#EXT-X-MEDIA-SEQUENCE:")) {
                mediaSequence = Long.parseLong(line.split(":")[1].trim());
                continue;
            }
            if (line.startsWith("#EXT-X-ENDLIST")) {
                isLive = false;
                continue;
            }
            if (line.startsWith("#EXT-X-STREAM-INF:")) {
                expectChildPlaylist = true;
                continue;
            }
            if (line.startsWith("#")) continue;
            URI resolvedUrl = httpRequest.uri().resolve(line);
            if (expectChildPlaylist) {
                return M3U8Parser.fetchPlaylist(client, request);
            }
            tsUrls.add(resolvedUrl);
        }
        if (!isM3U8) {
            throw new IOException("Invalid M3U8 format: Missing #EXTM3U header in " + String.valueOf(httpRequest.uri()));
        }
        return new M3U8Playlist(targetDurationSec, isLive, mediaSequence, tsUrls);
    }

    public static InputStream fetchSegment(HttpClient client, URI tsUri, Function<URI, HttpRequest> tsSegmentRequest) throws IOException, InterruptedException {
        HttpRequest request = tsSegmentRequest.apply(tsUri);
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() == 200) {
            return new ByteArrayInputStream(response.body());
        }
        throw new IOException("Failed to fetch TS segment, HTTP code: " + response.statusCode() + ", URI: " + String.valueOf(tsUri));
    }
}

