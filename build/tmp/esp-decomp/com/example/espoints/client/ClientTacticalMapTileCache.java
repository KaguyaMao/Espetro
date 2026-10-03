/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.resources.ResourceLocation
 */
package com.example.espoints.client;

import com.example.espoints.ESPointsMod;
import com.example.espoints.client.TacticalMapTextureSampling;
import com.example.espoints.config.TacticalMapConfig;
import com.example.espoints.network.NetworkHandler;
import com.example.espoints.network.RequestTacticalMapTileMessage;
import com.example.espoints.tile.ClientTileRequestScheduler;
import com.example.espoints.tile.RollingLatencyWindow;
import com.example.espoints.tile.TacticalMapLodPlanner;
import com.example.espoints.tile.TacticalMapPyramidLayout;
import com.example.espoints.tile.TacticalMapTextureFilterPolicy;
import com.example.espoints.tile.TacticalMapTileAtlasLayout;
import com.example.espoints.tile.TacticalMapTileKey;
import com.example.espoints.tile.TacticalMapTileService;
import com.example.espoints.tile.WeightedLruCache;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

public final class ClientTacticalMapTileCache {
    private static final int MAX_DECODED_UPLOAD_QUEUE = 24;
    private static final int MAX_QUEUED_DECODES = 32;
    private static final int DEFAULT_SENDS_PER_TICK = 8;
    private static final int MAX_LOCAL_LOADS_PER_UPDATE = 24;
    private static final long DESIRED_PLAN_EXPIRY_MILLIS = 15000L;
    private static final ClientTacticalMapTileCache INSTANCE = new ClientTacticalMapTileCache();
    private final WeightedLruCache<TacticalMapTileKey, TextureEntry> textures = new WeightedLruCache(0x4000000L, TextureEntry::weight);
    private final ClientTileRequestScheduler<TacticalMapTileKey> requests = new ClientTileRequestScheduler();
    private final Set<TacticalMapTileKey> decoding = new HashSet<TacticalMapTileKey>();
    private static final Comparator<DecodeJob> DECODE_ORDER = Comparator.comparingInt(DecodeJob::priority).thenComparingLong(DecodeJob::sequence);
    private final PriorityQueue<DecodeJob> pendingDecodes = new PriorityQueue<DecodeJob>(DECODE_ORDER);
    private final Map<TacticalMapTileKey, Integer> desiredRanks = new HashMap<TacticalMapTileKey, Integer>();
    private final Deque<PendingUpload> decodedUploadQueue = new ArrayDeque<PendingUpload>();
    private TacticalMapTileService.Descriptor descriptor = TacticalMapTileService.Descriptor.EMPTY;
    private TacticalMapPyramidLayout layout;
    private long generation;
    private long decodeSequence;
    private long lastDesiredPlanAt;
    private List<TacticalMapPyramidLayout.TileCoordinate> lastDesiredPlan = List.of();
    private long lastDesiredPlanRevision = Long.MIN_VALUE;
    private long readinessRevision;
    private TextureEntry layerAtlas;
    private AtlasKey layerAtlasKey;
    private final AtomicLong nativeImagesCreated = new AtomicLong();
    private final AtomicLong nativeImagesClosed = new AtomicLong();
    private final RollingLatencyWindow uploadLatencyNanos = new RollingLatencyWindow(256);

    private ClientTacticalMapTileCache() {
        for (int index = 0; index < 2; ++index) {
            Thread worker = new Thread(this::runDecodeWorker, "ESPoints-TacticalMapDecode-" + (index + 1));
            worker.setDaemon(true);
            worker.start();
        }
    }

    public static ClientTacticalMapTileCache get() {
        return INSTANCE;
    }

    public synchronized void applyDescriptor(TacticalMapTileService.Descriptor incoming) {
        TacticalMapTileService.Descriptor normalized;
        TacticalMapTileService.Descriptor descriptor = normalized = incoming == null ? TacticalMapTileService.Descriptor.EMPTY : incoming;
        if (this.descriptor.session() == normalized.session() && this.descriptor.sha256().equals(normalized.sha256())) {
            if (normalized.present()) {
                this.request(normalized.maxLevel(), 0, 0);
                this.tryLoadLocalPreview(normalized);
            }
            return;
        }
        this.releaseAll();
        ++this.generation;
        this.descriptor = normalized;
        this.layout = normalized.present() ? new TacticalMapPyramidLayout(normalized.width(), normalized.height()) : null;
        for (TextureEntry evicted : this.textures.setMaximumWeight(Math.max(16L, (long)((Integer)TacticalMapConfig.tileTextureCacheMiB.get()).intValue()) * 1024L * 1024L)) {
            this.release(evicted);
        }
        if (normalized.present()) {
            TacticalMapTileKey preview = new TacticalMapTileKey(normalized.session(), normalized.maxLevel(), 0, 0);
            this.requests.updateDesired(List.of(preview));
            this.desiredRanks.put(preview, 0);
            this.lastDesiredPlanAt = System.currentTimeMillis();
            ESPointsMod.LOGGER.info("\u5ba2\u6237\u7aef\u5df2\u5e94\u7528\u6218\u672f\u5730\u56fe descriptor session={} {}x{} previewLevel={}", (Object)normalized.session(), (Object)normalized.width(), (Object)normalized.height(), (Object)normalized.maxLevel());
            this.tryLoadLocalPreview(normalized);
        } else {
            ESPointsMod.LOGGER.info("\u5ba2\u6237\u7aef\u6218\u672f\u5730\u56fe descriptor \u5df2\u6e05\u7a7a");
        }
    }

    public synchronized TacticalMapTileService.Descriptor descriptor() {
        return this.descriptor;
    }

    public synchronized TacticalMapPyramidLayout layout() {
        return this.layout;
    }

    public synchronized boolean hasAll(List<TacticalMapPyramidLayout.TileCoordinate> tiles) {
        if (tiles == null || tiles.isEmpty()) {
            return true;
        }
        if (!this.descriptor.present()) {
            return false;
        }
        for (TacticalMapPyramidLayout.TileCoordinate tile : tiles) {
            if (tile != null && this.textures.get(new TacticalMapTileKey(this.descriptor.session(), tile.level(), tile.x(), tile.y())) != null) continue;
            return false;
        }
        return true;
    }

    public synchronized TextureEntry texture(int level, int x, int y) {
        if (!this.descriptor.present()) {
            return null;
        }
        return this.textures.get(new TacticalMapTileKey(this.descriptor.session(), level, x, y));
    }

    public synchronized LayerAtlas composeLayer(int level, List<TacticalMapPyramidLayout.TileCoordinate> tiles) {
        NativeImage composed;
        if (this.layout == null || !this.descriptor.present()) {
            return null;
        }
        TacticalMapTileAtlasLayout.Spec spec = TacticalMapTileAtlasLayout.spec(this.layout, level, tiles);
        if (spec == null) {
            return null;
        }
        AtlasKey key = new AtlasKey(this.descriptor.session(), this.generation, this.readinessRevision, spec);
        if (this.layerAtlas != null && key.equals(this.layerAtlasKey)) {
            return new LayerAtlas(this.layerAtlas, spec);
        }
        try {
            composed = this.stampLayer(spec, tiles);
        }
        catch (RuntimeException error) {
            ESPointsMod.LOGGER.warn("\u6218\u672f\u5730\u56fe\u56fe\u5c42\u62fc\u63a5\u5931\u8d25: L{} {}", (Object)level, (Object)error.toString());
            return null;
        }
        if (composed == null) {
            return null;
        }
        this.nativeImagesCreated.incrementAndGet();
        try {
            DynamicTexture texture = new DynamicTexture(composed);
            TacticalMapTextureSampling.apply(texture, false);
            ResourceLocation location = Minecraft.m_91087_().m_91097_().m_118490_("espoints_tactical_layer_" + this.descriptor.session() + "_" + level + "_" + this.readinessRevision, texture);
            TextureEntry entry = new TextureEntry(location, texture, composed.m_84982_(), composed.m_85084_());
            entry.linear = false;
            TextureEntry previous = this.layerAtlas;
            this.layerAtlas = entry;
            this.layerAtlasKey = key;
            if (previous != null) {
                this.release(previous);
            }
            return new LayerAtlas(entry, spec);
        }
        catch (RuntimeException error) {
            this.closeImage(composed);
            throw error;
        }
    }

    private NativeImage stampLayer(TacticalMapTileAtlasLayout.Spec spec, List<TacticalMapPyramidLayout.TileCoordinate> tiles) {
        NativeImage composed = new NativeImage(spec.width(), spec.height(), false);
        try {
            for (TacticalMapPyramidLayout.TileCoordinate tile : tiles) {
                NativeImage pixels;
                TextureEntry source = this.textures.get(new TacticalMapTileKey(this.descriptor.session(), tile.level(), tile.x(), tile.y()));
                NativeImage nativeImage = pixels = source == null ? null : source.pixels();
                if (source == null || pixels == null || source.width() != this.layout.tileWidth(tile.level(), tile.x()) || source.height() != this.layout.tileHeight(tile.level(), tile.y()) || pixels.m_84982_() != source.width() || pixels.m_85084_() != source.height()) {
                    composed.close();
                    return null;
                }
                ClientTacticalMapTileCache.copyTile(composed, pixels, TacticalMapTileAtlasLayout.atlasX(spec, tile.x()), TacticalMapTileAtlasLayout.atlasY(spec, tile.y()));
            }
            return composed;
        }
        catch (RuntimeException error) {
            composed.close();
            throw error;
        }
    }

    private static void copyTile(NativeImage dest, NativeImage source, int destX, int destY) {
        int width = source.m_84982_();
        int height = source.m_85084_();
        for (int y = 0; y < height; ++y) {
            int targetY = destY + y;
            if (targetY < 0 || targetY >= dest.m_85084_()) continue;
            for (int x = 0; x < width; ++x) {
                int targetX = destX + x;
                if (targetX < 0 || targetX >= dest.m_84982_()) continue;
                dest.m_84988_(targetX, targetY, source.m_84985_(x, y));
            }
        }
    }

    public synchronized TacticalMapLodPlanner.TileState tileState(TacticalMapPyramidLayout.TileCoordinate tile) {
        if (this.layout == null || tile == null || !this.layout.isValid(tile.level(), tile.x(), tile.y())) {
            return TacticalMapLodPlanner.TileState.MISSING;
        }
        TacticalMapTileKey key = new TacticalMapTileKey(this.descriptor.session(), tile.level(), tile.x(), tile.y());
        if (this.textures.get(key) != null) {
            return TacticalMapLodPlanner.TileState.READY;
        }
        if (this.decoding.contains(key)) {
            return TacticalMapLodPlanner.TileState.REQUESTED;
        }
        if (this.requests.isOutstanding(key) || this.requests.isDesired(key)) {
            return TacticalMapLodPlanner.TileState.REQUESTED;
        }
        return TacticalMapLodPlanner.TileState.MISSING;
    }

    public long textureBudgetBytes() {
        return Math.max(16L, (long)((Integer)TacticalMapConfig.tileTextureCacheMiB.get()).intValue()) * 1024L * 1024L;
    }

    public synchronized void requestCurrentPreview() {
        if (!this.descriptor.present() || this.layout == null) {
            return;
        }
        this.request(this.descriptor.maxLevel(), 0, 0);
    }

    public synchronized void request(int level, int x, int y) {
        if (this.layout == null || !this.layout.isValid(level, x, y)) {
            return;
        }
        TacticalMapTileKey key = new TacticalMapTileKey(this.descriptor.session(), level, x, y);
        if (this.textures.get(key) != null) {
            return;
        }
        this.requests.addDesired(key);
        this.desiredRanks.putIfAbsent(key, this.desiredRanks.size());
        this.lastDesiredPlanAt = System.currentTimeMillis();
    }

    public synchronized void updateDesired(List<TacticalMapPyramidLayout.TileCoordinate> orderedTiles) {
        List<Object> normalizedPlan;
        if (this.layout == null || !this.descriptor.present()) {
            this.requests.clear();
            this.desiredRanks.clear();
            this.lastDesiredPlan = List.of();
            return;
        }
        List<Object> list = normalizedPlan = orderedTiles == null ? List.of() : List.copyOf(orderedTiles);
        if (this.lastDesiredPlan.equals(normalizedPlan) && this.lastDesiredPlanRevision == this.readinessRevision) {
            this.lastDesiredPlanAt = System.currentTimeMillis();
            return;
        }
        ArrayList<TacticalMapTileKey> ordered = new ArrayList<TacticalMapTileKey>();
        TacticalMapTileKey preview = new TacticalMapTileKey(this.descriptor.session(), this.descriptor.maxLevel(), 0, 0);
        if (this.textures.get(preview) == null) {
            ordered.add(preview);
        }
        if (orderedTiles != null) {
            for (TacticalMapPyramidLayout.TileCoordinate tile : orderedTiles) {
                TacticalMapTileKey key;
                if (tile == null || !this.layout.isValid(tile.level(), tile.x(), tile.y()) || this.textures.get(key = new TacticalMapTileKey(this.descriptor.session(), tile.level(), tile.x(), tile.y())) != null) continue;
                ordered.add(key);
            }
        }
        this.requests.updateDesired(ordered);
        this.desiredRanks.clear();
        for (int index = 0; index < ordered.size(); ++index) {
            this.desiredRanks.putIfAbsent((TacticalMapTileKey)ordered.get(index), index);
        }
        this.lastDesiredPlan = normalizedPlan;
        this.lastDesiredPlanRevision = this.readinessRevision;
        this.discardUndesiredQueuedWork();
        this.lastDesiredPlanAt = System.currentTimeMillis();
        this.tryLoadLocalTiles(ordered);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tickRequests() {
        List<TacticalMapTileKey> toSend;
        ClientTacticalMapTileCache clientTacticalMapTileCache = this;
        synchronized (clientTacticalMapTileCache) {
            long now = System.currentTimeMillis();
            long textureBudget = this.textureBudgetBytes();
            boolean evicted = false;
            for (TextureEntry entry : this.textures.setMaximumWeight(textureBudget)) {
                this.release(entry);
                evicted = true;
            }
            if (evicted) {
                ++this.readinessRevision;
            }
            if (this.lastDesiredPlanAt > 0L && now - this.lastDesiredPlanAt > 15000L) {
                TacticalMapTileKey preview = this.previewKeyOrNull();
                this.requests.clear();
                this.desiredRanks.clear();
                this.lastDesiredPlan = List.of();
                this.lastDesiredPlanRevision = Long.MIN_VALUE;
                if (preview != null && this.textures.get(preview) == null) {
                    this.requests.addDesired(preview);
                    this.desiredRanks.put(preview, 0);
                    this.lastDesiredPlanAt = now;
                } else {
                    this.lastDesiredPlanAt = 0L;
                }
            }
            if (Minecraft.m_91087_().m_91403_() == null) {
                this.suspendRequests();
                return;
            }
            toSend = this.requests.poll(now, 8);
        }
        for (TacticalMapTileKey key : toSend) {
            NetworkHandler.INSTANCE.sendToServer((Object)new RequestTacticalMapTileMessage(key.session(), key.level(), key.x(), key.y()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void accept(long session, int level, int x, int y, int width, int height, byte[] encoded) {
        TacticalMapTileKey key = new TacticalMapTileKey(session, level, x, y);
        ClientTacticalMapTileCache clientTacticalMapTileCache = this;
        synchronized (clientTacticalMapTileCache) {
            if (this.layout == null || this.descriptor.session() != session || !this.layout.isValid(level, x, y) || this.layout.tileWidth(level, x) != width || this.layout.tileHeight(level, y) != height) {
                ESPointsMod.LOGGER.warn("\u4e22\u5f03\u6218\u672f\u5730\u56fe\u74e6\u7247: session={} level={} ({},{}) {}x{} layout={} descriptorSession={}", (Object)session, (Object)level, (Object)x, (Object)y, (Object)width, (Object)height, this.layout == null ? "null" : this.layout.width() + "x" + this.layout.height(), (Object)this.descriptor.session());
                return;
            }
            boolean preview = this.isPreview(key);
            if (!(preview || this.requests.isDesired(key) || this.requests.isOutstanding(key) || this.requests.isProcessing(key))) {
                return;
            }
            if (preview) {
                this.requests.addDesired(key);
            }
            if (encoded == null || encoded.length < 8 || encoded.length > 0x200000) {
                this.requests.reject(key, System.currentTimeMillis());
                return;
            }
            this.requests.received(key);
            if (this.textures.get(key) != null) {
                this.requests.complete(key);
                this.desiredRanks.remove(key);
                return;
            }
            if (this.decoding.contains(key)) {
                return;
            }
            this.decoding.add(key);
            PendingUpload pending = new PendingUpload(key, this.generation, width, height, encoded);
            if (preview) {
                this.decodedUploadQueue.addFirst(pending);
            } else if (this.decodedUploadQueue.size() >= 24) {
                PendingUpload evicted = this.decodedUploadQueue.pollLast();
                if (evicted != null) {
                    this.decoding.remove(evicted.key());
                    this.requests.reject(evicted.key(), System.currentTimeMillis());
                }
                this.decodedUploadQueue.addLast(pending);
            } else {
                this.decodedUploadQueue.addLast(pending);
            }
            ESPointsMod.LOGGER.info("\u5ba2\u6237\u7aef\u5df2\u6536\u5230\u6218\u672f\u5730\u56fe\u74e6\u7247 {} {}x{} ({} bytes, queue={})", (Object)key, (Object)width, (Object)height, (Object)encoded.length, (Object)this.decodedUploadQueue.size());
        }
    }

    public synchronized void clear() {
        this.descriptor = TacticalMapTileService.Descriptor.EMPTY;
        this.layout = null;
        ++this.generation;
        this.releaseAll();
    }

    public synchronized void suspendRequests() {
        this.requests.clear();
        this.desiredRanks.clear();
        this.pendingDecodes.clear();
        this.decoding.clear();
        this.decodedUploadQueue.clear();
        this.lastDesiredPlanAt = 0L;
        this.lastDesiredPlan = List.of();
        this.lastDesiredPlanRevision = Long.MIN_VALUE;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void drainUploadQueue(int maximumUploads, long softBudgetNanos) {
        int remaining = Math.max(0, maximumUploads);
        long startedAt = System.nanoTime();
        while (remaining-- > 0) {
            NativeImage image;
            PendingUpload pending;
            if (System.nanoTime() - startedAt >= Math.max(0L, softBudgetNanos)) {
                return;
            }
            ClientTacticalMapTileCache clientTacticalMapTileCache = this;
            synchronized (clientTacticalMapTileCache) {
                pending = this.decodedUploadQueue.pollFirst();
            }
            if (pending == null) {
                return;
            }
            try {
                image = this.decode(pending.encoded(), pending.width(), pending.height());
            }
            catch (RuntimeException error) {
                this.markDecodeFailed(pending.key());
                ESPointsMod.LOGGER.warn("\u6218\u672f\u5730\u56fe\u74e6\u7247\u89e3\u7801\u5931\u8d25: {}", (Object)pending.key(), (Object)error);
                continue;
            }
            long uploadStartedAt = System.nanoTime();
            this.register(pending.key(), pending.generation(), image);
            this.uploadLatencyNanos.record(System.nanoTime() - uploadStartedAt);
        }
    }

    private NativeImage decode(byte[] encoded, int width, int height) {
        try {
            NativeImage image = NativeImage.m_85058_((InputStream)new ByteArrayInputStream(encoded));
            this.nativeImagesCreated.incrementAndGet();
            if (image.m_84982_() != width || image.m_85084_() != height) {
                this.closeImage(image);
                throw new IOException("Decoded tile dimensions do not match packet");
            }
            return image;
        }
        catch (IOException error) {
            throw new IllegalStateException(error);
        }
    }

    private synchronized void register(TacticalMapTileKey key, long decodedGeneration, NativeImage image) {
        this.decoding.remove(key);
        boolean preview = this.isPreview(key);
        if (this.generation != decodedGeneration || this.descriptor.session() != key.session() || this.layout == null || !preview && !this.requests.isDesired(key)) {
            this.closeImage(image);
            this.requests.reject(key, System.currentTimeMillis());
            return;
        }
        if (preview) {
            this.requests.addDesired(key);
        }
        try {
            DynamicTexture texture = new DynamicTexture(image);
            boolean linear = TacticalMapTextureFilterPolicy.useLinearFiltering(key.level(), this.descriptor.maxLevel());
            TacticalMapTextureSampling.apply(texture, linear);
            ResourceLocation location = Minecraft.m_91087_().m_91097_().m_118490_("espoints_tactical_tile_" + key.session() + "_" + key.level() + "_" + key.x() + "_" + key.y(), texture);
            TextureEntry entry = new TextureEntry(location, texture, image.m_84982_(), image.m_85084_());
            entry.linear = linear;
            boolean evictedAny = false;
            for (TextureEntry evicted : this.textures.put(key, entry)) {
                this.release(evicted);
                evictedAny = true;
            }
            ++this.readinessRevision;
            if (evictedAny) {
                ++this.readinessRevision;
            }
            this.requests.complete(key);
            this.desiredRanks.remove(key);
            if (preview) {
                ESPointsMod.LOGGER.info("\u5ba2\u6237\u7aef\u5df2\u4e0a\u4f20\u6218\u672f\u5730\u56fe\u9884\u89c8\u74e6\u7247 {}x{}", (Object)image.m_84982_(), (Object)image.m_85084_());
            }
        }
        catch (RuntimeException error) {
            this.closeImage(image);
            this.requests.reject(key, System.currentTimeMillis());
            throw error;
        }
    }

    private synchronized void queueDecoded(TacticalMapTileKey key, long decodedGeneration, NativeImage image) {
        this.closeImage(image);
        this.decoding.remove(key);
    }

    private void releaseAll() {
        this.requests.clear();
        this.desiredRanks.clear();
        this.pendingDecodes.clear();
        this.decoding.clear();
        this.lastDesiredPlan = List.of();
        this.lastDesiredPlanRevision = Long.MIN_VALUE;
        this.decodedUploadQueue.clear();
        this.releaseLayerAtlas();
        for (TextureEntry entry : this.textures.clear()) {
            this.release(entry);
        }
    }

    private void releaseLayerAtlas() {
        if (this.layerAtlas != null) {
            this.release(this.layerAtlas);
            this.layerAtlas = null;
            this.layerAtlasKey = null;
        }
    }

    private synchronized void markDecodeFailed(TacticalMapTileKey key) {
        this.decoding.remove(key);
        this.requests.reject(key, System.currentTimeMillis());
    }

    public synchronized long readinessRevision() {
        return this.readinessRevision;
    }

    public long nativeImagesCreated() {
        return this.nativeImagesCreated.get();
    }

    public long nativeImagesClosed() {
        return this.nativeImagesClosed.get();
    }

    public long uploadP95Nanos() {
        return this.uploadLatencyNanos.percentile(0.95);
    }

    public long uploadP99Nanos() {
        return this.uploadLatencyNanos.percentile(0.99);
    }

    public synchronized int decodedUploadQueueSize() {
        return this.decodedUploadQueue.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int queuedDecodeCount() {
        ClientTacticalMapTileCache clientTacticalMapTileCache = this;
        synchronized (clientTacticalMapTileCache) {
            return this.decoding.size();
        }
    }

    private synchronized boolean enqueueDecode(DecodeJob incoming) {
        if (this.decoding.size() >= 32) {
            DecodeJob lowest = this.pendingDecodes.stream().max(DECODE_ORDER).orElse(null);
            if (lowest == null || DECODE_ORDER.compare(incoming, lowest) >= 0) {
                return false;
            }
            this.pendingDecodes.remove(lowest);
            this.decoding.remove(lowest.key());
            this.requests.reject(lowest.key(), System.currentTimeMillis());
        }
        this.decoding.add(incoming.key());
        this.pendingDecodes.add(incoming);
        this.notifyAll();
        return true;
    }

    private void discardUndesiredQueuedWork() {
        this.pendingDecodes.removeIf(job -> {
            if (this.requests.isDesired(job.key())) {
                return false;
            }
            this.decoding.remove(job.key());
            return true;
        });
        this.decodedUploadQueue.removeIf(pending -> {
            if (this.requests.isDesired(pending.key()) || this.isPreview(pending.key())) {
                return false;
            }
            this.decoding.remove(pending.key());
            return true;
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void runDecodeWorker() {
        while (!Thread.currentThread().isInterrupted()) {
            DecodeJob job;
            ClientTacticalMapTileCache clientTacticalMapTileCache = this;
            synchronized (clientTacticalMapTileCache) {
                while (this.pendingDecodes.isEmpty()) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                job = this.pendingDecodes.poll();
                boolean preview = this.isPreview(job.key());
                if (job.generation() != this.generation || this.descriptor.session() != job.key().session() || !preview && !this.requests.isDesired(job.key())) {
                    this.decoding.remove(job.key());
                    this.requests.reject(job.key(), System.currentTimeMillis());
                    continue;
                }
            }
            try {
                NativeImage image = this.decode(job.encoded(), job.width(), job.height());
                this.queueDecoded(job.key(), job.generation(), image);
            }
            catch (RuntimeException error) {
                this.markDecodeFailed(job.key());
                ESPointsMod.LOGGER.warn("\u6218\u672f\u5730\u56fe\u74e6\u7247\u89e3\u7801\u5931\u8d25: {}", (Object)job.key(), (Object)error);
            }
        }
    }

    private void tryLoadLocalPreview(TacticalMapTileService.Descriptor incoming) {
        if (incoming == null || !incoming.present() || this.layout == null) {
            return;
        }
        TacticalMapTileKey preview = new TacticalMapTileKey(incoming.session(), incoming.maxLevel(), 0, 0);
        if (this.tryLoadLocalTile(preview)) {
            ESPointsMod.LOGGER.info("\u4ece\u672c\u5730\u7f13\u5b58\u8f7d\u5165\u6218\u672f\u5730\u56fe\u9884\u89c8");
        }
    }

    private void tryLoadLocalTiles(List<TacticalMapTileKey> keys) {
        if (keys == null || keys.isEmpty() || !this.descriptor.present() || this.layout == null) {
            return;
        }
        int loaded = 0;
        for (TacticalMapTileKey key : keys) {
            if (loaded >= 24) break;
            if (!this.tryLoadLocalTile(key)) continue;
            ++loaded;
        }
        if (loaded > 0) {
            ESPointsMod.LOGGER.info("\u4ece\u672c\u5730\u7f13\u5b58\u8f7d\u5165 {} \u4e2a\u6218\u672f\u5730\u56fe\u74e6\u7247", (Object)loaded);
        }
    }

    private boolean tryLoadLocalTile(TacticalMapTileKey key) {
        if (key == null || this.textures.get(key) != null || this.decoding.contains(key)) {
            return false;
        }
        Path file = TacticalMapTileService.localTileFile(this.descriptor.sha256(), key.level(), key.x(), key.y());
        if (!Files.isRegularFile(file, new LinkOption[0])) {
            return false;
        }
        try {
            byte[] bytes = Files.readAllBytes(file);
            int width = this.layout.tileWidth(key.level(), key.x());
            int height = this.layout.tileHeight(key.level(), key.y());
            this.accept(key.session(), key.level(), key.x(), key.y(), width, height, bytes);
            return this.textures.get(key) != null || this.decoding.contains(key) || this.decodedUploadQueue.stream().anyMatch(pending -> pending.key().equals(key));
        }
        catch (Exception error) {
            ESPointsMod.LOGGER.debug("\u672c\u5730\u6218\u672f\u5730\u56fe\u74e6\u7247\u8bfb\u53d6\u5931\u8d25: {}", (Object)file, (Object)error);
            return false;
        }
    }

    private TacticalMapTileKey previewKeyOrNull() {
        if (!this.descriptor.present() || this.layout == null) {
            return null;
        }
        return new TacticalMapTileKey(this.descriptor.session(), this.descriptor.maxLevel(), 0, 0);
    }

    private boolean isPreview(TacticalMapTileKey key) {
        return key != null && this.descriptor.present() && key.session() == this.descriptor.session() && key.level() == this.descriptor.maxLevel() && key.x() == 0 && key.y() == 0;
    }

    private void closeImage(NativeImage image) {
        if (image != null) {
            image.close();
            this.nativeImagesClosed.incrementAndGet();
        }
    }

    private void release(TextureEntry entry) {
        if (entry != null) {
            Minecraft.m_91087_().m_91097_().m_118513_(entry.location);
            this.nativeImagesClosed.incrementAndGet();
        }
    }

    public static final class TextureEntry {
        private final ResourceLocation location;
        private final DynamicTexture texture;
        private final int width;
        private final int height;
        private boolean linear;

        private TextureEntry(ResourceLocation location, DynamicTexture texture, int width, int height) {
            this.location = location;
            this.texture = texture;
            this.width = width;
            this.height = height;
            this.linear = false;
        }

        public ResourceLocation location() {
            return this.location;
        }

        public int gpuId() {
            return this.texture.m_117963_();
        }

        public int width() {
            return this.width;
        }

        public int height() {
            return this.height;
        }

        NativeImage pixels() {
            return this.texture.m_117991_();
        }

        public void prepareFiltering(int level, int maximumLevel, double scaleX, double scaleY) {
            boolean next = TacticalMapTextureFilterPolicy.useLinearFiltering(level, maximumLevel, scaleX, scaleY);
            if (next != this.linear) {
                TacticalMapTextureSampling.apply(this.texture, next);
                this.linear = next;
            }
        }

        private long weight() {
            return (long)this.width * (long)this.height * 4L;
        }
    }

    private record AtlasKey(long session, long generation, long readinessRevision, TacticalMapTileAtlasLayout.Spec spec) {
    }

    public record LayerAtlas(TextureEntry texture, TacticalMapTileAtlasLayout.Spec spec) {
    }

    private record PendingUpload(TacticalMapTileKey key, long generation, int width, int height, byte[] encoded) {
    }

    private record DecodeJob(TacticalMapTileKey key, long generation, int width, int height, byte[] encoded, int priority, long sequence) {
    }
}

