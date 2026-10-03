/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package LOL_141.vehicle_addition.client.audio;

import LOL_141.vehicle_addition.api.NetWorker;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkedAudioStream
extends InputStream {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Function<Long, HttpRequest> request;
    private InputStream currentStream;
    private long currentStart;

    public ChunkedAudioStream(Function<Long, HttpRequest> request) throws IOException {
        this(request, 0L);
    }

    public ChunkedAudioStream(Function<Long, HttpRequest> request, long startOffset) throws IOException {
        this.request = request;
        this.currentStart = startOffset;
        this.currentStream = this.openChunk(this.currentStart);
    }

    private InputStream openChunk(long start) throws IOException {
        HttpRequest httpRequest = this.request.apply(start);
        URI uri = httpRequest.uri();
        HttpResponse<InputStream> response = NetWorker.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        int statusCode = response.statusCode();
        if (statusCode != 200 && statusCode != 206) {
            throw new IOException("Audio not found at %s: %d".formatted(uri, statusCode));
        }
        return Optional.ofNullable(response.body()).orElseThrow(() -> new IOException("Audio not found at %s: empty response body".formatted(uri)));
    }

    private InputStream getCurrentStream() throws IOException {
        if (this.currentStream == null) {
            this.currentStream = this.openChunk(this.currentStart);
        }
        return this.currentStream;
    }

    public int tryRead(int count) throws IOException {
        if (count <= 0) {
            throw new IOException("Failed to read audio stream after multiple attempts");
        }
        try {
            return this.getCurrentStream().read();
        }
        catch (IOException e) {
            LOGGER.error("Error reading audio stream at {}: {}, left {} attempts", (Object)this.currentStart, (Object)e.getMessage(), (Object)(count - 1));
            this.clearCurrentStream();
            return this.tryRead(--count);
        }
    }

    public int tryRead(byte[] b, int off, int len, int count) throws IOException {
        if (count <= 0) {
            throw new IOException("Failed to read audio stream after multiple attempts");
        }
        try {
            return this.getCurrentStream().read(b, off, len);
        }
        catch (IOException e) {
            LOGGER.error("Error reading audio stream at {}: {}, left {} attempts", (Object)this.currentStart, (Object)e.getMessage(), (Object)(count - 1));
            this.clearCurrentStream();
            return this.tryRead(b, off, len, --count);
        }
    }

    @Override
    public int read() throws IOException {
        int byteRead = this.tryRead(3);
        this.currentStart += (long)byteRead;
        return byteRead;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int byteRead = this.tryRead(b, off, len, 3);
        this.currentStart += (long)byteRead;
        return byteRead;
    }

    private void clearCurrentStream() throws IOException {
        if (this.currentStream != null) {
            InputStream stream = this.currentStream;
            this.currentStream = null;
            stream.close();
        }
    }

    @Override
    public void close() throws IOException {
        this.clearCurrentStream();
        super.close();
    }
}

