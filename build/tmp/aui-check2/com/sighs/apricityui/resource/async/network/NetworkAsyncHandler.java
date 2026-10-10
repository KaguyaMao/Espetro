/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.resource.async.network;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.loader.Loader;
import com.sighs.apricityui.resource.async.network.NetworkHandle;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.task.AbstractAsyncHandler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import javax.net.ssl.HttpsURLConnection;

public final class NetworkAsyncHandler
extends AbstractAsyncHandler<Void> {
    public static final NetworkAsyncHandler INSTANCE = new NetworkAsyncHandler();
    private static final Map<String, CacheEntry> SUCCESS_CACHE = new ConcurrentHashMap<String, CacheEntry>();
    private static final AtomicLong LAST_SUCCESS_CACHE_SWEEP_MS = new AtomicLong();
    private static final Map<String, InFlightRequest> IN_FLIGHT = new ConcurrentHashMap<String, InFlightRequest>();
    private static final Map<String, NetworkHandle> HANDLES = new ConcurrentHashMap<String, NetworkHandle>();
    private static final Semaphore PERMITS = new Semaphore(4, true);

    private NetworkAsyncHandler() {
        super("network", 32, 1, 1500000L, "ApricityUI-NetworkWorker");
    }

    private static NetworkHandle prepareHandle(NetworkHandle existing, String url, long generation, long now) {
        NetworkHandle handle = existing;
        if (handle == null || handle.generation() != generation || handle.state() == AbstractAsyncHandler.AsyncState.STALE) {
            return new NetworkHandle(url, generation);
        }
        if (handle.state() == AbstractAsyncHandler.AsyncState.FAILED && now - handle.failedAtMs() >= 5000L) {
            handle.reset(generation);
        }
        return handle;
    }

    private static byte[] downloadWithRetry(String url) throws IOException {
        int attempt = 0;
        while (true) {
            try {
                return NetworkAsyncHandler.downloadOnce(url);
            }
            catch (RetryableHttpException retryable) {
                if (attempt >= 1) {
                    throw new IOException("\u4e0b\u8f7d\u5931\u8d25: " + url + " (HTTP " + retryable.statusCode + ")", retryable);
                }
                ApricityUI.LOGGER.warn("[AUI Network] retrying HTTP request url={} status={} attempt={}/{}", new Object[]{url, retryable.statusCode, attempt + 1, 1});
                NetworkAsyncHandler.sleepQuietly(retryable.delayMs);
                ++attempt;
                continue;
            }
            catch (SocketTimeoutException timeout) {
                if (attempt >= 1) {
                    throw new IOException("\u4e0b\u8f7d\u8d85\u65f6: " + url, timeout);
                }
                ApricityUI.LOGGER.warn("[AUI Network] retrying timed out request url={} attempt={}/{}", new Object[]{url, attempt + 1, 1});
                NetworkAsyncHandler.sleepQuietly(2000L);
                ++attempt;
                continue;
            }
            break;
        }
    }

    /*
     * Loose catch block
     */
    private static byte[] downloadOnce(String originUrl) throws IOException {
        NetworkAsyncHandler.acquirePermit();
        try {
            String requestUrl = originUrl;
            for (int i = 0; i <= 3; ++i) {
                HttpsURLConnection connection = NetworkAsyncHandler.openConnection(requestUrl);
                try {
                    InputStream inputStream;
                    int status = connection.getResponseCode();
                    if (!NetworkAsyncHandler.isRedirect(status)) {
                        byte[] byArray;
                        block20: {
                            if (status == 429) {
                                throw new RetryableHttpException(status, 20000L);
                            }
                            if (status >= 500 && status <= 599) {
                                throw new RetryableHttpException(status, 2000L);
                            }
                            if (status < 200 || status >= 300) {
                                throw new IOException("\u4e0b\u8f7d\u5931\u8d25: " + requestUrl + " (HTTP " + status + ")");
                            }
                            NetworkAsyncHandler.validateContentType(connection.getContentType(), requestUrl);
                            int contentLength = connection.getContentLength();
                            if (contentLength > 0x800000) {
                                throw new IOException("\u8d44\u6e90\u8d85\u51fa\u5927\u5c0f\u9650\u5236(8MB): " + requestUrl);
                            }
                            inputStream = connection.getInputStream();
                            byArray = NetworkAsyncHandler.readAllBytesWithLimit(inputStream, requestUrl);
                            if (inputStream == null) break block20;
                            inputStream.close();
                        }
                        return byArray;
                    }
                    requestUrl = NetworkAsyncHandler.resolveRedirect(requestUrl, connection.getHeaderField("Location"));
                    continue;
                    catch (Throwable throwable) {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                }
                finally {
                    connection.disconnect();
                }
            }
            throw new IOException("\u91cd\u5b9a\u5411\u6b21\u6570\u8d85\u9650: " + originUrl);
        }
        finally {
            PERMITS.release();
        }
    }

    private static String resolveRedirect(String fromUrl, String location) throws IOException {
        if (location == null || location.isBlank()) {
            throw new IOException("\u91cd\u5b9a\u5411\u7f3a\u5931 Location: " + fromUrl);
        }
        URI base = URI.create(fromUrl);
        URI target = base.resolve(location);
        String resolved = target.toString();
        if (!Loader.isRemotePath(resolved)) {
            throw new IOException("\u91cd\u5b9a\u5411\u76ee\u6807\u975e HTTPS\uff0c\u5df2\u62d2\u7edd: " + resolved);
        }
        return resolved;
    }

    private static HttpsURLConnection openConnection(String url) throws IOException {
        URL target = URI.create(url).toURL();
        HttpsURLConnection connection = (HttpsURLConnection)target.openConnection();
        connection.setRequestMethod("GET");
        connection.setUseCaches(false);
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3000);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("User-Agent", "ApricityUI/AsyncResourceLoader");
        return connection;
    }

    private static void validateContentType(String contentType, String url) throws IOException {
        if (contentType == null || contentType.isBlank()) {
            return;
        }
        String normalized = contentType.toLowerCase();
        if (normalized.startsWith("image/")) {
            return;
        }
        if (normalized.startsWith("text/css")) {
            return;
        }
        if (normalized.startsWith("font/")) {
            return;
        }
        if (normalized.startsWith("application/font")) {
            return;
        }
        throw new IOException("\u8fdc\u7a0b\u8d44\u6e90\u7c7b\u578b\u4e0d\u652f\u6301: " + url + " (Content-Type: " + contentType + ")");
    }

    private static void acquirePermit() throws IOException {
        try {
            PERMITS.acquire();
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new IOException("\u4e0b\u8f7d\u7ebf\u7a0b\u88ab\u4e2d\u65ad", interruptedException);
        }
    }

    private static byte[] readAllBytesWithLimit(InputStream inputStream, String url) throws IOException {
        int read;
        ByteArrayOutputStream output = new ByteArrayOutputStream(16384);
        byte[] buffer = new byte[8192];
        int total = 0;
        while ((read = inputStream.read(buffer)) != -1) {
            if ((total += read) > 0x800000) {
                throw new IOException("\u8d44\u6e90\u8d85\u51fa\u5927\u5c0f\u9650\u5236(8MB): " + url);
            }
            output.write(buffer, 0, read);
        }
        if (total <= 0) {
            throw new IOException("\u8fdc\u7a0b\u8d44\u6e90\u4e3a\u7a7a: " + url);
        }
        return output.toByteArray();
    }

    private static boolean isRedirect(int status) {
        return status == 301 || status == 302 || status == 303 || status == 307 || status == 308;
    }

    private static void sleepQuietly(long delayMs) {
        try {
            Thread.sleep(delayMs);
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            ApricityUI.LOGGER.warn("[AUI Network] retry wait interrupted", (Throwable)interruptedException);
        }
    }

    public byte[] fetchBytes(String url) throws IOException {
        byte[] diskCached;
        if (!Loader.isRemotePath(url)) {
            ApricityUI.LOGGER.error("[AUI Network] rejected non-HTTPS resource url={}", (Object)url);
            throw new IOException("\u4ec5\u5141\u8bb8 HTTPS \u8fdc\u7a0b\u8d44\u6e90: " + url);
        }
        long now = System.currentTimeMillis();
        NetworkAsyncHandler.sweepExpiredIfDue(now);
        long generation = this.currentGeneration();
        NetworkHandle handle = HANDLES.compute(url, (key, existing) -> NetworkAsyncHandler.prepareHandle(existing, key, generation, now));
        CacheEntry cached = SUCCESS_CACHE.get(url);
        if (cached != null && cached.expiresAtMs > now) {
            handle.markReady();
            return cached.bytes;
        }
        if (cached != null) {
            SUCCESS_CACHE.remove(url, cached);
        }
        if ((diskCached = NetworkAsyncHandler.readDiskCache(url, now)) != null) {
            NetworkAsyncHandler.putCache(url, diskCached, now);
            handle.markReady();
            return diskCached;
        }
        InFlightRequest own = new InFlightRequest();
        InFlightRequest existing2 = IN_FLIGHT.putIfAbsent(url, own);
        if (existing2 != null) {
            byte[] bytes = existing2.await(url);
            handle.markReady();
            return bytes;
        }
        handle.markLoading();
        try {
            byte[] bytes = NetworkAsyncHandler.downloadWithRetry(url);
            NetworkAsyncHandler.putCache(url, bytes, System.currentTimeMillis());
            NetworkAsyncHandler.writeDiskCache(url, bytes);
            own.complete(bytes, null);
            handle.markReady();
            byte[] byArray = bytes;
            return byArray;
        }
        catch (IOException exception) {
            own.complete(null, exception);
            handle.markFailed(exception, System.currentTimeMillis());
            ApricityUI.LOGGER.error("[AUI Network] request failed url={} state={} generation={}", new Object[]{url, handle.state(), generation, exception});
            throw exception;
        }
        finally {
            IN_FLIGHT.remove(url, own);
        }
    }

    private static byte[] readDiskCache(String url, long nowMs) {
        try {
            Path file = NetworkAsyncHandler.diskCachePath(url);
            if (!Files.exists(file, new LinkOption[0]) || !Files.isRegularFile(file, new LinkOption[0])) {
                return null;
            }
            long ageMs = nowMs - Files.getLastModifiedTime(file, new LinkOption[0]).toMillis();
            if (ageMs < 0L || ageMs > 604800000L) {
                return null;
            }
            long size = Files.size(file);
            if (size <= 0L || size > 0x800000L) {
                return null;
            }
            return Files.readAllBytes(file);
        }
        catch (Exception exception) {
            ApricityUI.LOGGER.debug("[AUI Network] disk cache read failed url={}", (Object)url, (Object)exception);
            return null;
        }
    }

    private static void writeDiskCache(String url, byte[] bytes) {
        if (bytes == null || bytes.length == 0 || bytes.length > 0x800000) {
            return;
        }
        try {
            Path file = NetworkAsyncHandler.diskCachePath(url);
            Files.createDirectories(file.getParent(), new FileAttribute[0]);
            Files.write(file, bytes, new OpenOption[0]);
        }
        catch (Exception exception) {
            ApricityUI.LOGGER.warn("[AUI Network] disk cache write failed url={}", (Object)url, (Object)exception);
        }
    }

    private static Path diskCachePath(String url) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String hash = HexFormat.of().formatHex(digest.digest(url.getBytes(StandardCharsets.UTF_8)));
        return NetworkAsyncHandler.resolveGameDir().resolve("apricity/.cache/network/" + hash + ".bin");
    }

    private static Path resolveGameDir() {
        return AuiServices.client().getGameDirectory();
    }

    private static void putCache(String url, byte[] bytes, long nowMs) {
        SUCCESS_CACHE.put(url, new CacheEntry(bytes, nowMs + 60000L, nowMs));
        NetworkAsyncHandler.trimCache(nowMs);
    }

    private static void trimCache(long nowMs) {
        SUCCESS_CACHE.entrySet().removeIf(entry -> ((CacheEntry)entry.getValue()).expiresAtMs <= nowMs);
        int excess = SUCCESS_CACHE.size() - 256;
        if (excess <= 0) {
            return;
        }
        SUCCESS_CACHE.entrySet().stream().sorted(Comparator.comparingLong(entry -> ((CacheEntry)entry.getValue()).storedAtMs)).limit(excess).forEach(entry -> SUCCESS_CACHE.remove(entry.getKey(), entry.getValue()));
    }

    private static void sweepExpiredIfDue(long nowMs) {
        long last = LAST_SUCCESS_CACHE_SWEEP_MS.get();
        if (nowMs - last < 30000L) {
            return;
        }
        if (LAST_SUCCESS_CACHE_SWEEP_MS.compareAndSet(last, nowMs)) {
            SUCCESS_CACHE.entrySet().removeIf(entry -> ((CacheEntry)entry.getValue()).expiresAtMs <= nowMs);
        }
    }

    @Override
    protected void applyOnMainThread(Void task, long currentGeneration) {
    }

    @Override
    protected void onBeforeClear(long nextGeneration) {
        for (NetworkHandle handle : HANDLES.values()) {
            handle.markStale();
        }
        HANDLES.clear();
        SUCCESS_CACHE.clear();
        IN_FLIGHT.clear();
    }

    private static final class RetryableHttpException
    extends IOException {
        private final int statusCode;
        private final long delayMs;

        private RetryableHttpException(int statusCode, long delayMs) {
            this.statusCode = statusCode;
            this.delayMs = delayMs;
        }
    }

    private record CacheEntry(byte[] bytes, long expiresAtMs, long storedAtMs) {
    }

    private static final class InFlightRequest {
        private final CountDownLatch latch = new CountDownLatch(1);
        private volatile byte[] bytes;
        private volatile IOException error;

        private InFlightRequest() {
        }

        private void complete(byte[] bytes, IOException error) {
            this.bytes = bytes;
            this.error = error;
            this.latch.countDown();
        }

        private byte[] await(String url) throws IOException {
            try {
                this.latch.await();
            }
            catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                ApricityUI.LOGGER.warn("[AUI Network] waiting for in-flight request was interrupted url={}", (Object)url, (Object)interruptedException);
                throw new IOException("\u7b49\u5f85\u8fdc\u7a0b\u8d44\u6e90\u7ed3\u679c\u88ab\u4e2d\u65ad: " + url, interruptedException);
            }
            if (this.error != null) {
                throw this.error;
            }
            if (this.bytes == null) {
                throw new IOException("\u8fdc\u7a0b\u8d44\u6e90\u7ed3\u679c\u4e3a\u7a7a: " + url);
            }
            return this.bytes;
        }
    }
}

