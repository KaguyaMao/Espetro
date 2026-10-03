/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.m3u8;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.m3u8.M3U8Parser;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.m3u8.M3U8Playlist;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public class M3U8InputStream
extends InputStream {
    private final BlockingQueue<InputStream> segmentQueue = new LinkedBlockingQueue<InputStream>(5);
    private final Set<URI> processedUrls = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<URI, Boolean>(64, 0.75f, true){

        @Override
        protected boolean removeEldestEntry(Map.Entry<URI, Boolean> eldest) {
            return this.size() > 50;
        }
    }));
    private final HttpClient httpClient;
    private final Supplier<HttpRequest> playlistRequest;
    private final Function<URI, HttpRequest> tsSegmentRequest;
    private InputStream currentSegmentStream = null;
    private ScheduledExecutorService scheduler;
    private volatile boolean isClosed = false;
    private volatile boolean noMoreSegments = false;

    public M3U8InputStream(HttpClient httpClient, Supplier<HttpRequest> playlistRequest, Function<URI, HttpRequest> tsSegmentRequest) {
        this.httpClient = httpClient;
        this.playlistRequest = playlistRequest;
        this.tsSegmentRequest = tsSegmentRequest;
        this.initScheduler();
    }

    private void initScheduler() {
        this.scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory(){
            private final AtomicInteger counter = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "M3U8-Worker-" + this.counter.getAndIncrement());
                t.setDaemon(true);
                return t;
            }
        });
        this.scheduler.execute(this::refreshAndDownloadTask);
    }

    private void refreshAndDownloadTask() {
        if (this.isClosed || this.noMoreSegments) {
            return;
        }
        try {
            M3U8Playlist playlist = M3U8Parser.fetchPlaylist(this.httpClient, this.playlistRequest);
            for (URI uri : playlist.getNewTsUrls()) {
                if (this.isClosed) break;
                if (this.processedUrls.contains(uri)) continue;
                InputStream tsData = M3U8Parser.fetchSegment(this.httpClient, uri, this.tsSegmentRequest);
                this.segmentQueue.put(tsData);
                this.processedUrls.add(uri);
            }
            if (!playlist.isLive()) {
                this.noMoreSegments = true;
                return;
            }
            if (!this.isClosed && !this.noMoreSegments) {
                long nextRefreshDelayMs = playlist.getTargetDurationMs();
                this.scheduler.schedule(this::refreshAndDownloadTask, nextRefreshDelayMs, TimeUnit.MILLISECONDS);
            }
        }
        catch (Throwable e) {
            try {
                this.close();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    @Override
    public int read() throws IOException {
        while (!this.isClosed) {
            int data;
            if (this.currentSegmentStream == null) {
                if (this.noMoreSegments && this.segmentQueue.isEmpty()) {
                    return -1;
                }
                try {
                    this.currentSegmentStream = this.segmentQueue.poll(5L, TimeUnit.SECONDS);
                    if (this.currentSegmentStream == null) {
                        return -1;
                    }
                }
                catch (InterruptedException e) {
                    return -1;
                }
            }
            if ((data = this.currentSegmentStream.read()) != -1) {
                return data;
            }
            this.currentSegmentStream.close();
            this.currentSegmentStream = null;
        }
        return -1;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        while (!this.isClosed) {
            int bytesRead;
            if (this.currentSegmentStream == null) {
                if (this.noMoreSegments && this.segmentQueue.isEmpty()) {
                    return -1;
                }
                try {
                    this.currentSegmentStream = this.segmentQueue.poll(5L, TimeUnit.SECONDS);
                    if (this.currentSegmentStream == null) {
                        return -1;
                    }
                }
                catch (InterruptedException e) {
                    return -1;
                }
            }
            if ((bytesRead = this.currentSegmentStream.read(b, off, len)) != -1) {
                return bytesRead;
            }
            this.currentSegmentStream.close();
            this.currentSegmentStream = null;
        }
        return -1;
    }

    @Override
    public void close() throws IOException {
        InputStream queuedStream;
        if (this.isClosed) {
            return;
        }
        this.isClosed = true;
        if (this.scheduler != null) {
            this.scheduler.shutdownNow();
        }
        if (this.currentSegmentStream != null) {
            this.currentSegmentStream.close();
        }
        while ((queuedStream = (InputStream)this.segmentQueue.poll()) != null) {
            queuedStream.close();
        }
        this.processedUrls.clear();
        super.close();
    }
}

